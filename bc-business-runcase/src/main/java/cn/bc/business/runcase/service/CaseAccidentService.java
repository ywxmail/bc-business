/**
 * 
 */
package cn.bc.business.runcase.service;

import cn.bc.business.car.domain.Car;
import cn.bc.business.carman.domain.CarMan;
import cn.bc.business.runcase.domain.Case4Accident;
import cn.bc.core.service.CrudService;

/**
 * 营运事件事故理赔Service
 * 
 * @author dragon
 */
public interface CaseAccidentService extends CrudService<Case4Accident> {

	/** 根据司机ID查找返回状态为启用中相关辆信息 */
	Car selectAllCarByCarManId(Long id);
	
	/** 根据车辆ID查找返回营运班次为正班的司机信息 */
	CarMan selectCarManByCarId(Long id);
}