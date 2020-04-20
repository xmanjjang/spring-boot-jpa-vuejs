package com.xman.admin.modules.role;

import com.xman.admin.base.AbstractServiceTest;
import com.xman.admin.constants.UseYnCode;
import com.xman.admin.exception.BizException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Menu service")
@SpringBootTest
@Transactional
class RoleServiceTest extends AbstractServiceTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private RoleService roleService;

    @Test
    void selectRoleMstList_useYn_Y() {
        List<Role> roles = roleService.findRoles("Y");
        System.out.println("roles = " + roles);

        assertThat(roles).size().isGreaterThan(0);
    }

    @Test
    void selectRoleMstList_useYn_empty() {
        List<Role> roles = roleService.findRoles(null);
        System.out.println("roles = " + roles);

        assertThat(roles).size().isGreaterThan(0);
    }

    @Test
    void findRoleMenus() {
        String roleCode = "R-001";
        List<RoleMenu> roleMenus = roleService.findRoleMenus(roleCode);

        System.out.println("roleMenus = " + roleMenus);
        assertThat(roleMenus.size()).isGreaterThan(0);
    }

    @Test
    void saveRoleMenus() {
        List<String> menuIdList = Arrays.asList("test1", "test2", "test3");

        int saveCnt = roleService.saveRoleMenus("R-999", menuIdList);
        em.flush();
        em.clear();

        assertThat(saveCnt).isEqualTo(3);
    }

    @Test
    void insertRoleMst() {
        Role role = Role.builder()
                .roleCode("T-9999")
                .codeNm("Test Code")
                .managerYn("Y")
                .useYn(UseYnCode.Y.name()).build();

        roleService.saveRole(role);
        em.flush();
        em.clear();

        List<Role> roleMsts = roleService.findRoles("");
        assertThat(roleMsts.contains(role)).isTrue();
    }

    @Test
    void updateRoleMst() {
        Role role = Role.builder()
                .roleCode("T-9999")
                .codeNm("Test Code")
                .managerYn("Y")
                .useYn(UseYnCode.Y.name()).build();
        roleService.saveRole(role);
        em.flush();
        em.clear();

        String compareCodeNm = "updateCode";
        role.setCodeNm(compareCodeNm);
        roleService.updateRole(role);
        em.flush();
        em.clear();

        List<Role> roleMsts = roleService.findRoles("");
        Role updatedRole = roleMsts.stream().filter(role1 -> role1.getRoleCode().equals(role.getRoleCode())).findFirst().get();
        assertThat(updatedRole.getCodeNm()).isEqualTo(compareCodeNm);
    }

    @Test
    void updateRoleMst_not_exist_member() {
        assertThrows(BizException.class, () -> {
            Role role = Role.builder()
                    .roleCode("R-001")
                    .codeNm("Test Code")
                    .managerYn("Y")
                    .useYn(UseYnCode.N.name()).build();
            roleService.updateRole(role);
        });
    }

    @Test
    void isDuplicateRoleCode() {
        boolean duplicateRoleCode = roleService.isDuplicateRoleCode("R-001");
        assertThat(duplicateRoleCode).isTrue();
    }

    @Test
    void isDuplicateRoleCode_not_exist_roleCode() {
        boolean duplicateRoleCode = roleService.isDuplicateRoleCode("T-901");
        assertThat(duplicateRoleCode).isFalse();
    }
}