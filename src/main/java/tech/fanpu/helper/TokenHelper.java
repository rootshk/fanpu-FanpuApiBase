package tech.fanpu.helper;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import tech.fanpu.common.config.ApplicationConfig;
import tech.fanpu.common.exception.SystemException;
import tech.fanpu.common.util.SystemUtil;
import tech.fanpu.common.util.response.HttpStatusCode;
import tech.fanpu.common.util.response.ServiceCode;
import java.util.concurrent.TimeUnit;

/**
 * @Title token helper
 * @Package tech.fanpu.helper
 * @Author porridge
 * @Date 2018/10/16 4:49 PM
 * @Version V1.0
 */
@Component
public class TokenHelper {
  final Logger logger = LoggerFactory.getLogger(this.getClass());
  final String PREFIX = "FANPU_HAIRDRESSINNG_SHOP_TOKEN_";
  final String JWT_USER_ID_KEY = "FANPU_HAIRDRESSINNG_SHOP_USER_ID_";
  final Integer TIMEOUT_DAYS = 7;
  @Autowired
  ApplicationConfig config;
  @Autowired
  StringRedisTemplate redis;

  private void deleteKey (String key) {
    if (redis.hasKey(key)) {
      redis.delete(key);
    }
  }

  /**
   * 通过用户换token
   */
  public String exchangeToken (String openId) {
    String token = SystemUtil.getUUID();
    String redisKey = JWT_USER_ID_KEY + openId;
    if (redis.hasKey(redisKey)) {
      String oldToken = redis.opsForValue().get(redisKey);
      deleteKey(PREFIX + oldToken);
      deleteKey(PREFIX + oldToken + "_cache");
    }
    redis.opsForValue().set(PREFIX + token, openId, TIMEOUT_DAYS, TimeUnit.DAYS);
    redis.opsForValue().set(redisKey, token, TIMEOUT_DAYS, TimeUnit.DAYS);
    logger.info("{} 用户登录,token:{}", openId, token);
    return token;
  }

  /**
   * 移除token登录状态
   *
   * @param openId
   */
  public void removeUserIdToken (String openId) {
    if (StringUtils.isNotBlank(openId)) {
      return;
    }
    String redisKey = JWT_USER_ID_KEY + openId;
    if (redis.hasKey(redisKey)) {
      String oldToken = redis.opsForValue().get(redisKey);
      deleteKey(PREFIX + oldToken);
      deleteKey(PREFIX + oldToken + "_cache");
      logger.info("移除{}登录帐户状态", openId);
    }
  }

  /**
   * 通过token 拿取用户id
   *
   * @param token
   * @param throwException
   * @return
   */
  public String getOpenId (String token, Boolean throwException) {
    return getRedisKey(token, PREFIX, throwException);
  }

  private String getRedisKey (String token, String key, Boolean throwException) {
    if (token == null) {
      if (throwException) {
        throw new SystemException(HttpStatusCode.BAD_REQUEST, ServiceCode.SESSION_TIMEOUT, ServiceCode.SESSION_TIMEOUT_MSG);
      }
    }

    token = key + token;
    if (redis.hasKey(token) == false) {
      if (throwException) {
        throw new SystemException(HttpStatusCode.BAD_REQUEST, ServiceCode.SESSION_TIMEOUT, ServiceCode.SESSION_TIMEOUT_MSG);
      } else {
        return null;
      }
    }

    String result = redis.opsForValue().get(token);
    if (redis.hasKey(token + "_cache") == false) {
      redis.opsForValue().set(token + "_cache", "true", 1, TimeUnit.DAYS);
      redis.opsForValue().set(token, result, TIMEOUT_DAYS, TimeUnit.DAYS);
    }
    return result;
  }

}
