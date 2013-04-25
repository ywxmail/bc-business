/**
 * 
 */
package cn.bc.business.spider.service;

import org.json.JSONObject;

/**
 * 网络抓取服务接口
 * 
 * @author dragon
 * 
 */
public interface SpiderService {
	String execute(String code, JSONObject config) throws Exception;
}
