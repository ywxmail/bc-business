package cn.bc.business.ownership.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.business.car.dao.CarDao;
import cn.bc.business.ownership.dao.OwnershipDao;
import cn.bc.business.ownership.domain.Ownership;
import cn.bc.core.service.DefaultCrudService;
import cn.bc.identity.domain.ActorHistory;
import cn.bc.identity.web.SystemContextHolder;

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
	public void saveBatchTaxis(Map<String, Object> ownershipInfo, Long[] carIds) {
		// 需要更新的车辆ID数组
		ArrayList<Object> updateCarIds = new ArrayList<Object>();
		// // 需要插入的车辆ID数组
		// ArrayList<Object> insertcarIds = new ArrayList<Object>();
		// 需要更新的车辆Id
		updateCarIds = this.ownershipDao.getUpdateCarIdsList(carIds);

		// 所有的车辆的ID
		ArrayList<Long> allCarIds = new ArrayList<Long>(Arrays.asList(carIds));

		// 找出carIds里要更新的车辆ID
		ArrayList<Object> findUpdateCarIds = new ArrayList<Object>();
		if (updateCarIds != null) {
			if (updateCarIds.size() != 0) {
				for (int i = 0; i < allCarIds.size(); i++) {
					for (int j = 0; j < updateCarIds.size(); j++) {
						if (allCarIds.get(i).equals(updateCarIds.get(j))) {
							findUpdateCarIds.add(allCarIds.get(i));
						}
					}
				}
			}
		}

		// 将要更新的车辆ID从所有车辆的ID里移走
		allCarIds.removeAll(findUpdateCarIds);
		// 执行更新操作
		if (updateCarIds != null) {
			if ((Long[]) updateCarIds.toArray(new Long[0]) != null
					|| ((Long[]) updateCarIds.toArray(new Long[0])).length != 0) {
				this.ownershipDao.updateOwnershipByCarId(ownershipInfo,
						(Long[]) updateCarIds.toArray(new Long[0]));
			}
		}
		// 执行插入操作
		ActorHistory author = SystemContextHolder.get().getUserHistory();
		Calendar fileDate = (Calendar) ownershipInfo.get("fileDate");
		ActorHistory modifier = SystemContextHolder.get().getUserHistory();
		Calendar modifiedDate = (Calendar) ownershipInfo.get("modifiedDate");
		String nature = (String) ownershipInfo.get("nature");
		String situation = (String) ownershipInfo.get("situation");
		String owner = (String) ownershipInfo.get("owner");
		String description = (String) ownershipInfo.get("description");
		if (allCarIds != null) {
			if ((Long[]) allCarIds.toArray(new Long[0]) != null
					|| ((Long[]) allCarIds.toArray(new Long[0])).length != 0) {
				// for (Long carId : (Long[]) allCarIds.toArray(new Long[0])) {
				Ownership ow = new Ownership();
				// Car insertCar = this.carDao.load(carId);
				// ow.setCar(insertCar);
				ow.setAuthor(author);
				ow.setFileDate(fileDate);
				ow.setModifier(modifier);
				ow.setModifiedDate(modifiedDate);
				if (nature != null) {
					ow.setNature(nature);
				}
				if (situation != null) {
					ow.setSituation(situation);
				}
				if (owner != null) {
					ow.setOwner(owner);
				}
				if (description != null) {
					ow.setDescription(description);
				}
				this.ownershipDao.save(ow);
				// }
			}
		}
	}

	public Ownership getOwershipByNumber(String number) {
		return this.ownershipDao.getOwershipByNumber(number);
	}

}
