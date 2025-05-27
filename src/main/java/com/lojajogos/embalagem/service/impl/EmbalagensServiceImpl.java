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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmbalagensServiceImpl implements EmbalagensService {
  private static final Logger log = LoggerFactory.getLogger(EmbalagensServiceImpl.class);
  private final EmpacotamentoService empacotamentoService;

  @Autowired
  public EmbalagensServiceImpl(EmpacotamentoService empacotamentoService) {
    this.empacotamentoService = empacotamentoService;
  }

  @Override
  public ResponseDTO processarPedidos(List<PedidoDTO> pedidosDTO) {
    log.info(
        "Iniciando processamento para {} pedido(s)", pedidosDTO != null ? pedidosDTO.size() : 0);
    List<PedidoResponseDTO> responses =
        pedidosDTO.stream().map(this::processarPedido).collect(Collectors.toList());
    log.info("Processamento de pedidos concluído.");
    return new ResponseDTO(responses);
  }

  private PedidoResponseDTO processarPedido(PedidoDTO pedidoDTO) {
    log.debug("Processando pedido ID: {}", pedidoDTO.getPedido_id());
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
