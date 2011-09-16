/**
 * 
 */
package cn.bc.business.ws;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import cn.bc.web.ws.dotnet.DataSet;

/**
 * 人车对应关系接口数据获取测试：查询备案车辆列表
 * 
 * @author dragon
 * 
 */
public class RC_GetDriverTaxiListWSTest extends WSBaseConfig {
	@Override
	protected String getSoapMethod() {
		return "GetDriverTaxiList";
	}

	@Override
	protected Map<String, String> getParams() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("strQYID", id4baoCheng);
		// 已经备案:1,未备案,0,全部列表:空,正班司机1和正班司机2都填报的车:11, 只填报主班司机1或正班司机2的车:12,
		// 正班司机1和正班司机2都未填报的车:13 ,有替班的车辆:14
		params.put("beianType", "");
		params.put("strMsg", "");
		return params;
	}

	@Test
	public void test() {
		// System.out.println(this.runWs4Xml());

//		DataSet dataSet = this.runWs4DataSet();
		// Assert.assertNotNull(dataSet);
		// Assert.assertNotNull(dataSet.getRows());
		// Assert.assertTrue(!dataSet.getRows().isEmpty());
//		System.out.println(dataSet);
	}
}
