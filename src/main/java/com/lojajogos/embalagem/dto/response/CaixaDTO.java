package com.lojajogos.embalagem.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CaixaDTO {
  private String caixa_id;
  private List<String> produtos;
  private String observacao;
}
