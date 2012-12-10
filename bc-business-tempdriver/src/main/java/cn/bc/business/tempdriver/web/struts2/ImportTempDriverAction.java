package cn.bc.business.tempdriver.web.struts2;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.bc.business.tempdriver.domain.TempDriver;
import cn.bc.business.tempdriver.service.TempDriverService;
import cn.bc.core.util.DateUtils;
import cn.bc.docs.web.struts2.ImportDataAction;
import cn.bc.identity.domain.ActorHistory;
import cn.bc.identity.service.IdGeneratorService;
import cn.bc.identity.web.SystemContext;
import cn.bc.identity.web.SystemContextHolder;
import cn.bc.placeorigin.domain.PlaceOrigin;
import cn.bc.placeorigin.service.PlaceOriginService;

import com.google.gson.JsonObject;

/**
 * 导入司机招聘数据的Action
 * 
 * @author lbj
 * 
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Controller
public class ImportTempDriverAction extends ImportDataAction {
	private static final long serialVersionUID = 1L;
	private final static Log logger = LogFactory
			.getLog(ImportTempDriverAction.class);
	private TempDriverService tempDriverService;
	private IdGeneratorService idGeneratorService;// 用于生成uid的服务
	private PlaceOriginService placeOriginServie;

	@Autowired
	public void setPlaceOriginServie(PlaceOriginService placeOriginServie) {
		this.placeOriginServie = placeOriginServie;
	}

	@Autowired
	public void setTempDriverService(TempDriverService tempDriverService) {
		this.tempDriverService = tempDriverService;
	}
	
	@Autowired
	public void setIdGeneratorService(IdGeneratorService idGeneratorService) {
		this.idGeneratorService = idGeneratorService;
	}

	@Override
	protected void importData(List<Map<String, Object>> data, JsonObject json,
			String fileType) {
		
		List<TempDriver> lists=new ArrayList<TempDriver>();//声明将要保存的集合
		
		TempDriver entity;//司机招聘对象
		int updateCount = 0;//更新的行数
		int insertCount = 0;//插入的行数
		int invalidCount = 0;//无效数据
		String invalidCert = "";//插入不成功的身份证号
		String key;//声明取map值的key
		// 系统上下文
		SystemContext context = (SystemContext) SystemContextHolder.get();
		//当期用户
		ActorHistory user=context.getUserHistory();
		
		for(Map<String, Object> map:data){
			key="身份证号码";
			if(map.get(key)!=null && validateCertIdentity(map.get(key).toString()) && map.get("姓名")!=null ){
				String certIdentity=map.get(key).toString();
				entity=this.tempDriverService.loadByCertIdentity(certIdentity);
				
				if(entity==null){//非空：更新信息，空：插入信息
					entity=new TempDriver();
					insertCount++;
					//身份证号
					entity.setCertIdentity(certIdentity);
					entity.setUid(this.idGeneratorService.next(TempDriver.KEY_UID));
					//设置创建者
					entity.setAuthor(user);
					entity.setFileDate(Calendar.getInstance());
				}else{
					updateCount++;
				}
				
				//状态都修改为待聘
				entity.setStatus(TempDriver.STATUS_RESERVE);
				//设置最后修改人
				entity.setModifier(user);
				entity.setModifiedDate(Calendar.getInstance());
				
				key="姓名";
				if(map.get(key)!=null){
					entity.setName(map.get(key).toString().trim());
				}
				key="服务资格证";
				if(map.get(key)!=null)
					entity.setCertFWZG(map.get(key).toString().trim());
				key="电话";
				if(map.get(key)!=null)
					entity.setPhone(map.get(key).toString().trim());
				key="原单位";
				if(map.get(key)!=null)
					entity.setFormerUnit(map.get(key).toString().trim());
				key="将入车号";
				if(map.get(key)!=null)
					entity.setEntryCar(map.get(key).toString().trim());
				key="申请属性";
				if(map.get(key)!=null)
					entity.setApplyAttr(map.get(key).toString().trim());
				key="犯罪记录";
				if(map.get(key)!=null)
					entity.setCrimeRecode(map.get(key).toString().trim());
				key="信誉档案";
				if(map.get(key)!=null)
					entity.setCreditDesc(map.get(key).toString().trim());
				key="背景调查";
				if(map.get(key)!=null)
					entity.setBackGround(map.get(key).toString().trim());
				key="面试日期";
				if(map.get(key)!=null){
					Date d=(Date) map.get(key);
					Calendar cal=Calendar.getInstance();
					cal.setTime(d);	
					entity.setInterviewDate(cal);
				}
				key="报名日期";
				if(map.get(key)!=null){
					Date d=(Date) map.get(key);
					Calendar cal=Calendar.getInstance();
					cal.setTime(d);	
					entity.setRegisterDate(cal);
				}
				
				loadCertIdentityInfo(certIdentity,map,entity,
						//更新的数量，不超过配置的最大值才会更新籍贯和区域
						data.size()<=Integer.valueOf(this.getText("importTempDriver.updateOR.maxCount")));	
				
				lists.add(entity);
			}else{
				//拼接不能插入的身份证信息
				if(map.get(key)!=null){
					invalidCert+=","+map.get(key).toString();
				}
				
				invalidCount++;
			}
		}
		
		if(lists.size()>0)//保存数据
			this.tempDriverService.doSaveList(lists);
		
		String msg="总数："+data.size()+"条数据！"+"成功导入:" + (updateCount+insertCount)+ "条，"
				+ "其中更新数据"+updateCount+"条,"+"插入数据"+insertCount+"条.";
		
		String invalidMsg=invalidCount >0 ? "不能导入：" + invalidCount + "条!"
				+(invalidCert.length()>0?"身份证号为:"+invalidCert.substring(1):""):"";
		
		json.addProperty("msg",msg+invalidMsg);
		
		if(logger.isDebugEnabled()){
			//logger.debug("TODO: ImportOptionAction.importData");
			logger.debug("ImportOptionAction.importData:"+msg+invalidMsg);
		}
		
	}
	
	//自动加载与身份证相关的信息 updateOR:更新籍贯和区域的控制
	private void loadCertIdentityInfo(String certIdentity,Map<String, Object> map,TempDriver entity,boolean updateOR){
		String origin="";
		int region=TempDriver.REGION_;
		
		//更新的数量，不超过配置的最大值才会更新籍贯和区域
		if(updateOR){
			// 根据编码找出籍贯
			List<PlaceOrigin> pList = null;
			String code = certIdentity.substring(0, 6);
			// 先按身份证前6位查找
			pList = this.placeOriginServie.findPlaceOrigin(code);
			if (pList == null || pList.size() == 0) {
				// 若前6位找不到然后按前4位
				code = certIdentity.substring(0, 4);
				pList = this.placeOriginServie.findPlaceOrigin(code + "00");
				if (pList == null || pList.size() == 0) {
					code = certIdentity.substring(0, 2);
					pList = this.placeOriginServie.findPlaceOrigin(code + "0000");
				}
			}
			
			if (pList != null && pList.size() > 0) {
				// 取集合中第一的对象
				PlaceOrigin po = pList.get(0);
				origin = po.getFullname();
				if (origin.indexOf("广东省广州市")!= -1) {// 本市
					region=TempDriver.REGION_BEN_SHI;
				} else if(origin.indexOf("广东省") != -1 ){// 本省
					region=TempDriver.REGION_BEN_SHENG;
				}else{// 外省
					region=TempDriver.REGION_WAI_SHENG;
				}

			}
		}
		
		
		if(map.get("籍贯")!=null){
			entity.setOrigin(map.get("籍贯").toString().trim());
		}else if(updateOR){
			entity.setOrigin(origin);
		}
		
		//区域
		entity.setRegion(region);
		
		String b_year = "";// 年份     
		String b_month = "";// 月份     
		String b_day = "";// 月份  
		String _sex="";
		if(certIdentity.length()==15){
			_sex=certIdentity.substring(13,14);
			b_year="19"+certIdentity.substring(6,8);
			b_month=certIdentity.substring(8,10);
			b_day=certIdentity.substring(10,12);
		}else{
			_sex=certIdentity.substring(16,17);
			b_year=certIdentity.substring(6,10);
			b_month=certIdentity.substring(10,12);
			b_day=certIdentity.substring(12,14);
		}
		
		//性别
		if(Integer.valueOf(_sex)%2 == 1){//男
			entity.setSex(TempDriver.SEX_MAN);
		}else{//女
			entity.setSex(TempDriver.SEX_WOMAN);
		}
		
		//出生日期
		String birthdate=b_year+"-"+b_month+"-"+b_day;
		if(isDate(birthdate))
			entity.setBirthdate(DateUtils.getCalendar(birthdate));	
	}
	
	//身份证号验证
	private boolean validateCertIdentity(String certIdentity){
		//不能出现空格
		if(certIdentity.length()!=certIdentity.trim().length())
			return false;
		
		//长度 15 或 18位
		if(!(certIdentity.length() == 15 || certIdentity.length() == 18))
			return false;
		
		return true;
	}
	
	private boolean isDate(String strDate) {     
		Pattern pattern = Pattern.compile("^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s(((0?[0-9])|([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$");     
		Matcher m = pattern.matcher(strDate);     
		if (m.matches()) {     
			return true;     
		 } else {     
			return false;     
		}     
	 }     

	@Override
	protected Object getCellValue(Cell cell, String columnName, String fileType) {
		if (cell == null)
			return null;
		
		String columnNameKey="身份证号码";
		if(columnName.equals(columnNameKey)){
			if (cell.getCellType() == Cell.CELL_TYPE_STRING) {// 字符串
				 // 去空格
				return cell.getStringCellValue().trim();
			} else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {// 数字
				return String.valueOf((long) cell.getNumericCellValue());
			}
		}
		
		columnNameKey="服务资格证";
		if(columnName.equals(columnNameKey)){
			if (cell.getCellType() == Cell.CELL_TYPE_STRING) {// 字符串
				// 列名去空格
				if (cell.getStringCellValue() != null) {
					return cell.getStringCellValue().trim();
				} else {
					return cell.getStringCellValue();
				}
			} else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {// 数字
				return String.valueOf((long) cell.getNumericCellValue());
			}
		}
		
		columnNameKey="电话";
		if(columnName.equals(columnNameKey)){
			if (cell.getCellType() == Cell.CELL_TYPE_STRING) {// 字符串
				// 列名去空格
				if (cell.getStringCellValue() != null) {
					return cell.getStringCellValue().trim();
				} else {
					return cell.getStringCellValue();
				}
			} else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {// 数字
				return String.valueOf((long) cell.getNumericCellValue());
			}
		}
		
		columnNameKey="面试日期";
		if(columnName.equals(columnNameKey)&&cell.getCellType() == Cell.CELL_TYPE_NUMERIC)
			return cell.getDateCellValue();
		
		columnNameKey="报名日期";
		if(columnName.equals(columnNameKey)&&cell.getCellType() == Cell.CELL_TYPE_NUMERIC)
			return cell.getDateCellValue();

		return super.getCellValue(cell, columnName, fileType);
	}
}
