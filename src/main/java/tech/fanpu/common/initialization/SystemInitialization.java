package tech.fanpu.common.initialization;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import tech.fanpu.common.config.ApplicationConfig;
import tech.fanpu.common.constant.SystemConstant;
import tech.fanpu.common.util.FileUtil;

/**
 * @author porridge
 * @version V1.0
 * @Title: SystemInitialization.java
 * @Package tech.fanpu.common.initialization
 * @Description: TODO  系统初始化执行代码
 * @date 2017年10月19日 下午3:32:37
 */
@Component
public class SystemInitialization implements InitializingBean {
    final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    ApplicationConfig config;
    @Autowired
    Environment env;

    @Override
    public void afterPropertiesSet() {
        logger.info(String.format("运行环境:%s", config.env));
        SystemConstant.API_HOST = config.apiHost;
        FileUtil.ensureDirExists(config.tempPath);
    }

}
