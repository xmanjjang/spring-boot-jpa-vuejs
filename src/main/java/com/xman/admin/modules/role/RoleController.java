package com.xman.admin.modules.role;

import com.xman.admin.modules.role.dto.RoleDto;
import com.xman.admin.modules.role.dto.RoleMenuDto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;
    private final ModelMapper modelMapper;

    @GetMapping
    public ResponseEntity<List<RoleDto.Model>> findRoles(@RequestBody(required = false) RoleDto.Request params) {
        List<Role> roles = roleService.findRoles(params.getUseYn());
        return ResponseEntity.ok(convertRolesToDto(roles));
    }

    @PostMapping
    public ResponseEntity insertRole(@RequestBody RoleDto.Model role) {
        roleService.saveRole(modelMapper.map(role, Role.class));
        return ResponseEntity.status(CREATED).build();
    }

    @PutMapping
    public ResponseEntity updateRoles(@RequestBody RoleDto.Model role) {
        roleService.updateRole(modelMapper.map(role, Role.class));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{roleCode}")
    public ResponseEntity findRoleCode(@PathVariable String roleCode) {
        Role role = roleService.findRole(roleCode);
        return ResponseEntity.ok(role);
    }

    @GetMapping("/roleMenus")
    public ResponseEntity findRoleMenu(@RequestBody RoleMenuDto.Request params) {
        List<RoleMenu> roleMenus = roleService.findRoleMenus(params.getRoleCd());
        return ResponseEntity.ok(convertRoleMenusToDto(roleMenus));
    }

    @PostMapping("roleMenus")
    public ResponseEntity insertRoleMenus(@RequestBody RoleMenuDto.Insert params) {
        roleService.saveRoleMenus(params.getRoleCd(), params.getMenuIds());
        return ResponseEntity.status(CREATED).build();
    }

    private List<RoleMenuDto.Response> convertRoleMenusToDto(List<RoleMenu> list) {
        modelMapper.addMappings(new ToRoleMenuDto());
        return list.stream().map(role -> modelMapper.map(role, RoleMenuDto.Response.class)).collect(Collectors.toList());
    }

    private List<RoleDto.Model> convertRolesToDto(List<Role> roles) {
        return roles.stream().map(role -> modelMapper.map(role, RoleDto.Model.class)).collect(Collectors.toList());
    }

    class ToRoleMenuDto extends PropertyMap<RoleMenu, RoleMenuDto.Response> {
        @Override
        protected void configure() {
            map(source.getId().getMenuId(), destination.getMenuId());
            map(source.getId().getRoleCd(), destination.getRoleCd());
        }
    }
}

