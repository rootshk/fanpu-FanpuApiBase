package tech.fanpu.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import tech.fanpu.po.SendTemplate;

import java.util.Date;
import java.util.List;

public interface SendTemplateRepository extends JpaRepository<SendTemplate, Integer> {
    List<SendTemplate> findByUserIdAndStateAndCreatedAtGreaterThan(Long userId, Integer state, Date date, Pageable pageable);
}
