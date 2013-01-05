/**
 * 
 */
package cn.bc.business.runcase.service;

import cn.bc.business.runcase.domain.Case4InfractCode;
import cn.bc.core.service.CrudService;

/**
 * 违法代码管理Service
 * 
 * @author zxr
 */
public interface Case4InfractCodeService extends CrudService<Case4InfractCode> {

	/**
	 * 通过违法代码查找违法代码对象
	 * 
	 * @param code违法代码
	 * @return
	 */
	Case4InfractCode getEntityByCode(String code);
}