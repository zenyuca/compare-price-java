//package data;
//
//import java.util.Date;
//
//import org.junit.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import com.wzd.model.entity.Welfare;
//import com.wzd.model.enums.HistoryType;
//import com.wzd.service.WelfareService;
//import com.wzd.utils.DateUtil;
//
//import dao.BasicTest;
//
///**
// * 创建测试数据
// * 
// * @author WeiZiDong
// *
// */
//public class Data extends BasicTest {
//	@Autowired
//	private WelfareService welfareService;
//
//	/**
//	 * 创建福利
//	 */
//	private void crete(Integer days, String title, Integer score, Integer time, Integer total, HistoryType type) {
//		Welfare w = new Welfare();
//		Date date = new Date();
//		w.setProvider("成都爱创业科技有限公司"); // 提供者
//		w.setWebsite("http://www.ichuangye.cn"); // 提供者网站
//		w.setStartTime(DateUtil.getBeforeDate(date, days)); // 开始时间
//		w.setEndTime(DateUtil.getAfterDate(date, days)); // 结束时间
//		w.setName(title); // 名称
//		w.setScore(score); // 积分
//		w.setTime(time); // 次数
//		w.setTotal(total); // 总的个数
//		w.setType(type); // 类型
//		w.setRule("1、福利期间每人只能兑换" + time + "次福利。"); // 规则
//		welfareService.create(w, null);
//	}
//
//	@Test
//	public void createWelfare() {
//		crete(15, "一元红包", 500, 2, 3000, HistoryType.红包福利);
//		crete(10, "电影票兑换", 1000, 1, 500, HistoryType.券票福利);
//		crete(20, "优惠券兑换", 2000, 1, 200, HistoryType.券票福利);
//	}
//
//}
