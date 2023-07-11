package com.mbb.bts.casa.service;

import static com.mbb.eclipse.common.util.CommonFunctions.getStackTrace;
import static com.mbb.eclipse.common.util.CommonFunctions.isNullOrBlank;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.mbb.bts.casa.model.SATransactionHistory;
import com.mbb.bts.casa.repo.SATransactionHistoryRepo;
import com.mbb.bts.casa.util.TransactionIndicator;
import com.mbb.eclipse.common.exception.EclipseException;
import com.mbb.eclipse.common.exception.ResourceNotFoundException;
import com.mbb.eclipse.common.util.CurrencyCodeEnum;
import com.mbb.eclipse.common.util.TransactionIndicatorEnum;
import com.mbb.eclipse.common.view.Pagination;
import com.mbb.eclipse.common.view.PaginationResponse;
import com.mbb.eclipse.common.view.View_TransactionHistory;

import lombok.extern.slf4j.Slf4j;

/**
 * The CasaService class implements an application that used for retrieve CASA
 * Related Services
 *
 * @author Mohamed Saleem
 * @version 1.0 Created 2019-10-02
 */
@Service
@Slf4j
public class SAService {

	@Autowired
	SATransactionHistoryRepo casaHistorySARepo;

	/**
	 * @param accountNumber
	 * @param startDate
	 * @param endDate
	 * @param transactionIndicator
	 * @param limit
	 * @param sortBy
	 * @param continuationToken
	 * @throws EclipseException
	 * 
	 */
	public ResponseEntity<Object> retrieveSAHistory(final String accountNumber, String startDate, String endDate,
			final TransactionIndicator transactionIndicator, final Integer limit, final String sortBy,
			final String continuationToken) throws EclipseException {
		log.info("Response Start: ");
		try {
			
			// Padding ACCOUNT_NUMBER
			/*List<String> acctNumbers = new ArrayList<>();
			for (String acctNumber : accountNumbers) {
				acctNumbers.add(leftPad(acctNumber, (12 - acctNumber.length()), "0"));
			}*/

			int page = isNullOrBlank(continuationToken) ? 0 : Integer.parseInt(continuationToken);
			if (isNullOrBlank(startDate) || isNullOrBlank(endDate)) {
				endDate = LocalDate.now().toString();
				startDate = LocalDate.now().minusMonths(3).toString();
			} else {
				endDate = LocalDate.parse(endDate).plusDays(1).toString();
			}

			DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
			String[] sortByArray = sortBy.split(":");
			Direction sortByOrder = sortBy.split(":").length > 1 && "desc".equals(sortBy.split(":")[1]) ? Direction.DESC
					: Direction.ASC;
			Pageable pageable = PageRequest.of(page, limit, Sort.by(sortByOrder, sortByArray[0]));
			List<SATransactionHistory> casaHistorySAList = null;
			if (TransactionIndicator.ALL.equals(transactionIndicator)) {
				casaHistorySAList = casaHistorySARepo.findAllByAccountNumberInAndTransactionDateTimeBetween(
						accountNumber, startDate, endDate, pageable);
			} else {
				casaHistorySAList = casaHistorySARepo
						.findAllByAccountNumberInAndTransactionDateTimeBetweenAndTransactionIndicator(accountNumber,
								startDate, endDate, transactionIndicator.getType(), pageable);
			}

			String continueToken = "";
			List<View_TransactionHistory> viewTransactionHistoryList = new ArrayList<>();
			if (casaHistorySAList != null && !casaHistorySAList.isEmpty()) {
				for (SATransactionHistory casaHistorySA : casaHistorySAList) {
					View_TransactionHistory viewTransactionHistory = new View_TransactionHistory();
					viewTransactionHistory.setBtsId(casaHistorySA.getBtsId());
					viewTransactionHistory.setTransactionAmount(casaHistorySA.getTransactionAmount().setScale(2));
					viewTransactionHistory.setCurrencyCode(CurrencyCodeEnum.RM);
					viewTransactionHistory
							.setTransactionDate(LocalDate.parse(casaHistorySA.getTransactionDateTime(), df).toString());
					if (TransactionIndicator.IN.getType().equals(casaHistorySA.getTransactionIndicator()))
						viewTransactionHistory.setTransactionIndicator(TransactionIndicatorEnum.CR);
					else
						viewTransactionHistory.setTransactionIndicator(TransactionIndicatorEnum.DR);
					viewTransactionHistory.setTransactionDescription(casaHistorySA.getCustomerReference()
							+ casaHistorySA.getPaymentDetail() + casaHistorySA.getSenderName());
					viewTransactionHistoryList.add(viewTransactionHistory);
				}
				continueToken = casaHistorySAList.size() == limit ? String.valueOf(page + 1) : "";
			} else {
				log.info("RECORD NOT AVAILABLE FOR ACCOUNT : " + accountNumber);
				// throw new ResourceNotFoundException("ACCOUNT : ",
				// accountNumber);
			}
			PaginationResponse paginationResponse = new PaginationResponse();
			Pagination pagination = new Pagination();
			pagination.setLimit(limit);
			pagination.setContinuationToken(continueToken);
			paginationResponse.setPagination(pagination);
			paginationResponse.setData(viewTransactionHistoryList);
			
			log.info("Response End: ");
			return new ResponseEntity<>(paginationResponse, HttpStatus.OK);
		} catch (ResourceNotFoundException ex) {
			throw ex;
		} catch (Exception ex) {
			log.error(getStackTrace(ex));
			throw new EclipseException("Exception Occurred : " + ex.getMessage());
		}
	}
}
