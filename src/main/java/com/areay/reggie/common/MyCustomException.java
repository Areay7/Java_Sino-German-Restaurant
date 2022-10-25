package com.areay.reggie.common;

//  自定义异常
public class MyCustomException extends RuntimeException{

    public MyCustomException(String message) {
        super(message);
    }

}