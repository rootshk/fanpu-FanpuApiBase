package tech.fanpu.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserStarCallBackVo {

    @ApiModelProperty(value = "信息类型", example = "event")
    private String msgType;

    @ApiModelProperty(value = "ticket", example = "gQG38DwAAAAAAAAAAS5odHRwOi8vd2Vp")
    private String ticket;

    @ApiModelProperty(value = "微信唯一标识", example = "ooOHAv2qbxxdVMrE3rRytngwfn3I")
    private String openId;

    @ApiModelProperty(value = "eventKey", example = "fanpu.tech_amazontest")
    private String eventKey;

    @ApiModelProperty(value = "appid", example = "wx0f27c2dbb7dab562")
    private String appId;

    @ApiModelProperty(value = "event", example = "SCAN")
    private String event;

    @Override
    public String toString() {
        return "UserStarCallBackVo{" +
                "msgType='" + msgType + '\'' +
                ", ticket='" + ticket + '\'' +
                ", openId='" + openId + '\'' +
                ", eventKey='" + eventKey + '\'' +
                ", appId='" + appId + '\'' +
                ", event='" + event + '\'' +
                '}';
    }

}
