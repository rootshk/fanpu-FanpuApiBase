package tech.fanpu.po;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @Title 模版消息发送
 * @Package tech.fanpu.po
 * @Author porridge
 * @Date 2018/12/31 6:48 PM
 * @Version V1.0
 */
@Data
@Entity
@Table(name = "basis_send_templates")
public class SendTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private String openId;
    private String templateId;
    private String page;
    private Boolean sendResult;
    private String formId;
    private Integer state;//状态 1未使用 2已使用
    private String content;
    private Date createdAt;
    private Date updatedAt;

}
