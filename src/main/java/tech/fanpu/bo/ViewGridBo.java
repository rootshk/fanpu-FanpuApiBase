package tech.fanpu.bo;

import io.swagger.annotations.ApiModelProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Title
 * @Package tech.fanpu.bo
 * @Author porridge
 * @Date 2018/10/17 11:46 PM
 * @Version V1.0
 */
public class ViewGridBo<T> {

    final static Logger logger = LoggerFactory.getLogger(ViewGridBo.class);
    @ApiModelProperty("总数量")
    private String totalCount;
    @ApiModelProperty("当前页码")
    private Integer currentPage;
    @ApiModelProperty("总页码")
    private String totalPage;
    @ApiModelProperty("最后一页？")
    private boolean lastPage;
    @ApiModelProperty("第一页？")
    private boolean firstPage;
    private T data;

    /**
     * @Description: 将结果数组进行类型转换，从数据库类型转化为接口返回类型
     * @Param: [fromListViewGridBo, toClass]
     * @return: tech.fanpu.bo.ViewGridBo<java.util.List < Tt>>
     * @Author: louis
     * @Date: 2019/7/2
     */
    public static <Tf extends Object, Tt extends Object> ViewGridBo<List<Tt>> convertToResponseDto(ViewGridBo<List<Tf>> dbListViewGridBo, Class<Tt> toClass) {
        ViewGridBo<List<Tt>> responseViewGridBo = new ViewGridBo<>();

        List<Tt> listData = dbListViewGridBo.getData().stream().map(fromObject -> {
            try {
                Object toObject = toClass.newInstance();
                BeanUtils.copyProperties(fromObject, toObject);
                return (Tt) toObject;
            } catch (InstantiationException | IllegalAccessException e) {
                logger.error("ViewGridBo convertToResponseDto method error happen: " + e.getMessage());
                return null;
            }
        }).collect(Collectors.toList());

        BeanUtils.copyProperties(dbListViewGridBo, responseViewGridBo);
        responseViewGridBo.setData(listData);
        return responseViewGridBo;
    }

    public boolean isFirstPage() {
        return firstPage;
    }

    public void setFirstPage(boolean firstPage) {
        this.firstPage = firstPage;
    }

    public boolean isLastPage() {
        return lastPage;
    }

    public void setLastPage(boolean lastPage) {
        this.lastPage = lastPage;
    }

    public String getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount.toString();
    }

    public void setTotalCount(String totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public String getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage.toString();
    }

    public void setTotalPage(String totalPage) {
        this.totalPage = totalPage;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public void clone(ViewGridBo from, T data) {
        this.setData(data);
        this.setTotalCount(from.totalCount);
        this.setTotalPage(from.totalPage);
        this.setCurrentPage(from.currentPage);
        this.setFirstPage(from.firstPage);
        this.setLastPage(from.lastPage);
    }

}
