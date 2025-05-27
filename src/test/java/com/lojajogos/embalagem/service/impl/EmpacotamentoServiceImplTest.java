package com.lojajogos.embalagem.service.impl;

import com.lojajogos.embalagem.dto.response.CaixaDTO;
import com.lojajogos.embalagem.dto.response.PedidoResponseDTO;
import com.lojajogos.embalagem.model.Dimensao;
import com.lojajogos.embalagem.model.Pedido;
import com.lojajogos.embalagem.model.Produto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmpacotamentoServiceImplTest {

    private EmpacotamentoServiceImpl empacotamentoService;

    @BeforeEach
    void setUp() {
        empacotamentoService = new EmpacotamentoServiceImpl();
    }

    @Test
    @DisplayName("Processar pedido com um produto que cabe na Caixa 1")
    void testProcessar_singleProductFitsCaixa1() {
        Produto produto1 = new Produto("ProdutoPequeno", new Dimensao(10, 10, 10));
        Pedido pedido = new Pedido(1, Collections.singletonList(produto1));

        PedidoResponseDTO response = empacotamentoService.processar(pedido);

        assertNotNull(response);
        assertEquals(1, response.getPedido_id());
        assertEquals(1, response.getCaixas().size());
        CaixaDTO caixa = response.getCaixas().get(0);
        assertEquals("Caixa 1", caixa.getCaixa_id());
        assertEquals(1, caixa.getProdutos().size());
        assertTrue(caixa.getProdutos().contains("ProdutoPequeno"));
        assertNull(caixa.getObservacao());
    }

    @Test
    @DisplayName("Processar pedido com dois produtos que cabem juntos na Caixa 1 (Mouse e Teclado - Pedido 4)")
    void testProcessar_twoProductsFitInCaixa1_Pedido4Scenario() {
        Produto mouse = new Produto("Mouse Gamer", new Dimensao(5, 8, 12));
        Produto teclado = new Produto("Teclado Mecânico", new Dimensao(4, 45, 15));
        Pedido pedido = new Pedido(4, Arrays.asList(mouse, teclado));

        PedidoResponseDTO response = empacotamentoService.processar(pedido);

        assertNotNull(response);
        assertEquals(4, response.getPedido_id());
        assertEquals(1, response.getCaixas().size());
        CaixaDTO caixa = response.getCaixas().get(0);
        assertEquals("Caixa 1", caixa.getCaixa_id());
        assertEquals(Arrays.asList("Teclado Mecânico", "Mouse Gamer"), caixa.getProdutos());
        assertNull(caixa.getObservacao());
    }

    @Test
    @DisplayName("Processar pedido com três produtos pequenos que cabem juntos na Caixa 1")
    void testProcessar_threeSmallProductsFitInCaixa1() {
        Produto p1 = new Produto("P1", new Dimensao(10, 10, 10));
        Produto p2 = new Produto("P2", new Dimensao(5, 5, 5));
        Produto p3 = new Produto("P3", new Dimensao(4, 4, 4));
        Pedido pedido = new Pedido(10, Arrays.asList(p3, p1, p2));

        PedidoResponseDTO response = empacotamentoService.processar(pedido);

        assertNotNull(response);
        assertEquals(1, response.getCaixas().size());
        CaixaDTO caixa = response.getCaixas().get(0);
        assertEquals("Caixa 1", caixa.getCaixa_id());
        assertEquals(Arrays.asList("P1", "P2", "P3"), caixa.getProdutos());
        assertNull(caixa.getObservacao());
    }


    @Test
    @DisplayName("Processar pedido com um produto que cabe na Caixa 2 mas não na 1")
    void testProcessar_singleProductFitsCaixa2_NotCaixa1() {
        Produto produtoMedio = new Produto("ProdutoMedio", new Dimensao(31, 31, 31));
        Pedido pedido = new Pedido(2, Collections.singletonList(produtoMedio));

        PedidoResponseDTO response = empacotamentoService.processar(pedido);

        assertNotNull(response);
        assertEquals(1, response.getCaixas().size());
        CaixaDTO caixa = response.getCaixas().get(0);
        assertEquals("Caixa 2", caixa.getCaixa_id());
        assertTrue(caixa.getProdutos().contains("ProdutoMedio"));
        assertNull(caixa.getObservacao());
    }

    @Test
    @DisplayName("Processar pedido com um produto que cabe na Caixa 3 mas não na 1 ou 2")
    void testProcessar_singleProductFitsCaixa3_NotCaixa1Or2() {
        Produto produtoGrande = new Produto("ProdutoGrande", new Dimensao(51, 51, 41));
        Pedido pedido = new Pedido(3, Collections.singletonList(produtoGrande));

        PedidoResponseDTO response = empacotamentoService.processar(pedido);

        assertNotNull(response);
        assertEquals(1, response.getCaixas().size());
        CaixaDTO caixa = response.getCaixas().get(0);
        assertEquals("Caixa 3", caixa.getCaixa_id());
        assertTrue(caixa.getProdutos().contains("ProdutoGrande"));
        assertNull(caixa.getObservacao());
    }

    @Test
    @DisplayName("Processar múltiplos produtos onde alguns cabem juntos e um é muito grande")
    void testProcessar_mixedFitAndTooLarge_Corrected() {
        Produto p1 = new Produto("Pequeno1", new Dimensao(5,5,5));
        Produto pGigante = new Produto("GiganteIncaixavel", new Dimensao(200,200,200));
        Produto p2 = new Produto("Pequeno2", new Dimensao(6,6,6));

        Pedido pedido = new Pedido(9, Arrays.asList(p1, pGigante, p2));
        PedidoResponseDTO response = empacotamentoService.processar(pedido);

        assertNotNull(response);
        assertEquals(2, response.getCaixas().size(), "Deveria ter 2 caixas: 1 normal e 1 especial");

        Optional<CaixaDTO> normalCaixaOpt = response.getCaixas().stream()
                .filter(c -> "Caixa 1".equals(c.getCaixa_id()))
                .findFirst();
        assertTrue(normalCaixaOpt.isPresent(), "Deveria haver uma Caixa 1 para Pequeno1 e Pequeno2");
        CaixaDTO normalCaixa = normalCaixaOpt.get();
        assertEquals(Arrays.asList("Pequeno2", "Pequeno1"), normalCaixa.getProdutos());
        assertNull(normalCaixa.getObservacao());

        Optional<CaixaDTO> caixaEspecialOpt = response.getCaixas().stream()
                .filter(c -> c.getCaixa_id() == null)
                .findFirst();
        assertTrue(caixaEspecialOpt.isPresent(), "Deveria haver uma caixa especial para GiganteIncaixavel");
        CaixaDTO caixaEspecial = caixaEspecialOpt.get();
        assertEquals(1, caixaEspecial.getProdutos().size());
        assertTrue(caixaEspecial.getProdutos().contains("GiganteIncaixavel"));
        assertEquals("Produto não cabe em nenhuma caixa disponível.", caixaEspecial.getObservacao());
    }

    @Test
    @DisplayName("Processar Pedido 1 (PS5 e Volante) devem caber na Caixa 1")
    void testProcessar_pedido1_PS5AndVolante_FitInCaixa1() {
        Produto ps5 = new Produto("PS5", new Dimensao(40, 10, 25));
        Produto volante = new Produto("Volante", new Dimensao(40, 30, 30));
        Pedido pedido = new Pedido(1, Arrays.asList(ps5, volante));

        PedidoResponseDTO response = empacotamentoService.processar(pedido);

        assertNotNull(response);
        assertEquals(1, response.getPedido_id());
        assertEquals(1, response.getCaixas().size());

        CaixaDTO caixa = response.getCaixas().get(0);
        assertEquals("Caixa 1", caixa.getCaixa_id());
        assertEquals(Arrays.asList("Volante", "PS5"), caixa.getProdutos());
        assertNull(caixa.getObservacao());
    }

    @Test
    @DisplayName("Processar Pedido 6 (Monitor, Notebook, Webcam, Microfone) conforme lógica atual")
    void testProcessar_pedido6_ComplexScenario() {
        Produto monitor = new Produto("Monitor", new Dimensao(50, 60, 20));
        Produto notebook = new Produto("Notebook", new Dimensao(2, 35, 25));
        Produto webcam = new Produto("Webcam", new Dimensao(7, 10, 5));
        Produto microfone = new Produto("Microfone", new Dimensao(25, 10, 10));

        Pedido pedido = new Pedido(6, Arrays.asList(monitor, notebook, webcam, microfone));
        PedidoResponseDTO response = empacotamentoService.processar(pedido);

        assertNotNull(response);
        assertEquals(6, response.getPedido_id());
        assertEquals(2, response.getCaixas().size(), "Esperado 2 caixas para o Pedido 6");

        Optional<CaixaDTO> caixa1Opt = response.getCaixas().stream()
                .filter(c -> "Caixa 1".equals(c.getCaixa_id()))
                .findFirst();
        assertTrue(caixa1Opt.isPresent(), "Caixa 1 não encontrada para Pedido 6");
        CaixaDTO caixa1 = caixa1Opt.get();
        assertEquals(Arrays.asList("Microfone", "Notebook", "Webcam"), caixa1.getProdutos());
        assertNull(caixa1.getObservacao());

        Optional<CaixaDTO> caixa2Opt = response.getCaixas().stream()
                .filter(c -> "Caixa 2".equals(c.getCaixa_id()))
                .findFirst();
        assertTrue(caixa2Opt.isPresent(), "Caixa 2 não encontrada para Pedido 6");
        CaixaDTO caixa2 = caixa2Opt.get();
        assertEquals(Collections.singletonList("Monitor"), caixa2.getProdutos());
        assertNull(caixa2.getObservacao());
    }

    @Test
    @DisplayName("Processar pedido com produto que não cabe em nenhuma caixa")
    void testProcessar_productTooLargeForAllBoxes() {
        Produto produtoGigante = new Produto("ProdutoSuperGigante", new Dimensao(100, 100, 100));
        Pedido pedido = new Pedido(5, Collections.singletonList(produtoGigante));

        PedidoResponseDTO response = empacotamentoService.processar(pedido);

        assertNotNull(response);
        assertEquals(1, response.getCaixas().size());
        CaixaDTO caixa = response.getCaixas().get(0);
        assertNull(caixa.getCaixa_id());
        assertTrue(caixa.getProdutos().contains("ProdutoSuperGigante"));
        assertEquals("Produto não cabe em nenhuma caixa disponível.", caixa.getObservacao());
    }

    @Test
    @DisplayName("Processar pedido vazio deve retornar resposta vazia")
    void testProcessar_emptyPedido() {
        Pedido pedido = new Pedido(7, Collections.emptyList());
        PedidoResponseDTO response = empacotamentoService.processar(pedido);

        assertNotNull(response);
        assertEquals(7, response.getPedido_id());
        assertTrue(response.getCaixas().isEmpty());
    }
}