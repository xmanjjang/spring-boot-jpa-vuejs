package com.xman.admin.modules.member;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xman.admin.base.MockMvcTest;
import com.xman.admin.utils.TestObjectFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import javax.persistence.EntityManager;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockMvcTest
@DisplayName("RoleControllerTest")
@WithUserDetails("admin")
class MemberControllerTest {
    @Autowired
    private MemberService memberService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private final String MEMBER_URL = "/members";

    @Test
    void findMember() throws Exception {
        mockMvc.perform(get(MEMBER_URL)
                .param("page", "")
                .param("size", "5")
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
        ;
    }

    @Test
    void testFindMember() throws Exception {
        Member member = initCreateMember();

        mockMvc.perform(get(MEMBER_URL + "/" + member.getMbrId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mbrId").value(member.getMbrId()))
        ;

    }

    private Member initCreateMember() {
        Member member = TestObjectFactory.getMember()
                .mbrPw(null)
                .mbrPwOld1(null)
                .mbrPwOld2(null)
                .build();

        memberService.insertMember(member);
        em.flush();
        em.clear();
        return member;
    }

    @Test
    void insertMember() throws Exception {
        MemberDto.Model member = TestObjectFactory.getMemberDto()
                .newPw("12354")
                .build();

        mockMvc.perform(post(MEMBER_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(member))
        )
                .andDo(print())
                .andExpect(status().isCreated())
        ;
        em.flush();
        em.clear();

        Member findMember = memberService.findMember(member.getMbrId());
        assertThat(member.getMbrId()).isEqualTo(findMember.getMbrId());
    }

    @Test
    void updateMember() throws Exception {
        String memberName = "memberName";
        Member insertMember = initCreateMember();

        MemberDto.Model member = TestObjectFactory.getMemberDto()
                .mbrId(insertMember.getMbrId())
                .mbrNm(memberName)
                .build();

        mockMvc.perform(put(MEMBER_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(member))
        )
                .andDo(print())
                .andExpect(status().isOk())
        ;
        em.flush();
        em.clear();

        Member findMember = memberService.findMember(member.getMbrId());
        assertThat(member.getMbrNm()).isEqualTo(findMember.getMbrNm());
    }

    @Test
    void updateMemberPwd() throws Exception {
        String memberName = "memberName";
        Member insertMember = initCreateMember();

        MemberDto.Model member = TestObjectFactory.getMemberDto()
                .mbrId(insertMember.getMbrId())
                .mbrNm(memberName)
                .newPw("0987")
                .build();

        mockMvc.perform(put(MEMBER_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(member))
        )
                .andDo(print())
                .andExpect(status().isOk())
        ;
        em.flush();
        em.clear();

        Member findMember = memberService.findMember(member.getMbrId());
        assertThat(member.getMbrNm()).isEqualTo(findMember.getMbrNm());
        assertThat(passwordEncoder.matches(member.getNewPw(), findMember.getMbrPw()));
    }

    @Test
    void deleteMember() throws Exception {
        Member member = initCreateMember();

        MemberDto.Delete params = new MemberDto.Delete();
        params.setMemberIds(Arrays.asList(member.getMbrId()));

        mockMvc.perform(delete(MEMBER_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(params))
        )
        .andDo(print())
        .andExpect(status().isOk())
        ;

        Member findMember = memberService.findMember(member.getMbrId());
        assertThat(findMember).isNull();
    }

    @Test
    void findCompanies() throws Exception {
        mockMvc.perform(get(MEMBER_URL + "/companies"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}