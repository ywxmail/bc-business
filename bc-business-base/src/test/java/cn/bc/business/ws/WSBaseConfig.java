/**
 * 
 */
package cn.bc.business.ws;

import java.util.Map;
import java.util.Map.Entry;

import cn.bc.core.exception.CoreException;
import cn.bc.web.ws.dotnet.DataSet;
import cn.bc.web.ws.dotnet.converter.DataSetConverter;
import cn.bc.web.ws.dotnet.formater.WSCalendarFormater;
import cn.bc.web.ws.util.WSUtils;

/**
 * @author dragon
 * 
 */
public abstract class WSBaseConfig {
	protected String id4baoCheng = "17E0FFF7-7816-46A5-83A7-23D5C9F762AB";// 宝城ID
	protected String id4guangFa = "4CD105EB-9EA3-4660-9E85-4BA39AA7960B";// 广发ID

	protected String getSoapUrl() {
		return "http://61.144.39.126/middle/WSMiddle.asmx";
	}

	protected abstract String getSoapMethod();

	protected abstract Map<String, String> getParams();

	protected String getSoapNamespace() {
		return "http://tempuri.org/";
	}

	protected String getSoapAction() {
		return getSoapNamespace() + getSoapMethod();
	}

	/**
	 * 获取ws返回的xml文档转换为的DataSet对象
	 * 
	 * @return
	 */
	public DataSet runWs4DataSet() {
		// 通过参数生成发送字符串
		StringBuffer msgTpl = buildMessage();
		// System.out.println(msgTpl.toString());

		// 执行
		DataSetConverter converter = new DataSetConverter();
		converter.addFormater("dateTime", new WSCalendarFormater());
		DataSet dataSet = WSUtils.sendAndReceive(getSoapUrl(), getSoapAction(),
				msgTpl.toString(), null, converter);

		return dataSet;
	}

	/**
	 * 获取ws返回的xml文档
	 * 
	 * @return
	 */
	public String runWs4Xml() {
		StringBuffer msgTpl = buildMessage();
		System.out.println(msgTpl.toString());

		// 执行
		DataSetConverter converter = new DataSetConverter();
		converter.addFormater("dateTime", new WSCalendarFormater());
		return WSUtils.sendAndReceive(getSoapUrl(), getSoapAction(),
				msgTpl.toString(), null);

	}

	/**
	 * 通过参数生成发送字符串
	 * 
	 * @return
	 */
	protected StringBuffer buildMessage() {
		Map<String, String> params = this.getParams();
		if (params == null)
			throw new CoreException("need params");
		StringBuffer msgTpl = new StringBuffer();
		msgTpl.append(this.wrapXMLMark(this.getSoapMethod() + " xmlns=\""
				+ this.getSoapNamespace() + "\""));
		for (Entry<String, String> e : params.entrySet()) {
			msgTpl.append(this.makeXMLNode(e.getKey(), e.getValue()));
		}
		msgTpl.append(this.wrapXMLMark("/" + this.getSoapMethod()));
		return msgTpl;
	}

	protected Object makeXMLNode(String key, String value) {
		return this.wrapXMLMark(key) + value + this.wrapXMLMark("/" + key);
	}

	protected String wrapXMLMark(String value) {
		return "<" + value + ">";
	}
}
