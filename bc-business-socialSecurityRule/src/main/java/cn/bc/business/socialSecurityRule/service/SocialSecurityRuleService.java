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
	 * @param areaName 使用区域  null时自动设置默认值，'广东省广州市'
	 * @param houseType 户口类型
	 * @param year 年
	 * @param month 月
	 * @return 返回根据年月计算个人需要缴纳的社保费用
	 */
	public Float countPersonal(String areaName,String houseType,int year,int month);
	
	/**
	 * 计算单位社保费用接口
	 * @param areaName 使用区域  null时自动设置默认值，'广东省广州市'
	 * @param houseType 户口类型
	 * @param year 年
	 * @param month 月
	 * @return 返回根据年月计算单位每月需要为个人缴纳的社保费用
	 */
	public Float countUnit(String areaName,String houseType,int year,int month);
	
	/**
	 * 根据户口类型计算现时广东省广州市个人社保费用接口
	 * @param houseType 户口类型
	 * @return 返回单位每月个人缴纳的社保费用
	 */
	public Float countNowPersonal4GZ(String houseType);
	
	/**
	 * 根据户口类型计算现时广东省广州市单位社保费用接口
	 * @param houseType 户口类型
	 * @return 返回单位每月单位需要为个人缴纳的社保费用
	 */
	public Float countNowUnit4GZ(String houseType);

}