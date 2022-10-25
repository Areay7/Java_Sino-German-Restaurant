package com.areay.reggie.service.impl;

import com.areay.reggie.entity.User;
import com.areay.reggie.mapper.UserMapper;
import com.areay.reggie.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
