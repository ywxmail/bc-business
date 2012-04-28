/**
 * 
 */
package cn.bc.business.socialSecurityRule.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.business.socialSecurityRule.dao.SocialSecurityRuleDao;
import cn.bc.business.socialSecurityRule.domain.SocialSecurityRule;
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


	
}