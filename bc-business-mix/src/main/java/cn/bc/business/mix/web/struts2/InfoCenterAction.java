/**
 * 
 */
package cn.bc.business.mix.web.struts2;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.mix.domain.InfoCenter;
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
	protected static final Log logger = LogFactory
			.getLog(InfoCenterAction.class);
	private static final long serialVersionUID = 1L;
	private InfoCenterService infoCenterService;
	private MotorcadeService motorcadeService;
	private ActorService actorService;
	public JSONArray units;// 分公司的下拉列表信息
	public JSONArray motorcades;// 车队的下拉列表信息
	public JSONArray searchTypes;// 查找类型的下拉列表信息
	public PageOption pageOption;
	public Long unitId;// 分公司id
	public Long motorcadeId;// 车队id
	public String json;// ajax返回的json信息
	public String searchType;// 模糊查询的字段类型
	public String searchText;// 模糊查询的值
	public Long carId;// 车辆的id

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
				.setMinimizable(true).setMinWidth(760).setWidth(880)
				.setMinHeight(380).setHeight(500);
		this.pageOption.setHelp("cheliangchaxun");// 添加帮助按钮
		// this.pageOption.setPrint("bs/infoCenter");//添加打印按钮

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

	// 根据车辆所属分公司或车队获取相应的车辆列表信息
	public String findCarsByUnitAndMotorcade() throws Exception {
		this.json = this.infoCenterService.findCars(unitId, motorcadeId)
				.toString();
		return "json";
	}

	// 根据车辆所属分公司或车队获取相应的车辆列表信息
	public String findCarsBySearchText() throws Exception {
		this.json = this.infoCenterService.findCars(this.searchType,
				this.searchText).toString();
		return "json";
	}

	// 根据车辆id获取详细的综合信息
	public String findCarDetail() throws Exception {
		JSONObject json;
		try {
			json = this.infoCenterService.findCarDetail(carId);
			json.put("success", true);
		} catch (Exception e) {
			json = new JSONObject();
			json.put("success", false);
			json.put("msg", e.getMessage());
			logger.warn(e.getMessage(), e);
		}
		this.json = json.toString();
		return "json";
	}

	/**
	 * 构建查找类型的下拉列表信息
	 * 
	 * @return
	 * @throws JSONException
	 */
	private JSONArray getSearchTypes() throws JSONException {
		JSONArray jsons = new JSONArray();
		jsons.put(getOneSearchTypeJson("车牌", InfoCenter.TYPE_CAR_PLATE));
		jsons.put(getOneSearchTypeJson("管理号", InfoCenter.TYPE_CAR_MANAGENO));
		jsons.put(getOneSearchTypeJson("自编号", InfoCenter.TYPE_CAR_CODE));
		jsons.put(getOneSearchTypeJson("发动机号", InfoCenter.TYPE_CAR_ENGINENO));
		jsons.put(getOneSearchTypeJson("车架号", InfoCenter.TYPE_CAR_VIN));
		// jsons.put(getOneSearchTypeJson("购置税发票号",
		// InfoCenter.TYPE_CAR_INVOICENO));
		jsons.put(getOneSearchTypeJson("服务资格证", InfoCenter.TYPE_MAN_CERT_FWZG));
		jsons.put(getOneSearchTypeJson("姓名", InfoCenter.TYPE_MAN_NAME));
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
