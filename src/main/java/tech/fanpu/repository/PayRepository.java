package tech.fanpu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tech.fanpu.po.Pay;

import java.util.List;

public interface PayRepository extends JpaRepository<Pay, Integer> {
    Pay findByOrderNo(String orderNo);

    List<Pay> findByPayStatAndBusinessType(Integer payStat, String businessType);

    @Query(value = "select sum(p.price) from Pay p where p.payStat=?1")
    Long sumByPayState(Integer state);

    @Query(value = "select sum(p.price) from Pay p where p.payStat=?1 and associated like ?2")
    Long sumByPayStateAndAssociatedLike(Integer state, String associated);

    List<Pay> findAllByPayStatAndServiceOrderNo(Integer payStat, String payNo);
}
