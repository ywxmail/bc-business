/**
 * 
 */
package cn.bc.business.fee.template.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.business.fee.template.dao.FeeTemplateDao;
import cn.bc.business.fee.template.domain.FeeTemplate;
import cn.bc.core.service.DefaultCrudService;

/**
 * Service的实现
 * 
 * @author 
 */
public class FeeTemplateServiceImpl extends DefaultCrudService<FeeTemplate> implements
		FeeTemplateService {
	private FeeTemplateDao feeTemplateDao;

	@Autowired
	public void setFeeTemplateDao(FeeTemplateDao feeTemplateDao) {
		this.feeTemplateDao = feeTemplateDao;
		this.setCrudDao(feeTemplateDao);
	}

	public List<Map<String, String>> getTemplate() {
		return this.feeTemplateDao.getTemplate();
	}

	public boolean isFeeBelong2Template(Long pid) {
		return this.feeTemplateDao.isFeeBelong2Template(pid);
	}

	public boolean isTemplateExistFee(Long id) {
		return this.feeTemplateDao.isTemplateExistFee(id);
	}

	public List<Map<String, String>> getFeeBelong2Template(Long pid) {
		return this.getFeeBelong2Template(pid);
	}

	
	
}