import request from "@/utils/request.js";

// 文章列表
export const getArticleListService = ({
  page = 1,
  pageSize = 10,
  sortBy = "create_time",
  order = "DESC",
  keyword = "",
} = {}) => {
  return request.get("/article/list", {
    params: { page, pageSize, sortBy, order, keyword },
  });
};

// 文章详情
export const getArticleByIdService = (id) => request.get(`/article/${id}`);

// 最新文章
export const getArticleLatestService = (count = 3) =>
  request.get("/article/latest", { params: { count } });

// 点赞 / 取消点赞（需要登录）
export const toggleArticleLikeService = (id) =>
  request.post(`/article/${id}/like/toggle`);

// 收藏 / 取消收藏（需要登录）
export const toggleArticleFavoriteService = (id) =>
  request.post(`/article/${id}/favorite/toggle`);

// 我点赞的文章
export const getMyLikedArticlesService = () => request.get("/article/me/liked");

// 我收藏的文章
export const getMyFavoritedArticlesService = () =>
  request.get("/article/me/favorited");

// 管理员：文章管理
export const adminGetArticleList = ({
  page = 1,
  pageSize = 10,
  sortBy = "create_time",
  order = "DESC",
  keyword = "",
  includeDeleted = 0,
} = {}) => {
  return request.get("/admin/articles", {
    params: { page, pageSize, sortBy, order, keyword, includeDeleted },
  });
};

export const adminHardDeleteArticle = (id) =>
  request.delete(`/admin/articles/${id}`);

export const adminGetArticleById = (id) => request.get(`/admin/articles/${id}`);

export const adminSoftDeleteArticle = (id) =>
  request.put(`/admin/articles/${id}/delete`);

export const adminRestoreArticle = (id) =>
  request.put(`/admin/articles/${id}/restore`);
