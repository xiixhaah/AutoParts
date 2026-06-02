package com.autopartsretrieval.autoparts.controller;

import com.autopartsretrieval.autoparts.SearchResultDTO;
import com.autopartsretrieval.autoparts.service.SearchService;
import com.autopartsretrieval.autoparts.mapper.PartsMapper; // 新增导入
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // 允许前端跨域请求
public class SearchController {

    @Autowired
    private SearchService searchService;

    @Autowired
    private PartsMapper partsMapper; // 新增注入

//    @PostMapping("/search")
//    public List<SearchResultDTO> search(
//            @RequestParam(value = "text", required = false) String text,
//            @RequestParam(value = "file", required = false) MultipartFile file) {
//
//        return searchService.performSearch(text, file);
//    }

    @PostMapping("/search")
    public List<SearchResultDTO> search(
            @RequestParam(value = "text", required = false) String text,
            @RequestParam(value = "file", required = false) MultipartFile file) {

        // 1. 调用 Service 执行检索 (获取结果)
        List<SearchResultDTO> results = searchService.performSearch(text, file);

        // 2. 异步记录搜索行为到数据库 (只记录第一条命中的结果，代表本次搜索意图)
        if (results != null && !results.isEmpty()) {
            SearchResultDTO topResult = results.get(0);
            try {
                // imageID, userID(默认E-01), 用户上传图片的暂存路径(目前先留空字符串或占位)
                partsMapper.insertSearchRecord(topResult.getImageID(), "E-01", "temp_upload.jpg");
                System.out.println("✅ 搜索记录已保存，命中的 ImageID: " + topResult.getImageID());
            } catch (Exception e) {
                System.out.println("❌ 搜索记录保存失败: " + e.getMessage());
            }
        }
        return results;
    }
}
