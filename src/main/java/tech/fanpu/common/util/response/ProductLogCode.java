package tech.fanpu.common.util.response;

/**
 * @author hongkeng
 * @version V1.0
 * @Title: OrderCode.java
 * @Package tech.fanpu.common.util.response
 * @Description: 订单返回参数
 * @date 2016年10月27日 下午10:08:14
 */
public class ProductLogCode {

    public static final int HAS_ORDER = 44000001;
    public static final String HAS_ORDER_DEFAULT_MSG = "已存在订单";

    public static final int DONT_HAS_STOCK = 44000002;
    public static final String DONT_HAS_STOCK_DEFAULT_MSG = "没有库存";

    public static final int ORDER_ID_NULL = 44000003;
    public static final String ORDER_ID_NULL_DEFAULT_MSG = "订单ID为空";

}
