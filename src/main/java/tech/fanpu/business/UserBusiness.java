package tech.fanpu.business;

import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import tech.fanpu.bo.SessionUserBo;
import tech.fanpu.po.User;
import tech.fanpu.vo.UserStarCallBackVo;

/**
 * @Title
 * @Package tech.fanpu.business
 * @Author porridge
 * @Date 2019-08-18 23:08
 * @Version V1.0
 */
public interface UserBusiness {
    /**
     * 根据openId查询数据
     *
     * @param openId
     * @return
     */
    User findByOpenId(String openId);

    /**
     * 查询基础版用户信息
     * @param openId
     * @return
     */
    SessionUserBo findUserMiniBo(String openId);

    /**
     * 保存openId,
     *
     * @param openId
     */
    User saveOpenId(String openId);

    /**
     * 解密并且绑定用户
     *
     * @param maUser
     * @return
     */
    User decryption(WxMaUserInfo maUser);

    /**
     * 用户扫码关注公众号回调
     *
     * @param vo
     */
    void callback(UserStarCallBackVo vo);

    /**
     * 检查设备是否登录
     *
     * @param ticket
     * @return
     */
    String checkDeviceLoginStatus(String ticket);

}
