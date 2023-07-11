package com.mbb.bts.casa.repo;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mbb.bts.casa.model.CasaSummaryModel;
import com.mbb.bts.casa.view.View_AccountSummary;

public interface CasaSummaryRepo extends JpaRepository<CasaSummaryModel, Long> {

	List<CasaSummaryModel> findByAccountNumberIn(List<String> accountNumbers);

	@Query("SELECT new com.mbb.bts.casa.view.View_AccountSummary( s.btsMonthStartDate, s.accountNumber, "
			+ "s.openBalance, s.debitAmount, s.debitCount, s.creditAmount, s.creditCount)  FROM CasaSummaryModel s "
			+ "WHERE s.accountNumber IN (:accountNumbers) AND s.btsMonthStartDate >= DATEADD(month, -6, getdate()) "
			+ "ORDER BY s.accountNumber, s.btsMonthStartDate DESC")
	List<View_AccountSummary> findCasaSummaryByDefaultMonth(@Param("accountNumbers") List<String> accountNumbers);

	@Query("SELECT new com.mbb.bts.casa.view.View_AccountSummary( s.btsMonthStartDate, s.accountNumber, "
			+ "s.openBalance, s.debitAmount, s.debitCount, s.creditAmount, s.creditCount)  FROM CasaSummaryModel s "
			+ "WHERE s.accountNumber IN (:accountNumbers) "
			+ "AND (:startBtsStartMonthDateString is not null AND s.btsMonthStartDate >= :startBtsStartMonthDateString)"
			+ "AND (:endBtsStartMonthDateString is not null AND s.btsMonthStartDate <= :endBtsStartMonthDateString)"
			+ "ORDER BY s.accountNumber, s.btsMonthStartDate DESC")
	List<View_AccountSummary> findCasaSummary(@Param("accountNumbers") List<String> accountNumbers,
			@Param("startBtsStartMonthDateString") Date startBtsStartMonthDateString,
			@Param("endBtsStartMonthDateString") Date endBtsStartMonthDateString);

	@Query("SELECT new com.mbb.bts.casa.view.View_AccountSummary( s.btsMonthStartDate, s.accountNumber, "
			+ "s.openBalance, s.debitAmount, s.debitCount, s.creditAmount, s.creditCount)  FROM CasaSummaryModel s "
			+ "WHERE s.accountNumber IN (:accountNumbers) "
			+ "AND (:startBtsStartMonthDateString is not null AND s.btsMonthStartDate >= :startBtsStartMonthDateString)"
			+ "ORDER BY s.accountNumber, s.btsMonthStartDate DESC")
	List<View_AccountSummary> findCasaSummaryByStartDate(@Param("accountNumbers") List<String> accountNumbers,
			@Param("startBtsStartMonthDateString") Date startBtsStartMonthDateString);

	@Query("SELECT new com.mbb.bts.casa.view.View_AccountSummary( s.btsMonthStartDate, s.accountNumber, "
			+ "s.openBalance, s.debitAmount, s.debitCount, s.creditAmount, s.creditCount)  FROM CasaSummaryModel s "
			+ "WHERE s.accountNumber IN (:accountNumbers) "
			+ "AND (:endBtsStartMonthDateString is not null AND s.btsMonthStartDate <= :endBtsStartMonthDateString)"
			+ "ORDER BY s.accountNumber, s.btsMonthStartDate DESC")
	List<View_AccountSummary> findCasaSummaryByEndDate(@Param("accountNumbers") List<String> accountNumbers,
			@Param("endBtsStartMonthDateString") Date endBtsStartMonthDateString);

}