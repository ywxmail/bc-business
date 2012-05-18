/**
 * 
 */
package cn.bc.business.fee.web.struts2;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.tools.ant.util.DateUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.BCConstants;
import cn.bc.business.OptionConstants;
import cn.bc.business.car.service.CarService;
import cn.bc.business.carman.service.CarManService;
import cn.bc.business.fee.domain.Fee;
import cn.bc.business.fee.domain.FeeDetail;
import cn.bc.business.fee.service.FeeService;
import cn.bc.business.motorcade.domain.Motorcade;
import cn.bc.business.motorcade.service.MotorcadeService;
import cn.bc.business.web.struts2.FileEntityAction;
import cn.bc.identity.web.SystemContext;
import cn.bc.option.domain.OptionItem;
import cn.bc.option.service.OptionService;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.json.Json;
import cn.bc.web.ui.json.JsonArray;

/**
 * 承包费Action
 * 
 * @author wis
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class FeeAction extends FileEntityAction<Long, Fee> {
	// private static Log logger = LogFactory.getLog(BulletinAction.class);
	private static final long serialVersionUID = 1L;

	public Long carManId;
	public Long carId;
	
	public List<Map<String, String>> motorcadeList; // 可选车队列表
	public List<Map<String, String>> feeMonths; //可选费用月份
	public Map<String,String> statusesValue; //状态常数转换
	public JSONArray feeNames; // 承包费明细名称列表
	public String feeDetails;// 承包费本期实收明细的json字符串
	public Set<FeeDetail> b4feeOweDetail;// 前期欠费明细
	public String json;
	
	public FeeService feeService;
	public CarManService carManService;
	public CarService carService;
	private OptionService optionService;
	private MotorcadeService motorcadeService;

	@Autowired
	public void setMotorcadeService(MotorcadeService motorcadeService) {
		this.motorcadeService = motorcadeService;
	}

	@Autowired
	public void setCarManService(CarManService carManService) {
		this.carManService = carManService;
	}

	@Autowired
	public void CarService(CarService carService) {
		this.carService = carService;
	}

	@Autowired
	public void setFeeService(FeeService feeService) {
		this.feeService = feeService;
		this.setCrudService(feeService);
	}

	@Autowired
	public void setOptionService(OptionService optionService) {
		this.optionService = optionService;
	}

	@Override
	public boolean isReadonly() {
		// 承包费管理或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.fee.manage"),
				getText("key.role.bc.admin"));
	}

	@Override
	protected void afterCreate(Fee entity) {
		super.afterCreate(entity);
		entity.setUid(this.getIdGeneratorService().next(Fee.ATTACH_TYPE));
		entity.setStatus(BCConstants.STATUS_ENABLED);
		entity.setCompany("宝城");
		entity.setFeeYear(Integer.valueOf(DateUtils.format(new Date(), "yyyy").toString()));
		entity.setFeeMonth(Integer.valueOf(DateUtils.format(new Date(), "MM").toString()));
		//SystemContext context = this.getSystyemContext();

	}
	
	//## 前期欠费计算的变量   ##//
	private float b4oweSubtotal; //前期欠费小计
	private float b4oweUpkeep; //前期欠费维修费
	private float b4oweTotal; //前期欠费合计

	public float getB4oweSubtotal() {
		return b4oweSubtotal;
	}

	public void setB4oweSubtotal(float b4oweSubtotal) {
		this.b4oweSubtotal = b4oweSubtotal;
	}

	public float getB4oweUpkeep() {
		return b4oweUpkeep;
	}

	public void setB4oweUpkeep(float b4oweUpkeep) {
		this.b4oweUpkeep = b4oweUpkeep;
	}

	public float getB4oweTotal() {
		return b4oweTotal;
	}

	public void setB4oweTotal(float b4oweTotal) {
		this.b4oweTotal = b4oweTotal;
	}

	@Override
	protected void afterEdit(Fee entity) {
		super.afterEdit(entity);
		//获取上期fee对象(根据本期的carId和本期的收费年月)
		Fee b4Fee = this.feeService.findb4FeeByCarIdANDYearAndMonth(
				entity.getCarId(), entity.getFeeYear(),entity.getFeeMonth());
		
		if(null != b4Fee){
			//前期欠费小计
			this.b4oweSubtotal = b4Fee.getOweSubtotal();
			//前期欠费维修费
			this.b4oweUpkeep = b4Fee.getOweUpkeep();
			//前期欠费合计
			this.b4oweTotal = b4Fee.getOweTotal();
			
			Set<FeeDetail> feeDetail = b4Fee.getFeeDetail();
			if(null != feeDetail && feeDetail.size() > 0){
				this.b4feeOweDetail = new LinkedHashSet<FeeDetail>();
				for(FeeDetail obj : feeDetail){
					if(FeeDetail.TYPE_OWE == obj.getFeeType()){ 
						this.b4feeOweDetail.add(obj); //欠费的明细用于前台显示
					}
				}
			}
			
		}
	}

	@Override
	protected void buildFormPageButtons(PageOption pageOption, boolean editable) {
		boolean readonly = this.isReadonly();
		if (editable && !readonly) {
			// 添加默认的保存按钮
			pageOption.addButton(new ButtonOption(getText("label.save"), null,
					"bs.feeForm.save"));
		}
	}

	// 设置页面的尺寸
	@Override
	protected PageOption buildFormPageOption(boolean editable) {
		return super.buildFormPageOption(editable).setWidth(745).setMinWidth(250).setHeight(500)
				.setMinHeight(200);
	}

	@Override
	protected void beforeSave(Fee entity) {
		super.beforeSave(entity);

		// 插入承包费明细
		try {
			Set<FeeDetail> fees = null;

			if (this.feeDetails != null && this.feeDetails.length() > 0) {
				fees = new LinkedHashSet<FeeDetail>();
				FeeDetail resource;
				JSONArray jsons = new JSONArray(this.feeDetails);
				JSONObject json;
				for (int i = 0; i < jsons.length(); i++) {
					json = jsons.getJSONObject(i);
					resource = new FeeDetail();
					if (json.has("id"))
						resource.setId(json.getLong("id"));
					resource.setFee(this.getE());
					resource.setFeeName(json.getString("feeName"));
					resource.setCharge(Float.parseFloat(json.getString("charge")));
					resource.setFeeDescription(json.getString("feeDescription"));
					resource.setFeeType(Integer.parseInt(json.getString("feeType")));
					
					fees.add(resource);

				}
			}
			if (this.getE().getFeeDetail() != null) {
				this.getE().getFeeDetail().clear();
				this.getE().getFeeDetail().addAll(fees);
			} else {
				this.getE().setFeeDetail(fees);
			}
		} catch (JSONException jsonE) {
			logger.error(jsonE.getMessage(), jsonE);
			try {
				throw jsonE;
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	@Override
	public String save() throws Exception {
		Json info = new Json();
		Fee e = this.getE();
		Long excludeId = null;
		//保存之前检测此车辆是否存在本年本月的承包费用
		excludeId = this.feeService.checkFeeIsExist(e.getId(), e.getCarId(),
				e.getFeeYear(), e.getFeeMonth());
		if(excludeId != null){
			info.put("success", false);
			info.put("msg", getText("此车辆已存在"+e.getFeeYear()+"年"+e.getFeeMonth()+"月的承包费用"));
		} else {
			// 执行基类的保存
			this.beforeSave(e);

			// 设置最后更新人的信息
			SystemContext context = this.getSystyemContext();
			e.setModifier(context.getUserHistory());
			e.setModifiedDate(Calendar.getInstance());
			
			this.feeService.save(e);
			
			this.afterSave(e);
			
			info.put("id", e.getId());
			info.put("success", true);
			info.put("msg", getText("form.save.success"));
		}
		json = info.toString();
		return "json";
	}

	@Override
	protected void initForm(boolean editable) throws Exception {
		super.initForm(editable);
		
		statusesValue =	this.getFeeStatuses(); //状态常数装换
		
		// 加载可选车队列表
		this.motorcadeList = this.motorcadeService.findEnabled4Option();
		if (this.getE().getMotorcadeId() != null) {
			Motorcade m = this.motorcadeService.load(this.getE()
					.getMotorcadeId());
			if (m != null) {
				OptionItem.insertIfNotExist(this.motorcadeList, m.getId()
						.toString(), m.getName());
			}
		}
		// 批量加载可选项列表
		Map<String, List<Map<String, String>>> optionItems = this.optionService
				.findOptionItemByGroupKeys(new String[] { OptionConstants.FEE_MONTH });

		// 缴费月
		feeMonths = optionItems.get(OptionConstants.FEE_MONTH);
		
		// 证件名称列表
		this.feeNames = OptionItem.toLabelValues(optionItems
				.get(OptionConstants.FEE_NAME));
	}

	//根据车辆,收费年,月查找上期承包费欠费明细
	private Long searchCarId;
	private Integer feeYear;
	private Integer feeMonth;
	
	public Long getSearchCarId() {
		return searchCarId;
	}

	public void setSearchCarId(Long searchCarId) {
		this.searchCarId = searchCarId;
	}

	public Integer getFeeYear() {
		return feeYear;
	}

	public void setFeeYear(Integer feeYear) {
		this.feeYear = feeYear;
	}

	public Integer getFeeMonth() {
		return feeMonth;
	}

	public void setFeeMonth(Integer feeMonth) {
		this.feeMonth = feeMonth;
	}

	public String selectFeeb4OweDetail(){
		
		Long excludeId = null;
		//保存之前检测此车辆是否存在本年本月的承包费用
		excludeId = this.feeService.checkFeeIsExist(null, this.searchCarId,
				this.feeYear, this.feeMonth);
		if(excludeId != null){
			Json info = new Json();
			info.put("success", true);
			info.put("msg", getText("此车辆已存在"+this.feeYear+"年"+this.feeMonth+"月的承包费用"));
			json = info.toString();
		}else{
			Fee b4Fee = this.feeService.findb4FeeByCarIdANDYearAndMonth(
					this.searchCarId, this.feeYear,this.feeMonth);
			JsonArray jsons = new JsonArray();
			Json o;
			if(null != b4Fee){
				Set<FeeDetail> b4feeDetails = b4Fee.getFeeOweDetail();	

				for(FeeDetail detail : b4feeDetails){
					o = new Json();
					o.put("id", detail.getId());
					o.put("feeName", detail.getFeeName());
					o.put("charge", detail.getCharge());
					o.put("feeDescription", detail.getFeeDescription());
					o.put("feeType", detail.getFeeType());
					jsons.add(o);
				}
			}
			json = jsons.toString();
		}
		return "json";
	}
	
	
	/**
	 * 状态值转换列表：在案|注销|全部
	 * 
	 * @return
	 */
	protected Map<String, String> getFeeStatuses() {
		Map<String, String> statuses = new LinkedHashMap<String, String>();
		statuses.put(String.valueOf(BCConstants.STATUS_ENABLED),
				getText("fee.status.active"));
		statuses.put(String.valueOf(BCConstants.STATUS_DISABLED),
				getText("fee.status.logout"));
		statuses.put("", getText("fee.status.all"));
		return statuses;
	}
}
