package cn.bc.business.spider.impl.gis;

import java.util.Map;

/**
 * 人车对应历史车辆异常查询
 * 
 * @author dragon
 * 
 */
public class RCDY4CarHistoryCallable extends RCDY4CarCurrentCallable {
	private String fromDate = "";// 开始日期: yyyy-MM-dd
	private String toDate = "";// 结束日期: yyyy-MM-dd

	public RCDY4CarHistoryCallable() {
		super();
		this.addFormData(LoginCallable.KEY4FUNCTION, "200103");
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