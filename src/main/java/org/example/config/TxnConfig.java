package org.example.config;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;

@Configuration
public class TxnConfig {

    @Bean("mysqlTransactionManager")
    public JpaTransactionManager transactionManager(
            ObjectProvider<TransactionManagerCustomizers> transactionManagerCustomizers) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManagerCustomizers.ifAvailable((customizers) -> customizers.customize(transactionManager));
        return transactionManager;
    }

    @Bean("mongoTransactionManager")
    MongoTransactionManager transactionManager(MongoDatabaseFactory factory) {
        return new MongoTransactionManager(factory);
    }

    @Bean("mixedTransactionManager")
    public MixedTransactionManager transactionManager() {
        return new MixedTransactionManager();
    }

}
