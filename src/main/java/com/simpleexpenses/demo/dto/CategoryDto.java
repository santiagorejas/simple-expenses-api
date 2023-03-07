package com.simpleexpenses.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryDto {

    private long id;
    private String categoryId;
    private String userId;
    private String title;
    private String color;

}
