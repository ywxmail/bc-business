/**
 * 
 */
package cn.bc.business.carman.dao;

import java.io.Serializable;

import cn.bc.business.car.domain.Car;
import cn.bc.business.carman.domain.CarByDriver;
import cn.bc.core.dao.CrudDao;


/**
 * 司机营运车辆Dao
 * 
 * @author dragon
 */
public interface CarByDriverDao extends CrudDao<CarByDriver> {

	
	//根据司机ID查询营运车辆信息
	Car findBycarManId(Long id);
}