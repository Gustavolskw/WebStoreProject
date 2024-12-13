package ms.com.alurafood.pagamentos.http;


import ms.com.alurafood.pagamentos.model.Pedido;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@FeignClient(name = "pedidos-ms-t", url = "http://localhost:8082/pedidos-ms-t")
public interface PedidoClient {

    @RequestMapping(method = RequestMethod.PUT, value = "/pedidos/{id}/pago")
    public void atializaPagamento(@PathVariable Long id);


    @RequestMapping(method = RequestMethod.GET, value = "/pedidos/{id}")
    Pedido buscaItensDoPedido(@PathVariable Long id);
}