/**
 * 
 */
package cn.bc.business.spider.web.struts2;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.spider.Spider4GzTaxiXhDriverInfo;
import cn.bc.business.spider.domain.GzTaxiXhDriverInfo;
import cn.bc.core.util.DateUtils;
import cn.bc.web.spider.Spider;
import cn.bc.web.ui.html.page.PageOption;

import com.opensymphony.xwork2.ActionSupport;

/**
 * 广州市出租汽车协会网Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class GzTaxiXhAction extends ActionSupport {
	protected static final Log logger = LogFactory.getLog(GzTaxiXhAction.class);
	private static final long serialVersionUID = 1L;
	public PageOption pageOption;
	public String json;// ajax返回的json信息
	public String v;// 服务资格证号

	public String driverInfo() throws Exception {
		// 初始化页面配置信息
		this.pageOption = new PageOption().setMaximizable(true)
				.setMinimizable(true).setMinWidth(300).setWidth(650)
				.setMinHeight(200).setHeight(350);
		// this.pageOption.setHelp("cheliangchaxun");// 添加帮助按钮

		return SUCCESS;
	}

	public String findDriverInfo() throws Exception {
		JSONObject json = new JSONObject();
		Date start = new Date();
		String type = "服务资格证";
		String userName = "bch";
		String userPassword = "baocheng123";
		String rootPath = getText("app.data.realPath");

		try {
			Spider<GzTaxiXhDriverInfo> spider = new Spider4GzTaxiXhDriverInfo(
					type, this.v, userName, userPassword, rootPath);
			GzTaxiXhDriverInfo info = spider.excute();

			json.put("success", true);
			json.put("msg", info.getMsg());
			json.put("simple", info.getSimple());
			json.put("detail", info.getDetail());
			json.put("pic", info.getPic());
		} catch (Exception e) {
			json.put("success", false);
			json.put("msg", e.getMessage());
		}
		json.put("waste", DateUtils.getWasteTime(start));

		this.json = json.toString();
		return "json";
	}
}
