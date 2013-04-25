package cn.bc.business.spider.impl.gis;

import org.junit.Assert;
import org.junit.Test;

import cn.bc.spider.Result;
import cn.bc.spider.TaskExecutor;

public class LoginCallableTest {
	@Test
	public void test() throws Exception {
		// 登录测试
		Result<Boolean> result = TaskExecutor.get(new LoginCallable(
				"baochenglh_admin", "baocheng-gs"));
		Assert.assertTrue(result.isSuccess());

		// 打印响应的html内容
		System.out.println(result.getHtml());
	}
}
