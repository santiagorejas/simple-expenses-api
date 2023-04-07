package com.simpleexpenses.demo.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto {

    private long id;
    private String categoryId;
    private String userId;
    private String title;
    private String color;

}
