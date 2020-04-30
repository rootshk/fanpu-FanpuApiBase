package tech.fanpu.bo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * @Title 登录用户存储数据
 * @Package tech.fanpu.bo
 * @Author porridge
 * @Date 2019/1/22 8:15 PM
 * @Version V1.0
 */
@Data
public class SessionUserBo {
    /**
     * 用户ID
     */
    @JsonIgnore
    private Long id;
    private String name;
    private String avatarUrl;
    private String openId;
    public SessionUserBo () {
    }

}
