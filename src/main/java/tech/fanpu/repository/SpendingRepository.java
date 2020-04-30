package tech.fanpu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.fanpu.po.Spending;

public interface SpendingRepository extends JpaRepository<Spending, Integer> {
    Long countByBusinessTypeAndDataIdAndStat(String businessType, String dataId, Integer stt);
}
