package com.lojajogos.embalagem.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.lojajogos.embalagem.dto.request.DimensaoDTO;
import com.lojajogos.embalagem.dto.request.PedidoDTO;
import com.lojajogos.embalagem.dto.request.ProdutoDTO;
import com.lojajogos.embalagem.dto.response.CaixaDTO;
import com.lojajogos.embalagem.dto.response.PedidoResponseDTO;
import com.lojajogos.embalagem.dto.response.ResponseDTO;
import com.lojajogos.embalagem.model.Pedido;
import com.lojajogos.embalagem.model.Produto;
import com.lojajogos.embalagem.service.EmpacotamentoService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class EmbalagensServiceImplTest {

  @Mock private EmpacotamentoService empacotamentoServiceMock;

  @InjectMocks private EmbalagensServiceImpl embalagensService;

  private PedidoDTO pedidoDTO1;
  private PedidoDTO pedidoDTO2;
  private ProdutoDTO produtoDTO1;
  private ProdutoDTO produtoDTO2;
  private PedidoResponseDTO pedidoResponseDTO1;
  private PedidoResponseDTO pedidoResponseDTO2;

  @BeforeEach
  void setUp() {
    DimensaoDTO dimensaoDTO1 = new DimensaoDTO(10, 10, 10);
    produtoDTO1 = new ProdutoDTO("produto1", dimensaoDTO1);
    pedidoDTO1 = new PedidoDTO(1, Collections.singletonList(produtoDTO1));

    DimensaoDTO dimensaoDTO2 = new DimensaoDTO(20, 20, 20);
    produtoDTO2 = new ProdutoDTO("produto2", dimensaoDTO2);
    pedidoDTO2 = new PedidoDTO(2, Arrays.asList(produtoDTO1, produtoDTO2));

    CaixaDTO caixaDTO1 = new CaixaDTO("Caixa1", Collections.singletonList("produto1"), null);
    pedidoResponseDTO1 = new PedidoResponseDTO(1, Collections.singletonList(caixaDTO1));

    CaixaDTO caixaDTO2 = new CaixaDTO("Caixa2", Arrays.asList("produto1", "produto2"), null);
    pedidoResponseDTO2 = new PedidoResponseDTO(2, Collections.singletonList(caixaDTO2));
  }

  @Test
  @DisplayName("processarPedidos deve retornar ResponseDTO vazio para lista de pedidos vazia")
  void testProcessarPedidos_emptyList() {
    ResponseDTO response = embalagensService.processarPedidos(Collections.emptyList());

    assertNotNull(response);
    assertTrue(response.getPedidos().isEmpty());
    verifyNoInteractions(empacotamentoServiceMock);
  }

  @Test
  @DisplayName("processarPedidos deve processar um único pedido corretamente")
  void testProcessarPedidos_singlePedido() {
    when(empacotamentoServiceMock.processar(any(Pedido.class))).thenReturn(pedidoResponseDTO1);

    List<PedidoDTO> pedidosDTO = Collections.singletonList(pedidoDTO1);
    ResponseDTO response = embalagensService.processarPedidos(pedidosDTO);

    assertNotNull(response);
    assertEquals(1, response.getPedidos().size());
    assertEquals(pedidoResponseDTO1, response.getPedidos().get(0));

    ArgumentCaptor<Pedido> pedidoCaptor = ArgumentCaptor.forClass(Pedido.class);
    verify(empacotamentoServiceMock, times(1)).processar(pedidoCaptor.capture());

    Pedido capturedPedido = pedidoCaptor.getValue();
    assertEquals(pedidoDTO1.getPedido_id(), capturedPedido.getId());
    assertEquals(1, capturedPedido.getProdutos().size());
    Produto capturedProduto = capturedPedido.getProdutos().get(0);
    assertEquals(produtoDTO1.getProduto_id(), capturedProduto.getId());
    assertEquals(
        produtoDTO1.getDimensoes().getAltura(), capturedProduto.getDimensoes().getAltura());
    assertEquals(
        produtoDTO1.getDimensoes().getLargura(), capturedProduto.getDimensoes().getLargura());
    assertEquals(
        produtoDTO1.getDimensoes().getComprimento(),
        capturedProduto.getDimensoes().getComprimento());
  }

  @Test
  @DisplayName("processarPedidos deve processar múltiplos pedidos corretamente")
  void testProcessarPedidos_multiplePedidos() {
    when(empacotamentoServiceMock.processar(any(Pedido.class)))
        .thenReturn(pedidoResponseDTO1)
        .thenReturn(pedidoResponseDTO2);

    List<PedidoDTO> pedidosDTO = Arrays.asList(pedidoDTO1, pedidoDTO2);
    ResponseDTO response = embalagensService.processarPedidos(pedidosDTO);

    assertNotNull(response);
    assertEquals(2, response.getPedidos().size());
    assertTrue(response.getPedidos().contains(pedidoResponseDTO1));
    assertTrue(response.getPedidos().contains(pedidoResponseDTO2));

    ArgumentCaptor<Pedido> pedidoCaptor = ArgumentCaptor.forClass(Pedido.class);
    verify(empacotamentoServiceMock, times(2)).processar(pedidoCaptor.capture());

    List<Pedido> capturedPedidos = pedidoCaptor.getAllValues();

    Pedido capturedPedido1 = capturedPedidos.get(0);
    assertEquals(pedidoDTO1.getPedido_id(), capturedPedido1.getId());
    assertEquals(1, capturedPedido1.getProdutos().size());
    assertEquals(produtoDTO1.getProduto_id(), capturedPedido1.getProdutos().get(0).getId());

    Pedido capturedPedido2 = capturedPedidos.get(1);
    assertEquals(pedidoDTO2.getPedido_id(), capturedPedido2.getId());
    assertEquals(2, capturedPedido2.getProdutos().size());
    assertEquals(
        produtoDTO1.getProduto_id(),
        capturedPedido2.getProdutos().get(0).getId()); // First product in pedidoDTO2
    assertEquals(
        produtoDTO2.getProduto_id(),
        capturedPedido2.getProdutos().get(1).getId()); // Second product in pedidoDTO2
  }

  @Test
  @DisplayName("processarPedido deve converter ProdutoDTO para Produto corretamente")
  void testProcessarPedido_produtoConversion() {

    DimensaoDTO dimensaoComplexaDTO = new DimensaoDTO(5, 15, 25);
    ProdutoDTO produtoComplexoDTO = new ProdutoDTO("produtoComplexo", dimensaoComplexaDTO);
    PedidoDTO pedidoComplexoDTO = new PedidoDTO(99, Collections.singletonList(produtoComplexoDTO));

    PedidoResponseDTO mockResponse = new PedidoResponseDTO(99, new ArrayList<>());
    when(empacotamentoServiceMock.processar(any(Pedido.class))).thenReturn(mockResponse);

    embalagensService.processarPedidos(Collections.singletonList(pedidoComplexoDTO));

    ArgumentCaptor<Pedido> pedidoCaptor = ArgumentCaptor.forClass(Pedido.class);
    verify(empacotamentoServiceMock).processar(pedidoCaptor.capture());

    Pedido capturedPedido = pedidoCaptor.getValue();
    assertNotNull(capturedPedido.getProdutos());
    assertEquals(1, capturedPedido.getProdutos().size());

    Produto capturedProduto = capturedPedido.getProdutos().get(0);
    assertEquals("produtoComplexo", capturedProduto.getId());
    assertNotNull(capturedProduto.getDimensoes());
    assertEquals(5, capturedProduto.getDimensoes().getAltura());
    assertEquals(15, capturedProduto.getDimensoes().getLargura());
    assertEquals(25, capturedProduto.getDimensoes().getComprimento());
  }

  @Test
  @DisplayName("processarPedidos deve lidar com PedidoDTO contendo lista de produtos vazia")
  void testProcessarPedidos_pedidoWithEmptyProductList() {
    PedidoDTO pedidoSemProdutosDTO = new PedidoDTO(3, Collections.emptyList());
    PedidoResponseDTO pedidoSemProdutosResponseDTO =
        new PedidoResponseDTO(3, Collections.emptyList());

    when(empacotamentoServiceMock.processar(any(Pedido.class)))
        .thenReturn(pedidoSemProdutosResponseDTO);

    List<PedidoDTO> pedidosDTO = Collections.singletonList(pedidoSemProdutosDTO);
    ResponseDTO response = embalagensService.processarPedidos(pedidosDTO);

    assertNotNull(response);
    assertEquals(1, response.getPedidos().size());
    assertEquals(pedidoSemProdutosResponseDTO, response.getPedidos().get(0));

    ArgumentCaptor<Pedido> pedidoCaptor = ArgumentCaptor.forClass(Pedido.class);
    verify(empacotamentoServiceMock, times(1)).processar(pedidoCaptor.capture());

    Pedido capturedPedido = pedidoCaptor.getValue();
    assertEquals(pedidoSemProdutosDTO.getPedido_id(), capturedPedido.getId());
    assertTrue(capturedPedido.getProdutos().isEmpty());
  }
}
