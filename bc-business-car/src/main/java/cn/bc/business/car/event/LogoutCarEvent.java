package cn.bc.business.car.event;

import java.util.Calendar;

import org.springframework.context.ApplicationEvent;

/**
 * 注销车辆后自动生成或更新该车辆的出车更新模块中相关的状态和完成进度项目中的交车信息
 * 注:车辆执行注销操作后，1)车辆更新模块中如果存在该车辆相关的信息，就将该信息的状态更改为“更新中”，并完成进度项目
 * 中“交车”项目(进度项目中的时间为车辆模块操作注销操作后的交车日期)。2)车辆更新模块中如果不存在就在车辆更新模块生成
 * 一条该车辆的更新信息(状态为“更新中”,并完成进度项目中“交车”项目(进度项目中的时间为车辆模块操作注销操作后的交车日期))。
 * 
 * @author zxr
 * 
 */
public class LogoutCarEvent extends ApplicationEvent {

	private static final long serialVersionUID = 1L;
	// 车辆Id
	private final Long carId;
	private String plateType;// 车牌归属，如“粤A”
	private String plateNo;// 车牌号码，如“C4X74”
	private Calendar returnDate;// 交车日期

	public Long getCarId() {
		return carId;
	}

	public String getPlateType() {
		return plateType;
	}

	public void setPlateType(String plateType) {
		this.plateType = plateType;
	}

	public String getPlateNo() {
		return plateNo;
	}

	public void setPlateNo(String plateNo) {
		this.plateNo = plateNo;
	}

	public Calendar getReturnDate() {
		return returnDate;
	}

	public void setReturnDate(Calendar returnDate) {
		this.returnDate = returnDate;
	}

	/**
	 * @param carId车辆Id
	 * @param plateType车牌归属
	 *            ，如“粤A”
	 * @param plateNo车牌号码
	 *            ，如“C4X74”
	 * @param returnDate交车日期
	 */
	public LogoutCarEvent(Long carId, String plateType, String plateNo,
			Calendar returnDate) {
		super(carId);
		this.carId = carId;
		this.plateType = plateType;
		this.plateNo = plateNo;
		this.returnDate = returnDate;
	}
}
