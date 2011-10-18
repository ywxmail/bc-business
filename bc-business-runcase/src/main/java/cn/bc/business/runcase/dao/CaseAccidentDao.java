/**
 * 
 */
package cn.bc.business.runcase.dao;

import cn.bc.business.carman.domain.CarMan;
import cn.bc.business.runcase.domain.Case4Accident;
import cn.bc.core.dao.CrudDao;

/**
 * 营运事件事故理赔Dao
 * 
 * @author dragon
 */
public interface CaseAccidentDao extends CrudDao<Case4Accident> {

	/** 理赔模块里通过carId找司机 */
	CarMan findcarManBycarcarId(Long id);

}