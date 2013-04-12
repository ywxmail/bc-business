/**
 * 
 */
package cn.bc.business.carPrepare.event;

import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;

import cn.bc.business.car.event.LogoutCarEvent;
import cn.bc.business.car.service.CarService;
import cn.bc.business.carPrepare.domain.CarPrepare;
import cn.bc.business.carPrepare.domain.CarPrepareItem;
import cn.bc.business.carPrepare.service.CarPrepareService;
import cn.bc.core.util.DateUtils;
import cn.bc.identity.web.SystemContext;
import cn.bc.identity.web.SystemContextHolder;

/**
 * 如果车辆信息模块执行注销操作就在车辆更新模块生成或更新该车辆的更新信息的监听器：
 * 
 * @author zxr
 * 
 */
public class CreateOrUpdateCarPrepareInfoByLogoutCar implements
		ApplicationListener<LogoutCarEvent> {
	public CarPrepareService carPrepareService;
	public CarService carService;

	@Autowired
	public void setCarPrepareService(CarPrepareService carPrepareService) {
		this.carPrepareService = carPrepareService;
	}

	@Autowired
	public void setCarService(CarService carService) {
		this.carService = carService;
	}

	public void onApplicationEvent(LogoutCarEvent event) {
		// 车牌号码
		String plateType = event.getPlateType();
		String plateNo = event.getPlateNo();
		// 交车日期
		Calendar returnDate = event.getReturnDate();
		// 通过车牌号码来查找车辆更新计划
		if (plateType != null && plateNo != null) {
			CarPrepare carPrepare = carPrepareService
					.getCarPrepareByPlateTypeAndPlateNo(plateType, plateNo);
			// 如果不为空就更新车辆更新信息
			if (carPrepare != null) {
				// 将状态更改为更新中
				carPrepare.setStatus(CarPrepare.STATUS_INTHEUPDATE);
				// 完成交车项目
				Set<CarPrepareItem> carPrepareItems = carPrepare
						.getCarPrepareItem();
				if (!carPrepareItems.isEmpty()) {
					Iterator<CarPrepareItem> p = carPrepareItems.iterator();
					while (p.hasNext()) {
						CarPrepareItem cpi = p.next();
						// 判断进度项目是否为“交车”
						if (cpi.getName().equals("交车")) {
							// 更新日期
							cpi.setDate(returnDate);
							// 更新状态
							cpi.setStatus(CarPrepareItem.STATUS_FINISHED);
						}
					}
				}
				carPrepare.setCarPrepareItem(carPrepareItems);
				carPrepareService.save(carPrepare);
			} else {
				// 如果为空就新建一条车辆更新
				CarPrepare newCarPrepare = new CarPrepare();
				SystemContext context = SystemContextHolder.get();
				// 获取车辆的相关信息
				String carInfo = carService
						.getCarRelevantInfoByPlateNo(plateNo);
				String bsType = null;
				String certNo2 = null;
				String scrapto = null;
				String scrapDate = null;
				String registerDate = null;
				String contractEndDate = null;
				String company = null;
				Integer motorcadeId = null;
				String commerialEndDate = null;
				String greenslipEndDate = null;
				if (carInfo != null) {
					try {
						JSONObject carInfo4json = new JSONObject(carInfo);
						if (carInfo4json.getBoolean("success")) {
							bsType = carInfo4json.getString("bsType");
							certNo2 = carInfo4json.getString("certNo2");
							scrapto = carInfo4json.getString("scrapto");
							company = carInfo4json.getString("company");
							motorcadeId = carInfo4json.getInt("motorcadeId");
							if (carInfo4json.has("scrapDate")) {
								scrapDate = carInfo4json.getString("scrapDate");
							}
							if (carInfo4json.has("registerDate")) {
								registerDate = carInfo4json
										.getString("registerDate");
							}
							if (carInfo4json.has("contractEndDate")) {
								contractEndDate = carInfo4json
										.getString("contractEndDate");
							}
							if (carInfo4json.has("commerialEndDate")) {
								commerialEndDate = carInfo4json
										.getString("commerialEndDate");
							}
							if (carInfo4json.has("greenslipEndDate")) {
								greenslipEndDate = carInfo4json
										.getString("greenslipEndDate");
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				// 将状态设置为更新中
				newCarPrepare.setStatus(CarPrepare.STATUS_INTHEUPDATE);
				newCarPrepare.setAuthor(context.getUserHistory());
				newCarPrepare.setFileDate(Calendar.getInstance());
				newCarPrepare.setModifier(context.getUserHistory());
				newCarPrepare.setModifiedDate(Calendar.getInstance());
				newCarPrepare.setUid(CarPrepare.KEY_UID);
				newCarPrepare.setC1PlateNo(plateNo);
				newCarPrepare.setC1PlateType(plateType);
				newCarPrepare.setC2PlateType("粤A");
				newCarPrepare.setC1BsType(bsType);
				newCarPrepare.setC1Scrapto(scrapto);
				newCarPrepare.setC1Company(company);
				newCarPrepare.setCode(certNo2);
				newCarPrepare.setC1Motorcade(motorcadeId);
				newCarPrepare.setC1CommeriaEndDate(DateUtils
						.getCalendar(commerialEndDate));
				newCarPrepare.setC1ContractEndDate(DateUtils
						.getCalendar(contractEndDate));
				newCarPrepare.setC1GreenslipEndDate(DateUtils
						.getCalendar(greenslipEndDate));
				newCarPrepare.setC1RegisterDate(DateUtils
						.getCalendar(registerDate));
				newCarPrepare.setPlanDate(DateUtils.getCalendar(scrapDate));
				Set<CarPrepareItem> carPrepareItems = new LinkedHashSet<CarPrepareItem>();
				// 进度
				// 完成交车进度
				carPrepareService.initializeCarPrepareItemInfo(newCarPrepare,
						carPrepareItems, "交车", returnDate,
						CarPrepareItem.STATUS_FINISHED, 1);
				carPrepareService.initializeCarPrepareItemInfo(newCarPrepare,
						carPrepareItems, "二手车行提车", null,
						CarPrepareItem.STATUS_UNFINISHED, 2);
				carPrepareService.initializeCarPrepareItemInfo(newCarPrepare,
						carPrepareItems, "报停计价器", null,
						CarPrepareItem.STATUS_UNFINISHED, 3);
				carPrepareService.initializeCarPrepareItemInfo(newCarPrepare,
						carPrepareItems, "收转篮报废凭证", null,
						CarPrepareItem.STATUS_UNFINISHED, 4);
				carPrepareService.initializeCarPrepareItemInfo(newCarPrepare,
						carPrepareItems, "报停车", null,
						CarPrepareItem.STATUS_UNFINISHED, 5);
				carPrepareService.initializeCarPrepareItemInfo(newCarPrepare,
						carPrepareItems, "办新车指标", null,
						CarPrepareItem.STATUS_UNFINISHED, 6);
				carPrepareService.initializeCarPrepareItemInfo(newCarPrepare,
						carPrepareItems, "新车上牌", null,
						CarPrepareItem.STATUS_UNFINISHED, 7);
				carPrepareService.initializeCarPrepareItemInfo(newCarPrepare,
						carPrepareItems, "出车", null,
						CarPrepareItem.STATUS_UNFINISHED, 8);
				newCarPrepare.setCarPrepareItem(carPrepareItems);
				carPrepareService.save(newCarPrepare);
			}
		}
	}
}
