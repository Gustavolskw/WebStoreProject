package ms.com.alurafood.pagamentos.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import ms.com.alurafood.pagamentos.model.ItemDoPedido;
import ms.com.alurafood.pagamentos.model.Status;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class  PagamentoDto {

    private Long id;
    private BigDecimal valor;
    private String nome;
    private String numero;
    private String expiracao;
    private String codigo;
    private Status status;
    private Long formaDePagamento;
    private  Long pedidoId;
    private List<ItemDoPedido> itens;
}
