package com.xman.admin.modules.member;

import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Arrays;

@Getter
public class MemberAccount extends User {
    private Member member;

    public MemberAccount(Member member) {
        super(member.getMbrNm(), member.getMbrPw(), Arrays.asList(new SimpleGrantedAuthority(member.getRoleCd())));
        this.member = member;
    }
}
