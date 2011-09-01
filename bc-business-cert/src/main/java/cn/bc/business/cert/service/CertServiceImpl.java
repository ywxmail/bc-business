/**
 * 
 */
package cn.bc.business.cert.service;

import cn.bc.business.cert.dao.CertDao;
import cn.bc.business.cert.domain.Cert;
import cn.bc.core.service.DefaultCrudService;

/**
 * 证件Service的实现
 * 
 * @author dragon
 */
public class CertServiceImpl extends DefaultCrudService<Cert> implements
		CertService {
	private CertDao certDao;

	public CertDao getCertDao() {
		return certDao;
	}

	public void setCertDao(CertDao certDao) {
		this.certDao = certDao;
		this.setCrudDao(certDao);
	}

	public Cert findCertByCarManId(Long carManId) {
		Cert cert = this.certDao.findCertByCarManId(carManId);
		return cert;
	}
}