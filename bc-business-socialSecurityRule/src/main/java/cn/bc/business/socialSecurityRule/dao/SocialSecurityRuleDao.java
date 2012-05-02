package cn.bc.business.socialSecurityRule.dao;

import java.util.List;
import java.util.Map;

import cn.bc.business.socialSecurityRule.domain.SocialSecurityRule;
import cn.bc.core.dao.CrudDao;

public interface SocialSecurityRuleDao extends CrudDao<SocialSecurityRule> {

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
	 * 根据使用区域、户口类型、年份、月份查找社保信息
	 * 
	 * @param areaName 使用区域
	 * @param houseType 户口类型
	 * @param year 年份
	 * @return 根据startYear降序的集合
	 */
	public List<SocialSecurityRule> findSocialSecurityRules(String areaName,String houseType,int year);
}
