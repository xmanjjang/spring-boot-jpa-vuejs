package com.xman.admin.modules.member.repository;

import com.xman.admin.modules.member.Member;

import java.util.List;

public interface MemberCustomRepository {
    List<Member> findMember(Member member);
}
