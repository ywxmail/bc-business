/**
 * 
 */
package cn.bc.business.runcase.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.business.car.dao.CarDao;
import cn.bc.business.car.domain.Car;
import cn.bc.business.runcase.dao.CaseTrafficDao;
import cn.bc.business.runcase.domain.Case4InfractTraffic;
import cn.bc.business.runcase.domain.CaseBase;
import cn.bc.business.spider.domain.JinDunJTWF;
import cn.bc.business.sync.dao.JiaoWeiJTWFDao;
import cn.bc.business.sync.dao.JinDunJTWFDao;
import cn.bc.business.sync.domain.JiaoWeiJTWF;
import cn.bc.core.service.DefaultCrudService;
import cn.bc.core.util.StringUtils;
import cn.bc.identity.service.IdGeneratorService;
import cn.bc.identity.web.SystemContext;
import cn.bc.identity.web.SystemContextHolder;
import cn.bc.sync.dao.SyncBaseDao;
import cn.bc.sync.domain.SyncBase;

/**
 * 营运事件交通违章Service的实现
 * 
 * @author dragon
 */
public class CaseTrafficServiceImpl extends DefaultCrudService<Case4InfractTraffic> implements
		CaseTrafficService {
	private CaseTrafficDao caseTrafficDao;

	private SyncBaseDao syncBaseDao;	   //同步基表
	private JinDunJTWFDao jinDunJTWFDao;   //金盾网交通违法
	private JiaoWeiJTWFDao jiaoWeiJTWFDao; //交委交通违法
	private CarDao carDao;
	
	private IdGeneratorService idGeneratorService;// 用于生成uid的服务

	@Autowired
	public void setSyncBaseDao(SyncBaseDao syncBaseDao) {
		this.syncBaseDao = syncBaseDao;
	}
	
	@Autowired
	public void setJinDunJTWFDao(JinDunJTWFDao jinDunJTWFDao) {
		this.jinDunJTWFDao = jinDunJTWFDao;
	}

	@Autowired
	public void setJiaoWeiJTWFDao(JiaoWeiJTWFDao jiaoWeiJTWFDao) {
		this.jiaoWeiJTWFDao = jiaoWeiJTWFDao;
	}

	@Autowired
	public void setCarDao(CarDao carDao) {
		this.carDao = carDao;
	}
	
	@Autowired
	public void setIdGeneratorService(IdGeneratorService idGeneratorService) {
		this.idGeneratorService = idGeneratorService;
	}

	public CaseTrafficDao getCaseTrafficDao() {
		return caseTrafficDao;
	}

	public void setCaseTrafficDao(CaseTrafficDao caseTrafficDao) {
		this.caseTrafficDao = caseTrafficDao;
		this.setCrudDao(caseTrafficDao);
	}

	/**
	 * 保存并更新Sycn对象的状态
	 * @param e
	 * @param sb
	 * @return
	 */
	public Case4InfractTraffic save(Case4InfractTraffic e, SyncBase sb) {
		//默认的保存处理
		e = super.save(e);
		if(sb != null){
			//保存SyncBase对象
			this.syncBaseDao.save(sb);
		}
		return e;
	}

	public List<Case4InfractTraffic> doPatchSave(String syncIds) {
		List<Case4InfractTraffic> citList = new ArrayList<Case4InfractTraffic>(); 
		Case4InfractTraffic cit = null;
		
		//遍历syncIds
		Long[] syncIdArray = StringUtils.stringArray2LongArray(syncIds.split(","));
		Long carId = null;
		for(Long syncId : syncIdArray){
			SyncBase sb = this.syncBaseDao.load(syncId);
			cit = new Case4InfractTraffic();
			if(sb.getSyncType().equals(JinDunJTWF.KEY_TYPE)){	//判断是否金盾网同步
				JinDunJTWF jinDunJTWF = this.jinDunJTWFDao.load(syncId);
				//通过金断网交通违法并装交通违章对象
				carId = findCarId(jinDunJTWF.getCarPlateNo()); //根据车牌查找carId;
				cit.setCaseNo(jinDunJTWF.getSyncCode());
				cit.setAddress(jinDunJTWF.getAddress());
				cit.setHappenDate(jinDunJTWF.getHappenDate());
				cit.setJeom(jinDunJTWF.getJeom());
				cit.setFrom("金盾网/"+jinDunJTWF.getSource());
			}else{	//交委同步
				JiaoWeiJTWF jiaoWeiJTWF = this.jiaoWeiJTWFDao.load(syncId);
				//通过交委接口交通违法并装交通违章对象
				carId = findCarId(jiaoWeiJTWF.getCarPlateNo()); //根据车牌查找carId;
				cit.setCaseNo(jiaoWeiJTWF.getSyncCode());
				cit.setSubject(jiaoWeiJTWF.getContent());
				cit.setJeom(jiaoWeiJTWF.getJeom());
				cit.setHappenDate(jiaoWeiJTWF.getHappenDate());
				cit.setFrom("交委/");
			}
			
			//设置交通违法对象车辆相关信息
			Car car = carDao.load(carId);
			cit.setCarId(car.getId());	//设置carId
			cit.setCarPlate(car.getPlate());
			cit.setMotorcadeId(car.getMotorcade().getId());
			cit.setMotorcadeName(car.getMotorcade().getName());
			
			//设置来源
			cit.setSource(CaseBase.SOURCE_GENERATION);
			//设置syncId
			cit.setSyncId(syncId);
			// 初始化信息
			cit.setUid(this.idGeneratorService.next(Case4InfractTraffic.ATTACH_TYPE));
			// 自动生成自编号
			cit.setCode(this.idGeneratorService.nextSN4Month(Case4InfractTraffic.KEY_CODE));
			cit.setType  (CaseBase.TYPE_INFRACT_TRAFFIC);
			cit.setStatus(CaseBase.STATUS_ACTIVE);
			
			// 设置创建人信息和最后修改人信息
			SystemContext context = SystemContextHolder.get();
			cit.setAuthor(context.getUserHistory());
			cit.setFileDate(Calendar.getInstance());
			cit.setModifier(context.getUserHistory());
			cit.setModifiedDate(Calendar.getInstance());
			
			//保存交通违章对象
			cit = super.save(cit);
			
			//保存同步对象
			sb.setStatus(SyncBase.STATUS_GEN);	//设置同步对象的已生成
			this.syncBaseDao.save(sb);
			citList.add(cit);
		}
		return citList;
	}
	
	/** 根据车牌号查找carId*/
	public Long findCarId(String carPlateNo) {
		Long carId = null;
		if(carPlateNo.length() > 0){ //判断车牌号是否为空
			carId = this.carDao.findcarIdByCarPlateNo(carPlateNo);
		}
		return carId;
	}
	
}