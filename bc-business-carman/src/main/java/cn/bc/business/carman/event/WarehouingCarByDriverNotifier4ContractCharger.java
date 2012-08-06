/**
 * 
 */
package cn.bc.business.carman.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;

import cn.bc.BCConstants;
import cn.bc.business.carman.domain.CarByDriverHistory;
import cn.bc.business.carman.service.CarByDriverHistoryService;
import cn.bc.business.contract.event.WarehousingCarByDrierHistoryEvent;

/**
 * 入库草稿迁移记录时将未入库的迁移记录入库事件的监听器：
 * 
 * @author zxr
 * 
 */
public class WarehouingCarByDriverNotifier4ContractCharger implements
		ApplicationListener<WarehousingCarByDrierHistoryEvent> {
	public CarByDriverHistoryService carByDriverHistoryService;

	@Autowired
	public void setCarByDriverHistoryService(
			CarByDriverHistoryService carByDriverHistoryService) {
		this.carByDriverHistoryService = carByDriverHistoryService;
	}

	public void onApplicationEvent(WarehousingCarByDrierHistoryEvent event) {
		CarByDriverHistory carByDriverHistory = carByDriverHistoryService
				.load(event.getCarByHistoryId());
		// 顶班的操作
		if (carByDriverHistory.getMoveType() == CarByDriverHistory.MOVETYPE_DINGBAN) {
			// 将顶班车辆组装成字符串赋值给shiftwork字段：plate1,id1;plate2,id2;...
			Long[] carIds = null;
			String shiftwork = carByDriverHistory.getShiftwork().trim();
			if (shiftwork.length() != 0) {
				carByDriverHistory.setShiftwork(shiftwork + ";");
				String[] shiftworks = shiftwork.split(";");
				carIds = new Long[shiftworks.length];
				if (carIds.length > 0) {
					for (int i = 0; i < shiftworks.length; i++) {
						carIds[i] = new Long(shiftworks[i].split(",")[1]);
					}
				}
			}
			carByDriverHistory.setStatus(BCConstants.STATUS_ENABLED);
			carByDriverHistoryService.saveShiftwork(carByDriverHistory, carIds);
		} else {
			// 其他类型的操作(除了转车队)
			carByDriverHistory.setStatus(BCConstants.STATUS_ENABLED);
			carByDriverHistoryService.save(carByDriverHistory);
		}
	}
}
