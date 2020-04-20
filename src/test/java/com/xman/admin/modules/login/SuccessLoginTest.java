package com.xman.admin.modules.login;

import com.xman.admin.base.AbstractServiceTest;
import com.xman.admin.constants.SystemStatusCode;
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
@DisplayName("SuccessLogin")
class SuccessLoginTest extends AbstractServiceTest {
    @Autowired
    private SuccessLogin successLogin;

    @Autowired
    private MemberService memberService;
    private String LOGIN_ID = "sLoginId";

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
    void processLocked_잠금해제() {
        LocalDateTime nowDate = LocalDateTime.now();
        Member member = memberService.findMember(LOGIN_ID);
        member.setLoginFailDt(nowDate.minusMinutes(LoginService.LOGIN_BLOCK_MINUTE + 1));
        member.setLoginFailCnt(String.valueOf(LoginService.PASSWORD_FAIL_COUNT + 1));
        member.setUseYn(UseYnCode.N.name());
        em.flush();

        successLogin.processLocked(member, nowDate);
        initEm();

        Member findMember = memberService.findMember(LOGIN_ID);
        assertThat(findMember.getUseYn()).isEqualTo(UseYnCode.Y.name());
        assertThat(findMember.getLoginFailCnt()).isEqualTo("0");
    }

    @Test
    void processLocked_로그인실패_아이디잠금() {
        LocalDateTime nowDate = LocalDateTime.now();
        Member member = memberService.findMember(LOGIN_ID);
        member.setLoginFailDt(nowDate.minusMinutes(LoginService.LOGIN_BLOCK_MINUTE - 1));
        member.setLoginFailCnt("0");
        member.setUseYn(UseYnCode.N.name());
        em.flush();

        BizException bizException = assertThrows(BizException.class, () -> {
            successLogin.processLocked(member, nowDate);
        });
        initEm();

        assertThat(bizException.getCode()).isEqualByComparingTo(SystemStatusCode.LOGIN_FAIL_LOCK);
    }

    @Test
    void processUnLocked() {
        LocalDateTime nowDate = LocalDateTime.now();
        Member member = memberService.findMember(LOGIN_ID);
        member.setLoginFailDt(nowDate.minusMinutes(LoginService.LOGIN_BLOCK_MINUTE - 3));
        member.setLoginFailCnt("0");
        member.setUseYn(UseYnCode.Y.name());
        em.flush();

        successLogin.processUnLocked(member, nowDate);
        initEm();

        Member findMember = memberService.findMember(LOGIN_ID);
        assertThat(findMember.getUseYn()).isEqualTo(UseYnCode.Y.name());
        assertThat(findMember.getLoginFailCnt()).isEqualTo("0");
    }


    @Test
    void processUnLocked_잠금상태_메시지() {
        LocalDateTime nowDate = LocalDateTime.now();
        Member member = memberService.findMember(LOGIN_ID);
        member.setLoginFailDt(nowDate.minusMinutes(LoginService.LOGIN_BLOCK_MINUTE - 3));
        member.setLoginFailCnt(String.valueOf(LoginService.PASSWORD_FAIL_COUNT + 1));
        member.setUseYn(UseYnCode.Y.name());
        em.flush();

        assertThrows(BizException.class, () -> {
            successLogin.processUnLocked(member, nowDate);
        });
        initEm();
    }
}