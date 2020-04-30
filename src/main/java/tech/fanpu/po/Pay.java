package tech.fanpu.po;

import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @Title 支付
 * @Package tech.fanpu.po
 * @Author porridge
 * @Date 2018/10/16 6:33 PM
 * @Version V1.0
 */
@Data
@Entity
@Table(name = "basis_pays")
public class Pay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long payId;
    @Column(unique = true)
    private String orderNo; //订单号
    private String productName;//购买产品名称
    private String openId; //微信支付openId
    private Long userId; //用户id
    private Integer price;//应付 分
    private String notifyUrl;//异步通知地址
    private String businessType;//业务类型 商城订单
    private Long productId;//产品id
    private String platform;
    private String remark;
    private String prepayId;
    private Integer payStat;//订单支付状态  1 未支付 2已支付 3支付取消 4退款成功
    private String payNo;//第三方支付单号
    private String serviceOrderNo;//服务单号
    private Date payDate;//支付时间
    private String associated;
    @Version
    private Integer version;//乐观锁
    private Date createdAt;
    private Date updatedAt;

    public Pay(String productName, String openId, Long userId, Integer price,
               String businessType, Long productId, String serviceOrderNo) {
        this.productName = productName;
        this.openId = openId;
        this.userId = userId;
        this.price = price;
        this.businessType = businessType;
        this.productId = productId;
        this.serviceOrderNo = serviceOrderNo;
    }

    public Pay() {
    }

    public WxPayUnifiedOrderRequest toPayInfo(String clientIp) {
        WxPayUnifiedOrderRequest request = new WxPayUnifiedOrderRequest();
        request.setDeviceInfo("WEB");
        request.setBody(this.getProductName());
        request.setOutTradeNo(this.getOrderNo());
        request.setDetail(this.getProductName());
        request.setTotalFee(this.getPrice());//总金额|分计
        request.setSpbillCreateIp(clientIp);
        request.setNotifyUrl(this.getNotifyUrl());
        request.setTradeType("JSAPI");//交易类型
        request.setProductId(this.getProductId().toString());//商品id
        request.setOpenid(this.getOpenId());
        return request;
    }
}
