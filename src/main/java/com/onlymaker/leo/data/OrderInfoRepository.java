package com.onlymaker.leo.data;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by jibo on 2016/11/21.
 */
public interface OrderInfoRepository extends CrudRepository<OrderInfo, Long> {
    long countByOrderId(String orderId);
    Page<OrderInfo> findAll(Pageable pageable);
}
