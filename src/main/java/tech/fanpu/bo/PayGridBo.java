package tech.fanpu.bo;

import tech.fanpu.common.util.DateUtil;

import java.util.Date;

/**
 * @Title
 * @Package tech.fanpu.bo
 * @Author porridge
 * @Date 2018/11/23 9:25 PM
 * @Version V1.0
 */
public class PayGridBo {
    private String orderNo;
    private String payNo;
    private Date payDate;
    private Integer price;
    private String productName;
    private Integer productId;
    private String nickName;
    private String avatarUrl;
    private Integer userId;

    public PayGridBo(String orderNo, String payNo, Date payDate, Integer price, String productName, Integer productId, String nickName, String avatarUrl, Integer userId) {
        this.orderNo = orderNo;
        this.payNo = payNo;
        this.payDate = payDate;
        this.price = price;
        this.productName = productName;
        this.productId = productId;
        this.nickName = nickName;
        this.avatarUrl = avatarUrl;
        this.userId = userId;
    }

    public PayGridBo() {
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getPayNo() {
        return payNo;
    }

    public void setPayNo(String payNo) {
        this.payNo = payNo;
    }

    public Date getPayDate() {
        return payDate;
    }

    public void setPayDate(Date payDate) {
        this.payDate = payDate;
    }

    public String getPayDateDateTime() {
        return DateUtil.yyyyMMddHHmm(payDate);
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
