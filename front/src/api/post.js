import request from "@/utils/request.js";

// 获取帖子列表（可匿名，登录后会带 liked/favorited 字段）
export const getAllpostsService = ({ page = 1, pageSize = 10, sortBy = "create_time", order = "DESC", keyword = "" } = {}) => {
  return request.get("/post/list", {
    params: { page, pageSize, sortBy, order, keyword },
  });
};

// 帖子详情
export const getPostByIdService = (id) => request.get(`/post/${id}`);

// 发布帖子（需要登录）
export const insertPostService = (post) => request.post("/post", post);

// 点赞 / 取消点赞（需要登录）
export const togglePostLikeService = (id) => request.post(`/post/${id}/like/toggle`);

// 收藏 / 取消收藏（需要登录）
export const togglePostFavoriteService = (id) => request.post(`/post/${id}/favorite/toggle`);

// 评论树（可匿名）
export const getPostCommentsService = (id) => request.get(`/post/${id}/comments`);

// 发表评论/回复（需要登录）
export const createCommentService = (payload) => request.post(`/comment`, payload);

// 删除评论（需要登录）
export const deleteCommentService = (commentId) => request.delete(`/comment/${commentId}`);

// 我发布的帖子（管理页）
export const getMyPostsService = () => request.get('/post/me/list')

// 编辑我的帖子
export const updateMyPostService = (id, data) => request.put(`/post/me/${id}`, data)

// 删除我的帖子
export const deleteMyPostService = (id) => request.delete(`/post/me/${id}`)

// 我点赞的帖子
export const getMyLikedPostsService = () => request.get('/post/me/liked')

// 我收藏的帖子
export const getMyFavoritedPostsService = () => request.get('/post/me/favorited')
