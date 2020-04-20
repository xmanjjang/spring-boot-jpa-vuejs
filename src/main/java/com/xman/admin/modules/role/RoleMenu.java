package com.xman.admin.modules.role;

import lombok.*;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_ROLE_MENU")
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@ToString
@Builder
@AllArgsConstructor
public class RoleMenu implements Persistable<RoleMenuId> {

  @EmbeddedId
  private RoleMenuId id;

  @Column(name = "INIT_DT", nullable = false)
  private LocalDateTime initDt;

  @Column(name = "INIT_MBR", nullable = false, length = 10)
  private String initMbr;

  @Override
  public boolean isNew() {
    return true;
  }
}
