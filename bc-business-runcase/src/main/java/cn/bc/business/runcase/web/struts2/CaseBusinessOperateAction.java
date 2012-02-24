/**
 * 
 */
package cn.bc.business.runcase.web.struts2;

import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.runcase.domain.Case4InfractBusiness;
import cn.bc.business.runcase.service.CaseBusinessService;
import cn.bc.web.ui.json.Json;

import com.opensymphony.xwork2.ActionSupport;

/**
 * 营运违章相关操作的Action
 * 
 * @author wis
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class CaseBusinessOperateAction extends ActionSupport {
	private static final long serialVersionUID = 1L;
	private CaseBusinessService caseBusinessService;

	@Autowired
	public void setCaseBusinessService(CaseBusinessService caseBusinessService) {
		this.caseBusinessService = caseBusinessService;
	}
	
	private Long id;



	public Json json;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	// ========结案代码开始========
	private Calendar closeDate;
	
	public Calendar getCloseDate() {
		return closeDate;
	}

	public void setCloseDate(Calendar closeDate) {
		this.closeDate = closeDate;
	}

	/**
	 * 营运违章结案操作
	 */
	public String doCloseFile() throws Exception {
		Long fromBusinessId = this.getId();
		Case4InfractBusiness business  = this.caseBusinessService.doCloseFile(
				fromBusinessId, this.closeDate);
		json = new Json();
		json.put("id", business.getId());
		json.put("oldId", fromBusinessId);
		json.put("msg", "结案成功！");
		return "json";
	}

	// ========结案代码结束========
}