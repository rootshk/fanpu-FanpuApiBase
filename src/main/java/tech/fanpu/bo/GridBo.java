package tech.fanpu.bo;

import java.io.Serializable;


/**
 * @author porridge
 * @version V1.0
 * @Title: GridBo.java
 * @Package com.tensei.injapan.bo
 * @Description: TODO表格生成对象
 * @date 2016年5月5日 下午5:49:37
 */
public class GridBo<T> implements Serializable {

    private static final long serialVersionUID = 8766296094652310714L;
    private Integer sEcho;//随机数，禁止缓存用
    private Long iTotalRecords;//总行数
    private Long iTotalDisplayRecords;//过滤总行数
    private T aaData;

    public GridBo() {
        super();
    }

    public GridBo(Integer sEcho, Long iTotalRecords, Long iTotalDisplayRecords, T aaData) {
        super();
        this.sEcho = sEcho;
        this.iTotalRecords = iTotalRecords;
        this.iTotalDisplayRecords = iTotalDisplayRecords;
        this.aaData = aaData;
    }

    public Integer getsEcho() {
        return sEcho;
    }

    public void setsEcho(Integer sEcho) {
        this.sEcho = sEcho;
    }

    public Long getiTotalRecords() {
        return iTotalRecords;
    }

    public void setiTotalRecords(Long iTotalRecords) {
        this.iTotalRecords = iTotalRecords;
    }

    public Long getiTotalDisplayRecords() {
        return iTotalDisplayRecords;
    }

    public void setiTotalDisplayRecords(Long iTotalDisplayRecords) {
        this.iTotalDisplayRecords = iTotalDisplayRecords;
    }

    public T getAaData() {
        return aaData;
    }

    public void setAaData(T aaData) {
        this.aaData = aaData;
    }
}
