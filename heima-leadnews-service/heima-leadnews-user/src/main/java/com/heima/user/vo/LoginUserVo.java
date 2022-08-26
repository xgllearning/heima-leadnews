package com.heima.user.vo;

import com.heima.model.user.pojos.ApUser;
import lombok.Data;

@Data
public class LoginUserVo {
    //用于返回登录后的信息,密码和盐需要置为空
    private ApUser user;

    private String token;
}
