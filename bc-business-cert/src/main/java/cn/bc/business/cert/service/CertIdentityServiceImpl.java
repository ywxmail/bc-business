/**
 * 
 */
package cn.bc.business.cert.service;

import cn.bc.business.cert.dao.CertIdentityDao;
import cn.bc.business.cert.domain.Cert4Identity;
import cn.bc.core.service.DefaultCrudService;

/**
 * 居民身份证Service的实现
 * 
 * @author dragon
 */
public class CertIdentityServiceImpl extends DefaultCrudService<Cert4Identity> implements
		CertIdentityService {
	private CertIdentityDao certIdentityDao;

	public CertIdentityDao getCertIdentityDao() {
		return certIdentityDao;
	}

	public void setCertIdentityDao(CertIdentityDao certIdentityDao) {
		this.certIdentityDao = certIdentityDao;
		this.setCrudDao(certIdentityDao);
	}
	
}