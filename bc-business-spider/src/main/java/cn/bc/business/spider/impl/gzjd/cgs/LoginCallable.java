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

	public LoginCallable(String userName, String password, String captcha) {
		this.setKey4userName("j_username");
		this.setKey4password("j_password");
		this.setUserName(userName);
		this.setPassword(password);
		this.captcha = captcha;

		this.setId("wscgs");
		this.setUrl("http://www.gzjd.gov.cn/cgs/j_ajax_security_check");
	}

	@Override
	protected Map<String, String> getFormData() {
		Map<String, String> formData = super.getFormData();

		// 添加帐号和密码参数
		if (captcha != null)
			formData.put("captchaId", captcha);

		return formData;
	}

	@Override
	public void parseDocument() {
		// Do nothing：因请求返回的是json格式，没有dom结构
	}

	@Override
	public Boolean isSuccess() {
		return "\"1\"".equals(this.content) || "1".equals(this.content);
	}
}