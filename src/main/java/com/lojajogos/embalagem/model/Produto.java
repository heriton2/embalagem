package com.lojajogos.embalagem.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Produto {
    private String id;
    private Dimensao dimensoes;
}