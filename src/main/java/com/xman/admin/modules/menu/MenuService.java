package com.xman.admin.modules.menu;

import com.xman.admin.constants.SystemStatusCode;
import com.xman.admin.constants.UseYnCode;
import com.xman.admin.exception.BizException;
import com.xman.admin.modules.menu.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MenuService {
    private final MenuRepository menuRepository;

    public List<Menu> findMenuBy(String memberId) {
        List<Menu> rootMenus = menuRepository.findRootMenusBy(memberId);
        List<Menu> subMenus = menuRepository.findSubMenusBy(memberId);

        List<Menu> setMenuList = combineRootAndSubMenu(rootMenus, subMenus);

        return setMenuList;
    }

    private List<Menu> combineRootAndSubMenu(List<Menu> rootMenus, List<Menu> subMenus) {
        return rootMenus.stream().map(menuInfo -> {
            List<Menu> fillteredMenuList = subMenus.stream().filter(submenu -> submenu.getUpmenuId().equalsIgnoreCase(menuInfo.getMenuId()))
                    .map(subMenu -> {
                        subMenu.setUpmenuNm(menuInfo.getMenuNm());
                        return subMenu;
                    })
                    .collect(Collectors.toList());
            menuInfo.setSubMenus(fillteredMenuList);
            return menuInfo;
        }).collect(Collectors.toList());
    }

    // Menu 메뉴 사용 함수들 -------------------------------------------------------------------
    public List<Menu> findMenus(String useYn, Pageable page) {
        return menuRepository.findMenus(useYn, page);
    }

    public List<Menu> findSubMenus() {
        return menuRepository.findSubMenus();
    }

    public List<Menu> findUpMenuList() {
        return menuRepository.findByDepthAndUseYn("1", UseYnCode.Y.name());
    }

    public Menu findMenu(String menuId) {
        return menuRepository.findMenu(menuId);
    }

    public void updateMenu(Menu menuInfo) {
        Assert.hasLength(menuInfo.getMenuNm(), "MenuName is null");
        Assert.hasLength(menuInfo.getUseYn(), "useYN is null");
        Assert.hasLength(menuInfo.getOrdNo(), "ordNo is null");
        Assert.hasLength(menuInfo.getMenuId(), "menuId is null");

        menuRepository.save(menuInfo);
    }

    public void insertSubMenu(Menu menu) {
        Assert.hasLength(menu.getUpmenuId(), "upMenuId is null");

        Menu findUpMenu = menuRepository.findMenu(menu.getUpmenuId());
        if(ObjectUtils.isEmpty(findUpMenu)) throw new BizException("Not Found UpMenu");

        int menuCnt = menuRepository.countByUpmenuId(menu.getUpmenuId()) + 1;
        menu.setMenuId(findUpMenu.getMenuId() + (menuCnt < 10 ? "0" + menuCnt : menuCnt));

        menuRepository.save(menu);
    }

    public String insertUpMenu(Menu menu) {
        Assert.hasLength(menu.getMenuNm(), "menuName is null");
        Assert.hasLength(menu.getUseYn(), "useYn is null");

        int upMenuCnt = menuRepository.countByDepth("1");

        menu.setMenuId(this.generateMenuId(upMenuCnt));
        Menu save = menuRepository.save(menu);
        return save.getMenuId();
    }

    public String generateMenuId(int rowCount) {
        if(rowCount > 675) throw new BizException("Invalid rowCount for generating menuId");

        final int ALPHABET_LENGTH = 26;

        char FIRST_CODE = 'A';
        char SECOND_CODE = 'A';

        char firstCode = (char) (FIRST_CODE + (rowCount / ALPHABET_LENGTH));
        char secondCode = (char) (SECOND_CODE + (rowCount % ALPHABET_LENGTH));

        return String.format("%c%c", firstCode, secondCode);
    }

}
