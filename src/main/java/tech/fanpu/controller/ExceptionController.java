package tech.fanpu.controller;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import tech.fanpu.common.exception.SystemException;
import tech.fanpu.common.util.response.HttpStatusCode;
import tech.fanpu.common.util.response.ResultBean;
import tech.fanpu.common.util.response.ServiceCode;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import javax.validation.UnexpectedTypeException;
import javax.validation.executable.ValidateOnExecution;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 服务器异常
 *
 * @author porridge
 */
@ControllerAdvice
public class ExceptionController {
  final Logger logger = LoggerFactory.getLogger(this.getClass());

  private Integer getErrorStatus (HttpServletRequest req) {
    return req.getServletPath().indexOf("/web/") == 0 ? HttpStatusCode.INTERNAL_SERVER_ERROR : HttpStatusCode.INTERNAL_SERVER_ERROR;
  }

  /**
   * 自定义异常类
   */
  @ResponseBody
  @ExceptionHandler(value = {SystemException.class})
  public ResultBean<String> customException (HttpServletRequest req, HttpServletResponse resp, Exception e) {
    ResultBean<String> result = new ResultBean<>();
    SystemException businessException = (SystemException) e;
    resp.setStatus(getErrorStatus(req));
    result.setStatus(businessException.getRetCode());
    result.setMsg(businessException.getMessage());
    e.printStackTrace();
    logger.error("customException error", e);
    return result;
  }

  /**
   * 未找到该方法或方法类型错误
   */
  @ResponseBody
  @ExceptionHandler(value = {HttpRequestMethodNotSupportedException.class})
  public ResultBean<String> notFindException (HttpServletRequest req, HttpServletResponse resp, Exception e) {
    resp.setStatus(HttpStatusCode.BAD_REQUEST);
    e.printStackTrace();
    logger.error("notFindException error", e);
    return new ResultBean<>(ServiceCode.RESOURCE_NOT_FOUND, ServiceCode.RESOURCE_NOT_FOUND_DEFAULT_MSG);
  }

  @ResponseBody
  @ExceptionHandler(value = {IllegalArgumentException.class})
  public ResultBean<String> IllegalArgumentException (HttpServletRequest req, HttpServletResponse resp, Exception e) {
    resp.setStatus(getErrorStatus(req));
    logger.info("notFindException报警 {}", "访问错误" + e.getClass().toString() + " " + e.getMessage());
    e.printStackTrace();
    logger.error("error", e);
    IllegalArgumentException illegal = (IllegalArgumentException) e;
    return new ResultBean<>(ServiceCode.RESOURCE_NOT_FOUND, illegal.getMessage());
  }

  /**
   * 参数验证失败 （注意：返回的 HTTP Status 是 400）
   */
  @ResponseBody
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ValidateOnExecution
  @ExceptionHandler(value = {
          MethodArgumentNotValidException.class,
          MethodArgumentTypeMismatchException.class,
          MissingServletRequestParameterException.class,
          TypeMismatchException.class,
          ConstraintViolationException.class,
          UnexpectedTypeException.class})
  public ResultBean<List<String>> validationException (HttpServletRequest req, HttpServletResponse resp, Exception e) {
    ResultBean<List<String>> result = new ResultBean<>();
    String msg = ServiceCode.INVILD_PARAM_DEFAULT_MSG;

    if (e instanceof MethodArgumentNotValidException) {
      MethodArgumentNotValidException exception = (MethodArgumentNotValidException) e;
      List<String> errors = exception.getBindingResult()
              .getFieldErrors()
              .stream()
              .map(x -> x.getField() + " 验证失败：" + x.getRejectedValue())
              .collect(Collectors.toList());
      msg = JSON.toJSONString(errors);
    }

    if (e instanceof MethodArgumentTypeMismatchException) {
      MethodArgumentTypeMismatchException exception = (MethodArgumentTypeMismatchException) e;
      msg = exception.getMessage();
    }

    logger.info("validationException 报警{} {} {}", "请求参数转换失败", JSON.toJSONString(result), e.getClass().toString());
    logger.error("error", e);

    result.setStatus(ServiceCode.INVILD_PARAM_CODE);
    result.setMsg(msg);
    result.setData(new ArrayList<>());

    return result;
  }

  @ResponseBody
  @ValidateOnExecution
  @ExceptionHandler(value = {BindException.class})
  public ResultBean<List<String>> validationBindException (HttpServletRequest req, HttpServletResponse resp, Exception e) {
    resp.setStatus(getErrorStatus(req));
    ResultBean<List<String>> result = new ResultBean<List<String>>();
    result.setStatus(ServiceCode.INVILD_PARAM_CODE);
    result.setMsg(ServiceCode.INVILD_PARAM_DEFAULT_MSG);
    BindException bindException = (BindException) e;
    List<ObjectError> objectErrors = bindException.getAllErrors();
    List<String> errors = new ArrayList<String>();
    for (ObjectError error : objectErrors) {
      errors.add(error.getCodes()[1] + error.getDefaultMessage());
    }
    result.setData(errors);
    e.printStackTrace();
    logger.error("validationBindException error", e);
    return result;
  }

  /**
   * 服务器通用异常
   */
  @ResponseBody
  @ExceptionHandler(value = Exception.class)
  public ResultBean<String> handleException (HttpServletRequest req, HttpServletResponse resp, Exception e) {
    logger.error("handleException报警 {}", "服务器异常" + e.getClass().toString());
    e.printStackTrace();
    logger.error("error", e);
    resp.setStatus(getErrorStatus(req));
    ResultBean<String> result = new ResultBean<>();
    result.setStatus(ServiceCode.SERVER_ERROR);
    result.setMsg(ServiceCode.SERVER_ERROR_DEFAULT_MSG);
    return result;
  }

}
