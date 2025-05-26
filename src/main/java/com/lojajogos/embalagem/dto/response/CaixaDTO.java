package com.lojajogos.embalagem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CaixaDTO {
    private String caixa_id;
    private List<String> produtos;
    private String observacao;
}