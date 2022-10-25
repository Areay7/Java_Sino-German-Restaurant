package com.areay.reggie.service.impl;

import com.areay.reggie.entity.AddressBook;
import com.areay.reggie.mapper.AddressBookMapper;
import com.areay.reggie.service.AddressBookService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
