/**
 * 
 */
package cn.bc.business.cert.service;

import cn.bc.business.cert.dao.CertFwzgDao;
import cn.bc.business.cert.domain.Cert4FuWuZiGe;
import cn.bc.core.service.DefaultCrudService;

/**
 * 服务资格证Service的实现
 * 
 * @author dragon
 */
public class CertFwzgServiceImpl extends DefaultCrudService<Cert4FuWuZiGe> implements
		CertFwzgService {
	private CertFwzgDao certFwzgDao;

	public CertFwzgDao getCertFwzgDao() {
		return certFwzgDao;
	}

	public void setCertFwzgDao(CertFwzgDao certFwzgDao) {
		this.certFwzgDao = certFwzgDao;
		this.setCrudDao(certFwzgDao);
	}
	
}