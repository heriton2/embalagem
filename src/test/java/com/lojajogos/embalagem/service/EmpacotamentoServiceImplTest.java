package com.lojajogos.embalagem.service;

import com.lojajogos.embalagem.dto.response.CaixaDTO;
import com.lojajogos.embalagem.dto.response.PedidoResponseDTO;
import com.lojajogos.embalagem.model.Dimensao;
import com.lojajogos.embalagem.model.Pedido;
import com.lojajogos.embalagem.model.Produto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class EmpacotamentoServiceImplTest {

    @Autowired
    private EmpacotamentoService empacotamentoService;

    private Produto headset;
    private Produto cadeiraGamer;
    private Produto ps5;
    private Produto volante;
    private Produto webcam;
    private Produto microfone;
    private Produto monitor;
    private Produto notebook;

    @BeforeEach
    public void setup() {
        headset = new Produto("Headset", new Dimensao(25, 15, 20));
        cadeiraGamer = new Produto("Cadeira Gamer", new Dimensao(120, 60, 70));
        ps5 = new Produto("PS5", new Dimensao(40, 10, 25));
        volante = new Produto("Volante", new Dimensao(40, 30, 30));
        webcam = new Produto("Webcam", new Dimensao(7, 10, 5));
        microfone = new Produto("Microfone", new Dimensao(25, 10, 10));
        monitor = new Produto("Monitor", new Dimensao(50, 60, 20));
        notebook = new Produto("Notebook", new Dimensao(2, 35, 25));
    }

    @Test
    @DisplayName("Deve embalar um produto pequeno na Caixa 1")
    public void testProcessarPedidoComUmProdutoPequeno() {
        Pedido pedido = new Pedido(1, Arrays.asList(headset));

        PedidoResponseDTO response = empacotamentoService.processar(pedido);

        assertEquals(1, response.getPedido_id(), "ID do pedido deve ser preservado");
        assertEquals(1, response.getCaixas().size(), "Deve ter apenas uma caixa");

        CaixaDTO caixa = response.getCaixas().get(0);
        assertEquals("Caixa 1", caixa.getCaixa_id(), "Produto pequeno deve usar Caixa 1");
        assertEquals(1, caixa.getProdutos().size(), "Deve conter apenas um produto");
        assertTrue(caixa.getProdutos().contains("Headset"), "Deve conter o produto Headset");
        assertNull(caixa.getObservacao(), "Não deve ter observações");
    }

    @Test
    @DisplayName("Deve identificar produto que não cabe em nenhuma caixa")
    public void testProcessarPedidoComProdutoGrande() {
        Pedido pedido = new Pedido(1, Arrays.asList(cadeiraGamer));

        PedidoResponseDTO response = empacotamentoService.processar(pedido);

        assertEquals(1, response.getPedido_id());
        assertEquals(1, response.getCaixas().size());

        CaixaDTO caixa = response.getCaixas().get(0);
        assertNull(caixa.getCaixa_id(), "Caixa para produto grande não deve ter ID");
        assertEquals(1, caixa.getProdutos().size());
        assertTrue(caixa.getProdutos().contains("Cadeira Gamer"));
        assertNotNull(caixa.getObservacao(), "Deve conter observação");
        assertEquals("Produto não cabe em nenhuma caixa disponível.", caixa.getObservacao());
    }

    @Test
    @DisplayName("Deve embalar múltiplos produtos na mesma caixa quando couberem")
    public void testProcessarPedidoComMultiplosProdutos() {
        Pedido pedido = new Pedido(1, Arrays.asList(ps5, volante));

        PedidoResponseDTO response = empacotamentoService.processar(pedido);

        assertEquals(1, response.getPedido_id());
        assertEquals(1, response.getCaixas().size(), "Deve usar apenas uma caixa para ambos produtos");

        CaixaDTO caixa = response.getCaixas().get(0);
        assertEquals("Caixa 1", caixa.getCaixa_id(), "Deve usar a Caixa 1 para os produtos combinados");

        assertTrue(caixa.getProdutos().containsAll(Arrays.asList("PS5", "Volante")),
                "Deve conter ambos os produtos");
        assertNull(caixa.getObservacao(), "Não deve ter observações");
    }

    @Test
    @DisplayName("Deve distribuir produtos em múltiplas caixas quando necessário")
    public void testProcessarPedidoComProdutosQueNecessitamMultiplasCaixas() {
        Pedido pedido = new Pedido(6, Arrays.asList(webcam, microfone, monitor, notebook));

        PedidoResponseDTO response = empacotamentoService.processar(pedido);

        assertEquals(6, response.getPedido_id(), "ID do pedido deve ser preservado");
        assertEquals(1, response.getCaixas().size(), "Deve usar exatamente uma caixa");

        CaixaDTO caixa = response.getCaixas().get(0);

        String expectedCaixaId = "Caixa 2";
        assertEquals(expectedCaixaId, caixa.getCaixa_id(), "Deve usar " + expectedCaixaId + " para todos os produtos");

        assertTrue(caixa.getProdutos().containsAll(Arrays.asList("Webcam", "Microfone", "Monitor", "Notebook")),
                "Caixa deve conter todos os produtos");
        assertNull(caixa.getObservacao(), "Não deve ter observações");
    }

    @Test
    @DisplayName("Deve processar pedido vazio corretamente")
    public void testProcessarPedidoVazio() {
        Pedido pedido = new Pedido(10, Arrays.asList());

        PedidoResponseDTO response = empacotamentoService.processar(pedido);

        assertEquals(10, response.getPedido_id());
        assertTrue(response.getCaixas().isEmpty(), "Pedido vazio não deve ter caixas");
    }

    @Test
    @DisplayName("Deve escolher a menor caixa possível para um produto")
    public void testEscolhaCaixaOtimizada() {
        Produto fone = new Produto("Fone de Ouvido", new Dimensao(5, 5, 2));
        Pedido pedido = new Pedido(11, Arrays.asList(fone));

        PedidoResponseDTO response = empacotamentoService.processar(pedido);

        assertEquals(1, response.getCaixas().size());
        CaixaDTO caixa = response.getCaixas().get(0);
        assertEquals("Caixa 1", caixa.getCaixa_id(), "Deve escolher a menor caixa possível");
    }
}