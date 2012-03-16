/**
 * 
 */
package cn.bc.business.contract.web.struts2;

import java.util.Calendar;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.opensymphony.xwork2.ActionSupport;

/**
 * 选择日期的Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class SelectTwoDateAction extends ActionSupport {
	private static final long serialVersionUID = 1L;

	public String title = getText("title.selectDate");// 对话框标题
	private Calendar resignDate;//离职日期 yyyy-MM-dd
	private Calendar stopDate;//停保日期 yyyy-MM-dd
	
	public Calendar getResignDate() {
		return resignDate;
	}
	public void setResignDate(Calendar resignDate) {
		this.resignDate = resignDate;
	}
	public Calendar getStopDate() {
		return stopDate;
	}
	public void setStopDate(Calendar stopDate) {
		this.stopDate = stopDate;
	}

	@Override
	public String execute() throws Exception {
		return SUCCESS;
	}
}