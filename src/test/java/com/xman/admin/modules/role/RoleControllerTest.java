package com.xman.admin.modules.role;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xman.admin.base.MockMvcTest;
import com.xman.admin.modules.role.dto.RoleDto;
import com.xman.admin.modules.role.dto.RoleMenuDto;
import com.xman.admin.utils.TestObjectFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import javax.persistence.EntityManager;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockMvcTest
@DisplayName("RoleControllerTest")
@WithUserDetails("admin")
class RoleControllerTest {
    private String ROLE_URL = "/roles";
    @Autowired
    private RoleService roleService;
    @Autowired
    private EntityManager em;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void findRoles() throws Exception {
        RoleDto.Request params = new RoleDto.Request();
        params.setUseYn("");

        mockMvc.perform(get(ROLE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(params))
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
        ;
    }

    @Test
    void insertRole() throws Exception {
        Role role = TestObjectFactory.getRole()
                .build();

        mockMvc.perform(post(ROLE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(role))
        )
                .andDo(print())
                .andExpect(status().isCreated())
        ;

        em.flush();
        em.clear();
    }

    @Test
    void updateRoles() throws Exception {
        Role role = initInsertRole();

        role.setCodeNm("updateRole");
        mockMvc.perform(put(ROLE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(role))
        )
                .andDo(print())
                .andExpect(status().isOk())
        ;
        em.flush();
        em.clear();

        Role findRole = roleService.findRole(role.getRoleCode());
        assertThat(findRole.getCodeNm()).isEqualTo(role.getCodeNm());
    }

    private Role initInsertRole() {
        Role role = TestObjectFactory.getRole()
                .build();

        roleService.saveRole(role);
        em.flush();
        em.clear();
        return role;
    }

    @Test
    void findRoleCode() throws Exception {
        Role role = initInsertRole();

        mockMvc.perform(get(ROLE_URL + "/" + role.getRoleCode()))
                .andDo(print())
                .andExpect(status().isOk())
        ;
    }

    @Test
    void findRoleMenu() throws Exception {
        Role role = initInsertRole();
        roleService.saveRoleMenus(role.getRoleCode(), Arrays.asList("AA01", "AA02", "AA03"));
        em.flush();
        em.clear();

        RoleMenuDto.Request params = new RoleMenuDto.Request();
        params.setRoleCd(role.getRoleCode());

        mockMvc.perform(get(ROLE_URL + "/roleMenus")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(params))
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
        ;
    }

    @Test
    void insertRoleMenus() throws Exception {
        Role role = initInsertRole();
        RoleMenuDto.Insert params = new RoleMenuDto.Insert();
        params.setRoleCd(role.getRoleCode());
        params.setMenuIds(Arrays.asList("AA01", "AA02", "AA03"));

        mockMvc.perform(post(ROLE_URL + "/roleMenus")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(params))
        )
        .andDo(print())
        .andExpect(status().isCreated())
        ;
        em.flush();
        em.clear();

        List<RoleMenu> findRoleMenus = roleService.findRoleMenus(role.getRoleCode());
        assertThat(findRoleMenus.size()).isEqualTo(3);
    }
}