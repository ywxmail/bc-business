/**
 * 
 */
package cn.bc.business.cert.service;

import cn.bc.business.cert.dao.CertRoadtransportDao;
import cn.bc.business.cert.domain.Cert4RoadTransport;
import cn.bc.core.service.DefaultCrudService;

/**
 * 道路运输证Service
 * 
 * @author dragon
 */
public class CertRoadtransportServiceImpl extends DefaultCrudService<Cert4RoadTransport> implements
	CertRoadtransportService {
	private CertRoadtransportDao certRoadtransportDao;

	public CertRoadtransportDao getCertRoadtransportDao() {
		return certRoadtransportDao;
	}

	public void setCertRoadtransportDao(CertRoadtransportDao certRoadtransportDao) {
		this.certRoadtransportDao = certRoadtransportDao;
		this.setCrudDao(certRoadtransportDao);
	}
	
}