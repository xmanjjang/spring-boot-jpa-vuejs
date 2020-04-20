package com.xman.admin.modules.menu;

import com.xman.admin.base.AbstractServiceTest;
import com.xman.admin.constants.UseYnCode;
import com.xman.admin.utils.TestObjectFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Menu service")
@SpringBootTest
@Transactional
class MenuServiceTest extends AbstractServiceTest {

    @Autowired
    private MenuService menuService;
    private String UPMENU_ID;

    @BeforeEach
    public void setup() {
        Menu upMenu = TestObjectFactory.getUpMenu()
                .depth("1")
                .build();

        UPMENU_ID = menuService.insertUpMenu(upMenu);
        initEm();
    }

    @Test
    void findMemberMenu() {
        String memberId = "admin";
        List<Menu> memberMenu = menuService.findMenuBy(memberId);
        System.out.println("memberMenu = " + memberMenu);
    }

    @Test
    public void findMenus() {
        PageRequest pageable = PageRequest.of(0, 5);
        List<Menu> menuList = menuService.findMenus("Y", pageable);

        Menu rootMenu = menuList.get(0);
        assertThat(rootMenu.getMenuId()).isEqualTo("AA");
        assertThat(rootMenu.getUpmenuId()).isNull();

        Menu subMenu = menuList.get(1);
        assertThat(subMenu.getMenuId()).isEqualTo("AA01");
        assertThat(subMenu.getMenuNm()).isEqualTo("사용자관리");
        assertThat(subMenu.getUpmenuNm()).isEqualTo("환경설정");

        assertThat(menuList.size()).isEqualTo(5);

        System.out.println("menuList = " + menuList);
    }

    @Test
    public void findRoleMenus() {
        List<Menu> roleMenuList = menuService.findSubMenus();
        System.out.println("roleMenuList = " + roleMenuList);

        assertThat(roleMenuList.stream().allMatch(m -> m.getDepth().equals("2"))).isTrue();
    }

    @Test
    public void findUpMenuList() {
        List<Menu> upMenuList = menuService.findUpMenuList();
        assertThat(upMenuList).size().isGreaterThan(0);
        assertThat(upMenuList.get(0).getDepth()).isEqualTo("1");
        assertThat(upMenuList.get(0).getUseYn()).isEqualTo(UseYnCode.Y.name());
    }

    @Test
    public void findSubMenuInfo() {
        String menuId = "AA01";
        Menu menuInfo = menuService.findMenu(menuId);
        assertThat(menuInfo.getDepth()).isEqualTo("2");
        assertThat(menuInfo.getMenuId()).isEqualTo(menuId);
    }

    @Test
    public void insertSubMenu() {
        String subMenuId = UPMENU_ID + "00";
        Menu subMenu = TestObjectFactory.getSubMenu()
                .menuId("")
                .upmenuId(UPMENU_ID)
                .build();

        menuService.insertSubMenu(subMenu);
        initEm();

        Menu findMenu = menuService.findMenu(subMenuId);
        assertThat(findMenu.getMenuId()).isEqualTo(subMenuId);
        assertThat(findMenu.getDepth()).isEqualTo("2");
    }

    @Test
    public void updateMenu() {
        Menu subMenu = TestObjectFactory.getSubMenu()
                .menuNm("MenuNm")
                .upmenuId(UPMENU_ID)
                .build();
        menuService.insertSubMenu(subMenu);
        initEm();

        subMenu.setMenuNm("updateMenuNm");
        menuService.updateMenu(subMenu);
        initEm();

        Menu findSubMenu = menuService.findMenu(subMenu.getMenuId());
        assertThat(findSubMenu.getMenuNm()).isEqualTo(subMenu.getMenuNm());
    }

}