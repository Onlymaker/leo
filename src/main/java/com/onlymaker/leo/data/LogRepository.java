package com.onlymaker.leo.data;

import org.springframework.data.repository.CrudRepository;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by jibo on 2016/10/19.
 */
public interface LogRepository extends CrudRepository<Log, Long> {
    long countByEntry(Entry entry);
    Log findFirstByEntryOrderByIdDesc(Entry entry);
    List<Log> findFirst2ByEntryOrderByIdDesc(Entry entry);
    Iterable<Log> findAllByEntryAndCreateTimeAfterOrderByIdDesc(Entry entry, Timestamp createTime);
}
