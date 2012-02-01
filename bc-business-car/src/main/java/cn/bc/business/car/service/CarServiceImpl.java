/**
 * 
 */
package cn.bc.business.car.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.bc.business.car.dao.CarDao;
import cn.bc.business.car.domain.Car;
import cn.bc.core.Page;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.service.DefaultCrudService;

/**
 * 车辆Service的实现
 * 
 * @author dragon
 */
public class CarServiceImpl extends DefaultCrudService<Car> implements
		CarService {
	protected final Log logger = LogFactory.getLog(getClass());
	private CarDao carDao;

	public void setCarDao(CarDao carDao) {
		this.carDao = carDao;
		this.setCrudDao(carDao);
	}

	@Override
	public void delete(Serializable id) {
		// 删除车量
		this.carDao.delete(id);
	}

	@Override
	public void delete(Serializable[] ids) {
		// 批量删除车辆
		this.carDao.delete(ids);
	}

	/**
	 * 查找汽车列表
	 * 
	 * @parma condition
	 * @return
	 */
	public List<Map<String, Object>> list(Condition condition) {
		return this.carDao.list(condition);
	}

	/**
	 * 查找汽车分页
	 * 
	 * @parma condition
	 * @parma condition
	 * @return
	 */
	public Page<Map<String, Object>> page(Condition condition, int pageNo,
			int pageSize) {
		return this.carDao.page(condition, pageNo, pageSize);
	}
	
	/**
	 * 根据司机ID查找返回状态为启用中相关辆信息
	 * 
	 * @parma id
	 * @return
	 */
	public List<Car> selectAllCarByCarManId(Long id) {
		// TODO Auto-generated method stub
		return  (this.carDao.findAllcarBycarManId(id));
	}

	/**
	 * 根据车牌号查找车牌号
	 * @parma carPlateNo 
	 * @return Long
	 */
	public Long findcarInfoByCarPlateNo(String carPlateNo) {
		Long carId = 0L;
		if(carPlateNo.length() > 0){
			carId = this.carDao.findcarInfoByCarPlateNo(carPlateNo);
		}
		return carId;
	}

	public Car findcarOriginNoByCode(String code) {
		return this.carDao.findcarOriginNoByCode(code);
	}

	public Map<String, Object> findcarInfoByCarPlateNo2(String carPlateNo) {
		return this.carDao.findcarInfoByCarPlateNo2(carPlateNo);
	}
}