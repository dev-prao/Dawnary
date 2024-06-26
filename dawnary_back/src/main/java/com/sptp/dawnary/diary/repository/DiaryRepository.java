package com.sptp.dawnary.diary.repository;

import com.sptp.dawnary.diary.domain.Diary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DiaryRepository extends JpaRepository<Diary, Long> {

    List<Diary> findByMemberId(Long memberId);

    @Query("SELECT d FROM Diary d WHERE d.member.id = :memberId AND d.status = 1")
    List<Diary> findOtherUserDiaries(@Param("memberId") Long memberId);

    @Query("SELECT d FROM Diary d, Follow f WHERE d.member.id = f.following.id AND f.follower.id = :memberId AND d.status = 1")
    List<Diary> findFollowUserDiaries(Long memberId);
}
