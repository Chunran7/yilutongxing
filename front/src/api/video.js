import request from "@/utils/request.js";

// 视频列表
export const getVideoListService = ({ page = 1, pageSize = 10, sortBy = "create_time", order = "DESC", keyword = "" } = {}) => {
  return request.get("/video/list", { params: { page, pageSize, sortBy, order, keyword } });
};

// 视频详情
export const getVideoByIdService = (id) => request.get(`/video/${id}`);

// 最新视频
export const getVideoLatestService = (count = 8) => request.get("/video/latest", { params: { count } });

// 点赞 / 取消点赞（需要登录）
export const toggleVideoLikeService = (id) => request.post(`/video/${id}/like/toggle`);

// 收藏 / 取消收藏（需要登录）
export const toggleVideoFavoriteService = (id) => request.post(`/video/${id}/favorite/toggle`);

// 我点赞的视频
export const getMyLikedVideosService = () => request.get('/video/me/liked')

// 我收藏的视频
export const getMyFavoritedVideosService = () => request.get('/video/me/favorited')
