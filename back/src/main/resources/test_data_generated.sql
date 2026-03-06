-- 测试数据（自动生成）
-- 主题：医务人员职业丧痛与心理支持平台
-- 资源目录（相对于后端静态目录）:
--  /uploads/avatars/{id}.jpg
--  /uploads/articles/{id}.jpg
--  /uploads/article_txt/{id}.txt
--  说明：文章正文在 content 字段中写了简短描述并指向对应的文本文件位置，若需完整正文可手动从文件夹复制到 content 字段中。
SET
    NAMES utf8mb4;

-- ==========================
-- 用户 (10 个，医护人员、心理咨询师及志愿者)
-- ==========================
INSERT INTO
    `user` (
        username,
        password,
        nickname,
        email,
        user_pic,
        status
    )
VALUES
    (
        'admin',
        MD5('admin123'),
        '平台管理员',
        'admin@medcare.com',
        '/uploads/avatars/1.jpg',
        1
    ),
    (
        'dr_wang',
        MD5('drwang123'),
        '肿瘤科王医生',
        'wang.oncology@hospital.com',
        '/uploads/avatars/2.jpg',
        1
    ),
    (
        'nurse_li',
        MD5('nurseli123'),
        'ICU李护士',
        'li.icu@hospital.com',
        '/uploads/avatars/3.jpg',
        1
    ),
    (
        'dr_zhang',
        MD5('drzhang123'),
        '急诊科张医生',
        'zhang.er@hospital.com',
        '/uploads/avatars/4.jpg',
        1
    ),
    (
        'therapist_chen',
        MD5('chen123'),
        '陈心理督导师',
        'chen.psy@clinic.com',
        '/uploads/avatars/5.jpg',
        1
    ),
    (
        'nurse_zhao',
        MD5('zhao123'),
        '安宁疗护赵护士',
        'zhao.palliative@hospital.com',
        '/uploads/avatars/6.jpg',
        1
    ),
    (
        'dr_liu',
        MD5('liu123'),
        '儿科重症刘医生',
        'liu.picu@hospital.com',
        '/uploads/avatars/7.jpg',
        1
    ),
    (
        'dr_sun',
        MD5('sun123'),
        '呼吸内科孙医生',
        'sun.respi@hospital.com',
        '/uploads/avatars/8.jpg',
        1
    ),
    (
        'nurse_zhou',
        MD5('zhou123'),
        '外科周护士长',
        'zhou.surg@hospital.com',
        '/uploads/avatars/9.jpg',
        1
    ),
    (
        'volunteer_wu',
        MD5('wu123'),
        '医务社工小吴',
        'wu.social@hospital.com',
        '/uploads/avatars/10.jpg',
        1
    );

-- ==========================
-- 文章 (6 篇，探讨职业丧痛、心理调适与安宁疗护)
-- ==========================
INSERT INTO
    `article` (
        user_id,
        title,
        first_picture,
        description,
        content,
        views,
        like_count,
        is_deleted,
        create_time,
        update_time
    )
VALUES
    (
        2,
        '肿瘤科医生的自白：面对生命的无力感与自我疗愈',
        '/uploads/articles/1.jpg',
        '从医十年，送走了无数患者。这不仅是医学的局限，更是每个肿瘤科医生必须面对的心理课题。',
        '肿瘤科医生的自白：面对生命的无力感与自我疗愈\n\n很多人觉得医生看惯了生死，心肠都变硬了。其实不然。每一次开出死亡证明，笔尖的沉重感从未减轻过。在肿瘤科，我们不仅要对抗癌细胞，还要处理深深的“无力感”。\n\n上周，那个总是笑眯眯给我带自家种的橘子的老张走了。查房时看到空荡荡的病床，我的心像是被掏空了一块。医学是有局限的，但我们的共情没有局限。这篇文章想和同道们分享，如何在职业丧痛中寻找光亮，如何建立自己的情感缓冲带，不让“共情疲劳”压垮我们……',
        450,
        24,
        0,
        NOW(),
        NOW()
    ),
    (
        5,
        '什么是“替代性创伤”？医护人员如何建立心理防线',
        '/uploads/articles/2.jpg',
        '科普“替代性创伤”的概念，探讨临床医护人员如何识别心理预警信号并进行干预。',
        '什么是“替代性创伤”？医护人员如何建立心理防线\n\n在长期的抢救和护理工作中，医护人员会长期暴露在患者的痛苦、绝望和死亡阴影下。这种间接的创伤暴露，极易引发“替代性创伤”（Vicarious Trauma）。\n\n表现包括：下班后持续的情绪低落、对生死的过度焦虑、失眠或梦魇、甚至对医疗工作产生排斥。建议大家在工作中：1. 明确职业边界，做到“尽力而不越界”；2. 建立科室内部的倾诉机制，不要把情绪带回家；3. 必要时寻求专业的心理督导干预……',
        320,
        18,
        0,
        NOW(),
        NOW()
    ),
    (
        3,
        'ICU病房门外，那些我没能说出口的告别',
        '/uploads/articles/3.jpg',
        '重症监护室的日与夜，呼吸机的滴答声中，一位年轻ICU护士的哀伤日记。',
        'ICU病房门外，那些我没能说出口的告别\n\nICU的门很厚，隔绝了家属的哭声，却隔绝不了生命的流逝。今晚是我这个月的第四个大夜班，拔管的那一刻，监控仪上的波浪线变成了一条直线。\n\n我还记得昨天给这位阿姨擦身子时，她微弱地握了握我的手。作为护士，我们在学校里学了无数种抢救技能，却没有人教过我们，当穷尽一切手段仍无法挽回生命时，我们该如何面对自己内心的波澜？写下这篇日记，以此纪念那些在我职业生涯中匆匆路过的生命。',
        580,
        45,
        0,
        NOW(),
        NOW()
    ),
    (
        6,
        '安宁疗护病房的故事：让生命在温柔中落幕',
        '/uploads/articles/4.jpg',
        '放弃无谓的创伤性抢救，转向症状控制与心理安抚，安宁疗护如何治愈患者也治愈医护。',
        '安宁疗护病房的故事：让生命在温柔中落幕\n\n这里没有刺耳的除颤仪警报，也没有冰冷的插管。在安宁疗护病房（缓和医疗），我们的首要任务是“减轻痛苦”，而不是“延续器官跳动”。\n\n上个月，一位晚期胃癌爷爷在我们的病房里，听着他最爱的京剧，握着老伴的手平静地离开了。没有痛苦的呻吟，只有体面和尊严。这也是安宁疗护给我带来的最大慰藉：当治愈已成奢望，我们还能给予陪伴和安慰。',
        210,
        15,
        0,
        NOW(),
        NOW()
    ),
    (
        7,
        '第一次面对小患者离世：年轻儿科医生的心理重建指南',
        '/uploads/articles/5.jpg',
        '儿科重症病房的生死离别最为揪心，分享我的心路历程与自我疏导方法。',
        '第一次面对小患者离世：年轻儿科医生的心理重建指南\n\n儿科被称为“哑巴科”，而PICU（儿科重症）更是常人难以想象的修罗场。那个患有先天性心脏病的小女孩，大大的眼睛，昨天还在拉着我的白大褂喊“医生哥哥”。\n\n下班后我一个人在车里哭了半个小时。后来主任对我说：“允许自己难过，这是你作为人的温度。但明天穿上白大褂，你还得是其他孩子的希望。”在这里分享几个帮助我走出职业初期崩溃期的实用心理小技巧……',
        410,
        32,
        0,
        NOW(),
        NOW()
    ),
    (
        10,
        '医护心理互助小组：在这里，允许你脱下坚强的白大褂',
        '/uploads/articles/6.jpg',
        '介绍医院内成立的“巴林特小组”运作模式，为医护人员提供情绪安全的港湾。',
        '医护心理互助小组：在这里，允许你脱下坚强的白大褂\n\n长久以来，社会对医护人员的期待是“白衣天使”、“钢铁战士”。但我们也是血肉之躯。今年，院内工会牵头成立了针对医务人员的“巴林特小组”。\n\n在这里，大家坐成一圈，不讨论病例的诊断是否正确，只讨论在救治过程中的“感受”与“情绪”。这种安全的倾诉环境，极大地缓解了大家的职业倦怠感。希望更多的医疗机构能够重视员工的职业丧痛干预。',
        190,
        11,
        0,
        NOW(),
        NOW()
    );

-- ==========================
-- 部分文章点赞与收藏
-- ==========================
INSERT INTO
    `article_like` (article_id, user_id)
VALUES
    (1, 3),
    (1, 4),
    (1, 5),
    (3, 2),
    (3, 6),
    (3, 7),
    (3, 8),
    (5, 1),
    (5, 9);

INSERT INTO
    `article_favorite` (article_id, user_id)
VALUES
    (2, 3),
    (2, 4),
    (2, 6),
    (5, 10),
    (6, 2);

-- 更新文章点赞统计
UPDATE
    `article`
SET
    like_count = (
        SELECT
            COUNT(*)
        FROM
            article_like
        WHERE
            article_like.article_id = article.id
    );

-- ==========================
-- 视频 (4 条，相关的纪录片或科普讲座)
-- ==========================
INSERT INTO
    `video` (
        user_id,
        title,
        url,
        cover,
        description,
        create_time,
        update_time
    )
VALUES
    (
        5,
        '心理学科普：如何处理临床工作中的职业丧痛？',
        '/uploads/videos/1.mp4',
        '/uploads/videos/1.png',
        '专业心理督导师主讲，深入解析医务人员的共情疲劳与应对策略。',
        NOW(),
        NOW()
    ),
    (
        6,
        '访谈：安宁疗护护士眼中的生死观',
        '/uploads/videos/2.mp4',
        '/uploads/videos/2.png',
        '走进安宁疗护病房，聆听一线护士如何面对生命的凋零，又如何汲取前行的力量。',
        NOW(),
        NOW()
    ),
    (
        10,
        '纪录片《守护生命的微光》片段：夜班医生的三点半',
        '/uploads/videos/3.mp4',
        '/uploads/videos/3.png',
        '真实记录急诊科与ICU医生在凌晨的高压抢救工作，以及抢救无效后的沉默瞬间。',
        NOW(),
        NOW()
    ),
    (
        5,
        '正念与放松引导：专为高压环境下的医护人员设计',
        '/uploads/videos/4.mp4',
        '/uploads/videos/4.png',
        '10分钟的正念冥想音频，帮助刚经历抢救失败或重大压力的医护快速平复交感神经系统。',
        NOW(),
        NOW()
    );

-- 视频统计数据初始化
UPDATE
    `video`
SET
    views = 620,
    like_count = 0
WHERE
    id = 1;

UPDATE
    `video`
SET
    views = 450,
    like_count = 0
WHERE
    id = 2;

UPDATE
    `video`
SET
    views = 890,
    like_count = 0
WHERE
    id = 3;

UPDATE
    `video`
SET
    views = 340,
    like_count = 0
WHERE
    id = 4;

-- ==========================
-- 视频点赞与收藏
-- ==========================
INSERT INTO
    `video_like` (video_id, user_id)
VALUES
    (1, 2),
    (1, 3),
    (1, 4),
    (1, 6),
    (2, 1),
    (2, 7),
    (3, 2),
    (3, 8),
    (3, 9),
    (4, 3),
    (4, 4);

INSERT INTO
    `video_favorite` (video_id, user_id)
VALUES
    (1, 3),
    (4, 3),
    (4, 7);

-- 更新视频点赞统计
UPDATE
    `video`
SET
    like_count = (
        SELECT
            COUNT(*)
        FROM
            video_like
        WHERE
            video_like.video_id = video.id
    );

-- ==========================
-- 管理员 (3 个)
-- ==========================
INSERT INTO
    `admin` (
        id,
        username,
        password,
        nickname,
        email,
        admin_pic,
        status
    )
VALUES
    (
        1,
        'admin',
        MD5('admin123'),
        '超级管理员',
        'admin@medcare.com',
        '/uploads/avatars/admin1.jpg',
        1
    ),
    (
        2,
        'content_mgr',
        MD5('admin123'),
        '内容审核员',
        'content@medcare.com',
        '/uploads/avatars/admin2.jpg',
        1
    ),
    (
        3,
        'psy_expert',
        MD5('admin123'),
        '心理督导后台',
        'expert@medcare.com',
        '/uploads/avatars/admin3.jpg',
        1
    );

-- ==========================
-- 帖子（4 条，论坛互动内容）
-- ==========================
INSERT INTO
    `post` (
        user_id,
        title,
        content,
        views,
        like_count,
        reply_count,
        is_deleted,
        create_time,
        update_time
    )
VALUES
    (
        4,
        '大家第一次遇到负责的病人离世时，是怎么调整心态的？',
        '今天凌晨，我管床的一个急性心梗患者没抢救过来。虽然主任安慰我说病情太重没办法，但我一闭上眼睛就是抢救时的心电图。大家当年是怎么走出来的？',
        150,
        0,
        0,
        0,
        NOW(),
        NOW()
    ),
    (
        5,
        '推荐几本关于生死学和医学人文的书籍',
        '面对死亡是医生的必修课。推荐大家阅读阿图·葛文德的《最好的告别》以及保罗·卡拉尼什的《当呼吸化为空气》，或许能给你带来不一样的启发。',
        210,
        0,
        0,
        0,
        NOW(),
        NOW()
    ),
    (
        3,
        '倾诉树洞：今天科室里那位很配合治疗的爷爷走了，心里很难受',
        '爷爷人特别好，每次扎针都说“闺女辛苦了”。今天交班得知他凌晨走了，突然觉得手里的工作失去了意义，感觉很窒息，只想找个地方哭一场。',
        380,
        0,
        0,
        0,
        NOW(),
        NOW()
    ),
    (
        9,
        '关于建立医院内部员工心理辅导机制的几点建议',
        '各位同仁，近期科室年轻护士离职率偏高，大多是因为长期的夜班压力和面对死亡的创伤。我想向院方提交一份建立常态化“巴林特小组”的提案，大家有什么建议吗？',
        120,
        0,
        0,
        0,
        NOW(),
        NOW()
    );

-- ==========================
-- 帖子点赞与收藏
-- ==========================
INSERT INTO
    `post_like` (post_id, user_id)
VALUES
    (1, 2),
    (1, 3),
    (1, 5),
    (1, 6),
    (1, 7),
    (3, 2),
    (3, 4),
    (3, 6),
    (3, 8),
    (4, 1),
    (4, 5);

INSERT INTO
    `post_favorite` (post_id, user_id)
VALUES
    (2, 3),
    (2, 4),
    (2, 7),
    (4, 2),
    (4, 10);

-- 更新帖子点赞统计
UPDATE
    `post`
SET
    like_count = (
        SELECT
            COUNT(*)
        FROM
            post_like
        WHERE
            post_like.post_id = post.id
    );

COMMIT;