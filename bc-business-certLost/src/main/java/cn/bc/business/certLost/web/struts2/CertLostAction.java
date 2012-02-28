/**
 * 
 */
package cn.bc.business.certLost.web.struts2;

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

import cn.bc.business.OptionConstants;
import cn.bc.business.car.service.CarService;
import cn.bc.business.carman.service.CarManService;
import cn.bc.business.certLost.domain.CertLost;
import cn.bc.business.certLost.domain.CertLostItem;
import cn.bc.business.certLost.service.CertLostService;
import cn.bc.business.motorcade.domain.Motorcade;
import cn.bc.business.motorcade.service.MotorcadeService;
import cn.bc.business.web.struts2.FileEntityAction;
import cn.bc.core.util.DateUtils;
import cn.bc.identity.web.SystemContext;
import cn.bc.option.domain.OptionItem;
import cn.bc.option.service.OptionService;
import cn.bc.web.ui.html.page.ButtonOption;
import cn.bc.web.ui.html.page.PageOption;

/**
 * 证照遗失Action
 * 
 * @author zxr
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class CertLostAction extends FileEntityAction<Long, CertLost> {
	// private static Log logger = LogFactory.getLog(BulletinAction.class);
	private static final long serialVersionUID = 1L;
	public CertLostService certLostService;
	public String portrait;
	public CarManService carManService;
	public CarService carService;
	public Long carManId;
	public Long carId;
	public List<Map<String, String>> motorcadeList; // 可选车队列表
	public JSONArray certNames; // 证件名称列表
	private OptionService optionService;
	private MotorcadeService motorcadeService;
	public String certs;// 补办证件的json字符串

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
	public void setcertLostService(CertLostService certLostService) {
		this.certLostService = certLostService;
		this.setCrudService(certLostService);
	}

	@Autowired
	public void setOptionService(OptionService optionService) {
		this.optionService = optionService;
	}

	@Override
	public boolean isReadonly() {
		// 车辆管理/司机管理或系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bs.certLost"),
				getText("key.role.bc.admin"));
	}

	@Override
	protected void afterCreate(CertLost entity) {
		super.afterCreate(entity);
		SystemContext context = this.getSystyemContext();
		entity.setTransactor(context.getUserHistory());

	}

	@Override
	protected void buildFormPageButtons(PageOption pageOption, boolean editable) {
		boolean readonly = this.isReadonly();
		if (editable && !readonly) {
			// 添加默认的保存按钮
			pageOption.addButton(new ButtonOption(getText("label.save"), null,
					"bc.certLostForm.save"));
		}
	}

	// 设置页面的尺寸
	@Override
	protected PageOption buildFormPageOption(boolean editable) {
		return super.buildFormPageOption(editable).setWidth(850)
				.setMinWidth(250);
	}

	@SuppressWarnings("static-access")
	@Override
	protected void beforeSave(CertLost entity) {
		super.beforeSave(entity);
		// 如果司机为空时,将其设为空
		CertLost c = this.getE();
		if (c.getDriver() != null && c.getDriver().getId() == null) {
			c.setDriver(null);
		}

		// 插入证件
		try {
			Set<CertLostItem> certs = null;

			if (this.certs != null && this.certs.length() > 0) {
				certs = new LinkedHashSet<CertLostItem>();
				CertLostItem resource;
				JSONArray jsons = new JSONArray(this.certs);
				JSONObject json;
				for (int i = 0; i < jsons.length(); i++) {
					json = jsons.getJSONObject(i);
					resource = new CertLostItem();
					if (json.has("id"))
						resource.setId(json.getLong("id"));
					resource.setCertLost(this.getE());
					resource.setCertName(json.getString("certName"));
					resource.setCertNo(json.getString("certNo"));
					resource.setReason(json.getString("reason"));
					resource.setLostAddress(json.getString("lostAddress"));
					resource.setDescription(json.getString("description"));
					resource.setNewCertNo(json.getString("newCertNo"));
					resource.setRemains(new Boolean(json.getString("remains")));
					resource.setReplace(new Boolean(json.getString("replace")));
					if (("").equals(json.getString("replaceDate"))) {
						resource.setReplaceDate(null);
					} else {
						resource.setReplaceDate(new DateUtils()
								.getCalendar(json.getString("replaceDate")));
					}
					certs.add(resource);

				}
			}
			if (this.getE().getCertLostItem() != null) {
				this.getE().getCertLostItem().clear();
				this.getE().getCertLostItem().addAll(certs);
			} else {
				this.getE().setCertLostItem(certs);
			}
		} catch (JSONException e) {
			logger.error(e.getMessage(), e);
			try {
				throw e;
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
	}

	@Override
	protected void initForm(boolean editable) throws Exception {
		super.initForm(editable);
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
				.findOptionItemByGroupKeys(new String[] { OptionConstants.CERT_NAME });

		// 证件名称列表
		this.certNames = OptionItem.toLabelValues(optionItems
				.get(OptionConstants.CERT_NAME));

	}

}
