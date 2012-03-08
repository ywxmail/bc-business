/**
 * 
 */
package cn.bc.business.invoice.service;

import java.util.List;
import java.util.Map;

import cn.bc.business.invoice.domain.Invoice4Buy;
import cn.bc.core.service.CrudService;

/**
 * 票务采购Service
 * 
 * @author wis
 */
public interface Invoice4BuyService extends CrudService<Invoice4Buy> {

	/**
	 * 获取当前可用的购买单下拉列表信息
	 * 
	 * @return 返回结果中的元素Map格式为：：id -- Invoice4Buy的id,
	 * name -- Invoice4Buy的code(startNo-endNo),如:XXXX(10001~20000)
	 */
	List<Map<String, String>> findEnabled4Option();

}