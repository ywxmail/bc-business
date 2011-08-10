package cn.bc.business.charger.web.struts2;

import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.charger.domain.Charger;
import cn.bc.business.web.struts2.FileEntityAction;
import cn.bc.core.query.condition.Condition;
import cn.bc.core.service.CrudService;
import cn.bc.docs.util.ImageUtils;
import cn.bc.web.ui.html.grid.Column;
import cn.bc.web.ui.html.grid.GridData;

/**
 * 相片处理Action
 * 
 * @author dragon
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class ModifyPhotoAction extends FileEntityAction<Long, Charger> {
	// private static Log logger = LogFactory.getLog(BulletinAction.class);
	protected final Log logger = LogFactory.getLog(getClass());
	private static final long serialVersionUID = 1L;
	public boolean isManager;
    public int x1;
    public int y1;
    public int w;
    public int h;
    public ImageUtils imageUils;
    public String photoUrl;
    
	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}

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

	
    public String modify() throws IOException{
        FileInputStream inputFile=new FileInputStream(new File("src/main/webapp/"+photoUrl));
        DataInputStream dis=new DataInputStream(inputFile);
    	String extension = "jpg";
    	 Date date=new Date();
    	String dateString=String.valueOf(date.getTime());
    	    String path=photoUrl.substring(0,28);
    	    photoUrl=path+dateString +".jpg";
		BufferedImage newImg = ImageUtils.crop(dis, x1, y1, w,h);
		ImageIO.write(newImg, extension, new File("src/main/webapp/"+photoUrl));
		this.setPhotoUrl(photoUrl);
    	return "modify";
    }
    
}
