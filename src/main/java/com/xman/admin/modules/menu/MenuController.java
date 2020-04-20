package com.xman.admin.modules.menu;

import com.xman.admin.modules.login.CurrentAccount;
import com.xman.admin.modules.member.Member;
import com.xman.admin.modules.menu.dto.MenuDto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
@RequestMapping("/menus")
@CrossOrigin
public class MenuController {
    private final MenuService menuService;
    private final ModelMapper modelMapper;

    @GetMapping()
    public ResponseEntity<List<MenuDto.Response>> findMenus(@RequestParam(required = false) String useYn, Pageable page) {
        List<Menu> menus = menuService.findMenus(useYn, page);

        return ResponseEntity.ok(convertMenuToDto(menus));
    }

    private List<MenuDto.Response> convertMenuToDto(List<Menu> menus) {
        return menus.stream().map(menu -> modelMapper.map(menu, MenuDto.Response.class)).collect(Collectors.toList());
    }

    @GetMapping("/{menuId}")
    public ResponseEntity<MenuDto.Response> findSubMenu(@PathVariable String menuId) {
        Menu menu = menuService.findMenu(menuId);
        return ResponseEntity.ok(modelMapper.map(menu, MenuDto.Response.class));
    }

    @GetMapping("/leftMenus")
    public ResponseEntity<List<MenuDto.Response>> selectLeftMenus(@CurrentAccount Member member) {
        List<Menu> memberMenu = menuService.findMenuBy(member.getMbrId());
        return ResponseEntity.ok(convertMenuToDto(memberMenu));
    }

    @GetMapping("/upMenus")
    public ResponseEntity<List<MenuDto.Response>> findUpMenus() {
        List<Menu> upMenuList = menuService.findUpMenuList();
        return ResponseEntity.ok(convertMenuToDto(upMenuList));
    }

    @PostMapping("/upMenus")
    public ResponseEntity insertUpMenu(@RequestBody(required = false) MenuDto.Request menu) {
        menuService.insertUpMenu(modelMapper.map(menu, Menu.class));
        return ResponseEntity.status(CREATED).build();
    }

    @PutMapping("/upMenus")
    public ResponseEntity updateUpMenu(@RequestBody MenuDto.Request menu) {
        menuService.updateMenu(modelMapper.map(menu, Menu.class));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/subMenus")
    public ResponseEntity<List<MenuDto.Response>> findSubMenus() {
        List<Menu> menus = menuService.findSubMenus();
        return ResponseEntity.ok(convertMenuToDto(menus));
    }

    @PostMapping("/subMenus")
    public ResponseEntity insertSubMenu(@RequestBody MenuDto.Request menu) {
        menuService.insertSubMenu(modelMapper.map(menu, Menu.class));
        return ResponseEntity.status(CREATED).build();
    }

    @PutMapping("/subMenus")
    public ResponseEntity updateSubMenu(@RequestBody MenuDto.Request menu) {
        menuService.updateMenu(modelMapper.map(menu, Menu.class));
        return ResponseEntity.ok().build();
    }
}
