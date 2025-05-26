package com.lojajogos.embalagem.service.impl;

import com.lojajogos.embalagem.dto.response.CaixaDTO;
import com.lojajogos.embalagem.dto.response.PedidoResponseDTO;
import com.lojajogos.embalagem.model.Caixa;
import com.lojajogos.embalagem.model.Dimensao;
import com.lojajogos.embalagem.model.Pedido;
import com.lojajogos.embalagem.model.Produto;
import com.lojajogos.embalagem.service.EmpacotamentoService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmpacotamentoServiceImpl implements EmpacotamentoService {

    private static final List<Caixa> TIPOS_CAIXAS = Arrays.asList(
            new Caixa("Caixa 1", new Dimensao(30, 40, 80)),
            new Caixa("Caixa 2", new Dimensao(80, 50, 40)),
            new Caixa("Caixa 3", new Dimensao(50, 80, 60))
    );

    @Override
    public PedidoResponseDTO processar(Pedido pedido) {
        List<Produto> produtosRestantes = new ArrayList<>(pedido.getProdutos());
        produtosRestantes.sort(Comparator.comparing((Produto p) -> p.getDimensoes().getVolume()).reversed());

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
            }
        }

        List<CaixaDTO> caixasDTO = caixasUtilizadas.stream()
                .map(this::converterParaCaixaDTO)
                .collect(Collectors.toList());

        return new PedidoResponseDTO(pedido.getId(), caixasDTO);
    }

    private List<Produto> encontrarMaiorGrupoQueCabe(List<Produto> produtos, Caixa caixa) {
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

        int somaAltura = grupo.stream().mapToInt(p -> p.getDimensoes().getAltura()).sum();
        int maxLargura = grupo.stream().mapToInt(p -> p.getDimensoes().getLargura()).max().orElse(0);
        int maxComprimento = grupo.stream().mapToInt(p -> p.getDimensoes().getComprimento()).max().orElse(0);

        int somaLargura = grupo.stream().mapToInt(p -> p.getDimensoes().getLargura()).sum();
        int maxAltura = grupo.stream().mapToInt(p -> p.getDimensoes().getAltura()).max().orElse(0);
        int maxComprimento2 = grupo.stream().mapToInt(p -> p.getDimensoes().getComprimento()).max().orElse(0);

        int somaComprimento = grupo.stream().mapToInt(p -> p.getDimensoes().getComprimento()).sum();
        int maxAltura2 = grupo.stream().mapToInt(p -> p.getDimensoes().getAltura()).max().orElse(0);
        int maxLargura2 = grupo.stream().mapToInt(p -> p.getDimensoes().getLargura()).max().orElse(0);

        return (
                (somaAltura <= caixa.getDimensoes().getAltura() &&
                        maxLargura <= caixa.getDimensoes().getLargura() &&
                        maxComprimento <= caixa.getDimensoes().getComprimento()) ||
                        (somaLargura <= caixa.getDimensoes().getLargura() &&
                                maxAltura <= caixa.getDimensoes().getAltura() &&
                                maxComprimento2 <= caixa.getDimensoes().getComprimento()) ||
                        (somaComprimento <= caixa.getDimensoes().getComprimento() &&
                                maxAltura2 <= caixa.getDimensoes().getAltura() &&
                                maxLargura2 <= caixa.getDimensoes().getLargura())
        );
    }

    private List<List<Produto>> combinacoes(List<Produto> produtos, int k) {
        List<List<Produto>> resultado = new ArrayList<>();
        combinacoesHelper(produtos, k, 0, new ArrayList<>(), resultado);
        return resultado;
    }

    private void combinacoesHelper(List<Produto> produtos, int k, int start, List<Produto> atual, List<List<Produto>> resultado) {
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
        List<String> produtosIds = caixa.getProdutos().stream()
                .map(Produto::getId)
                .collect(Collectors.toList());

        String observacao = null;
        if (caixa.getId() == null) {
            observacao = "Produto não cabe em nenhuma caixa disponível.";
        }

        return new CaixaDTO(caixa.getId(), produtosIds, observacao);
    }
}