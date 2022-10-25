package com.areay.reggie.service.impl;


import com.areay.reggie.entity.OrderDetail;
import com.areay.reggie.mapper.OrderDetailMapper;
import com.areay.reggie.service.OrderDetailService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
