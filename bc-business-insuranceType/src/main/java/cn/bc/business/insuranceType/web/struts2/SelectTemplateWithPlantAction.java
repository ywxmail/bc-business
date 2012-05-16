/**
 * 
 */
package cn.bc.business.insuranceType.web.struts2;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;


import cn.bc.business.insuranceType.domain.InsuranceType;
import cn.bc.business.insuranceType.service.InsuranceTypeService;
import cn.bc.business.web.struts2.FileEntityAction;
import cn.bc.web.ui.json.Json;
import cn.bc.web.ui.json.JsonArray;

/**
 * 根据模板ID选择多险种信息
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class SelectTemplateWithPlantAction extends FileEntityAction<Long, InsuranceType> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private InsuranceTypeService insuranceTypeService;
	public String pid;

	@Autowired
	public void setCarService(InsuranceTypeService insuranceTypeService) {
		this.insuranceTypeService = insuranceTypeService;
		this.setCrudService(insuranceTypeService);
	}
	public String selectInsuranceTypes() throws Exception {
		if(pid!=null){
			String[] sarr=pid.split(",");
			JsonArray jsons = new JsonArray();
			for(String sid:sarr){
				if(sid!=""){
					List<InsuranceType> iList=this.insuranceTypeService.findTemplateWithPlant(Long.valueOf(sid));
					Json o;
						for(InsuranceType it:iList){
							o=new Json();
							o.put("id", it.getId());
							o.put("name", it.getName());
							o.put("coverage", it.getCoverage());
							o.put("description", it.getDescription() != null ? it.getDescription() : "");
							jsons.add(o);
						}
				}
			}
			json=jsons.toString();
		}
		return "json";
	}
}
