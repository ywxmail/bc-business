/**
 * 
 */
package cn.bc.business.cert.service;

import cn.bc.business.cert.dao.CertVehicelicenseDao;
import cn.bc.business.cert.domain.Cert4VehiceLicense;
import cn.bc.core.service.DefaultCrudService;

/**
 * 机动车行驶证Service的实现
 * 
 * @author dragon
 */
public class CertVehicelicenseServiceImpl extends DefaultCrudService<Cert4VehiceLicense> implements
	CertVehicelicenseService {
	private CertVehicelicenseDao certVehicelicenseDao;

	public CertVehicelicenseDao getCertVehicelicenseDao() {
		return certVehicelicenseDao;
	}

	public void setCertVehicelicenseDao(CertVehicelicenseDao certVehicelicenseDao) {
		this.certVehicelicenseDao = certVehicelicenseDao;
		this.setCrudDao(certVehicelicenseDao);
	}
	
}