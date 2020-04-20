package com.xman.admin.utils;

import com.xman.admin.constants.RoleCode;
import com.xman.admin.constants.UseYnCode;
import com.xman.admin.modules.member.Member;
import com.xman.admin.modules.member.MemberDto;
import com.xman.admin.modules.menu.Menu;
import com.xman.admin.modules.role.Role;
import com.xman.admin.modules.role.RoleMenu;
import com.xman.admin.modules.role.RoleMenuId;

import java.time.LocalDateTime;

public class TestObjectFactory {
    public static Member.MemberBuilder getMember() {
        return Member.builder()
                .mbrId("testAdmin")
                .mbrNm("어드민")
                .mbrPw("12345")
                .useYn("Y")
                .roleCd(RoleCode.ADMIN.name())
                .email("test@email.com")
                .tel("01012341234")
                .pwApplyDt(LocalDateTime.now())
                .mbrPw("mbrPw")
                .company("company")
                .mbrPwOld1("mbrPwOld1")
                .mbrPwOld2("mbrPwOld2")
                .newPw("mbrPw")
                ;
    }

    public static MemberDto.Model.ModelBuilder getMemberDto() {
        return MemberDto.Model.builder()
                .mbrId("testAdmin")
                .mbrNm("어드민")
                .useYn("Y")
                .roleCd(RoleCode.ADMIN.name())
                .email("test@email.com")
                .tel("01012341234")
                .company("company")
                ;
    }

    public static Menu.MenuBuilder getUpMenu() {
        return Menu.builder()
                .menuId("ZZ")
                .menuNm("1depthMenu")
                .depth("1")
                .ordNo("0")
                .useYn(UseYnCode.Y.name())
                ;
    }

    public static Menu.MenuBuilder getSubMenu() {
        return Menu.builder()
                .menuId("ZZ01")
                .menuNm("2depthMenu")
                .depth("2")
                .ordNo("0")
                .upmenuId("ZZ")
                .useYn(UseYnCode.Y.name())
                ;
    }

    public static Role.RoleBuilder getRole() {
        return Role.builder()
                .roleCode("Z-001")
                .useYn(UseYnCode.Y.name())
                .codeNm("TestRole")
                .managerYn("Y")
                ;
    }

    public static RoleMenu.RoleMenuBuilder getRoleMenu() {
        return RoleMenu.builder()
                .id(RoleMenuId.builder()
                        .menuId("AA01")
                        .roleCd("Z-001")
                        .build())
                .initDt(LocalDateTime.now())
                .initMbr("admin")
                ;
    }

}
