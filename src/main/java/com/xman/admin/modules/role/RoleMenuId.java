package com.xman.admin.modules.role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class RoleMenuId implements Serializable {
    @Column(name = "MENU_ID", nullable = false, length = 8)
    private String menuId;

    @Column(name = "ROLE_CD", nullable = false, length = 10)
    private String roleCd;

}
