package com.mbb.bts.casa.repo;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.mbb.bts.casa.model.CATransactionHistory;

public interface CATransactionHistoryRepo extends JpaRepository<CATransactionHistory, String> {

	List<CATransactionHistory> findAllByAccountNumberAndTransactionDateBetweenAndTransactionTypeIn(String accountNumber,
			String startDate, String endDate, List<String> transactionTypes, Pageable pageable);
}
