/**
 * 
 */
package cn.bc.business.contract.web.struts2;

import java.util.Calendar;
import java.util.Date;

import org.apache.tools.ant.util.DateUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.opensymphony.xwork2.ActionSupport;

/**
 * 选择日期范围的Action
 * 
 * @author wis
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class SelectDateAndChargerAction extends ActionSupport {
	private static final long serialVersionUID = 1L;

	public String title = getText("title.selectDateRange");// 对话框标题
	private Calendar startDate;// 开始日期 yyyy-MM-dd
	private Calendar endDate;// 结束日期 yyyy-MM-dd
	private Long carId;// 车辆ID
	private int addDay = 0;// 要在开始日期上附加的天数
	public String code;// 合同编号

	public int getAddDay() {
		return addDay;
	}

	public void setAddDay(int addDay) {
		this.addDay = addDay;
	}

	public Calendar getStartDate() {
		return startDate;
	}

	public void setStartDate(Calendar startDate) {
		this.startDate = startDate;
	}

	public Calendar getEndDate() {
		return endDate;
	}

	public void setEndDate(Calendar endDate) {
		this.endDate = endDate;
	}

	public Long getCarId() {
		return carId;
	}

	public void setCarId(Long carId) {
		this.carId = carId;
	}

	@Override
	public String execute() throws Exception {
		if (addDay > 0 && startDate != null)
			startDate.add(Calendar.DAY_OF_MONTH, addDay);

		// 自动生成经济合同编号的前缀
		this.code = "CLHT" + DateUtils.format(new Date(), "yyyyMM");
		return SUCCESS;
	}
}