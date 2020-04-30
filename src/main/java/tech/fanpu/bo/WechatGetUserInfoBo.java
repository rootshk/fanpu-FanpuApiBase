package tech.fanpu.bo;

import lombok.Data;

public class WechatGetUserInfoBo {

    @Data
    public static class UserInfoResponse {
        private Long status;
        private String msg;
        private UserInfo data;
    }

    @Data
    public static class UserInfo {

        private String createdAt;
        private String openId;
        private Boolean subscribe;
        private Long userId;
        private String weixinAppId;
        private String nickname;
        private String sexDesc;
        private String sex;
        private String language;
        private String city;
        private String province;
        private String country;
        private String headImgUrl;
        private String tagNames;
        private String subscribeTime;
        private String unionId;
        private String remark;
        private Long groupId;
        private String subscribeScene;
        private String qrScene;
        private String qrSceneStr;
    }

}
