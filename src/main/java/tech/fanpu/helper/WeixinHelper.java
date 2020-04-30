package tech.fanpu.helper;

import com.alibaba.fastjson.JSONObject;
import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import jodd.util.StringUtil;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import tech.fanpu.bo.LoginQrCodeBo;
import tech.fanpu.bo.WechatGetUserInfoBo;
import tech.fanpu.common.config.ApplicationConfig;
import tech.fanpu.common.constant.SystemConstant;
import tech.fanpu.common.exception.SystemException;
import tech.fanpu.common.util.SystemUtil;
import tech.fanpu.common.util.response.HttpStatusCode;
import tech.fanpu.common.util.response.ServiceCode;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @Title
 * @Package tech.fanpu.helper
 * @Author porridge
 * @Date 2019/1/5 7:47 PM
 * @Version V1.0
 */
@Component
public class WeixinHelper {
    final String TOKEN_KEY = "Fanpu.DeepTechSpace.miniapp.token";
    final Logger logger = LoggerFactory.getLogger(this.getClass());
    final Integer timeout = 3000;
    @Autowired
    ApplicationConfig config;
    @Autowired
    StringRedisTemplate redis;

    /**
     * 发送模版消息
     *
     * @param openId
     * @param templateId
     * @param page
     * @param formId
     * @param datas
     * @return
     */
    public boolean sendTemplate(String openId, String templateId, String page, String formId, Map<String, String> datas) {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("touser", openId);
            map.put("template_id", templateId);
            map.put("page", page);
            map.put("form_id", formId);
            Map<String, Object> temp = new HashMap<>();
            for (String key : datas.keySet()) {
                temp.put(key, new HashMap() {{
                    put("value", datas.get(key));
                }});
            }
            map.put("data", temp);
            String url = String.format("https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token=%s", getToken());
            String body = JSONObject.toJSONString(map);
            HttpResponse response = HttpRequest.post(url).body(StringUtil.convertCharset(body, "UTF-8", "ISO-8859-1")).header("Content-Type", "application/json;charset=utf-8").connectionTimeout(timeout).timeout(timeout).send();
            JSONObject json = JSONObject.parseObject(response.body());
            logger.info("模版消息发送：openId:{},templateId:{} formId:{}发送结果：{}", openId, templateId, formId, response.body());
            if (json.getString("errcode").equals("40001")) {
                redis.delete(TOKEN_KEY);
                return sendTemplate(openId, templateId, page, formId, datas);
            }
            Boolean result = json.getInteger("errcode") == 0;
            return result;
        } catch (Exception e) {
            logger.error("发送模版消息失败, openId:" + openId);
            return false;
        }
    }

    /**
     * 获得token
     *
     * @return
     */
    public String getToken() {
        if (redis.hasKey(TOKEN_KEY)) {
            return redis.opsForValue().get(TOKEN_KEY);
        }
        HttpResponse response = HttpRequest.get(String.format("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s", config.appId, config.secret)).connectionTimeout(timeout).timeout(timeout).send();
        JSONObject json = JSONObject.parseObject(response.body());
        String token = json.getString("access_token");
        logger.info("小程序获得token成功, ", response.body());
        redis.opsForValue().set(TOKEN_KEY, token, json.getInteger("expires_in") - 100, TimeUnit.SECONDS);
        return token;
    }

    public LoginQrCodeBo getLoginQrCodeUrl(String deviceId) {
        String postUrl = SystemConstant.SERVER_BASE_URL + String.format(SystemConstant.SERVER_GET_QRCODE_URL, config.qrAppId);
        HttpResponse response = HttpRequest
                .post(postUrl)
                .timeout(5000)
                .connectionTimeout(5000)
                .contentType("x-www-form-urlencoded")
                .form("scene", "makeup_moblie_qrcode_login")
                .form("expireSeconds", 2592000)
                .send();
        JSONObject responseJson = JSONObject.parseObject(response.bodyText());
        if (!responseJson.getLong("status").equals(21020000L)) {
            // 获取二维码失败
            logger.error("获取二维码失败, deviceId: [{}], 请求: [{}], 返回: [{}]", deviceId, postUrl, response.bodyText());
            return null;
        }
        String ticket = responseJson.getJSONObject("data").getString("ticket");
        redis.opsForValue().set(config.redisPrefix + SystemConstant.USER_QRCODE_URL_REDIS_KEY + ticket, deviceId, 1, TimeUnit.HOURS);
        return new LoginQrCodeBo(responseJson.getJSONObject("data").getString("url"), deviceId, ticket);
    }

    /**
     * 通过openId获取UserInfo
     *
     * @param openId
     * @return
     */
    public WechatGetUserInfoBo.UserInfo getUserInfoByOpenId(String openId) {
        String url = SystemConstant.SERVER_BASE_URL + String.format(SystemConstant.SERVER_GET_USERINFO_BY_OPEN_ID_URL, config.qrAppId, openId);
        return getUserInfo(url);
    }

    /**
     * 获取用户USERINFO
     *
     * @param url
     * @return
     */
    private WechatGetUserInfoBo.UserInfo getUserInfo(String url) {
        ResponseEntity<String> response = new RestTemplate().getForEntity(url, String.class);
        logger.info("请求userinfo: {}", response.getBody());
        if (response.getBody() == null) {
            return null;
        }
        if (!JSONObject.isValidObject(response.getBody())) {
            logger.info("请求userinfo失败: 返回的格式错误: [{}]", response.getBody());
            return null;
        }
        WechatGetUserInfoBo.UserInfoResponse userInfoResponse = JSONObject.parseObject(response.getBody(), WechatGetUserInfoBo.UserInfoResponse.class);
        if (userInfoResponse.getStatus() != ServiceCode.OK) {
            logger.error("获取用户UserInfo错误, 原因: {}", userInfoResponse.getMsg());
            return null;
        }
        logger.info("返回的userinfo: {}", userInfoResponse.getData());
        return userInfoResponse.getData();
    }



}
