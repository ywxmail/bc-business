/**
 * 
 */
package cn.bc.business.carman.dao.hibernate.jpa;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	private CertDao certDao;

	public void setCertDao(CertDao certDao) {
		this.certDao = certDao;
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
		String hql = "select c.driver from CarByDriver c where c.car.id=? and c.status=?";
		// 0为启用中
		return this.getJpaTemplate().find(hql,
				new Object[] { carId, new Integer(0) });
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
}