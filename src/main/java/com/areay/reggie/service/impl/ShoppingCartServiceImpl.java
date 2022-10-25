package com.areay.reggie.service.impl;

import com.areay.reggie.entity.ShoppingCart;
import com.areay.reggie.mapper.ShoppingCartMapper;
import com.areay.reggie.service.ShoppingCartService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
