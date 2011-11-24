package cn.bc.business.spider;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.bc.core.exception.CoreException;
import cn.bc.spider.Spider;

/**
 * 金盾网未处理的交通违法信息的抓取：只能抓取"粤A"牌的车辆
 * 
 * @author rongjih
 * 
 */
public class JinDunSpider4JiaoTongWeiFa implements
		Spider<Map<String, Map<String, Object>>> {
	private static Log logger = LogFactory
			.getLog(JinDunSpider4JiaoTongWeiFa.class);
	private HttpContext httpContext = new BasicHttpContext();
	private HttpClient httpClient = new DefaultHttpClient();
	private String plateNo;
	private String engineNo;

	public String getPlateNo() {
		return plateNo;
	}

	public JinDunSpider4JiaoTongWeiFa setPlateNo(String plateNo) {
		this.plateNo = plateNo;
		return this;
	}

	public String getEngineNo() {
		return engineNo;
	}

	public JinDunSpider4JiaoTongWeiFa setEngineNo(String engineNo) {
		// 只用发动机号的末4位
		this.engineNo = engineNo.substring(engineNo.length() - 4);
		return this;
	}

	public JinDunSpider4JiaoTongWeiFa() {
	}

	public JinDunSpider4JiaoTongWeiFa(String plateNo, String engineNo) {
		// 车牌号码，不含"粤A"字符
		this.plateNo = plateNo;

		// 发动机号
		this.setEngineNo(engineNo);
	}

	public List<Map<String, Object>> excute() {
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();

		// 只要是使用了相同的httpClient，session就是相同的
		// 列：序号、决定书编号、违法时间、违法地点、违法来源、处理状态、是否确认

		// 广州市交通违法信息查询的url
		String url = "http://www.gzjd.gov.cn/gzwfcx/chaxunservlet?ywlx=cxlist";
		url += "&hpzl=02";// 号牌种类：02-小型汽车
		url += "&hphm=" + "A" + this.plateNo;
		url += "&fdjh=" + this.engineNo;
		url += "&jm=156756dfgd75sdfsdf123fasdfsdfsdf" + "A" + this.plateNo
				+ engineNo;

		String html = getRequestHtml(url);
		Document doc = Jsoup.parse(html);
		Elements trs = doc.select("div.listTab>table>tr:gt(0)");
		Map<String, Object> rowData;
		Elements tds;
		int i;
		for (Element tr : trs) {
			rowData = new LinkedHashMap<String, Object>();
			data.add(rowData);
			tds = tr.children();

			i = 0;
			rowData.put("plateNo", this.plateNo);
			rowData.put("num", tds.get(i++).html().trim());
			rowData.put("决定书编号", tds.get(i++).html().trim());
			rowData.put("违法时间", tds.get(i++).html().trim());
			rowData.put("违法地点", tds.get(i++).html().trim());
			rowData.put("违法来源", tds.get(i++).html().trim());
			rowData.put("处理状态", tds.get(i++).html().trim());

			// 格式为：“javascript:getvioDtail("4401131009507673","1111","1111111111111111")”--单号(序号)、决定书类别、决定书编号
			String t = tds.get(i++).child(0).attr("href").trim();
			String[] tt = t.replaceAll("javascript:getvioDtail\\(|\\)|\"", "")
					.split(",");
			rowData.put("单号", tt[0]);
			rowData.put("决定书类别", tt[1]);

			// 获取该条信息的详细信息
			this.getDetail(rowData);
		}
		if (logger.isInfoEnabled())
			logger.info("data=" + data);
		return data;
	}

	private void getDetail(Map<String, Object> rowData) {
		// 广州市交通违法明细的url
		String url = "http://www.gzjd.gov.cn/gzwfcx/chaxunservlet?ywlx=cxdetail";
		url += "&tmpwfsxh=" + rowData.get("单号");
		url += "&tmpwslb=" + rowData.get("决定书类别");
		url += "&hphm=" + "A" + this.plateNo;
		url += "&mm=adfsdfasdfasdfasdfadfadfdfa" + rowData.get("单号") + "A"
				+ this.plateNo;

		String html = getRequestHtml(url);
		Document doc = Jsoup.parse(html);
		Elements trs = doc.select("div.mx_con table>tr");
		if (logger.isDebugEnabled()) {
			int i = 0;
			for (Element tr : trs) {
				System.out.println(i + "--" + tr.html());
				i++;
			}
		}
		rowData.put("违法记分数", this.getSpecalText(trs.get(0).child(2)));

		rowData.put("当事人", trs.get(1).child(1).text().trim());
		rowData.put("罚款金额", this.getSpecalText(trs.get(1).child(2)));

		rowData.put("号码种类", trs.get(2).child(1).text().trim());
		rowData.put("滞纳金", this.getSpecalText(trs.get(2).child(2)));

		rowData.put("交通方式", trs.get(3).child(1).text().trim());

		rowData.put("违法行为", trs.get(5).child(1).text().trim());
	}

	private String getSpecalText(Element td) {
		td.child(0).remove();// 移除<strong>元素
		return td.text();
	}

	private String getRequestHtml(String url) {
		try {
			// HTTP请求
			HttpUriRequest request = new HttpGet(url);

			if (logger.isInfoEnabled()) {
				logger.info("url=" + url);
				logger.info("requestLine=" + request.getRequestLine());
				logger.info("before request.headers:");
				for (HeaderIterator itor = request.headerIterator(); itor
						.hasNext();) {
					logger.info("  " + itor.next());
				}
			}

			// 发送请求，返回响应
			HttpResponse response = httpClient.execute(request, httpContext);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				if (logger.isInfoEnabled()) {
					logger.info("after request.headers:");
					for (HeaderIterator itor = request.headerIterator(); itor
							.hasNext();) {
						logger.info("  " + itor.next());
					}

					logger.info("response.statusCode="
							+ response.getStatusLine().getStatusCode());
					logger.info("response.headers:");
					for (HeaderIterator itor = response.headerIterator(); itor
							.hasNext();) {
						logger.info("  " + itor.next());
					}
				}
				String html = EntityUtils.toString(entity);
				if (logger.isDebugEnabled())
					logger.debug("html=" + html);

				// 保证连接能释放回管理器
				entity.consumeContent();

				return html;
			} else {
				return null;
			}
		} catch (Exception e) {
			throw new CoreException(e);
		}
	}
}
