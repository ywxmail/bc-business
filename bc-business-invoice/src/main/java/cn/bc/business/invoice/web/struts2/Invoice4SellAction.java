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
	public String readType;//查询类型，1-发票销售，2-发票退票，3，发票查询,默认null为发票销售

	public Long buyerId;
	public Long carId;

	public List<Map<String, String>> companyList; // 所属公司列表（宝城、广发）
	public List<Map<String, String>> motorcadeList; // 可选车队列表
	public List<Map<String, String>> typeList; // 发票类型列表（打印票、手撕票）
	public List<Map<String, String>> unitList; // 发票单位列表（卷、本）
	// public List<Map<String, String>> payTypeList; // 付款方式列表（现金、银行卡）
	public List<Map<String, String>> codeList;

	public boolean isMoreCar; // 是否存在多辆车
	public boolean isMoreBuyer; // 是否存在多个购买人
	public boolean isNullCar; // 此司机没有车
	public boolean isNullBuyer; // 此车没有购买人

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

	@Override
	public boolean isReadonly() {
		// 票务管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.invoice"),
				getText("key.role.bs.invoice4sell"),
				getText("key.role.bc.admin"));
	}

	@Override
	protected PageOption buildFormPageOption(boolean editable) {
		return super.buildFormPageOption(editable).setWidth(770)
				.setMinWidth(250).setMinHeight(200);
	}

	@Override
	protected void afterCreate(Invoice4Sell entity) {
		super.afterCreate(entity);
		entity.setSellDate(Calendar.getInstance());
		// entity.setBuyPrice(7F);
		entity.setStatus(BCConstants.STATUS_ENABLED);
		entity.setPayType(Invoice4Sell.PAY_TYPE_CASH);
		entity.setCashierId(entity.getAuthor());
		
		//查看状态为退票时
		if(readType!=null&&readType.equals(Invoice4Sell.READ_TYPE_REFUND)){
			entity.setType(Invoice4Sell.TYPE_REFUND);
			this.codeList = this.invoice4BuyService.findEnabled4Option();
		}else{
			entity.setType(Invoice4Sell.TYPE_SELL);
			// 发票代码
			this.codeList = this.invoice4BuyService.findEnabled4Option();
		}
	}

	@Override
	protected void afterEdit(Invoice4Sell entity) {
		super.afterEdit(entity);
		Set<Invoice4SellDetail> isd4set = entity.getInvoice4SellDetail();

		//查看状态为退票时
		if(readType!=null&&readType.equals(Invoice4Sell.READ_TYPE_REFUND)){
			// 发票代码
			this.codeList = this.invoice4BuyService.findEnabled4Option();
			// 遍历销售单对应的set集合，查出每个集合对应的采购
			for (Invoice4SellDetail isd : isd4set) {
				OptionItem.insertIfNotExist(codeList, isd.getBuyId().toString(), 
				this.invoice4BuyService.findOneInvoice4Buy(isd.getBuyId()).get(0).get("value"));
			}
		}else{
			// 发票代码
			this.codeList = this.invoice4BuyService.findEnabled4Option();
			// 遍历销售单对应的set集合，查出每个集合对应的采购
			for (Invoice4SellDetail isd : isd4set) {
				OptionItem.insertIfNotExist(codeList, isd.getBuyId().toString(), 
				this.invoice4BuyService.findOneInvoice4Buy(isd.getBuyId()).get(0).get("value"));
			}
		}
	}

	@Override
	protected void afterOpen(Invoice4Sell entity) {
		super.afterOpen(entity);
		Set<Invoice4SellDetail> isd4set = entity.getInvoice4SellDetail();
		List<Map<String, String>> code4list = new ArrayList<Map<String, String>>();
		// 遍历销售单对应的set集合，查出每个集合对应的采购
		for (Invoice4SellDetail isd : isd4set) {
			for (Map<String, String> map : this.invoice4BuyService
					.findOneInvoice4Buy(isd.getBuyId())) {
				code4list.add(map);
			}
		}
		// 发票代码
		this.codeList = code4list;
	}

	public String sellDetails;// 销售明细字符串JSON格式

	@Override
	protected void beforeSave(Invoice4Sell entity) {
		super.beforeSave(entity);

		// 销售明细集合
		Set<Invoice4SellDetail> details = this.parseSell4DetailString(
				this.sellDetails, entity.getStatus() ,entity.getType());

		// 集合放进对象中
		if (this.getE().getInvoice4SellDetail() != null
				&& this.getE().getInvoice4SellDetail().size() > 0) {
			this.getE().getInvoice4SellDetail().clear();
			this.getE().getInvoice4SellDetail().addAll(details);
		} else {
			this.getE().setInvoice4SellDetail(details);
		}

	}

	
	
	@Override
	protected String getPageTitle() {
		// TODO Auto-generated method stub
		return super.getPageTitle();
	}

	// 解析销售明细字符串返回销售明细集合
	private LinkedHashSet<Invoice4SellDetail> parseSell4DetailString(
			String detailStr, int status,int type) {
		try {
			// 销售明细集合
			LinkedHashSet<Invoice4SellDetail> details = null;
			if (detailStr != null && detailStr.length() > 0) {
				details = new LinkedHashSet<Invoice4SellDetail>();
				Invoice4SellDetail resDetails;
				JSONArray jsonArray = new JSONArray(detailStr);
				JSONObject json;
				for (int i = 0; i < jsonArray.length(); i++) {
					json = jsonArray.getJSONObject(i);
					resDetails = new Invoice4SellDetail();
					if (json.has("id"))
						resDetails.setId(json.getLong("id"));
					resDetails.setInvoice4Sell(this.getE());
					resDetails.setStatus(status);
					resDetails.setType(type);
					resDetails.setBuyId(Long.parseLong(json.getString("buyId")
							.trim()));
					resDetails.setStartNo(json.getString("startNo").trim());
					resDetails.setEndNo(json.getString("endNo").trim());
					resDetails.setCount(Integer.parseInt(json
							.getString("count").trim()));
					resDetails.setPrice(Float.parseFloat(json
							.getString("price").trim()));
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
		if (this.getE().getMotorcadeId() != null) {
			OptionItem.insertIfNotExist(this.motorcadeList, this.getE()
					.getMotorcadeId().getId().toString(), this.getE()
					.getMotorcadeId().getName());
		}

		// 批量加载可选项列表
		Map<String, List<Map<String, String>>> optionItems = this.optionService
				.findOptionItemByGroupKeys(new String[] { OptionConstants.CAR_COMPANY });

		// 所属公司列表
		this.companyList = optionItems.get(OptionConstants.CAR_COMPANY);
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
		//带权限且在查看状态为销售和退票时
		if (!this.isReadonly()
				&&(readType==null
					||readType.equals(Invoice4Sell.READ_TYPE_SELL)
						||readType.equals(Invoice4Sell.READ_TYPE_REFUND))){
			if (editable) {// 可编辑时显示保存按钮
				pageOption.addButton(new ButtonOption(getText("label.save"),
						null, "bs.invoice4SellForm.save")
						.setId("invoice4SellSave"));
				pageOption.addButton(new ButtonOption(getText("invoice.saveAndClose"), 
						null,"bs.invoice4SellForm.saveAndClose")
						.setId("invoice4SellSave"));
			} else {// open时
				if (this.getE().getStatus() == Invoice4Sell.STATUS_NORMAL) {
					// 维护
					pageOption.addButton(new ButtonOption(
							getText("invoice.optype.edit"), null,
							"bs.invoice4SellForm.doMaintenance")
							.setId("invoice4SellEdit"));
				}
			}
		}
	}

	// 自动加载采购单发票代码信息
	public String autoLoadInvoice4BuyCode() {
		JsonArray jsonArray = new JsonArray();
		Json json = null;
		List<Map<String, String>> codeListMap = this.invoice4BuyService
				.findEnabled4Option();
		for (Map<String, String> codMap : codeListMap) {
			if (codMap != null) {
				json = new Json();
				json.put("key", codMap.get("key").trim());
				json.put("value", codMap.get("value").trim());
				jsonArray.add(json);
			}
		}
		this.json = jsonArray.toString();
		return "json";
	}

	public Long buyId;// 采购单ID

	// 根据采购单ID查找一个采购单
	public String findOneInvoice4Buy() {
		Json json = null;
		if (this.buyId != null) {
			json = new Json();
			List<Map<String, String>> list = this.invoice4SellService
					.selectListSellDetailByCode(this.buyId);
			if (list != null && list.size() > 0) {
				json.put("sellPrice", list.get(0).get("sellPrice"));
				json.put("eachCount", list.get(0).get("eachCount"));// 每份数量
				// 取此结束号
				String str = list.get(0).get("endNo4Sell").trim();
				// 此结束号+1
				Long l = Long.parseLong(str);
				l += 1;
				String strLong = String.valueOf(l);
				if (strLong.length() >= str.length()) {
					json.put("startNo", strLong);
				} else {
					// 取此结束号+1作为开始号 //处理0开头的字符串
					json.put("startNo",
							str.substring(0, (str.length() - strLong.length()))
									+ strLong);
				}
				this.json = json.toString();
			} else {// list为空，此采购单还有销售记录
				Invoice4Buy i4buy = this.invoice4BuyService.load(this.buyId);
				json.put("sellPrice", i4buy.getSellPrice());
				json.put("eachCount", i4buy.getEachCount());// 每份数量
				json.put("startNo", i4buy.getStartNo());
				this.json = json.toString();
			}
		}
		return "json";
	}

	// ----------验证销售明细的正确性---------开始----
	public Long sellId;
	public int status;
	public String sellDetailsStr;

	public String checkSell4Detail() {
		Json json = new Json();
		// 作废销售单不需要进入检测
		if (this.status == Invoice4Sell.STATUS_NORMAL) {
			LinkedHashSet<Invoice4SellDetail> details = this
					.parseSell4DetailString(this.sellDetailsStr, 0,1);
			// 检查结果：checkResult
			String jsonStr = this.checkDetailItself(
					this.setChangeList4detail(details), json);
			// 自身验证正常时进入数据库信息验证
			if (jsonStr.equals("")) {
				String reslt = this.checkDetailScope(details, this.sellId, json);
				if(reslt.equals("")){
					this.json=json.toString();
				}else{
					this.json=reslt;
				}
			} else {
				this.json = jsonStr;
			}
		} else {
			this.json = json.toString();
		}
		return "json";
	}

	// 将set集合转为List集合
	private List<Invoice4SellDetail> setChangeList4detail(
			LinkedHashSet<Invoice4SellDetail> details) {
		List<Invoice4SellDetail> detailList = new ArrayList<Invoice4SellDetail>();
		for (Invoice4SellDetail detail : details) {
			detailList.add(detail);
		}
		return detailList;
	}

	// 先检测来自页面请求的明细本身是否带有同一code的编码范围是否有重叠
	private String checkDetailItself(List<Invoice4SellDetail> dlist, Json json) {
		// 比较开始 抽取每一个对象和本身集合中的每一个对象逐个比较
		for (int i = 0; i < dlist.size(); i++) {
			// 开始号必须要小于结束号,字符串长度必须相等
			if (dlist.get(i).getStartNo().trim().length() == dlist.get(i)
					.getEndNo().trim().length()
					&& Long.parseLong(dlist.get(i).getStartNo().trim()) < Long
							.parseLong(dlist.get(i).getEndNo().trim())) {
				for (int j = 0; j < dlist.size(); j++) {
					// 当i=j时，即为对象本身 不需要比较，所需要比较i!=j的情况
					if (i != j) {
						// 开始号必须要小于结束号
						if (Long.parseLong(dlist.get(j).getStartNo().trim()) < Long
								.parseLong(dlist.get(j).getEndNo().trim())) {
							// 采购单ID相同时进行比较，要保证不出现开始结束号得范围不能够重叠
							if (dlist.get(i).getBuyId()
									.equals(dlist.get(j).getBuyId())) {
								// 出现重叠的情况
								if (!(Long.parseLong(dlist.get(i).getEndNo()
										.trim()) < Long.parseLong(dlist.get(j)
										.getStartNo().trim()) || Long
										.parseLong(dlist.get(i).getStartNo()
												.trim()) > Long.parseLong(dlist
										.get(j).getEndNo().trim()))) {
									json.put(
											"checkResult",
											getText("invoice4Sell.detail.tips3"));
									return json.toString();
								}
							}
						} else {
							json.put("checkResult",
									getText("invoice4Sell.detail.tips3"));
							return json.toString();
						}
					}
				}
			} else {
				json.put("checkResult", getText("invoice4Sell.detail.tips3"));
				return json.toString();
			}
		}
		return "";
	}

	// 检测来自页面的明细对象的编码范围是否与系统中的明细对象集合中的范围重叠。
	private String checkDetailScope(LinkedHashSet<Invoice4SellDetail> details,
			Long sellId, Json json) {
		// 遍历每一个对象
		for (Invoice4SellDetail detail : details) {
			// 1.确定一条销售明细的开始和结束号范围在此明细对应的采购单之内
			Invoice4Buy invoice4Buy = this.invoice4BuyService.load(detail
					.getBuyId());
			if (detail.getStartNo().trim().length() == invoice4Buy.getStartNo()
					.trim().length()
					&& detail.getEndNo().trim().length() == invoice4Buy
							.getEndNo().trim().length()
					&& Long.parseLong(detail.getStartNo().trim()) >= Long
							.parseLong(invoice4Buy.getStartNo().trim())
					&& Long.parseLong(detail.getEndNo().trim()) <= Long
							.parseLong(invoice4Buy.getEndNo().trim())) {
				if (sellId == null) {
					List<Map<String, String>> dList = this.invoice4SellService
							.selectSellDetailByCode(detail.getBuyId());
					// 2.比对数据库中的销售明细信息 //通过每一个对象的buyID 相应查找到
					for (Map<String, String> findDetail : dList) {
						// 出现重叠的情况
						if (!(Long.parseLong(detail.getEndNo().trim()) < Long
								.parseLong(findDetail.get("startNo").trim()) || Long
								.parseLong(detail.getStartNo().trim()) > Long
								.parseLong(findDetail.get("endNo").trim()))) {
							Json jr=new Json();
							jr.put("code",invoice4Buy.getCode());
							jr.put("save_startNo", detail.getStartNo());
							jr.put("save_endNo", detail.getEndNo());
							jr.put("data_startNo", findDetail.get("startNo"));
							jr.put("data_endNo", findDetail.get("endNo"));
							jr.put("data_sellId", findDetail.get("sellId"));
							json.put("checkResult4Sell",jr);
							return json.toString();
						}
					}
				} else {

					List<Map<String, String>> dList = this.invoice4SellService
							.selectSellDetailByCode(detail.getBuyId(), sellId);
					// 3.比对数据库中的销售明细信息，不包括本销售ID的
					for (Map<String, String> findDetail : dList) {
						// 出现重叠的情况
						if (!(Long.parseLong(detail.getEndNo().trim()) < Long
								.parseLong(findDetail.get("startNo").trim()) || Long
								.parseLong(detail.getStartNo().trim()) > Long
								.parseLong(findDetail.get("endNo").trim()))) {
							Json jr=new Json();
							jr.put("code",invoice4Buy.getCode());
							jr.put("save_startNo", detail.getStartNo());
							jr.put("save_endNo", detail.getEndNo());
							jr.put("data_startNo", findDetail.get("startNo"));
							jr.put("data_endNo", findDetail.get("endNo"));
							jr.put("data_sellId", findDetail.get("sellId"));
							json.put("checkResult4Sell",jr);
							return json.toString();
						}
					}
				}
			} else {
				Json jr=new Json();
				jr.put("code",invoice4Buy.getCode());
				jr.put("save_startNo", detail.getStartNo());
				jr.put("save_endNo", detail.getEndNo());
				jr.put("data_startNo", invoice4Buy.getStartNo());
				jr.put("data_endNo", invoice4Buy.getEndNo());
				jr.put("data_buyId", invoice4Buy.getId());
				json.put("checkResult4Buy",jr);
				return json.toString();
			}
		}
		return "";
	}

	// ----------验证销售明细的正确性---------结束----
}