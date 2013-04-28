package cn.bc.business.spider.impl.gzjd.cgs;

import java.util.Date;

import org.apache.tools.ant.util.DateUtils;
import org.junit.Assert;
import org.junit.Test;

import cn.bc.spider.Result;
import cn.bc.spider.TaskExecutor;

public class CaptchaCallableTest {
	@Test
	public void test() throws Exception {
		// 获取验证码
		String key = DateUtils.format(new Date(), "yyyyMMddHHmmssSSSS");
		System.out.println("key=" + key);
		Result<String> result = TaskExecutor.get(new CaptchaCallable(key));
		Assert.assertTrue(result.isSuccess());

		// 打印响应的html内容
		System.out.println("captcha=" + result.getData());
	}
}