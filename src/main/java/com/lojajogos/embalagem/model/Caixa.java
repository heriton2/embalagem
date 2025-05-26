package com.lojajogos.embalagem.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class Caixa {
    private String id;
    private Dimensao dimensoes;
    private List<Produto> produtos;
    private int volumeDisponivel;

    public Caixa(String id, Dimensao dimensoes) {
        this.id = id;
        this.dimensoes = dimensoes;
        this.produtos = new ArrayList<>();
        this.volumeDisponivel = dimensoes.getVolume();
    }

    public boolean podeAdicionar(Produto produto) {
        // Verificar se o produto cabe na dimensão física da caixa
        Dimensao dimProduto = produto.getDimensoes();
        Dimensao dimCaixa = this.dimensoes;

        // Verificar todas as possíveis orientações do produto
        return (
                // Orientação 1: original
                (dimProduto.getAltura() <= dimCaixa.getAltura() &&
                        dimProduto.getLargura() <= dimCaixa.getLargura() &&
                        dimProduto.getComprimento() <= dimCaixa.getComprimento()) ||
                        // Orientação 2: rotação em X
                        (dimProduto.getAltura() <= dimCaixa.getAltura() &&
                                dimProduto.getLargura() <= dimCaixa.getComprimento() &&
                                dimProduto.getComprimento() <= dimCaixa.getLargura()) ||
                        // Orientação 3: rotação em Y
                        (dimProduto.getAltura() <= dimCaixa.getLargura() &&
                                dimProduto.getLargura() <= dimCaixa.getAltura() &&
                                dimProduto.getComprimento() <= dimCaixa.getComprimento()) ||
                        // Orientação 4: rotação em Y e X
                        (dimProduto.getAltura() <= dimCaixa.getLargura() &&
                                dimProduto.getLargura() <= dimCaixa.getComprimento() &&
                                dimProduto.getComprimento() <= dimCaixa.getAltura()) ||
                        // Orientação 5: rotação em Z
                        (dimProduto.getAltura() <= dimCaixa.getComprimento() &&
                                dimProduto.getLargura() <= dimCaixa.getAltura() &&
                                dimProduto.getComprimento() <= dimCaixa.getLargura()) ||
                        // Orientação 6: rotação em Z e X
                        (dimProduto.getAltura() <= dimCaixa.getComprimento() &&
                                dimProduto.getLargura() <= dimCaixa.getLargura() &&
                                dimProduto.getComprimento() <= dimCaixa.getAltura())
        );
    }

    public void adicionarProduto(Produto produto) {
        this.produtos.add(produto);
        this.volumeDisponivel -= produto.getDimensoes().getVolume();
    }
}