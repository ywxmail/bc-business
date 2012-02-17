/**
 * 
 */
package cn.bc.business.mix.web.struts2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.mix.service.InfoCenterService;
import cn.bc.web.ui.html.page.PageOption;

import com.opensymphony.xwork2.ActionSupport;

/**
 * 信息中心综合查询Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class InfoCenterAction extends ActionSupport {
	private static final long serialVersionUID = 1L;
	private InfoCenterService infoCenterService;
	public PageOption pageOption;

	@Override
	public String execute() throws Exception {
		// 初始化页面配置信息
		pageOption = new PageOption().setMaximizable(true).setMinimizable(true)
				.setMinWidth(500).setMaxHeight(300).setHeight(400)
				.setWidth(700);

		// 返回综合查询页面
		return super.execute();
	}

	@Autowired
	public void setInfoCenterService(InfoCenterService infoCenterService) {
		this.infoCenterService = infoCenterService;
	}
}
