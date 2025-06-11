package com.cts.onlineexamportall.dao;

import java.util.List;

import com.cts.onlineexamportall.model.Leaderboard;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeaderboardDAO extends JpaRepository<Leaderboard, Long> {
    List<Leaderboard> findByExamIdOrderByMarksDesc(Long examId);

    @Transactional
    void deleteByExamId(Long examId);

    boolean existsByExamId(Long examId);
}
