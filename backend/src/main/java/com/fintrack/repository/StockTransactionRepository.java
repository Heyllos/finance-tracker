package com.fintrack.repository;

import com.fintrack.entity.StockTransaction;
import com.fintrack.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockTransactionRepository extends JpaRepository<StockTransaction, Long> {
    
    List<StockTransaction> findByUserOrderByTransactionDateDesc(User user);
    
    List<StockTransaction> findByUserAndSymbolOrderByTransactionDateDesc(User user, String symbol);
    
    List<StockTransaction> findByUserIdOrderByTransactionDateDesc(Long userId);
}
