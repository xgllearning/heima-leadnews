package com.heima.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.dtos.LoginDto;
import com.heima.model.user.pojos.ApUser;
import com.heima.user.mapper.ApUserMapper;
import com.heima.user.service.ApUserService;
import com.heima.utils.common.AppJwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;


@Service
@Transactional
@Slf4j
public class ApUserServiceImpl extends ServiceImpl<ApUserMapper, ApUser> implements ApUserService {
    //实现了ApUserService接口,但是里面的方法想复用而不是去实现，则需要继承ServiceImpl
    /**
     * app端登录功能
     * @param dto
     * @return
     */
    //app端登录逻辑
    public ResponseResult login(LoginDto dto) {
        //1. 首先判断用户有没有输入用户名和密码, LoginDto如果都不为空, 则执行查询数据库操作，如果都为空，则进行游客登录
        if (StringUtils.isNotBlank(dto.getPhone()) && StringUtils.isNotBlank(dto.getPassword())) {
            //2. 都不为空, 根据手机号查询用户
            LambdaQueryWrapper<ApUser> queryWrapper = new LambdaQueryWrapper<>();
            ApUser user = getOne(queryWrapper.eq(ApUser::getPhone, dto.getPhone()));
            //3. 判断用户是否为null, 为null说明查询失败
            if (user == null) {
                return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST, "用户不存在");
            }
            //4. 用户存在, 解析密码(获取盐, 盐 + 输入的密码 ? 数据库密码)
            String salt = user.getSalt();
            String pwd = dto.getPassword();//用户输入密码
            pwd = DigestUtils.md5DigestAsHex((pwd + salt).getBytes());
            if (!StringUtils.equals(pwd, user.getPassword())) {
                //5. 用户输入的密码与数据库密码不一致
                return ResponseResult.errorResult(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR, "密码错误");
            }
            //6. 返回json数据 + jwt-- token, 携带不敏感信息
            HashMap<String, Object> map = new HashMap<>();
            user.setPassword("");
            user.setSalt("");
            map.put("user", user);
            String token = AppJwtUtil.getToken(user.getId().longValue());
            map.put("token", token);
            return ResponseResult.okResult(map);
        } else {
            //此时为游客登录, 返回id为0的token
            HashMap<String, Object> map = new HashMap<>();
            String token = AppJwtUtil.getToken(0l);
            map.put("token", token);
            return ResponseResult.okResult(map);
        }
    }
}
