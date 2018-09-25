package com.common.api;

import java.io.Serializable;
import java.util.List;

public class PageBean<T extends BaseBean> implements Serializable {
    private int currPage = 1; // 当前页码
    private int pageSize = 10; // 每页行数
    private int total; // 总行数
    private int pageCnt; // 总页数

    // 起始记录index
    private int beginIndex;
    // 末尾记录index
    private int endIndex;

    private List<T> list;

    public void setTotal(int total) {
        this.total = total;

        pageCnt = (this.total - 1) / pageSize + 1;
        if (this.currPage > pageCnt) {
            this.currPage = pageCnt;
        }
        if (this.currPage < 1) {
            this.currPage = 1;
        }
        beginIndex = (this.currPage - 1) * this.pageSize;
        endIndex = beginIndex + this.pageSize;
        if (endIndex > this.total) {
            endIndex = this.total;
        }
    }

    public PageBean(int currPage, int pageSize) {
        this.currPage = currPage;
        this.pageSize = pageSize;
    }

    public PageBean() {
    }

    public int getCurrPage() {
        return currPage;
    }

    public void setCurrPage(int currPage) {
        this.currPage = currPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getBeginIndex() {
        return beginIndex;
    }

    public void setBeginIndex(int beginIndex) {
        this.beginIndex = beginIndex;
    }
}
