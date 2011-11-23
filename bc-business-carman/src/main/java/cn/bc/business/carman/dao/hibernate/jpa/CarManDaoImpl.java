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
		//保存证件
		cert = this.certDao.save(cert);
		
		//加载司机责任人
		CarMan carMan = this.load(carManId);
		
		//添加证件关联
		Set<Cert> certs = carMan.getCerts();
		boolean notin = true;
		if (certs != null) {
			for (Cert c : certs) {
				if (c.getId().equals(cert.getId())) {
					notin = false;
					break;
				}
			}
		}else{
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
		}else{
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
	public List<CarMan> findAllcarManBycarId(Long carId) {
		String hql = "select c.driver from CarByDriver c where c.car.id=? and c.status=?";
		//0为启用中
		return this.getJpaTemplate().find(hql, new Object[] { carId,new Integer( 0 )});
	}
}