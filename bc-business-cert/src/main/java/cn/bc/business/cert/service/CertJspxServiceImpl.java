/**
 * 
 */
package cn.bc.business.cert.service;

import cn.bc.business.cert.dao.CertJspxDao;
import cn.bc.business.cert.domain.Cert4DriverEducation;
import cn.bc.core.service.DefaultCrudService;

/**
 * 驾驶培训证Service的实现
 * 
 * @author dragon
 */
public class CertJspxServiceImpl extends DefaultCrudService<Cert4DriverEducation> implements
		CertJspxService {
	private CertJspxDao certJspxDao;

	public CertJspxDao getCertJspxDao() {
		return certJspxDao;
	}

	public void setCertJspxDao(CertJspxDao certJspxDao) {
		this.certJspxDao = certJspxDao;
		this.setCrudDao(certJspxDao);
	}
	
}