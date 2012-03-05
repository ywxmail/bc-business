package cn.bc.business.spider;

import java.text.SimpleDateFormat;

import junit.framework.Assert;

import org.junit.Test;

import cn.bc.business.spider.domain.GzTaxiXhDriverInfo;
import cn.bc.web.spider.Spider;

public class Spider4GzTaxiXhDriverInfoTest {
	SimpleDateFormat chineseDateFormat = new SimpleDateFormat("yyyy年M月d日");

	@Test
	public void test01() throws Exception {
		String type = "服务资格证";
		String userName = "bch";
		String userPassword = "baocheng123";
		String rootPath = "/bcdata";

		String value = "257506";
		Spider<GzTaxiXhDriverInfo> spider = new Spider4GzTaxiXhDriverInfo(type,
				value, userName, userPassword, rootPath, (int) 0);
		GzTaxiXhDriverInfo info = spider.excute();
		Assert.assertNotNull(info);

		System.out.println("simple" + info.getSimple());
		System.out.println("detail=" + info.getDetail());
		System.out.println("msg=" + info.getMsg());
		System.out.println("pic=" + info.getPic());
	}
}
