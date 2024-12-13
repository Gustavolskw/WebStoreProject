package ms.com.alurafood.pagamentos.controller;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import ms.com.alurafood.pagamentos.dto.PagamentoDto;
import ms.com.alurafood.pagamentos.response.ApiResponse;
import ms.com.alurafood.pagamentos.service.PagamentoService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("${api.prefix}/pagamentos")
@RequiredArgsConstructor
public class PagamentoController {

    private final PagamentoService pagamentoService;

    private final RabbitTemplate rabbitTemplate;


    @GetMapping
    public ResponseEntity<ApiResponse> listar(@PageableDefault(size = 10) Pageable pageable) {
        try{
            Page<PagamentoDto> pagamentoPaginado = pagamentoService.obterTodosPagamnetos(pageable);

            Map<String, Object> data = new HashMap<>();
            data.put("pagamentos", pagamentoPaginado.getContent());
            data.put("total", pagamentoPaginado.getTotalElements());
            data.put("current_page", pagamentoPaginado.getNumber() + 1);
            data.put("last_page", pagamentoPaginado.getTotalPages());
            data.put("per_page", pagamentoPaginado.getSize());

            return ResponseEntity.ok().body(new ApiResponse("Sucesso!", data));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), null));
        }

    }


    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> listar(@PathVariable @NotNull Long id){
        try{
            return ResponseEntity.ok().body(new ApiResponse("Sucesso!", pagamentoService.obterPorId(id)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), null));
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deletar(@PathVariable @NotNull Long id){
        try{
            pagamentoService.excluirPagamento(id);
            return ResponseEntity.ok().body(new ApiResponse("Pagamento excluido com sucesso!", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse> salvar(@RequestBody @Valid PagamentoDto pagamentoDto, UriComponentsBuilder uriBuilder){
        try{
            PagamentoDto pagamentoDtoNew = pagamentoService.criarPagamento(pagamentoDto);
            URI endereco  = uriBuilder.path("/pagamentos/{id}").buildAndExpand(pagamentoDtoNew.getId()).toUri();

            rabbitTemplate.convertAndSend("PAGAMENTOS_EXCHANGE", "", pagamentoDtoNew);
            return ResponseEntity.created(endereco).body(new ApiResponse("Pagamento criado com sucesso!", pagamentoDtoNew));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> atualizar(@PathVariable @NotNull Long id, @RequestBody  @Valid PagamentoDto pagamentoDto){
        try{
            return ResponseEntity.ok().body(new ApiResponse("Pagamento atualizado com sucesso!", pagamentoService.atualizarPagamento(id, pagamentoDto)));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage(), null));
        }
    }


    @PatchMapping("/{id}/confirmar")
    @CircuitBreaker(name = "atualizaPedido", fallbackMethod = "pagamentoAutorizadoComIntegracaoPendente")
    public void confirmarPagamento(@PathVariable @NotNull Long id){
        pagamentoService.confirmarPagamento(id);
    }

    public void pagamentoAutorizadoComIntegracaoPendente(Long id, Exception e){
        pagamentoService.alteraStatus(id);
    }
}
