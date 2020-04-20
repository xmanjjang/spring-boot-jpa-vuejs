package com.xman.admin.modules.role.respository;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.xman.admin.modules.role.QRole;
import com.xman.admin.modules.role.Role;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@RequiredArgsConstructor
public class RoleRepositoryImpl implements RoleCustomRepository {
    private final JPAQueryFactory query;

    @Override
    public List<Role> findRoles(String useYn) {
        QRole r = QRole.role;

        JPAQuery<Role> from = query.select(r).from(r);
        if(StringUtils.isNotEmpty(useYn)) from.where(r.useYn.eq(useYn));

        return from.fetch();
    }
}
