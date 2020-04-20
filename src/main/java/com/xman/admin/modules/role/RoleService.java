package com.xman.admin.modules.role;

import com.xman.admin.constants.SystemStatusCode;
import com.xman.admin.constants.UseYnCode;
import com.xman.admin.exception.BizException;
import com.xman.admin.modules.member.Member;
import com.xman.admin.modules.member.MemberService;
import com.xman.admin.modules.role.respository.RoleMenuRepository;
import com.xman.admin.modules.role.respository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    private final RoleMenuRepository roleMenuRepository;
    private final MemberService memberService;

    public List<Role> findRoles(String useYn) {
        return roleRepository.findRoles(useYn);
    }

    public Role findRole(String roleCode) {
        return roleRepository.findById(roleCode).orElse(null);
    }

    public List<RoleMenu> findRoleMenus(String roleCode) {
        Assert.hasLength(roleCode, "roleCode is null");
        return roleMenuRepository.findByRoleCode(roleCode);
    }

    @Transactional
    public int saveRoleMenus(String roleCode, List<String> menuIdList) {
        Assert.hasLength(roleCode, "roleCode is null");
        Assert.notNull(menuIdList, "menuIdList is null");

        //해당 권한코드 roleCode 전체 delete
        roleMenuRepository.deleteByRoleCode(roleCode);

        List<RoleMenu> saveRoleMenus = menuIdList.stream().map(menuId -> RoleMenu.builder().id(RoleMenuId.builder().menuId(menuId).roleCd(roleCode).build()).build()).collect(Collectors.toList());
        List<RoleMenu> roleMenus = roleMenuRepository.saveAll(saveRoleMenus);

        return roleMenus.size();
    }

    public void saveRole(Role role) {
        Assert.notNull(role, "role is null");
        Assert.hasLength(role.getRoleCode(), "roleCode is null");
        if (StringUtils.isEmpty(role.getManagerYn())) role.setManagerYn("N");

        roleRepository.save(role);
    }

    public void updateRole(Role role) {
        Assert.notNull(role, "role is null");
        Assert.hasLength(role.getRoleCode(), "roleCode is null");

        if (role.getUseYn().equalsIgnoreCase(UseYnCode.N.name()))
            checkUsingRoleCodeInMbrList(role.getRoleCode());

        roleRepository.save(role);
    }

    public boolean isDuplicateRoleCode(String roleCode) {
        Assert.hasLength(roleCode, "roleCode is null");

        int duplicateRoleCode = roleRepository.countByRoleCode(roleCode);

        return duplicateRoleCode > 0;
    }

    private void checkUsingRoleCodeInMbrList(String roleCode) {
        List<Member> mbrInfoList = memberService.findMembers();

        String findUsers = mbrInfoList.stream()
                .filter(mbrInfo -> mbrInfo.getRoleCd()
                        .equalsIgnoreCase(roleCode))
                .map(mbrInfo -> mbrInfo.getMbrId())
                .collect(Collectors.joining(","));

        if (StringUtils.isNotEmpty(findUsers))
            throw new BizException(String.format("[%s] 사용자가 %s 코드를 사용하고 있습니다. 권한 변경 후 미사용으로 변경 해 주세요", findUsers, roleCode));

    }

    public void checkDisabledRoleCode(String roleCode) {
        List<Role> roles = this.findRoles(UseYnCode.N.name());
        boolean anyMatch = roles.stream()
                .anyMatch(authInfo -> authInfo.getRoleCode()
                        .equalsIgnoreCase(roleCode));
        if (anyMatch) throw new BizException(SystemStatusCode.FAIL_LOGIN, "사용 중인 권한 코드가 미사용 상태입니다. 관리자에게 문의해 주세요");
    }
}
