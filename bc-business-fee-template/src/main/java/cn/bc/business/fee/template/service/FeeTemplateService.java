/**
 * 
 */
package cn.bc.business.fee.template.service;

import java.util.List;
import java.util.Map;

import cn.bc.business.fee.template.domain.FeeTemplate;
import cn.bc.core.service.CrudService;

/**
 * 合同Service
 * 
 * @author dragon
 */
public interface FeeTemplateService extends CrudService<FeeTemplate> {

	/**
	 * 获取模板
	 * 
	 * @return
	 */
	public List<Map<String, String>> getTemplate();
	
	/**
	 * 检测费用是否属于模板
	 * 
	 * @param pid
	 * @return true-有，false-没
	 */
	public boolean isFeeBelong2Template(Long pid);
	
	/**
	 * 检测模板是否存在费用
	 * 
	 * @param pid
	 * @return true-有，false-没
	 */
	public boolean isTemplateExistFee(Long id);
	

	/**
	 * 获取属于此模板的费用集合
	 * 
	 * @param pid
	 * @return
	 */
	public List<Map<String, String>> getFeeBelong2Template(Long pid);
}