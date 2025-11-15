package com.fintrack.repository;

import com.fintrack.entity.PortfolioPosition;
import com.fintrack.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PortfolioPositionRepository extends JpaRepository<PortfolioPosition, Long> {
    
    List<PortfolioPosition> findByUserOrderByCurrentValueDesc(User user);
    
    List<PortfolioPosition> findByUserOrderBySymbolAsc(User user);
    
    Optional<PortfolioPosition> findByUserAndSymbol(User user, String symbol);
    
    List<PortfolioPosition> findByUserIdOrderByCurrentValueDesc(Long userId);
    
    void deleteByUserAndSymbol(User user, String symbol);
}
