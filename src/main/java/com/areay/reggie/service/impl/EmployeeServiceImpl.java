package com.areay.reggie.service.impl;

import com.areay.reggie.entity.Employee;
import com.areay.reggie.mapper.EmployeeMapper;
import com.areay.reggie.service.EmployeeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
}
