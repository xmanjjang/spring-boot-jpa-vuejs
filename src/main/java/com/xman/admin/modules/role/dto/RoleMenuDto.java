package com.xman.admin.modules.role.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

public class RoleMenuDto {

    @Getter
    @Setter
    public static class Request {
        private String menuId;
        private String roleCd;
    }

    @Getter
    @Setter
    public static class Response {
        private String menuId;
        private String roleCd;
    }

    @Getter
    @Setter
    public static class Insert {
        private String roleCd;
        private List<String> menuIds;
    }
}
