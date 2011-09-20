/**
 * 
 */
package cn.bc.business.cert.service;

import cn.bc.business.cert.dao.CertDrivingDao;
import cn.bc.business.cert.domain.Cert4Driving;
import cn.bc.core.service.DefaultCrudService;

/**
 * 机动车驾驶证件Service的实现
 * 
 * @author dragon
 */
public class CertDrivingServiceImpl extends DefaultCrudService<Cert4Driving> implements
		CertDrivingService {
	private CertDrivingDao certDrivingDao ;

	public CertDrivingDao getCertDrivingDao() {
		return certDrivingDao;
	}

	public void setCertDrivingDao(CertDrivingDao certDrivingDao) {
		this.certDrivingDao = certDrivingDao;
		this.setCrudDao(certDrivingDao);
	}
	
}