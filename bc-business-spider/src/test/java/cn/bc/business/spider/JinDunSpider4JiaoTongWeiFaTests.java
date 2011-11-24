package cn.bc.business.spider;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

import cn.bc.spider.Spider;

public class JinDunSpider4JiaoTongWeiFaTests {

	@Test
	public void test() {
		Spider<Map<String, Map<String, Object>>> spider = new JinDunSpider4JiaoTongWeiFa(
				"F1P51", "6892");
		spider.excute();
	}
}
