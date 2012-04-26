/**
 * 
 */
package cn.bc.business.fee.service;

import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.BCConstants;
import cn.bc.business.car.dao.CarDao;
import cn.bc.business.car.domain.Car;
import cn.bc.business.fee.dao.FeeDao;
import cn.bc.business.fee.domain.Fee;
import cn.bc.core.service.DefaultCrudService;
import cn.bc.identity.domain.ActorHistory;
import cn.bc.identity.service.IdGeneratorService;
import cn.bc.identity.web.SystemContextHolder;

/**
 * 承包费Service的实现
 * 
 * @author wis
 */
public class FeeServiceImpl extends DefaultCrudService<Fee> implements
		FeeService {

	private FeeDao feeDao;
	public CarDao carDao;
	private IdGeneratorService idGeneratorService;// 用于生成uid的服务
	
	public FeeDao getFeeDao() {
		return feeDao;
	}

	public void setFeeDao(FeeDao feeDao) {
		this.feeDao = feeDao;
		this.setCrudDao(feeDao);
	}
	
	@Autowired
	public void setCarDao(CarDao carDao) {
		this.carDao = carDao;
	}
	
	@Autowired
	public void setIdGeneratorService(IdGeneratorService idGeneratorService) {
		this.idGeneratorService = idGeneratorService;
	}

	/**
	 * 根据本期的carId和feeDate查找前期的fee对象
	 * @param carId
	 * @param feeDate
	 * @return
	 */
	public Fee findb4FeeByCarIdANDFeeDate(Long carId, Calendar feeDate) {
		return this.feeDao.findb4FeeByCarIdANDFeeDate(carId,feeDate);
	}

	/**
	 * 根据本期的carId和feeYear和feeMonth查找前期的fee对象
	 * @param carId
	 * @param feeYear
	 * @param feeMonth
	 * @return
	 */
	public Fee findb4FeeByCarIdANDYearAndMonth(Long carId, Integer feeYear,
			Integer feeMonth) {
		int thisYear = feeYear.intValue();
		int thisMonth = feeMonth.intValue();
		int b4Year = 0;
		int b4Month = 0;
		
		if(thisMonth == 1){ //如果thisYear(本月)是1月,减一年,月份为12
			b4Year = thisYear - 1;
			b4Month = 12;
		}else{
			b4Year = thisYear;
			b4Month = thisMonth - 1;
		}
		return this.feeDao.findb4FeeByCarIdANDYearAndMonth(carId,b4Year,b4Month);
	}
	
	/**
	 * 检测此车辆是否存在本年本月的承包费用
	 * @param feeId
	 * @param carId
	 * @param feeYear
	 * @param feeMonth
	 * @return
	 */
	public Long checkFeeIsExist(Long feeId, Long carId, Integer feeYear,
			Integer feeMonth) {
		return this.feeDao.checkFeeIsExist(feeId,carId,feeYear,feeMonth);
	}

	/**
	 * 批量保存承包费车辆
	 * @param carIds
	 * @param carPlates
	 * @param feeYear
	 * @param feeMonth
	 */
	public void saveBatchInit(Long[] carIds,String[] carPlates,Integer feeYear, Integer feeMonth) {
		if(null != carIds && carIds.length > 0){
			// 执行插入操作
			ActorHistory author = SystemContextHolder.get().getUserHistory();
			Calendar fileDate = Calendar.getInstance();
			ActorHistory modifier = SystemContextHolder.get().getUserHistory();
			Calendar modifiedDate = Calendar.getInstance();

			for(int i = 0;i < carIds.length;i++){
				Fee fee = new Fee();
				fee.setCarId(carIds[i]);
				fee.setCarPlate(carPlates[i]);
				//设置车辆的附属信息
				Car car = this.carDao.load(fee.getCarId());
				fee.setMotorcadeId(car.getMotorcade().getId());
				fee.setMotorcadeName(car.getMotorcade().getName());
				fee.setCompany(car.getCompany());
				
				fee.setFeeYear(feeYear);
				fee.setFeeMonth(feeMonth);
				fee.setStatus(BCConstants.STATUS_ENABLED);
				//set UID
				fee.setUid(this.idGeneratorService.next(Fee.ATTACH_TYPE));
				//设置最后修改人信息
				fee.setAuthor(author);
				fee.setFileDate(fileDate);
				fee.setModifiedDate(modifiedDate);
				fee.setModifier(modifier);
				this.feeDao.save(fee);
			}
		}
	}

}