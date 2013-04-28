package cn.bc.business.spider.impl.gzjd.cgs;

import java.util.Date;

import org.apache.tools.ant.util.DateUtils;
import org.junit.Assert;
import org.junit.Test;

import cn.bc.spider.Result;
import cn.bc.spider.TaskExecutor;

public class LoginCallableTest {
	@Test
	public void test() throws Exception {
		// 获取验证码
		String key = DateUtils.format(new Date(), "yyyyMMddHHmmssSSSS");
		System.out.println("key=" + key);
		Result<String> result = TaskExecutor.get(new CaptchaCallable(key));
		Assert.assertTrue(result.isSuccess());
		String captcha = result.getData();
		System.out.println("captcha=" + captcha);

		// 登录前的用户信息验证
		Result<Boolean> validate = TaskExecutor.get(new LoginValidateCallable(
				"72197317-9", "818000", captcha));
		Assert.assertTrue(validate.isSuccess());
		System.out.println("validate html=" + validate.getContent());

		// 登录
		Result<Boolean> login = TaskExecutor.get(new LoginCallable(
				"72197317-9", "818000", captcha));
		Assert.assertTrue(login.isSuccess());
		System.out.println("login html=" + login.getContent());
	}
}
