/**
 * 
 */
package cn.bc.business.runcase.dao;

import cn.bc.business.runcase.domain.Case4InfractCode;
import cn.bc.core.dao.CrudDao;

/**
 * 违法代码管理Dao
 * 
 * @author zxr
 */
public interface Case4InfractCodeDao extends CrudDao<Case4InfractCode> {

	/**
	 * 通过违法代码查找违法代码对象
	 * 
	 * @param code违法代码
	 * @return
	 */
	Case4InfractCode getEntityByCode(String code);
}