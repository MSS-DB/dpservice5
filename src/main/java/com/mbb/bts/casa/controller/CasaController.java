package com.mbb.bts.casa.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mbb.bts.casa.service.CAService;
//import com.mbb.bts.casa.service.CasaService;
import com.mbb.bts.casa.service.SAService;
//import com.mbb.bts.casa.util.ProductCode;
import com.mbb.bts.casa.util.TransactionIndicator;
import com.mbb.eclipse.common.exception.EclipseException;
import com.mbb.eclipse.common.util.TransactionTypeEnum;
import com.mbb.eclipse.common.view.View_BalanceDetails;
import com.mbb.eclipse.common.view.View_CashFlowDetails;
import com.mbb.eclipse.common.view.View_TransactionHistory;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class CasaController {

	/*@Autowired
	CasaService casaService;*/

	@Autowired
	CAService caService;

	@Autowired
	SAService saService;

	/*@GetMapping("/history-realtime/{product-code}/{account-number}")
	@ApiOperation(value = "Retrieve RealTime CASA History By AccountNumber and ProductCode", response = View_TransactionHistory.class)
	@ApiImplicitParams(@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, allowEmptyValue = false, paramType = "header", example = "Bearer MXIzVURjamdOSU9QejVkbjZla01QTmFGTEtkcHdROE9HOHhoYjJ0RQ=="))
	public ResponseEntity<Object> retrieveCasaHistory(
			@ApiParam(value = "0377965030009558", required = true) @PathVariable("account-number") String accountNumber,
			@PathVariable("product-code") ProductCode productCode,
			@RequestParam(value = "limit", defaultValue = "100") Integer limit,
			@RequestParam(value = "continuation-token", required = false) String continuationToken)
			throws EclipseException {
		log.info("retrieveCasaHistory product-code : " + productCode + ", account-number : " + accountNumber);

		return casaService.retrieveCasaHistory(accountNumber, productCode, limit, continuationToken);
	}*/

	@GetMapping("/history/ca/{account-number}")
	@ApiOperation(value = "Retrieve Current Account History By AccountNumber and Date Range (from T-1 date)", response = View_TransactionHistory.class)
	@ApiImplicitParams(@ApiImplicitParam(name = "Authorization", value = "Access Token", paramType = "header", example = "Bearer MXIzVURjamdOSU9QejVkbjZla01QTmFGTEtkcHdROE9HOHhoYjJ0RQ=="))
	public ResponseEntity<Object> retrieveCAHistory(
			@ApiParam(value = "000566010610326", required = true) @PathVariable("account-number") String accountNumber,
			@RequestParam(value = "transaction-indicator", defaultValue = "ALL") TransactionIndicator transactionIndicator,
			@ApiParam(value = "2018-01-01", required = false) @RequestParam(value = "start-date", required = false) String startDate,
			@ApiParam(value = "2019-12-31", required = false) @RequestParam(value = "end-date", required = false) String endDate,
			@RequestParam(value = "limit", defaultValue = "100") Integer limit,
			@RequestParam(value = "sortby", defaultValue = "transactionDateTime:desc") String sortBy,
			@RequestParam(value = "continuation-token", required = false) String continuationToken,
			HttpServletRequest request) throws EclipseException {
		log.debug("retrieveCAHistory account-number : " + accountNumber);

		return caService.retrieveCAHistory(accountNumber, startDate, endDate, transactionIndicator, limit, sortBy,
				continuationToken);
	}

	@GetMapping("/cash-flow-graph/ca/{account-numbers}")
	@ApiOperation(value = "Retrieve CurrentAccount CashFlow Details By List of AccountNumber", response = View_CashFlowDetails.class)
	@ApiImplicitParams(@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, allowEmptyValue = false, paramType = "header", example = "Bearer MXIzVURjamdOSU9QejVkbjZla01QTmFGTEtkcHdROE9HOHhoYjJ0RQ=="))
	public ResponseEntity<Object> getCACashFlowDetails(
			@ApiParam(value = "534389423742712,147373849732482,12812773429334", required = true) @PathVariable("account-numbers") List<String> accountNumbers,
			@ApiParam(value = "2019-10-01", required = false) @RequestParam(value = "start-date", required = false) String startDate,
			@RequestParam(value = "transaction-type", required = false, defaultValue = "DAILY") TransactionTypeEnum transactionType)
			throws EclipseException {
		log.info("getCACashFlowDetails transaction-type: " + transactionType + ", account-numbers: " + accountNumbers);

		return caService.getCACashFlowDetails(accountNumbers, transactionType, startDate);
	}

	@GetMapping("/balance-graph/ca/{account-numbers}")
	@ApiOperation(value = "Retrieve CurrentAccount Balance Details By List of AccountNumber")
	@ApiImplicitParams(@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, allowEmptyValue = false, paramType = "header", example = "Bearer MXIzVURjamdOSU9QejVkbjZla01QTmFGTEtkcHdROE9HOHhoYjJ0RQ=="))
	public List<View_BalanceDetails> getCABalanceDetails(
			@ApiParam(value = "534389423742712,147373849732482,12812773429334", required = true) @PathVariable("account-numbers") List<String> accountNumbers,
			@ApiParam(value = "2019-10-01", required = false) @RequestParam(value = "start-date", required = false) String startDate,
			@RequestParam(value = "transaction-type", required = false, defaultValue = "DAILY") TransactionTypeEnum transactionType)
			throws EclipseException {
		log.info("getCABalanceDetails transaction-type: " + transactionType + ", account-numbers: " + accountNumbers);

		return caService.retrieveCABalance(accountNumbers, startDate, transactionType);
	}

	@GetMapping("/history/sa/{account-number}")
	@ApiOperation(value = "Retrieve Savings Account History By AccountNumber and Date Range (from T-1 date)", response = View_TransactionHistory.class)
	@ApiImplicitParams(@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, allowEmptyValue = false, paramType = "header", example = "Bearer MXIzVURjamdOSU9QejVkbjZla01QTmFGTEtkcHdROE9HOHhoYjJ0RQ=="))
	public ResponseEntity<Object> retrieveCAHistory(
			@ApiParam(value = "147373849732482", required = true) @PathVariable("account-number") String accountNumber,
			@ApiParam(value = "2018-01-01", required = true) @RequestParam(value = "start-date", required = true) String startDate,
			@ApiParam(value = "2019-12-31", required = true) @RequestParam(value = "end-date", required = true) String endDate,
			@RequestParam(value = "limit", defaultValue = "100") Integer limit,
			@RequestParam(value = "sortby", defaultValue = "transactionDate:desc") String sortBy,
			@RequestParam(value = "continuation-token") String continuationToken) throws EclipseException {
		log.info("retrieveSAHistory account-number : " + accountNumber);

		return saService.retrieveSAHistory(accountNumber, startDate, endDate, TransactionIndicator.ALL, limit, sortBy,
				continuationToken);
	}

	@GetMapping("/cash-flow-graph/sa/{account-numbers}")
	@ApiOperation(value = "Retrieve SavingsAccount CashFlow Details By List of AccountNumber", response = View_CashFlowDetails.class)
	@ApiImplicitParams(@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, allowEmptyValue = false, paramType = "header", example = "Bearer MXIzVURjamdOSU9QejVkbjZla01QTmFGTEtkcHdROE9HOHhoYjJ0RQ=="))
	public ResponseEntity<Object> getSACashFlowDetails(
			@ApiParam(value = "534389423742712,147373849732482,12812773429334", required = true) @PathVariable("account-numbers") List<String> accountNumbers,
			@ApiParam(value = "2019-10-01", required = false) @RequestParam(value = "start-date", required = false) String startDate,
			@RequestParam(value = "transaction-type", required = false, defaultValue = "DAILY") TransactionTypeEnum transactionType)
			throws EclipseException {
		log.info("getCACashFlowDetails transaction-type: " + transactionType + ", account-numbers: " + accountNumbers);

		return caService.getCACashFlowDetails(accountNumbers, transactionType, startDate);
	}

	@GetMapping("/balance-graph/sa/{account-numbers}")
	@ApiOperation(value = "Retrieve SavingsAccount Balance Details By List of AccountNumber")
	@ApiImplicitParams(@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, allowEmptyValue = false, paramType = "header", example = "Bearer MXIzVURjamdOSU9QejVkbjZla01QTmFGTEtkcHdROE9HOHhoYjJ0RQ=="))
	public List<View_BalanceDetails> getSABalanceDetails(
			@ApiParam(value = "534389423742712,147373849732482,12812773429334", required = true) @PathVariable("account-numbers") List<String> accountNumbers,
			@ApiParam(value = "2019-10-01", required = false) @RequestParam(value = "start-date", required = false) String startDate,
			@RequestParam(value = "transaction-type", required = false, defaultValue = "DAILY") TransactionTypeEnum transactionType)
			throws EclipseException {
		log.info("getCABalanceDetails transaction-type: " + transactionType + ", account-numbers: " + accountNumbers);

		return caService.retrieveCABalance(accountNumbers, startDate, transactionType);
	}
}
