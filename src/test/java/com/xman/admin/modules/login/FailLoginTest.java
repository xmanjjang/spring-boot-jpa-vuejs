package com.xman.admin.modules.login;

import com.xman.admin.base.AbstractServiceTest;
import com.xman.admin.constants.UseYnCode;
import com.xman.admin.exception.BizException;
import com.xman.admin.modules.member.Member;
import com.xman.admin.modules.member.MemberService;
import com.xman.admin.utils.TestObjectFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@DisplayName("FailLogin")
class FailLoginTest extends AbstractServiceTest {

    @Autowired
    private FailLogin failLogin;

    @Autowired
    private MemberService memberService;
    private String LOGIN_ID = "fLoginId";

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
    void failLogin() {
    }

    @Test
    void processUnlocked() {
        Member member = memberService.findMember(LOGIN_ID);

        failLogin.processUnlocked(member);
        initEm();

        Member loginId = memberService.findMember(LOGIN_ID);
        assertThat(loginId.getLoginFailCnt()).isEqualTo("1");
    }

    @Test
    void processUnlocked_empty_failCnt_잠금_횟수보다_크다() {
        Member member = memberService.findMember(LOGIN_ID);
        member.setLoginFailCnt(String.valueOf(LoginService.PASSWORD_FAIL_COUNT + 1));
        em.flush();

        assertThrows(BizException.class, () -> {
            failLogin.processUnlocked(member);
        });
        initEm();

        Member loginId = memberService.findMember(LOGIN_ID);
        assertThat(loginId.getUseYn()).isEqualTo("N");
    }

    @Test
    void processLocked_잠금_해제() {
        Member member = memberService.findMember(LOGIN_ID);
        member.setUseYn(UseYnCode.N.name());
        LocalDateTime nowDate = LocalDateTime.now();
        member.setLoginFailDt(nowDate.minusMinutes(LoginService.LOGIN_BLOCK_MINUTE + 1));
        member.setLoginFailCnt(String.valueOf(LoginService.PASSWORD_FAIL_COUNT + 1));
        em.flush();

        failLogin.processLocked(member, nowDate);
        initEm();

        Member loginId = memberService.findMember(LOGIN_ID);
        assertThat(loginId.getUseYn()).isEqualTo("Y");
        assertThat(loginId.getLoginFailCnt()).isEqualTo("1");
    }

    @Test
    void processLocked_잠금_상태() {
        Member member = memberService.findMember(LOGIN_ID);
        member.setUseYn(UseYnCode.N.name());
        LocalDateTime nowDate = LocalDateTime.now();
        member.setLoginFailDt(nowDate.minusMinutes(1));
        member.setLoginFailCnt(String.valueOf(LoginService.PASSWORD_FAIL_COUNT + 1));
        em.flush();

        assertThrows(BizException.class, () -> {
            failLogin.processLocked(member, nowDate);
        });
        initEm();

        Member loginId = memberService.findMember(LOGIN_ID);
        assertThat(loginId.getUseYn()).isEqualTo("N");
    }
}