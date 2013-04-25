package cn.bc.business.spider.impl.gis;

import cn.bc.spider.SimpleLoginCallable;

/**
 * 出租车应用管理系统 登录
 * 
 * @author dragon
 * 
 */
public class LoginCallable extends SimpleLoginCallable {
	/** 获取数据的url */
	public static String URL4EXEC = "http://gis.gci-china.com:8083/exec.aspx";
	/** 函数键 */
	public static String KEY4FUNCTION = "Sys_FuncID";

	public LoginCallable(String userName, String password) {
		this.setKey4userName("pUserID");
		this.setKey4password("pPWD");
		this.setUserName(userName);
		this.setPassword(password);

		this.setId("gis");
		this.setUrl(LoginCallable.URL4EXEC);
		this.addFormData(LoginCallable.KEY4FUNCTION, "110");

		this.setSuccessExpression("new Integer(document.select(\"#__ROWCOUNT\").val()) > 0");
		this.setResultExpression("document.select(\"#tabDataGrid\").get(0)");
	}
}