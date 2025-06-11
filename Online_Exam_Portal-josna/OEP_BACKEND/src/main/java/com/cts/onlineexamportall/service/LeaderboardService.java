package com.cts.onlineexamportall.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cts.onlineexamportall.dao.LeaderboardDAO;
import com.cts.onlineexamportall.model.Leaderboard;
import com.cts.onlineexamportall.model.Report;

@Service
public class LeaderboardService {
    @Autowired
    private LeaderboardDAO leaderboardDAO;

    public List<Leaderboard> getLeaderboardByExamId(Long examId) {
        return leaderboardDAO.findByExamIdOrderByMarksDesc(examId);
    }

    public void updateLeaderboard(Long examId) {
        List<Leaderboard> leaderboard = getLeaderboardByExamId(examId);
        for (int i = 0; i < leaderboard.size(); i++) {
            leaderboard.get(i).setPosition(i + 1);
            leaderboardDAO.save(leaderboard.get(i));
        }
    }

    public void createLeaderboardEntries(Long examId, List<Report> reports){
        if (leaderboardDAO.existsByExamId(examId)) {
            // Delete existing leaderboard entries for the exam
            leaderboardDAO.deleteByExamId(examId);
        }

        for(Report report : reports){
            Leaderboard leaderboardEntry = new Leaderboard(examId, report.getUserName(), report.getScore());
            leaderboardDAO.save(leaderboardEntry);
        }
        updateLeaderboard(examId);
    }
}
