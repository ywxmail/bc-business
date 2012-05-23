/**
 * 
 */
package cn.bc.business.fee.template.web.struts2;

import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.fee.template.domain.FeeTemplate;
import cn.bc.business.fee.template.service.FeeTemplateService;
import cn.bc.business.web.struts2.FileEntityAction;

/**
 * 根据模板ID选择多个费用信息
 * 
 * @author lbj
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class SelectTemplateWithFeeTemplateAction extends
		FileEntityAction<Long, FeeTemplate> {
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
		if (pid != null) {
			String[] sarr = pid.split(",");
			JSONArray jsons = new JSONArray();
			for (String sid : sarr) {
				if (sid != "") {
					List<Map<String, String>> iList = this.feeTemplateService
							.findFee(Long.parseLong(sid));
					JSONObject o;
					for (Map<String, String> it : iList) {
						o = new JSONObject();
						o.put("id", it.get("id"));
						o.put("name", it.get("name"));
						o.put("price", it.get("price"));
						o.put("count", it.get("count"));
						o.put("payType", it.get("payType"));
						o.put("desc", it.get("desc") != null ? it.get("desc")
								: "");
						String spec = it.get("spec");
						o.put("spec",
								spec != null && spec.length() > 0 ? new JSONObject(
										spec) : new JSONObject());
						o.put("code", it.get("code"));
						jsons.put(o);
					}
				}
			}
			json = jsons.toString();
		}
		return "json";
	}
}
