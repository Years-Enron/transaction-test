package org.example.dao;

import org.example.domain.DbMysql;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DbMySqlDao extends CrudRepository<DbMysql,Long> {
}
