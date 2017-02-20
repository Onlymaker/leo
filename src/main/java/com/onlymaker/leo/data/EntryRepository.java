package com.onlymaker.leo.data;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by jibo on 2016/10/19.
 */
public interface EntryRepository extends CrudRepository<Entry, Long> {
    long countByStatus(int status);
    long countByStore(String store);
    Entry findFirstByAsin(String asin);
    Iterable<Entry> findByErrorLessThanEqual(int error);
    Page<Entry> findAll(Pageable pageable);
    Page<Entry> findByStatus(Integer status, Pageable pageable);
    Page<Entry> findByStore(String store, Pageable pageable);
}
