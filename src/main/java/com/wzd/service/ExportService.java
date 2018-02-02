package com.wzd.service;

import com.wzd.model.dao.ExportDao;
import com.wzd.model.dao.ExportDetailCopyDao;
import com.wzd.model.dao.ExportDetailDao;
import com.wzd.model.dao.FileDao;
import com.wzd.model.entity.Files;
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

    public Export find(Integer type) {

        return null;
    }

    public Map findDetailCopyByType (Integer type) {
        Example example = new Example(Export.class);
        example.setOrderByClause("createTime DESC");
        example.createCriteria().andEqualTo("type",type);
        List<Export> exports = exportDao.selectByExample(example);
        if (exports == null || exports.size() < 1)return null;
        example = new Example(ExportDetailCopy.class);
        example.setOrderByClause("num ASC");
        example.createCriteria().andEqualTo("exportId",exports.get(0).getId());
        Map<String,Object> result = new HashMap<>();
        result.put("exportId",exports.get(0).getId());
        result.put("downUrl",exports.get(0).getUrl());
        result.put("endTime",exports.get(0).getEndTime());
        result.put("data",detailCopyDao.selectByExample(example));
        return result;
    }

    public List<ExportDetail> findTenderResult (Integer type) {
        Example example = new Example(Export.class);
        example.setOrderByClause("createTime DESC");
        example.createCriteria().andEqualTo("type",type);
        List<Export> exports = exportDao.selectByExample(example);
        if (exports == null || exports.size() < 1)return null;
        return detailDao.findTenderResult(exports.get(0).getId());
    }

    public String exportTenderResult (Integer type) {
        Example example = new Example(Export.class);
        example.setOrderByClause("createTime DESC");
        example.createCriteria().andEqualTo("type",type);
        List<Export> exports = exportDao.selectByExample(example);
        if (exports == null || exports.size() < 1)return null;
        String[] headers = new String[] { "序号@num", "名称@name", "规格@spec", "数量@number",
                "单价@unitPrice", "供应商名称@agentName", "菜品要求@business","实物图片@url","备注@remark" };
        String url = PoiExcelUtils.createExcel2FilePath("招标结果列表", "招标结果列表", FileUtil.BASE_PATH, headers, detailDao.findTenderResult(exports.get(0).getId()));
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
//        FormDataBodyPart filePart = form.getField("file");
//        Integer type = null;
//        if (form.getField("type") != null) {
//            type = form.getField("type").getValueAs(Integer.class);
//        }
//        InputStream inputStream = filePart.getValueAs(InputStream.class);
//        FormDataContentDisposition disposition = filePart.getFormDataContentDisposition();
//        InputStream file = filePart.getValueAs(InputStream.class);
//        Files f = FileUtil.writeFile(file, disposition);
//        Export export = new Export();
//        export.setUrl(f.getUrl());
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
                            HSSFCell cell = row.getCell(k);
                            if (row.getCell(k) != null && cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {// 如果为数字
                                DecimalFormat df = new DecimalFormat("0");
                                map.put(header.get(k), df.format(cell.getNumericCellValue()));
                            } else if (row.getCell(k) != null) {
                                map.put(header.get(k), cell.toString());
                            }
                            if (cell == null) continue;
                            if (k == 0) {
                                DecimalFormat df = new DecimalFormat("0");
                                detailCopy.setNum(Integer.parseInt(df.format(cell.getNumericCellValue())));
                            }
                            if (k == 1) detailCopy.setName(cell.toString());
                            if (k == 2) detailCopy.setCategory(cell.toString());
                            if (k == 3) detailCopy.setSpec(cell.toString());
                            if (k == 4) detailCopy.setUnit(cell.toString());
                            if (k == 5) detailCopy.setBusiness(cell.toString());
//                            if (k == 6) {
//                                DecimalFormat df = new DecimalFormat("0");
//                                detailCopy.setUnitPrice(Integer.parseInt(df.format(cell.getNumericCellValue())));
//                            }
//                            if (k == 7) {
//                                DecimalFormat df = new DecimalFormat("0");
//                                detailCopy.setNumber(Integer.parseInt(df.format(cell.getNumericCellValue())));
//                            }
                            if (k == 8) detailCopy.setLevel(cell.toString());
                            if (k == 9) detailCopy.setSpec2(cell.toString());
                            if (k == 10) detailCopy.setBarCode(cell.toString());
                            if (k == 11) detailCopy.setManufacturer(cell.toString());
                            if (k == 12) detailCopy.setPlaceOfOrigin(cell.toString());
                            if (k == 12) detailCopy.setRemark(cell.toString());
                            if (k == 12) detailCopy.setSerialNumber(cell.toString());
                            if (k == 12) detailCopy.setUrl(cell.toString());
//                            if (null != cell) {
//                                switch (cell.getCellType()) {
//                                    case HSSFCell.CELL_TYPE_NUMERIC: // 数字
//                                        DecimalFormat df = new DecimalFormat("0");
//                                        System.err.print(df.format(cell.getNumericCellValue())
//                                                + "   ");
//                                        break;
//                                    case HSSFCell.CELL_TYPE_STRING: // 字符串
//                                        System.out.print(cell.getStringCellValue()
//                                                + "   ");
//                                        break;
//                                    case HSSFCell.CELL_TYPE_BOOLEAN: // Boolean
//                                        System.out.println(cell.getBooleanCellValue()
//                                                + "   ");
//                                        break;
//                                    case HSSFCell.CELL_TYPE_FORMULA: // 公式
//                                        System.out.print(cell.getCellFormula() + "   ");
//                                        break;
//                                    case HSSFCell.CELL_TYPE_BLANK: // 空值
//                                        System.out.println(" ");
//                                        break;
//                                    case HSSFCell.CELL_TYPE_ERROR: // 故障
//                                        System.out.println(" ");
//                                        break;
//                                    default:
//                                        System.out.print("未知类型   ");
//                                        break;
//                                }
//                            }
                        }
                        data.add(map);
                    }
                    if (j == 1)continue;
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


    //select a.* from bj_export_detail_copy a where unitPrice = (select min(unitPrice) from bj_export_detail_copy where name = a.name) order by a.name
    public Object importTenderExcel(Integer type,String exportId,FormDataMultiPart form, HttpServletRequest request) {
        Admin admin = (Admin) SessionUtil.getUser(request);
        if (type == null || StringUtils.isBlank(exportId)) {
            throw new WebException(ResponseCode.数据参数异常,"传入类型或导入id不存在");
        }
        Example example = new Example(ExportDetail.class);
        example.createCriteria().andEqualTo("exportId",exportId);
//        detailDao.deleteByExample(example);// 删除之前存在的，对应exportId
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
                            HSSFCell cell = row.getCell(k);
                            if (row.getCell(k) != null && cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {// 如果为数字
                                DecimalFormat df = new DecimalFormat("0");
                                map.put(header.get(k), df.format(cell.getNumericCellValue()));
                            } else if (row.getCell(k) != null) {
                                map.put(header.get(k), cell.toString());
                            }
                            if (cell == null) continue;
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
                                detail.setUnitPrice(Integer.parseInt(df.format(cell.getNumericCellValue())));
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
                            if (k == 12) detail.setRemark(cell.toString());
                            if (k == 12) detail.setSerialNumber(cell.toString());
                            if (k == 12) detail.setUrl(cell.toString());
                        }
                        data.add(map);
                    }
                    if (j == 1)continue;
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
}
