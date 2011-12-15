package cn.bc.business.arrange.service;

import cn.bc.business.arrange.dao.ArrangeMemberDao;
import cn.bc.business.arrange.domain.ArrangeMember;
import cn.bc.core.service.DefaultCrudService;

/**
 * 安排的考勤成员实例Service的实现
 * 
 * @author dragon
 * 
 */
public class ArrangeMemberServiceImpl extends DefaultCrudService<ArrangeMember>
		implements ArrangeMemberService {
	private ArrangeMemberDao arrangeMemberDao;

	public void setArrangeMemberDao(ArrangeMemberDao arrangeMemberDao) {
		this.arrangeMemberDao = arrangeMemberDao;
		this.setCrudDao(arrangeMemberDao);
	}
}
