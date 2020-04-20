package com.xman.admin.modules.menu;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xman.admin.base.MockMvcTest;
import com.xman.admin.constants.UseYnCode;
import com.xman.admin.utils.TestObjectFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockMvcTest
@DisplayName("MenuControllerTest")
@WithUserDetails("admin")
class MenuControllerTest {
    @Autowired
    private MockMvc mockMvc;
    private String MENU_URL = "/menus";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MenuService menuService;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findMenus() throws Exception {
        mockMvc.perform(get("/menus?page=0&size=5"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value("5"))
        ;
    }

    @Test
    void selectLeftMenus() throws Exception {
        mockMvc.perform(get(MENU_URL + "/leftMenus"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
        ;
    }

    @Test
    void findUpMenus() throws Exception {
        mockMvc.perform(get(MENU_URL + "/upMenus"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
        ;
    }

    @Test
    void insertUpMenu() throws Exception {
        Menu menu = TestObjectFactory.getUpMenu()
                .menuNm("insertMenu")
                .depth("1")
                .useYn(UseYnCode.Y.name())
                .build();

        mockMvc.perform(post(MENU_URL + "/upMenus")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(menu))
        )
                .andDo(print())
                .andExpect(status().isCreated())
        ;

        entityManager.flush();
    }

    @Test
    void updateUpMenu() throws Exception {
        String menuId = "AA";
        String menuNm = "updateMenu";
        Menu menu = TestObjectFactory.getUpMenu()
                .menuId(menuId)
                .menuNm(menuNm)
                .useYn(UseYnCode.Y.name())
                .build();

        mockMvc.perform(put(MENU_URL + "/upMenus")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(menu))
        )
                .andDo(print())
                .andExpect(status().isOk())
        ;

        entityManager.flush();
        entityManager.clear();
        Menu updateMenu = menuService.findMenu(menuId);
        assertThat(updateMenu.getMenuNm()).isEqualTo(menuNm);
    }

    @Test
    void findSubMenus() throws Exception {
        mockMvc.perform(get(MENU_URL + "/subMenus"))
                .andDo(print())
                .andExpect(jsonPath("$").isArray())
        ;
    }

    @Test
    void findSubMenu() throws Exception {
        String menuId = "AA01";

        mockMvc.perform(get(MENU_URL + "/" + menuId))
                .andDo(print())
                .andExpect(jsonPath("$.menuId").value(menuId))
        ;
    }

    @Test
    void insertSubMenu() throws Exception {
        Menu subMenu = TestObjectFactory.getSubMenu()
                .menuNm("insertSubMenu")
                .menuId(null)
                .upmenuId("AA")
                .build();

        mockMvc.perform(post(MENU_URL + "/subMenus")
                .content(objectMapper.writeValueAsString(subMenu))
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isCreated())
        ;

        entityManager.flush();
        entityManager.clear();

        List<Menu> subMenus = menuService.findSubMenus();
        boolean match = subMenus.stream().anyMatch(menu -> subMenu.getMenuNm().equals(menu.getMenuNm()));
        assertThat(match).isTrue();
    }

    @Test
    void insertSubMenu_invalid_upMenuId() throws Exception {
        Menu subMenu = TestObjectFactory.getSubMenu()
                .menuNm("insertSubMenu")
                .menuId(null)
                .upmenuId("SS")
                .build();

        mockMvc.perform(post(MENU_URL + "/subMenus")
                .content(objectMapper.writeValueAsString(subMenu))
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;

        entityManager.flush();
    }

    @Test
    void updateSubMenu() throws Exception {
        Menu subMenu = TestObjectFactory.getSubMenu()
                .menuNm("subMenu")
                .menuId(null)
                .upmenuId("AA")
                .build();

        menuService.insertSubMenu(subMenu);
        entityManager.flush();
        entityManager.clear();

        subMenu.setMenuNm("updateSubMenu");
        mockMvc.perform(put(MENU_URL + "/subMenus")
                .content(objectMapper.writeValueAsString(subMenu))
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk())
        ;

        entityManager.flush();
        entityManager.clear();

        List<Menu> subMenus = menuService.findSubMenus();
        boolean match = subMenus.stream().anyMatch(menu -> subMenu.getMenuNm().equals(menu.getMenuNm()));
        assertThat(match).isTrue();
    }
}