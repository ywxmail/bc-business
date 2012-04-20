/**
 * 
 */
package cn.bc.business.fee.template.web.struts2;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;


import cn.bc.business.fee.template.service.FeeTemplateService;
import cn.bc.business.fee.template.domain.FeeTemplate;
import cn.bc.business.web.struts2.FileEntityAction;
import cn.bc.web.ui.json.Json;
import cn.bc.web.ui.json.JsonArray;

/**
 * 根据模板ID选择多个费用信息
 * 
 * @author lbj
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class SelectTemplateWithFeeTemplateAction extends FileEntityAction<Long, FeeTemplate> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private FeeTemplateService feeTemplateService;
	public String pid;

	@Autowired
	public void setFeeTemplateService(FeeTemplateService feeTemplateService) {
		this.feeTemplateService = feeTemplateService;
		this.setCrudService(feeTemplateService);
	}
	
	public String json;
	public String selectFeeTemplates() throws Exception {
		if(pid!=null){
			String[] sarr=pid.split(",");
			JsonArray jsons = new JsonArray();
			for(String sid:sarr){
				System.out.println(sid);
				if(sid!=""){
					List<Map<String,String>> iList=this.feeTemplateService.findFee(Long.parseLong(sid));
					Json o;
					for(Map<String,String> it:iList){
						o=new Json();
						o.put("id", it.get("id"));
						o.put("name", it.get("name"));
						o.put("price", it.get("price"));
						o.put("count", it.get("count"));
						o.put("payType", it.get("payType"));
						o.put("desc", it.get("desc") != null ? it.get("desc") : "");
						jsons.add(o);
					}
				}
			}
			json=jsons.toString();
		}
		return "json";
	}
}
