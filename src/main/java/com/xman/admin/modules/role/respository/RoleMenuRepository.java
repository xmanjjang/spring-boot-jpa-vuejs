package com.xman.admin.modules.role.respository;

import com.xman.admin.modules.role.RoleMenu;
import com.xman.admin.modules.role.RoleMenuId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleMenuRepository extends JpaRepository<RoleMenu, RoleMenuId> {
    @Query("select r from RoleMenu r where r.id.roleCd = :roleCode")
    List<RoleMenu> findByRoleCode(@Param("roleCode") String roleCode);

    @Modifying
    @Query("delete from RoleMenu r where r.id.roleCd = :roleCode")
    void deleteByRoleCode(@Param("roleCode") String roleCode);
}
