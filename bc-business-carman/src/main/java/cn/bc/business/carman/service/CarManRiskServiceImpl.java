/**
 * 
 */
package cn.bc.business.carman.service;

import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.business.carman.dao.CarManRiskDao;
import cn.bc.business.carman.domain.CarMan;
import cn.bc.business.carman.domain.CarManRisk;
import cn.bc.core.service.DefaultCrudService;

/**
 * 司机人意险Service的实现
 * 
 * @author dragon
 */
public class CarManRiskServiceImpl extends DefaultCrudService<CarManRisk>
		implements CarManRiskService {
	private CarManRiskDao carManRiskDao;
	private CarManService carManService;

	@Autowired
	public void setCarManRiskDao(CarManRiskDao carManRiskDao) {
		this.carManRiskDao = carManRiskDao;
		this.setCrudDao(carManRiskDao);
	}
	
	@Autowired
	public void setCarManService(CarManService carManService) {
		this.carManService = carManService;
	}

	public Map<String, Object> getCarManInfo(String identity) {
		return this.carManRiskDao.getCarManInfo(identity);
	}

	public CarManRisk loadByCode(String code) {
		return this.carManRiskDao.loadByCode(code);
	}

	public CarManRisk loadByCompanyAndDate(String company, Calendar startDate,
			Calendar endDate) {
		return this.carManRiskDao.loadByCompanyAndDate(company, startDate,
				endDate);
	}

	public List<String> findRiskCompanies() {
		return this.carManRiskDao.findRiskCompanies();
	}

	public void doDeleteCarMan(Map<Long, List<Long>> info) {
		Set<Long> carManRiskIds= info.keySet();
		CarManRisk cmr;
		Set<CarMan> deleteCarMans;
		Set<CarMan> _carMans;
		Set<CarMan> carMans;
		for(Long id:carManRiskIds){
			cmr=this.carManRiskDao.load(id);
			_carMans=cmr.getInsurants();
			//当人意险司机的数量等于其将要删除的司机数量时 直接删除此人意险
			if(_carMans.size()==info.get(id).size()){
				this.carManRiskDao.delete(id);
			}else{
				deleteCarMans=new LinkedHashSet<CarMan>();
				carMans=new LinkedHashSet<CarMan>();
				for(Long man_id:info.get(id)){
					deleteCarMans.add(this.carManService.load(man_id));
				}
				//删除司机
				_carMans.removeAll(deleteCarMans);
				
				for(CarMan cm:_carMans){
					carMans.add(cm);
				}

				if (cmr.getInsurants() != null) {
					cmr.getInsurants().clear();
					cmr.getInsurants().addAll(carMans);
				} else {
					cmr.setInsurants(carMans);
				}
				
				this.carManRiskDao.save(cmr);
			}
		}
		
		
	}
}