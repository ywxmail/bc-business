/**
 * 
 */
package cn.bc.business.mix.web.struts2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.mix.service.InfoCenterService;
import cn.bc.business.motorcade.service.MotorcadeService;
import cn.bc.identity.domain.Actor;
import cn.bc.identity.service.ActorService;
import cn.bc.option.domain.OptionItem;
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
	private MotorcadeService motorcadeService;
	private ActorService actorService;
	public JSONArray units;// 分公司的下拉列表信息
	public JSONArray motorcades;// 车队的下拉列表信息
	public JSONArray searchTypes;// 查找类型的下拉列表信息

	public PageOption pageOption;

	@Autowired
	public void setActorService(
			@Qualifier("actorService") ActorService actorService) {
		this.actorService = actorService;
	}

	@Autowired
	public void setMotorcadeService(MotorcadeService motorcadeService) {
		this.motorcadeService = motorcadeService;
	}

	@Autowired
	public void setInfoCenterService(InfoCenterService infoCenterService) {
		this.infoCenterService = infoCenterService;
	}

	@Override
	public String execute() throws Exception {
		// 初始化页面配置信息
		this.pageOption = new PageOption().setMaximizable(true)
				.setMinimizable(true).setMinWidth(500).setWidth(900)
				.setMinHeight(350).setHeight(400);

		// 可选分公司列表
		units = OptionItem.toLabelValues(this.actorService.find4option(
				new Integer[] { Actor.TYPE_UNIT }, (Integer[]) null), "name",
				"id");

		// 可选车队列表
		motorcades = OptionItem.toLabelValues(this.motorcadeService
				.find4Option(null));

		// 可选查找类型列表
		searchTypes = this.getSearchTypes();

		// 返回综合查询页面
		return super.execute();
	}

	/**
	 * 构建查找类型的下拉列表信息
	 * 
	 * @return
	 * @throws JSONException
	 */
	private JSONArray getSearchTypes() throws JSONException {
		JSONArray jsons = new JSONArray();
		jsons.put(getOneSearchTypeJson("车牌", "plate"));
		jsons.put(getOneSearchTypeJson("自编号", "code"));// 车辆的自编号
		jsons.put(getOneSearchTypeJson("车主", "man"));
		jsons.put(getOneSearchTypeJson("服务资格证号", "cert4fwzg"));
		jsons.put(getOneSearchTypeJson("车架号", "vin"));
		jsons.put(getOneSearchTypeJson("发动机号", "engineNo"));
		jsons.put(getOneSearchTypeJson("发票号", "invoiceNo"));// 购置税发票号
		return jsons;
	}

	private JSONObject getOneSearchTypeJson(String label, String value)
			throws JSONException {
		JSONObject json = new JSONObject();
		json.put("label", label);
		json.put("value", value);
		return json;
	}
}
