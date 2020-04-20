package com.xman.admin.modules.member.validator;

import com.xman.admin.exception.BizException;
import com.xman.admin.modules.member.Member;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberValidator {
    private final PasswordEncoder passwordEncoder;

    public void checkContainsPwdInUserInfo(String newPw, Member member) {
        // userid 사용 확인
        if (StringUtils.containsAny(newPw, member.getMbrId(), member.getTel()))
            throw new BizException("비빌번호에 개인정보가 포함되어 있으면 안됩니다.");
    }

    public void checkUsedPassword(String encNewPw, Member member) {
        // 이전에 사용했던 비번 확인
        if (match(encNewPw, member.getMbrPw()) ||
                match(encNewPw, member.getMbrPwOld1()) ||
                match(encNewPw, member.getMbrPwOld2())) {
            throw new BizException("이전에 사용한 비밀번호 입니다.");
        }
    }

    private boolean match(String pwd1, String pwd2) {
        if(StringUtils.isEmpty(pwd2) || !pwd2.contains("{")) return false;

        return passwordEncoder.matches(pwd1, pwd2);
    }
}
