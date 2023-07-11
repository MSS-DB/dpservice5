package com.mbb.bts.casa.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mbb.bts.casa.model.CACashFlow;

public interface CACashFlowRepo extends JpaRepository<CACashFlow, String> {

	@Query(value = "select max(bts_id) as bts_id,convert(numeric(15),wo_acct_number)as wo_acct_number,bts_transformed_wo_process_date as bts_cashflow_date,"
			+ "sum(case when wo_trans_type_ind='C' then bts_transformed_wo_amount else 0 end) as bts_transformed_cash_in,"
			+ "sum(case when wo_trans_type_ind='D' then bts_transformed_wo_amount else 0 end) as bts_transformed_cash_out "
			+ "from casa_history_ca ca_daily where convert(numeric(15),ca_daily.wo_acct_number) in :wo_acct_numbers and (ca_daily.bts_transformed_wo_process_date between :start_date and :end_date) "
			+ "group by ca_daily.wo_acct_number,ca_daily.bts_transformed_wo_process_date order by ca_daily.bts_transformed_wo_process_date desc", nativeQuery = true)
	List<CACashFlow> findDailyByAccountNumbers(@Param("wo_acct_numbers") List<String> accountNumbers,
			@Param("start_date") String startDate, @Param("end_date") String endDate);

	@Query(value = "select bts_id,convert(numeric(15),wo_acct_number)as wo_acct_number,bts_week_start_date as bts_cashflow_date,bts_transformed_cash_in,bts_transformed_cash_out "
			+ "from casa_cashflow_ca_weekly ca_weekly where convert(numeric(15),ca_weekly.wo_acct_number) in :wo_acct_numbers and "
			+ "(ca_weekly.bts_week_start_date between :start_date and :end_date) order by ca_weekly.bts_week_start_date desc", nativeQuery = true)
	List<CACashFlow> findWeeklyByAccountNumbers(@Param("wo_acct_numbers") List<String> accountNumbers,
			@Param("start_date") String startDate, @Param("end_date") String endDate);

	@Query(value = "select bts_id,convert(numeric(15),wo_acct_number)as wo_acct_number,bts_month_start_date as bts_cashflow_date,bts_transformed_cash_in,bts_transformed_cash_out "
			+ "from casa_cashflow_ca_monthly ca_monthly where convert(numeric(15),ca_monthly.wo_acct_number) in :wo_acct_numbers and "
			+ "(ca_monthly.bts_month_start_date between :start_date and :end_date) order by ca_monthly.bts_month_start_date desc", nativeQuery = true)
	List<CACashFlow> findMonthlyByAccountNumbers(@Param("wo_acct_numbers") List<String> accountNumbers,
			@Param("start_date") String startDate, @Param("end_date") String endDate);
}
