package com.xman.admin.modules.member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
@Slf4j
public class MemberController {
    private final MemberService memberService;
    private final ModelMapper modelMapper;

    @GetMapping
    public ResponseEntity findMember(Pageable page) {
        Page<Member> members = memberService.findMembers(page);
        return ResponseEntity.ok(members);
    }

    @GetMapping("/{memberId}")
    public ResponseEntity findMember(@PathVariable String memberId) {
        Member member = memberService.findMember(memberId);
        return ResponseEntity.ok(member);
    }

    @PostMapping
    public ResponseEntity insertMember(@RequestBody MemberDto.Model member) {
        memberService.insertMember(modelMapper.map(member, Member.class));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping
    public ResponseEntity updateMember(@RequestBody MemberDto.Model member) {
        memberService.updateMember(modelMapper.map(member, Member.class));
        return ResponseEntity.ok().build();
    }

    @PutMapping("/password")
    public ResponseEntity updateMemberPwd(@RequestBody MemberDto.MemberPwd member) {
        memberService.updateMember(modelMapper.map(member, Member.class));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity deleteMember(List<String> memberIds) {
        memberService.deleteMembers(memberIds);
        return ResponseEntity.ok().build();
    }
}
