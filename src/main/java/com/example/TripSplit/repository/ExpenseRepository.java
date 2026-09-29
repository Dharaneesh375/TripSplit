package com.example.TripSplit.repository;

import com.example.TripSplit.entity.Expense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByTripId(Long tripId);
    Page<Expense> findByTripId(Long tripId, Pageable pageable);
}
