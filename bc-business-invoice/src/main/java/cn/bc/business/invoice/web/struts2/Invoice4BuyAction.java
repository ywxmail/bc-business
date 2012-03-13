/**
 * 
 */
package cn.bc.business.invoice.web.struts2;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.BCConstants;
import cn.bc.business.OptionConstants;
import cn.bc.business.invoice.domain.Invoice4Buy;
import cn.bc.business.invoice.service.Invoice4BuyService;
import cn.bc.business.web.struts2.FileEntityAction;
import cn.bc.identity.web.SystemContext;
import cn.bc.option.domain.OptionItem;
import cn.bc.option.service.OptionService;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.json.Json;

/**
 * 票务采购Action
 * 
 * @author wis
 * 
 */

@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class Invoice4BuyAction extends FileEntityAction<Long, Invoice4Buy> {
	// private static Log logger = LogFactory.getLog(CarAction.class);
	private static final long serialVersionUID = 1L;
	private Invoice4BuyService invoice4BuyService;
	private OptionService optionService;

	public List<Map<String, String>> companyList; // 所属公司列表（宝城、广发）
	public List<Map<String, String>> typeList; // 发票类型列表（打印票、手撕票）
	public List<Map<String, String>> unitList; // 发票单位列表（卷、本）

	public Float amount;// 合计

	@Autowired
	public void setInvoice4BuyService(Invoice4BuyService invoice4BuyService) {
		this.setCrudService(invoice4BuyService);
		this.invoice4BuyService = invoice4BuyService;
	}

	@Autowired
	public void setOptionService(OptionService optionService) {
		this.optionService = optionService;
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
		return super.buildFormPageOption(editable).setWidth(550)
				.setMinWidth(250).setMinHeight(200);
	}

	@Override
	protected void afterCreate(Invoice4Buy entity) {
		super.afterCreate(entity);
		entity.setBuyDate(Calendar.getInstance());
		entity.setBuyPrice(7F);
		entity.setStatus(BCConstants.STATUS_ENABLED);
	}

	@Override
	protected void afterEdit(Invoice4Buy entity) {
		// 计算合计
		this.amount = entity.getCount() * entity.getBuyPrice();
		super.afterEdit(entity);
	}

	@Override
	protected void afterOpen(Invoice4Buy entity) {
		// 计算合计
		this.amount = entity.getCount() * entity.getBuyPrice();
		super.afterOpen(entity);

	}

	@Override
	protected void initForm(boolean editable) throws Exception {
		super.initForm(editable);
		// 批量加载可选项列表
		Map<String, List<Map<String, String>>> optionItems = this.optionService
				.findOptionItemByGroupKeys(new String[] { OptionConstants.CAR_COMPANY, });

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
		if (editable) {// 可编辑时显示保存按钮
			pageOption.addButton(new ButtonOption(getText("label.save"), null,
					"bs.invoice4BuyForm.save").setId("invoice4BuySave"));
		} else {// open时
			if (this.getE().getStatus() == Invoice4Buy.STATUS_NORMAL) {
				// 维护
				pageOption.addButton(new ButtonOption(
						getText("invoice.optype.edit"), null,
						"bs.invoice4BuyForm.doMaintenance")
						.setId("invoice4BuyEdit"));
			}

		}
	}

	// ---检查相同发票代码的采购开始号和结束号是否出现在已保存的采购编码范围内---开始--
	public String code;// 发票代码
	public String startNo;
	public String endNo;
	public Long id4Buy;// 采购单Id

	public String checkSameCode4StartNoAndEndNo() {
		Json json = new Json();
		List<Invoice4Buy> bList = null;
		if (id4Buy != null) {// 编辑
			bList = invoice4BuyService.selectInvoice4BuyByCode(code, id4Buy);
		} else {// 新建
			bList = invoice4BuyService.selectInvoice4BuyByCode(code);
		}
		if (bList == null || bList.size() == 0) {// 没有这钟发票代码的采购单
			// 检查结果：checkResult 0表示没有这钟发票代码的采购单，1表示有且开始号或结束号在其它采购单编码范围内；
			json.put("checkResult", "0");
			this.json = json.toString();
			return "json";
		} else {
			Long startNo4Long = Long.parseLong(startNo.trim());
			Long endNo4Long = Long.parseLong(endNo.trim());
			this.json = this.eachListInvoice4Buy(bList, startNo4Long,
					endNo4Long, json);
			return "json";
		}
	}

	// 遍历采购单集合，检查相同发票代码的采购单开始号和结束号是否出现在已保存的采购编码范围内
	private String eachListInvoice4Buy(List<Invoice4Buy> bList, Long startNo,
			Long endNo, Json json) {
		for (int i = 0; i < bList.size(); i++) {
			Invoice4Buy i4Buy = bList.get(i);
			if (endNo < Long.parseLong(i4Buy.getStartNo().trim())) {
				json.put("checkResult", "0");
				break;
			} else if (bList.size()==1
					&& endNo > Long.parseLong(i4Buy.getEndNo().trim())
					&& startNo > Long.parseLong(i4Buy.getEndNo().trim())) {// 遍历到集合的最后一个对象时
				json.put("checkResult", "0");
				break;
			} else if (endNo > Long.parseLong(i4Buy.getEndNo().trim())
					&& startNo > Long.parseLong(i4Buy.getEndNo().trim())) {	
				bList.remove(0);//比较完第一个对象，下次再比较时已不需要再比较这个对象
				eachListInvoice4Buy(bList, startNo, endNo, json);//继续递归
			} else {
				json.put("checkResult", "1");
				break;
			}
		}

		return json.toString();
	}
	// ---检查相同发票代码的采购开始号和结束号是否出现在已保存的采购编码范围内---结束--
}