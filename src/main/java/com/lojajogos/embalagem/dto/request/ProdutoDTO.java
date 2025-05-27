package com.lojajogos.embalagem.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProdutoDTO {
  private String produto_id;
  private DimensaoDTO dimensoes;
}
