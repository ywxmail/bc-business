package cn.bc.business.spider.impl.gzjd.cgs;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import cn.bc.spider.SimpleLoginCallable;

/**
 * 网上车管所 登录前的用户信息验证
 * <p>
 * 经实际测试这里验证码是多余的，就算错了也通用验证通过
 * </p>
 * 
 * @author dragon
 * 
 */
public class LoginValidateCallable extends SimpleLoginCallable {
	private String captcha;// 验证码

	public LoginValidateCallable(String userName, String password,
			String captcha) {
		this.setKey4userName("username");
		this.setKey4password("password");
		this.setUserName(userName);
		this.setPassword(password);
		this.captcha = captcha;

		this.setId("wscgs");
		this.setUrl("http://www.gzjd.gov.cn/cgs/service/getUserInfo");
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
		try {
			JSONObject json = new JSONObject(this.content.toString());
			// System.out.println(json);
			if (json.getInt("returnCode") == 1) {// 代表用户信息验证成功,不过也可能使未激活的用户
				if ("inactive".equals(json.getJSONObject("data").getString(
						"STATUS"))) {
					// 未激活的用户
					return false;
				}
				return true;
			}
		} catch (JSONException e) {
			logger.warn(e.getMessage(), e);
		}
		return false;
	}
}