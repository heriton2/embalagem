package com.lojajogos.embalagem.service;

import com.lojajogos.embalagem.dto.request.PedidoDTO;
import com.lojajogos.embalagem.dto.response.ResponseDTO;

import java.util.List;

public interface EmbalagensService {
    ResponseDTO processarPedidos(List<PedidoDTO> pedidosDTO);
}
