/**
 * 
 */
package cn.bc.business.carman.web.struts2;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.opensymphony.xwork2.ActionSupport;

/**
 * 选择迁移类型信息
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class SelectMoveTypeAction extends ActionSupport {
	private static final long serialVersionUID = 1L;

	

	
	public String selectMoveType() throws Exception {
		return "selectMoveTypesdialog";
	}
}
