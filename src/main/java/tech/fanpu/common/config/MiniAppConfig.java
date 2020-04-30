package tech.fanpu.common.config;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.config.WxMaInMemoryConfig;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

/**
 * @Title 小程序配置相关
 * @Package tech.fanpu.common.config
 * @Author porridge
 * @Date 2018/10/16 5:05 PM
 * @Version V1.0
 */
@Service
public class MiniAppConfig {
    final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    Environment env;

    @Bean
    public WxMaService lighterService() {
        WxMaInMemoryConfig config = new WxMaInMemoryConfig();
        config.setAppid(env.getProperty("miniapp.appid"));
        config.setSecret(env.getProperty("miniapp.secret"));
        config.setToken("");
        config.setAesKey("");
        WxMaService service = new WxMaServiceImpl();
        logger.info("初始化小程序...");
        service.setWxMaConfig(config);
        return service;
    }

    @Bean
    public WxPayService wxPayService() {
        WxPayService wxPayService = new WxPayServiceImpl();
        WxPayConfig payConfig = new WxPayConfig();
        payConfig.setAppId(env.getProperty("miniapp.appid"));
        payConfig.setMchId(env.getProperty("pay.mcid"));
        payConfig.setMchKey(env.getProperty("pay.secret"));
        payConfig.setKeyPath(env.getProperty("pay.keypath"));
        wxPayService.setConfig(payConfig);
        logger.info("初始化小程序支付...");
        return wxPayService;
    }

}
