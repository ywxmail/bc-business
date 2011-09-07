package cn.bc.business.ws.service;

import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import org.springframework.xml.transform.StringResult;

import cn.bc.core.exception.CoreException;
import cn.bc.web.ws.dotnet.DataSet;
import cn.bc.web.ws.dotnet.converter.DataSetConverter;
import cn.bc.web.ws.dotnet.formater.WSCalendarFormater;
import cn.bc.web.ws.util.WSUtils;

/**
 * 交委WebService接口方法的Java接口实现
 * 
 * @author dragon
 * 
 */
public class WSMiddleImpl implements WSMiddle, InitializingBean {
	protected final Log logger = LogFactory.getLog(getClass());
	private String soapUrl;
	private String soapNamespace;
	private WebServiceTemplate webServiceTemplate;
	private SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd");// 日期格式不能带时分秒
	private DataSetConverter converter;

	public void setWebServiceTemplate(WebServiceTemplate webServiceTemplate) {
		this.webServiceTemplate = webServiceTemplate;
	}

	public void setSoapUrl(String soapUrl) {
		this.soapUrl = soapUrl;
	}

	public void setSoapNamespace(String soapNamespace) {
		this.soapNamespace = soapNamespace;
	}

	public void afterPropertiesSet() throws Exception {
		Assert.notNull(webServiceTemplate,
				"Property 'webServiceTemplate' is required");
		Assert.notNull(soapNamespace, "Property 'soapNamespace' is required");
	}

	public DataSet findDataSet(String bodyXml, String soapAction,
			StringBuffer strMsg) {
		if (logger.isDebugEnabled()) {
			logger.debug("soapUrl=" + soapUrl);
			logger.debug("requestBodyXml=" + bodyXml);
		}

		// 执行WebService请求
		StreamSource source = new StreamSource(new StringReader(bodyXml));
		StringResult xmlResult = new StringResult();
		if (soapAction == null || soapAction.length() == 0)
			webServiceTemplate.sendSourceAndReceiveToResult(soapUrl, source,
					xmlResult);
		else
			webServiceTemplate.sendSourceAndReceiveToResult(soapUrl, source,
					new SoapActionCallback(soapAction), xmlResult);

		// 将请求结果转换为DataSet对象
		DataSet ds = this.getDefaultDataSetConverter().convert(
				xmlResult.toString());
		if (ds != null && ds.getMsg() != null) {
			strMsg.append(ds.getMsg());
		}
		if (logger.isDebugEnabled())
			logger.debug("responeDataSet=" + ds.toString());
		return ds;
	}

	public String findXml(String bodyXml, String soapAction, StringBuffer strMsg) {
		if (logger.isDebugEnabled()) {
			logger.debug("soapUrl=" + soapUrl);
			logger.debug("requestBodyXml=" + bodyXml);
		}

		// 执行WebService请求
		StreamSource source = new StreamSource(new StringReader(bodyXml));
		StringResult xmlResult = new StringResult();
		if (soapAction == null || soapAction.length() == 0)
			webServiceTemplate.sendSourceAndReceiveToResult(soapUrl, source,
					xmlResult);
		else
			webServiceTemplate.sendSourceAndReceiveToResult(soapUrl, source,
					new SoapActionCallback(soapAction), xmlResult);

		if (logger.isDebugEnabled())
			logger.debug("responeXml=" + xmlResult.toString());
		return xmlResult.toString();
	}

	public DataSet findBreachOfTraffic(String qyId, Calendar fromDate,
			Calendar toDate, StringBuffer strMsg) {
		// 构建请求参数
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("qyid", qyId);// 企业id
		params.put("StartTime", dateFormater.format(fromDate.getTime()));// 起始日期
		params.put("EndTime", dateFormater.format(toDate.getTime()));// 结束日期
		params.put("strMsg", strMsg.toString());

		// 所调用的WebService接口方法名
		String soapMethod = "SearchPublicTransport";

		return doRequest(soapMethod, params, strMsg);
	}

	public DataSet findBreachOfBusiness(String qyId, Calendar fromDate,
			Calendar toDate, StringBuffer strMsg) {
		// 构建请求参数
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("strMasterID", qyId);// 企业id
		params.put("dWeiZhangKSRQ", dateFormater.format(fromDate.getTime()));// 起始日期
		params.put("dWeiZhangJZRQ", dateFormater.format(toDate.getTime()));// 结束日期
		params.put("strMsg", strMsg.toString());

		// 所调用的WebService接口方法名
		String soapMethod = "GetMasterWZ";

		return doRequest(soapMethod, params, strMsg);
	}

	public DataSet findAccuseAndAdvice(String qyId, Calendar fromDate,
			Calendar toDate, StringBuffer strMsg) {
		// TODO Auto-generated method stub
		return null;
	}

	public DataSet findAccuseAndAdvice(String qyId, Calendar fromDate,
			Calendar toDate, String status, String man, StringBuffer strMsg) {
		// TODO Auto-generated method stub
		return null;
	}

	public DataSet findDriverByType(String qyId, String type,
			StringBuffer strMsg) {
		// 构建请求参数
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("strQYID", qyId);
		params.put("Dtype", type);
		params.put("strMsg", strMsg.toString());

		// 所调用的WebService接口方法名
		String soapMethod = "GetDriverList";

		return doRequest(soapMethod, params, strMsg);
	}

	public DataSet findDriverDetail(String qyId, String fuWuZiGeZheng,
			String driverName, StringBuffer strMsg) {
		// 构建请求参数
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("strQYID", qyId);
		params.put("strDriverNO", fuWuZiGeZheng);
		params.put("strDriverName", driverName);
		params.put("strMsg", strMsg.toString());

		// 所调用的WebService接口方法名
		String soapMethod = "GetDriverInfo";

		return doRequest(soapMethod, params, strMsg);
	}

	public DataSet findCarDetail(String qyId, String plate, StringBuffer strMsg) {
		// 构建请求参数
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("strQYID", qyId);
		params.put("strCarNO", plate);
		params.put("strMsg", strMsg.toString());

		// 所调用的WebService接口方法名
		String soapMethod = "GetCarInfo";

		return doRequest(soapMethod, params, strMsg);
	}

	private DataSet doRequest(String soapMethod, Map<String, String> params,
			StringBuffer strMsg) {
		// 通过参数生成发送字符串
		StringBuffer requestBodyXml = buildRequestBodyXml(soapMethod, params);

		// 执行WebService请求
		StreamSource source = new StreamSource(new StringReader(
				requestBodyXml.toString()));
		StringResult xmlResult = new StringResult();
		webServiceTemplate.sendSourceAndReceiveToResult(soapUrl, source,
				new SoapActionCallback(soapNamespace + soapMethod), xmlResult);

		// 将请求结果转换为DataSet对象
		DataSet ds = this.getDefaultDataSetConverter().convert(
				xmlResult.toString());
		if (ds != null && ds.getMsg() != null) {
			strMsg.append(ds.getMsg());
		}
		if (logger.isDebugEnabled())
			logger.debug("responeDataSet=" + ds.toString());
		return ds;
	}

	/**
	 * 默认的DataSet转换器
	 * 
	 * @return
	 */
	private DataSetConverter getDefaultDataSetConverter() {
		if (converter == null) {
			converter = new DataSetConverter();
			// 注册日期类型转换器
			converter.addFormater("dateTime", new WSCalendarFormater());
		}
		return converter;
	}

	/**
	 * 构建WebService请求的Body部分的XML内容
	 * 
	 * @param soapMethod
	 *            对应调用交委WebService接口的方法名
	 * @param params
	 *            对应接口方法的中要求的参数的键值对
	 * @return
	 */
	private StringBuffer buildRequestBodyXml(String soapMethod,
			Map<String, String> params) {
		if (params == null)
			throw new CoreException("Param 'params' is required");
		StringBuffer msgTpl = new StringBuffer();
		msgTpl.append(WSUtils.wrapXMLMark(soapMethod + " xmlns=\""
				+ this.soapNamespace + "\""));
		for (Entry<String, String> e : params.entrySet()) {
			msgTpl.append(WSUtils.makeXMLNode(e.getKey(), e.getValue()));
		}
		msgTpl.append(WSUtils.wrapXMLMark("/" + soapMethod));
		if (logger.isDebugEnabled())
			logger.debug("requestBodyXml=" + msgTpl.toString());
		return msgTpl;
	}
}
