/**
 * 
 */
package cn.bc.business.carmodel.service;

import java.util.List;
import java.util.Map;

import cn.bc.business.carmodel.domain.CarModel;
import cn.bc.core.service.CrudService;


/**
 * 车型配置Service
 * 
 * @author wis
 */
public interface CarModelService extends CrudService<CarModel> {

	/**
	 * 获取当前可用的车型配置下拉列表信息
	 * 
	 * @return 返回结果中的元素Map格式为：：id -- CarModel的id,name -- CarModel的factoryModel
	 */
	List<Map<String, String>> findEnabled4Option();

	/**
	 * 通过factoryModel查找车型配置
	 * @param factoryModel
	 * @return
	 */
	CarModel findcarModelByFactoryModel(String factoryModel);

}