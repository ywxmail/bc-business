/**
 * 
 */
package cn.bc.business.spider.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import cn.bc.core.util.DateUtils;

/**
 * 网络抓取服务接口的默认实现
 * 
 * @author dragon
 * 
 */
public class SpiderServiceImpl implements SpiderService {
	static Map<String, Object> spiders = new HashMap<String, Object>();
	static Map<String, Map<String, Object>> process = new HashMap<String, Map<String, Object>>();
	static {
		spiders.put("car1", "");
	}

	public String execute(String code, JSONObject config) throws Exception {
		// 生成唯一批号
		String uid = config.has("uid") ? config.getString("uid") : null;
		if (uid == null) {
			uid = DateUtils.formatDateTime(new Date(), "yyyyMMddHHmmssSSSS");
			config.put("uid", uid);
		}
		return uid;
	}
}
