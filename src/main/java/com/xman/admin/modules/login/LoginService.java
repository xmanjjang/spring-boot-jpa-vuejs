package com.xman.admin.modules.login;

import com.xman.admin.modules.member.Member;
import com.xman.admin.modules.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class LoginService {
    private Logger log = LoggerFactory.getLogger(this.getClass());
    private final MemberRepository memberRepository;
    private final PasswordEncoder encryptPassword;
    private final SuccessLogin successLogin;
    private final FailLogin failLogin;
    public static final int PASSWORD_CHANGE_MONTHS = 3;
    public static final int LOGIN_BLOCK_MINUTE = 10;
    public static final int PASSWORD_FAIL_COUNT = 5;

    public void successLogin(Member member) {
        successLogin.successLogin(member);
    }

    public void failLogin(Member member) {
        failLogin.failLogin(member);
    }

    public boolean isExpirePasswordDuration(Member member, LocalDate nowDate) {
        Member findMember = memberRepository.findById(member.getMbrId()).orElseThrow(EntityNotFoundException::new);
        if (ObjectUtils.isEmpty(findMember.getPwApplyDt())) return false;

        LocalDate passwordChangeDate = findMember.getPwApplyDt().toLocalDate();
        LocalDate changeDate = nowDate.minusMonths(PASSWORD_CHANGE_MONTHS);

        return passwordChangeDate.isBefore(changeDate);
    }

    public boolean isExistMember(Member member) {
        Assert.notNull(member, "loginInfo is null");
        Assert.hasLength(member.getMbrPw(), "mbrPw is null");
        Assert.hasLength(member.getMbrId(), "mbrId is null");

        Optional<Member> byId = memberRepository.findById(member.getMbrId());
        return byId.isPresent() && encryptPassword.matches(member.getMbrPw(), byId.get().getMbrPw());
    }
}
