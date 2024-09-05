package com.fiap.techchallenge5.infrastructure.pagamento.repository;

import com.fiap.techchallenge5.infrastructure.pagamento.model.PagamentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PagamentoRepository extends JpaRepository<PagamentoEntity, Long> {

}
