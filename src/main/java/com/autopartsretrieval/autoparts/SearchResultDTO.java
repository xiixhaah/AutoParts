package com.autopartsretrieval.autoparts;

import java.math.BigDecimal;

// 去掉 @Data，我们手写，100% 保证前端能拿到数据！
public class SearchResultDTO {
    private String partsID; // 新增：配件主键ID，用于修改和删除
    private String imageID;
    private String imageSrc;
    private String partsName;
    private String partsCode;
    private BigDecimal retailPr;
    private String enterpriseName;
    private String adaptedVehicle;
    private String partsType;
    private String enterpriseID;


    // 在原有字段下方新增这一行：用于接收和返回多张图片路径
    private java.util.List<String> imageList;

    // 下面全是 Getter 和 Setter
    // 补充 partsID 的 Getter 和 Setter
    public String getPartsID() { return partsID; }
    public void setPartsID(String partsID) { this.partsID = partsID; }

    public String getImageID() { return imageID; }
    public void setImageID(String imageID) { this.imageID = imageID; }

    public String getImageSrc() { return imageSrc; }
    public void setImageSrc(String imageSrc) { this.imageSrc = imageSrc; }

    public String getPartsName() { return partsName; }
    public void setPartsName(String partsName) { this.partsName = partsName; }

    public String getPartsCode() { return partsCode; }
    public void setPartsCode(String partsCode) { this.partsCode = partsCode; }

    public BigDecimal getRetailPr() { return retailPr; }
    public void setRetailPr(BigDecimal retailPr) { this.retailPr = retailPr; }

    public String getEnterpriseName() { return enterpriseName; }
    public void setEnterpriseName(String enterpriseName) { this.enterpriseName = enterpriseName; }

    public String getAdaptedVehicle() { return adaptedVehicle; }
    public void setAdaptedVehicle(String adaptedVehicle) { this.adaptedVehicle = adaptedVehicle; }

    public String getPartsType() { return partsType; }
    public void setPartsType(String partsType) { this.partsType = partsType; }

    public String getEnterpriseID() { return enterpriseID; }
    public void setEnterpriseID(String enterpriseID) { this.enterpriseID = enterpriseID; }




    // 并加上它的 Getter 和 Setter
    public java.util.List<String> getImageList() { return imageList; }
    public void setImageList(java.util.List<String> imageList) { this.imageList = imageList; }
}