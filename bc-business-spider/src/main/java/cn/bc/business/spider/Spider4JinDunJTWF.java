package cn.bc.business.spider;

import java.util.Calendar;
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
import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.business.car.service.CarService;
import cn.bc.business.spider.domain.JinDunJTWF;
import cn.bc.core.exception.CoreException;
import cn.bc.core.util.DateUtils;
import cn.bc.identity.domain.ActorHistory;
import cn.bc.web.spider.Spider;

/**
 * 金盾网未处理的交通违法信息的抓取：只能抓取"粤A"牌的车辆
 * 
 * @author rongjih
 * 
 */
public class Spider4JinDunJTWF implements Spider<JinDunJTWF> {
	private static Log logger = LogFactory.getLog(Spider4JinDunJTWF.class);
	private HttpContext httpContext = new BasicHttpContext();
	private HttpClient httpClient = new DefaultHttpClient();
	private String carType;// 号牌种类：02-小型汽车
	private String carPlateType;// 车牌归属，如“粤A”
	private String carPlateNo;// 车牌号码，如“C4X74”
	private String engineNo;// 发动机号
	private ActorHistory syncer;// 执行同步操作的用户
	private CarService carService;
	public Map<String, Object> carInfoMap;// 车辆信息map

	private String getCarPlateInner() {
		// 去除首个中文字符,如"粤"
		return carPlateType.substring(1) + carPlateNo;
	}

	private String getEngineNoInner() {
		// 发动机号的末4位
		return engineNo.substring(engineNo.length() - 4);
	}

	public void setCarPlateType(String carPlateType) {
		this.carPlateType = carPlateType;
	}

	public void setCarPlateNo(String carPlateNo) {
		this.carPlateNo = carPlateNo;
	}

	public void setEngineNo(String engineNo) {
		this.engineNo = engineNo;
	}

	public void setCarType(String carType) {
		this.carType = carType;
	}

	public void setSyncer(ActorHistory syncer) {
		this.syncer = syncer;
	}
	
	@Autowired
	public void setCarService(CarService carService) {
		this.carService = carService;
	}

	public Spider4JinDunJTWF() {
	}

	public Spider4JinDunJTWF(ActorHistory syncer, String carType,
			String carPlateType, String carPlateNo, String engineNo) {
		this.carType = carType;
		this.carPlateType = carPlateType;
		this.carPlateNo = carPlateNo;
		this.engineNo = engineNo;
		this.syncer = syncer;
	}

//	public List<JinDunJTWF> excute() {
//		List<JinDunJTWF> list = new ArrayList<JinDunJTWF>();
//
//		// 只要是使用了相同的httpClient，session就是相同的
//		// 列：序号、决定书编号、违法时间、违法地点、违法来源、处理状态、是否确认
//
//		// 广州市交通违法信息查询的url
//		String url = "http://www.gzjd.gov.cn/gzwfcx/chaxunservlet?ywlx=cxlist";
//		String url4detail = "http://www.gzjd.gov.cn/gzwfcx/chaxunservlet?ywlx=cxdetail";
//		url += "&hpzl=" + this.carType;
//		String plateInner = this.getCarPlateInner();
//		String engineNoInner = this.getEngineNoInner();
//		url += "&hphm=" + plateInner;
//		url += "&fdjh=" + engineNoInner;
//		url += "&jm=156756dfgd75sdfsdf123fasdfsdfsdf" + plateInner
//				+ engineNoInner;
//
//		String html = getRequestHtml(url);
//		Document doc = Jsoup.parse(html);
//		Elements trs = doc.select("div.listTab>table>tr:gt(0)");
//		JinDunJTWF jtwf;
//		Elements tds;
//		for (Element tr : trs) {
//			jtwf = new JinDunJTWF();
//			jtwf.setAuthor(syncer);
//			jtwf.setSyncDate(Calendar.getInstance());
//			jtwf.setSyncType(JinDunJTWF.class.getSimpleName());
//
//			tds = tr.children();
//			jtwf.setCarType("02");
//			//通过车牌号查找此车辆所属的分公司与车队
//			carInfoMap = this.carService.findcarInfoByCarPlateNo2(this.carPlateNo);
//			if(carInfoMap != null && carInfoMap.get("unit_name") != null){
//				jtwf.setUnitName(carInfoMap.get("unit_name")+""); //设置分公司
//			}
//			if(carInfoMap != null && carInfoMap.get("motorcade_name") != null){
//				jtwf.setMotorcadeName(carInfoMap.get("motorcade_name")+""); //设置所属车队
//			}
//			jtwf.setCarPlateType(this.carPlateType);
//			jtwf.setCarPlateNo(this.carPlateNo);
//			jtwf.setEngineNo(this.engineNo);
//			jtwf.setDecisionNo(tds.get(1).html().trim());// 决定书编号
//			// 违法时间,格式为2011-11-06 22:33
//			jtwf.setHappenDate(DateUtils.getCalendar(tds.get(2).html().trim()));
//			jtwf.setAddress(tds.get(3).html().trim());// 违法地点
//			jtwf.setSource(tds.get(4).html().trim());// 违法来源
//			jtwf.setStatus("已处理".equals(tds.get(5).html().trim()) ? JinDunJTWF.STATUS_DONE
//					: JinDunJTWF.STATUS_NEW);// 处理状态
//
//			// 格式为：“javascript:getvioDtail("4401131009507673","1111","1111111111111111")”--单号(序号)、决定书类别、决定书编号
//			String t = tds.get(6).child(0).attr("href").trim();
//			String[] tt = t.replaceAll("javascript:getvioDtail\\(|\\)|\"", "")
//					.split(",");
//			jtwf.setSyncCode(tt[0]);// 单号
//			jtwf.setDecisionType(tt[1]);// 决定书类别
//
//			// 获取该条信息的详细信息
//			// --广州市交通违法明细的url
//			String url2 = url4detail;
//			url2 += "&tmpwfsxh=" + jtwf.getSyncCode();
//			url2 += "&tmpwslb=" + jtwf.getDecisionType();
//			url2 += "&hphm=" + plateInner;
//			url2 += "&mm=adfsdfasdfasdfasdfadfadfdfa" + jtwf.getSyncCode()
//					+ plateInner;
//			jtwf.setSyncFrom(url2);
//			this.getDetail(url2, plateInner, jtwf);
//
//			list.add(jtwf);
//		}
//		if (logger.isInfoEnabled())
//			logger.info("data=" + list);
//		return list;
//	}
	public JinDunJTWF excute() {
		
		// 只要是使用了相同的httpClient，session就是相同的
		// 列：序号、决定书编号、违法时间、违法地点、违法来源、处理状态、是否确认
		
		// 广州市交通违法信息查询的url
		String url = "http://www.gzjd.gov.cn/gzwfcx/chaxunservlet?ywlx=cxlist";
		String url4detail = "http://www.gzjd.gov.cn/gzwfcx/chaxunservlet?ywlx=cxdetail";
		url += "&hpzl=" + this.carType;
		String plateInner = this.getCarPlateInner();
		String engineNoInner = this.getEngineNoInner();
		url += "&hphm=" + plateInner;
		url += "&fdjh=" + engineNoInner;
		url += "&jm=156756dfgd75sdfsdf123fasdfsdfsdf" + plateInner
				+ engineNoInner;
		
		String html = getRequestHtml(url);
		Document doc = Jsoup.parse(html);
		Elements trs = doc.select("div.listTab>table>tr:gt(0)");
		JinDunJTWF jtwf = null;
		Elements tds;
		for (Element tr : trs) {
			jtwf = new JinDunJTWF();
			jtwf.setAuthor(syncer);
			jtwf.setSyncDate(Calendar.getInstance());
			jtwf.setSyncType(JinDunJTWF.class.getSimpleName());
			
			tds = tr.children();
			jtwf.setCarType("02");
			//通过车牌号查找此车辆所属的分公司与车队
			carInfoMap = this.carService.findcarInfoByCarPlateNo2(this.carPlateNo);
			if(carInfoMap != null && carInfoMap.get("unit_name") != null){
				jtwf.setUnitName(carInfoMap.get("unit_name")+""); //设置分公司
			}
			if(carInfoMap != null && carInfoMap.get("motorcade_name") != null){
				jtwf.setMotorcadeName(carInfoMap.get("motorcade_name")+""); //设置所属车队
			}
			jtwf.setCarPlateType(this.carPlateType);
			jtwf.setCarPlateNo(this.carPlateNo);
			jtwf.setEngineNo(this.engineNo);
			jtwf.setDecisionNo(tds.get(1).html().trim());// 决定书编号
			// 违法时间,格式为2011-11-06 22:33
			jtwf.setHappenDate(DateUtils.getCalendar(tds.get(2).html().trim()));
			jtwf.setAddress(tds.get(3).html().trim());// 违法地点
			jtwf.setSource(tds.get(4).html().trim());// 违法来源
			jtwf.setStatus("已处理".equals(tds.get(5).html().trim()) ? JinDunJTWF.STATUS_DONE
					: JinDunJTWF.STATUS_NEW);// 处理状态
			
			// 格式为：“javascript:getvioDtail("4401131009507673","1111","1111111111111111")”--单号(序号)、决定书类别、决定书编号
			String t = tds.get(6).child(0).attr("href").trim();
			String[] tt = t.replaceAll("javascript:getvioDtail\\(|\\)|\"", "")
					.split(",");
			jtwf.setSyncCode(tt[0]);// 单号
			jtwf.setDecisionType(tt[1]);// 决定书类别
			
			// 获取该条信息的详细信息
			// --广州市交通违法明细的url
			String url2 = url4detail;
			url2 += "&tmpwfsxh=" + jtwf.getSyncCode();
			url2 += "&tmpwslb=" + jtwf.getDecisionType();
			url2 += "&hphm=" + plateInner;
			url2 += "&mm=adfsdfasdfasdfasdfadfadfdfa" + jtwf.getSyncCode()
					+ plateInner;
			jtwf.setSyncFrom(url2);
			this.getDetail(url2, plateInner, jtwf);
			
		}
		if (logger.isInfoEnabled())
			logger.info("data=" + jtwf);
		return jtwf;
	}

	private void getDetail(String url, String plate, JinDunJTWF jtwf) {
		String html = getRequestHtml(url);
		Document doc = Jsoup.parse(html);
		Elements trs = doc.select("div.mx_con table>tr");
		if (logger.isDebugEnabled()) {
			int i = 0;
			for (Element tr : trs) {
				logger.debug("tr" + i + ".html=" + tr.html());
				i++;
			}
		}
		jtwf.setJeom(toFloat(this.getSpecalText(trs.get(0).child(2))));// 违法记分数
		jtwf.setDriverName(trs.get(1).child(1).text().trim());// 当事人
		jtwf.setPenalty(toFloat(this.getSpecalText(trs.get(1).child(2))));// 罚款金额
		jtwf.setCarTypeDesc(trs.get(2).child(1).text().trim()); // 号码种类
		jtwf.setOverduePayment(toFloat(this.getSpecalText(trs.get(2).child(2))));// 滞纳金
		jtwf.setTraffic(trs.get(3).child(1).text().trim());// 交通方式
		jtwf.setBreakType(trs.get(5).child(1).text().trim());// 违法行为
	}

	private Float toFloat(String value) {
		if (value == null || value.length() == 0) {
			return new Float(0);
		} else {
			return new Float(value);
		}
	}

	private String getSpecalText(Element td) {
		td.child(0).remove();// 移除<strong>元素
		return td.text().trim();
	}

	private String getRequestHtml(String url) {
		try {
			// HTTP请求
			HttpUriRequest request = new HttpGet(url);

			logger.info("url=" + url);
			if (logger.isDebugEnabled()) {
				logger.debug("requestLine=" + request.getRequestLine());
				logger.debug("before request.headers:");
				for (HeaderIterator itor = request.headerIterator(); itor
						.hasNext();) {
					logger.debug("  " + itor.next());
				}
			}

			// 发送请求，返回响应
			HttpResponse response = httpClient.execute(request, httpContext);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				if (logger.isDebugEnabled()) {
					logger.debug("after request.headers:");
					for (HeaderIterator itor = request.headerIterator(); itor
							.hasNext();) {
						logger.debug("  " + itor.next());
					}

					logger.debug("response.statusCode="
							+ response.getStatusLine().getStatusCode());
					logger.debug("response.headers:");
					for (HeaderIterator itor = response.headerIterator(); itor
							.hasNext();) {
						logger.debug("  " + itor.next());
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
