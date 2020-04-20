package com.xman.admin.modules.menu.repository;

import com.xman.admin.modules.menu.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<Menu, String> , MenuCustomRepository{
    List<Menu> findByUseYn(String useYn);

    Integer countByUpmenuId(String upmenuId);

    List<Menu> findByDepthAndUseYn(String depth, String useYn);

    Integer countByDepth(String s);
}
