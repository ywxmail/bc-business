package cn.bc.business.mix.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.business.mix.dao.InfoCenterDao;

/**
 * 信息中心综合查询
 * 
 * @author dragon
 * 
 */
public class InfoCenterServiceImpl implements InfoCenterService {
	private InfoCenterDao infoCenterDao;

	@Autowired
	public void setInfoCenterDao(InfoCenterDao infoCenterDao) {
		this.infoCenterDao = infoCenterDao;
	}

	public JSONArray findCars(Long unitId, Long motorcadeId) {
		return this.infoCenterDao.findCars(unitId, motorcadeId);
	}

	public JSONArray findCars(String searchType, String searchText) {
		return this.infoCenterDao.findCars(searchType, searchText);
	}

	public JSONObject findCarDetail(Long carId) throws Exception {
		return this.infoCenterDao.findCarDetail(carId);
	}
}
