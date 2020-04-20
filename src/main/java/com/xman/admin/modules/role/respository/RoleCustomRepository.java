package com.xman.admin.modules.role.respository;

import com.xman.admin.modules.role.Role;

import java.util.List;

public interface RoleCustomRepository {
    List<Role> findRoles(String useYn);
}
