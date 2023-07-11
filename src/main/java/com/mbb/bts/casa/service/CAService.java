package com.mbb.bts.casa.service;

import static com.mbb.eclipse.common.util.CommonFunctions.getStackTrace;
import static com.mbb.eclipse.common.util.CommonFunctions.isNullOrBlank;
import static com.mbb.bts.casa.util.CommonFunctions.leftPad;

import java.time.LocalDate;
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

import com.mbb.bts.casa.model.CACashFlow;
import com.mbb.bts.casa.model.CABalance;
import com.mbb.bts.casa.model.CATransactionHistory;
import com.mbb.bts.casa.repo.CACashFlowRepo;
import com.mbb.bts.casa.repo.CABalanceRepo;
import com.mbb.bts.casa.repo.CATransactionHistoryRepo;
import com.mbb.bts.casa.util.TransactionIndicator;
import com.mbb.eclipse.common.exception.EclipseException;
import com.mbb.eclipse.common.exception.ResourceNotFoundException;
import com.mbb.eclipse.common.util.CurrencyCodeEnum;
import com.mbb.eclipse.common.util.TransactionIndicatorEnum;
import com.mbb.eclipse.common.util.TransactionTypeEnum;
import com.mbb.eclipse.common.view.Pagination;
import com.mbb.eclipse.common.view.PaginationResponse;
import com.mbb.eclipse.common.view.View_BalanceDetails;
import com.mbb.eclipse.common.view.View_CashFlowDetails;
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
public class CAService {

	@Autowired
	CATransactionHistoryRepo casaHistoryCARepo;

	@Autowired
	CABalanceRepo casaBalanceCARepo;

	@Autowired
	CACashFlowRepo cACashFlowRepo;

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
	public ResponseEntity<Object> retrieveCAHistory(final String accountNumber, String startDate, String endDate,
			final TransactionIndicator transactionIndicator, final Integer limit, final String sortBy,
			final String continuationToken) throws EclipseException {
		log.debug("Response Start: ");
		try {
			// Padding ACCOUNT_NUMBER
			String acctNumber = leftPad(accountNumber, (15 - accountNumber.length()), "0");
			int page = isNullOrBlank(continuationToken) ? 0 : Integer.parseInt(continuationToken);
			if (isNullOrBlank(startDate) || isNullOrBlank(endDate)) {
				endDate = LocalDate.now().toString();
				startDate = LocalDate.now().minusMonths(3).toString();
			}

			String[] sortByArray = sortBy.split(":");
			Direction sortByOrder = sortBy.split(":").length > 1 && "desc".equals(sortBy.split(":")[1]) ? Direction.DESC
					: Direction.ASC;
			Pageable pageable = PageRequest.of(page, limit, Sort.by(sortByOrder, sortByArray[0]));
			List<CATransactionHistory> casaHistoryCAList = null;
			List<String> transactionTypes = new ArrayList<>();
			if (TransactionIndicator.ALL.equals(transactionIndicator)) {
				transactionTypes.add(TransactionIndicator.IN.getType());
				transactionTypes.add(TransactionIndicator.OUT.getType());
			} else {
				transactionTypes.add(transactionIndicator.getType());
			}
			casaHistoryCAList = casaHistoryCARepo.findAllByAccountNumberAndTransactionDateBetweenAndTransactionTypeIn(
					acctNumber, startDate, endDate, transactionTypes, pageable);

			String continueToken = "";
			List<View_TransactionHistory> viewTransactionHistoryList = new ArrayList<>();
			if (casaHistoryCAList != null && !casaHistoryCAList.isEmpty()) {
				for (CATransactionHistory casaHistoryCA : casaHistoryCAList) {
					View_TransactionHistory viewTransactionHistory = new View_TransactionHistory();
					viewTransactionHistory.setBtsId(casaHistoryCA.getBtsId());
					// viewTransactionHistory.setAccountNumber(casaHistoryCA.getAccountNumberDisplay());
					viewTransactionHistory.setTransactionAmount(casaHistoryCA.getTransactionAmount().setScale(2));
					viewTransactionHistory.setCurrencyCode(CurrencyCodeEnum.RM);
					viewTransactionHistory.setTransactionDate(casaHistoryCA.getTransactionDate());
					if (TransactionIndicator.IN.getType().equals(casaHistoryCA.getTransactionType()))
						viewTransactionHistory.setTransactionIndicator(TransactionIndicatorEnum.CR);
					else
						viewTransactionHistory.setTransactionIndicator(TransactionIndicatorEnum.DR);
					viewTransactionHistory.setTransactionDescription(casaHistoryCA.getTransactionDescription());
					viewTransactionHistoryList.add(viewTransactionHistory);
				}
				continueToken = casaHistoryCAList.size() == limit ? String.valueOf(page + 1) : "";
			} else {
				log.info("RECORD NOT AVAILABLE FOR ACCOUNT : " + acctNumber);
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

	/**
	 * 
	 * @param accountNumbers
	 * @param startDate
	 * @param transactionType
	 *            { DAILY, WEEKLY, MONTHLY }
	 * @return BTS Standard : DAILY (3 weeks), WEEKLY (3 months), MONTHLY (12
	 *         months)
	 * @throws EclipseException
	 */
	public List<View_BalanceDetails> retrieveCABalance(List<String> accountNumbers, String startDate,
			TransactionTypeEnum transactionType) throws EclipseException {
		log.debug("RetrieveCABalance Response Start: ");

		List<View_BalanceDetails> viewBalanceDetailsList = new ArrayList<>();

		try {

			if (isNullOrBlank(startDate)) {
				startDate = LocalDate.now().toString();
			}

			List<CABalance> currentAccountBalanceList = null;
			if (TransactionTypeEnum.MONTHLY.equals(transactionType)) {
				currentAccountBalanceList = casaBalanceCARepo
						.findAllByAccountNumberInAndWorkDateBetweenAndBtsIsEOMonthTrueOrderByWorkDateDesc(
								accountNumbers, startDate, LocalDate.now().minusMonths(12).toString());
			} else if (TransactionTypeEnum.WEEKLY.equals(transactionType)) {
				currentAccountBalanceList = casaBalanceCARepo
						.findAllByAccountNumberInAndWorkDateBetweenAndBtsIsEOMonthTrueOrderByWorkDateDesc(
								accountNumbers, startDate, LocalDate.now().minusMonths(3).toString());
			} else {
				currentAccountBalanceList = casaBalanceCARepo
						.findAllByAccountNumberInAndWorkDateBetweenOrderByWorkDateDesc(accountNumbers, startDate,
								LocalDate.now().minusWeeks(3).toString());
			}

			if (currentAccountBalanceList != null && !currentAccountBalanceList.isEmpty()) {

				for (CABalance cABalance : currentAccountBalanceList) {
					View_BalanceDetails viewBalanceDetails = new View_BalanceDetails();
					viewBalanceDetails.setAccountNumber(cABalance.getAccountNumber());
					viewBalanceDetails.setTransactionDate(cABalance.getWorkDate());
					viewBalanceDetails.setOpeningBalance(cABalance.getStartBalance());
					viewBalanceDetails.setClosingBalance(cABalance.getCurrentBalance());
					viewBalanceDetails.setCurrencyCode(CurrencyCodeEnum.RM);
					viewBalanceDetailsList.add(viewBalanceDetails);
				}
			} else {
				log.debug("No data found for account number, " + accountNumbers.toArray());
			}

			return viewBalanceDetailsList;

		} catch (Exception ex) {
			log.error(getStackTrace(ex));
			throw new EclipseException("retrieveCABalance Exception Occurred : " + ex.getMessage());
		}
	}

	/**
	 * 
	 * @param accountNumbers
	 * @param transactionType
	 *            { DAILY, WEEKLY, MONTHLY }
	 * @param startDate
	 * @return BTS Standard : DAILY (3 weeks), WEEKLY (3 months), MONTHLY (12
	 *         months)
	 * @throws EclipseException
	 */
	public ResponseEntity<Object> getCACashFlowDetails(List<String> accountNumbers, TransactionTypeEnum transactionType,
			String startDate) throws EclipseException {
		try {

			if (isNullOrBlank(startDate)) {
				startDate = LocalDate.now().toString();
			}
			List<CACashFlow> cashFlowCAList;
			String previousDate;
			if (TransactionTypeEnum.DAILY.equals(transactionType)) {
				previousDate = LocalDate.parse(startDate).minusWeeks(3).toString();
				cashFlowCAList = cACashFlowRepo.findDailyByAccountNumbers(accountNumbers, previousDate, startDate);
			} else if (TransactionTypeEnum.WEEKLY.equals(transactionType)) {
				previousDate = LocalDate.parse(startDate).minusMonths(3).toString();
				cashFlowCAList = cACashFlowRepo.findWeeklyByAccountNumbers(accountNumbers, previousDate, startDate);
			} else {
				previousDate = LocalDate.parse(startDate).minusMonths(12).toString();
				cashFlowCAList = cACashFlowRepo.findMonthlyByAccountNumbers(accountNumbers, previousDate, startDate);
			}

			List<View_CashFlowDetails> viewCashFlowDetailsList = new ArrayList<>();
			if (cashFlowCAList != null && !cashFlowCAList.isEmpty()) {
				for (CACashFlow cACashFlow : cashFlowCAList) {
					View_CashFlowDetails viewCashFlowDetails = new View_CashFlowDetails();
					viewCashFlowDetails.setAccountNumber(cACashFlow.getAccountNumber());
					viewCashFlowDetails.setCashIn(cACashFlow.getCashIn().setScale(2));
					viewCashFlowDetails.setCashOut(cACashFlow.getCashOut().setScale(2));
					viewCashFlowDetails.setCurrencyCode(CurrencyCodeEnum.RM);
					viewCashFlowDetails.setTransactionDate(cACashFlow.getStartDate());
					viewCashFlowDetailsList.add(viewCashFlowDetails);
				}
			} else {
				log.debug("No data found for account numbers, " + accountNumbers.toArray());
			}
			return new ResponseEntity<>(viewCashFlowDetailsList, HttpStatus.OK);
		} catch (Exception ex) {
			log.error(getStackTrace(ex));
			throw new EclipseException("Exception Occurred : " + ex.getMessage());
		}
	}
}
