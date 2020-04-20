package com.xman.admin.modules.role;


import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.repository.Modifying;

import javax.persistence.*;

@Entity
@Table(name = "TB_ROLE_MST")
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "roleCode")
@ToString
@Builder
@AllArgsConstructor
@DynamicUpdate
public class Role {
  @Id
  @Column(name = "ROLE_CODE", nullable = false, length = 10)
  private String roleCode;

  @Column(name = "MANAGER_YN", nullable = false, length = 2)
  private String managerYn;

  @Column(name = "USE_YN", nullable = false, length = 1)
  private String useYn;

  @Column(name = "CODE_NM", length = 30)
  private String codeNm;
}
