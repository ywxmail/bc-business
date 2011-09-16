/**
 * 
 */
package cn.bc.business.ws;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import cn.bc.web.ws.dotnet.DataSet;

/**
 * 人车对应关系接口数据获取测试：查询企业人车备案信息
 * 
 * @author dragon
 * 
 */
public class RC_GetDriverTaxiQYWSTest extends WSBaseConfig {
	@Override
	protected String getSoapMethod() {
		return "GetDriverTaxiQY";
	}

	@Override
	protected Map<String, String> getParams() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("strQYID", id4baoCheng);
		// 司机服务资格证(模糊匹配)
		params.put("strDriverNO", "");
		// 车牌号码(模糊匹配)
		params.put("strCarNO", "");
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
