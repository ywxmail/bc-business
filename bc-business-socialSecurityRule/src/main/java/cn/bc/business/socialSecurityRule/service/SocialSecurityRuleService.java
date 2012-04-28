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
}