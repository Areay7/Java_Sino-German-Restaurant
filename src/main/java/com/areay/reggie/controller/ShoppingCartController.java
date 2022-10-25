package com.areay.reggie.controller;

import com.areay.reggie.common.BaseContext;
import com.areay.reggie.common.R;
import com.areay.reggie.entity.ShoppingCart;
import com.areay.reggie.service.ShoppingCartService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 购物车
 */
@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        log.info("购物车数据:{}",shoppingCart);

//        设置用户id，指定是哪个用户的购物车数据
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);

//        查询当前菜品或者套餐，是否在购物车中
        Long dishId = shoppingCart.getDishId();

        LambdaQueryWrapper<ShoppingCart> queryWrapperuer = new LambdaQueryWrapper<>();
        queryWrapperuer.eq(ShoppingCart::getUserId,currentId);
        if (dishId != null){
//            说明添加到购物车到是菜品
            queryWrapperuer.eq(ShoppingCart::getDishId, shoppingCart.getDishId());

        }else {
//            添加到购物车到是套餐
            queryWrapperuer.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());

        }
        ShoppingCart cartServiceOne = shoppingCartService.getOne(queryWrapperuer);

        if (cartServiceOne != null){
//        如果已经存在，在原来数量基础上+1
            Integer number = cartServiceOne.getNumber();
            cartServiceOne.setNumber(number + 1);
            shoppingCartService.updateById(cartServiceOne);
        }else {
//        如果不存在，则添加到购物车，默认数量为1
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            cartServiceOne = shoppingCart;
        }

        return R.success(cartServiceOne);
    }

    // 在购物车中删减订单
    @PostMapping("/sub")
    public R<String> subToCart(@RequestBody ShoppingCart shoppingCart){
        log.info("购物车中的数据:{}"+shoppingCart.toString());

        shoppingCart.setUserId(BaseContext.getCurrentId());

        // 查询当前菜品或套餐是否 在购物车中
        Long dishId = shoppingCart.getDishId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());  // 根据登录用户的 userId去ShoppingCart表中查询该用户的购物车数据

        if (dishId != null){ // 添加进购物车的是菜品，且 购物车中已经添加过 该菜品
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
        }else {
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }


        ShoppingCart oneCart = shoppingCartService.getOne(queryWrapper);
        //  如果购物车中 已经存在该菜品或套餐，其数量+1，不存在，就将该购物车数据保存到数据库中
        if (oneCart != null){
            Integer number = oneCart.getNumber();
            if (number != 0){
                oneCart.setNumber(number - 1);
                shoppingCartService.updateById(oneCart);
            }else {
                shoppingCartService.remove(queryWrapper);
            }

        }
        return R.success("成功删减订单!");
    }

    /**
     * 查看购物车
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list (){
        log.info("查看购物车...");

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);

        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);

        return R.success(list);
    }

    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean(){
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());

        shoppingCartService.remove(queryWrapper);
        return R.success("清空购物车成功");
    }
}
