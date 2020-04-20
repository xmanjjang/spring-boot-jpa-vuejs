package com.xman.admin.service;

import com.xman.admin.base.AbstractServiceTest;
import com.xman.admin.modules.member.Member;
import com.xman.admin.modules.member.MemberService;
import com.xman.admin.modules.member.repository.MemberRepository;
import com.xman.admin.utils.TestObjectFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("회원서비스 테스트")
@SpringBootTest
@Transactional
class MemberServiceTest extends AbstractServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MemberService memberService;
    private Member MEMBER;

    @BeforeEach
    public void setUp() {
        MEMBER = TestObjectFactory.getMember()
                .mbrPw(passwordEncoder.encode("1111"))
                .mbrPwOld1(passwordEncoder.encode("222"))
                .mbrPwOld2(passwordEncoder.encode("33"))
                .build();

        memberRepository.saveAndFlush(MEMBER);
    }

    @Test
    void loadUserByUsername() {
        UserDetails userDetails = memberService.loadUserByUsername(MEMBER.getMbrId());

        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(MEMBER.getMbrNm());
    }

    @DisplayName("이름으로 가져오기_username Not found 오류")
    @Test
    void usernameNotFound() {
        UsernameNotFoundException notfound = assertThrows(UsernameNotFoundException.class, () -> {
            UserDetails userDetails = memberService.loadUserByUsername("NOTFOUND");
        });

        assertThat(notfound.getMessage()).isEqualTo("username Not found");
    }

    @DisplayName("insertMember_정상케이스")
    @Test
    void insertMember() {
        Member member = TestObjectFactory.getMember()
                .mbrNm("입력")
                .mbrId("insertId")
                .build();

        memberService.insertMember(member);
        Member findMember = memberRepository.findById(member.getMbrId())
                .get();

        assertThat(findMember.getMbrNm()).isEqualTo(member.getMbrNm());
    }

    @DisplayName("insertMember_member is null")
    @Test
    void IllegalArgumentException_member_isNull() {
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () -> {
            memberService.insertMember(null);
        });

        assertThat(illegalArgumentException.getMessage()).isEqualTo("member is null");
    }

    @DisplayName("insertMember_roleCd is null")
    @Test
    void IllegalArgumentException_roleCd_isNull() {
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () -> {
            Member member = TestObjectFactory.getMember()
                    .roleCd(null)
                    .build();

            memberService.insertMember(member);
        });

        assertThat(illegalArgumentException.getMessage()).isEqualTo("roleCd is null");
    }

    @DisplayName("insertMember_mbrPw is null")
    @Test
    void IllegalArgumentException_mbrPw_isNull() {
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () -> {
            Member member = TestObjectFactory.getMember()
                    .mbrPw(null)
                    .build();

            memberService.insertMember(member);
        });

        assertThat(illegalArgumentException.getMessage()).isEqualTo("mbrPw is null");
    }

    @DisplayName("insertMember_mbrNm is null")
    @Test
    void IllegalArgumentException_mbrNm_isNull() {
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () -> {
            Member member = TestObjectFactory.getMember()
                    .mbrNm(null)
                    .build();
            memberService.insertMember(member);
        });

        assertThat(illegalArgumentException.getMessage()).isEqualTo("mbrNm is null");
    }

    @Test
    @DisplayName("updateMember_정상케이스")
    public void updateMember() {
        Member member = TestObjectFactory.getMember()
                .mbrId(MEMBER.getMbrId())
                .mbrNm("수정11")
                .build();

        memberService.updateMember(member);
        initEm();

        Member findMember = memberRepository.findById(MEMBER.getMbrId())
                .get();
        assertThat(findMember.getMbrNm()).isEqualTo(member.getMbrNm());
    }


    @Test
    @DisplayName("updateMember_비밀번호변경")
    public void updateMember_a() {
        Member member = TestObjectFactory.getMember()
                .mbrId(MEMBER.getMbrId())
                .mbrNm("수정11")
                .newPw("1234")
                .build();

        memberService.updateMember(member);
        initEm();

        Member findMember = memberRepository.findById(MEMBER.getMbrId()).get();
        assertThat(findMember.getMbrNm()).isEqualTo(member.getMbrNm());
        assertThat(passwordEncoder.matches(member.getNewPw(), findMember.getMbrPw()));
    }

    @Test
    @DisplayName("updatePwd")
    public void updatePwd() {
        Member member = Member.builder()
                .mbrId(MEMBER.getMbrId())
                .newPw("1234")
                .build();

        memberService.updatePwd(member);
        initEm();

        Member findMember = memberRepository.findById(MEMBER.getMbrId()).get();
        assertThat(passwordEncoder.matches(member.getNewPw(), findMember.getMbrPw()));
    }


    @Test
    @DisplayName("deleteMember")
    public void deleteMembers() {
        String deleteID = "deleteID";
        memberRepository.saveAndFlush(TestObjectFactory.getMember()
                .mbrId(deleteID)
                .build());

        List<String> memberIds = Arrays.asList(MEMBER.getMbrId(), deleteID);
        memberService.deleteMembers(memberIds);

        List<Member> allById = memberRepository.findAllById(memberIds);
        assertThat(allById).size().isEqualTo(0);
    }

    @Test
    @DisplayName("findCompanies")
    public void findCompanies() {
        List<String> allCompanies = memberService.findCompanies();
        assertThat(allCompanies.size()).isGreaterThan(0);
        assertThat(allCompanies).contains("company");
    }

    @Test
    @DisplayName("findMembers")
    public void findMembers() {
        List<Member> members = memberService.findMembers();
        System.out.println("members = " + members);

        assertThat(members.size()).isGreaterThan(0);
    }

    @Test
    @DisplayName("findMembers_paging")
    public void findMembersWithPaging() {
        PageRequest page = PageRequest.of(1, 5);
        Page<Member> members = memberService.findMembers(page);
        System.out.println("members = " + members.getContent());

        assertThat(members.getTotalElements()).isGreaterThan(0);
    }
}
