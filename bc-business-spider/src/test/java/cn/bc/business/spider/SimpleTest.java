package cn.bc.business.spider;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import junit.framework.Assert;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Test;

public class SimpleTest {
	SimpleDateFormat chineseDateFormat = new SimpleDateFormat("yyyy年M月d日");

	@Test
	public void test01() throws ParseException {
		// 格式为：“javascript:getvioDtail("4401131009507673","1111","1111111111111111")”--单号(序号)、决定书类别、决定书编号
		String t = "javascript:getvioDtail(\"4401131009507673\",\"1111\",\"1111111111111111\")";
		t = t.replaceAll("javascript:getvioDtail\\(|\\)|\"", "");
		Assert.assertEquals("4401131009507673,1111,1111111111111111", t);
	}

	@Test
	public void test02() throws ParseException {
		String t = "<strong>罚款金额:</strong>150";
		t = t.replaceAll("<strong>*</strong>", "");
		Assert.assertEquals("150", t);
	}

	@Test
	public void test03() throws ParseException {
		String html = "<body><td><strong>罚款金额:</strong>150</td></body>";
		Document doc = Jsoup.parse(html);
		Elements tds = doc.select("td");
		tds.get(0).child(0).remove();
		System.out.println(tds.get(0).text());
	}
}
