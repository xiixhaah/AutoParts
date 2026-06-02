package com.autopartsretrieval.autoparts.mapper;

import com.autopartsretrieval.autoparts.SearchResultDTO;
import com.autopartsretrieval.autoparts.entity.Parts;
import com.autopartsretrieval.autoparts.entity.PartsImage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

@Mapper
public interface PartsMapper {
    // 根据传入的图片ID列表，联表查询完整的配件信息
    List<SearchResultDTO> getPartsByImageIds(@Param("imageIds") List<String> imageIds);
    // 根据文本模糊检索
    List<SearchResultDTO> getPartsByText(@Param("text") String text);

    // ======== 新增：后台管理与统计接口 ========
    // 1. 获取所有配件列表 (去重图片)
    List<SearchResultDTO> getAllManageParts();

    // 2. 修改配件信息
    int updatePart(Parts part);

    // 3. 删除配件
    int deletePart(@Param("partsID") String partsID);

    // 4. 统计：按供应商分组统计配件数量
    List<Map<String, Object>> countPartsByEnterprise();

    // 5. 统计：按配件类型分组统计数量
    List<Map<String, Object>> countPartsByType();

    // ======== 新增：支持 Manage.vue 的增删改和 Stats.vue 的新图表 ========
    // 新增配件
    int insertPart(Parts part);

    // 插入配件图片关联
    int insertPartsImage(PartsImage partsImage);

    // 获取搜索排行统计 (关联 search_record 表)
    List<Map<String, Object>> getSearchRanking();

    // 新增：插入搜索记录
    int insertSearchRecord(@Param("imageID") String imageID, @Param("userID") String userID, @Param("searchImage") String searchImage);

    // ======== 新增：多图管理专用接口 ========
    // 获取某个配件的所有图片路径
    List<String> getImagesByPartsID(@Param("partsID") String partsID);

    // 删除某个配件名下的所有图片 (用于编辑时全量替换覆盖)
    int deleteImagesByPartsID(@Param("partsID") String partsID);

}
