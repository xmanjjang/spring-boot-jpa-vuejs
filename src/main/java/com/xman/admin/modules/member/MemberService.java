package com.xman.admin.modules.member;

import com.xman.admin.modules.member.repository.MemberRepository;
import com.xman.admin.modules.member.validator.MemberValidator;
import com.xman.admin.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {
    private final MemberRepository memberRepository;
    private final DateUtils dateUtils;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final MemberValidator memberValidator;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        Member member = memberRepository.findById(userName)
                .orElseThrow(() -> new UsernameNotFoundException("username Not found"));

        return new MemberAccount(member);
    }

    public Page<Member> findMembers(Pageable page) {
        return memberRepository.findAll(page);
    }

    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    public Member findMember(String mbrId) {
        return memberRepository.findById(mbrId).orElse(null);
    }

    public boolean isDuplicateMember(String mbrId, String tel) {
        if (StringUtils.isAllEmpty(mbrId, tel)) throw new IllegalArgumentException("mbrId, tel is null");

        memberRepository.findMember(Member.builder().mbrId(mbrId).tel(tel).build());
        return false;
    }

    public Member insertMember(Member member) {
        Assert.notNull(member, "member is null");
        Assert.notNull(member.getRoleCd(), "roleCd is null");
        Assert.notNull(member.getNewPw(), "newPw is null");
        Assert.notNull(member.getMbrNm(), "mbrNm is null");

        member.setMbrPw(passwordEncoder.encode(member.getNewPw()));
        member.setPwApplyDt(dateUtils.getToday());
        return memberRepository.save(member);
    }

    public void updateMember(Member member) {
        Assert.notNull(member, "member is null");
        Assert.hasLength(member.getMbrId(), "mbrId is null");

        Member findMember = memberRepository.findById(member.getMbrId())
                .orElseThrow(() -> new EntityNotFoundException());

        modelMapper.map(member, findMember);
        member.injectNewPasswordsTo(findMember, memberValidator, passwordEncoder);

        memberRepository.save(findMember);
    }

    public void updatePwd(Member member) {
        Assert.hasLength(member.getMbrId(), "mbrId is null");
        Assert.hasLength(member.getNewPw(), "newPwd is null");

        Member findMember = memberRepository.findById(member.getMbrId())
                .orElseThrow(() -> new EntityNotFoundException());

        member.injectNewPasswordsTo(findMember, memberValidator, passwordEncoder);
        memberRepository.save(findMember);
    }

    public void deleteMembers(List<String> mbrIdList) {
        Assert.notNull(mbrIdList, "mbrIdList is null");
        Assert.isTrue(mbrIdList.size() > 0, "mbrIdList is empty");

        memberRepository.deleteListById(mbrIdList);
    }

    public List<String> findCompanies() {
        return memberRepository.findCompanies();
    }
}
