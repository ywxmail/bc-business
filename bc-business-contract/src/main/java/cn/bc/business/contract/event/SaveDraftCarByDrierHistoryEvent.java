package cn.bc.business.contract.event;

import org.springframework.context.ApplicationEvent;

/**
 * 入库经济合同时将草稿的迁移记录入库的事件
 * 
 * @author zxr
 * 
 */
public class SaveDraftCarByDrierHistoryEvent extends ApplicationEvent {

	private static final long serialVersionUID = 1L;
	// 车辆Id
	private final Long carByHistoryId;

	public Long getCarByHistoryId() {
		return carByHistoryId;
	}

	public SaveDraftCarByDrierHistoryEvent(Long carByHistoryId) {
		super(carByHistoryId);
		this.carByHistoryId = carByHistoryId;
	}
}
