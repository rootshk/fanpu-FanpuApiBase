package tech.fanpu.common.util.response;

/**
 * @author porridge
 * @version V1.0
 * @Title: HttpStatusCode.java
 * @Description: TODO http返回状态码
 * @date 2016年10月27日 下午10:07:10
 */
public class HttpStatusCode {

    /**
     * 获取资源成功
     */
    public static final int OK = 200;

    /**
     * 新建或修改数据成功
     */
    public static final int CREATED = 201;
    /**
     * 已删除成功
     */
    public static final int DELETE = 202;

    /**
     * 删除成功
     */
    public static final int NO_CONTENT = 204;

    /**
     * 错误请求
     */
    public static final int BAD_REQUEST = 400;
    /**
     * 未授权
     */
    public static final int UNAUTHORIZED = 401;

    /**
     * 访问被禁止
     */
    public static final int FORBIDDEN = 403;
    /**
     * 资源不存在
     */
    public static final int NOT_FOUND = 404;
    /**
     * 服务端错误
     */
    public static final int INTERNAL_SERVER_ERROR = 500;

}
