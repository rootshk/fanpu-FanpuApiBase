package tech.fanpu.common.exception;

/**
 * @author porridge
 * @version V1.0
 * @Title: InJapanException.java
 * @Package com.tensei.injapan.common.exception
 * @Description: TODO
 * @date 2016年4月28日 下午3:38:35
 */
public class SystemException extends RuntimeException {
    private static final long serialVersionUID = -6434860015551743202L;
    protected int httpCode;
    protected int retCode;
    protected String msg;
    protected Object obj;


    public SystemException(int retCode, String msg) {
        super(msg);
        this.httpCode = 500;
        this.retCode = retCode;
        this.msg = msg;
    }

    public SystemException(int httpCode, int retCode, String msg) {
        super(msg);
        this.httpCode = httpCode;
        this.retCode = retCode;
        this.msg = msg;
    }

    public SystemException(int httpCode, int retCode, String msg, Object obj) {
        super(msg);
        this.httpCode = httpCode;
        this.retCode = retCode;
        this.msg = msg;
        this.obj = obj;
    }

    /**
     * 异常抛出接口
     *
     * @param httpCode           返回http code 参考 HttpStatusCode
     * @param retCode            返回业务错误码
     * @param msg                返回错误信息
     * @param cause              错误或异常的超类
     * @param enableSuppression  是否静默
     * @param writableStackTrace 是否打印堆栈
     */
    public SystemException(int httpCode, int retCode, String msg, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(msg, cause, enableSuppression, writableStackTrace);
        this.httpCode = httpCode;
        this.retCode = retCode;
        this.msg = msg;
    }

    public SystemException(int httpCode, int retCode, String msg, Throwable cause) {
        super(msg, cause);
        this.httpCode = httpCode;
        this.retCode = retCode;
        this.msg = msg;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    public int getHttpCode() {
        return httpCode;
    }

    public void setHttpCode(int httpCode) {
        this.httpCode = httpCode;
    }

    public int getRetCode() {
        return retCode;
    }

    public void setRetCode(int retCode) {
        this.retCode = retCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
