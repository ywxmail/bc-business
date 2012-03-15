/**
 * 
 */
package cn.bc.business.invoice.web.struts2;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.BCConstants;
import cn.bc.business.OptionConstants;
import cn.bc.business.car.domain.Car;
import cn.bc.business.car.service.CarService;
import cn.bc.business.carman.domain.CarMan;
import cn.bc.business.carman.service.CarManService;
import cn.bc.business.invoice.domain.Invoice4Buy;
import cn.bc.business.invoice.domain.Invoice4Sell;
import cn.bc.business.invoice.domain.Invoice4SellDetail;
import cn.bc.business.invoice.service.Invoice4BuyService;
import cn.bc.business.invoice.service.Invoice4SellService;
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
 * 票务采购Action
 * 
 * @author wis
 * 
 */

@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class Invoice4SellAction extends FileEntityAction<Long, Invoice4Sell> {
	// private static Log logger = LogFactory.getLog(CarAction.class);
	private static final long serialVersionUID = 1L;
	private Invoice4SellService invoice4SellService;
	private Invoice4BuyService invoice4BuyService;
	private OptionService optionService;
	private MotorcadeService motorcadeService;
	private CarService carService;
	private CarManService carManService;
	
	public Long buyerId;
	public Long carId;
	
	public List<Map<String, String>> companyList; // 所属公司列表（宝城、广发）
	public List<Map<String, String>> motorcadeList; // 可选车队列表
	public List<Map<String, String>> typeList; // 发票类型列表（打印票、手撕票）
	public List<Map<String, String>> unitList; // 发票单位列表（卷、本）
	//public List<Map<String, String>> payTypeList; // 付款方式列表（现金、银行卡）
	public List<Map<String,String>>	codeList;
	
	public boolean isMoreCar; // 是否存在多辆车
	public boolean isMoreCarMan; // 是否存在多个司机
	public boolean isNullCar; // 此司机没有车
	public boolean isNullCarMan; // 此车没有司机

	@Autowired
	public void setInvoice4BuyService(Invoice4SellService invoice4SellService) {
		this.setCrudService(invoice4SellService);
		this.invoice4SellService = invoice4SellService;
	}

	@Autowired
	public void setInvoice4BuyService(Invoice4BuyService invoice4BuyService) {
		this.invoice4BuyService = invoice4BuyService;
	}

	@Autowired
	public void setOptionService(OptionService optionService) {
		this.optionService = optionService;
	}
	
	@Autowired
	public void setMotorcadeService(MotorcadeService motorcadeService) {
		this.motorcadeService = motorcadeService;
	}

	@Autowired
	public void setCarService(CarService carService) {
		this.carService = carService;
	}

	@Autowired
	public void setCarManService(CarManService carManService) {
		this.carManService = carManService;
	}

	@Override
	public boolean isReadonly() {
		// 票务采购管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.invoice"),
				getText("key.role.bc.admin"));
	}

	@Override
	protected PageOption buildFormPageOption(boolean editable) {
		return super.buildFormPageOption(editable).setWidth(800)
				.setMinWidth(250).setMinHeight(200);
	}

	@Override
	protected void afterCreate(Invoice4Sell entity) {
		super.afterCreate(entity);
		entity.setSellDate(Calendar.getInstance());
		//entity.setBuyPrice(7F);
		entity.setStatus(BCConstants.STATUS_ENABLED);
		entity.setPayType(Invoice4Sell.PAY_TYPE_CASH);
		
		if (carId != null && buyerId == null) {// 车辆页签中的新建
			Car car=this.carService.load(carId);
			entity.setCarId(this.carId);
			entity.setCarPlate(car.getPlate());
		}else if(carId == null && buyerId != null){// 司机页签中的新建
			CarMan carman=this.carManService.load(buyerId);
			entity.setBuyerId(this.buyerId);
			entity.setBuyerName(carman.getName());
		}
	}
	
	public String sellDetails;//销售明细字符串JSON格式
	
	@Override
	protected void beforeSave(Invoice4Sell entity) {
		super.beforeSave(entity);
	
		//销售明细集合
		Set<Invoice4SellDetail> details=this.parseSell4DetailString(this.sellDetails);

		//集合放进对象中
		if(this.getE().getInvoice4SellDetail()!=null&&this.getE().getInvoice4SellDetail().size()>0){
			this.getE().getInvoice4SellDetail().clear();
			this.getE().getInvoice4SellDetail().addAll(details);
		}else{
			this.getE().setInvoice4SellDetail(details);
		}
			
	}
	
	//解析销售明细字符串返回销售明细集合
	private Set<Invoice4SellDetail> parseSell4DetailString(String detailStr){
		try {
			//销售明细集合
			Set<Invoice4SellDetail> details=null;
			if(detailStr!=null&&detailStr.length()>0){
				details=new LinkedHashSet<Invoice4SellDetail>();
				Invoice4SellDetail resDetails;
				JSONArray jsonArray=new JSONArray(detailStr);
				JSONObject json;
				for (int i = 0; i < jsonArray.length(); i++) {
					json = jsonArray.getJSONObject(i);
					resDetails = new Invoice4SellDetail();
					if (json.has("id"))
						resDetails.setId(json.getLong("id"));
					resDetails.setInvoice4Sell(this.getE());
					resDetails.setBuyId(Long.parseLong(json.getString("buyId").trim()));
					resDetails.setStartNo(json.getString("startNo").trim());
					resDetails.setEndNo(json.getString("endNo").trim());
					resDetails.setCount(Integer.parseInt(json.getString("count").trim()));
					resDetails.setPrice(Float.parseFloat(json.getString("price").trim()));
					details.add(resDetails);
				}
				return details;
			}
		} catch (JSONException e) {
			logger.error(e.getMessage(), e);
			try {
				throw e;
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
		return null;
	}

	@Override
	protected void initForm(boolean editable) throws Exception {
		super.initForm(editable);
		
		// 加载可选车队列表
		this.motorcadeList = this.motorcadeService.findEnabled4Option();
		/*if (this.getE().getMotorcadeId() != null)
			OptionItem.insertIfNotExist(this.motorcadeList, this.getE()
					.getMotorcadeId().toString(), this.getE()
					.getMotorcadeName());
		*/
		// 批量加载可选项列表
		Map<String, List<Map<String, String>>> optionItems = this.optionService
				.findOptionItemByGroupKeys(new String[] {
						OptionConstants.CAR_COMPANY
						});

		// 所属公司列表
		this.companyList =optionItems.get(OptionConstants.CAR_COMPANY);
		OptionItem.insertIfNotExist(companyList, null, getE().getCompany());
		// 发票类型
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		list.add(this.getOptiomItems(String.valueOf(Invoice4Buy.TYPE_PRINT),
				getText("invoice.type.dayinpiao")));
		list.add(this.getOptiomItems(String.valueOf(Invoice4Buy.TYPE_TORE),
				getText("invoice.type.shousipiao")));
		this.typeList = list;

		// 发票单位
		list = new ArrayList<Map<String, String>>();
		list.add(this.getOptiomItems(String.valueOf(Invoice4Buy.UNIT_JUAN),
				getText("invoice.unit.juan")));
		list.add(this.getOptiomItems(String.valueOf(Invoice4Buy.UNIT_BEN),
				getText("invoice.unit.ben")));
		this.unitList = list;	
		
		//发票代码
		this.codeList=this.invoice4BuyService.findEnabled4Option();
	}
	/**
	 * 生成OptiomItem key、value值
	 */
	private Map<String, String> getOptiomItems(String key, String value) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("key", key);
		map.put("value", value);
		return map;
	}
	
	@Override
	protected void buildFormPageButtons(PageOption pageOption, boolean editable) {
		if (editable) {// 可编辑时显示保存按钮
			pageOption.addButton(new ButtonOption(getText("label.save"), null,
					"bs.invoice4SellForm.save").setId("invoice4SellSave"));
		} else {// open时
			if (this.getE().getStatus() == Invoice4Buy.STATUS_NORMAL) {
				// 维护
				pageOption.addButton(new ButtonOption(
						getText("invoice.optype.edit"), null,
						"bs.invoice4SellForm.doMaintenance")
						.setId("invoice4SellEdit"));
			}

		}
	}
	
	//自动加载采购单发票代码信息
	public String autoLoadInvoice4BuyCode(){
		//JSONArray jsonArray=OptionItem.toLabelValues(this.invoice4BuyService.findEnabled4Option());
		JsonArray jsonArray=new JsonArray();
		Json json=null;
		List<Map<String, String>> codeListMap= this.invoice4BuyService.findEnabled4Option();
		for(Map<String,String> codMap:codeListMap){
			json=new Json();
			json.put("key", codMap.get("key").trim());
			json.put("value", codMap.get("value").trim());
			jsonArray.add(json);
		}
		this.json=jsonArray.toString();
		return "json";
	}
	
	//根据采购单的ID自动加载一个采购对象的信息
	public Long i4BuyId;
	
	public String autoLoadInvoice4BuyId(){
		Json json=new Json();
		this.json=json.toString();
		return "json";
	}
	
	
	//检测销售明细的正确性
	public String checkSell4Detail(){
		
		
		return "json";
	}
	
	
}