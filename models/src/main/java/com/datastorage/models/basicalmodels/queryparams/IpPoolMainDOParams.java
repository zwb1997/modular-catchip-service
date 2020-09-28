package com.datastorage.models.basicalmodels.queryparams;

public class IpPoolMainDOParams {
    private int startPos;
    private int endPos;

    public void setEndPos(int endPos) {
        this.endPos = endPos;
    }

    public void setStartPos(int startPos) {
        this.startPos = startPos;
    }

    public int getEndPos() {
        return endPos;
    }

    public int getStartPos() {
        return startPos;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "[ startPos :" + startPos + " , endPos :" + endPos + "]";
    }
}
