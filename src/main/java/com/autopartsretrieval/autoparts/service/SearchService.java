package com.autopartsretrieval.autoparts.service;

import com.autopartsretrieval.autoparts.SearchResultDTO;
import com.autopartsretrieval.autoparts.mapper.PartsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

@Service
public class SearchService {

    @Autowired
    private PartsMapper partsMapper;

    // 常用汽车配件关键词库（可自行扩展）
    private static final List<String> PARTS_KEYWORDS = Arrays.asList(
            "喷油器", "起动机", "刹车片", "发电机", "火花塞", "滤清器",
            "水泵", "油泵", "电瓶", "轮胎", "大灯", "保险杠", "减震器"
    );

    public List<SearchResultDTO> performSearch(String text, MultipartFile file) {
        // 判断参数状态
        boolean hasText = (text != null && !text.trim().isEmpty());
        boolean hasFile = (file != null && !file.isEmpty());

        System.out.println("接收到前端搜索请求！包含文本: " + hasText + ", 包含图片: " + hasFile);

        // ==========================================
        // 场景 1：图文混合检索 (上传了图片 + 输入了文字)
        // 目标：模拟检索“起动机”
        // ==========================================
        if (hasFile && hasText) {
            System.out.println("====== 执行模拟：图文混合检索 (起动机) ======");
            // 根据数据库脚本，选取 5 个起动机/马达相关的真实图片ID
            // 对应配件：雷诺起动机, 福田康明斯起动机, 起动机, 减速起动机, 重型车起动机
            List<String> starterMockIds = Arrays.asList(
                    "IMG-61-1",
                    "IMG-62-1",
                    "IMG-63-1",
                    "IMG-52-1",
                    "IMG-53-1"
            );
            return partsMapper.getPartsByImageIds(starterMockIds);
        }

        // ==========================================
        // 场景 2：纯图像检索 (只上传了图片，没有文字)
        // 目标：模拟检索“刹车片”
        // ==========================================
        if (hasFile && !hasText) {
            System.out.println("====== 执行模拟：纯图像检索 (刹车片) ======");
            // 根据数据库脚本，选取 5 个刹车片相关的真实图片ID
            // 对应配件：刹车片, 陶瓷刹车片, 半金属刹车片, 前刹车片总成, 后刹车片总成
            List<String> brakePadMockIds = Arrays.asList(
                    "IMG-191-1",
                    "IMG-192-1",
                    "IMG-193-1",
                    "IMG-194-1",
                    "IMG-195-1"
            );
            return partsMapper.getPartsByImageIds(brakePadMockIds);
        }

        // ==========================================
        // 场景 3：纯文本检索 (只有文字，没有图片)
        // 目标：走真实的 MySQL 模糊查询逻辑
        // ==========================================
//        if (!hasFile && hasText) {
//            System.out.println("====== 执行真实：纯文本模糊检索 ======");
//            return partsMapper.getPartsByText(text);
//        }

        // ==========================================
        // 场景 3：纯文本检索 → 核心修改点
        // ==========================================
        if (!hasFile && hasText) {
            System.out.println("====== 执行真实：纯文本模糊检索 ======");
            // 提取核心配件名称（关键代码）
            String searchKeyword = extractPartsKeyword(text);
            System.out.println("原始输入文本：" + text);
            System.out.println("提取后的检索关键词：" + searchKeyword);

            // 使用提取后的精准关键词查询
            return partsMapper.getPartsByText(searchKeyword);
        }

        // 4. 什么都没传的异常情况，返回空列表
        return new ArrayList<>();
    }



    /**
     * 核心方法：从描述文本中提取汽车配件关键词
     * 示例："汽车的喷油器 适配DF43车型" → 返回 "喷油器"
     */
    private String extractPartsKeyword(String inputText) {
        if (inputText == null || inputText.trim().isEmpty()) {
            return inputText;
        }

        // 遍历配件库，匹配文本中包含的配件名称
        for (String keyword : PARTS_KEYWORDS) {
            if (inputText.contains(keyword)) {
                return keyword;
            }
        }

        // 如果没有匹配到预设关键词，返回原文本（兼容普通搜索）
        return inputText.trim();
    }
}