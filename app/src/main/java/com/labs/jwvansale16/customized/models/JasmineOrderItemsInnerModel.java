package com.labs.jwvansale16.customized.models;

public class JasmineOrderItemsInnerModel {
private int m_index;

private String Material_No;
private String Material_Desc;
private String VanStockQty;
private String ItemQty;
private String UOM;
private String EANNR;
private String UnitPrice;


    public int getM_index() {
        return m_index;
    }

    public String getUnitPrice() {
        return UnitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        UnitPrice = unitPrice;
    }

    public void setM_index(int m_index) {
        this.m_index = m_index;
    }

    public String getMaterial_No() {
        return Material_No;
    }

    public void setMaterial_No(String material_No) {
        Material_No = material_No;
    }

    public String getMaterial_Desc() {
        return Material_Desc;
    }

    public void setMaterial_Desc(String material_Desc) {
        Material_Desc = material_Desc;
    }

    public String getVanStockQty() {
        return VanStockQty;
    }

    public void setVanStockQty(String vanStockQty) {
        VanStockQty = vanStockQty;
    }

    public String getItemQty() {
        return ItemQty;
    }

    public void setItemQty(String itemQty) {
        ItemQty = itemQty;
    }

    public String getUOM() {
        return UOM;
    }

    public void setUOM(String UOM) {
        this.UOM = UOM;
    }

    public String getEANNR() {
        return EANNR;
    }

    public void setEANNR(String EANNR) {
        this.EANNR = EANNR;
    }
}
