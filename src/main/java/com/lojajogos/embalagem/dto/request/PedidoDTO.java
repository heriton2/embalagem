package com.lojajogos.embalagem.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoDTO {
    private int pedido_id;
    private List<ProdutoDTO> produtos;
}