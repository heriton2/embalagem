package com.lojajogos.embalagem.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoResponseDTO {
  private int pedido_id;
  private List<CaixaDTO> caixas;
}
