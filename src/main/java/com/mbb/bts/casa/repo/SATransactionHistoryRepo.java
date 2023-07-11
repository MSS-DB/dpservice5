package com.mbb.bts.casa.repo;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.mbb.bts.casa.model.SATransactionHistory;

public interface SATransactionHistoryRepo extends JpaRepository<SATransactionHistory, String> {

	List<SATransactionHistory> findAllByAccountNumberInAndTransactionDateTimeBetween(String accountNumber,
			String startDate, String endDate, Pageable pageable);

	List<SATransactionHistory> findAllByAccountNumberInAndTransactionDateTimeBetweenAndTransactionIndicator(
			String accountNumber, String startDate, String endDate, String type, Pageable pageable);
}
