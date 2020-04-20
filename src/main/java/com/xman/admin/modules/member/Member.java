package com.xman.admin.modules.member;

import com.xman.admin.constants.UseYnCode;
import com.xman.admin.modules.member.validator.MemberValidator;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.domain.Persistable;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@Entity
@Table(name = "TB_MBR_INFO")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "mbrId")
@DynamicUpdate
@ToString
public class Member implements Persistable {
    @Id
    @Column(name = "MBR_ID", nullable = false, length = 10)
    private String mbrId;

    @Column(name = "USE_YN", nullable = false, length = 1)
    private String useYn;

    @Column(name = "ROLE_CD", nullable = false, length = 10)
    private String roleCd;

    @Column(name = "MBR_NM", nullable = false, length = 40)
    private String mbrNm;

    @Column(name = "MBR_PW", nullable = false, length = 512)
    private String mbrPw;

    @Column(name = "EMAIL", nullable = false, length = 250)
    private String email;

    @Column(name = "TEL", nullable = false, length = 20)
    private String tel;

    @Column(name = "COMPANY")
    private String company;

    @Column(name = "DEPARTMENT")
    private String department;

    @Column(name = "LOGIN_FAIL_CNT")
    private String loginFailCnt;

    @Column(name = "LOGIN_FAIL_DT")
    private LocalDateTime loginFailDt;

    @Column(name = "APPLY_DT")
    private LocalDateTime applyDt;

    @Column(name = "LOGIN_END_DT")
    private LocalDateTime loginEndDt;

    @Column(name = "PW_APPLY_DT", nullable = false)
    private LocalDateTime pwApplyDt;

    @Column(name = "REMARK", length = 250)
    private String remark;

    @Column(name = "MBR_PW_OLD1", length = 512)
    private String mbrPwOld1;

    @Column(name = "MBR_PW_OLD2", length = 512)
    private String mbrPwOld2;

    @Transient
    private String newPw;

    @Override
    public Object getId() {
        return getMbrId();
    }

    @Override
    public boolean isNew() {
        return getPwApplyDt() != null;
    }

    public void changePasswords(String encNewPw, MemberValidator validator) {
        validator.checkUsedPassword(encNewPw, this);

        this.setMbrPwOld2(this.getMbrPwOld1());
        this.setMbrPwOld1(this.getMbrPw());
        this.setMbrPw(encNewPw);
        this.setPwApplyDt(LocalDateTime.now());
    }

    public void updateUserUnLockAndResetFailCnt() {
        this.setLoginFailCnt("0");
        this.setUseYn(UseYnCode.Y.name());
    }

    public void updateLoginFailCnt() {
        this.setLoginFailDt(LocalDateTime.now());
        this.setLoginFailCnt(String.valueOf(NumberUtils.toInt(this.getLoginFailCnt(), 0) + 1));
    }

    public void updateUserLock() {
        this.setUseYn(UseYnCode.N.name());
    }

    public void updateLoginEndDate() {
        this.setLoginEndDt(LocalDateTime.now());
//        mapper.updateLoginEndDt(member);        // 마지막 접속날짜 등록
    }

    public void updateUserLockAndResetFailCnt() {
        this.setLoginFailCnt("0");
        this.setUseYn(UseYnCode.Y.name());
    }

    public void injectNewPasswordsTo(Member member, MemberValidator validator, PasswordEncoder passwordEncoder) {
        if (StringUtils.isEmpty(this.getNewPw())) return;

        validator.checkContainsPwdInUserInfo(this.getNewPw(), member);
        member.changePasswords(passwordEncoder.encode(this.getNewPw()), validator);
    }

}
