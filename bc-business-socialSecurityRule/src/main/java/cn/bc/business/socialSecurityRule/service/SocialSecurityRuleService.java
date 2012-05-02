/**
 * 
 */
package cn.bc.business.socialSecurityRule.service;

import java.util.List;
import java.util.Map;

import cn.bc.business.socialSecurityRule.domain.SocialSecurityRule;
import cn.bc.core.service.CrudService;

/**
 * 
 * 
 * @author 
 */
public interface SocialSecurityRuleService extends CrudService<SocialSecurityRule> {

	/**
	 * 查找使用区域选项
	 * 
	 * @return
	 */
	public List<Map<String,String>> findAreaOption();
	
	/**
	 * 查找户口类型选项
	 * 
	 * @return
	 */
	public List<Map<String,String>> findHouseTypeOption();
	
	/**
	 * 计算个人社保费用接口
	 * @param areaName 使用区域
	 * @param houseType 户口类型
	 * @param year 年
	 * @param month 月
	 * @return 返回根据年月计算个人需要缴纳的社保费用
	 */
	public String countPersonal(String areaName,String houseType,int year,int month);
	
	/**
	 * 计算单位社保费用接口
	 * @param areaName 使用区域
	 * @param houseType 户口类型
	 * @param year 年
	 * @param month 月
	 * @return 返回根据年月计算单位每月需要为个人缴纳的社保费用
	 */
	public String countUnit(String areaName,String houseType,int year,int month);

}