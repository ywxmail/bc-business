/**
 * 
 */
package cn.bc.business.certLost.service;

import cn.bc.business.certLost.dao.CertLostDao;
import cn.bc.business.certLost.domain.CertLost;
import cn.bc.core.service.DefaultCrudService;

/**
 * 证照遗失Service的实现
 * 
 * @author zxr
 */
public class CertLostServiceImpl extends DefaultCrudService<CertLost> implements
		CertLostService {

	private CertLostDao certLostDao;

	public CertLostDao getCertLostDao() {
		return certLostDao;
	}

	public void setCertLostDao(CertLostDao certLostDao) {
		this.certLostDao = certLostDao;
		this.setCrudDao(certLostDao);
	}

}