package com.xman.admin.modules.login;

import com.xman.admin.base.AbstractServiceTest;
import com.xman.admin.modules.member.Member;
import com.xman.admin.modules.member.MemberService;
import com.xman.admin.utils.TestObjectFactory;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.xman.admin.modules.login.LoginService.PASSWORD_CHANGE_MONTHS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@DisplayName("LoginServiceTest")
class LoginServiceTest extends AbstractServiceTest {

    @Autowired
    private PasswordEncoder encryptPassword;

    @Autowired
    private LoginService loginService;

    @Autowired
    private MemberService memberService;
    private String LOGIN_ID = "fLoginId";

    @Autowired
    private EntityManager em;

    @BeforeEach
    public void setup() {
        Member member = TestObjectFactory.getMember()
                .mbrId(LOGIN_ID)
                .mbrNm("test")
                .build();

        memberService.insertMember(member);
        initEm();
    }

    @Test
    void isExpirePasswordDuration() {
        LocalDate nowDate = LocalDate.now();
        Member member = memberService.findMember(LOGIN_ID);
        member.setPwApplyDt(LocalDateTime.now().minusMonths(PASSWORD_CHANGE_MONTHS + 1));
        em.flush();

        boolean expirePasswordDuration = loginService.isExpirePasswordDuration(member, nowDate);

        assertThat(expirePasswordDuration).isTrue();
    }

    @Test
    void isExpirePasswordDuration_비밀번호_날짜_안지남() {
        LocalDate nowDate = LocalDate.now();
        Member member = memberService.findMember(LOGIN_ID);
        member.setPwApplyDt(LocalDateTime.now().minusMonths(PASSWORD_CHANGE_MONTHS - 3));
        em.flush();

        boolean expirePasswordDuration = loginService.isExpirePasswordDuration(member, nowDate);

        assertThat(expirePasswordDuration).isFalse();
    }

    @Test
    void isExpirePasswordDuration_member_pwApplyDt_empty() {
        LocalDate nowDate = LocalDate.now();
        Member member = memberService.findMember(LOGIN_ID);
        member.setPwApplyDt(null);

        boolean expirePasswordDuration = loginService.isExpirePasswordDuration(member, nowDate);

        assertThat(expirePasswordDuration).isFalse();
    }

    @Test
    void isExistMember() {
        Member member = memberService.findMember(LOGIN_ID);
        String pwd = "password";
        member.setMbrPw(encryptPassword.encode(pwd));
        em.flush();

        boolean existMember = loginService.isExistMember(Member.builder().mbrId(LOGIN_ID).mbrPw(pwd).build());
        assertThat(existMember).isTrue();
    }
}