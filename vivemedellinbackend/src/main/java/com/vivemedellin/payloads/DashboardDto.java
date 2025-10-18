package com.vivemedellin.payloads;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DashboardDto {

    private GeneralStats generalStats;
    private List<PostStatsDto> topCommentedPosts;
    private List<UserStatsDto> mostActiveUsers;
    private List<CategoryStatsDto> popularCategories;
    private List<RecentActivityDto> recentActivity;

    @Getter
    @Setter
    public static class GeneralStats {
        private Long totalPosts;
        private Long totalComments;
        private Long totalUsers;
        private Long totalSavedPosts;
        private Long activeUsersLast7Days;
        private Long newPostsLast7Days;
    }

    @Getter
    @Setter
    public static class PostStatsDto {
        private Integer postId;
        private String postTitle;
        private String imageName;
        private Long commentCount;
        private Long savedCount;
        private UserResponseDto author;
        private String categoryName;
    }

    @Getter
    @Setter
    public static class UserStatsDto {
        private Integer userId;
        private String name;
        private String email;
        private Long postCount;
        private Long commentCount;
        private Long totalActivity;
    }

    @Getter
    @Setter
    public static class CategoryStatsDto {
        private Integer categoryId;
        private String categoryName;
        private String categoryDescription;
        private Long postCount;
        private Long commentCount;
    }

    @Getter
    @Setter
    public static class RecentActivityDto {
        private String activityType;
        private String description;
        private String timestamp;
        private UserResponseDto user;
        private Integer relatedPostId;
        private String relatedPostTitle;
    }
}
