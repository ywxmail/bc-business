/**
 * 
 */
package cn.bc.business.web.struts2;

import cn.bc.web.formater.AbstractFormater;

/**
 * 证照遗失模块补办结果列的布尔类型值的格式化
 * 
 * @author dragon
 * 
 */
public class BooleanFormater4certLost extends AbstractFormater<String> {
	private String yes = "补办";
	private String no = "未补办";

	public BooleanFormater4certLost() {

	}

	public BooleanFormater4certLost(String yes, String no) {
		this();
		this.yes = yes;
		this.no = no;
	}

	public String format(Object context, Object value) {
		if (value == null)
			return null;
		if (value instanceof Boolean)
			return ((Boolean) value).booleanValue() ? yes : no;
		else if (value instanceof String)
			return "true".equalsIgnoreCase((String) value) ? yes : no;
		else
			return value.toString();
	}
}
