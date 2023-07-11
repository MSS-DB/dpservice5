package com.mbb.bts.pfm.repo;

import java.util.List;

//import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mbb.bts.pfm.model.CasaTransactionHistory;

public interface CasaTransactionHistoryRepo extends JpaRepository<CasaTransactionHistory, String> {

	/*List<TransactionHistoryCASA> findAllByAccountNumberAndBtsProductTypeCodeIn(String accountNumber, List<String> productTypes,
			Pageable pageable);*/
	
	@Query(value="select t.BTS_ID,t.ACCOUNT_NUMBER,t.BTS_PRODUCT_TYPE_CODE,t.BTS_TRANSFORMED_AMOUNT,t.EFFECTIVE_DATE,t.BTS_DESCRIPTION,t.TRANS_IND from BTS_HISTORY t "
			+ "where t.ACCOUNT_NUMBER=:acct_no and (t.BTS_PRODUCT_TYPE_CODE in :product_type_codes) order by t.BTS_ID asc offset :offset rows fetch next :limit rows only", nativeQuery = true)
	List<CasaTransactionHistory> findAllByAccountNumberAndBtsProductTypeCodeIn(@Param("acct_no") String accountNumber, @Param("product_type_codes") List<String> productTypes,
			@Param("offset") int offset, @Param("limit") int limit);
}
