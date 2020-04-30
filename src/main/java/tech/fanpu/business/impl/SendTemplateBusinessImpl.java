package tech.fanpu.business.impl;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import tech.fanpu.business.SendTemplateBusiness;
import tech.fanpu.common.util.DateUtil;
import tech.fanpu.helper.WeixinHelper;
import tech.fanpu.po.SendTemplate;
import tech.fanpu.repository.SendTemplateRepository;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Title
 * @Package tech.fanpu.business.impl
 * @Author porridge
 * @Date 2019/1/3 6:46 PM
 * @Version V1.0
 */
@Service
@Transactional
public class SendTemplateBusinessImpl implements SendTemplateBusiness {
    final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    SendTemplateRepository sendTemplateRepository;
    @Autowired
    WeixinHelper weixinHelper;

    @Override
    public void save(String formId, Long userId, String openId) {
        if ("the formId is a mock one".equals(formId)) {
            return;
        }
        SendTemplate template = new SendTemplate();
        template.setUserId(userId);
        template.setOpenId(openId);
        template.setFormId(formId);
        template.setState(1);
        Date date = new Date();
        template.setCreatedAt(date);
        template.setUpdatedAt(date);
        sendTemplateRepository.save(template);
    }

    @Override
    public void sendTemplate(Integer userId, String templateId, String page, Map<String, String> datas) {
        List<SendTemplate> templates = sendTemplateRepository.findByUserIdAndStateAndCreatedAtGreaterThan(Long.valueOf(userId), 1, DateUtil.getAddDayDate(new Date(), -7), PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "id")));
        if (templates.isEmpty()) {
            logger.info("userId:{} tempalteId:{} 没有可用的formId发送模版消息 发送内容：{}", userId, templateId, JSONObject.toJSONString(datas));
        } else {
            SendTemplate template = templates.get(0);
            template.setTemplateId(templateId);
            template.setPage(page);
            template.setSendResult(weixinHelper.sendTemplate(template.getOpenId(), templateId, page, template.getFormId(), datas));
            template.setState(2);
            template.setContent(JSONObject.toJSONString(datas));
            sendTemplateRepository.save(template);
        }
    }
}
