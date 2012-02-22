/**
 * 
 */
package cn.bc.business.runcase.web.struts2;

import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.runcase.domain.Case4Advice;
import cn.bc.business.runcase.service.CaseAdviceService;
import cn.bc.web.ui.json.Json;

import com.opensymphony.xwork2.ActionSupport;

/**
 * 投诉相关操作的Action
 * 
 * @author wis
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class CaseAdviceOperateAction extends ActionSupport {
	private static final long serialVersionUID = 1L;
	private CaseAdviceService caseAdviceService;

	@Autowired
	public void setCaseAdviceService(CaseAdviceService caseAdviceService) {
		this.caseAdviceService = caseAdviceService;
	}

	private Long id;
	public Json json;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	// ========投诉核准代码开始========
	private Long handlerId;
	private String handlerName;
	private Calendar handleDate;
	private String handleOpinion;
	
	public Long getHandlerId() {
		return handlerId;
	}

	public void setHandlerId(Long handlerId) {
		this.handlerId = handlerId;
	}

	public String getHandlerName() {
		return handlerName;
	}

	public void setHandlerName(String handlerName) {
		this.handlerName = handlerName;
	}

	public Calendar getHandleDate() {
		return handleDate;
	}

	public void setHandleDate(Calendar handleDate) {
		this.handleDate = handleDate;
	}

	public String getHandleOpinion() {
		return handleOpinion;
	}

	public void setHandleOpinion(String handleOpinion) {
		this.handleOpinion = handleOpinion;
	}

	/**
	 * 投诉核准操作
	 */
	public String doManage() throws Exception {
		Long fromAdviceId = this.getId();
		Case4Advice advice  = this.caseAdviceService.doManage(
				fromAdviceId, handlerId, handlerName,handleDate,handleOpinion);
		json = new Json();
		json.put("id", advice.getId());
		json.put("oldId", fromAdviceId);
		json.put("msg", getText("runcase.doManage.success"));
		return "json";
	}

	// ========投诉核准代码结束========
}