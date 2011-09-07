/**
 * 
 */
package cn.bc.business.ws;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import cn.bc.web.ws.dotnet.DataSet;

/**
 * 人车对应关系接口数据获取测试：根据车牌号码查询车辆基本资料(企业)
 * 
 * @author dragon
 * 
 */
public class RC_GetCarInfoWSTest extends WSBaseConfig {
	@Override
	protected String getSoapMethod() {
		return "GetCarInfo";
	}

	@Override
	protected Map<String, String> getParams() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("strQYID", id4baoCheng);
		params.put("strCarNO", "");// 车牌号码
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
