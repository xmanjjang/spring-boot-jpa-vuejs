package com.xman.admin.modules.menu.repository;
import com.xman.admin.modules.menu.Menu;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MenuCustomRepository {
    List<Menu> findRootMenusBy(String memberId);
    List<Menu> findSubMenusBy(String memberId);

    List<Menu> findSubMenus();
    List<Menu> findMenus(String useYn, Pageable page);

    Menu findMenu(String menuId);
}
