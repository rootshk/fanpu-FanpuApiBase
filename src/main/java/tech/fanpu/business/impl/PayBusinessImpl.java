package tech.fanpu.business.impl;

import com.github.binarywang.wxpay.bean.entpay.EntPayRequest;
import com.github.binarywang.wxpay.bean.entpay.EntPayResult;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import com.github.binarywang.wxpay.bean.request.WxPayRefundRequest;
import com.github.binarywang.wxpay.bean.result.WxPayRefundResult;
import com.github.binarywang.wxpay.constant.WxPayConstants;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import tech.fanpu.bo.GridBo;
import tech.fanpu.bo.PayGridBo;
import tech.fanpu.business.*;
import tech.fanpu.common.config.ApplicationConfig;
import tech.fanpu.common.exception.SystemException;
import tech.fanpu.common.helper.QueryHelper;
import tech.fanpu.common.util.SystemUtil;
import tech.fanpu.common.util.response.HttpStatusCode;
import tech.fanpu.common.util.response.ServiceCode;
import tech.fanpu.po.Pay;
import tech.fanpu.po.PayRefund;
import tech.fanpu.po.Spending;
import tech.fanpu.repository.PayRefundRepository;
import tech.fanpu.repository.PayRepository;
import tech.fanpu.repository.SpendingRepository;
import tech.fanpu.vo.GridParamsVo;

import javax.transaction.Transactional;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Title
 * @Package tech.fanpu.business.impl
 * @Author porridge
 * @Date 2018/10/25 5:34 PM
 * @Version V1.0
 */
@Slf4j
@Service
@Transactional
public class PayBusinessImpl implements PayBusiness {
    final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    PayRepository payRepository;
    @Autowired
    WxPayService payService;
    @Autowired
    ApplicationConfig config;
    @Autowired
    QueryHelper queryHelper;
    @Autowired
    PayRefundRepository payRefundRepository;
    @Autowired
    StringRedisTemplate redis;
    @Autowired
    SendTemplateBusiness sendTemplateBusiness;
    @Autowired
    SpendingRepository spendingRepository;
    @Lazy
    @Autowired
    UserBusiness userBusiness;

    @Override
    public String create(Pay pay) {
        String orderNo = SystemUtil.getOrderNo();
        pay.setOrderNo(orderNo);
        pay.setPlatform("miniapp");
        pay.setPayStat(1);
        pay.setNotifyUrl(config.apiHost + "/open/wx/notify");
        Date date = new Date();
        pay.setCreatedAt(date);
        pay.setUpdatedAt(date);
        if (!config.env.equals("product")) {
            pay.setProductName("[测试]" + pay.getProductName());
        }
        payRepository.save(pay);
        return orderNo;
    }

    @Override
    public WxPayMpOrderResult getPayInfo(String orderNo, String ip) throws WxPayException {
        Pay pay = findByOrderNo(orderNo);
        if (!"product".equals(config.env)) {
            pay.setProductName("[测试]" + pay.getProductName());
        }
        WxPayMpOrderResult result = payService.createOrder(pay.toPayInfo(ip));
        pay.setUpdatedAt(new Date());
        pay.setPrepayId(result.getPackageValue().replace("prepay_id=", ""));
        return result;
    }

    @Override
    public void cancel(String orderNo) throws WxPayException {
        Pay pay = findByOrderNo(orderNo);
        Assert.isTrue(pay.getPayStat() != 2, "已支付订单不允许取消");
        pay.setPayStat(3);
        pay.setUpdatedAt(new Date());
        payRepository.save(pay);
        payService.closeOrder(orderNo);

    }

    @Override
    public String notify(String xmlData) throws WxPayException {
        Assert.isTrue(StringUtils.isNotBlank(xmlData), "非法请求, xmlData 为空");
        WxPayOrderNotifyResult result = payService.parseOrderNotifyResult(xmlData);
        if ("SUCCESS".equals(result.getResultCode())) {
            logger.info("支付回调XML:\n" + xmlData);
            Pay pay = findByOrderNo(result.getOutTradeNo());
            Assert.notNull(pay, "支付校验成功, 但是订单号不存在" + result.getOutTradeNo());
            pay.setPayStat(2);
            pay.setUpdatedAt(new Date());
            pay.setPayNo(result.getTransactionId());
            try {
                pay.setPayDate(new SimpleDateFormat("yyyyMMddHHmmss").parse(result.getTimeEnd()));
            } catch (ParseException e) {
                logger.error("weixin pay notify 转换时间出现失败.");
            }
            payRepository.save(pay);

            for (int i = 0; i < 3; i++) {//保存三次发送模版消息的机会
                sendTemplateBusiness.save(pay.getPrepayId(), pay.getUserId(), pay.getOpenId());
            }
            logger.info("订单{} 支付成功 支付金额{}", pay.getOrderNo(), pay.getPrice());
            paySuccessNotifyBusiness(pay);
            return "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
        } else {
            logger.error("支付校验未通过, 支付失败:{}", xmlData);
        }
        return "error";
    }

    private void paySuccessNotifyBusiness(Pay pay) {
        String orderNo = pay.getServiceOrderNo();
        try {

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("支付成功，业务回调失败, orderNo = " + orderNo, e);
        }
    }

    @Override
    public String mockNotifySuccess(Integer payId, String payNo, String time) {
        Pay pay = payRepository.findById(payId).get();
        Assert.notNull(pay, "支付校验成功, 但是订单号不存在" + pay.getOrderNo());
        pay.setPayStat(2);
        pay.setUpdatedAt(new Date());
        pay.setPayNo(payNo);
        try {
            pay.setPayDate(new SimpleDateFormat("yyyyMMddHHmmss").parse(time));
        } catch (ParseException e) {
            logger.error("weixin pay notify 转换时间出现失败.");
        }
        payRepository.save(pay);

        logger.info("订单{} 模拟支付成功 支付金额{}", pay.getOrderNo(), pay.getPrice());
        paySuccessNotifyBusiness(pay);
        return "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
    }

    @Override
    public Integer findByOrderState(String orderNo) {
        return findByOrderNo(orderNo).getPayStat();
    }

    @Override
    public GridBo listSuccess(GridParamsVo params, String associated) {
        Map<String, Object> fixeds = new HashMap<>();
        String hql = "from Pay p,User u where p.payStat=2 and p.userId=u.userId";
        if (associated != null) {
            hql += " and u.associated like '" + associated + "'";
        }
        Map<String, Object> result = queryHelper.queryHql(hql, "new tech.fanpu.bo.PayGridBo(p.orderNo, p.payNo, p.payDate, p.price, p.productName, p.productId, u.nickName, u.avatarUrl, u.userId)", params.getOrderBy() + " " + params.getSort(), null, fixeds, null,
            PayGridBo.class, params.getBegin(), params.getEnd() - params.getBegin());
        Long count = Long.valueOf(result.get("count").toString());
        return new GridBo(params.getDraw(), count, count, result.get("result"));
    }

    @Override
    public Pay findByOrderNo(String orderNo) {
        Pay pay = payRepository.findByOrderNo(orderNo);
        Assert.notNull(pay, "未找到支付订单数据");
        return pay;
    }

    @Override
    public List<Pay> findSuccessPayByOrder(String shopOrderNo) {
        return payRepository.findAllByPayStatAndServiceOrderNo(2, shopOrderNo);
    }

    @Override
    public String refund(String orderNo, Integer price, String reason) throws WxPayException {
        Pay pay = payRepository.findByOrderNo(orderNo);
        Assert.notNull(pay, "未找到该订单");
        Assert.isTrue(pay.getPayStat() == 2, "状态错误, 只允许退款已支付的订单");
        Long tmp = payRefundRepository.findRefundCount(orderNo); //目前已退金额
        Integer currentPirce = tmp == null ? 0 : tmp.intValue();
        if (price + currentPirce.intValue() > pay.getPrice()) {
            throw new SystemException(HttpStatusCode.INTERNAL_SERVER_ERROR, ServiceCode.SERVER_ERROR, "退款金额错误, 最大退款数为:" + (pay.getPrice() - currentPirce) + "分");
        }
        String refundNo = "REFUND_" + SystemUtil.getOrderNo();

        WxPayRefundRequest request = new WxPayRefundRequest();
        request.setRefundAccount(WxPayConstants.RefundAccountSource.UNSETTLED_FUNDS);
        request.setOutTradeNo(orderNo);
        request.setOutRefundNo(refundNo);
        request.setTotalFee(pay.getPrice());
        request.setRefundFee(price);
        request.setAppid(config.appId);

        WxPayRefundResult result = null;
        String error = "";
        WxPayException payError = null;
        try {
            result = payService.refund(request);
            logger.info("{} 尝试退款{}分 退款渠道 可用余额退款 退款结果：{}", orderNo, price, result.toString());
        } catch (WxPayException pe) {
            payError = pe;
            logger.error(pe.toString());
            error = pe.getErrCodeDes();
        }

        if ((result == null || !"SUCCESS".equals(result.getResultCode())) && "交易未结算资金不足，请使用可用余额退款".equals(error)) {
            try {
                request.setRefundAccount(WxPayConstants.RefundAccountSource.UNSETTLED_FUNDS);
                result = payService.refund(request);
                logger.info("{} 尝试退款{}分 退款渠道 未结算金额 退款结果：{}", orderNo, price, result.toString());
            } catch (WxPayException pe) {
                payError = pe;
                logger.error(pe.toString());
            }
        }

        Date date = new Date();
        PayRefund refund = new PayRefund();
        refund.setRefundNo(refundNo);
        refund.setOrderNo(orderNo);
        refund.setCreatedAt(date);
        refund.setUpdatedAt(date);
        refund.setSuccess(result != null && "SUCCESS".equals(result.getResultCode()));
        if (refund.getSuccess()) {
            refund.setOutRefundNo(result.getOutRefundNo());
            refund.setRefundId(result.getRefundId());
        }
        refund.setErrorReason(payError != null ? payError.getXmlString() : "");
        refund.setTotalFee(pay.getPrice());
        refund.setRefundFee(price);
        refund.setReason(reason);
        payRefundRepository.save(refund);
        if (refund.getSuccess() && pay.getPrice().equals((price + currentPirce))) {
            pay.setPayStat(4);
            pay.setUpdatedAt(date);
            payRepository.save(pay);
        }

        // 退款成功回调
        if (refund.getSuccess()) {
            refundSuccessNotifyBusiness(pay, price);
        }
        return refund.getSuccess() ? refundNo : "ERROR#" + orderNo;
    }

    private void refundSuccessNotifyBusiness(Pay pay, Integer price) {
        String orderNo = pay.getServiceOrderNo();
        try {

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("支付成功，业务回调失败, orderNo = " + orderNo, e);
        }
    }

    @Override
    public String withdrawal(Spending spending) {
        if(!"product".equals(config.env)) {
            logger.info ("非生产环境, 不进行付款操作");
            return "000000";
        }
        String key = "spending.lock.userid." + spending.getOpenId();
        if (!redis.opsForValue().setIfAbsent(key, "")) {
            throw new SystemException(HttpStatusCode.INTERNAL_SERVER_ERROR, ServiceCode.INVILD_PARAM_CODE, "用户支付正在进行中...");
        }
        redis.expire(key, 1, TimeUnit.MINUTES);

        if (spendingRepository.countByBusinessTypeAndDataIdAndStat(spending.getBusinessType(), spending.getDataId(), 1) != 0L) {
            throw new SystemException(HttpStatusCode.INTERNAL_SERVER_ERROR, ServiceCode.INVILD_PARAM_CODE, "频繁使用同一dataId支付, 系统拒绝!");
        }
        String orderNo = SystemUtil.getOrderNo();
        EntPayRequest request = new EntPayRequest();
        request.setMchAppid(payService.getConfig().getAppId());
        request.setMchId(payService.getConfig().getMchId());
        request.setPartnerTradeNo(orderNo);
        request.setOpenid(spending.getOpenId());
        request.setCheckName("NO_CHECK");
        request.setAmount(spending.getPrice());
        request.setDescription(spending.getDesc());
        request.setSpbillCreateIp(config.ip);
        spending.setOrderNo(orderNo);
        try {
            EntPayResult result = payService.getEntPayService().entPay(request);
            if ("SUCCESS".equals(result.getResultCode())) {
                Date date = new Date();
                spending.setPaymentNo(result.getPaymentNo());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                try {
                    spending.setPaymentTime(sdf.parse(result.getPaymentTime()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                spending.setCreatedAt(date);
                spending.setUpdatedAt(date);
                spending.setStat(1);
            } else {
                spending.setStat(2);
                spending.setErrorMsg(result.toString());
                logger.error("发起付款失败, {} {} {}", spending.getOpenId(), result.getErrCode(), result.getErrCodeDes());
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("发起付款失败，" + e);
            spending.setStat(2);
            spending.setErrorMsg("系统级别异常，详见日志");
        } finally {
            redis.delete(key);
        }
        spendingRepository.save(spending);
        return spending.getStat() == 1 ? orderNo : null;
    }

}
