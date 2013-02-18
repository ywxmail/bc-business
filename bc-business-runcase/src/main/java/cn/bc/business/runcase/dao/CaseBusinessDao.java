/**
 * 
 */
package cn.bc.business.runcase.dao;

import java.util.Map;

import cn.bc.business.runcase.domain.Case4InfractBusiness;
import cn.bc.core.dao.CrudDao;


/**
 * 营运事件营运违章Dao
 * 
 * @author dragon
 */
public interface CaseBusinessDao extends CrudDao<Case4InfractBusiness> {
	
	/**
	 * 
	 * 
	 * @param id
	 *            信息ID
	 * @param attributes
	 *            更新的信息
	 */
	void update4Flow(Long id, Map<String, Object> attributes);

}