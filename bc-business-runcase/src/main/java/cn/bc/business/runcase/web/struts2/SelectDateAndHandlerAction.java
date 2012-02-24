/**
 * 
 */
package cn.bc.business.runcase.web.struts2;

import java.util.Calendar;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.opensymphony.xwork2.ActionSupport;

/**
 * 选择核准日期和核准人Action
 * 
 * @author wis
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class SelectDateAndHandlerAction extends ActionSupport {
	private static final long serialVersionUID = 1L;

	public String title = "核准操作";// 对话框标题
	private Calendar handleDate;// 核准日期 yyyy-MM-dd

	public Calendar getHandleDate() {
		return handleDate;
	}

	public void setHandleDate(Calendar handleDate) {
		this.handleDate = handleDate;
	}

	@Override
	public String execute() throws Exception {
		handleDate = Calendar.getInstance();
		return SUCCESS;
	}
}