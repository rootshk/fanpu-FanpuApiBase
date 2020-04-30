package tech.fanpu.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author porridge
 * @version V1.0
 * @Title: ApplicationConfig.java
 * @Package tech.fanpu.common.config
 * @Description: TODO 常用环境变量
 * @date 2016年11月1日 下午1:59:12
 */
@Component
public class ApplicationConfig {
  @Value("${api.host}")
  public String apiHost;
  @Value("${spring.profiles.active}")
  public String env;//系统环境  test 测试  product 生产
  @Value("${temp.path}")
  public String tempPath;//存放临时文件路径
  @Value("${qrcode.appid}")
  public String qrAppId;
  @Value("${miniapp.appid}")
  public String appId;
  @Value("${miniapp.secret}")
  public String secret;
  @Value("${server.ip}")
  public String ip;//用户IP
  @Value("${redis.prefix}")
  public String redisPrefix;
}
