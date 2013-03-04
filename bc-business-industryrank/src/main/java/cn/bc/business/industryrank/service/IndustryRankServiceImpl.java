package cn.bc.business.industryrank.service;

import org.springframework.beans.factory.annotation.Autowired;

import cn.bc.business.industryrank.dao.IndustryRankDao;
import cn.bc.business.industryrank.domain.IndustryRank;
import cn.bc.core.service.DefaultCrudService;

/**
 * 行业排名 service 实现
 * 
 * @author dragon
 * 
 */
public class IndustryRankServiceImpl extends DefaultCrudService<IndustryRank>
		implements IndustryRankService {
	private IndustryRankDao industryRankDao;

	@Autowired
	public void setIndustryRankDao(IndustryRankDao industryRankDao) {
		this.industryRankDao = industryRankDao;
	}
}
