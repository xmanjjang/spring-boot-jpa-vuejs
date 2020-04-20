package com.xman.admin.exception;

import com.xman.admin.constants.SystemStatusCode;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResultDto {
   private SystemStatusCode code;
   private String message;
   private String Data;
}
