package org.example.service;

import lombok.extern.slf4j.Slf4j;
import org.example.dao.DbMongoDao;
import org.example.dao.DbMySqlDao;
import org.example.domain.DbMongo;
import org.example.domain.DbMysql;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.Resource;
import java.util.UUID;

@Slf4j
@Service
public class TestService {

    @Resource
    private DbMySqlDao mySqlDao;

    @Resource
    private DbMongoDao mongoDao;

    @Transactional(transactionManager = "mixedTransactionManager",rollbackFor = Exception.class)
    public void doTest() {
        TransactionInterceptor t;
        MongoTransactionManager mongoTransactionManager;
        AtomikosDataSourceBean atomikosDataSourceBean;
        DataSourceTransactionManager sourceTransactionManager;


        String txName = TransactionSynchronizationManager.getCurrentTransactionName();
        log.info("txName {}", txName);

        DbMysql mysql1 = new DbMysql();
        mysql1.setF1(UUID.randomUUID().toString());
        mysql1.setF2(UUID.randomUUID().toString());
        mysql1 = mySqlDao.save(mysql1);

        DbMongo mongo1 = new DbMongo();
        mongo1.setF1(UUID.randomUUID().toString());
        mongo1.setF2(UUID.randomUUID().toString());
        mongoDao.save(mongo1);
//
        throw new IllegalArgumentException("单表事务");
    }

}
