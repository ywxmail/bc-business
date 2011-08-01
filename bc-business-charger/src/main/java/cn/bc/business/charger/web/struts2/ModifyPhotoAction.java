package cn.bc.business.charger.web.struts2;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.charger.domain.Charger;
import cn.bc.business.web.struts2.CrudAction;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.service.CrudService;
import cn.bc.docs.util.ImageUtils;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.GridData;
import cn.bc.web.ui.json.Json;

/**
 * 相片处理Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class ModifyPhotoAction extends CrudAction<Long, Charger> {
	// private static Log logger = LogFactory.getLog(BulletinAction.class);
	protected final Log logger = LogFactory.getLog(getClass());
	private static final long serialVersionUID = 1L;
	public boolean isManager;
    public int x1;
    public int y1;
    public int w;
    public int h;
    public ImageUtils imageUils;
	@Autowired
	public void setChargerService(
			@Qualifier(value = "chargerService") CrudService<Charger> crudService) {
		this.setCrudService(crudService);
	}

	@Override
	protected GridData buildGridData(List<Column> columns) {
		return super.buildGridData(columns).setRowLabelExpression("name");
	}


	@Override
	protected Condition getSpecalCondition() {
		return null;
	}
	
    //获取Action名
	protected String getEntityConfigName() {
		return "ModifyPhoto";
	}

	
    public String modify(){
    	
    	
    	Json j=new Json();
    	j.put("url", "bc-business/modifyPhoto/img/1.jpg");
    	return "json";
    }
	
}
