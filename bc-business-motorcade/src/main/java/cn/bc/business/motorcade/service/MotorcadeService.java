package cn.bc.business.motorcade.service;

import java.util.List;

import cn.bc.business.motorcade.domain.Motorcade;
import cn.bc.core.service.CrudService;


public interface MotorcadeService extends CrudService<Motorcade>{
	/**
	 * 获取当前可用的车队
	 * @return
	 */
	List<Motorcade> findActive();
}
