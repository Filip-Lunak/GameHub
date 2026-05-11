package com.example.GameHub.dto;

import lombok.Data;

@Data
public class CartDTO {
    private Long idCart;
    private Long idGame;
    private String gameName;
    private Double gamePrice;
}
