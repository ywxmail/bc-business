/**
 * 
 */
package cn.bc.business.cert.dao.hibernate.jpa;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.bc.business.cert.dao.CertRoadtransportDao;
import cn.bc.business.cert.domain.Cert4RoadTransport;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;


/**
 * 道路运输证Dao的hibernate jpa实现
 * 
 * @author dragon
 */
public class CertRoadtransportDaoImpl extends HibernateCrudJpaDao<Cert4RoadTransport> implements CertRoadtransportDao{
	protected final Log logger =  LogFactory.getLog(getClass());

}

	