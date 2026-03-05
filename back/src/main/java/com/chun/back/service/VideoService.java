package com.chun.back.service;

import com.chun.back.pojo.Video;

import java.util.List;
import java.util.Map;

public interface VideoService {

    List<Video> list(int page, int pageSize, String sortBy, String order, String keyword, Long viewerId);

    List<Video> latest(int count, Long viewerId);

    Video getById(Long id, Long viewerId);

    Long create(Video video);

    /** 我点赞的视频 */
    List<Video> myLiked(Long userId);

    /** 我收藏的视频 */
    List<Video> myFavorited(Long userId);

    Map<String, Object> toggleLike(Long videoId, Long userId);

    Map<String, Object> toggleFavorite(Long videoId, Long userId);
}
