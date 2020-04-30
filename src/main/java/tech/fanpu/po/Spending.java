package tech.fanpu.po;

import io.swagger.annotations.ApiParam;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @Title
 * @Package tech.fanpu.po
 * @Author porridge
 * @Date 2019/2/13 11:36 AM
 * @Version V1.0
 */
@Data
@Entity
@Table(name = "basis_spendings")
public class Spending {
    @Id
    @ApiParam(hidden = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ApiParam(hidden = true)
    private String orderNo;//我们的业务单号
    @ApiParam(hidden = true)
    private String result;//结果
    @NotNull
    @ApiParam(required = true, value = "付款类型 1企业付款  2红包")
    private Integer spendingType;
    @NotNull
    @Min(1)
    @ApiParam(required = true, value = "付款金额 | 分")
    private Integer price;
    @ApiParam(required = true, value = "红包数量 | 红包状态下可用")
    private Integer totalNum;
    @ApiParam(required = true, value = "红包发送者名称 | 红包状态下可用")
    private String sendName;
    @ApiParam(required = true, value = "红包祝福语 | 红包状态下可用")
    private String wishing;
    @ApiParam(required = true, value = "活动名称 | 红包状态下可用")
    private String actName;
    @ApiParam(required = true, value = "openId")
    private String openId;
    @ApiParam(required = true, value = "企业付款描述信息,  在零钱收入时显示")
    @Column(name = "description")
    private String desc;
    @ApiParam(required = true, value = "付款原因, 必须写清楚, 内部人看的")
    private String reason;
    @ApiParam(hidden = true)
    private String paymentNo;
    @ApiParam(required = true, value = "业务类型 请自定义")
    private String businessType;
    @ApiParam(required = true, value = "如果是因为某些业务才使用的付款 请填写业务id")
    private String dataId;
    @ApiParam(hidden = true, value = "支付状态 1成功 2失败")
    private Integer stat;
    @Column(length = 5000)
    @ApiParam(hidden = true, value = "失败原因")
    private String errorMsg;
    @ApiParam(hidden = true)
    private Date paymentTime;
    @ApiParam(hidden = true)
    private Date createdAt;
    @ApiParam(hidden = true)
    private Date updatedAt;

    /**
     * 默认构造企业付款
     *
     * @param price
     * @param openId
     * @param desc
     * @param reason
     * @param businessType
     * @param dataId
     */
    public Spending(@NotNull @Min(1) Integer price, String openId, String desc, String reason, String businessType, String dataId) {
        spendingType = 1;
        this.spendingType = spendingType;
        this.price = price;
        this.openId = openId;
        this.desc = desc;
        this.reason = reason;
        this.businessType = businessType;
        this.dataId = dataId;
    }

    public Spending() {
    }

}
