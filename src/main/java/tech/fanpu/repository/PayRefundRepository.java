package tech.fanpu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tech.fanpu.po.PayRefund;

public interface PayRefundRepository extends JpaRepository<PayRefund, Integer> {

    @Query("select sum(r.refundFee) from PayRefund r where orderNo=?1 and success=true")
    Long findRefundCount(String orderNo);
}
