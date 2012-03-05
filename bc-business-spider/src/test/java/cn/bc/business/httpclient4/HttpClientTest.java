package cn.bc.business.httpclient4;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.FileCopyUtils;

/**
 * http://fireinwind.javaeye.com/blog/707260
 * 
 * @author dragon
 * 
 */
public class HttpClientTest {
	public final static String KEY_VIEWSTATE = "__VIEWSTATE";
	public final static String KEY_EVENTVALIDATION = "__EVENTVALIDATION";

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test0() throws Exception {
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

		// 核心应用类
		HttpClient httpClient = new DefaultHttpClient();

		// 获取登录页面
		System.out
				.println("1-------------------------------------------------------------");
		request4get = new HttpGet(loginUrl);
		// System.out.println(request.getRequestLine());
		Assert.assertEquals("GET http://www.gztaxixh.com/login.aspx HTTP/1.1",
				request4get.getRequestLine().toString());
		response = httpClient.execute(request4get);
		System.out.println(request4get.getURI());
		System.out.println(response.getStatusLine());
		Assert.assertEquals("HTTP/1.1 200 OK", response.getStatusLine()
				.toString());
		html = EntityUtils.toString(response.getEntity());
		// System.out.println(html);
		doc = Jsoup.parse(html);
		viewState = doc.select("#" + KEY_VIEWSTATE).val();
		eventValidation = doc.select("#" + KEY_EVENTVALIDATION).val();
		System.out.println("viewState=" + viewState);
		System.out.println("eventValidation=" + eventValidation);

		// 执行登录处理
		System.out
				.println("2-------------------------------------------------------------");
		formParams = new ArrayList<NameValuePair>();
		formParams.add(new BasicNameValuePair(KEY_VIEWSTATE, viewState));
		formParams.add(new BasicNameValuePair(KEY_EVENTVALIDATION,
				eventValidation));
		formParams.add(new BasicNameValuePair("TextBox1", "bch"));
		formParams.add(new BasicNameValuePair("TextBox2", "baocheng123"));
		formParams.add(new BasicNameValuePair("Button1", "确定"));
		entity = new UrlEncodedFormEntity(formParams, "GBK");
		request4post = new HttpPost(loginUrl);
		request4post.setEntity(entity);
		response = httpClient.execute(request4post);
		System.out.println(request4post.getURI());
		System.out.println(response.getStatusLine());
		html = EntityUtils.toString(response.getEntity());
		// System.out.println(html);
		doc = Jsoup.parse(html);
		boolean loginSuccess = "已登陆".equals(doc.select("#_ctl0_L1").text());
		System.out.println(loginSuccess ? "登录成功" : "登录失败");
		if (!loginSuccess)
			return;

		// 获取“出租车驾驶员信誉档案”页面
		System.out
				.println("3-------------------------------------------------------------");
		request4get = new HttpGet(infoUrl);
		response = httpClient.execute(request4get);
		System.out.println(request4get.getURI());
		System.out.println(response.getStatusLine());
		html = EntityUtils.toString(response.getEntity());
		// System.out.println(html);
		doc = Jsoup.parse(html);
		viewState = doc.select("#" + KEY_VIEWSTATE).val();
		eventValidation = doc.select("#" + KEY_EVENTVALIDATION).val();
		System.out.println("viewState=" + viewState);
		System.out.println("eventValidation=" + eventValidation);

		// 获取指定资格证的页面
		System.out
				.println("4-------------------------------------------------------------");
		formParams = new ArrayList<NameValuePair>();
		formParams.add(new BasicNameValuePair(KEY_VIEWSTATE, viewState));
		formParams.add(new BasicNameValuePair(KEY_EVENTVALIDATION,
				eventValidation));
		formParams.add(new BasicNameValuePair("_ctl0:TextBox1", ""));
		formParams.add(new BasicNameValuePair("txtEnterValue", ""));
		formParams.add(new BasicNameValuePair(
				"_ctl0:ContentPlaceHolder1:ddlSearchBy", "服务资格证"));
		formParams.add(new BasicNameValuePair(
				"_ctl0:ContentPlaceHolder1:txtFilter", "257506"));
		formParams.add(new BasicNameValuePair(
				"_ctl0:ContentPlaceHolder1:btnSubmit", "确认"));
		entity = new UrlEncodedFormEntity(formParams, "GBK");
		request4post = new HttpPost(infoUrl);
		request4post.setEntity(entity);
		response = httpClient.execute(request4post);
		System.out.println(request4post.getURI());
		System.out.println(response.getStatusLine());
		html = EntityUtils.toString(response.getEntity());
		// System.out.println(html);
		doc = Jsoup.parse(html);
		viewState = doc.select("#" + KEY_VIEWSTATE).val();
		eventValidation = doc.select("#" + KEY_EVENTVALIDATION).val();
		System.out.println("viewState=" + viewState);
		System.out.println("eventValidation=" + eventValidation);
		Elements table = doc.select("#_ctl0_ContentPlaceHolder1_dgCardList");
		// System.out.println(table.outerHtml());
		Elements trs = table.select("tr:gt(0)");
		System.out.println(trs.size());

		// 获取指定资格证的详细页面
		System.out
				.println("5-------------------------------------------------------------");
		formParams = new ArrayList<NameValuePair>();
		formParams.add(new BasicNameValuePair(KEY_VIEWSTATE, viewState));
		formParams.add(new BasicNameValuePair(KEY_EVENTVALIDATION,
				eventValidation));
//		formParams.add(new BasicNameValuePair("_ctl0:TextBox1", ""));
//		formParams.add(new BasicNameValuePair("txtEnterValue", ""));
//		formParams.add(new BasicNameValuePair(
//				"_ctl0:ContentPlaceHolder1:ddlSearchBy", "服务资格证"));
//		formParams.add(new BasicNameValuePair(
//				"_ctl0:ContentPlaceHolder1:txtFilter", "257506"));
		formParams.add(new BasicNameValuePair(
				"_ctl0:ContentPlaceHolder1:dgCardList:_ctl2:ibtnView.x", "8"));
		formParams.add(new BasicNameValuePair(
				"_ctl0:ContentPlaceHolder1:dgCardList:_ctl2:ibtnView.y", "8"));
		entity = new UrlEncodedFormEntity(formParams, "GBK");
		request4post = new HttpPost(infoUrl);
		request4post.setEntity(entity);
		response = httpClient.execute(request4post);
		System.out.println(request4post.getURI());
		System.out.println(response.getStatusLine());
		html = EntityUtils.toString(response.getEntity());
		// System.out.println(html);
		doc = Jsoup.parse(html);
		table = doc.select("#Table3 table.ListTable");
		System.out.println(table.outerHtml());

		// 获取司机图片
		String picUrl = "http://www.gztaxixh.com/"
				+ table.select("img#_ctl0_ContentPlaceHolder1_imgPic").attr(
						"src");
		System.out.println(picUrl);
		request4get = new HttpGet(picUrl);
		response = httpClient.execute(request4get);
		if (response.getEntity().isStreaming()) {
			System.out.println("c:/t.jpg");
			File storeFile = new File("d:/t/t.jpg");
			FileOutputStream output = new FileOutputStream(storeFile);
			// 得到网络资源的字节数组,并写入文件
			FileCopyUtils.copy(response.getEntity().getContent(), output);
		}
	}
	

	@Test
	public void test1() throws Exception {
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

		// 核心应用类
		HttpClient httpClient = new DefaultHttpClient();

		// 获取登录页面
		System.out
				.println("1-------------------------------------------------------------");
		request4get = new HttpGet(loginUrl);
		// System.out.println(request.getRequestLine());
		Assert.assertEquals("GET http://www.gztaxixh.com/login.aspx HTTP/1.1",
				request4get.getRequestLine().toString());
		response = httpClient.execute(request4get);
		System.out.println(request4get.getURI());
		System.out.println(response.getStatusLine());
		Assert.assertEquals("HTTP/1.1 200 OK", response.getStatusLine()
				.toString());
		html = EntityUtils.toString(response.getEntity());
		// System.out.println(html);
		doc = Jsoup.parse(html);
		viewState = doc.select("#" + KEY_VIEWSTATE).val();
		eventValidation = doc.select("#" + KEY_EVENTVALIDATION).val();
		System.out.println("viewState=" + viewState);
		System.out.println("eventValidation=" + eventValidation);

		// 执行登录处理
		System.out
				.println("2-------------------------------------------------------------");
		formParams = new ArrayList<NameValuePair>();
		formParams.add(new BasicNameValuePair(KEY_VIEWSTATE, viewState));
		formParams.add(new BasicNameValuePair(KEY_EVENTVALIDATION,
				eventValidation));
		formParams.add(new BasicNameValuePair("TextBox1", "bch"));
		formParams.add(new BasicNameValuePair("TextBox2", "baocheng123"));
		formParams.add(new BasicNameValuePair("Button1", "确定"));
		entity = new UrlEncodedFormEntity(formParams, "GBK");
		request4post = new HttpPost(loginUrl);
		request4post.setEntity(entity);
		response = httpClient.execute(request4post);
		System.out.println(request4post.getURI());
		System.out.println(response.getStatusLine());
		html = EntityUtils.toString(response.getEntity());
		// System.out.println(html);
		doc = Jsoup.parse(html);
		boolean loginSuccess = "已登陆".equals(doc.select("#_ctl0_L1").text());
		System.out.println(loginSuccess ? "登录成功" : "登录失败");
		if (!loginSuccess)
			return;

		// 获取“出租车驾驶员信誉档案”页面
		System.out
				.println("3-------------------------------------------------------------");
		request4get = new HttpGet(infoUrl);
		response = httpClient.execute(request4get);
		System.out.println(request4get.getURI());
		System.out.println(response.getStatusLine());
		html = EntityUtils.toString(response.getEntity());
		// System.out.println(html);
		doc = Jsoup.parse(html);
		viewState = doc.select("#" + KEY_VIEWSTATE).val();
		eventValidation = doc.select("#" + KEY_EVENTVALIDATION).val();
		System.out.println("viewState=" + viewState);
		System.out.println("eventValidation=" + eventValidation);

		// 获取指定资格证的页面
//		System.out
//				.println("4-------------------------------------------------------------");
//		formParams = new ArrayList<NameValuePair>();
//		formParams.add(new BasicNameValuePair(KEY_VIEWSTATE, viewState));
//		formParams.add(new BasicNameValuePair(KEY_EVENTVALIDATION,
//				eventValidation));
//		formParams.add(new BasicNameValuePair("_ctl0:TextBox1", ""));
//		formParams.add(new BasicNameValuePair("txtEnterValue", ""));
//		formParams.add(new BasicNameValuePair(
//				"_ctl0:ContentPlaceHolder1:ddlSearchBy", "服务资格证"));
//		formParams.add(new BasicNameValuePair(
//				"_ctl0:ContentPlaceHolder1:txtFilter", "257506"));
//		formParams.add(new BasicNameValuePair(
//				"_ctl0:ContentPlaceHolder1:btnSubmit", "确认"));
//		entity = new UrlEncodedFormEntity(formParams, "GBK");
//		request4post = new HttpPost(infoUrl);
//		request4post.setEntity(entity);
//		response = httpClient.execute(request4post);
//		System.out.println(request4post.getURI());
//		System.out.println(response.getStatusLine());
//		html = EntityUtils.toString(response.getEntity());
//		// System.out.println(html);
//		doc = Jsoup.parse(html);
//		viewState = doc.select("#" + KEY_VIEWSTATE).val();
//		eventValidation = doc.select("#" + KEY_EVENTVALIDATION).val();
//		System.out.println("viewState=" + viewState);
//		System.out.println("eventValidation=" + eventValidation);
//		Elements table = doc.select("#_ctl0_ContentPlaceHolder1_dgCardList");
//		// System.out.println(table.outerHtml());
//		Elements trs = table.select("tr:gt(0)");
//		System.out.println(trs.size());

		// 获取指定资格证的详细页面
		System.out
				.println("5-------------------------------------------------------------");
		formParams = new ArrayList<NameValuePair>();
		formParams.add(new BasicNameValuePair(KEY_VIEWSTATE, viewState));
		formParams.add(new BasicNameValuePair(KEY_EVENTVALIDATION,
				eventValidation));
		formParams.add(new BasicNameValuePair("_ctl0:TextBox1", ""));
		formParams.add(new BasicNameValuePair("txtEnterValue", ""));
		formParams.add(new BasicNameValuePair(
				"_ctl0:ContentPlaceHolder1:ddlSearchBy", "服务资格证"));
		formParams.add(new BasicNameValuePair(
				"_ctl0:ContentPlaceHolder1:txtFilter", "257506"));
		formParams.add(new BasicNameValuePair(
				"_ctl0:ContentPlaceHolder1:btnSubmit", "确认"));
		formParams.add(new BasicNameValuePair(
				"_ctl0:ContentPlaceHolder1:dgCardList:_ctl2:ibtnView.x", "8"));
		formParams.add(new BasicNameValuePair(
				"_ctl0:ContentPlaceHolder1:dgCardList:_ctl2:ibtnView.y", "8"));
		entity = new UrlEncodedFormEntity(formParams, "GBK");
		request4post = new HttpPost(infoUrl);
		request4post.setEntity(entity);
		response = httpClient.execute(request4post);
		System.out.println(request4post.getURI());
		System.out.println(response.getStatusLine());
		html = EntityUtils.toString(response.getEntity());
		 System.out.println(html);
		doc = Jsoup.parse(html);
		Elements table = doc.select("#Table3 table.ListTable");
		System.out.println(table.outerHtml());

		// 获取司机图片
		String picUrl = "http://www.gztaxixh.com/"
				+ table.select("img#_ctl0_ContentPlaceHolder1_imgPic").attr(
						"src");
		System.out.println(picUrl);
		request4get = new HttpGet(picUrl);
		response = httpClient.execute(request4get);
		if (response.getEntity().isStreaming()) {
			System.out.println("c:/t.jpg");
			File storeFile = new File("d:/t/t.jpg");
			FileOutputStream output = new FileOutputStream(storeFile);
			// 得到网络资源的字节数组,并写入文件
			FileCopyUtils.copy(response.getEntity().getContent(), output);
		}
	}
}
