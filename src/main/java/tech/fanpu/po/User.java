package tech.fanpu.po;

import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import lombok.Data;
import tech.fanpu.bo.SessionUserBo;
import tech.fanpu.bo.WechatGetUserInfoBo;

import javax.persistence.*;
import java.util.Date;

/**
 * @Title 用户
 * @Package tech.fanpu.po
 * @Author porridge
 * @Date 2018/10/16 5:49 PM
 * @Version V1.0
 */
@Data
@Entity
@Table(name = "basis_users")
public class User extends BasePo {

    private static final long serialVersionUID = 8189073324093808667L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;//用户ID

    @Version
    private Long version;

    @Column(unique = true)
    private String phone;
    @Column(unique = true)
    private String openId;
    private String nickName;
    private String gender;
    private String language;
    private String city;
    private String province;
    private String country;
    private String avatarUrl;
    private String unionId;

    public User() {
    }

    public SessionUserBo toSessionUserBo() {
        SessionUserBo bo = new SessionUserBo();
        bo.setId(this.userId);
        bo.setName(this.nickName);
        bo.setAvatarUrl(this.avatarUrl);
        bo.setOpenId(this.openId);
        return bo;
    }

    public void setWxMaUserInfo(WxMaUserInfo maUser) {
        this.openId = maUser.getOpenId();
        this.nickName = maUser.getNickName();
        this.language = maUser.getLanguage();
        this.city = maUser.getCity();
        this.province = maUser.getProvince();
        this.country = maUser.getCountry();
        this.avatarUrl = maUser.getAvatarUrl();
        this.unionId = maUser.getUnionId();
        Date date = new Date();
        this.setUpdatedAt(date);
    }

    public void setPublicUserInfo(WechatGetUserInfoBo.UserInfo userInfo) {
        if (userInfo == null) {
            return;
        }
        this.openId = userInfo.getOpenId();
        this.nickName = userInfo.getNickname();
        this.language = userInfo.getLanguage();
        this.city = userInfo.getCity();
        this.province = userInfo.getProvince();
        this.country = userInfo.getCountry();
        this.avatarUrl = userInfo.getHeadImgUrl();
        this.unionId = userInfo.getUnionId();
        Date date = new Date();
        this.setUpdatedAt(date);
    }

}
