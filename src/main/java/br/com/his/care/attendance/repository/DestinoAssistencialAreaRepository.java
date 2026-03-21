package br.com.his.care.attendance.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.his.care.attendance.model.DestinoAssistencialArea;

public interface DestinoAssistencialAreaRepository extends JpaRepository<DestinoAssistencialArea, Long> {

    @Query("""
            select da
            from DestinoAssistencialArea da
            join fetch da.area a
            join fetch a.unidade u
            where da.destinoAssistencial.id = :destinoAssistencialId
            order by da.prioridade, a.descricao
            """)
    List<DestinoAssistencialArea> listarPorDestinoOrdenado(Long destinoAssistencialId);

    void deleteByDestinoAssistencialId(Long destinoAssistencialId);
}
