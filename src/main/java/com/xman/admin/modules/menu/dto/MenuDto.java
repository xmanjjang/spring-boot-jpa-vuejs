package com.xman.admin.modules.menu.dto;

import com.xman.admin.modules.menu.Menu;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

public class MenuDto {
        @Setter
        @Getter
        public static class Response{
                private String menuId;
                private String depth;
                private String useYn;
                private LocalDateTime regdate;
                private String ordNo;
                private String upmenuId;
                private String upmenuNm;
                private List<Menu> subMenus;
                private String regper;
                private String menuNm;
                private String menuUrl;
        }

        @Setter
        @Getter
        public static class Request{
                private String menuId;
                private String depth;
                private String useYn;
                private LocalDateTime regdate;
                private String ordNo;
                private String upmenuId;
                private String upmenuNm;
                private List<Menu> subMenus;
                private String regper;
                private String menuNm;
                private String menuUrl;
        }

}
