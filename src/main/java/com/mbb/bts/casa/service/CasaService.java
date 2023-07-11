/*package com.mbb.bts.casa.service;

import static com.mbb.bts.casa.util.CasaCommonUtil.formatBtsDateToString;
import static com.mbb.bts.casa.util.CasaCommonUtil.formatCicsAmountToBigDecimal;
import static com.mbb.bts.casa.util.CasaCommonUtil.formatCicsDateToString;
import static com.mbb.bts.casa.util.CasaCommonUtil.getTodayDateString;
import static com.mbb.bts.casa.util.CicsSocketConstants.ACCOUNT_NUMBER_APPEND_CHAR;
import static com.mbb.bts.casa.util.CicsSocketConstants.ACCOUNT_NUMBER_LENGTH;
import static com.mbb.bts.casa.util.CicsSocketConstants.ACCOUNT_TYPE_CURRENT_ACCOUNT_HISTORY;
import static com.mbb.bts.casa.util.CicsSocketConstants.ACCOUNT_TYPE_SAVINGS_ACCOUNT_HISTORY;
import static com.mbb.bts.casa.util.CicsSocketConstants.CICS_DATE_FRORMAT;
import static com.mbb.bts.casa.util.CicsSocketConstants.CREDIT_INDICATOR;
import static com.mbb.bts.casa.util.CicsSocketConstants.DEFAULT_RETRIEVAL_REFERENCE_NO;
import static com.mbb.bts.casa.util.CicsSocketConstants.SERVICE_CODE_CURRENT_ACCOUNT_HISTORY;
import static com.mbb.bts.casa.util.CicsSocketConstants.SERVICE_CODE_SAVINGS_ACCOUNT_HISTORY;
import static com.mbb.eclipse.common.util.CommonFunctions.getStackTrace;
import static com.mbb.eclipse.common.util.CommonFunctions.isNullOrBlank;
import static com.mbb.eclipse.common.util.CommonFunctions.padRight;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maybank.tcpip.cicssst.SocketCall_TransactionHistory;
import com.maybank.tcpip.cicssst.model.TransactionHistoryRequest;
import com.maybank.tcpip.cicssst.model.TransactionHistoryResponseStatements;
import com.maybank.tcpip.cicssst.model.TranscationHistoryResponse;
import com.mbb.bts.casa.util.ProductCode;
import com.mbb.bts.pfm.model.CasaTransactionHistory;
import com.mbb.bts.pfm.repo.CasaTransactionHistoryRepo;
import com.mbb.bts.pfm.util.BTS_ProductTypeCode;
import com.mbb.eclipse.common.exception.EclipseException;
import com.mbb.eclipse.common.util.CurrencyCodeEnum;
import com.mbb.eclipse.common.util.TransactionIndicatorEnum;
import com.mbb.eclipse.common.view.Pagination;
import com.mbb.eclipse.common.view.PaginationResponse;
import com.mbb.eclipse.common.view.View_TransactionHistory;

import lombok.extern.slf4j.Slf4j;

*//**
 * The CasaService class implements an application that used for retrieve CASA
 * Related Services
 *
 * @author Mohamed Saleem
 * @version 1.0 Created 2020-04-01
 *//*
@Service
@Slf4j
public class CasaService {

	@Autowired
	CasaTransactionHistoryRepo casaTransactionHistoryRepo;

	public static final String CICS_TOKEN_TYPE = "cics";
	public static final String BTS_TOKEN_TYPE = "bts";

	*//**
	 * @param accountNumber
	 * @param productCode
	 * @param limit
	 * @param continuationToken
	 * @throws EclipseException
	 *//*
	public ResponseEntity<Object> retrieveCasaHistory(String accountNumber, ProductCode productCode, Integer limit,
			String continuationTokenRequest) throws EclipseException {

		if (limit < 20 || limit > 100 || limit % 20 != 0) {
			return new ResponseEntity<>("Invalid limit - [Min: 20; Max: 100; Allowable Values: 20, 40, 60, 80, 100]",
					HttpStatus.BAD_REQUEST);
		}

		log.debug("retrieveCasaHistory- Response Start ");
		try {
			// List Object For BTS Table
			List<CasaTransactionHistory> btsCasaTransactionHistoryList = null;
			// List Object For CICS Socket
			List<TransactionHistoryResponseStatements> cicsCasaTransactionHistoryList = null;
			String nextTokenValue = "";
			String continuationTokenType = isNullOrBlank(continuationTokenRequest) ? ""
					: continuationTokenRequest.split("_")[0];
			String continuationToken = isNullOrBlank(continuationTokenRequest) ? ""
					: continuationTokenRequest.split("_")[1];

			if (isNullOrBlank(continuationTokenType) || CICS_TOKEN_TYPE.equalsIgnoreCase(continuationTokenType)) {
				int cicsCallCount = 0;
				log.debug("cicsCall(" + cicsCallCount + ") - Start");
				String retrievalReferenceNo;
				TranscationHistoryResponse transcationHistoryResponse = callCicsTransactionHistory(accountNumber,
						productCode, continuationToken);
				cicsCasaTransactionHistoryList = transcationHistoryResponse.objTransactionHistoryResponseStatements;
				retrievalReferenceNo = transcationHistoryResponse.retrievalReferenceNo;
				log.debug("cicsCall(" + cicsCallCount + ") - End");
				log.debug("cicsCall(" + cicsCallCount + ") Size : " + cicsCasaTransactionHistoryList.size());
				cicsCallCount++;

				// CICS will return max 20 records, So Iterate until it matches the limit(input value)
				while (cicsCasaTransactionHistoryList != null && cicsCasaTransactionHistoryList.size() < limit
						&& !DEFAULT_RETRIEVAL_REFERENCE_NO.equalsIgnoreCase(retrievalReferenceNo)) {

					log.debug("cicsCall(" + cicsCallCount + ") - Start");
					TranscationHistoryResponse subsequentCicsResponse = callCicsTransactionHistory(accountNumber,
							productCode, CICS_TOKEN_TYPE + "_" + retrievalReferenceNo);
					cicsCasaTransactionHistoryList
							.addAll(subsequentCicsResponse.objTransactionHistoryResponseStatements);
					retrievalReferenceNo = subsequentCicsResponse.retrievalReferenceNo;
					log.debug("cicsCall(" + cicsCallCount + ") - End");
					log.debug("cicsCall(" + cicsCallCount + ") Size : "
							+ subsequentCicsResponse.objTransactionHistoryResponseStatements.size());
					cicsCallCount++;

					log.debug("retrievalReferenceNo : " + retrievalReferenceNo);
					log.debug("cicsListSize : " + cicsCasaTransactionHistoryList.size());
				}
				nextTokenValue = CICS_TOKEN_TYPE + "_" + retrievalReferenceNo;

				if (cicsCasaTransactionHistoryList == null || cicsCasaTransactionHistoryList.size() < limit) {
					log.debug("btsCall - Start");
					int btsLimit = cicsCasaTransactionHistoryList == null ? limit
							: limit - cicsCasaTransactionHistoryList.size();
					btsCasaTransactionHistoryList = callBtsTransactionHistory(accountNumber, productCode, btsLimit, "");
					nextTokenValue = btsCasaTransactionHistoryList.size() == btsLimit ? BTS_TOKEN_TYPE + "_" + btsLimit
							: "";
					log.debug("btsCall - End");
					log.debug("btsCall Size : " + btsCasaTransactionHistoryList.size());
				}
			} else {
				log.debug("btsCall - Start ");
				btsCasaTransactionHistoryList = callBtsTransactionHistory(accountNumber, productCode, limit,
						continuationToken);
				nextTokenValue = btsCasaTransactionHistoryList.size() == limit
						? BTS_TOKEN_TYPE + "_" + (Long.valueOf(continuationToken) + limit) : "";
				log.debug("btsCall - End ");
				log.debug("btsCall Size : " + btsCasaTransactionHistoryList.size());
			}

			PaginationResponse paginationResponse = new PaginationResponse();
			List<View_TransactionHistory> viewTransactionHistoryList = new ArrayList<>();

			// Construct CICS_History into View_Response
			if (cicsCasaTransactionHistoryList != null && !cicsCasaTransactionHistoryList.isEmpty()) {
				for (TransactionHistoryResponseStatements responce : cicsCasaTransactionHistoryList) {
					View_TransactionHistory viewTransactionHistory = new View_TransactionHistory();
					viewTransactionHistory.setBtsId(Long.valueOf(0));
					viewTransactionHistory
							.setTransactionAmount(formatCicsAmountToBigDecimal(responce.getAmount()).setScale(2));
					viewTransactionHistory.setCurrencyCode(CurrencyCodeEnum.RM);
					viewTransactionHistory.setTransactionDate(formatCicsDateToString(responce.getTransactionDate()));
					viewTransactionHistory.setTransactionDescription(responce.getDescriptionIBTChequeNo());
					if (CREDIT_INDICATOR.equalsIgnoreCase(responce.getCreditDebitIndicator()))
						viewTransactionHistory.setTransactionIndicator(TransactionIndicatorEnum.CR);
					else
						viewTransactionHistory.setTransactionIndicator(TransactionIndicatorEnum.DR);
					viewTransactionHistoryList.add(viewTransactionHistory);
				}
			}

			// Construct BTS_History into View_Response
			if (btsCasaTransactionHistoryList != null && !btsCasaTransactionHistoryList.isEmpty()) {
				for (CasaTransactionHistory casaTransactionHistory : btsCasaTransactionHistoryList) {
					View_TransactionHistory viewTransactionHistory = new View_TransactionHistory();
					viewTransactionHistory.setBtsId(Long.valueOf(casaTransactionHistory.getBtsId()));
					viewTransactionHistory.setTransactionAmount(
							new BigDecimal(casaTransactionHistory.getTransactionAmount()).setScale(2));
					viewTransactionHistory.setCurrencyCode(CurrencyCodeEnum.RM);
					viewTransactionHistory
							.setTransactionDate(formatBtsDateToString(casaTransactionHistory.getTransactionDate()));
					viewTransactionHistory
							.setTransactionDescription(casaTransactionHistory.getTransactionDescription());
					viewTransactionHistory.setTransactionIndicator(
							TransactionIndicatorEnum.valueOf(casaTransactionHistory.getTransactionIndicator()));
					viewTransactionHistoryList.add(viewTransactionHistory);
				}
			}

			else {
				log.debug("RECORD NOT AVAILABLE FOR ACCOUNT : " + accountNumber);
			}
			Pagination pagination = new Pagination();
			pagination.setLimit(limit);
			pagination.setContinuationToken(nextTokenValue);
			paginationResponse.setPagination(pagination);
			paginationResponse.setData(viewTransactionHistoryList);

			log.debug("retrieveCasaHistory- Response End");
			return new ResponseEntity<>(paginationResponse, HttpStatus.OK);
		} catch (Exception ex) {
			log.error(getStackTrace(ex));
			throw new EclipseException("Exception Occurred : " + ex.getMessage());
		}
	}

	public TranscationHistoryResponse callCicsTransactionHistory(String accountNumber, ProductCode productCode,
			String continuationToken) throws EclipseException {
		String cicsDateFormat = CICS_DATE_FRORMAT;
		accountNumber = padRight(accountNumber, ACCOUNT_NUMBER_LENGTH, ACCOUNT_NUMBER_APPEND_CHAR);
		TransactionHistoryRequest transactionHistoryRequest = null;
		SocketCall_TransactionHistory socketCallTransactionHistory = new SocketCall_TransactionHistory();

		if (isNullOrBlank(continuationToken)) {
			if (ProductCode.CA.equals(productCode)) {
				transactionHistoryRequest = new TransactionHistoryRequest(accountNumber,
						SERVICE_CODE_CURRENT_ACCOUNT_HISTORY, ACCOUNT_TYPE_CURRENT_ACCOUNT_HISTORY,
						getTodayDateString(cicsDateFormat));
			} else if (ProductCode.SA.equals(productCode)) {
				transactionHistoryRequest = new TransactionHistoryRequest(accountNumber,
						SERVICE_CODE_SAVINGS_ACCOUNT_HISTORY, ACCOUNT_TYPE_SAVINGS_ACCOUNT_HISTORY,
						getTodayDateString(cicsDateFormat));
			}
		} else {
			if (ProductCode.CA.equals(productCode)) {
				transactionHistoryRequest = new TransactionHistoryRequest(accountNumber,
						SERVICE_CODE_CURRENT_ACCOUNT_HISTORY, ACCOUNT_TYPE_CURRENT_ACCOUNT_HISTORY,
						getTodayDateString(cicsDateFormat), continuationToken);
			} else if (ProductCode.SA.equals(productCode)) {
				transactionHistoryRequest = new TransactionHistoryRequest(accountNumber,
						SERVICE_CODE_SAVINGS_ACCOUNT_HISTORY, ACCOUNT_TYPE_SAVINGS_ACCOUNT_HISTORY,
						getTodayDateString(cicsDateFormat), continuationToken);
			}
		}
		// calling service TCPIP
		TranscationHistoryResponse response = socketCallTransactionHistory
				.callTransactionHistoryTCPIP(transactionHistoryRequest);
		ObjectMapper jsonmapper = new ObjectMapper();
		try {
			System.out.println("CICS Response from class :::::\n" + jsonmapper.writeValueAsString(response));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		if (null == response || null == response.callStatus
				|| "UNSUCCESSFUL".equalsIgnoreCase(response.callStatus.trim())) {
			throw new EclipseException("CICS Call Failed.");
		}
		return response;
	}

	public List<CasaTransactionHistory> callBtsTransactionHistory(String accountNumber, ProductCode productCode,
			Integer limit, String continuationToken) throws EclipseException {
		try {
			int offset = isNullOrBlank(continuationToken) ? 0 : Integer.parseInt(continuationToken);

			List<String> productTypeCodes = new ArrayList<>();
			if (ProductCode.CA.equals(productCode)) {
				productTypeCodes.add(BTS_ProductTypeCode.CA.toString());
				productTypeCodes.add(BTS_ProductTypeCode.PSV.toString());
			} else {
				productTypeCodes.add(BTS_ProductTypeCode.SA.toString());
			}
			return casaTransactionHistoryRepo.findAllByAccountNumberAndBtsProductTypeCodeIn(accountNumber,
					productTypeCodes, offset, limit);
		} catch (Exception ex) {
			throw new EclipseException(getStackTrace(ex));
		}
	}

	public ResponseEntity<Object> retrieveCasaHistoryDummy(String accountNumber, ProductCode productCode, Integer limit,
			String continuationToken) {

		if (limit < 20 || limit > 100 || limit % 20 != 0) {
			return new ResponseEntity<>("Invalid limit - [Min: 20; Max: 100; Allowable Values: 20, 40, 60, 80, 100]",
					HttpStatus.BAD_REQUEST);
		}

		List<View_TransactionHistory> viewTransactionHistoryList = new LinkedList<>();
		View_TransactionHistory viewTransactionHistory1 = new View_TransactionHistory();
		viewTransactionHistory1.setBtsId((long) 101);
		viewTransactionHistory1.setTransactionAmount(BigDecimal.valueOf(30).setScale(2));
		viewTransactionHistory1.setCurrencyCode(CurrencyCodeEnum.RM);
		viewTransactionHistory1.setTransactionDate("2020-03-25");
		viewTransactionHistory1.setTransactionDescription("<RECIPIENT_REFERENCE><OTHER_PAYMENT_DETAILS><SENDER_NAME>");
		viewTransactionHistory1.setTransactionIndicator(TransactionIndicatorEnum.CR);

		View_TransactionHistory viewTransactionHistory2 = new View_TransactionHistory();
		viewTransactionHistory2.setBtsId((long) 102);
		viewTransactionHistory2.setTransactionAmount(BigDecimal.valueOf(159.35).setScale(2));
		viewTransactionHistory2.setCurrencyCode(CurrencyCodeEnum.RM);
		viewTransactionHistory2.setTransactionDate("2020-03-25");
		viewTransactionHistory2.setTransactionDescription("<RECIPIENT_REFERENCE><OTHER_PAYMENT_DETAILS><SENDER_NAME>");
		viewTransactionHistory2.setTransactionIndicator(TransactionIndicatorEnum.CR);

		View_TransactionHistory viewTransactionHistory3 = new View_TransactionHistory();
		viewTransactionHistory3.setBtsId((long) 103);
		viewTransactionHistory3.setTransactionAmount(BigDecimal.valueOf(200).setScale(2));
		viewTransactionHistory3.setCurrencyCode(CurrencyCodeEnum.RM);
		viewTransactionHistory3.setTransactionDate("2020-03-26");
		viewTransactionHistory3.setTransactionDescription("<RECIPIENT_REFERENCE><OTHER_PAYMENT_DETAILS><SENDER_NAME>");
		viewTransactionHistory3.setTransactionIndicator(TransactionIndicatorEnum.DR);

		View_TransactionHistory viewTransactionHistory4 = new View_TransactionHistory();
		viewTransactionHistory4.setBtsId((long) 104);
		viewTransactionHistory4.setTransactionAmount(BigDecimal.valueOf(25.60).setScale(2));
		viewTransactionHistory4.setCurrencyCode(CurrencyCodeEnum.RM);
		viewTransactionHistory4.setTransactionDate("2020-03-27");
		viewTransactionHistory4.setTransactionDescription("<RECIPIENT_REFERENCE><OTHER_PAYMENT_DETAILS><SENDER_NAME>");
		viewTransactionHistory4.setTransactionIndicator(TransactionIndicatorEnum.CR);

		View_TransactionHistory viewTransactionHistory5 = new View_TransactionHistory();
		viewTransactionHistory5.setBtsId((long) 105);
		viewTransactionHistory5.setTransactionAmount(BigDecimal.valueOf(7445.23).setScale(2));
		viewTransactionHistory5.setCurrencyCode(CurrencyCodeEnum.RM);
		viewTransactionHistory5.setTransactionDate("2020-03-27");
		viewTransactionHistory5.setTransactionDescription("<RECIPIENT_REFERENCE><OTHER_PAYMENT_DETAILS><SENDER_NAME>");
		viewTransactionHistory5.setTransactionIndicator(TransactionIndicatorEnum.DR);

		viewTransactionHistoryList.add(viewTransactionHistory5);
		viewTransactionHistoryList.add(viewTransactionHistory4);
		viewTransactionHistoryList.add(viewTransactionHistory3);
		viewTransactionHistoryList.add(viewTransactionHistory2);
		viewTransactionHistoryList.add(viewTransactionHistory1);

		PaginationResponse paginationResponse = new PaginationResponse();
		Pagination pagination = new Pagination();
		pagination.setLimit(limit);
		pagination.setContinuationToken("");
		paginationResponse.setPagination(pagination);
		paginationResponse.setData(viewTransactionHistoryList);

		return new ResponseEntity<>(paginationResponse, HttpStatus.OK);
	}

}
*/