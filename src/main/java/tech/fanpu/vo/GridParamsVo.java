package tech.fanpu.vo;

import org.apache.commons.lang3.StringUtils;
import tech.fanpu.common.constant.SystemConstant;

import javax.validation.constraints.NotNull;

public class GridParamsVo {

    private Integer draw;
    @NotNull
    private Integer begin;
    @NotNull
    private Integer end;
    private String search;
    @NotNull
    private String orderBy;
    @NotNull
    private String sort;

    public GridParamsVo() {
    }

    public GridParamsVo(Integer begin, Integer end) {
        this.begin = begin;
        this.end = end;
        this.search = null;
        this.orderBy = "";
        this.sort = "desc";
    }

    public static GridParamsVo getParams(Integer page) {
        return getParams(page, "id", "desc");
    }

    public static GridParamsVo getParams(Integer page, Integer size, String orderBy, String sort) {
        return getParams(page, orderBy, sort, size);
    }

    public static GridParamsVo getParams(Integer page, String orderBy, String sort) {
        return getParams(page, orderBy, sort, SystemConstant.PAGE_COUNT);
    }

    public static GridParamsVo getParams(Integer page, String orderBy, String sort, Integer pageCount) {
        GridParamsVo params = new GridParamsVo();
        if (page <= 0) {
            page = 1;
        }
        --page;
        params.setBegin(pageCount * page);
        params.setEnd(pageCount * page + pageCount);
        params.setOrderBy(orderBy);
        params.setSort(sort);
        return params;
    }

    public Integer getDraw() {
        return draw;
    }

    public void setDraw(Integer draw) {
        this.draw = draw;
    }

    public Integer getBegin() {
        return begin;
    }

    public void setBegin(Integer begin) {
        this.begin = begin;
    }

    public Integer getEnd() {
        return end;
    }

    public void setEnd(Integer end) {
        this.end = end;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getSort() {
        return " " + sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }
}
