package com.xman.admin.modules.member.repository;

import com.xman.admin.modules.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<Member, String>, MemberCustomRepository {
    @Query("select distinct m.company from Member m where m.company is not null")
    List<String> findCompanies();

    @Modifying
    @Query("delete from Member m where m.mbrId in :mbrIdList")
    void deleteListById(@Param("mbrIdList") List<String> mbrIdList);

    List<Member> findByMbrId(String mbrId);
}
