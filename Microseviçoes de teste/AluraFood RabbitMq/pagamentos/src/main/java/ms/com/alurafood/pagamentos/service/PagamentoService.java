package ms.com.alurafood.pagamentos.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import ms.com.alurafood.pagamentos.dto.PagamentoDto;
import ms.com.alurafood.pagamentos.http.PedidoClient;
import ms.com.alurafood.pagamentos.model.Pagamento;
import ms.com.alurafood.pagamentos.model.Status;
import ms.com.alurafood.pagamentos.repository.PagamentoRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;

    private final ModelMapper modelMapper;

   private final PedidoClient pedidoClient;



    public Page<PagamentoDto> obterTodosPagamnetos(Pageable paginacao){

        return pagamentoRepository
                .findAll(paginacao)
                .map(p->modelMapper.map(p,PagamentoDto.class));

    }

    public PagamentoDto obterPorId(Long id){
        Pagamento pagamento  =  Optional.of(id)
                .filter(pagamentoB -> pagamentoRepository.existsById(id))
                .map(pagamentoR-> pagamentoRepository.getById(id)).orElseThrow(EntityNotFoundException::new);

        PagamentoDto dto  = modelMapper.map(pagamento,PagamentoDto.class);
        dto.setItens(pedidoClient.buscaItensDoPedido(pagamento.getPedidoId()).getItens());
        return dto;
    }

    @Transactional
    public PagamentoDto criarPagamento(PagamentoDto pagamento) {
        Pagamento pagamentoSalvo = modelMapper.map(pagamento,Pagamento.class);
        pagamentoSalvo.setStatus(Status.CRIADO);
        pagamentoRepository.save(pagamentoSalvo);
        return  modelMapper.map(pagamentoSalvo,PagamentoDto.class);
    }

    @Transactional
    public PagamentoDto atualizarPagamento(Long id, PagamentoDto dto) {
        Pagamento pagamento = modelMapper.map(dto, Pagamento.class);
        pagamento.setId(id);
        pagamento = pagamentoRepository.save(pagamento);
        return modelMapper.map(pagamento, PagamentoDto.class);
    }

    @Transactional
    public void excluirPagamento(Long id) {
        pagamentoRepository.deleteById(id);
    }


    public void confirmarPagamento(Long id){
        Optional<Pagamento> pagamento = pagamentoRepository.findById(id);

        if (pagamento.isEmpty()) {
            throw new EntityNotFoundException();
        }

        pagamento.get().setStatus(Status.CONFIRMADO);
        pagamentoRepository.save(pagamento.get());
        pedidoClient.atializaPagamento(pagamento.get().getPedidoId());
    }

    public void alteraStatus(Long id) {
        Optional<Pagamento> pagamento = pagamentoRepository.findById(id);

        if (pagamento.isEmpty()) {
            throw new EntityNotFoundException();
        }

        pagamento.get().setStatus(Status.CONFIRMADO_SEM_INTEGRACAO);
        pagamentoRepository.save(pagamento.get());

    }




}
