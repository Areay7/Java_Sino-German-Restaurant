package com.areay.reggie.service.impl;

import com.areay.reggie.common.CustomException;
import com.areay.reggie.common.MyCustomException;
import com.areay.reggie.dto.SetmealDto;
import com.areay.reggie.entity.Setmeal;
import com.areay.reggie.entity.SetmealDish;
import com.areay.reggie.mapper.SetmealMapper;
import com.areay.reggie.service.SetmealDishService;
import com.areay.reggie.service.SetmealService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     *
     * @param setmealDto
     */
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
//        保存套餐的基本信息，操作setmeal，执行insert操作
        this.save(setmealDto);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

//        保存套餐和菜品的关联信息，操作操作setmeal_dish,执行insert操作
        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 删除套餐，同时需要删除套餐和菜品的关联数据
     *
     * @param ids
     */
    @Transactional
    public void removeWithDish(List<Long> ids) {
//        查询套餐的状态，确定是否可以删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId, ids);
        queryWrapper.eq(Setmeal::getStatus, 1);

        int count = this.count(queryWrapper);
        if (count > 0) {
//        如果不能删除，抛出一个业务异常
            throw new CustomException("套餐正在售卖中，不能删除");
        }

//        如果可以删除，先删除套餐表中的数据--setmeal
        this.removeByIds(ids);

        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getSetmealId, ids);
//        再删除关系表中的数据--setmeal_dish
        setmealDishService.remove(lambdaQueryWrapper);

    }

    @Override
    public void batchDeleteByIds(List<Long> ids) {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ids != null, Setmeal::getId, ids);

        List<Setmeal> list = this.list(queryWrapper);
        if (list != null) {
            for (Setmeal setmeal : list) {
                if (setmeal.getStatus() == 0) {
                    this.removeById(setmeal.getId());
                } else {
                    throw new MyCustomException("有套餐正在售卖，不能删除全部套餐！");
                }
            }
        }
    }

    @Override
    public SetmealDto getSetmealData(Long id) {
        Setmeal setmeal = this.getById(id);
        SetmealDto setmealDto = new SetmealDto();

        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(id != null, SetmealDish::getSetmealId, id);


        if (setmeal != null) {
            BeanUtils.copyProperties(setmeal, setmealDto);

            List<SetmealDish> dishes = setmealDishService.list(queryWrapper);
            setmealDto.setSetmealDishes(dishes);

            return setmealDto;
        }
        return null;
    }
}
