package com.xman.admin.modules.member.form;

import com.xman.admin.modules.member.Member;
import com.xman.admin.modules.member.validator.MemberValidator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberForm {
    @Size(max = 10, message = "mbrId의 크기가 10보다 큽니다")
    @NotNull(message = "mbrId의 값이 없습니다.")
    private String mbrId;

    @Size(max = 40, message = "mbrNm의 크기가 40보다 큽니다")
    private String mbrNm;

    @Size(max = 250, message = "email의 크기가 250보다 큽니다")
    @Email(message = "email 정보가 유효하지 않습니다.")
    private String email;

    @Size(max = 20, message = "전화번호의 크기가 20보다 큽니다")
    private String tel;

    @Size(max = 10, message = "role Code의 크기가 10보다 큽니다")
    private String roleCd;

    private String loginEndDt;

    @Size(max = 1, message = "사용유뮤의 크기가 1보다 큽니다")
    private String useYn;

    private String loginFailCnt;

    private String applyDt;
    private String codeNm;

    @Size(max = 512, message = "mbrPw의 크기가 512보다 큽니다")
    private String newPw;

    private String remark;
    private String mbrCompany;
    private String mbrDptmt;

    public void injectNewPasswordsTo(Member member, MemberValidator validator, PasswordEncoder passwordEncoder) {
        if (StringUtils.isEmpty(this.getNewPw())) return;

        validator.checkContainsPwdInUserInfo(this.getNewPw(), member);
        member.changePasswords(passwordEncoder.encode(this.getNewPw()), validator);
    }
}

