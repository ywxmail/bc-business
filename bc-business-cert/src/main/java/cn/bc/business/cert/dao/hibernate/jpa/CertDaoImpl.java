/**
 * 
 */
package cn.bc.business.cert.dao.hibernate.jpa;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.bc.business.cert.dao.CertDao;
import cn.bc.business.cert.domain.Cert;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;


/**
 * 证件Dao的hibernate jpa实现
 * 
 * @author dragon
 */
public class CertDaoImpl extends HibernateCrudJpaDao<Cert> implements CertDao{
	protected final Log logger =  LogFactory.getLog(getClass());
	@SuppressWarnings("rawtypes")
	public Cert findCertByCarManId(Long carManId){
		
		Cert cert = null;
		String hql = "select c from CarMan cman join cman.certs c where cman.id = ?";
		List list = this.getJpaTemplate().find(hql, new Object [] {carManId});
		if(list.size() == 1){
			cert = (Cert)list.get(0);;
			return cert;
		}else if(list.size() < 1){
			return null;
		}else{
			cert = (Cert)list.get(0);
			if(logger.isDebugEnabled()){
				logger.debug("有两个或两个以上证件的，已选择其第一个");
			}
			return cert;
		}
	}
}