package com.xman.admin.modules.menu;


import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "TB_MENU_MST")
@NoArgsConstructor
@Getter @Setter
@EqualsAndHashCode(of = "menuId")
@AllArgsConstructor
@ToString
@Builder
@DynamicUpdate
public class Menu {
  @Id
  @Column(name = "MENU_ID", length = 8, nullable = false)
  private String menuId;

  @Column(name = "DEPTH", length = 1, nullable = false)
  private String depth;

  @Column(name = "USE_YN", length = 1, nullable = false)
  private String useYn;

  @Column(name = "REGDATE")
  private LocalDateTime regdate;

  @Column(name = "ORD_NO", length = 2, nullable = false)
  private String ordNo;

  @Column(name = "UPMENU_ID", length = 8)
  private String upmenuId;

  @Transient
  private String upmenuNm;

  @Transient
  private List<Menu> subMenus;

  @Column(name = "REGPER", length = 10)
  private String regper;

  @Column(name = "MENU_NM", length = 100)
  private String menuNm;

  @Column(name = "MENU_URL", length = 500)
  private String menuUrl;

}
