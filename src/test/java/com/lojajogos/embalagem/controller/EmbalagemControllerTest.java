package com.lojajogos.embalagem.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.lojajogos.embalagem.dto.request.DimensaoDTO;
import com.lojajogos.embalagem.dto.request.PedidoDTO;
import com.lojajogos.embalagem.dto.request.ProdutoDTO;
import com.lojajogos.embalagem.dto.response.CaixaDTO;
import com.lojajogos.embalagem.dto.response.PedidoResponseDTO;
import com.lojajogos.embalagem.dto.response.ResponseDTO;
import com.lojajogos.embalagem.service.impl.EmbalagensServiceImpl;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class EmbalagemControllerTest {

  @Mock private EmbalagensServiceImpl embalagensServiceMock;

  @InjectMocks private EmbalagemController embalagemController;

  private PedidoDTO pedidoDTO1;
  private ResponseDTO mockResponseDTO;

  @BeforeEach
  void setUp() {
    DimensaoDTO dimensaoDTO = new DimensaoDTO(10, 10, 10);
    ProdutoDTO produtoDTO = new ProdutoDTO("ProdutoX", dimensaoDTO);
    pedidoDTO1 = new PedidoDTO(1, Collections.singletonList(produtoDTO));

    CaixaDTO caixaResponseDTO =
        new CaixaDTO("Caixa 1", Collections.singletonList("ProdutoX"), null);
    PedidoResponseDTO pedidoResponseDTO =
        new PedidoResponseDTO(1, Collections.singletonList(caixaResponseDTO));
    mockResponseDTO = new ResponseDTO(Collections.singletonList(pedidoResponseDTO));
  }

  @Test
  @DisplayName("processarPedidos deve retornar 200 OK com ResponseDTO para requisição válida")
  void processarPedidos_validRequest_shouldReturnOkWithResponse() {
    List<PedidoDTO> pedidosList = Collections.singletonList(pedidoDTO1);
    Map<String, List<PedidoDTO>> requestBody = new HashMap<>();
    requestBody.put("pedidos", pedidosList);

    when(embalagensServiceMock.processarPedidos(pedidosList)).thenReturn(mockResponseDTO);

    ResponseEntity<ResponseDTO> responseEntity =
        embalagemController.otimizarEmbalagens(requestBody);

    assertNotNull(responseEntity);
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(mockResponseDTO, responseEntity.getBody());
    verify(embalagensServiceMock).processarPedidos(pedidosList);
  }

  @Test
  @DisplayName(
      "processarPedidos deve retornar 200 OK com ResponseDTO para lista de pedidos vazia na requisição")
  void processarPedidos_emptyPedidosListInRequest_shouldReturnOkWithPotentiallyEmptyResponse() {
    List<PedidoDTO> emptyPedidosList = new ArrayList<>();
    Map<String, List<PedidoDTO>> requestBody = new HashMap<>();
    requestBody.put("pedidos", emptyPedidosList);

    ResponseDTO emptyServiceResponseDTO =
        new ResponseDTO(new ArrayList<>()); // Service handles empty list
    when(embalagensServiceMock.processarPedidos(emptyPedidosList))
        .thenReturn(emptyServiceResponseDTO);

    ResponseEntity<ResponseDTO> responseEntity =
        embalagemController.otimizarEmbalagens(requestBody);

    assertNotNull(responseEntity);
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(emptyServiceResponseDTO, responseEntity.getBody());
    verify(embalagensServiceMock).processarPedidos(emptyPedidosList);
  }

  @Test
  @DisplayName(
      "processarPedidos deve retornar 200 OK e delegar tratamento de chave 'pedidos' ausente ao serviço")
  void processarPedidos_missingPedidosKey_shouldDelegateNullToService() {
    Map<String, List<PedidoDTO>> requestBody = new HashMap<>(); // "pedidos" key is missing

    ResponseDTO serviceResponseForNull = new ResponseDTO(new ArrayList<>());
    when(embalagensServiceMock.processarPedidos(null)).thenReturn(serviceResponseForNull);

    ResponseEntity<ResponseDTO> responseEntity =
        embalagemController.otimizarEmbalagens(requestBody);

    assertNotNull(responseEntity);
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(serviceResponseForNull, responseEntity.getBody());
    verify(embalagensServiceMock).processarPedidos(null);
  }
}
