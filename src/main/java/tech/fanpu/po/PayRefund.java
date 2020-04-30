package tech.fanpu.po;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * 订单退款
 */
@Data
@Entity
@Table(name = "basis_pay_refunds")
public class PayRefund {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String refundNo;//系统定义的退款单号
    private String orderNo;//系统的支付订单号
    @Column(unique = true)
    private String outRefundNo;//退款订单号
    private Integer totalFee;//订单退款总金额
    private Integer refundFee;//订单退款金额
    private String refundId;//订单退款id  | 第三方提供
    private String reason;//退款原因
    private Boolean success;//退款结果
    @Column(columnDefinition = "text")
    private String errorReason;
    private Date createdAt;
    private Date updatedAt;

}
