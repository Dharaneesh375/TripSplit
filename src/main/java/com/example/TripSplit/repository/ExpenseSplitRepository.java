package com.example.TripSplit.repository;

import com.example.TripSplit.entity.ExpenseSplit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseSplitRepository extends JpaRepository<ExpenseSplit, Long> {
    List<ExpenseSplit> findByExpenseTripId(Long tripId);
}
