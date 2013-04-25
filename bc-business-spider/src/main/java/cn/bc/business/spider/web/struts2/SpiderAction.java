/**
 * 
 */
package cn.bc.business.spider.web.struts2;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.spider.service.SpiderService;
import cn.bc.web.ui.html.page.PageOption;

import com.opensymphony.xwork2.ActionSupport;

/**
 * 网络抓取通用Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class SpiderAction extends ActionSupport {
	protected static final Log logger = LogFactory.getLog(SpiderAction.class);
	private static final long serialVersionUID = 1L;
	public String uid;
	public String code;
	public JSONObject json;
	public PageOption pageOption;
	private SpiderService spiderService;

	@Autowired
	public void setSpiderService(SpiderService spiderService) {
		this.spiderService = spiderService;
	}

	// 配置页
	@Override
	public String execute() throws Exception {
		json = new JSONObject();

		// 页面参数配置
		pageOption = new PageOption().setWidth(600).setHeight(400)
				.setModal(false).setMinWidth(200).setMinHeight(200);

		// 抓取处理
		try {
			JSONObject config = new JSONObject();
			if (uid == null)
				config.put("uid", uid);
			uid = spiderService.execute(code, config);
			json.put("success", true);

			test();
		} catch (Exception e) {
			json.put("success", false);
			json.put("msg", e.getMessage());
		}
		return SUCCESS;
	}

	private void test() {
	}
}
