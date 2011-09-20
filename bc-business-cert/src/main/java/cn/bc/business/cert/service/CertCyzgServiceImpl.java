/**
 * 
 */
package cn.bc.business.cert.service;

import cn.bc.business.cert.dao.CertCyzgDao;
import cn.bc.business.cert.domain.Cert4CongYeZiGe;
import cn.bc.core.service.DefaultCrudService;

/**
 * 从业资格证Service的实现
 * 
 * @author dragon
 */
public class CertCyzgServiceImpl extends DefaultCrudService<Cert4CongYeZiGe> implements
		CertCyzgService {
	private CertCyzgDao certCyzgDao;

	public CertCyzgDao getCertCyzgDao() {
		return certCyzgDao;
	}

	public void setCertCyzgDao(CertCyzgDao certCyzgDao) {
		this.certCyzgDao = certCyzgDao;
		this.setCrudDao(certCyzgDao);
	}
	
}