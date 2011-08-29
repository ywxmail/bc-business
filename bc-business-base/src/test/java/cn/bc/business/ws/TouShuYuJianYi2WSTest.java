/**
 * 
 */
package cn.bc.business.ws;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

import cn.bc.web.ws.dotnet.DataSet;

/**
 * 投诉和建议接口数据获取测试
 * 
 * @author dragon
 * 
 */
public class TouShuYuJianYi2WSTest extends WSBaseConfig {
	@Override
	protected String getSoapMethod() {
		return "SearchQYXC";
	}

	@Override
	protected Map<String, String> getParams() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("qyid", id4baoCheng);
		params.put("StartTime", "2011-01-01");// 起始日期
		params.put("EndTime", "2011-01-31");// 结束日期
		params.put("Status", "3");// 1.未处理 2.已处理 3.已结案
		params.put("Man", "");// 经办人
		params.put("strMsg", "");
		return params;
	}

	@Test
	public void test() {
		System.out.println(this.runWs4Xml());
		DataSet dataSet = this.runWs4DataSet();
		Assert.assertNotNull(dataSet);
		Assert.assertNotNull(dataSet.getRows());
		Assert.assertTrue(!dataSet.getRows().isEmpty());
		System.out.println(dataSet);
	}
}
