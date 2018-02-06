package com.wzd.service;

import com.wzd.model.dao.*;
import com.wzd.model.entity.Files;
import com.wzd.model.enums.RoleType;
import com.wzd.utils.FileUtil;
import com.wzd.utils.PoiExcelUtils;
import com.wzd.web.dto.Admin;
import com.wzd.web.dto.Export;
import com.wzd.web.dto.ExportDetail;
import com.wzd.web.dto.ExportDetailCopy;
import com.wzd.web.dto.exception.WebException;
import com.wzd.web.dto.response.ResponseCode;
import com.wzd.web.dto.session.SessionUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.*;

@Service
@Transactional
public class ExportService {
    @Autowired
    private ExportDao exportDao;
    @Autowired
    private FileDao fileDao;

    @Autowired
    private ExportDetailCopyDao detailCopyDao;
    @Autowired
    private ExportDetailDao detailDao;
    @Autowired
    private AdminDao adminDao;

    public Export find(Integer type) {

        return null;
    }

    public Map findDetailCopyByType(Integer type) {
        Example example = new Example(Export.class);
        example.setOrderByClause("createTime DESC");
        example.createCriteria().andEqualTo("type", type);
        List<Export> exports = exportDao.selectByExample(example);
        if (exports == null || exports.size() < 1) return null;
        example = new Example(ExportDetailCopy.class);
        example.setOrderByClause("num ASC");
        example.createCriteria().andEqualTo("exportId", exports.get(0).getId());
        Map<String, Object> result = new HashMap<>();
        result.put("exportId", exports.get(0).getId());
        result.put("downUrl", exports.get(0).getUrl());
        result.put("endTime", exports.get(0).getEndTime());
        result.put("data", detailCopyDao.selectByExample(example));
        return result;
    }

    public List<ExportDetail> findTenderResult(Integer type, String agentId) {
        if (StringUtils.isBlank(agentId)) throw new WebException(ResponseCode.数据参数异常);
        Admin admin = new Admin();
        admin.setId(agentId);
        admin = adminDao.selectOne(admin);
        if (admin == null) {
            throw new WebException(ResponseCode.数据参数异常);
        }
        if (RoleType.管理员.getValue().equals(admin.getRole())) agentId = "";
        Example example = new Example(Export.class);
        example.setOrderByClause("createTime DESC");
        example.createCriteria().andEqualTo("type", type);
        List<Export> exports = exportDao.selectByExample(example);
        if (exports == null || exports.size() < 1) return null;
        return detailDao.findTenderResult(exports.get(0).getId(), agentId);
    }

    public String exportTenderResult(Integer type, String agentId) {
        if (StringUtils.isBlank(agentId)) throw new WebException(ResponseCode.数据参数异常);
        Admin admin = new Admin();
        admin.setId(agentId);
        admin = adminDao.selectOne(admin);
        if (admin == null) {
            throw new WebException(ResponseCode.数据参数异常);
        }
        if (RoleType.管理员.equals(admin.getRole())) agentId = "";
        Example example = new Example(Export.class);
        example.setOrderByClause("createTime DESC");
        example.createCriteria().andEqualTo("type", type);
        List<Export> exports = exportDao.selectByExample(example);
        if (exports == null || exports.size() < 1) return null;
        String[] headers = new String[]{"序号@num", "名称@name", "规格@spec", "数量@number",
                "单价@unitPrice", "供应商名称@agentName", "菜品要求@business", "实物图片@url", "备注@remark"};
        String url = PoiExcelUtils.createExcel2FilePath("招标结果列表", "招标结果列表", FileUtil.BASE_PATH, headers, detailDao.findTenderResult(exports.get(0).getId(), agentId));
        return url;
    }

    public Object importExcel(Export export, HttpServletRequest request) {
        Files files = export.getFiles();
        String path = FileUtil.BASE_PATH + files.getUrl().substring(files.getUrl().indexOf("userfiles"));
        File f = new File(path);
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(f);
        } catch (Exception e) {

        }
        export.setUrl(files.getUrl());
        export.setStartTime(new Date());
        exportDao.create(export);
        fileDao.update(new Files(files.getId(), export.getId(), files.getType()));
        HSSFWorkbook workbook = null;
        List<String> header = new ArrayList<>();
        List<Map<String, String>> data = new ArrayList<>();
        Map<String, String> map = null;
        try {
            // 读取Excel文件
            workbook = new HSSFWorkbook(inputStream);
            HSSFSheet sheet = null;
            List<ExportDetailCopy> list = new ArrayList<>();
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {//获取每个Sheet表
                sheet = workbook.getSheetAt(i);
                for (int j = 1; j < sheet.getPhysicalNumberOfRows(); j++) {//获取每行
                    ExportDetailCopy detailCopy = new ExportDetailCopy();
                    detailCopy.setType(export.getType());
                    detailCopy.setExportId(export.getId());
                    HSSFRow row = sheet.getRow(j);
                    map = new HashMap<>();
                    if (j == 1) {
                        for (int k = 0; k < row.getPhysicalNumberOfCells(); k++) {//获取每个单元格
                            header.add(row.getCell(k) + "");
                        }
                    } else {
                        for (int k = 0; k < header.size(); k++) {
                            if (row == null) break;
                            HSSFCell cell = row.getCell(k);
                            if (row.getCell(k) != null && cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {// 如果为数字
                                DecimalFormat df = new DecimalFormat("0");
                                map.put(header.get(k), df.format(cell.getNumericCellValue()));
                            } else if (row.getCell(k) != null) {
                                map.put(header.get(k), cell.toString());
                            }
                            if (cell == null) continue;
                            if (export.getType() == 1) this.setDataByTypeCopy1(k, detailCopy, cell);//蔬菜类数据组装
                            if (export.getType() == 2) this.setDataByTypeCopy2(k, detailCopy, cell);//肉类数据组装
                            if (export.getType() == 3) this.setDataByTypeCopy3(k, detailCopy, cell);//干杂类数据组装
                            if (export.getType() == 4) this.setDataByTypeCopy4(k, detailCopy, cell);//水产类数据组装
                        }
                        data.add(map);
                    }
                    if (j == 1 || detailCopy.getNum() == null || StringUtils.isBlank(detailCopy.getName())) continue;
                    list.add(detailCopy);
                }
            }
            detailCopyDao.createList(list);
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public Object importTenderExcel(Integer type, String exportId, FormDataMultiPart form, HttpServletRequest request) {
        Admin admin = (Admin) SessionUtil.getUser(request);
        if (type == null || StringUtils.isBlank(exportId)) {
            throw new WebException(ResponseCode.数据参数异常, "传入类型或导入id不存在");
        }
        Export export = new Export();
        export.setId(exportId);
        export = exportDao.selectOne(export);
        if (export == null) throw new WebException(ResponseCode.数据参数异常, "还未开始竞标，或对应竞标不存在");
        if (export.getEndTime().getTime() < System.currentTimeMillis()) {
            throw new WebException(ResponseCode.数据参数异常, "当前竞标已结束");
        }
        Example example = new Example(ExportDetail.class);
        example.createCriteria().andEqualTo("exportId", exportId)
                .andEqualTo("agentId", admin.getId());
        List<ExportDetail> exportDetailList = detailDao.selectByExample(example);
        if (exportDetailList != null && exportDetailList.size() > 0) {
            throw new WebException(ResponseCode.数据参数异常, "请勿重复导入竞标文件");
        }
        FormDataBodyPart filePart = form.getField("file");
        if (form.getField("type") != null) {
            type = form.getField("type").getValueAs(Integer.class);
        }
        InputStream inputStream = filePart.getValueAs(InputStream.class);
        FormDataContentDisposition disposition = filePart.getFormDataContentDisposition();
        InputStream file = filePart.getValueAs(InputStream.class);
        Files f = FileUtil.writeFile(file, disposition);
        HSSFWorkbook workbook = null;
        List<String> header = new ArrayList<>();
        List<Map<String, String>> data = new ArrayList<>();
        Map<String, String> map = null;
        try {
            // 读取Excel文件
            workbook = new HSSFWorkbook(inputStream);
            HSSFSheet sheet = null;
            List<ExportDetail> list = new ArrayList<>();
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {//获取每个Sheet表
                sheet = workbook.getSheetAt(i);
                for (int j = 1; j < sheet.getPhysicalNumberOfRows(); j++) {//获取每行
                    ExportDetail detail = new ExportDetail();
                    detail.setExportId(exportId);
                    detail.setType(type);
                    detail.setCreateTime(new Date());
                    detail.setAgentId(admin.getId());
                    detail.setAgentName(admin.getName());
                    HSSFRow row = sheet.getRow(j);
                    map = new HashMap<>();
                    if (j == 1) {
                        for (int k = 0; k < row.getPhysicalNumberOfCells(); k++) {//获取每个单元格
                            header.add(row.getCell(k) + "");
                        }
                    } else {
                        for (int k = 0; k < header.size(); k++) {
                            if (row == null) break;
                            HSSFCell cell = row.getCell(k);
                            if (row.getCell(k) != null && cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {// 如果为数字
                                DecimalFormat df = new DecimalFormat("0");
                                map.put(header.get(k), df.format(cell.getNumericCellValue()));
                            } else if (row.getCell(k) != null) {
                                map.put(header.get(k), cell.toString());
                            }
                            if (cell == null) continue;
                            if (type == 1) this.setDataByType1(k, detail, cell);//蔬菜类数据组装
                            if (type == 2) this.setDataByType2(k, detail, cell);//肉类数据组装
                            if (type == 3) this.setDataByType3(k, detail, cell);//干杂类数据组装
                            if (type == 4) this.setDataByType4(k, detail, cell);//水产类数据组装
                        }
                        data.add(map);
                    }
                    if (j == 1 || detail.getNum() == null || StringUtils.isBlank(detail.getName())) continue;
                    list.add(detail);
                }
            }
            detailDao.createList(list);
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
//		Salarybill payroll = new Salarybill();
//		payroll.setData(data);
//		payroll.setHeader(header);
        return null;
    }

    /**
     * 蔬菜类
     */
    public void setDataByType1(Integer k, ExportDetail detail, HSSFCell cell) {
        if (k == 0) {
            DecimalFormat df = new DecimalFormat("0");
            detail.setNum(Integer.parseInt(df.format(cell.getNumericCellValue())));
        }
        if (k == 1) detail.setCategory(cell.toString());
        if (k == 2) detail.setName(cell.toString());
        if (k == 3) detail.setSpec(cell.toString());
        if (k == 4) detail.setUnit(cell.toString());
        if (k == 5) detail.setBusiness(cell.toString());
        if (k == 6) {
            DecimalFormat df = new DecimalFormat("0");
            detail.setUnitPrice(Double.parseDouble(df.format(cell.getNumericCellValue())));
        }
        if (k == 7) {
            DecimalFormat df = new DecimalFormat("0");
            detail.setNumber(Integer.parseInt(df.format(cell.getNumericCellValue())));
        }
        if (k == 8) detail.setRemark(cell.toString());
        if (k == 9) detail.setUrl(cell.toString());
        if (k == 10) detail.setRemarks1(cell.toString());
        if (k == 11) detail.setRemarks2(cell.toString());
    }

    /**
     * 肉类
     */
    public void setDataByType2(Integer k, ExportDetail detail, HSSFCell cell) {
        if (k == 0) {
            DecimalFormat df = new DecimalFormat("0");
            detail.setNum(Integer.parseInt(df.format(cell.getNumericCellValue())));
        }
        if (k == 1) detail.setCategory(cell.toString());
        if (k == 2) detail.setName(cell.toString());
        if (k == 3) detail.setSpec(cell.toString());
        if (k == 4) detail.setUnit(cell.toString());
        if (k == 5) detail.setBusiness(cell.toString());
        if (k == 6) {
            DecimalFormat df = new DecimalFormat("0");
            detail.setUnitPrice(Double.parseDouble(df.format(cell.getNumericCellValue())));
        }
        if (k == 7) {
            DecimalFormat df = new DecimalFormat("0");
            detail.setNumber(Integer.parseInt(df.format(cell.getNumericCellValue())));
        }
        if (k == 8) detail.setRemark(cell.toString());
        if (k == 9) detail.setUrl(cell.toString());
        if (k == 10) detail.setRemarks1(cell.toString());
        if (k == 11) detail.setRemarks2(cell.toString());
    }

    /**
     * 干杂类
     */
    public void setDataByType3(Integer k, ExportDetail detail, HSSFCell cell) {
        if (k == 0) {
            DecimalFormat df = new DecimalFormat("0");
            detail.setNum(Integer.parseInt(df.format(cell.getNumericCellValue())));
        }
        if (k == 1) detail.setCategory(cell.toString());
        if (k == 2) detail.setName(cell.toString());
        if (k == 3) detail.setSpec(cell.toString());
        if (k == 4) detail.setUnit(cell.toString());
        if (k == 5) detail.setBusiness(cell.toString());
        if (k == 6) {
            DecimalFormat df = new DecimalFormat("0");
            detail.setUnitPrice(Double.parseDouble(df.format(cell.getNumericCellValue())));
        }
        if (k == 7) {
            DecimalFormat df = new DecimalFormat("0");
            detail.setNumber(Integer.parseInt(df.format(cell.getNumericCellValue())));
        }
        if (k == 8) detail.setLevel(cell.toString());
        if (k == 9) detail.setSpec2(cell.toString());
        if (k == 10) detail.setBarCode(cell.toString());
        if (k == 11) detail.setManufacturer(cell.toString());
        if (k == 12) detail.setPlaceOfOrigin(cell.toString());
        if (k == 13) detail.setRemark(cell.toString());
        if (k == 14) detail.setSerialNumber(cell.toString());
        if (k == 15) detail.setUrl(cell.toString());
        if (k == 16) detail.setRemarks1(cell.toString());
        if (k == 17) detail.setRemarks2(cell.toString());
    }

    /**
     * 水产类
     */
    public void setDataByType4(Integer k, ExportDetail detail, HSSFCell cell) {
        if (k == 0) {
            DecimalFormat df = new DecimalFormat("0");
            detail.setNum(Integer.parseInt(df.format(cell.getNumericCellValue())));
        }
        if (k == 1) detail.setCategory(cell.toString());
        if (k == 2) detail.setName(cell.toString());
        if (k == 3) detail.setSpec(cell.toString());
        if (k == 4) detail.setUnit(cell.toString());
        if (k == 5) detail.setBusiness(cell.toString());
        if (k == 6) {
            DecimalFormat df = new DecimalFormat("0");
            detail.setUnitPrice(Double.parseDouble(df.format(cell.getNumericCellValue())));
        }
        if (k == 7) {
            DecimalFormat df = new DecimalFormat("0");
            detail.setNumber(Integer.parseInt(df.format(cell.getNumericCellValue())));
        }
        if (k == 8) detail.setLevel(cell.toString());
        if (k == 9) detail.setSpec2(cell.toString());
        if (k == 10) detail.setBarCode(cell.toString());
        if (k == 11) detail.setManufacturer(cell.toString());
        if (k == 12) detail.setPlaceOfOrigin(cell.toString());
        if (k == 13) detail.setRemark(cell.toString());
        if (k == 14) detail.setSerialNumber(cell.toString());
        if (k == 15) detail.setUrl(cell.toString());
        if (k == 16) detail.setRemarks1(cell.toString());
        if (k == 17) detail.setRemarks2(cell.toString());
    }

    /**
     * 蔬菜类
     */
    public void setDataByTypeCopy1(Integer k, ExportDetailCopy detail, HSSFCell cell) {
        if (k == 0) {
            DecimalFormat df = new DecimalFormat("0");
            detail.setNum(Integer.parseInt(df.format(cell.getNumericCellValue())));
        }
        if (k == 1) detail.setCategory(cell.toString());
        if (k == 2) detail.setName(cell.toString());
        if (k == 3) detail.setSpec(cell.toString());
        if (k == 4) detail.setUnit(cell.toString());
        if (k == 5) detail.setBusiness(cell.toString());
        if (k == 6) {
            DecimalFormat df = new DecimalFormat("0");
            detail.setUnitPrice(Double.parseDouble(df.format(cell.getNumericCellValue())));
        }
        if (k == 7) {
            DecimalFormat df = new DecimalFormat("0");
            detail.setNumber(Integer.parseInt(df.format(cell.getNumericCellValue())));
        }
        if (k == 8) detail.setRemark(cell.toString());
        if (k == 9) detail.setUrl(cell.toString());
        if (k == 10) detail.setRemarks1(cell.toString());
        if (k == 11) detail.setRemarks2(cell.toString());
    }

    /**
     * 肉类
     */
    public void setDataByTypeCopy2(Integer k, ExportDetailCopy detail, HSSFCell cell) {
        if (k == 0) {
            DecimalFormat df = new DecimalFormat("0");
            detail.setNum(Integer.parseInt(df.format(cell.getNumericCellValue())));
        }
        if (k == 1) detail.setCategory(cell.toString());
        if (k == 2) detail.setName(cell.toString());
        if (k == 3) detail.setSpec(cell.toString());
        if (k == 4) detail.setUnit(cell.toString());
        if (k == 5) detail.setBusiness(cell.toString());
        if (k == 6) {
            DecimalFormat df = new DecimalFormat("0");
            detail.setUnitPrice(Double.parseDouble(df.format(cell.getNumericCellValue())));
        }
        if (k == 7) {
            DecimalFormat df = new DecimalFormat("0");
            detail.setNumber(Integer.parseInt(df.format(cell.getNumericCellValue())));
        }
        if (k == 8) detail.setRemark(cell.toString());
        if (k == 9) detail.setUrl(cell.toString());
        if (k == 10) detail.setRemarks1(cell.toString());
        if (k == 11) detail.setRemarks2(cell.toString());
    }

    /**
     * 干杂类
     */
    public void setDataByTypeCopy3(Integer k, ExportDetailCopy detail, HSSFCell cell) {
        if (k == 0) {
            DecimalFormat df = new DecimalFormat("0");
            detail.setNum(Integer.parseInt(df.format(cell.getNumericCellValue())));
        }
        if (k == 1) detail.setName(cell.toString());
        if (k == 2) detail.setCategory(cell.toString());
        if (k == 3) detail.setSpec(cell.toString());
        if (k == 4) detail.setUnit(cell.toString());
        if (k == 5) detail.setBusiness(cell.toString());
        if (k == 6) {
            DecimalFormat df = new DecimalFormat("0");
            detail.setUnitPrice(Double.parseDouble(df.format(cell.getNumericCellValue())));
        }
        if (k == 7) {
            DecimalFormat df = new DecimalFormat("0");
            detail.setNumber(Integer.parseInt(df.format(cell.getNumericCellValue())));
        }
        if (k == 8) detail.setLevel(cell.toString());
        if (k == 9) detail.setSpec2(cell.toString());
        if (k == 10) detail.setBarCode(cell.toString());
        if (k == 11) detail.setManufacturer(cell.toString());
        if (k == 12) detail.setPlaceOfOrigin(cell.toString());
        if (k == 13) detail.setRemark(cell.toString());
        if (k == 14) detail.setSerialNumber(cell.toString());
        if (k == 15) detail.setUrl(cell.toString());
        if (k == 16) detail.setRemarks1(cell.toString());
        if (k == 17) detail.setRemarks2(cell.toString());
    }

    /**
     * 水产类
     */
    public void setDataByTypeCopy4(Integer k, ExportDetailCopy detail, HSSFCell cell) {
        if (k == 0) {
            DecimalFormat df = new DecimalFormat("0");
            detail.setNum(Integer.parseInt(df.format(cell.getNumericCellValue())));
        }
        if (k == 1) detail.setCategory(cell.toString());
        if (k == 2) detail.setName(cell.toString());
        if (k == 3) detail.setSpec(cell.toString());
        if (k == 4) detail.setUnit(cell.toString());
        if (k == 5) detail.setBusiness(cell.toString());
        if (k == 6) {
            DecimalFormat df = new DecimalFormat("0");
            detail.setUnitPrice(Double.parseDouble(df.format(cell.getNumericCellValue())));
        }
        if (k == 7) {
            DecimalFormat df = new DecimalFormat("0");
            detail.setNumber(Integer.parseInt(df.format(cell.getNumericCellValue())));
        }
        if (k == 8) detail.setLevel(cell.toString());
        if (k == 9) detail.setSpec2(cell.toString());
        if (k == 10) detail.setBarCode(cell.toString());
        if (k == 11) detail.setManufacturer(cell.toString());
        if (k == 12) detail.setPlaceOfOrigin(cell.toString());
        if (k == 13) detail.setRemark(cell.toString());
        if (k == 14) detail.setSerialNumber(cell.toString());
        if (k == 15) detail.setUrl(cell.toString());
        if (k == 16) detail.setRemarks1(cell.toString());
        if (k == 17) detail.setRemarks2(cell.toString());
    }

}
