package com.lojajogos.embalagem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoResponseDTO {
    private int pedido_id;
    private List<CaixaDTO> caixas;
}