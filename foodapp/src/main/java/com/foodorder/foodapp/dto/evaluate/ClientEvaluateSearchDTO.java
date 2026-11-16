package com.foodorder.foodapp.dto.evaluate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@NoArgsConstructor 
@AllArgsConstructor 
public class ClientEvaluateSearchDTO {
    private Long productId; 
    
    private int page;

    private int perPage;
}