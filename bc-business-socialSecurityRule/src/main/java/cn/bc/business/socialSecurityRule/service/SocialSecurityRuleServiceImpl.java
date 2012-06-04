/**
 * 
 */
package cn.bc.business.socialSecurityRule.service;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.business.socialSecurityRule.dao.SocialSecurityRuleDao;
import cn.bc.business.socialSecurityRule.domain.SocialSecurityRule;
import cn.bc.business.socialSecurityRule.domain.SocialSecurityRuleDetail;
import cn.bc.core.service.DefaultCrudService;

/**
 * Service的实现
 * 
 * @author
 */
public class SocialSecurityRuleServiceImpl extends
		DefaultCrudService<SocialSecurityRule> implements
		SocialSecurityRuleService {
	private static Log logger = LogFactory
			.getLog(SocialSecurityRuleServiceImpl.class);

	private SocialSecurityRuleDao socialSecurityRuleDao;

	@Autowired
	public void setSocialSecurityRuleDao(
			SocialSecurityRuleDao socialSecurityRuleDao) {
		this.socialSecurityRuleDao = socialSecurityRuleDao;
		this.setCrudDao(socialSecurityRuleDao);
	}

	public List<Map<String, String>> findAreaOption() {
		return this.socialSecurityRuleDao.findAreaOption();
	}

	public List<Map<String, String>> findHouseTypeOption() {
		return this.socialSecurityRuleDao.findHouseTypeOption();
	}

	public Float countPersonal(String areaName, String houseType, int year,
			int month) {
		return this.count(areaName, houseType, year, month, "personal");
	}

	public Float countUnit(String areaName, String houseType, int year,
			int month) {
		return this.count(areaName, houseType, year, month, "unit");
	}

	private Float count(String areaName, String houseType, int year,
			int month, String countType) {
		if (month < 1 || month > 12) {
			logger.warn(month + "月,无此月份！");
			return null;
		}

		List<SocialSecurityRule> list = this.socialSecurityRuleDao
				.findSocialSecurityRules(areaName == null
						|| areaName.length() == 0 ? "广东省广州市" : areaName,
						houseType, year);

		if (list == null) {
			logger.warn("找不到区域为" + areaName + ",户口类型为" + houseType + ",年份小于或等于"
					+ year + "年的社保规则！");
			return null;
		}

		// 声明社保规则
		SocialSecurityRule ssr = null;

		Integer index = null;
		// 比较月份得出需要使用的社保规则
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getStartYear() == year) {
				if (month >= list.get(i).getStartMonth()) {
					index = i;
					break;
				}
			} else {
				index = i;
				break;
			}
		}

		if (index == null) {
			logger.warn("根据使用区域" + areaName + "、户口类型" + houseType + "、年份"
					+ year + "、月份" + month + ",找不到相应社保规则！");
			return null;
		}
		ssr = list.get(index);

		Set<SocialSecurityRuleDetail> ssrdSet = ssr
				.getSocialSecurityRuleDetail();

		if (ssrdSet == null) {
			logger.warn(ssr.getAreaName() + ssr.getHouseType()
					+ ssr.getStartYear() + "年" + ssr.getStartMonth()
					+ "社保收费规则没明细！");
			return null;
		}

		// 声明保存社保费用的值。
		Float fee = new Float(0);

		Object[] ssrdArrObj = ssrdSet.toArray();
		for (Object ssrdObj : ssrdArrObj) {
			SocialSecurityRuleDetail ssrd = (SocialSecurityRuleDetail) ssrdObj;
			if (countType.equals("personal")) {
				float f = ssrd.getPersonalRate() * ssrd.getBaseNumber();
				if (f != 0) {
					BigDecimal b = new BigDecimal(f / 100);
					// 四舍五入，去小点后两位
					fee += b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				}
			} else if (countType.equals("unit")) {
				float f = ssrd.getUnitRate() * ssrd.getBaseNumber();
				if (f != 0) {
					BigDecimal b = new BigDecimal(f / 100);
					fee += b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				}
			}
		}
		BigDecimal b = new BigDecimal(fee);
		fee = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
		return fee;
	}

	public Float countNowPersonal4GZ(String houseType) {
		return this.count(null, houseType, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH) + 1, "personal");
	}

	public Float countNowUnit4GZ(String houseType) {
		return this.count(null, houseType, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH) + 1, "unit");
	}

}