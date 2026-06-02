//**1. 配件实体类
package com.autopartsretrieval.autoparts.entity;

import lombok.Data;
import java.math.BigDecimal;


public class Parts {
    private String partsID;
    private String partsCode;
    private String partsName;
    private String partsType;
    private BigDecimal retailPr;
    private String enterpriseID;
    private String adaptedVehicle;

    // 联表查询时用于存储企业名称
    private String enterpriseName;


    // --- 下面全是 Getter 和 Setter ---
    public String getPartsID() { return partsID; }
    public void setPartsID(String partsID) { this.partsID = partsID; }

    public String getPartsCode() { return partsCode; }
    public void setPartsCode(String partsCode) { this.partsCode = partsCode; }

    public String getPartsName() { return partsName; }
    public void setPartsName(String partsName) { this.partsName = partsName; }

    public String getPartsType() { return partsType; }
    public void setPartsType(String partsType) { this.partsType = partsType; }

    public BigDecimal getRetailPr() { return retailPr; }
    public void setRetailPr(BigDecimal retailPr) { this.retailPr = retailPr; }

    public String getEnterpriseID() { return enterpriseID; }
    public void setEnterpriseID(String enterpriseID) { this.enterpriseID = enterpriseID; }

    public String getAdaptedVehicle() { return adaptedVehicle; }
    public void setAdaptedVehicle(String adaptedVehicle) { this.adaptedVehicle = adaptedVehicle; }

    public String getEnterpriseName() { return enterpriseName; }
    public void setEnterpriseName(String enterpriseName) { this.enterpriseName = enterpriseName; }

}

