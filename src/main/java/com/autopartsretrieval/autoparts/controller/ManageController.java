package com.autopartsretrieval.autoparts.controller;

import com.autopartsretrieval.autoparts.SearchResultDTO;
import com.autopartsretrieval.autoparts.entity.Parts;
import com.autopartsretrieval.autoparts.entity.PartsImage;
import com.autopartsretrieval.autoparts.mapper.PartsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 图片上传引入需要的包，确保放在文件最上方（如果已经有就不需要重复引）
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/manage")
@CrossOrigin(origins = "*")
public class ManageController {

    @Autowired
    private PartsMapper partsMapper;

    // 1. 新增：获取某个配件的所有图片
    @GetMapping("/images/{id}")
    public List<String> getPartImages(@PathVariable("id") String id) {
        return partsMapper.getImagesByPartsID(id);
    }

    // 2. 改造：新增配件 (支持多图循环插入)
    @PostMapping("/add")
    public String addPart(@RequestBody SearchResultDTO dto) {
        String newPartsID = "P-" + System.currentTimeMillis();

        Parts part = new Parts();
        part.setPartsID(newPartsID);
        part.setPartsName(dto.getPartsName());
        part.setPartsCode(dto.getPartsCode());
        part.setRetailPr(dto.getRetailPr());
        part.setAdaptedVehicle(dto.getAdaptedVehicle());
//        part.setEnterpriseID("E-01");

        part.setPartsType(dto.getPartsType()); // ✨ 增加这行
        part.setEnterpriseID(dto.getEnterpriseID()); // ✨ 替换掉原先写死的 "E-01"
        partsMapper.insertPart(part);

        // 循环插入多张图片
        if (dto.getImageList() != null && !dto.getImageList().isEmpty()) {
            for (int i = 0; i < dto.getImageList().size(); i++) {
                PartsImage pImage = new PartsImage();
                pImage.setImageID("IMG-" + System.currentTimeMillis() + "-" + i);
                pImage.setPartsID(newPartsID);
                pImage.setImageSrc(dto.getImageList().get(i));
                pImage.setImageName(dto.getPartsName() + "_图" + (i + 1));
                pImage.setStorageFormat("jpg");
                partsMapper.insertPartsImage(pImage);
            }
        }
        return "success";
    }

    // 3. 改造：更新配件 (先删光旧图，再插入新图，实现多图自由增删)
    @PostMapping("/update")
    public String updatePart(@RequestBody SearchResultDTO dto) {
        Parts part = new Parts();
        part.setPartsID(dto.getPartsID());
        part.setPartsName(dto.getPartsName());
        part.setPartsCode(dto.getPartsCode());
        part.setRetailPr(dto.getRetailPr());
        part.setAdaptedVehicle(dto.getAdaptedVehicle());

        part.setPartsType(dto.getPartsType()); // ✨ 增加这行
        part.setEnterpriseID(dto.getEnterpriseID()); // ✨ 增加这行
        partsMapper.updatePart(part); // 更新基础信息

        // 处理图片修改：简单粗暴的全量替换法
        partsMapper.deleteImagesByPartsID(dto.getPartsID());
        if (dto.getImageList() != null && !dto.getImageList().isEmpty()) {
            for (int i = 0; i < dto.getImageList().size(); i++) {
                PartsImage pImage = new PartsImage();
                pImage.setImageID("IMG-" + System.currentTimeMillis() + "-" + i);
                pImage.setPartsID(dto.getPartsID());
                pImage.setImageSrc(dto.getImageList().get(i));
                pImage.setImageName(dto.getPartsName() + "_编辑图" + (i + 1));
                pImage.setStorageFormat("jpg");
                partsMapper.insertPartsImage(pImage);
            }
        }
        return "success";
    }

    // 1. 获取配件列表
    @GetMapping("/list")
    public List<SearchResultDTO> getList() {
        return partsMapper.getAllManageParts();
    }


    // 3. 删除配件
    @DeleteMapping("/delete/{id}")
    public String deletePart(@PathVariable("id") String id) {
        partsMapper.deletePart(id);
        return "success";
    }


    // 接收前端裁剪后的图片上传 (真实落盘版)
    @PostMapping("/upload")
    public String uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            System.out.println("上传的图片为空！");
            return "";
        }

        // 1. 明确定义前端 public 目录的绝对路径
        // ⚠️ 注意：请确保这个路径与你实际前端项目存放图片的路径完全一致！(注意斜杠方向)
        String frontendPublicPath = "E:/Program Files (x86)/IdeaProjects/autoparts-retrieval-vue/frontend/public/auto_parts_images/UPLOAD/";

        // 检查目录是否存在，如果不存在则自动创建
        File directory = new File(frontendPublicPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // 2. 生成一个全球唯一的文件名，防止不同配件的图片名字冲突覆盖
        String originalName = file.getOriginalFilename();
        String suffix = (originalName != null && originalName.contains("."))
                ? originalName.substring(originalName.lastIndexOf("."))
                : ".jpg"; // 默认给个 jpg 后缀
        String newFileName = UUID.randomUUID().toString().replace("-", "") + suffix;

        // 3. 构建真正的目标文件，并保存到硬盘
        File destFile = new File(directory, newFileName);
        try {
            // transferTo 是 Spring 提供的核心方法，直接把内存里的文件写入硬盘
            file.transferTo(destFile);
            System.out.println("🎉 图片真实上传成功，物理路径: " + destFile.getAbsolutePath());

            // 4. 返回相对路径给数据库存储，例如 "UPLOAD/a1b2c3d4.jpg"
            // 因为前端 <img :src="`/auto_parts_images/${scope.row.imageSrc}`">，所以这里只要返回子路径即可
            return "UPLOAD/" + newFileName;

        } catch (IOException e) {
            System.out.println("❌ 图片上传失败: " + e.getMessage());
            e.printStackTrace();
            return "";
        }
    }

    // 更新获取图表统计数据
    // 4. 获取图表统计数据
    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        Map<String, Object> result = new HashMap<>();
        result.put("enterpriseData", partsMapper.countPartsByEnterprise());
        result.put("typeData", partsMapper.countPartsByType());
        result.put("rankingData", partsMapper.getSearchRanking()); // 新增搜索排行
        return result;
    }


}