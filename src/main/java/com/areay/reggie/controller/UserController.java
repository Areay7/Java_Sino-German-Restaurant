package com.areay.reggie.controller;


import com.areay.reggie.common.R;
import com.areay.reggie.entity.User;
import com.areay.reggie.service.UserService;
import com.areay.reggie.utils.SMSUtils;
import com.areay.reggie.utils.ValidateCodeUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 发送手机短信验证码
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    private R<String> sendMsg(@RequestBody User user, HttpSession session){
//        获取手机号
        String phone = user.getPhone();

        if (StringUtils.isNotEmpty(phone)){
//        生成随机6位验证码
            String code = ValidateCodeUtils.generateValidateCode(6).toString();
            log.info("code={}",code);

//        调用阿里云提供的短信服务API完成发送短信业务
//            SMSUtils.sendMessage("阿里云短信测试","SMS_154950909",phone,code);

//        需要将生成的验证码保存到Session
            session.setAttribute(phone,code);

            return R.success("手机验证码短信发送成功");
        }

        return R.error("短信发送失败");
    }

    /**
     * 移动端用户登录
     * @param map
     * @return
     */
    @PostMapping("/login")
    private R<User> login(@RequestBody Map map, HttpSession session){

        log.info(map.toString());

//        获取手机号
        String phone = map.get("phone").toString();

//        获取验证码
        String code = map.get("code").toString();

//        从Session中获取保存的验证码
        Object codeInSession = session.getAttribute(phone);

//        进行验证码的比对（页面提交的验证码和Session中保存的验证码比对）
        if (codeInSession != null && codeInSession.equals(code)){
//        如果比对成功，说明登录成功

            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);

            User user = userService.getOne(queryWrapper);
            if (user == null){
//        判断当前手机号对应的用户是否为新用户，如果为新用户自动完成注册
            user = new User();
            user.setPhone(phone);
            user.setStatus(1);
            userService.save(user);
            }
            session.setAttribute("user",user.getId());
            return R.success(user);
        }
        return R.error("登录失败");
    }

}
