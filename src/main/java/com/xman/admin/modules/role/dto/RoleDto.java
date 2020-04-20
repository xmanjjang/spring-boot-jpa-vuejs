package com.xman.admin.modules.role.dto;

import lombok.Getter;
import lombok.Setter;


public class RoleDto {
    @Getter
    @Setter
    public static class Request {
        private String useYn;
    }

    @Getter
    @Setter
    public static class Model {
        private String roleCode;
        private String managerYn;
        private String useYn;
        private String codeNm;
    }
}
