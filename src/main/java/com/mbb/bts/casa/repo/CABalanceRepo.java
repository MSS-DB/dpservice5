package com.mbb.bts.casa.repo;

import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;

import com.mbb.bts.casa.model.CABalance;

public interface CABalanceRepo extends JpaRepository<CABalance, String>{

	List<CABalance> findAllByAccountNumberInAndWorkDateBetweenOrderByWorkDateDesc(List<String> accountNumberList, String startDate, String endDate);
	
	List<CABalance> findAllByAccountNumberInAndWorkDateBetweenAndBtsIsSundayTrueOrderByWorkDateDesc(List<String> accountNumberList, String startDate, String endDate);

	List<CABalance> findAllByAccountNumberInAndWorkDateBetweenAndBtsIsEOMonthTrueOrderByWorkDateDesc(List<String> accountNumberList, String startDate, String endDate);

}
