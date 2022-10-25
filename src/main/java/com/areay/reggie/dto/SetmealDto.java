package com.areay.reggie.dto;

import com.areay.reggie.entity.Setmeal;
import com.areay.reggie.entity.SetmealDish;
import lombok.Data;

import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
