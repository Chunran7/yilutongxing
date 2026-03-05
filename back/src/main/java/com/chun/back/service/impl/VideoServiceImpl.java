package com.chun.back.service.impl;

import com.chun.back.mapper.VideoFavoriteMapper;
import com.chun.back.mapper.VideoLikeMapper;
import com.chun.back.mapper.VideoMapper;
import com.chun.back.pojo.Video;
import com.chun.back.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VideoServiceImpl implements VideoService {

    @Autowired
    private VideoMapper videoMapper;

    @Autowired
    private VideoLikeMapper videoLikeMapper;

    @Autowired
    private VideoFavoriteMapper videoFavoriteMapper;

    private String safeSortBy(String sortBy) {
        if (sortBy == null) return "create_time";
        return switch (sortBy) {
            case "create_time", "like_count", "views" -> sortBy;
            default -> "create_time";
        };
    }

    private String safeOrder(String order) {
        if (order == null) return "DESC";
        String up = order.toUpperCase();
        return ("ASC".equals(up) || "DESC".equals(up)) ? up : "DESC";
    }

    @Override
    public List<Video> list(int page, int pageSize, String sortBy, String order, String keyword, Long viewerId) {
        int p = Math.max(page, 1);
        int ps = Math.min(Math.max(pageSize, 1), 50);
        int offset = (p - 1) * ps;

        String sb = safeSortBy(sortBy);
        String od = safeOrder(order);

        List<Video> list = videoMapper.list(ps, offset, sb, od, keyword);

        if (viewerId != null) {
            for (Video v : list) {
                v.setLiked(videoLikeMapper.exists(v.getId(), viewerId) > 0);
                v.setFavorited(videoFavoriteMapper.exists(v.getId(), viewerId) > 0);
            }
        } else {
            for (Video v : list) {
                v.setLiked(false);
                v.setFavorited(false);
            }
        }
        return list;
    }

    @Override
    public List<Video> latest(int count, Long viewerId) {
        int c = Math.min(Math.max(count, 1), 30);
        List<Video> list = videoMapper.latest(c);

        if (viewerId != null) {
            for (Video v : list) {
                v.setLiked(videoLikeMapper.exists(v.getId(), viewerId) > 0);
                v.setFavorited(videoFavoriteMapper.exists(v.getId(), viewerId) > 0);
            }
        } else {
            for (Video v : list) {
                v.setLiked(false);
                v.setFavorited(false);
            }
        }
        return list;
    }

    @Override
    public Video getById(Long id, Long viewerId) {
        videoMapper.incViews(id);
        Video v = videoMapper.selectByIdWithAuthor(id);
        if (v == null) return null;

        if (viewerId != null) {
            v.setLiked(videoLikeMapper.exists(id, viewerId) > 0);
            v.setFavorited(videoFavoriteMapper.exists(id, viewerId) > 0);
        } else {
            v.setLiked(false);
            v.setFavorited(false);
        }
        return v;
    }

    @Override
    public Long create(Video video) {
        videoMapper.insert(video);
        return video.getId();
    }

    @Override
    public List<Video> myLiked(Long userId) {
        List<Video> list = videoMapper.listLikedByUser(userId);
        for (Video v : list) {
            v.setLiked(true);
            v.setFavorited(videoFavoriteMapper.exists(v.getId(), userId) > 0);
        }
        return list;
    }

    @Override
    public List<Video> myFavorited(Long userId) {
        List<Video> list = videoMapper.listFavoritedByUser(userId);
        for (Video v : list) {
            v.setFavorited(true);
            v.setLiked(videoLikeMapper.exists(v.getId(), userId) > 0);
        }
        return list;
    }

    @Override
    public Map<String, Object> toggleLike(Long videoId, Long userId) {
        boolean existed = videoLikeMapper.exists(videoId, userId) > 0;
        if (existed) {
            videoLikeMapper.delete(videoId, userId);
        } else {
            videoLikeMapper.insert(videoId, userId);
        }
        int likeCount = videoLikeMapper.countByVideoId(videoId);
        videoMapper.updateLikeCount(videoId, likeCount);

        Map<String, Object> res = new HashMap<>();
        res.put("liked", !existed);
        res.put("likeCount", likeCount);
        return res;
    }

    @Override
    public Map<String, Object> toggleFavorite(Long videoId, Long userId) {
        boolean existed = videoFavoriteMapper.exists(videoId, userId) > 0;
        if (existed) {
            videoFavoriteMapper.delete(videoId, userId);
        } else {
            videoFavoriteMapper.insert(videoId, userId);
        }
        Map<String, Object> res = new HashMap<>();
        res.put("favorited", !existed);
        return res;
    }
}
