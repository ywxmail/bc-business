package cn.bc.business.spider.service;

import java.util.List;
import java.util.Map;

/**
 * 外网爬虫器
 * 
 * @author rongjih
 * 
 */
public interface BsSpiderService {
	/**
	 * 爬去金盾网上未处理的交通违法信息
	 * 
	 * @param carIds
	 *            车辆id，多个id间用逗号连接，为空代表查询所有在案车辆
	 * @return Map的key为车辆的id
	 */
	Map<String, List<Map<String, Object>>> findJinDunJiaoTongWeiZhang(String carIds);
}
