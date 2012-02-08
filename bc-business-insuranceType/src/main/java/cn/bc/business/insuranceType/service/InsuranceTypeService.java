package cn.bc.business.insuranceType.service;

import java.util.List;
import java.util.Map;

import cn.bc.business.insuranceType.domain.InsuranceType;
import cn.bc.core.service.CrudService;

/**
 * 车辆保单险种Service
 * @author zxr
 *
 */
public interface InsuranceTypeService extends CrudService<InsuranceType> {
		
	/**
	 * 获取险种表中所有的模板
	 * 
	 * @return InsuranceType ID,名称
	 */
	public List<Map<String,String>> findEnabled4Option();
	
	/**
	 * 根据模板 获取险种
	 * 
	 * @param pid 模板ID
	 * @return
	 */
	public List<InsuranceType> findTemplateWithPlant(Long pid);
}
