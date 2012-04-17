/**
 * 
 */
package cn.bc.business.sync.dao.hibernate.jpa;

import java.util.Calendar;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaCallback;
import org.springframework.orm.jpa.JpaTemplate;

import cn.bc.business.carman.dao.hibernate.jpa.CarManDaoImpl;
import cn.bc.business.sync.dao.JiaoWeiJTWFDao;
import cn.bc.business.sync.domain.JiaoWeiJTWF;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;

/**
 * 交委交通违章Dao 的hibernate jpa实现
 * 
 * @author wis
 */
public class JiaoWeiJTWFDaoImpl extends HibernateCrudJpaDao<JiaoWeiJTWF>
		implements JiaoWeiJTWFDao {
	protected static final Log logger = LogFactory.getLog(CarManDaoImpl.class);
	private JpaTemplate jpaTemplate;

	@Autowired
	public void setJpaTemplate(JpaTemplate jpaTemplate) {
		this.jpaTemplate = jpaTemplate;
	}

	public String findJinDunAddress(final String syncCode,
			final String plateNo, final Calendar happenDate) {

		final StringBuffer sql = new StringBuffer();
		sql.append("select findJinDunByJiaoWei(?,?,?)  from bs_sync_jiaowei_jtwf t ");
		// sql.append(" inner join bc_sync_base b on b.id=t.id");

		if (logger.isDebugEnabled()) {
			logger.debug("syncCode=" + syncCode + ";sql=" + sql);
		}
		// JpaCallback 返回函数的类型
		return this.jpaTemplate.execute(new JpaCallback<String>() {
			// JpaCallback接口的方法
			public String doInJpa(EntityManager em) throws PersistenceException {
				Query queryObject = em.createNativeQuery(sql.toString());
				// 设置问号所对应的参数
				queryObject.setParameter(1, syncCode);
				queryObject.setParameter(2, plateNo);
				queryObject.setParameter(3, happenDate);
				// 设置从第几个开始查
				queryObject.setFirstResult(0);
				// 设置查几多个[如果想查所有,则不设]
				queryObject.setMaxResults(1);
				String address;
				try {
					address = (String) queryObject.getSingleResult();
				} catch (NoResultException e) {
					if (logger.isDebugEnabled())
						logger.debug("address = null,syncCode=" + syncCode);
					return null;
				}
				if (address != null) {
					return address;
				}
				return null;
			}
		});

	}

}