package com.lojajogos.embalagem.service.impl;

import com.lojajogos.embalagem.dto.request.PedidoDTO;
import com.lojajogos.embalagem.dto.request.ProdutoDTO;
import com.lojajogos.embalagem.dto.response.PedidoResponseDTO;
import com.lojajogos.embalagem.dto.response.ResponseDTO;
import com.lojajogos.embalagem.model.Dimensao;
import com.lojajogos.embalagem.model.Pedido;
import com.lojajogos.embalagem.model.Produto;
import com.lojajogos.embalagem.service.EmbalagensService;
import com.lojajogos.embalagem.service.EmpacotamentoService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmbalagensServiceImpl implements EmbalagensService {

  private final EmpacotamentoService empacotamentoService;

  @Autowired
  public EmbalagensServiceImpl(EmpacotamentoService empacotamentoService) {
    this.empacotamentoService = empacotamentoService;
  }

  @Override
  public ResponseDTO processarPedidos(List<PedidoDTO> pedidosDTO) {
    List<PedidoResponseDTO> responses =
        pedidosDTO.stream().map(this::processarPedido).collect(Collectors.toList());

    return new ResponseDTO(responses);
  }

  private PedidoResponseDTO processarPedido(PedidoDTO pedidoDTO) {
    List<Produto> produtos =
        pedidoDTO.getProdutos().stream().map(this::convertToEntity).collect(Collectors.toList());

    Pedido pedido = new Pedido(pedidoDTO.getPedido_id(), produtos);
    return empacotamentoService.processar(pedido);
  }

  private Produto convertToEntity(ProdutoDTO dto) {
    Dimensao dimensao =
        new Dimensao(
            dto.getDimensoes().getAltura(),
            dto.getDimensoes().getLargura(),
            dto.getDimensoes().getComprimento());

    return new Produto(dto.getProduto_id(), dimensao);
  }
}
