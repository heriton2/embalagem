package com.lojajogos.embalagem.dto.request;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoDTO {
  private int pedido_id;
  private List<ProdutoDTO> produtos;
}
