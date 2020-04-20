package com.xman.admin.modules.role.respository;

import com.xman.admin.modules.role.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, String>, RoleCustomRepository {
    int countByRoleCode(String roleCode);
}
