package tech.fanpu.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author roothk
 */
@Data
@ApiModel("设备登录返回参数")
public class LoginQrCodeBo {

    @ApiModelProperty("扫码地址")
    private String url;

    @ApiModelProperty("设备id")
    private String deviceId;

    @ApiModelProperty("ticket")
    private String ticket;

    public LoginQrCodeBo() {
    }

    public LoginQrCodeBo(String url, String deviceId, String ticket) {
        this.url = url;
        this.deviceId = deviceId;
        this.ticket = ticket;
    }
}
