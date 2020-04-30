package tech.fanpu.business.impl;

import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import tech.fanpu.bo.SessionUserBo;
import tech.fanpu.bo.WechatGetUserInfoBo;
import tech.fanpu.business.*;
import tech.fanpu.common.config.ApplicationConfig;
import tech.fanpu.common.constant.SystemConstant;
import tech.fanpu.common.exception.SystemException;
import tech.fanpu.common.util.DateUtil;
import tech.fanpu.common.util.response.HttpStatusCode;
import tech.fanpu.common.util.response.ServiceCode;
import tech.fanpu.helper.TokenHelper;
import tech.fanpu.helper.WeixinHelper;
import tech.fanpu.po.*;
import tech.fanpu.repository.UserRepository;
import tech.fanpu.vo.UserStarCallBackVo;

import javax.transaction.Transactional;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @Title 用户逻辑
 * @Package tech.fanpu.business.impl
 * @Author porridge
 * @Date 2019-08-18 23:09
 * @Version V1.0
 */
@Slf4j
@Service
@Transactional
public class UserBusinessImpl extends BaseBusinessImpl<User> implements UserBusiness {
  @Autowired
  UserRepository userRepository;
  @Autowired
  ApplicationConfig config;
  @Autowired
  StringRedisTemplate redis;
  @Autowired
  WeixinHelper weixinHelper;
  @Autowired
  TokenHelper tokenHelper;
    final static Type userType = new TypeReference<SessionUserBo>() {}.getType();

  @Override
  public User findByOpenId (String openId) {
    return userRepository.findByOpenId(openId);
  }

  @Override
  public SessionUserBo findUserMiniBo (String openId) {
    if (StringUtils.isBlank(openId)) {
      return null;
    }
    String redisKey = getUserMiniKey(openId);
    SessionUserBo userBo = null;
    if (!redis.hasKey(redisKey)) {
      User user = findByOpenId(openId);
      if (user != null) {
        userBo = user.toSessionUserBo();
        redis.opsForValue().set(redisKey, JSONObject.toJSONString(userBo), 2, TimeUnit.DAYS);
      }
    }

    if(redis.hasKey(redisKey)) {
        userBo = JSON.parseObject(redis.opsForValue().get(redisKey), userType);
    }
    return userBo;
  }

  private String getUserMiniKey(String openId) {
    return  config.redisPrefix + "_findUserMiniBo_" + openId;
  }

  @Override
  public User saveOpenId (String openId) {
    User user = findByOpenId(openId);
    if (user != null) {
      return user;
    }
    user = new User();
    user.setOpenId(openId);
    super.saveOrUpdate(user, user.getUserId());
    return super.saveOrUpdate(user, user.getUserId());
  }

  @Override
  public User decryption (WxMaUserInfo maUser) {
    User user = findByOpenId(maUser.getOpenId());
    Assert.notNull(user, "用户未登录");
    user.setWxMaUserInfo(maUser);
    if(StringUtils.isBlank(user.getNickName())) {
      user.setNickName("-");
    }
    user = super.saveOrUpdate(user, user.getUserId());
    redis.delete(getUserMiniKey(user.getOpenId()));
    return user;
  }

  @Override
  public void callback(UserStarCallBackVo vo) {
    // 获得ticket
    String ticket = vo.getTicket();
    // 去redis找到这个tiket对应的设备ID
    Boolean hasKey = redis.hasKey(config.redisPrefix + SystemConstant.USER_QRCODE_URL_REDIS_KEY + ticket);

    // 没有找到
    if (null == hasKey || !hasKey) {
      log.error("查询设备错误, 未通过ticket找到对应的设备, ticket: {}", ticket);
      return;
    }

    // 查找用户
    User user = userRepository.findByOpenId(vo.getOpenId());
    if (user == null) {
      log.info("未找到该openId:[{}]", vo.getOpenId());
      log.info("自动为该openid自动创建用户");
      user = new User();
    }

    WechatGetUserInfoBo.UserInfo userInfo = weixinHelper.getUserInfoByOpenId(vo.getOpenId());
    user.setPublicUserInfo(userInfo);
    if(StringUtils.isBlank(user.getNickName())) {
      user.setNickName("-");
    }
    super.saveOrUpdate(user, user.getUserId());

    // 删除该ticket
//    redis.delete(config.redisPrefix + SystemConstant.USER_QRCODE_URL_REDIS_KEY + ticket);
    // 设置该ticket的设备为已登录
    redis.opsForValue().set(config.redisPrefix + SystemConstant.USER_QRCODE_LOGIN_SUCCESS_REDIS_KEY + ticket, vo.getOpenId(), 5, TimeUnit.MINUTES);
  }

  @Override
  public String checkDeviceLoginStatus(String ticket) {
    Boolean hasKey = redis.hasKey(config.redisPrefix + SystemConstant.USER_QRCODE_LOGIN_SUCCESS_REDIS_KEY + ticket);
    if (null == hasKey || !hasKey) {
      throw new SystemException(HttpStatusCode.BAD_REQUEST, ServiceCode.USER_NOT_INITIALIZE, "用户未授权登录, 请继续请求");
    }
    String openId = redis.opsForValue().get(config.redisPrefix + SystemConstant.USER_QRCODE_LOGIN_SUCCESS_REDIS_KEY + ticket);
    if (org.apache.commons.lang3.StringUtils.isBlank(openId)) {
      throw new SystemException(HttpStatusCode.BAD_REQUEST, ServiceCode.USER_NOT_INITIALIZE, "用户未授权登录, 请继续请求");
    }
    User user = findByOpenId(openId);

    if (user == null) {
      throw new SystemException(HttpStatusCode.BAD_REQUEST, ServiceCode.USER_NOT_INITIALIZE, "该用户已授权, 但未在系统内, 无法操作");
    }

    return tokenHelper.exchangeToken(openId);
  }
}
