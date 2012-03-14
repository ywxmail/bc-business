/**
 * 
 */
package cn.bc.business.carman.dao.hibernate.jpa;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaCallback;
import org.springframework.orm.jpa.JpaTemplate;

import cn.bc.business.carman.dao.CarManDao;
import cn.bc.business.carman.domain.CarMan;
import cn.bc.business.cert.dao.CertDao;
import cn.bc.business.cert.domain.Cert;
import cn.bc.business.contract.domain.Contract;
import cn.bc.orm.hibernate.jpa.HibernateCrudJpaDao;

/**
 * 司机责任人Dao的hibernate jpa实现
 * 
 * @author dragon
 */
public class CarManDaoImpl extends HibernateCrudJpaDao<CarMan> implements
		CarManDao {
	protected static final Log logger = LogFactory.getLog(CarManDaoImpl.class);
	private JpaTemplate jpaTemplate;
	private CertDao certDao;

	public void setCertDao(CertDao certDao) {
		this.certDao = certDao;
	}

	@Autowired
	public void setJpaTemplate(JpaTemplate jpaTemplate) {
		this.jpaTemplate = jpaTemplate;
	}

	public CarMan saveCert4CarMan(Long carManId, Cert cert) {
		// 保存证件
		cert = this.certDao.save(cert);

		// 加载司机责任人
		CarMan carMan = this.load(carManId);

		// 添加证件关联
		Set<Cert> certs = carMan.getCerts();
		boolean notin = true;
		if (certs != null) {
			for (Cert c : certs) {
				if (c.getId().equals(cert.getId())) {
					notin = false;
					break;
				}
			}
		} else {
			certs = new HashSet<Cert>();
			carMan.setCerts(certs);
		}
		if (notin) {
			certs.add(cert);
			return this.save(carMan);
		}

		return carMan;
	}

	public CarMan saveCertRelationship(Long carManId, Long certId) {
		CarMan carMan = this.load(carManId);
		Cert cert = this.getJpaTemplate().find(Cert.class, certId);
		Set<Cert> certs = carMan.getCerts();
		boolean notin = true;
		if (certs != null) {
			for (Cert c : certs) {
				if (c.getId().equals(cert.getId())) {
					notin = false;
					break;
				}
			}
		} else {
			certs = new HashSet<Cert>();
			carMan.setCerts(certs);
		}
		if (notin) {
			certs.add(cert);
			return this.save(carMan);
		}
		return carMan;

		// //使用 SQL
		// String hql =
		// "insert into BS_CARMAN_CERT (man_id,cert_id) values (?,?)";
		// List<Object> args = new ArrayList<Object>();
		// args.add(carManId);
		// args.add(certId);
		// this.executeSql(hql, args);
	}

	/**
	 * 根据车辆ID查找返回状态为启用中相关司机信息
	 * 
	 * @parma carId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<CarMan> findAllcarManBycarId(Long carId) {
		String hql = "select c.driver from CarByDriver c where c.car.id=?";
		// 0为启用中
		return this.getJpaTemplate().find(hql, new Object[] { carId });
	}

	public Long checkCert4FWZGIsExists(Long excludeId, String cert4fwzg) {
		boolean gte6 = cert4fwzg.length() > 5;
		if (gte6) {
			// 截取前6位
			cert4fwzg = cert4fwzg.substring(0, 6) + "%";
		}

		String hql = "select m.id as id from CarMan m where m.cert4FWZG"
				+ (gte6 ? " like ?" : " = ?");
		Object[] args;
		if (excludeId != null) {
			hql += " and m.id != ?";
			args = new Object[] { cert4fwzg, excludeId };
		} else {
			args = new Object[] { cert4fwzg };
		}
		@SuppressWarnings("unchecked")
		List<Long> list = this.getJpaTemplate().find(hql, args);
		if (list != null && !list.isEmpty())
			return list.get(0);
		else
			return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public CarMan save(CarMan entity) {
		if (!entity.isNew()) {
			// 重新加载证件信息，避免保存时将其删除
			if (entity.getCerts() == null)
				entity.setCerts(new HashSet<Cert>());
			entity.getCerts()
					.addAll(this
							.getJpaTemplate()
							.find("select cert from CarMan man join man.certs cert where man.id=?",
									entity.getId()));

			// 重新加载合同信息，避免保存时将其删除
			if (entity.getContracts() == null)
				entity.setContracts(new HashSet<Contract>());
			entity.getContracts()
					.addAll(this
							.getJpaTemplate()
							.find("select contract from CarMan man join man.contracts contract where man.id=?",
									entity.getId()));
		}

		// 调用基类的保存
		return super.save(entity);
	}

	public String getNewestCarInfo4Driver(final Long driverId) {

		final StringBuffer sql = new StringBuffer();
		sql.append("select getCarInfoByDriverId(id) from bs_carman where id = ?");

		if (logger.isDebugEnabled()) {
			logger.debug("driverId=" + driverId + ";sql=" + sql);
		}
		// JpaCallback 返回函数的类型
		return this.jpaTemplate.execute(new JpaCallback<String>() {
			// JpaCallback接口的方法
			public String doInJpa(EntityManager em) throws PersistenceException {
				Query queryObject = em.createNativeQuery(sql.toString());
				// 设置问号所对应的参数
				queryObject.setParameter(1, driverId);
				// 设置从第几个开始查
				queryObject.setFirstResult(0);
				// 设置查几多个[如果想查所有,则不设]
				queryObject.setMaxResults(1);
				String carInfo;
				try {
					carInfo = (String) queryObject.getSingleResult();
				} catch (NoResultException e) {
					if (logger.isDebugEnabled())
						logger.debug("carInfo = null,id=" + driverId);
					return null;
				}
				if (carInfo != null) {
					return carInfo;
				}
				return null;
			}
		});

	}
}