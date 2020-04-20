package com.xman.admin.modules.menu.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.xman.admin.constants.UseYnCode;
import com.xman.admin.modules.member.QMember;
import com.xman.admin.modules.menu.Menu;
import com.xman.admin.modules.menu.QMenu;
import com.xman.admin.modules.role.QRole;
import com.xman.admin.modules.role.QRoleMenu;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class MenuRepositoryImpl  implements MenuCustomRepository {

    @Autowired
    private JPAQueryFactory query;

    @Override
    public List<Menu> findRootMenusBy(String memberId) {
        QMenu m = QMenu.menu;
        QRoleMenu rm = QRoleMenu.roleMenu;
        QMember mb = QMember.member;

        List<Menu> rootMenus = query.select(m).from(m).where(m.menuId.in(
                JPAExpressions
                        .select(m.upmenuId)
                        .from(rm).join(m).on(rm.id.menuId.eq(m.menuId))
                        .where(
                                m.depth.eq("2")
                                .and(m.useYn.eq(UseYnCode.Y.name()))
                                .and(rm.id.roleCd.eq(
                                        JPAExpressions.select(mb.roleCd).from(mb).where(mb.mbrId.eq(memberId))
                                ))
                        )
                        .groupBy(m.upmenuId)
                        .having(m.upmenuId.count().gt(0))
                        .distinct()
        ))
                .orderBy(m.menuId.asc())
                .fetch();

        return rootMenus;
    }

    @Override
    public List<Menu> findSubMenusBy(String memberId) {
        QRole r = QRole.role;
        QRoleMenu rm = QRoleMenu.roleMenu;
        QMenu m = QMenu.menu;
        QMember mb = QMember.member;

        return query
                .select(m)
                .from(r)
                .join(rm).on(r.roleCode.eq(rm.id.roleCd))
                .join(m).on(rm.id.menuId.eq(m.menuId))
                .where(
                        r.roleCode.eq(
                                JPAExpressions.select(mb.roleCd)
                                        .from(mb)
                                        .where(mb.mbrId.eq(memberId)
                                                .and(mb.useYn.eq(UseYnCode.Y.name())))
                        )
                                .and(m.useYn.eq(UseYnCode.Y.name()))
                )
                .orderBy(m.ordNo.asc())
                .fetch()
                ;
    }

    @Override
    public List<Menu> findSubMenus() {
        QMenu m = QMenu.menu;
        QMenu mm = new QMenu("mm");
        return query.select(Projections.fields(Menu.class, m.menuId, m.upmenuId, m.useYn, m.ordNo, m.depth
                       ,m.menuNm, mm.menuNm.as("upmenuNm")
                        , m.menuUrl, m.regdate, m.regper
                        ))
                .from(m)
                .join(mm).on(m.upmenuId.eq(mm.menuId).and(mm.useYn.eq(UseYnCode.Y.name())))
                .where(m.useYn.eq(UseYnCode.Y.name()))
                .orderBy(m.upmenuId.asc(), m.ordNo.asc())
                .fetch()
                ;

    }

    @Override
    public List<Menu> findMenus(String useYn, Pageable page) {
        QMenu m = QMenu.menu;
        QMenu mm = new QMenu("mm");

        JPAQuery<Menu> on = query.select(Projections.fields(Menu.class, m.menuId, m.upmenuId, m.useYn, m.ordNo, m.depth,
                new CaseBuilder().when(m.upmenuId.isNotEmpty())
                        .then(m.menuNm)
                        .otherwise("")
                        .as("menuNm")
                , new CaseBuilder().when(mm.menuNm.isNotEmpty())
                        .then(mm.menuNm)
                        .otherwise(m.menuNm)
                        .as("upmenuNm")
                , m.menuUrl, m.regdate, m.regper))
                .from(m)
                .leftJoin(mm).on(m.upmenuId.eq(mm.menuId));

        if (StringUtils.isNotEmpty(useYn)) on.where(m.useYn.eq(useYn));

        return on.orderBy(m.menuId.asc(), m.ordNo.asc())
                .offset(page.getOffset())
                .limit(page.getPageSize())
                .fetch();
    }

    @Override
    public Menu findMenu(String menuId) {
        QMenu m = QMenu.menu;
        QMenu mm = new QMenu("mm");

        return query.select(Projections.fields(Menu.class, m.menuId, m.upmenuId, m.useYn, m.ordNo, m.depth
                ,m.menuNm, mm.menuNm.as("upmenuNm")
                , m.menuUrl, m.regdate, m.regper
        ))
                .from(m)
                .leftJoin(mm).on(m.upmenuId.eq(mm.menuId))
                .where(m.menuId.eq(menuId))
                .fetchOne()
                ;
    }
}
