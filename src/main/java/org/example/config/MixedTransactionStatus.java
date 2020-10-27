package org.example.config;

import lombok.Getter;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Getter
public class MixedTransactionStatus implements TransactionStatus {

    private final List<TransactionStatus> statuses;

    public MixedTransactionStatus(List<TransactionStatus> statuses) {
        this.statuses = statuses;
    }

    protected <R> List<R> call(Function<TransactionStatus, R> handler) {
        List<R> rs = new ArrayList<>();
        for (TransactionStatus status : statuses) {
            R r = handler.apply(status);
            rs.add(r);
        }
        return rs;
    }


    @Override
    public boolean hasSavepoint() {
        throw new IllegalArgumentException("未实现");
    }

    @Override
    public void flush() {
        call(new Function<TransactionStatus, Object>() {
            @Override
            public Object apply(TransactionStatus transactionStatus) {
                transactionStatus.flush();
                return null;
            }
        });
    }

    @Override
    public Object createSavepoint() throws TransactionException {
        throw new IllegalArgumentException("未实现");
    }

    @Override
    public void rollbackToSavepoint(Object savepoint) throws TransactionException {
        throw new IllegalArgumentException("未实现");
    }

    @Override
    public void releaseSavepoint(Object savepoint) throws TransactionException {
        throw new IllegalArgumentException("未实现");
    }

    @Override
    public boolean isNewTransaction() {
        List<Boolean> rs = call(new Function<TransactionStatus, Boolean>() {
            @Override
            public Boolean apply(TransactionStatus transactionStatus) {
                return transactionStatus.isNewTransaction();
            }
        });
        boolean result = true;
        for (Boolean r : rs) {
            if (r != null && !r) {
                result = false;
                break;
            }
        }
        return result;
    }

    @Override
    public void setRollbackOnly() {
        call(new Function<TransactionStatus, Object>() {
            @Override
            public Object apply(TransactionStatus transactionStatus) {
                transactionStatus.setRollbackOnly();
                return null;
            }
        });
    }

    @Override
    public boolean isRollbackOnly() {
        List<Boolean> rs = call(new Function<TransactionStatus, Boolean>() {
            @Override
            public Boolean apply(TransactionStatus transactionStatus) {
                return transactionStatus.isRollbackOnly();
            }
        });
        boolean result = true;
        for (Boolean r : rs) {
            if (r != null && !r) {
                result = false;
                break;
            }
        }
        return result;
    }

    @Override
    public boolean isCompleted() {
        List<Boolean> rs = call(new Function<TransactionStatus, Boolean>() {
            @Override
            public Boolean apply(TransactionStatus transactionStatus) {
                return transactionStatus.isCompleted();
            }
        });
        boolean result = true;
        for (Boolean r : rs) {
            if (r != null && !r) {
                result = false;
                break;
            }
        }
        return result;
    }
}
