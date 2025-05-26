package com.lojajogos.embalagem.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {
    private int id;
    private List<Produto> produtos;
}