package cn.bc.business.arrange.service;

import cn.bc.business.arrange.dao.MemberDao;
import cn.bc.business.arrange.domain.Member;
import cn.bc.core.service.DefaultCrudService;

/**
 * 考勤成员Service的实现
 * 
 * @author dragon
 * 
 */
public class MemberServiceImpl extends DefaultCrudService<Member> implements
		MemberService {
	private MemberDao memberDao;

	public void setMemberDao(MemberDao memberDao) {
		this.memberDao = memberDao;
		this.setCrudDao(memberDao);
	}
}
