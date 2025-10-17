package com.vivemedellin.controllers;

import com.vivemedellin.payloads.DashboardDto;
import com.vivemedellin.services.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<DashboardDto> getCompleteDashboard() {
        DashboardDto dashboard = dashboardService.getCompleteDashboard();
        return new ResponseEntity<>(dashboard, HttpStatus.OK);
    }

    @GetMapping("/stats")
    public ResponseEntity<DashboardDto.GeneralStats> getGeneralStats() {
        DashboardDto.GeneralStats stats = dashboardService.getGeneralStats();
        return new ResponseEntity<>(stats, HttpStatus.OK);
    }

    @GetMapping("/top-commented-posts")
    public ResponseEntity<List<DashboardDto.PostStatsDto>> getTopCommentedPosts(
            @RequestParam(defaultValue = "10") int limit) {
        List<DashboardDto.PostStatsDto> posts = dashboardService.getTopCommentedPosts(limit);
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    @GetMapping("/most-saved-posts")
    public ResponseEntity<List<DashboardDto.PostStatsDto>> getMostSavedPosts(
            @RequestParam(defaultValue = "10") int limit) {
        List<DashboardDto.PostStatsDto> posts = dashboardService.getMostSavedPosts(limit);
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    @GetMapping("/most-active-users")
    public ResponseEntity<List<DashboardDto.UserStatsDto>> getMostActiveUsers(
            @RequestParam(defaultValue = "10") int limit) {
        List<DashboardDto.UserStatsDto> users = dashboardService.getMostActiveUsers(limit);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/popular-categories")
    public ResponseEntity<List<DashboardDto.CategoryStatsDto>> getPopularCategories() {
        List<DashboardDto.CategoryStatsDto> categories = dashboardService.getPopularCategories();
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    @GetMapping("/recent-activity")
    public ResponseEntity<List<DashboardDto.RecentActivityDto>> getRecentActivity(
            @RequestParam(defaultValue = "20") int limit) {
        List<DashboardDto.RecentActivityDto> activities = dashboardService.getRecentActivity(limit);
        return new ResponseEntity<>(activities, HttpStatus.OK);
    }
}
