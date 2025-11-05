package com.vivemedellin.services.impl;

import com.vivemedellin.models.*;
import com.vivemedellin.payloads.DashboardDto;
import com.vivemedellin.payloads.UserResponseDto;
import com.vivemedellin.repositories.*;
import com.vivemedellin.services.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private PostRepo postRepo;

    @Autowired
    private CommentRepo commentRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private SavedPostRepo savedPostRepo;

    @Autowired
    private CategoryRepo categoryRepo;

    @Override
    public DashboardDto getCompleteDashboard() {
        DashboardDto dashboard = new DashboardDto();
        
        dashboard.setGeneralStats(getGeneralStats());
        dashboard.setTopCommentedPosts(getTopCommentedPosts(10));
        dashboard.setMostActiveUsers(getMostActiveUsers(10));
        dashboard.setPopularCategories(getPopularCategories());
        dashboard.setRecentActivity(getRecentActivity(20));
        
        return dashboard;
    }

    @Override
    public DashboardDto.GeneralStats getGeneralStats() {
        DashboardDto.GeneralStats stats = new DashboardDto.GeneralStats();
        
        stats.setTotalPosts(postRepo.count());
        stats.setTotalComments(commentRepo.count());
        stats.setTotalUsers(userRepo.count());
        stats.setTotalSavedPosts(savedPostRepo.count());
        
        Date sevenDaysAgo = Date.from(
            LocalDateTime.now().minusDays(7)
                .atZone(ZoneId.systemDefault()).toInstant()
        );
        
        List<Post> allPosts = postRepo.findAll();
        long newPostsLast7Days = allPosts.stream()
            .filter(post -> post.getCreationDate() != null && 
                          post.getCreationDate().after(sevenDaysAgo))
            .count();
        stats.setNewPostsLast7Days(newPostsLast7Days);
        
        List<Comment> allComments = commentRepo.findAll();
        Set<Integer> activeUsersIds = allComments.stream()
            .filter(comment -> comment.getCreatedDate() != null && 
                             comment.getCreatedDate().after(sevenDaysAgo))
            .map(comment -> comment.getUser().getId())
            .collect(Collectors.toSet());
        
        Set<Integer> activePostersIds = allPosts.stream()
            .filter(post -> post.getCreationDate() != null && 
                          post.getCreationDate().after(sevenDaysAgo))
            .map(post -> post.getUser().getId())
            .collect(Collectors.toSet());
        
        activeUsersIds.addAll(activePostersIds);
        stats.setActiveUsersLast7Days((long) activeUsersIds.size());
        
        return stats;
    }

    @Override
    public List<DashboardDto.PostStatsDto> getTopCommentedPosts(int limit) {
        List<Post> allPosts = postRepo.findAll();
        
        return allPosts.stream()
            .map(post -> {
                DashboardDto.PostStatsDto dto = new DashboardDto.PostStatsDto();
                dto.setPostId(post.getPostId());
                dto.setPostTitle(post.getPostTitle());
                dto.setImageName(post.getImageName());
                dto.setCommentCount((long) post.getComments().size());
                
                List<SavedPost> savedPosts = savedPostRepo.findByPost(post);
                dto.setSavedCount((long) savedPosts.size());
                
                UserResponseDto authorDto = new UserResponseDto();
                authorDto.setId(post.getUser().getId());
                authorDto.setName(post.getUser().getName());
                dto.setAuthor(authorDto);
                
                if (post.getCategory() != null) {
                    dto.setCategoryName(post.getCategory().getCategoryTitle());
                }
                
                return dto;
            })
            .sorted((a, b) -> Long.compare(b.getCommentCount(), a.getCommentCount()))
            .limit(limit)
            .collect(Collectors.toList());
    }

    @Override
    public List<DashboardDto.PostStatsDto> getMostSavedPosts(int limit) {
        List<Post> allPosts = postRepo.findAll();
        
        return allPosts.stream()
            .map(post -> {
                DashboardDto.PostStatsDto dto = new DashboardDto.PostStatsDto();
                dto.setPostId(post.getPostId());
                dto.setPostTitle(post.getPostTitle());
                dto.setImageName(post.getImageName());
                dto.setCommentCount((long) post.getComments().size());
                
                List<SavedPost> savedPosts = savedPostRepo.findByPost(post);
                dto.setSavedCount((long) savedPosts.size());
                
                UserResponseDto authorDto = new UserResponseDto();
                authorDto.setId(post.getUser().getId());
                authorDto.setName(post.getUser().getName());
                dto.setAuthor(authorDto);
                
                if (post.getCategory() != null) {
                    dto.setCategoryName(post.getCategory().getCategoryTitle());
                }
                
                return dto;
            })
            .filter(dto -> dto.getSavedCount() > 0)
            .sorted((a, b) -> Long.compare(b.getSavedCount(), a.getSavedCount()))
            .limit(limit)
            .collect(Collectors.toList());
    }

    @Override
    public List<DashboardDto.UserStatsDto> getMostActiveUsers(int limit) {
        List<User> allUsers = userRepo.findAll();
        
        return allUsers.stream()
            .map(user -> {
                DashboardDto.UserStatsDto dto = new DashboardDto.UserStatsDto();
                dto.setUserId(user.getId());
                dto.setName(user.getName());
                dto.setEmail(user.getEmail());
                
                long postCount = user.getPosts() != null ? user.getPosts().size() : 0;
                long commentCount = user.getComments() != null ? user.getComments().size() : 0;
                
                dto.setPostCount(postCount);
                dto.setCommentCount(commentCount);
                dto.setTotalActivity(postCount + commentCount);
                
                return dto;
            })
            .filter(dto -> dto.getTotalActivity() > 0)
            .sorted((a, b) -> Long.compare(b.getTotalActivity(), a.getTotalActivity()))
            .limit(limit)
            .collect(Collectors.toList());
    }

    @Override
    public List<DashboardDto.CategoryStatsDto> getPopularCategories() {
        List<Category> allCategories = categoryRepo.findAll();
        List<Post> allPosts = postRepo.findAll();
        
        return allCategories.stream()
            .map(category -> {
                DashboardDto.CategoryStatsDto dto = new DashboardDto.CategoryStatsDto();
                dto.setCategoryId(category.getCategoryId());
                dto.setCategoryName(category.getCategoryTitle());
                dto.setCategoryDescription(category.getCategoryDescription());
                
                List<Post> categoryPosts = allPosts.stream()
                    .filter(post -> post.getCategory() != null && 
                                  post.getCategory().getCategoryId().equals(category.getCategoryId()))
                    .collect(Collectors.toList());
                
                dto.setPostCount((long) categoryPosts.size());
                
                long totalComments = categoryPosts.stream()
                    .mapToLong(post -> post.getComments() != null ? post.getComments().size() : 0)
                    .sum();
                dto.setCommentCount(totalComments);
                
                return dto;
            })
            .sorted((a, b) -> Long.compare(b.getPostCount(), a.getPostCount()))
            .collect(Collectors.toList());
    }

    @Override
    public List<DashboardDto.RecentActivityDto> getRecentActivity(int limit) {
        List<DashboardDto.RecentActivityDto> activities = new ArrayList<>();
        
        List<Post> recentPosts = postRepo.findAll().stream()
            .filter(post -> post.getCreationDate() != null)
            .sorted((a, b) -> b.getCreationDate().compareTo(a.getCreationDate()))
            .limit(limit / 2)
            .collect(Collectors.toList());
        
        for (Post post : recentPosts) {
            DashboardDto.RecentActivityDto activity = new DashboardDto.RecentActivityDto();
            activity.setActivityType("NEW_POST");
            activity.setDescription("publicó un nuevo evento");
            activity.setTimestamp(formatDate(post.getCreationDate()));
            
            UserResponseDto userDto = new UserResponseDto();
            userDto.setId(post.getUser().getId());
            userDto.setName(post.getUser().getName());
            activity.setUser(userDto);
            
            activity.setRelatedPostId(post.getPostId());
            activity.setRelatedPostTitle(post.getPostTitle());
            
            activities.add(activity);
        }
        
        List<Comment> recentComments = commentRepo.findAll().stream()
            .filter(comment -> comment.getCreatedDate() != null)
            .sorted((a, b) -> b.getCreatedDate().compareTo(a.getCreatedDate()))
            .limit(limit / 2)
            .collect(Collectors.toList());
        
        for (Comment comment : recentComments) {
            DashboardDto.RecentActivityDto activity = new DashboardDto.RecentActivityDto();
            
            if (comment.getParentComment() != null) {
                activity.setActivityType("REPLY");
                activity.setDescription("respondió a un comentario");
            } else {
                activity.setActivityType("COMMENT");
                activity.setDescription("comentó en un evento");
            }
            
            activity.setTimestamp(formatDate(comment.getCreatedDate()));
            
            UserResponseDto userDto = new UserResponseDto();
            userDto.setId(comment.getUser().getId());
            userDto.setName(comment.getUser().getName());
            activity.setUser(userDto);
            
            activity.setRelatedPostId(comment.getPost().getPostId());
            activity.setRelatedPostTitle(comment.getPost().getPostTitle());
            
            activities.add(activity);
        }
        
        return activities.stream()
            .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()))
            .limit(limit)
            .collect(Collectors.toList());
    }
    
    private String formatDate(Date date) {
        LocalDateTime localDateTime = date.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        return localDateTime.format(formatter);
    }
}
