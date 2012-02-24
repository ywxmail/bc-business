package cn.bc.business.mix.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

import cn.bc.identity.domain.FileEntityImpl;

/**
 * 信息中心综合查询
 * 
 * @author dragon
 */
@Entity
@Table(name = "BS_INSURANCE_TYPE")
public class InfoCenter extends FileEntityImpl {
	private static final long serialVersionUID = 1L;

}