package tech.fanpu.common.util.response;

/**
 * @author porridge
 * @version V1.0
 * @Title: ResultBean.java
 * @Package tech.fanpu.common.util.response
 * @Description: TODO 统一返回格式
 * @date 2016年10月27日 下午10:06:55
 */
public class ResultBean<T> {
    private Integer status;
    private Integer code;
    private String msg;
    private T data;

    public ResultBean() {
        super();
    }

    public ResultBean(Integer status, String msg) {
        super();
        this.status = status;
        this.code = status;
        this.msg = msg;
    }

    public ResultBean(Integer status) {
        super();
        this.status = status;
        this.code = status;
    }

    public ResultBean(Integer status, String msg, T data) {
        super();
        this.status = status;
        this.code = status;
        this.msg = msg;
        this.data = data;
    }

    public static ResultBean<Object> defaultSuccess() {
        return new ResultBean<Object>(ServiceCode.OK, ServiceCode.OK_DEFAULT_MSG);
    }

    public static ResultBean<Object> defaultSuccess(Object obj) {
        return new ResultBean<Object>(ServiceCode.OK, ServiceCode.OK_DEFAULT_MSG, obj);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
