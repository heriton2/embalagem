package com.lojajogos.embalagem.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Dimensao {
  private int altura;
  private int largura;
  private int comprimento;

  public int getVolume() {
    return altura * largura * comprimento;
  }
}
