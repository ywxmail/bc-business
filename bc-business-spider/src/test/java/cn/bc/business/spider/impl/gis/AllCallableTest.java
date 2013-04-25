package cn.bc.business.spider.impl.gis;

import java.util.List;
import java.util.concurrent.Callable;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.util.StringUtils;

import cn.bc.spider.Result;
import cn.bc.spider.TaskExecutor;

public class AllCallableTest {
	@Test
	public void test() throws Exception {
		// 登录:帐号+密码
		Result<Boolean> loginResult = TaskExecutor.get(new LoginCallable(
				"baochenglh_admin", "baocheng-gs"));
		Assert.assertTrue(loginResult.isSuccess());
		System.out.println("login success.");

		Result<List<String[]>> result;
		Callable<Result<List<String[]>>> callable;

		// 人车对应 当前车辆异常
		callable = new RCDY4CarCurrentCallable();
		result = TaskExecutor.get(callable);
		Assert.assertTrue(result.isSuccess());
		printData("人车对应 当前车辆异常", result.getData());

		// 人车对应 历史车辆异常
		RCDY4CarHistoryCallable c1 = new RCDY4CarHistoryCallable();
		c1.setFromDate("2013-01-01");// 开始日期: yyyy-MM-dd
		c1.setToDate("2013-05-01");// 结束日期: yyyy-MM-dd
		result = TaskExecutor.get(c1);
		Assert.assertTrue(result.isSuccess());
		printData("人车对应 历史车辆异常", result.getData());

		// 人车对应 当前司机异常
		callable = new RCDY4DriverCurrentCallable();
		result = TaskExecutor.get(callable);
		Assert.assertTrue(result.isSuccess());
		printData("人车对应 历史司机异常", result.getData());

		// 人车对应 历史司机异常
		RCDY4DriverHistoryCallable c2 = new RCDY4DriverHistoryCallable();
		c2.setFromDate("2013-01-01");// 开始日期: yyyy-MM-dd
		c2.setToDate("2013-02-01");// 结束日期: yyyy-MM-dd
		result = TaskExecutor.get(c2);
		Assert.assertTrue(result.isSuccess());
		printData("人车对应 历史司机异常", result.getData());

		// 人车对应 停运报备再营运异常
		RCDY4CarStop2RunCallable c3 = new RCDY4CarStop2RunCallable();
		c3.setFromDate("2012-01-01");// 开始日期: yyyy-MM-dd
		c3.setToDate("2013-05-01");// 结束日期: yyyy-MM-dd
		result = TaskExecutor.get(c3);
		Assert.assertTrue(result.isSuccess());
		printData("人车对应 停运报备再营运异常", result.getData());
	}

	// 打印数据
	private void printData(String title, List<String[]> data) {
		System.out.println("====" + title + "====");
		System.out.println("data:(size=" + data.size() + ")");
		for (String[] info : data) {
			System.out.println(StringUtils.arrayToCommaDelimitedString(info));
		}
	}
}