package cn.bc.business.spider.impl.gis;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.util.StringUtils;

import cn.bc.spider.Result;
import cn.bc.spider.TaskExecutor;

public class RCDY4CarHistoryCallableTest {
	@Test
	public void test() throws Exception {
		// 登录:帐号+密码
		Result<Boolean> result1 = TaskExecutor.get(new LoginCallable(
				"baochenglh_admin", "baocheng-gs"));
		Assert.assertTrue(result1.isSuccess());
		System.out.println("login success.");

		// 获取数据
		RCDY4CarHistoryCallable callable = new RCDY4CarHistoryCallable();
		callable.setFromDate("2013-01-01");// 开始日期: yyyy-MM-dd
		callable.setToDate("2013-05-01");// 结束日期: yyyy-MM-dd
		Result<List<String[]>> result2 = TaskExecutor.get(callable);
		Assert.assertTrue(result2.isSuccess());

		// 打印数据
		List<String[]> data = result2.getData();
		System.out.println("data:(size=" + data.size() + ")");
		for (String[] info : data) {
			System.out.println(StringUtils.arrayToCommaDelimitedString(info));
		}
	}
}