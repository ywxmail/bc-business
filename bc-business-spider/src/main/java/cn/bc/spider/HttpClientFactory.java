/**
 * 
 */
package cn.bc.spider;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * @author dragon
 * 
 */
public class HttpClientFactory {
	private static Map<String, HttpClient> cache = new HashMap<String, HttpClient>();
	public static Map<String, String> userAgents = new HashMap<String, String>();

	static {
		// 可用的user-agent列表
		userAgents
				.put("Win7Chrome26",
						"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.64 Safari/537.31");
		userAgents
				.put("Win7IE10",
						"Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; WOW64; Trident/6.0)");
		userAgents
				.put("Win7IE9",
						"Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Win64; x64; Trident/5.0)");
		userAgents
				.put("Win7IE8",
						"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; WOW64; Trident/4.0)");
	}

	private HttpClientFactory() {
	}

	/**
	 * 初始化一个全新的默认的HttpClient实例
	 * 
	 * @return
	 */
	public static HttpClient create() {
		HttpClient httpClient = new DefaultHttpClient();
		httpClient.getParams().setParameter(HttpMethodParams.USER_AGENT,
				userAgents.get("Win7IE9"));
		return httpClient;
	}

	/**
	 * 获取指定标识的一个HttpClient实例，如果没有就创建一个新的并缓存起来
	 * 
	 * @param id
	 * @return
	 */
	public static HttpClient get(String id) {
		if (cache.containsKey(id)) {
			return cache.get(id);
		} else {
			HttpClient httpClient = create();
			cache.put(id, httpClient);
			return httpClient;
		}
	}

	/**
	 * 移除对指定标识的HttpClient实例的缓存
	 * 
	 * @param id
	 * @return
	 */
	public static HttpClient remove(String id) {
		if (id == null)
			return null;

		if (cache.containsKey(id)) {
			return cache.remove(id);
		} else {
			return null;
		}
	}

	public static int size() {
		return cache.size();
	}

}
