package com.lojajogos.embalagem.controller;

import com.lojajogos.embalagem.dto.request.PedidoDTO;
import com.lojajogos.embalagem.dto.response.ResponseDTO;
import com.lojajogos.embalagem.service.impl.EmbalagensServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/embalagens")
@Tag(name = "Embalagens API", description = "API para otimização de embalagens para pedidos")
public class EmbalagemController {

  private final EmbalagensServiceImpl embalagensServiceImpl;

  @Autowired
  public EmbalagemController(EmbalagensServiceImpl embalagensServiceImpl) {
    this.embalagensServiceImpl = embalagensServiceImpl;
  }

  @PostMapping
  @Operation(
      summary = "Otimizar embalagens para pedidos",
      description =
          "Recebe uma lista de pedidos com produtos e suas dimensões e retorna as caixas que devem ser usadas",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Pedidos processados com sucesso"),
        @ApiResponse(responseCode = "400", description = "Requisição inválida"),
        @ApiResponse(responseCode = "401", description = "Não autorizado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
      })
  public ResponseEntity<ResponseDTO> otimizarEmbalagens(
      @RequestBody Map<String, List<PedidoDTO>> request) {
    List<PedidoDTO> pedidos = request.get("pedidos");
    ResponseDTO response = embalagensServiceImpl.processarPedidos(pedidos);
    return ResponseEntity.ok(response);
  }
}
