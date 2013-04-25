package cn.bc.business.spider.impl.gis;

import java.util.Map;

/**
 * 人车对应 停运报备再营运查询
 * 
 * @author dragon
 * 
 */
public class RCDY4CarStop2RunCallable extends RCDY4CarCurrentCallable {
	private String fromDate = "";// 开始日期: yyyy-MM-dd
	private String toDate = "";// 结束日期: yyyy-MM-dd

	public RCDY4CarStop2RunCallable() {
		super();
		this.addFormData(LoginCallable.KEY4FUNCTION, "200200");
	}

	@Override
	protected Map<String, String> getFormData() {
		Map<String, String> formData = super.getFormData();

		// 添加扩展的参数
		if (fromDate != null)
			formData.put("TimeBegin", fromDate);
		if (toDate != null)
			formData.put("TimeEnd", toDate);

		return formData;
	}

	public String getFromDate() {
		return fromDate;
	}

	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}

	public String getToDate() {
		return toDate;
	}

	public void setToDate(String toDate) {
		this.toDate = toDate;
	}
}