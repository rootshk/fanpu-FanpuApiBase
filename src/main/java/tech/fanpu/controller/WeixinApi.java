package tech.fanpu.controller;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tech.fanpu.bo.LoginQrCodeBo;
import tech.fanpu.bo.SessionUserBo;
import tech.fanpu.business.SendTemplateBusiness;
import tech.fanpu.business.UserBusiness;
import tech.fanpu.common.config.ApplicationConfig;
import tech.fanpu.common.constant.SystemConstant;
import tech.fanpu.common.util.response.ResultBean;
import tech.fanpu.common.util.response.ServiceCode;
import tech.fanpu.helper.TokenHelper;
import tech.fanpu.helper.WeixinHelper;
import tech.fanpu.po.User;
import java.util.concurrent.TimeUnit;

@Api("用户")
@Log4j2
@RestController
@RequestMapping("/basis")
public class WeixinApi {
  private final String SESSION_KEY_PREFIX = "FANPU_API_BASE_SESSION_KEY_PREFIX_";
  @Autowired
  WxMaService wxMaService;
  @Autowired
  TokenHelper tokenHelper;
  @Autowired
  WeixinHelper weixinHelper;
  @Autowired
  UserBusiness userBusiness;
  @Autowired
  StringRedisTemplate redis;
  @Autowired
  ApplicationConfig config;
  @Autowired
  SendTemplateBusiness sendTemplateBusiness;

  @GetMapping("login/qrcode")
  @ApiOperation(value = "获取用于设备登录的二维码链接", notes = "请把返回的地址生成二维码展示出来, 并携带里面的ticket轮询是否授权登录接口")
  public ResultBean<LoginQrCodeBo> loginQrCode(@RequestHeader String deviceId) throws Exception {
    return new ResultBean<>(ServiceCode.OK, ServiceCode.OK_DEFAULT_MSG, weixinHelper.getLoginQrCodeUrl(deviceId));
  }

  @GetMapping("check")
  @ApiOperation(value = "检查设备登录状态", notes = "请把返回的token保存好")
  public ResultBean<String> check(@RequestParam String ticket) {
    return new ResultBean<>(ServiceCode.OK, ServiceCode.OK_DEFAULT_MSG, userBusiness.checkDeviceLoginStatus(ticket));
  }

  @GetMapping("login")
  @ApiOperation(value = "用户登录，传递code", notes = "请把返回的token保存好")
  public ResultBean<String> login (@RequestParam String code) throws Exception {
    WxMaJscode2SessionResult result = wxMaService.getUserService().getSessionInfo(code);
    User dbUser = userBusiness.findByOpenId(result.getOpenid());
    if (dbUser == null) {
      dbUser = userBusiness.saveOpenId(result.getOpenid());
    }
    redis.opsForValue().set(config.redisPrefix + SESSION_KEY_PREFIX + result.getOpenid(), result.getSessionKey(), 2, TimeUnit.HOURS);
    String token = tokenHelper.exchangeToken(dbUser.getOpenId());
    return new ResultBean<>(ServiceCode.OK, ServiceCode.OK_DEFAULT_MSG, token);
  }

  @PostMapping("decryption")
  @ApiOperation("拿取用户信息, 解密")
  public ResultBean<SessionUserBo> decryption (@RequestHeader("token") String token,
                                               @RequestParam String rawData, @RequestParam String encryptedData,
                                               @RequestParam String iv, @RequestParam String signature) {
    String openId = tokenHelper.getOpenId(token, true);
    String sessionKey = redis.opsForValue().get(config.redisPrefix + SESSION_KEY_PREFIX + openId);
    if (StringUtils.isBlank(sessionKey) || !wxMaService.getUserService().checkUserInfo(sessionKey, rawData, signature)) {
      log.error("{} 用户数据校验失败.", openId);
      return new ResultBean<>(ServiceCode.UNAUTHORIZED, "用户校验数据失败");
    }

    WxMaUserInfo maUser = wxMaService.getUserService().getUserInfo(sessionKey, encryptedData, iv);
    User user = userBusiness.decryption(maUser);
    SessionUserBo userBo = user.toSessionUserBo();
    userBo.setId(null);
    userBo.setOpenId(null);
    return new ResultBean<>(ServiceCode.OK, ServiceCode.OK_DEFAULT_MSG, userBo);
  }

  @GetMapping("userinfo")
  @ApiOperation("用户信息")
  public ResultBean<SessionUserBo> userinfo (@RequestHeader("token") String token) {
    SessionUserBo userBo = getUserBo(token);
    userBo.setId(null);
    userBo.setOpenId(null);
    return new ResultBean<>(ServiceCode.OK, ServiceCode.OK_DEFAULT_MSG, userBo);
  }

  @PostMapping("template")
  @ApiOperation("保存版消息")
  public ResultBean userinfo (@RequestHeader("token") String token, @RequestParam String formId) {
    SessionUserBo userBo = getUserBo(token);
    sendTemplateBusiness.save(formId, userBo.getId(), userBo.getOpenId());
    return new ResultBean<>(ServiceCode.OK, ServiceCode.OK_DEFAULT_MSG);
  }

  private SessionUserBo getUserBo(String token) {
    String openId = tokenHelper.getOpenId(token, true);
    SessionUserBo userBo = userBusiness.findUserMiniBo(openId);
    if(userBo == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Throw 401 Unauthorized with Custom error code and message");
    }
    return userBo;
  }


}
