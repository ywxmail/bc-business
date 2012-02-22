/**
 * 
 */
package cn.bc.business.carlpg.service;

import java.util.List;
import java.util.Map;

import cn.bc.business.carlpg.domain.CarLPG;
import cn.bc.core.service.CrudService;


/**
 * LPG配置Service
 * 
 * @author lbj
 */
public interface CarLPGService extends CrudService<CarLPG> {

	/**
	 * 获取当前可用的LPG配置下拉列表信息
	 * 
	 * @return 返回结果中的元素Map格式为：：id -- CarLPG的id,name -- CarLPG的name
	 */
	List<Map<String, String>> findEnabled4Option();

	/**
	 * 通过name查找LPG配置
	 * @param name
	 * @return
	 */
	CarLPG findcarLPGByLPGModel(String name);
}