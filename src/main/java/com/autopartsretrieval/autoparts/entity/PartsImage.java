//2. 配件图片实体类 `PartsImage.java`
package com.autopartsretrieval.autoparts.entity;

import lombok.Data;


public class PartsImage {
    private String imageID;
    private String imageName;
    private String storageFormat;
    private String imageSrc;
    private Double imageWidth;
    private Double imageHeight;
    private String partsID;

    // --- 下面全是 Getter 和 Setter ---
    public String getImageID() { return imageID; }
    public void setImageID(String imageID) { this.imageID = imageID; }

    public String getImageName() { return imageName; }
    public void setImageName(String imageName) { this.imageName = imageName; }

    public String getStorageFormat() { return storageFormat; }
    public void setStorageFormat(String storageFormat) { this.storageFormat = storageFormat; }

    public String getImageSrc() { return imageSrc; }
    public void setImageSrc(String imageSrc) { this.imageSrc = imageSrc; }

    public Double getImageWidth() { return imageWidth; }
    public void setImageWidth(Double imageWidth) { this.imageWidth = imageWidth; }

    public Double getImageHeight() { return imageHeight; }
    public void setImageHeight(Double imageHeight) { this.imageHeight = imageHeight; }

    public String getPartsID() { return partsID; }
    public void setPartsID(String partsID) { this.partsID = partsID; }
}