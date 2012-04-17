/**
 * 
 */
package cn.bc.business.fee.template.service;

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

	
}