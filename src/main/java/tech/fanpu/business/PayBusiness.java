package tech.fanpu.business;

import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import tech.fanpu.bo.GridBo;
import tech.fanpu.po.Pay;
import tech.fanpu.po.Spending;
import tech.fanpu.vo.GridParamsVo;

import java.util.List;

/**
 * @Title 支付
 * @Package tech.fanpu.business
 * @Description
 * @Author porridge
 * @Date 2018/10/25 5:31 PM
 * @Version V1.0
 */
public interface PayBusiness {

    /**
     * 创建一张订单
     *
     * @param pay
     * @return 返回 信息数据
     */
    String create(Pay pay);

    /***
     * 获得支付信息
     * @param orderNo
     * @param ip
     * @return
     */
    WxPayMpOrderResult getPayInfo(String orderNo, String ip) throws WxPayException;

    /**
     * 取消订单
     *
     * @param orderNo
     */
    void cancel(String orderNo) throws WxPayException;

    /**
     * 微信异步通知
     *
     * @param xml
     * @return
     */
    String notify(String xml) throws WxPayException;

    /**
     * 模拟测试支付成功
     *
     * @param payId
     * @return
     */
    String mockNotifySuccess(Integer payId, String payNo, String time);

    /**
     * 查询订单支付状态
     *
     * @param orderNo
     * @return
     */
    Integer findByOrderState(String orderNo);

    /**
     * 根据订单查询
     *
     * @param orderNo
     * @return
     */
    Pay findByOrderNo(String orderNo);

    /**
     * 查询成功的订单
     *
     * @param params
     * @return
     */
    GridBo listSuccess(GridParamsVo params, String associated);

    /**
     * 退款
     *
     * @param orderNo 支付时的订单号
     * @param price   退款价格
     * @param reason  退款原因
     * @throws WxPayException
     */
    String refund(String orderNo, Integer price, String reason) throws WxPayException;

    /**
     * 通过商城订单号获取支付成功的pays
     *
     * @param shopOrderNo 商城订单号
     * @return
     */
    List<Pay> findSuccessPayByOrder(String shopOrderNo);

    /**
     * 提现
     *
     * @param spending
     * @return
     */
    String withdrawal(Spending spending);
}
