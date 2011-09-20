/**
 * 
 */
package cn.bc.business.cert.dao.hibernate.jpa;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.bc.business.cert.dao.CertCyzgDao;
import cn.bc.business.cert.domain.Cert4CongYeZiGe;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;


/**
 * 从业资格证件Dao的hibernate jpa实现
 * 
 * @author dragon
 */
public class CertCyzgDaoImpl extends HibernateCrudJpaDao<Cert4CongYeZiGe> implements CertCyzgDao{
	protected final Log logger =  LogFactory.getLog(getClass());

}

	