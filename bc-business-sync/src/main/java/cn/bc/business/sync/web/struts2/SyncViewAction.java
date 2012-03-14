/**
 * 
 */
package cn.bc.business.sync.web.struts2;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.sync.domain.JiaoWeiJTWF;
import cn.bc.business.sync.service.BsSyncService;
import cn.bc.business.web.struts2.ViewAction;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.query.condition.ConditionUtils;
import cn.bc.identity.web.SystemContext;
import cn.bc.sync.domain.SyncBase;
import cn.bc.web.ui.html.page.PageOption;
import cn.bc.web.ui.json.Json;

/**
 * 交委接口的交通违法信息视图Action
 * 
 * @author dragon
 * 
 */
/**
 * @author rongjih
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public abstract class SyncViewAction extends ViewAction<Map<String, Object>> {
	private static final long serialVersionUID = 1L;
	protected BsSyncService bsSyncService;
	public String status = String.valueOf(JiaoWeiJTWF.STATUS_NEW); // 状态，多个用逗号连接

	@Autowired
	public void setBsSyncService(BsSyncService bsSyncService) {
		this.bsSyncService = bsSyncService;
	}

	@Override
	public boolean isReadonly() {
		// 系统管理员
		SystemContext context = (SystemContext) this.getContext();
		return !context.hasAnyRole(getText("key.role.bc.admin"));
	}

	protected Map<String, String> getSyncStatuses() {
		Map<String, String> statuses = new LinkedHashMap<String, String>();
		statuses.put(String.valueOf(SyncBase.STATUS_NEW),
				getText("bs.sync.status.new"));
		statuses.put(String.valueOf(SyncBase.STATUS_DONE),
				getText("bs.sync.status.done"));
		statuses.put(String.valueOf(SyncBase.STATUS_GEN),
				getText("bs.sync.status.gen"));
		statuses.put("", getText("bs.status.all"));
		return statuses;
	}

	@Override
	protected PageOption getHtmlPageOption() {
		return super.getHtmlPageOption().setWidth(800).setMinWidth(400)
				.setHeight(400).setMinHeight(300);
	}

	@Override
	protected String getGridRowLabelExpression() {
		return "['syncCode']";
	}

	@Override
	protected Condition getGridSpecalCondition() {
		// 状态条件
		return ConditionUtils.toConditionByComma4IntegerValue(this.status,
				"b.status_");
	}

	@Override
	protected String getGridDblRowMethod() {
		return "bc.page.open";
	}

	@Override
	protected void extendGridExtrasData(Json json) {
		super.extendGridExtrasData(json);

		// 状态条件
		if (this.status != null && this.status.trim().length() > 0) {
			json.put("status", status);
		}
	}

	@Override
	protected String getHtmlPageJs() {
		return this.getContextPath() + "/bc-business/sync/"
				+ getFormActionName() + "/view.js";
	}

	/**
	 * 同步的处理
	 * 
	 * @return
	 * @throws Exception
	 */
	public String sync() throws Exception {
		Json json = new Json();
		try {
			StringBuffer strMsg = new StringBuffer();// 用于保存错误信息

			// 执行同步
			int newCount = this.doSync(strMsg);

			// 发生异常就直接退出
			if (strMsg.length() > 0) {
				json.put("success", false);
				json.put("msg",
						this.getSyncFailedMsg(newCount, strMsg.toString()));
			} else {
				json.put("success", true);
				json.put(
						"msg",
						getText("bs.sync.success",
								new String[] { String.valueOf(newCount) }));
			}
		} catch (Exception e) {
			json.put("success", false);
			json.put("msg",
					getText("bs.sync.failed", new String[] { e.getMessage() }));
		}

		this.json = json.toString();
		return "json";
	}

	/**
	 * 获取同步失败的提示信息
	 * 
	 * @param strMsg
	 * @return
	 */
	protected String getSyncFailedMsg(int newCount, String strMsg) {
		if (newCount > 0) {
			return getText("bs.sync.finishedWithError",
					new String[] { String.valueOf(newCount), strMsg });
		} else {
			return getText("bs.sync.failed", new String[] { strMsg });
		}
	}

	protected abstract int doSync(StringBuffer strMsg);
	
}
