package com.zzz.entitymodel.servicebase.DTO;

import com.zzz.entitymodel.servicebase.DO.VideoInfoModelDO;

public class QueryCountsDTO {
    private int count;
    private int aid;

    public int getAid() {
        return aid;
    }

    public void setAid(int aid) {
        this.aid = aid;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof VideoInfoModelDO) {
            return this.aid == ((VideoInfoModelDO) o).getAid();
        } else if (o instanceof QueryCountsDTO) {
            return ((QueryCountsDTO) o).getAid() == this.aid && (((QueryCountsDTO) o).getCount() == this.count);
        }else{
            return false;
        }
    }

    @Override
    public int hashCode() {
        return aid;
    }
}
