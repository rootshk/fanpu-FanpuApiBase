package tech.fanpu.controller;

import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tech.fanpu.business.PayBusiness;
import tech.fanpu.common.util.response.ResultBean;
import tech.fanpu.common.util.response.ServiceCode;

/**
 * @Title
 * @Package tech.fanpu.controller.api
 * @Author porridge
 * @Date 2018/10/25 8:19 PM
 * @Version V1.0
 */
@Api(value = "支付")
@RestController
@RequestMapping("/api/pay")
public class PayApi {
    final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    PayBusiness payBusiness;

    @PutMapping("cancel")
    @ApiOperation("取消订单支付")
    public ResultBean cancel(@RequestParam String orderNo) throws WxPayException {
        payBusiness.cancel(orderNo);
        return new ResultBean<>(ServiceCode.OK, ServiceCode.OK_DEFAULT_MSG);
    }

    @GetMapping("info")
    @ApiOperation("获得订单支付信息")
    public ResultBean<WxPayMpOrderResult> info(@RequestHeader("token") String token, @RequestParam String orderNo) throws WxPayException {
//        return new ResultBean<>(ServiceCode.OK, ServiceCode.OK_DEFAULT_MSG, payBusiness.getPayInfo(orderNo, SessionThread.getUser().getIp()));
        return null;
    }

    @GetMapping("check")
    @ApiOperation(value = "检查订单支付状态", notes = "返回结果： 1 未支付 2已支付 3支付取消  如果是 1的状态，  需要以1秒钟重新再请求一下此接口。 直到成为2")
    public ResultBean check(@RequestParam String orderNo) {
        return new ResultBean<>(ServiceCode.OK, ServiceCode.OK_DEFAULT_MSG, payBusiness.findByOrderState(orderNo));
    }

}
