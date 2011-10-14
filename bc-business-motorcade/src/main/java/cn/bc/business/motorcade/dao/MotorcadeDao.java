package cn.bc.business.motorcade.dao;

import java.util.List;

import cn.bc.business.motorcade.domain.Motorcade;
import cn.bc.core.dao.CrudDao;

public interface MotorcadeDao extends CrudDao<Motorcade> {
	/**
	 * 获取当前可用的车队
	 * 
	 * @return
	 */
	List<Motorcade> findActive();
}
