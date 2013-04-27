package cn.bc.business.spider.impl.gzjd.cgs;

import java.util.Map;

import cn.bc.spider.SimpleLoginCallable;

/**
 * 网上车管所 登录
 * 
 * @author dragon
 * 
 */
public class LoginCallable extends SimpleLoginCallable {
	private String captcha;// 验证码

	/** 验证码图片的url */
	public static String URL4CAPTCHA = "http://www.gzjd.gov.cn/cgs/captcha.jpg";
	/** 获取数据的url */
	public static String URL4EXEC = "http://www.gzjd.gov.cn/cgs/service/getUserInfo";

	public LoginCallable(String userName, String password, String captcha) {
		this.setKey4userName("username");
		this.setKey4password("password");
		this.setUserName(userName);
		this.setPassword(password);
		this.captcha = captcha;

		this.setId("gzjd.cgs");
		this.setUrl(LoginCallable.URL4EXEC);

		this.setSuccessExpression("new Integer(document.select(\"#__ROWCOUNT\").val()) > 0");
		this.setResultExpression("document.select(\"#tabDataGrid\").get(0)");
	}

	@Override
	protected Map<String, String> getFormData() {
		Map<String, String> formData = super.getFormData();

		// 添加帐号和密码参数
		if (captcha != null)
			formData.put("captchaId", captcha);

		return formData;
	}
}