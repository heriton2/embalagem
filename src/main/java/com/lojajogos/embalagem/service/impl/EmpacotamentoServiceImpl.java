package com.lojajogos.embalagem.service.impl;

import com.lojajogos.embalagem.dto.response.CaixaDTO;
import com.lojajogos.embalagem.dto.response.PedidoResponseDTO;
import com.lojajogos.embalagem.model.Caixa;
import com.lojajogos.embalagem.model.Dimensao;
import com.lojajogos.embalagem.model.Pedido;
import com.lojajogos.embalagem.model.Produto;
import com.lojajogos.embalagem.service.EmpacotamentoService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmpacotamentoServiceImpl implements EmpacotamentoService {

  private static final Logger log = LoggerFactory.getLogger(EmpacotamentoServiceImpl.class);

  private static final List<Caixa> TIPOS_CAIXAS =
      Arrays.asList(
          new Caixa("Caixa 1", new Dimensao(30, 40, 80)),
          new Caixa("Caixa 2", new Dimensao(80, 50, 40)),
          new Caixa("Caixa 3", new Dimensao(50, 80, 60)));

  @Override
  public PedidoResponseDTO processar(Pedido pedido) {
    log.info(
        "Iniciando empacotamento para pedido ID: {}. Produtos: {}",
        pedido.getId(),
        pedido.getProdutos().size());
    List<Produto> produtosRestantes = new ArrayList<>(pedido.getProdutos());
    produtosRestantes.sort(
        Comparator.comparing((Produto p) -> p.getDimensoes().getVolume()).reversed());

    List<Caixa> caixasUtilizadas = new ArrayList<>();

    while (!produtosRestantes.isEmpty()) {
      boolean algumProdutoAlocado = false;

      for (Caixa tipoCaixa : TIPOS_CAIXAS) {
        List<Produto> melhorGrupo = encontrarMaiorGrupoQueCabe(produtosRestantes, tipoCaixa);

        if (!melhorGrupo.isEmpty()) {
          Caixa novaCaixa = new Caixa(tipoCaixa.getId(), tipoCaixa.getDimensoes());
          for (Produto p : melhorGrupo) {
            novaCaixa.adicionarProduto(p);
          }
          caixasUtilizadas.add(novaCaixa);
          produtosRestantes.removeAll(melhorGrupo);
          algumProdutoAlocado = true;
          break;
        }
      }

      if (!algumProdutoAlocado) {
        Produto produtoNaoEncaixa = produtosRestantes.get(0);
        Caixa caixaEspecial = new Caixa(null, new Dimensao(0, 0, 0));
        caixaEspecial.adicionarProduto(produtoNaoEncaixa);
        caixasUtilizadas.add(caixaEspecial);
        produtosRestantes.remove(0);
        log.warn(
            "Produto {} (Pedido ID: {}) não coube em nenhuma caixa padrão. Será colocado em caixa especial.",
            produtoNaoEncaixa.getId(),
            pedido.getId());
      }
    }

    List<CaixaDTO> caixasDTO =
        caixasUtilizadas.stream().map(this::converterParaCaixaDTO).collect(Collectors.toList());

    log.info(
        "Empacotamento concluído para pedido ID: {}. Caixas utilizadas: {}",
        pedido.getId(),
        caixasUtilizadas.size());
    return new PedidoResponseDTO(pedido.getId(), caixasDTO);
  }

  private List<Produto> encontrarMaiorGrupoQueCabe(List<Produto> produtos, Caixa caixa) {
    log.debug(
        "Tentando encontrar maior grupo de {} produtos para a caixa {}",
        produtos.size(),
        caixa.getId());
    int n = produtos.size();
    for (int k = n; k >= 1; k--) {
      List<List<Produto>> combinacoes = combinacoes(produtos, k);
      for (List<Produto> grupo : combinacoes) {
        if (grupoCabeNaCaixa(grupo, caixa)) {
          return grupo;
        }
      }
    }
    return Collections.emptyList();
  }

  private boolean grupoCabeNaCaixa(List<Produto> grupo, Caixa caixa) {
    Dimensao dimCaixa = caixa.getDimensoes();

    List<List<Dimensao>> combinacoesDeOrientacoesDoGrupo =
        gerarCombinacoesDeOrientacoesParaGrupo(grupo);

    for (List<Dimensao> umaCombinacaoDeOrientacoes : combinacoesDeOrientacoesDoGrupo) {

      int sumH = 0;
      int maxW_for_sumH = 0;
      int maxL_for_sumH = 0;
      for (Dimensao dimProdutoOrientado : umaCombinacaoDeOrientacoes) {
        sumH += dimProdutoOrientado.getAltura();
        maxW_for_sumH = Math.max(maxW_for_sumH, dimProdutoOrientado.getLargura());
        maxL_for_sumH = Math.max(maxL_for_sumH, dimProdutoOrientado.getComprimento());
      }
      if (sumH <= dimCaixa.getAltura()
          && maxW_for_sumH <= dimCaixa.getLargura()
          && maxL_for_sumH <= dimCaixa.getComprimento()) {
        return true;
      }

      int maxH_for_sumW = 0;
      int sumW = 0;
      int maxL_for_sumW = 0;
      for (Dimensao dimProdutoOrientado : umaCombinacaoDeOrientacoes) {
        maxH_for_sumW = Math.max(maxH_for_sumW, dimProdutoOrientado.getAltura());
        sumW += dimProdutoOrientado.getLargura();
        maxL_for_sumW = Math.max(maxL_for_sumW, dimProdutoOrientado.getComprimento());
      }
      if (maxH_for_sumW <= dimCaixa.getAltura()
          && sumW <= dimCaixa.getLargura()
          && maxL_for_sumW <= dimCaixa.getComprimento()) {
        return true;
      }

      int maxH_for_sumL = 0;
      int maxW_for_sumL = 0;
      int sumL = 0;
      for (Dimensao dimProdutoOrientado : umaCombinacaoDeOrientacoes) {
        maxH_for_sumL = Math.max(maxH_for_sumL, dimProdutoOrientado.getAltura());
        maxW_for_sumL = Math.max(maxW_for_sumL, dimProdutoOrientado.getLargura());
        sumL += dimProdutoOrientado.getComprimento();
      }
      if (maxH_for_sumL <= dimCaixa.getAltura()
          && maxW_for_sumL <= dimCaixa.getLargura()
          && sumL <= dimCaixa.getComprimento()) {
        return true;
      }
    }

    return false;
  }

  private List<List<Dimensao>> gerarCombinacoesDeOrientacoesParaGrupo(List<Produto> grupo) {
    List<List<Dimensao>> rotacoesPorProduto = new ArrayList<>();

    for (Produto produto : grupo) {
      Dimensao dim = produto.getDimensoes();
      List<Dimensao> rotacoes =
          Arrays.asList(
              new Dimensao(dim.getAltura(), dim.getLargura(), dim.getComprimento()),
              new Dimensao(dim.getAltura(), dim.getComprimento(), dim.getLargura()),
              new Dimensao(dim.getLargura(), dim.getAltura(), dim.getComprimento()),
              new Dimensao(dim.getLargura(), dim.getComprimento(), dim.getAltura()),
              new Dimensao(dim.getComprimento(), dim.getAltura(), dim.getLargura()),
              new Dimensao(dim.getComprimento(), dim.getLargura(), dim.getAltura()));
      rotacoesPorProduto.add(rotacoes);
    }

    return combinarOrientacoes(rotacoesPorProduto, 0, new ArrayList<>(), new ArrayList<>());
  }

  private List<List<Dimensao>> combinarOrientacoes(
      List<List<Dimensao>> rotacoesDisponiveisPorProduto,
      int indexProdutoAtual,
      List<Dimensao> combinacaoAtual,
      List<List<Dimensao>> todasCombinacoes) {
    if (indexProdutoAtual == rotacoesDisponiveisPorProduto.size()) {
      todasCombinacoes.add(new ArrayList<>(combinacaoAtual));
      return todasCombinacoes;
    }

    for (Dimensao rotacao : rotacoesDisponiveisPorProduto.get(indexProdutoAtual)) {
      combinacaoAtual.add(rotacao);
      combinarOrientacoes(
          rotacoesDisponiveisPorProduto, indexProdutoAtual + 1, combinacaoAtual, todasCombinacoes);
      combinacaoAtual.remove(combinacaoAtual.size() - 1);
    }

    return todasCombinacoes;
  }

  private List<List<Produto>> combinacoes(List<Produto> produtos, int k) {
    List<List<Produto>> resultado = new ArrayList<>();
    combinacoesHelper(produtos, k, 0, new ArrayList<>(), resultado);
    return resultado;
  }

  private void combinacoesHelper(
      List<Produto> produtos,
      int k,
      int start,
      List<Produto> atual,
      List<List<Produto>> resultado) {
    if (atual.size() == k) {
      resultado.add(new ArrayList<>(atual));
      return;
    }
    for (int i = start; i < produtos.size(); i++) {
      atual.add(produtos.get(i));
      combinacoesHelper(produtos, k, i + 1, atual, resultado);
      atual.remove(atual.size() - 1);
    }
  }

  private CaixaDTO converterParaCaixaDTO(Caixa caixa) {
    List<String> produtosIds =
        caixa.getProdutos().stream().map(Produto::getId).collect(Collectors.toList());

    String observacao = null;
    if (caixa.getId() == null) {
      observacao = "Produto não cabe em nenhuma caixa disponível.";
    }

    return new CaixaDTO(caixa.getId(), produtosIds, observacao);
  }
}
