/**
 * 
 */
package cn.bc.business.carman.web.struts2;

import java.util.ArrayList;
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

import cn.bc.business.carman.domain.CarMan;
import cn.bc.business.carman.domain.CarManRisk;
import cn.bc.business.carman.domain.CarManRiskItem;
import cn.bc.business.carman.service.CarManRiskService;
import cn.bc.business.carman.service.CarManService;
import cn.bc.business.web.struts2.FileEntityAction;
import cn.bc.core.exception.CoreException;
import cn.bc.identity.web.SystemContext;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.json.Json;

/**
 * 司机人意险Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class CarManRiskAction extends FileEntityAction<Long, CarManRisk> {
	private static final long serialVersionUID = 1L;

	private CarManService carManService;
	private CarManRiskService carManRiskService;
	
	public String items;//险种json字符串信息
	public String insurants;//被保人 id字符串 多个逗号隔开
	
	@Autowired
	public void setCarManService(CarManService carManService) {
		this.carManService = carManService;
	}

	@Autowired
	public void setCarManRiskService(CarManRiskService carManRiskService) {
		this.carManRiskService = carManRiskService;
		this.setCrudService(carManRiskService);
	}

	@Override
	public boolean isReadonly() {
		// 司机人意险管理员或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.carManRisk"),
				getText("key.role.bc.admin"));
	}

	@Override
	protected CarManRisk createEntity() {
		CarManRisk carManRisk = super.createEntity();
		carManRisk.setBuyType(CarManRisk.BUY_TYPE_NONE);
		return carManRisk;
	}

	@Override
	protected void afterCreate(CarManRisk entity) {
		super.afterCreate(entity);

		// 额外参数的设置
		this.getE().setUid(
				this.getIdGeneratorService().next(CarManRisk.KEY_UID));
	}
	

	@Override
	protected void initForm(boolean editable) throws Exception {
		super.initForm(editable);
	}

	@Override
	protected PageOption buildFormPageOption(boolean editable) {
		return super.buildFormPageOption(editable).setWidth(740)
				.setMinWidth(250).setMinHeight(200).setMaxHeight(600);
	}
	
	

	@Override
	protected void buildFormPageButtons(PageOption pageOption, boolean editable) {
		boolean readonly = this.isReadonly();
		
		if (editable && !readonly) {
			// 添加保存按钮
			pageOption.addButton(new ButtonOption(getText("label.save"), null, "bc.carManRiskForm.save"));
			
			// 添加保存并关闭按钮
			pageOption.addButton(new ButtonOption(getText("label.saveAndClose"), null, "bc.carManRiskForm.saveAndClose"));
		}
	}

	@Override
	public String delete() throws Exception {
		return super.delete();
	}
	
	@Override
	protected void beforeSave(CarManRisk entity) {
		super.beforeSave(entity);
		
		//插入司机
		this.addCarMans();
		
		try {
			//插入险种
			this.addItems();
		} catch (JSONException e) {
			logger.error(e.getMessage(), e);
			try {
				throw e;
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	//插入险种
	private void addItems() throws JSONException{
		Set<CarManRiskItem> items=null;
		if(this.items!=null&&this.items.length()>0){
			items=new LinkedHashSet<CarManRiskItem>();
			JSONArray jsons = new JSONArray(this.items);
			JSONObject json;
			CarManRiskItem item;
			for(int i=0;i<jsons.length();i++){
				json = jsons.getJSONObject(i);
				item=new CarManRiskItem();
				if (json.has("id"))
					item.setId(json.getLong("id"));
				item.setOrderNo(i);
				item.setName(json.getString("name"));
				item.setDescription(json.isNull("description")?null:json.getString("description"));
				item.setPremium(json.isNull("premium")?null:json.getString("premium"));
				item.setCoverage(json.isNull("coverage")?null:json.getString("coverage"));
				item.setParent(this.getE());
				items.add(item);
			}
		}
		if (this.getE().getItems() != null) {
			this.getE().getItems().clear();
			this.getE().getItems().addAll(items);
		} else {
			this.getE().setItems(items);
		}
	}
	
	//插入司机
	private void addCarMans(){
		Set<CarMan> insurants=null;
		if(this.insurants!=null&&this.insurants.length()>0){
			insurants=new LinkedHashSet<CarMan>();
			String[] _ids=this.insurants.split(",");
			CarMan carMan;
			for(String id:_ids){
				carMan=this.carManService.load(Long.valueOf(id));
				insurants.add(carMan);
			}
		}
		
		if (this.getE().getInsurants() != null) {
			this.getE().getInsurants().clear();
			this.getE().getInsurants().addAll(insurants);
		} else {
			this.getE().setInsurants(insurants);
		}
	}
	
	//格式 ：人意险ID1:司机ID1,人意险ID1:司机ID2,人意险ID2:司机ID3,。。。。。。
	public String idsAndCarManIds;
	
	//删除人意险中司机的信息
	public String deleteCarMan() throws Exception{
		Json json=new Json();
		if(this.idsAndCarManIds==null||this.idsAndCarManIds.length()==0){
			json.put("success", false);
			json.put("msg",getText("carManRisk.delete.lose1"));
			this.json=json.toString();
			return "json";
		}
		
		//对字符串的分析
		Map<Long,List<Long>> idsAndCarManIds=new HashMap<Long, List<Long>>();
		List<Long> carManIds;
		String[] _idsAndCarManIds = this.idsAndCarManIds.split(",");
		String[] idAndCarManId;
		Long carManId;
		for(String _idAndCarManId :_idsAndCarManIds){
			idAndCarManId=_idAndCarManId.split(":");
			if(idAndCarManId.length==2){
				carManId=Long.valueOf(idAndCarManId[0]);
				if(!idsAndCarManIds.containsKey(carManId)){
					carManIds=new ArrayList<Long>();
					carManIds.add(Long.valueOf(idAndCarManId[1]));
					idsAndCarManIds.put(carManId, carManIds);
				}else{
					carManIds=idsAndCarManIds.get(carManId);
					carManIds.add(Long.valueOf(idAndCarManId[1]));
					idsAndCarManIds.put(carManId, carManIds);
				}
				
			}else{
				if(idAndCarManId.length==1){
					throw new CoreException("id:"+idAndCarManId[0]+" not exist carManId!");
				}else{
					throw new CoreException("id is null!");
				}
			}
		}
		
		this.carManRiskService.doDeleteCarMan(idsAndCarManIds);
		json.put("success", true);
		this.json=json.toString();
		return "json";
	}
	
}