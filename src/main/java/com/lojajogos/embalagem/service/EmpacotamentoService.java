package com.lojajogos.embalagem.service;

import com.lojajogos.embalagem.dto.response.PedidoResponseDTO;
import com.lojajogos.embalagem.model.Pedido;

public interface EmpacotamentoService {
    PedidoResponseDTO processar(Pedido pedido);
}
