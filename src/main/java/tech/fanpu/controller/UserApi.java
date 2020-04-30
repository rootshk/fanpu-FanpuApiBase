package tech.fanpu.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tech.fanpu.bo.SessionUserBo;
import tech.fanpu.business.UserBusiness;
import tech.fanpu.common.util.UrlUtil;
import tech.fanpu.helper.TokenHelper;

/**
 * @Title
 * @Package tech.fanpu.controller.api
 * @Author porridge
 * @Date 2019-08-18 23:46
 * @Version V1.0
 */
@Api("用户")
@Log4j2
@RestController
@RequestMapping("/api")
public class UserApi {
  @Autowired
  TokenHelper tokenHelper;
  @Autowired
  UserBusiness userBusiness;

  @GetMapping("verify")
  @ApiOperation("查询当前登录的用户")
  public String find (@RequestHeader("token") String token) {
    String openId = tokenHelper.getOpenId(token, false);
    SessionUserBo userBo = userBusiness.findUserMiniBo(openId);
    if(userBo != null) {
      return openId
              + "###" + userBo.getId()
              + "###" + (StringUtils.isBlank(userBo.getName()) ? "" : UrlUtil.getURLEncoderString(userBo.getName()))
              + "###" + (userBo.getAvatarUrl() != null ? UrlUtil.getURLEncoderString(userBo.getAvatarUrl()) : " ");
    }
    return "";
  }
}