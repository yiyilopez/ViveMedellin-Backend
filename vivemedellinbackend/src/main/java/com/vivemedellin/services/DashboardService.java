package com.vivemedellin.services;

import com.vivemedellin.payloads.DashboardDto;

import java.util.List;

public interface DashboardService {
    
    DashboardDto getCompleteDashboard();
    
    DashboardDto.GeneralStats getGeneralStats();
    
    List<DashboardDto.PostStatsDto> getTopCommentedPosts(int limit);
    
    List<DashboardDto.PostStatsDto> getMostSavedPosts(int limit);
    
    List<DashboardDto.UserStatsDto> getMostActiveUsers(int limit);
    
    List<DashboardDto.CategoryStatsDto> getPopularCategories();
    
    List<DashboardDto.RecentActivityDto> getRecentActivity(int limit);
}
