/**
 * 
 */
package cn.bc.business.spider.web.struts2;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.spider.Spider4GzTaxiXhDriverInfo;
import cn.bc.business.spider.domain.GzTaxiXhDriverInfo;
import cn.bc.core.util.DateUtils;
import cn.bc.option.service.OptionService;
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
	public String value;// 查询的值
	public String type;// 查询类型
	public int index;// 简易列表信息含有多行时默认详细查询对应哪行的，第一条的数据对应值0
	private OptionService optionService;
	public List<String> types; // 可选类型列表

	@Autowired
	public void setOptionService(OptionService optionService) {
		this.optionService = optionService;
	}

	public String driverInfo() throws Exception {
		// 初始化页面配置信息
		this.pageOption = new PageOption().setMaximizable(true)
				.setMinimizable(true).setMinWidth(500).setWidth(650)
				.setMinHeight(200).setHeight(350);
		// this.pageOption.setHelp("cheliangchaxun");// 添加帮助按钮

		// 可选类型:http://www.gztaxixh.com/peccancySearch.aspx
		this.types = new ArrayList<String>();
		this.types.add("服务资格证");
		this.types.add("从业人员资格证");
		this.types.add("姓名");
		if (this.type == null || this.type.length() == 0)
			this.type = "服务资格证";

		return SUCCESS;
	}

	public String findDriverInfo() throws Exception {
		JSONObject json = new JSONObject();
		Date start = new Date();
		if (this.type == null || this.type.length() == 0)
			this.type = "服务资格证";
		String userName = optionService.getItemValue("account",
				"account.gztaxixh.name");// 出租协会帐号
		String userPassword = optionService.getItemValue("account",
				"account.gztaxixh.password");// 出租协会帐号的密码
		String rootPath = getText("app.data.realPath");// 司机图片保存的父目录

		try {
			Spider<GzTaxiXhDriverInfo> spider = new Spider4GzTaxiXhDriverInfo(
					this.type, this.value, userName, userPassword, rootPath,
					index);
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
