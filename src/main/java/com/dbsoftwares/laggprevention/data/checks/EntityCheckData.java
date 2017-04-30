package com.dbsoftwares.laggprevention.data.checks;

/*
 * Created by DBSoftwares on 30 april 2017
 * Developer: Dieter Blancke
 * Project: LaggPrevention
 * May only be used for CentrixPVP
 */

import com.dbsoftwares.laggprevention.enums.LaggEntity;
import lombok.Data;

import java.util.Map;

@Data
public class EntityCheckData {

    Integer total;
    Map<LaggEntity, Integer> mobLimits;
    Boolean kill;

    public EntityCheckData(Integer total, Map<LaggEntity, Integer> mobLimits, Boolean kill) {
        this.total = total;
        this.mobLimits = mobLimits;
        this.kill = kill;
    }
}