package tech.fanpu.business;

import java.util.Map;

/**
 * @Title 模版消息相关
 * @Package tech.fanpu.business
 * @Author porridge
 * @Date 2019/1/3 6:44 PM
 * @Version V1.0
 */
public interface SendTemplateBusiness {
    /**
     * 保存一条 埋点消息
     *
     * @param formId
     * @param userId
     * @param openId
     */
    void save (String formId, Long userId, String openId);

    /**
     * 发送模版消息
     *
     * @param userId
     * @param templateId
     * @param page
     * @param datas
     */
    void sendTemplate (Integer userId, String templateId, String page, Map<String, String> datas);
}
