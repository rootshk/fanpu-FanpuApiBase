package tech.fanpu.common.util.response;

/**
 * @author porridge
 * @version V1.0
 * @Title: ServiceCode.java
 * @Package tech.fanpu.common.util.response
 * @Description: TODO 业务返回参数
 * @date 2016年10月27日 下午10:08:14
 */
public class ServiceCode {

    public static final int ADMIN_OK = 20000;
    public static final String ADMIN_OK_DEFAULT_MSG = "操作成功";

    public static final int OK = 21020000;
    public static final String OK_DEFAULT_MSG = "操作成功";

    public static final int USER_NOT_INITIALIZE = 41020001;
    public static final String USER_NOT_INITIALIZE_DEFAULT_MSG = "用户未登录";

    public static final int SESSION_TIMEOUT = 21020002;
    public static final String SESSION_TIMEOUT_MSG = "登录超时";

    public static final int UNAUTHORIZED = 41020003;
    public static final String UNAUTHORIZED_DEFAULT_MSG = "未授权";

    public static final int RESOURCE_NOT_FOUND = 41020006;
    public static final String RESOURCE_NOT_FOUND_DEFAULT_MSG = "找不到请求的资源";

    public static final int SERVER_ERROR = 51020000;
    public static final String SERVER_ERROR_DEFAULT_MSG = "系统捉住一只BUG，请重试。";

    public static final int INVILD_PARAM_CODE = 21020001;
    public static final String INVILD_PARAM_DEFAULT_MSG = "无效参数";

}
