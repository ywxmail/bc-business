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
}
