package com.xman.admin.modules.login;

import com.xman.admin.constants.SystemStatusCode;
import com.xman.admin.constants.UseYnCode;
import com.xman.admin.exception.BizException;
import com.xman.admin.modules.member.Member;
import com.xman.admin.modules.role.Role;
import com.xman.admin.modules.role.RoleService;
import com.xman.admin.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.List;

import static com.xman.admin.modules.login.LoginService.LOGIN_BLOCK_MINUTE;
import static com.xman.admin.modules.login.LoginService.PASSWORD_FAIL_COUNT;

@Component
@RequiredArgsConstructor
public class SuccessLogin {
    private Logger log = LoggerFactory.getLogger(this.getClass());
    private final RoleService roleService;
    private final DateUtils dateUtils;

    public void successLogin(Member member) {
        Assert.notNull(member, "selected loginInfo is null");
        LocalDateTime nowDate = LocalDateTime.now();

        roleService.checkDisabledRoleCode(member.getRoleCd());

        if (!"Y".equalsIgnoreCase(member.getUseYn())) {
            this.processLocked(member, nowDate);
        } else {
            this.processUnLocked(member, nowDate);
        }

        member.updateLoginEndDate();
    }

    public void processLocked(Member member, LocalDateTime nowDate) {
        Assert.hasLength(member.getMbrId(), "mbrId is empty");
        Assert.hasLength(member.getLoginFailCnt(), "loginFailCnt is empty");
        Assert.notNull(member.getLoginFailCnt(), "loginFailDate is empty");

        int pwdFailCnt = NumberUtils.toInt(member.getLoginFailCnt(), 0);
        long minute = dateUtils.getLeftMinuteBetween(member.getLoginFailDt(), nowDate);

        // 10분 초과 5회 이상 계정 잠금해제
        if (minute > LOGIN_BLOCK_MINUTE && pwdFailCnt >= PASSWORD_FAIL_COUNT) {
            log.info("===== Success Login =====");
            member.updateUserLockAndResetFailCnt();
        } else if (pwdFailCnt == 0) {
            throw new BizException(SystemStatusCode.LOGIN_FAIL_LOCK, "로그인 실패 - 아이디 잠금상태");
        } else {
            throw new BizException(SystemStatusCode.LOGIN_FAIL_LOCK_LEFT_TIME, String.format("로그인전 인증 실패 (%s분 미만 잠금상태)", LOGIN_BLOCK_MINUTE));
        }
    }

    public void processUnLocked(Member member, LocalDateTime nowDate) {
        Assert.hasLength(member.getMbrId(), "mbrId is empty");

        int pwdFailCnt = NumberUtils.toInt(member.getLoginFailCnt(), 0);
        long minute = dateUtils.getLeftMinuteBetween(member.getLoginFailDt(), nowDate);

        //  실패 n회 이상 로그인제한 n분 이하
        if (pwdFailCnt >= PASSWORD_FAIL_COUNT && minute <= LOGIN_BLOCK_MINUTE) {
            throw new BizException(SystemStatusCode.LOGIN_FAIL_LOCK_LEFT_TIME, String.format("로그인전 인증 실패 (%s분 미만 잠금상태)", LOGIN_BLOCK_MINUTE));
        }

        log.info("===== Success Login =====");
        member.updateUserUnLockAndResetFailCnt();
    }
}
