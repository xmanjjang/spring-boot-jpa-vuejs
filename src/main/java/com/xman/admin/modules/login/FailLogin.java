package com.xman.admin.modules.login;

import com.xman.admin.constants.SystemStatusCode;
import com.xman.admin.exception.BizException;
import com.xman.admin.modules.member.Member;
import com.xman.admin.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

import static com.xman.admin.modules.login.LoginService.LOGIN_BLOCK_MINUTE;
import static com.xman.admin.modules.login.LoginService.PASSWORD_FAIL_COUNT;

@Component
@RequiredArgsConstructor
@Transactional
public class FailLogin {
    private final DateUtils dateUtils;

    public void failLogin(Member member) {
        Assert.notNull(member, "loginInfo is null");

        if ("Y" .equalsIgnoreCase(member.getUseYn())) {
            this.processUnlocked(member);
        } else {
            this.processLocked(member, LocalDateTime.now());
        }

        throw new BizException(SystemStatusCode.FAIL_LOGIN, "### 로그인 실패");
    }

    public void processUnlocked(Member member) {
        int pwdFailCnt = NumberUtils.toInt(member.getLoginFailCnt());
        if (pwdFailCnt >= PASSWORD_FAIL_COUNT) {    // 5회 오류나면 아이디 잠금 ( 사용여부 N 처리)
            member.updateUserLock();
            throw new BizException(SystemStatusCode.LOGIN_FAIL_LOCKED_USER_ID, "### 로그인 실패 - 아이디 잠금");
        }

        member.updateLoginFailCnt();
    }

    public void processLocked(Member member, LocalDateTime nowDate) {
        int pwdFailCnt = NumberUtils.toInt(member.getLoginFailCnt());
        long minute = dateUtils.getLeftMinuteBetween(member.getLoginFailDt(), nowDate);

        if (minute > LOGIN_BLOCK_MINUTE && pwdFailCnt >= PASSWORD_FAIL_COUNT) {
//			logger.info("### 로그인실패 - 10분경과 아이디 잠금해제 ");
            member.updateUserUnLockAndResetFailCnt(); // ### 로그인실패 - 10분경과 아이디 잠금해제
            member.updateLoginFailCnt();
            //log.debug("### 비밀번호 오류 회수 없데이트 : " + uptCnt);
        } else {
            throw new BizException("### 로그인 실패 - 아이디 잠금상태");
        }
    }
}
