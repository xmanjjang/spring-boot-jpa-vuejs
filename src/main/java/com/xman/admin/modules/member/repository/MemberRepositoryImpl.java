package com.xman.admin.modules.member.repository;

import com.xman.admin.modules.member.Member;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class MemberRepositoryImpl extends QuerydslRepositorySupport implements MemberCustomRepository {
    public MemberRepositoryImpl() {
        super(Member.class);
    }

    @Override
    public List<Member> findMember(Member build) {
        return null;
    }
}
