/**
 * 
 */
package cn.bc.business.info.web.struts2;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.info.domain.Info;
import cn.bc.identity.web.SystemContext;

/**
 * 公司文件视图Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class CompanyFilesAction extends InfosAction {
	private static final long serialVersionUID = 1L;

	@Override
	protected String getFormActionName() {
		return "companyFile";
	}

	@Override
	protected int getType() {
		return Info.TYPE_COMPANYGILE;
	}

	@Override
	public boolean isReadonly() {
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole("BS_COMPANYFILE_MANAGE", "BC_ADMIN");
	}
}