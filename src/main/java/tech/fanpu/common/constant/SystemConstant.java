package tech.fanpu.common.constant;

import lombok.extern.log4j.Log4j2;
import tech.fanpu.common.exception.SystemException;
import tech.fanpu.common.util.response.HttpStatusCode;
import tech.fanpu.common.util.response.ServiceCode;

import java.text.DecimalFormat;
import java.util.Map;

/**
 * @Title
 * @Package tech.fanpu.common.constant
 * @Author porridge
 * @Date 2018/10/16 2:20 PM
 * @Version V1.0
 */
@Log4j2
public class SystemConstant {
  public final static Integer PAGE_COUNT = 20;
  public static String API_HOST;

  /**
   * 用户扫码登录接口
   */
  public final static String USER_QRCODE_URL_REDIS_KEY = "USER_QRCODE_URL_REDIS_KEY";

  /**
   * 用户扫码登录成功后的回调
   */
  public final static String USER_QRCODE_LOGIN_SUCCESS_REDIS_KEY = "USER_QRCODE_LOGIN_SUCCESS_REDIS_KEY";

  public final static String SERVER_BASE_URL = "http://open.fpplus.site";
  public final static String SERVER_GET_QRCODE_URL = "/weixin/qrcode/%s/temp";
  public final static String SERVER_GET_USERINFO_BY_OPEN_ID_URL = "/weixin/user/%s/%s/openId";

}
