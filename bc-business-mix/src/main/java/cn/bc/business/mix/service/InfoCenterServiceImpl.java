package cn.bc.business.mix.service;

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

}
