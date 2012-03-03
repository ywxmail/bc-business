package cn.bc.business.httpclient4;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

/**
 * @author dragon
 * 
 */
public class NetUtils {
	private static final Log logger = LogFactory.getLog(NetUtils.class);
	private static final Map<String, String> userAgents;
	static {
		userAgents = new HashMap<String, String>();
		userAgents
				.put("chrome8",
						"Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US) AppleWebKit/534.10 (KHTML, like Gecko) Chrome/8.0.552.224 Safari/534.10");
		userAgents.put("ie6",
				"Mozilla/4.0 (compatible; MSIE 6.0; Windows 2000)");
		userAgents
				.put("firefox3",
						"Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2.13) Gecko/20101203 Firefox/3.6.13");
		userAgents.put("httpClient4.0", "Apache-HttpClient/4.0.3 (java 1.5)");

	}

	public static String getTestUrl() {
		return "http://127.0.0.1:8081/qcdebug/debug.do";
	}

	public static String getDefaultUserAgent() {
		return userAgents.get("ie6");
	}

	public static String getUserAgent(String key) {
		return userAgents.get(key);
	}

	public static Map<String, String> getDefaultHeaders() {
		Map<String, String> headers = new HashMap<String, String>();
		headers.put(
				"Accept",
				"application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
		headers.put("Accept-Charset", "GBK,utf-8;q=0.7,*;q=0.3");
		headers.put("Accept-Encoding", "gzip,deflate,sdch");
		headers.put("Accept-Language", "zh-CN,zh;q=0.8");
		headers.put("Cache-Control", "max-age=0");
		// headers.put("Connection", "keep-alive");
		// headers("Host", "rongjih.blog.163.com");
		// headers.put("Referer","http://rongjih.blog.163.com/blog/static/335744612010112041023772/");

		return headers;
	}

	/**
	 * 获取本机的计算机名称
	 * 
	 * @return
	 */
	public static String getLocalHostName() {
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * 获取本机的首选ip地址
	 * 
	 * @return
	 */
	public static String getLocalHostIP() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * 获取本机配置的所有ip
	 * 
	 * @return
	 */
	public static InetAddress[] getAllLocalAddress() {
		InetAddress[] localAddresses = null;
		try {
			localAddresses = InetAddress.getAllByName(InetAddress
					.getLocalHost().getHostName());
		} catch (UnknownHostException e) {
			logger.error(e.getMessage(), e);
		}
		return localAddresses;
	}

	/**
	 * 获取本机配置的所有连接
	 * 
	 * @return
	 */
	public static List<NetworkInterface> getAllNetworkInterface() {
		List<NetworkInterface> list = new ArrayList<NetworkInterface>();
		try {
			Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface
					.getNetworkInterfaces();
			while (allNetInterfaces.hasMoreElements()) {
				list.add(allNetInterfaces.nextElement());
			}
		} catch (SocketException e) {
			logger.error(e.getMessage(), e);
		}
		return list;
	}

	/**
	 * 获取本机的外网IP
	 * 
	 * @return
	 * @throws IOException
	 */
	public static String getOuterNetIP() {
		String url = "http://www.ip138.com/ip2city.asp";
		String selector = "body>center";
		Connection con = Jsoup.connect(url);
		String ip = null;
		try {
			ip = con.get().select(selector).html();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		if (ip == null)
			return null;
		return ip.trim().replace("您的IP地址是：[", "").replace("]", "")
				.replaceAll("\r|\n", "");
	}

	public static int getInterval(int interval, String unit) {
		if (unit == null || unit.length() == 0)
			return interval < 1 ? 100 : interval;// 最小100豪秒

		if (interval < 1)
			interval = 100;
		if ("m".equalsIgnoreCase(unit) || "分钟".equalsIgnoreCase(unit)) {// 分钟
			return interval * 60 * 1000;
		} else if ("h".equalsIgnoreCase(unit) || "小时".equalsIgnoreCase(unit)) {// 小时
			return interval * 60 * 60 * 1000;
		} else if ("ms".equalsIgnoreCase(unit) || "毫秒".equalsIgnoreCase(unit)) {// 毫秒
			return interval;
		} else if ("s".equalsIgnoreCase(unit) || "秒".equalsIgnoreCase(unit)) {// 秒
			return interval * 1000;
		} else {// 默认秒
			return interval * 1000;
		}
	}
}
