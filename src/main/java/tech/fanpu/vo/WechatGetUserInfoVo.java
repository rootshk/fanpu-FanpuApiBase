package tech.fanpu.vo;

import lombok.Data;

public class WechatGetUserInfoVo {

    @Data
    public static class QrCodeResponse {
        private Long status;
        private String msg;
        private QrCode data;
    }

    @Data
    public static class QrCode {
        private String ticket;
        private String expireSeconds;
        private String url;
    }

}
