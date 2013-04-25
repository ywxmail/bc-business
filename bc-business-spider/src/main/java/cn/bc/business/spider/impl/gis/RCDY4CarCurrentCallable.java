package cn.bc.business.spider.impl.gis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Element;

import cn.bc.spider.HttpClientCallable;

/**
 * 人车对应当前车辆异常查询
 * 
 * @author dragon
 * 
 */
public class RCDY4CarCurrentCallable extends HttpClientCallable<List<String[]>> {
	/**
	 * 异常类型:0-所有类型,1-无正班司机,2-正班异车驾驶,3-替班长时间驾驶
	 * 4-长期全天营运,5-在岗不营运,6-未登记关系司机,7-转岗营运,8-涉嫌代刷卡
	 */
	private String type = "0";
	private String company = "";// 公司
	private String plate = "";// 车牌号码: 粤A12345

	public RCDY4CarCurrentCallable() {
		this.setId("gis");
		this.setMethod("post");
		this.setSuccessExpression("document.select(\"#tabDataGrid\").size() > 0");
		this.setResultExpression("document.select(\"#tabDataGrid\").get(0)");

		this.setUrl(LoginCallable.URL4EXEC);
		this.addFormData(LoginCallable.KEY4FUNCTION, "200102");
	}

	@Override
	protected Map<String, String> getFormData() {
		Map<String, String> formData = super.getFormData();

		// 添加扩展的参数
		if (type != null)
			formData.put("ResultType", type);
		if (company != null)
			formData.put("CompanyName", company);
		if (plate != null)
			formData.put("PlateNo", plate);

		return formData;
	}

	@Override
	protected List<String[]> parseData(Object data) {
		Element table = (Element) data;
		if (logger.isInfoEnabled()) {
			logger.info(table.outerHtml());
		}
		// System.out.println(table.outerHtml());
		List<String[]> list = new ArrayList<String[]>();
		List<String> values;
		for (Element tr : table.children()) {
			values = new ArrayList<String>();
			for (Element td : tr.children()) {
				values.add(td.text());
			}
			list.add(values.toArray(new String[0]));
		}
		return list;
	}

	/**
	 * 异常类型:0-所有类型,1-无正班司机,2-正班异车驾驶,3-替班长时间驾驶
	 * 4-长期全天营运,5-在岗不营运,6-未登记关系司机,7-转岗营运,8-涉嫌代刷卡
	 */
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getPlate() {
		return plate;
	}

	public void setPlate(String plate) {
		this.plate = plate;
	}
}