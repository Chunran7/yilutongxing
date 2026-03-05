<template>
    <el-container class="home-container">
        <!-- 主体内容 -->
        <el-main class="main">
            <!-- 走马灯 -->
            <div class="carousel-container">
                <el-carousel height="500px" :interval="4000" arrow="hover" indicator-position="outside"
                    class="custom-carousel">
                    <el-carousel-item>
                        <div class="carousel-item">
                            <img src="https://images.unsplash.com/photo-1506905925346-21bda4d32df4?ixlib=rb-4.0.3&auto=format&fit=crop&w=1920&q=80"
                                style="width: 100%; height: 100%; object-fit: cover;">
                            <div class="carousel-overlay">
                                <div class="carousel-title">壮丽山川</div>
                                <div class="carousel-subtitle">探索大自然的鬼斧神工</div>
                            </div>
                        </div>
                    </el-carousel-item>
                    <el-carousel-item>
                        <div class="carousel-item">
                            <img src="https://images.unsplash.com/photo-1519681393784-d120267933ba?ixlib=rb-4.0.3&auto=format&fit=crop&w=1920&q=80"
                                style="width: 100%; height: 100%; object-fit: cover;">
                            <div class="carousel-overlay">
                                <div class="carousel-title">宁静湖泊</div>
                                <div class="carousel-subtitle">感受湖水的静谧与纯净</div>
                            </div>
                        </div>
                    </el-carousel-item>
                    <el-carousel-item>
                        <div class="carousel-item">
                            <img src="https://images.unsplash.com/photo-1542281286-9e0a16bb7366?ixlib=rb-4.0.3&auto=format&fit=crop&w=1920&q=80"
                                style="width: 100%; height: 100%; object-fit: cover;">
                            <div class="carousel-overlay">
                                <div class="carousel-title">雪山美景</div>
                                <div class="carousel-subtitle">体验高山雪峰的震撼</div>
                            </div>
                        </div>
                    </el-carousel-item>
                    <el-carousel-item>
                        <div class="carousel-item">
                            <img src="https://images.unsplash.com/photo-1513694203232-719a280e022f?ixlib=rb-4.0.3&auto=format&fit=crop&w=1920&q=80"
                                style="width: 100%; height: 100%; object-fit: cover;">
                            <div class="carousel-overlay">
                                <div class="carousel-title">森林秘境</div>
                                <div class="carousel-subtitle">探索原始森林的奥秘</div>
                            </div>
                        </div>
                    </el-carousel-item>
                </el-carousel>
            </div>

            <div class="content">
                <!-- 介绍我们 -->
                <section class="introduction">
                    <h1 class="section-title">介绍我们</h1>
                    <el-row gutter="60" align="middle">
                        <el-col :span="11">
                            <div class="intro-image-container">
                                <img src="@/assets/logo.png" alt="网站logo" class="intro-image" />
                            </div>
                        </el-col>
                        <el-col :span="13">
                            <div class="intro-text">
                                <h2 class="intro-subtitle">探索世界的每一个角落</h2>
                                <p class="intro-paragraph">
                                    欢迎来到我们的风景旅游网站！这里是旅行者的天堂，我们致力于为您提供最精彩的旅游资讯、最实用的旅行攻略和最美丽的风景展示。
                                </p>
                                <p class="intro-paragraph">
                                    我们的网站汇聚了全球各地的旅游文章，涵盖自然风光、人文景观、美食文化等多个方面。同时，我们还提供丰富的旅游视频，让您身临其境地感受世界各地的美景。
                                </p>
                                <p class="intro-paragraph">
                                    在我们的论坛社区，您可以与其他旅行者分享您的旅行经历，交流旅行心得，结识志同道合的朋友。无论您是经验丰富的旅行达人，还是刚刚开始探索世界的新手，都能在这里找到属于您的旅行灵感。
                                </p>
                                <div class="intro-cta">
                                    <el-button type="primary" size="large" round @click="exploreMore">
                                        开始探索
                                    </el-button>
                                </div>
                            </div>
                        </el-col>
                    </el-row>
                </section>

                <!-- 最新文章 -->
                <section class="articles">
                    <h1 class="section-title">最新文章</h1>
                    <div v-if="loading" class="loading">
                        <el-skeleton :rows="3" animated />
                    </div>
                    <el-row :gutter="24" v-else>
                        <el-col :span="8" v-for="article in articles" :key="article.id">
                            <div class="article-card" @click="goToArticle(article.id)">
                                <div class="article-img-container">
                                    <img :src="normalizeMedia(article.firstPicture) || 'https://placehold.co/600x400/3498db/ffffff?text=Travel+' + article.id"
                                        alt="封面图" class="article-img" />
                                    <div class="article-img-overlay"></div>
                                    <div class="article-category">旅行攻略</div>
                                    <div class="article-reading-time">5分钟阅读</div>
                                </div>
                                <div class="article-content">
                                    <h3 class="article-title">{{ article.title }}</h3>
                                    <p class="article-description">{{ truncate(article.description, 40) }}</p>
                                    <div class="article-meta">
                                        <span class="article-date">{{ article.createTime ? new
                                            Date(article.createTime).toLocaleDateString() : new
                                                Date().toLocaleDateString() }}</span>
                                    </div>
                                </div>
                            </div>
                        </el-col>
                    </el-row>
                </section>

                <!-- 视频课堂模块 -->
                <section class="videos">
                    <h1 class="section-title">视频课堂</h1>
                    <el-row :gutter="20">
                        <el-col :span="6" v-for="(video, index) in videos" :key="video.id">
                            <div class="video-card" @click="goToVideo(video.id)">
                                <div class="video-thumbnail-container">
                                    <img :src="normalizeMedia(video.cover) || 'https://placehold.co/400x300/2ecc71/ffffff?text=Video+' + video.id"
                                        alt="视频封面" class="video-thumbnail" />
                                    <div class="video-overlay">
                                        <div class="video-play-button">
                                            <span class="el-icon-video-play"></span>
                                        </div>
                                    </div>

                                </div>
                                <div class="video-info">
                                    <h3 class="video-title">{{ video.title }}</h3>
                                    <div class="video-meta">
                                        <span class="video-views">{{ video.views || video.view_count || 0 }} 次观看</span>
                                    </div>
                                </div>
                            </div>
                        </el-col>
                    </el-row>

                </section>
            </div>
        </el-main>

        <!-- 页脚 -->
        <Footer />
    </el-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import Footer from '@/components/Footer.vue'
import { getArticleLatestService } from '@/api/article.js'
import { getVideoLatestService } from '@/api/video.js'
import { normalizeMedia } from '@/utils/media.js'

const activeIndex = ref('1')
const isLoggedIn = ref(false)
const router = useRouter()
const articles = ref([])
const videos = ref([])
const loading = ref(true)

const handleLogin = () => {
    router.push('/login')
}

// 摘要截断函数
const truncate = (text, maxLen) => {
    return text?.length <= maxLen ? text : text?.slice(0, maxLen) + '...'
}

const getArticleLatest = async () => {
    try {
        loading.value = true
        const res = await getArticleLatestService(6)
        articles.value = res || []
    } catch (error) {
        console.error('获取最新文章失败:', error)
        articles.value = []
    }
}
const getVideoLatest = async () => {
    try {
        loading.value = true
        const res = await getVideoLatestService(8)
        videos.value = res || []
    } catch (error) {
        console.error('获取视频列表失败:', error)
        videos.value = []
    } finally {
        loading.value = false
    }
}

onMounted(() => {
    getArticleLatest()
    getVideoLatest()
})


const goToArticle = (id) => {
    router.push(`/article/${id}`)
}

const goToVideo = (id) => {
    router.push(`/video/${id}`)
}

const exploreMore = () => {
    // 可以根据需要导航到相应页面
    router.push('/article')
}

</script>

<style scoped>
.home-container {
    min-height: 100vh;
    display: flex;
    flex-direction: column;
    background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

.main {
    background-color: transparent;
    flex: 1;
    padding: 0;
}

.carousel-text {
    color: #475669;
    opacity: 0.75;
    line-height: 200px;
    margin: 0;
    text-align: center;
    font-size: 24px;
}

.content {
    width: 85%;
    max-width: 1200px;
    margin: 0 auto;
    padding: 40px 20px;
}

.section-title {
    text-align: center;
    font-size: 36px;
    font-weight: 700;
    color: #2c3e50;
    margin: 60px 0 40px;
    position: relative;
    padding-bottom: 20px;
}

.section-title::after {
    content: '';
    position: absolute;
    bottom: 0;
    left: 50%;
    transform: translateX(-50%);
    width: 80px;
    height: 4px;
    background: linear-gradient(90deg, #3498db, #2ecc71);
    border-radius: 2px;
}

/* 介绍我们 section 样式 */
.introduction {
    background: white;
    border-radius: 20px;
    padding: 60px;
    box-shadow: 0 12px 40px rgba(0, 0, 0, 0.1);
    margin-bottom: 60px;
    transition: transform 0.3s ease;
}

.introduction:hover {
    transform: translateY(-4px);
    box-shadow: 0 16px 50px rgba(0, 0, 0, 0.15);
}

.intro-image-container {
    position: relative;
    border-radius: 16px;
    overflow: hidden;
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
    transition: transform 0.3s ease;
}

.intro-image-container:hover {
    transform: scale(1.05);
}

.intro-image {
    width: 100%;
    height: auto;
    display: block;
    transition: filter 0.3s ease;
}

.intro-image-container:hover .intro-image {
    filter: brightness(1.05);
}

.intro-text {
    color: #333;
}

.intro-subtitle {
    font-size: 28px;
    font-weight: 700;
    color: #2c3e50;
    margin-bottom: 20px;
    position: relative;
    padding-left: 20px;
}

.intro-subtitle::before {
    content: '';
    position: absolute;
    left: 0;
    top: 50%;
    transform: translateY(-50%);
    width: 4px;
    height: 30px;
    background: linear-gradient(135deg, #3498db, #2ecc71);
    border-radius: 2px;
}

.intro-paragraph {
    font-size: 16px;
    line-height: 1.8;
    margin-bottom: 20px;
    color: #555;
}

.intro-cta {
    margin-top: 30px;
}

.intro-cta .el-button {
    background: linear-gradient(135deg, #3498db, #2ecc71);
    border: none;
    padding: 12px 32px;
    font-size: 16px;
    font-weight: 600;
    transition: all 0.3s ease;
}

.intro-cta .el-button:hover {
    background: linear-gradient(135deg, #2980b9, #27ae60);
    transform: translateY(-2px);
    box-shadow: 0 8px 20px rgba(52, 152, 219, 0.3);
}

/* 文章卡片样式 */
.article-card {
    cursor: pointer;
    transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
    margin: 20px 0;
    border-radius: 16px;
    overflow: hidden;
    background: white;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.article-card:hover {
    box-shadow: 0 16px 40px rgba(52, 152, 219, 0.25);
    transform: translateY(-10px) scale(1.03);
}

.article-img-container {
    position: relative;
    width: 100%;
    height: 240px;
    overflow: hidden;
}

.article-img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.5s ease;
}

.article-card:hover .article-img {
    transform: scale(1.1);
}

.article-img-overlay {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: linear-gradient(to bottom, rgba(0, 0, 0, 0) 60%, rgba(0, 0, 0, 0.3) 100%);
}

.article-category {
    position: absolute;
    top: 15px;
    left: 15px;
    background: linear-gradient(135deg, #3498db, #2ecc71);
    color: white;
    padding: 6px 14px;
    border-radius: 20px;
    font-size: 12px;
    font-weight: 600;
    text-transform: uppercase;
    letter-spacing: 0.5px;
    box-shadow: 0 4px 12px rgba(52, 152, 219, 0.3);
}

.article-reading-time {
    position: absolute;
    bottom: 15px;
    right: 15px;
    background: rgba(255, 255, 255, 0.9);
    color: #2c3e50;
    padding: 6px 12px;
    border-radius: 15px;
    font-size: 12px;
    font-weight: 500;
}

.article-content {
    padding: 20px;
}

.article-title {
    margin: 0 0 14px;
    font-size: 21px;
    font-weight: 700;
    color: #2c3e50;
    line-height: 1.4;
    transition: color 0.3s ease;
}

.article-card:hover .article-title {
    color: #3498db;
}

.article-description {
    font-size: 15px;
    color: #7f8c8d;
    margin-bottom: 18px;
    line-height: 1.7;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
    text-overflow: ellipsis;
}

.article-meta {
    padding-top: 15px;
    border-top: 1px solid #f0f0f0;
}

.article-date {
    font-size: 13px;
    color: #95a5a6;
    font-weight: 500;
}

/* 视频卡片样式 */
.video-card {
    cursor: pointer;
    transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
    margin-bottom: 30px;
    border-radius: 16px;
    overflow: hidden;
    background: white;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.video-card:hover {
    box-shadow: 0 16px 40px rgba(46, 204, 113, 0.25);
    transform: translateY(-10px) scale(1.03);
}

.video-thumbnail-container {
    position: relative;
    width: 100%;
    padding-top: 75%;
    /* 4:3 宽高比 */
    overflow: hidden;
    background: #f0f0f0;
}

.video-thumbnail {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.5s ease;
}

.video-card:hover .video-thumbnail {
    transform: scale(1.1);
}

.video-overlay {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: linear-gradient(to bottom, rgba(0, 0, 0, 0.1) 0%, rgba(0, 0, 0, 0.3) 100%);
    opacity: 0;
    transition: opacity 0.3s ease;
    display: flex;
    align-items: center;
    justify-content: center;
}

.video-card:hover .video-overlay {
    opacity: 1;
}

.video-play-button {
    width: 60px;
    height: 60px;
    background: rgba(255, 255, 255, 0.9);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 8px 20px rgba(0, 0, 0, 0.3);
    transform: scale(0.8);
    transition: all 0.3s ease;
    color: #2ecc71;
}

.video-card:hover .video-play-button {
    transform: scale(1);
    background: rgba(255, 255, 255, 1);
}

.video-play-button .el-icon-video-play {
    font-size: 30px;
    margin-left: 5px;
}

.video-duration {
    position: absolute;
    bottom: 12px;
    right: 12px;
    background: rgba(0, 0, 0, 0.8);
    color: white;
    padding: 4px 8px;
    border-radius: 12px;
    font-size: 11px;
    font-weight: 600;
    letter-spacing: 0.5px;
}

.video-info {
    padding: 16px;
}

.video-title {
    margin: 0 0 10px;
    font-size: 16px;
    font-weight: 600;
    color: #2c3e50;
    line-height: 1.4;
    transition: color 0.3s ease;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
    text-overflow: ellipsis;
    min-height: 42px;
}

.video-card:hover .video-title {
    color: #2ecc71;
}

.video-meta {
    font-size: 12px;
    color: #95a5a6;
}

.video-views {
    display: flex;
    align-items: center;
    font-weight: 500;
}

/* 轮播图样式 */
.carousel-container {
    position: relative;
    width: 100%;
    overflow: hidden;
    border-radius: 0 0 20px 20px;
    box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
}

.carousel-item {
    position: relative;
    width: 100%;
    height: 100%;
    overflow: hidden;
}

.carousel-overlay {
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    padding: 60px 40px 40px;
    background: linear-gradient(to top, rgba(0, 0, 0, 0.8), transparent);
    color: white;
}

.carousel-title {
    font-size: 48px;
    font-weight: 700;
    margin-bottom: 10px;
    text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
    animation: fadeInUp 0.8s ease-out;
}

.carousel-subtitle {
    font-size: 20px;
    opacity: 0.9;
    text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.5);
    animation: fadeInUp 1s ease-out 0.2s both;
}

/* 自定义轮播图指示器 */
.custom-carousel :deep(.el-carousel__indicators) {
    bottom: 30px;
}

.custom-carousel :deep(.el-carousel__indicator) {
    margin: 0 8px;
}

.custom-carousel :deep(.el-carousel__indicator--button) {
    width: 16px;
    height: 16px;
    background: rgba(255, 255, 255, 0.4);
    border-radius: 50%;
    transition: all 0.3s ease;
}

.custom-carousel :deep(.el-carousel__indicator.is-active .el-carousel__indicator--button) {
    width: 40px;
    height: 16px;
    background: white;
    border-radius: 8px;
}

/* 自定义轮播图箭头 */
.custom-carousel :deep(.el-carousel__arrow) {
    width: 50px;
    height: 50px;
    background: rgba(0, 0, 0, 0.3);
    border-radius: 50%;
    font-size: 24px;
    color: white;
    transition: all 0.3s ease;
}

.custom-carousel :deep(.el-carousel__arrow:hover) {
    background: rgba(0, 0, 0, 0.6);
    transform: scale(1.1);
}

/* 动画效果 */
@keyframes fadeInUp {
    from {
        opacity: 0;
        transform: translateY(30px);
    }

    to {
        opacity: 1;
        transform: translateY(0);
    }
}

@keyframes fadeIn {
    from {
        opacity: 0;
    }

    to {
        opacity: 1;
    }
}

@keyframes slideInLeft {
    from {
        opacity: 0;
        transform: translateX(-30px);
    }

    to {
        opacity: 1;
        transform: translateX(0);
    }
}

@keyframes slideInRight {
    from {
        opacity: 0;
        transform: translateX(30px);
    }

    to {
        opacity: 1;
        transform: translateX(0);
    }
}

@keyframes pulse {

    0%,
    100% {
        opacity: 1;
    }

    50% {
        opacity: 0.5;
    }
}

@keyframes ripple {
    0% {
        transform: scale(0);
        opacity: 1;
    }

    100% {
        transform: scale(4);
        opacity: 0;
    }
}

/* 页面加载动画 */
.introduction {
    animation: fadeInUp 0.8s ease-out;
}

.articles {
    animation: fadeInUp 0.8s ease-out 0.2s both;
}

.videos {
    animation: fadeInUp 0.8s ease-out 0.4s both;
}

/* 卡片加载动画 */
.article-card {
    animation: fadeIn 0.6s ease-out;
}

.video-card {
    animation: fadeIn 0.6s ease-out;
}

/* 延迟动画效果 */
.article-card:nth-child(2),
.video-card:nth-child(2) {
    animation-delay: 0.1s;
}

.article-card:nth-child(3),
.video-card:nth-child(3) {
    animation-delay: 0.2s;
}

.article-card:nth-child(4),
.video-card:nth-child(4) {
    animation-delay: 0.3s;
}

.article-card:nth-child(5),
.video-card:nth-child(5) {
    animation-delay: 0.4s;
}

.article-card:nth-child(6),
.video-card:nth-child(6) {
    animation-delay: 0.5s;
}

/* 按钮微交互 */
.intro-cta .el-button {
    position: relative;
    overflow: hidden;
}

.intro-cta .el-button::after {
    content: '';
    position: absolute;
    top: 50%;
    left: 50%;
    width: 0;
    height: 0;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.3);
    transform: translate(-50%, -50%);
    transition: width 0.6s, height 0.6s;
}

.intro-cta .el-button:active::after {
    width: 300px;
    height: 300px;
}

/* 响应式设计 */
@media (max-width: 1024px) {
    .content {
        width: 90%;
    }
}

@media (max-width: 768px) {
    .content {
        width: 95%;
        padding: 20px 10px;
    }

    .section-title {
        font-size: 28px;
        margin: 40px 0 30px;
    }

    /* 介绍我们响应式 */
    .introduction {
        padding: 20px;
    }

    .introduction .el-col {
        width: 100%;
        margin-bottom: 30px;
    }

    .intro-subtitle {
        font-size: 24px;
    }

    /* 文章卡片响应式 */
    .article-card {
        margin: 15px 0;
    }

    .article-card .el-col {
        width: 100%;
    }

    .article-img-container {
        height: 200px;
    }

    .article-title {
        font-size: 19px;
    }

    .article-description {
        font-size: 14px;
    }

    /* 视频卡片响应式 */
    .video-card {
        margin-bottom: 20px;
    }

    .video-card .el-col {
        width: 50%;
    }

    .video-title {
        font-size: 15px;
    }

    /* 轮播图响应式 */
    .carousel-container {
        border-radius: 0;
    }

    .carousel-title {
        font-size: 32px;
    }

    .carousel-subtitle {
        font-size: 16px;
    }

    .carousel-overlay {
        padding: 40px 20px 30px;
    }
}

@media (max-width: 480px) {
    .content {
        padding: 15px 8px;
    }

    .section-title {
        font-size: 24px;
        margin: 30px 0 20px;
    }

    /* 介绍我们响应式 */
    .introduction {
        padding: 15px;
    }

    .intro-subtitle {
        font-size: 22px;
    }

    .intro-paragraph {
        font-size: 15px;
        line-height: 1.6;
    }

    /* 文章卡片响应式 */
    .article-img-container {
        height: 180px;
    }

    .article-content {
        padding: 16px;
    }

    .article-title {
        font-size: 18px;
    }

    /* 视频卡片响应式 */
    .video-card .el-col {
        width: 100%;
    }

    .video-info {
        padding: 12px;
    }

    /* 轮播图响应式 */
    .carousel-title {
        font-size: 26px;
    }

    .carousel-subtitle {
        font-size: 14px;
    }

    .custom-carousel :deep(.el-carousel__arrow) {
        width: 40px;
        height: 40px;
        font-size: 18px;
    }
}
</style>