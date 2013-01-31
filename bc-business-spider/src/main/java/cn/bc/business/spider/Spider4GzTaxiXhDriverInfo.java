package cn.bc.business.spider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.tools.ant.util.DateUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.util.FileCopyUtils;

import cn.bc.business.spider.domain.GzTaxiXhDriverInfo;
import cn.bc.web.spider.Spider;

/**
 * 从广州市出租汽车协会网抓取驾驶员信誉档案
 * 
 * @author dragon
 * 
 */
public class Spider4GzTaxiXhDriverInfo implements Spider<GzTaxiXhDriverInfo> {
	private static Log logger = LogFactory
			.getLog(Spider4GzTaxiXhDriverInfo.class);
	private final static String KEY_VIEWSTATE = "__VIEWSTATE";
	private final static String KEY_EVENTVALIDATION = "__EVENTVALIDATION";
	private HttpClient httpClient = new DefaultHttpClient();
	private String type;// 查询类型：服务资格证、从业人员资格证、姓名
	private String value;// 查询的值，如当type为服务资格证是对应该资格证的号码
	private String rootPath;// 司机图片保存到的父目录
	private String userName;// 登录协会的帐号
	private String userPassword;// 登录协会的密码
	private int index;// 简易列表信息含有多行时默认详细查询对应哪行的，第一条的数据对应值0

	public Spider4GzTaxiXhDriverInfo() {
	}

	public Spider4GzTaxiXhDriverInfo(String type, String value,
			String userName, String userPassword, String rootPath, int index) {
		this.type = type;
		this.value = value;
		this.userName = userName;
		this.userPassword = userPassword;
		this.rootPath = rootPath;
		this.index = index;
	}

	public GzTaxiXhDriverInfo excute() throws Exception {
		GzTaxiXhDriverInfo info = new GzTaxiXhDriverInfo();
		HttpUriRequest request4get;
		HttpPost request4post;
		HttpResponse response;
		List<NameValuePair> formParams;
		HttpEntity entity;
		String loginUrl = "http://www.gztaxixh.com/login.aspx";
		String infoUrl = "http://www.gztaxixh.com/peccancySearch.aspx";
		String viewState;
		String eventValidation;
		String html;
		Document doc;
		Elements table;

		// 获取登录页面
		if (logger.isInfoEnabled()) {
			logger.info("获取登录页面:url=" + loginUrl);
		}
		request4get = new HttpGet(loginUrl);
		response = httpClient.execute(request4get);
		html = EntityUtils.toString(response.getEntity());
		if (logger.isDebugEnabled()) {
			logger.debug("获取登录页面:html=" + html);
		}
		doc = parseHtml(html);
		viewState = doc.select("#" + KEY_VIEWSTATE).val();
		eventValidation = doc.select("#" + KEY_EVENTVALIDATION).val();
		if (logger.isInfoEnabled()) {
			logger.info("viewState=" + viewState);
			logger.info("eventValidation=" + eventValidation);
		}

		// 执行登录处理
		if (logger.isInfoEnabled()) {
			logger.info("登录网站:url=" + loginUrl + ",userName=" + this.userName
					+ ",password=" + this.userPassword);
		}
		formParams = new ArrayList<NameValuePair>();
		formParams.add(new BasicNameValuePair(KEY_VIEWSTATE, viewState));
		formParams.add(new BasicNameValuePair(KEY_EVENTVALIDATION,
				eventValidation));
		formParams.add(new BasicNameValuePair("TextBox1", this.userName));
		formParams.add(new BasicNameValuePair("TextBox2", this.userPassword));
		formParams.add(new BasicNameValuePair("Button1", "确定"));
		entity = new UrlEncodedFormEntity(formParams, "GBK");
		request4post = new HttpPost(loginUrl);
		request4post.setEntity(entity);
		response = httpClient.execute(request4post);
		html = EntityUtils.toString(response.getEntity());
		if (logger.isDebugEnabled()) {
			logger.debug("登录结果:html=" + html);
		}
		doc = parseHtml(html);
		boolean loginSuccess = "已登陆".equals(doc.select("#_ctl0_L1").text());
		if (!loginSuccess) {
			info.setMsg("使用帐号“" + this.userName
					+ "”登录广州市出租汽车协会网失败！如果帐号密码有变动，请联系管理员到选项管理中作相应修改。");
			logger.info(info.getMsg());
			return info;
		} else {
			logger.info("登录成功");
		}

		// 获取“出租车驾驶员信誉档案”页面
		if (logger.isInfoEnabled()) {
			logger.info("获取“驾驶员信誉档案”页面:url=" + infoUrl);
		}
		request4get = new HttpGet(infoUrl);
		response = httpClient.execute(request4get);
		html = EntityUtils.toString(response.getEntity());
		if (logger.isDebugEnabled()) {
			logger.debug("获取“驾驶员信誉档案”页面:html=" + html);
		}
		doc = parseHtml(html);
		viewState = doc.select("#" + KEY_VIEWSTATE).val();
		eventValidation = doc.select("#" + KEY_EVENTVALIDATION).val();
		if (logger.isInfoEnabled()) {
			logger.info("viewState=" + viewState);
			logger.info("eventValidation=" + eventValidation);
		}

		// 获取简单信息页面
		if (logger.isInfoEnabled()) {
			logger.info("获取简单信息:url=" + infoUrl);
		}
		formParams = new ArrayList<NameValuePair>();
		formParams.add(new BasicNameValuePair(KEY_VIEWSTATE, viewState));
		formParams.add(new BasicNameValuePair(KEY_EVENTVALIDATION,
				eventValidation));
		formParams.add(new BasicNameValuePair("_ctl0:TextBox1", ""));
		formParams.add(new BasicNameValuePair("txtEnterValue", ""));
		formParams.add(new BasicNameValuePair(
				"_ctl0:ContentPlaceHolder1:ddlSearchBy", this.type));
		formParams.add(new BasicNameValuePair(
				"_ctl0:ContentPlaceHolder1:txtFilter", this.value));
		formParams.add(new BasicNameValuePair(
				"_ctl0:ContentPlaceHolder1:btnSubmit", "确认"));
		entity = new UrlEncodedFormEntity(formParams, "GBK");
		request4post = new HttpPost(infoUrl);
		request4post.setEntity(entity);
		response = httpClient.execute(request4post);
		html = EntityUtils.toString(response.getEntity());
		if (logger.isDebugEnabled()) {
			logger.debug("获取简单信息:html=" + html);
		}
		doc = parseHtml(html);
		viewState = doc.select("#" + KEY_VIEWSTATE).val();
		eventValidation = doc.select("#" + KEY_EVENTVALIDATION).val();
		if (logger.isInfoEnabled()) {
			logger.info("viewState=" + viewState);
			logger.info("eventValidation=" + eventValidation);
		}
		table = doc.select("#_ctl0_ContentPlaceHolder1_dgCardList");
		if (table.size() == 0) {
			info.setMsg("没有找到相关信息！");
			return info;
		}

		Elements inputs = table.select("input");
		if (inputs.size() > 1) {// ”查看详情“图片替换
			Iterator<Element> itor = inputs.iterator();
			int j = 0;
			Element td;
			while (itor.hasNext()) {
				td = itor.next().parent();
				if (j == index) {
					td.parent().addClass("active");
				}
				td.html("<span class='ui-icon ui-icon-search' data-index='"
						+ (j++) + "'></span>");
			}
		} else {// 删除”查看详情“图片
			inputs.remove();
		}

		info.setSimple(table.removeAttr("width").outerHtml());
		if (logger.isDebugEnabled()) {
			logger.debug("simple=" + info.getSimple());
		}

		// 获取详细信息页面
		if (logger.isInfoEnabled()) {
			logger.info("获取详细信息:url=" + infoUrl);
		}
		formParams = new ArrayList<NameValuePair>();
		formParams.add(new BasicNameValuePair(KEY_VIEWSTATE, viewState));
		formParams.add(new BasicNameValuePair(KEY_EVENTVALIDATION,
				eventValidation));
		formParams.add(new BasicNameValuePair("_ctl0:TextBox1", ""));
		formParams.add(new BasicNameValuePair("txtEnterValue", ""));
		formParams.add(new BasicNameValuePair(
				"_ctl0:ContentPlaceHolder1:ddlSearchBy", this.type));
		formParams.add(new BasicNameValuePair(
				"_ctl0:ContentPlaceHolder1:txtFilter", this.value));
		formParams.add(new BasicNameValuePair(
				"_ctl0:ContentPlaceHolder1:dgCardList:_ctl" + (index + 2)
						+ ":ibtnView.x", "8"));
		formParams.add(new BasicNameValuePair(
				"_ctl0:ContentPlaceHolder1:dgCardList:_ctl" + (index + 2)
						+ ":ibtnView.y", "8"));
		entity = new UrlEncodedFormEntity(formParams, "GBK");
		request4post = new HttpPost(infoUrl);
		request4post.setEntity(entity);
		response = httpClient.execute(request4post);
		html = EntityUtils.toString(response.getEntity());
		if (logger.isDebugEnabled()) {
			logger.debug("获取详细信息:html=" + html);
		}
		doc = parseHtml(html);
		table = doc.select("#Table3 table.ListTable");
		if (logger.isDebugEnabled()) {
			logger.debug("detail=" + table.outerHtml());
		}
		if (table.size() == 0) {
			info.setMsg("查看详情失败！");
			return info;
		} else {
			table.removeAttr("width");
			table.select("tr > td:eq(0)").attr("style",
					"text-align:right;padding-right:8px;");

			// 获取司机图片
			String picUrl = "http://www.gztaxixh.com/"
					+ table.select("img#_ctl0_ContentPlaceHolder1_imgPic")
							.attr("src");
			int i = picUrl.indexOf("=");
			String id;
			if (i != -1)
				id = picUrl.substring(i + 1);
			else {
				id = DateUtils.format(new Date(), "yyyyMMddHHmmssSSSS");
			}
			table.select("img").attr("src",
					"bc/attach/file?path=spider/gztaxixh/" + id + ".jpg");
			info.setDetail(table.outerHtml());
			if (logger.isInfoEnabled()) {
				logger.info("获取司机图片:url=" + picUrl);
			}
			request4get = new HttpGet(picUrl);
			response = httpClient.execute(request4get);
			entity = response.getEntity();
			if (entity.isStreaming()) {
				String toFile = rootPath + "/spider/gztaxixh";
				File storeFile = new File(toFile);
				if (!storeFile.exists()) {
					if (logger.isFatalEnabled()) {
						logger.fatal("mkdir=" + storeFile);
					}
					storeFile.mkdirs();
				}
				storeFile = new File(toFile + "/" + id + ".jpg");
				FileOutputStream output = new FileOutputStream(storeFile);
				FileCopyUtils.copy(entity.getContent(), output);
				info.setPic("spider/gztaxixh/" + id + ".jpg");

				// htmlfile
				table.select("img").attr("src", id + ".jpg");
				FileCopyUtils.copy(
						info.getSimple() + "\r\n" + table.outerHtml(),
						new FileWriter(toFile + "/" + id + ".htm"));
			} else {
				if (logger.isInfoEnabled()) {
					logger.info("获取司机图片失败！picUrl=" + picUrl);
				}
			}
		}
		return info;
	}

	/**
	 * @param html
	 * @return
	 */
	private Document parseHtml(String html) {
		Document doc = Jsoup.parse(html);
		doc.outputSettings().prettyPrint(false);// 无缩进格式
		return doc;
	}
}
