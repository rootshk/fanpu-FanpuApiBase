package tech.fanpu.common.util.response;

/**
 * @author hongkeng
 * @version V1.0
 * @Title: OrderCode.java
 * @Package tech.fanpu.common.util.response
 * @Description: 订单返回参数
 * @date 2016年10月27日 下午10:08:14
 */
public class OrderCode {

    public static final int ORDER_CREATE_NULL = 43000001;
    public static final String ORDER_CREATE_NULL_DEFAULT_MSG = "参数不能为空";

    public static final int PRODUCT_NULL = 43000002;
    public static final String PRODUCT_NULL_DEFAULT_MSG = "productId不能为空";

    public static final int NAME_NULL = 43000003;
    public static final String NAME_NULL_DEFAULT_MSG = "名称不能为空";

    public static final int PHONE_NULL = 43000004;
    public static final String PHONE_NULL_DEFAULT_MSG = "手机号不能为空";

    public static final int START_DATA_NULL = 43000005;
    public static final String START_DATA_NULL_DEFAULT_MSG = "起始时间不能为空";

    public static final int END_DATA_NULL = 43000006;
    public static final String END_DATA_NULL_DEFAULT_MSG = "结束时间不能为空";

    public static final int TYPE_NULL = 43000007;
    public static final String TYPE_NULL_DEFAULT_MSG = "订单类型不能为空";

    public static final int TYPE_ERROR = 43000008;
    public static final String TYPE_ERROR_DEFAULT_MSG = "当前房间不支持该订单模式不能为空";

    public static final int COUNTY_NULL = 43000009;
    public static final String COUNTY_NULL_DEFAULT_MSG = "地区不能为空";

    public static final int CITY_NULL = 43000010;
    public static final String CITY_NULL_DEFAULT_MSG = "城市不能为空";

    public static final int PROVINCE_NULL = 43000011;
    public static final String PROVINCE_NULL_DEFAULT_MSG = "省份不能为空";

    public static final int ADDRESS_NULL = 43000012;
    public static final String ADDRESS_NULL_DEFAULT_MSG = "地址不能为空";

    public static final int EMAIL_NULL = 43000013;
    public static final String EMAIL_NULL_DEFAULT_MSG = "邮箱不能为空";

    public static final int IDENTITY_TYPE_NULL = 43000014;
    public static final String IDENTITY_TYPE_NULL_DEFAULT_MSG = "证件类型不能为空";

    public static final int IDENTITY_NULL = 43000015;
    public static final String IDENTITY_NULL_DEFAULT_MSG = "证件号码不能为空";

    public static final int COMPANY_NAME_NULL = 43000016;
    public static final String COMPANY_NAME_NULL_DEFAULT_MSG = "工作单位不能为空";

    public static final int INDUSTRY_CATEGORY_NULL = 43000017;
    public static final String INDUSTRY_CATEGORY_NULL_DEFAULT_MSG = "行业类别不能为空";

    public static final int USAGE_PURPOSE_NULL = 43000018;
    public static final String USAGE_PURPOSE_NULL_DEFAULT_MSG = "租聘场地目的不能为空";

    public static final int SERVICE_CATEGORY_NULL = 43000019;
    public static final String SERVICE_CATEGORY_NULL_DEFAULT_MSG = "主要从事业务不能为空";

    public static final int PRODUCT_DESCRIPTION_NULL = 43000020;
    public static final String PRODUCT_DESCRIPTION_NULL_DEFAULT_MSG = "主要产品介绍不能为空";

    public static final int END_DATE_ERROR = 43000021;
    public static final String END_DATE_ERROR_DEFAULT_MSG = "结束时间不能在开始时间之前";

    public static final int STOCK_SHORTAGE_CODE = 43000022;
    public static final String STOCK_SHORTAGE_DEFAULT_MSG = "该时间段不可选取";

    public static final int ROOM_USAGE_TIME_NULL = 43000023;
    public static final String ROOM_USAGE_TIME_NULL_DEFAULT_MSG = "租赁时长不能为空";

    public static final int NOT_OPEN = 43000024;
    public static final String NOT_OPEN_DEFAULT_MSG = "不能选择非营业时间段";

    public static final int ACTIVITY_TIMEOUT = 43000025;
    public static final String ACTIVITY_TIMEOUT_DEFAULT_MSG = "活动时间超时";

}
