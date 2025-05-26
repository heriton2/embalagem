package com.lojajogos.embalagem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDTO {
    private List<PedidoResponseDTO> pedidos;
}