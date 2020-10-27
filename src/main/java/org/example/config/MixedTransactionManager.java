package org.example.config;

import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class MixedTransactionManager implements PlatformTransactionManager {

    @Lazy
    @Resource
    private List<PlatformTransactionManager> transactionManagers;

    @Override
    public TransactionStatus getTransaction(TransactionDefinition definition) throws TransactionException {
        List<TransactionStatus> statuses = call(new ResultHandler<PlatformTransactionManager, TransactionStatus>() {
            @Override
            public TransactionStatus apply(PlatformTransactionManager transactionManager) {
                return transactionManager.getTransaction(definition);
            }
        });
        return new MixedTransactionStatus(statuses);
    }

    @Override
    public void commit(TransactionStatus status) throws TransactionException {
        MixedTransactionStatus mixedStatus = (MixedTransactionStatus) status;
        List<TransactionStatus> realStatuses = mixedStatus.getStatuses();
        call(new IndexHandler<PlatformTransactionManager>() {
            @Override
            public void apply(int index, PlatformTransactionManager transactionManager) {
                TransactionStatus realStatus = realStatuses.get(index);
                transactionManager.commit(realStatus);
                initSynchronization();
            }
        });
        TransactionSynchronizationManager.clear();
    }

    @Override
    public void rollback(TransactionStatus status) throws TransactionException {
        MixedTransactionStatus mixedStatus = (MixedTransactionStatus) status;
        List<TransactionStatus> realStatuses = mixedStatus.getStatuses();
        call(new IndexHandler<PlatformTransactionManager>() {
            @Override
            public void apply(int index, PlatformTransactionManager transactionManager) {
                TransactionStatus realStatus = realStatuses.get(index);
                transactionManager.rollback(realStatus);
                initSynchronization();
            }
        });
        TransactionSynchronizationManager.clear();
    }

    public interface Handler<T, R> {
    }

    public interface VoidHandler<T> extends Handler<T, Object> {
        void apply(T t);
    }

    public interface ResultHandler<T, R> extends Handler<T, R> {
        R apply(T t);
    }

    public interface IndexHandler<T> extends Handler<T, Object> {
        void apply(int index, T t);
    }

    public <R> List<R> call(Handler<PlatformTransactionManager, R> handler) {
        List<R> rs = new ArrayList<>();
        for (int i = 0; i < transactionManagers.size(); i++) {
            PlatformTransactionManager transactionManager = transactionManagers.get(i);
            if (transactionManager == this) {
                continue;
            }
            R r = null;
            if (handler instanceof VoidHandler) {
                ((VoidHandler<PlatformTransactionManager>) handler).apply(transactionManager);
            } else if (handler instanceof ResultHandler) {
                r = ((ResultHandler<PlatformTransactionManager, R>) handler).apply(transactionManager);
            } else if (handler instanceof IndexHandler) {
                ((IndexHandler<PlatformTransactionManager>) handler).apply(i, transactionManager);
            }
            rs.add(r);
        }
        return rs;
    }

    @SuppressWarnings("unchecked")
    protected void initSynchronization() {
        Field field = ReflectionUtils.findField(TransactionSynchronizationManager.class, "synchronizations");
        assert field != null;
        field.setAccessible(true);
        try {
            ThreadLocal<Set<TransactionSynchronization>> synchronizations = (ThreadLocal<Set<TransactionSynchronization>>) field.get(null);
            synchronizations.set(new LinkedHashSet<>());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
