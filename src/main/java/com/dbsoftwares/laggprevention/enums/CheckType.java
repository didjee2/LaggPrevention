package com.dbsoftwares.laggprevention.enums;

/*
 * Created by DBSoftwares on 30 april 2017
 * Developer: Dieter Blancke
 * Project: LaggPrevention
 * May only be used for CentrixPVP
 */

public enum CheckType {
    
    ENTITY("com.dbsoftwares.laggprevention.checks.entity.EntityCheck"),
    ITEM("com.dbsoftwares.laggprevention.checks.item.ItemCheck"),
    LAGG("com.dbsoftwares.laggprevention.checks.lagg.LaggCheck");
    
    String clazz;
    
    CheckType(String clazz) {
        this.clazz = clazz;
    }
    
    public String getClazz() {
        return clazz;
    }
}
