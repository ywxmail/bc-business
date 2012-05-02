/**
 * 
 */
package cn.bc.business.socialSecurityRule.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
public class SocialSecurityRuleServiceImpl extends DefaultCrudService<SocialSecurityRule> implements
		SocialSecurityRuleService {
	private SocialSecurityRuleDao socialSecurityRuleDao;

	@Autowired
	public void setSocialSecurityRuleDao(SocialSecurityRuleDao socialSecurityRuleDao) {
		this.socialSecurityRuleDao = socialSecurityRuleDao;
		this.setCrudDao(socialSecurityRuleDao);
	}

	public List<Map<String, String>> findAreaOption() {
		return this.socialSecurityRuleDao.findAreaOption();
	}

	public List<Map<String, String>> findHouseTypeOption() {
		return this.socialSecurityRuleDao.findHouseTypeOption();
	}

	public String countPersonal(String areaName, String houseType,int year,int month){
		return this.count(areaName, houseType, year, month, "personal");
	}

	public String countUnit(String areaName, String houseType,int year,int month){
		return this.count(areaName, houseType, year, month, "unit");
	}
	
	private String count(String areaName, String houseType,int year,int month,String countType){
		if(month<1||month>12)return null;
		
		List<SocialSecurityRule> list=this.socialSecurityRuleDao
				.findSocialSecurityRules(areaName, houseType, year);
		
		if(list==null)return null;
		//声明社保规则
		SocialSecurityRule ssr=null;
		
		Integer index=null;
		//比较月份得出需要使用的社保规则
		for(int i=0;i<list.size();i++){
			if(list.get(i).getStartYear()==year){
				if(month>=list.get(i).getStartMonth()){
					index=i;
					break;
				}
			}else{
				index=i;
				break;
			}
		}
		if(index==null)return null;
		ssr=list.get(index);
		
		Set<SocialSecurityRuleDetail> ssrdSet=ssr.getSocialSecurityRuleDetail();
		
		if(ssrdSet==null)return null;
		
		//声明保存社保费用的值。
		Float fee=new Float(0);
		
		Object[] ssrdArrObj=ssrdSet.toArray();
		for(Object ssrdObj: ssrdArrObj){
			SocialSecurityRuleDetail ssrd=(SocialSecurityRuleDetail) ssrdObj;
			if(countType.equals("personal")){
				float f=ssrd.getPersonalRate()*ssrd.getBaseNumber();
				if(f!=0){
					BigDecimal b=new BigDecimal(f/100);
					fee+=b.setScale(2,BigDecimal.ROUND_HALF_UP).floatValue();
				}
			}else if(countType.equals("unit")){
				float f=ssrd.getUnitRate()*ssrd.getBaseNumber();
				if(f!=0){
					BigDecimal b=new BigDecimal(f/100);
					fee+=b.setScale(2,BigDecimal.ROUND_HALF_UP).floatValue();
				}
			}
		}
		BigDecimal b=new BigDecimal(fee);
		fee=b.setScale(2,BigDecimal.ROUND_HALF_UP).floatValue();
		return fee.toString();
	}

}