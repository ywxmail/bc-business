/**
 * 
 */
package cn.bc.business.cert.dao.hibernate.jpa;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.bc.business.cert.dao.CertDrivingDao;
import cn.bc.business.cert.domain.Cert4Driving;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;


/**
 * 动车驾驶证件Dao的hibernate jpa实现
 * 
 * @author dragon
 */
public class CertDrivingDaoImpl extends HibernateCrudJpaDao<Cert4Driving> implements CertDrivingDao{
	protected final Log logger =  LogFactory.getLog(getClass());

}

	