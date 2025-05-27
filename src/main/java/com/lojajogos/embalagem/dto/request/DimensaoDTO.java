package com.lojajogos.embalagem.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DimensaoDTO {
  private int altura;
  private int largura;
  private int comprimento;
}
