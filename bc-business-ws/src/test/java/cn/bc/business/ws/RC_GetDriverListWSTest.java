/**
 * 
 */
package cn.bc.business.ws;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import cn.bc.web.ws.dotnet.DataSet;

/**
 * 人车对应关系接口数据获取测试：查询人车备案司机列表信息
 * 
 * @author dragon
 * 
 */
public class RC_GetDriverListWSTest extends WSBaseConfig {
	@Override
	protected String getSoapMethod() {
		return "GetDriverList";
	}

	@Override
	protected Map<String, String> getParams() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("strQYID", id4baoCheng);
		params.put("Dtype", "");// 司机类型：主班1,主班2,公共替班,固定车辆组替班,固定车辆替班,不可营运替班,待岗替班
		params.put("strMsg", "");
		return params;
	}

	@Test
	public void test() {
//		System.out.println(this.runWs4Xml());

//		 DataSet dataSet = this.runWs4DataSet();
		// Assert.assertNotNull(dataSet);
		// Assert.assertNotNull(dataSet.getRows());
		// Assert.assertTrue(!dataSet.getRows().isEmpty());
//		 System.out.println(dataSet);
	}
}
