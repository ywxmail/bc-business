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
	 * 获取当前可用的采购单下拉列表信息
	 * 
	 * @return 返回结果中的元素Map格式为：：id -- Invoice4Buy的id,
	 * name -- Invoice4Buy的code(startNo-endNo),如:XXXX(10001~20000)
	 */
	List<Map<String, String>> findEnabled4Option();
	
	/**
	 * 根据公司，获取当前可用的采购单下拉列表信息
	 * 
	 * @param company 公司，例如宝城 、广发
	 * @return
	 */
	List<Map<String, String>> findEnabled4Option(String company);
	
	/**
	 * 获取当前可用的退票单下拉列表信息
	 * 
	 * @return 返回结果中的元素Map格式为：：id -- Invoice4Buy的id,
	 * name -- Invoice4Buy的code(startNo-endNo),如:XXXX(10001~20000)
	 */
	List<Map<String, String>> findRefundEnabled4Option();
	
	/**
	 * 获取一个拼装为字符串的采购单信息
	 * @param id
	 * @return value--code(startNo-endNo)company,buy_date,type,balanceCount,
	 * 		如：XXXX(10001~20000)宝城，2011-01-01，打印票，剩余数量：100卷
	 */
	List<Map<String, String>> findOneInvoice4Buy(Long id); 

	/**
	 * 通过发票代码查找对应可用的采购单列表
	 * @param code
	 * @return
	 */
	List<Invoice4Buy> selectInvoice4BuyByCode(String code);
	
	/**
	 * 通过发票代码查找对应可用的采购单列表(不包含自己本身)
	 * @param code
	 * @param id
	 * @return
	 */
	List<Invoice4Buy> selectInvoice4BuyByCode(String code,Long id);
	
	/**
	 * 获取采购单中公司列表
	 * 
	 * @return
	 */
	public List<Map<String, String>> findCompany4Option();
	
	/**
	 * 通过采购单ID，获取指定的销售明细开始结束号
	 * 
	 * @return
	 */
	public List<Map<String, String>> findSellDetail(Long id);
	
	
	/**
	 * 通过采购单ID，获取剩余数量
	 * 
	 * @return
	 */
	public List<String> findBalanceCountByInvoice4BuyId(Long id);
	
	/**
	 * 获取采购单剩余号码段
	 * @param id 采购单id
	 * @return 集合同包含采购单的范围
	 *     Map集合{
	 *     	sNo: 号码段开始号,
	 *     	eNo: 号码段结束号
	 *     }
	 */
	public List<Map<String,String>> findBalanceNumber(Long id);
	
	/**
	 * 获取采购单剩余号码段 ,不包括销售单
	 * @param id 采购单id,sellId 销售单Id
	 * @return 集合同包含采购单的范围
	 *     Map集合{
	 *     	sNo: 号码段开始号,
	 *     	eNo: 号码段结束号
	 *     }
	 */
	public List<Map<String,String>> findBalanceNumberExSell(Long id,Long sellId);
	
	/**
	 * 获取采购单剩余号码段,不报含退票单
	 * @param id 采购单id,refundId 退票单Id
	 * @return 集合同包含采购单的范围
	 *     Map集合{
	 *     	sNo: 号码段开始号,
	 *     	eNo: 号码段结束号
	 *     }
	 */
	public List<Map<String,String>> findBalanceNumberExRefund(Long id,Long refundId);
	
	/**
	 * 判断此采购单时候是否有对应的销售单或退票单
	 * 
	 * @param id 采购单id
	 * @return
	 */
	public boolean isExistSellAndRefund(Long id);

}