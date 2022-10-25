package com.areay.reggie.controller;

import com.areay.reggie.common.R;
import com.areay.reggie.dto.DishDto;
import com.areay.reggie.entity.Category;
import com.areay.reggie.entity.Dish;
import com.areay.reggie.entity.DishFlavor;
import com.areay.reggie.service.CategoryService;
import com.areay.reggie.service.DishFlavorService;
import com.areay.reggie.service.DishService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Base64Util;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜品管理
 */
@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());

        dishService.saveWithFlavor(dishDto);

        return R.success("新增菜品成功");
    }

    /**
     * 菜品信息分页
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
//        构造分页构造器
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage = new Page<>();


//        构造条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
//        添加过滤条件
        queryWrapper.like(name != null,Dish::getName,name);
//        添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);

//        执行分页查询
        dishService.page(pageInfo,queryWrapper);

//        对象拷贝
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");

        List<Dish> records = pageInfo.getRecords();

        List<DishDto> list = records.stream().map((item) ->{
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item,dishDto);

            Long categoryId = item.getCategoryId();  // 菜品分类id
//            根据id来查询分类对象
            Category category = categoryService.getById(categoryId);

            if (categoryId != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }

    /**
     * 根据id查询菜品信息和对应口味信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){

        DishDto dishDto = dishService.getByIdWithFlavor(id);

        return R.success(dishDto);
    }

    /**
     * 修改菜品
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());

        dishService.updateWithFlavor(dishDto);

        return R.success("新增菜品成功");
    }

    // 改变菜品的销售状态
    @PostMapping("/status/{status}")
    public R<String> updateSaleStatus(@PathVariable("status") Integer status,@RequestParam List<Long> ids){
        //  菜品具体的售卖状态 由前端修改并返回，该方法传入的status是 修改之后的售卖状态，可以直接根据一个或多个菜品id进行查询并修改售卖即可
        log.info("ids :"+ids);
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ids != null,Dish::getId,ids);


        List<Dish> list = dishService.list(queryWrapper);
        if (list != null){
            for (Dish dish : list) {
                dish.setStatus(status);
                dishService.updateById(dish);
            }
            return R.success("菜品的售卖状态已更改！");
        }
        return R.error("售卖状态不可更改,请联系管理员或客服！");

    }

    @DeleteMapping
    public R<String> batchDelete(@RequestParam("ids") List<Long> ids){
        dishService.batchDeleteByIds(ids);

        return R.success("成功删除菜品！");
    }

    /**
     * 根据条件来查询对应的菜品数据
     * @param dish
     * @return
     */
//    @GetMapping("/list")
//    public R<List<Dish>> list(Dish dish){
//
////        构造查询条件
//        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());
////        添加条件，查询状态为1（启售状态）
//        queryWrapper.eq(Dish::getStatus,1);
//
////        添加排序条件
//        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//
//        List<Dish> list = dishService.list(queryWrapper);
//
//        return R.success(list);
//    }

    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){

//        构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());
//        添加条件，查询状态为1（启售状态）
        queryWrapper.eq(Dish::getStatus,1);

//        添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);

        List<DishDto> dishDtoList = list.stream().map((item) ->{
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item,dishDto);

            Long categoryId = item.getCategoryId();  // 菜品分类id
//            根据id来查询分类对象
            Category category = categoryService.getById(categoryId);

            if (categoryId != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            //  当前菜品的id
            Long dishId = item.getId();

            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId,dishId);
            List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());



        return R.success(dishDtoList);
    }

}
