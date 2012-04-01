package cn.bc.business.ownership.service;

import java.util.ArrayList;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.business.car.dao.CarDao;
import cn.bc.business.car.domain.Car;
import cn.bc.business.ownership.dao.OwnershipDao;
import cn.bc.business.ownership.domain.Ownership;
import cn.bc.core.service.DefaultCrudService;

/**
 * @author zxr 车辆经营权serviec实现类
 * 
 */
public class OwnershipServiceImpl extends DefaultCrudService<Ownership>
		implements OwnershipService {
	private OwnershipDao ownershipDao;
	public CarDao carDao;

	public void setOwnershipDao(OwnershipDao ownershipDao) {
		this.ownershipDao = ownershipDao;
		this.setCrudDao(ownershipDao);
	}

	@Autowired
	public void setCarDao(CarDao carDao) {
		this.carDao = carDao;
	}

	public OwnershipDao getOwnershipDao() {
		return ownershipDao;
	}

	public Ownership getEByCarId(Long carId) {
		return this.ownershipDao.getEntityByCarid(carId);
	}

	// 保存批量处理经营权的车辆
	public void saveBatchTaxis(Map<String, Object> ownershipInfo,
			Long[] carIds, Ownership o) {
		// 需要更新的车辆ID数组
		ArrayList<Object> updatecarIds = new ArrayList<Object>();
		// 需要插入的车辆ID数组
		ArrayList<Object> insertcarIds = new ArrayList<Object>();

		for (int i = 0; i < carIds.length; i++) {
			Ownership os = this.getEByCarId(carIds[i]);
			// 反回的对象不为空，放入更新车辆ID的数组
			if (os != null) {
				updatecarIds.add(carIds[i]);

			} else {
				// 放入插入车辆ID的数组
				insertcarIds.add(carIds[i]);
			}
		}
		// 执行更新操作
		if ((Long[]) updatecarIds.toArray(new Long[0]) != null
				|| ((Long[]) updatecarIds.toArray(new Long[0])).length != 0) {
			this.ownershipDao.updateOwnershipByCarId(ownershipInfo,
					(Long[]) updatecarIds.toArray(new Long[0]));
		}
		// 执行插入操作
		if ((Long[]) insertcarIds.toArray(new Long[0]) != null
				|| ((Long[]) insertcarIds.toArray(new Long[0])).length != 0) {
			for (Long carId : (Long[]) insertcarIds.toArray(new Long[0])) {
				Ownership ow = new Ownership();
				ow = o;
				Car insertCar = this.carDao.load(carId);
				ow.setCar(insertCar);
				this.ownershipDao.save(ow);
			}
		}
	}

}
