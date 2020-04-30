package tech.fanpu.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.fanpu.business.UserBusiness;
import tech.fanpu.common.util.response.ResultBean;
import tech.fanpu.common.util.response.ServiceCode;
import tech.fanpu.vo.UserStarCallBackVo;

/**
 * @Title 回调接口
 * @Package tech.fanpu.controller.open
 * @Author porridge
 * @Date 2018/10/16 9:37 PM
 * @Version V1.0
 */
@Slf4j
@Api("业务回调")
@RestController
@RequestMapping("/basis/open")
public class OpenApi {

    @Autowired
    private UserBusiness userBusiness;

    @ApiOperation(value = "用户关注回调")
    @PostMapping("/wechat/callback")
    public ResultBean wxCallBack(UserStarCallBackVo vo) throws Exception {
        log.info("收到公众号关注的回调: vo:{}", vo);
        userBusiness.callback(vo);
        return new ResultBean(ServiceCode.OK, ServiceCode.OK_DEFAULT_MSG);
    }

}
