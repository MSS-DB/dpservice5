package com.mbb.bts.casa.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mbb.bts.casa.service.CasaSummaryService;
import com.mbb.bts.casa.util.CommonFunctions;
import com.mbb.bts.casa.view.View_AccountSummary;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class CasaSummaryController {

	@Autowired
	CasaSummaryService casaSummaryService;

	@GetMapping(value = { "/casa-summary/{account-numbers}" })
	@ApiOperation(value = "Retrieve CASA aging information by accounts numbers")
	@ApiImplicitParams(@ApiImplicitParam(name = "Authorization", value = "Access Token", required = true, allowEmptyValue = false, paramType = "header", example = "Bearer MXIzVURjamdOSU9QejVkbjZla01QTmFGTEtkcHdROE9HOHhoYjJ0RQ=="))
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Bad request! Missing or invalid format parameters"),
			@ApiResponse(code = 404, message = "Not Found! Bank account list is empty"),
			@ApiResponse(code = 500, message = "Internal Server Error") })
	public ResponseEntity<List<View_AccountSummary>> retrieveCasaSummary(
			@ApiParam(value = "446201349910,446201306332", required = true) @PathVariable("account-numbers") List<String> accountNumbers,
			@ApiParam(value = "2021-01-01 (default start-date/end-date empty for last 6 months)", required = false) @RequestParam(value = "start-date", required = false) String btsStartMonthString,
			@ApiParam(value = "2021-06-01 (default start-date/end-date empty for last 6 months)", required = false) @RequestParam(value = "end-date", required = false) String btsEndMonthString
			//@ApiParam(value = "Limit return list size", required = false) @RequestParam(value = "limit", required = false) Integer limit
			)
			throws Exception {
		try {
			List<View_AccountSummary> returnList = casaSummaryService.getSummaryList(accountNumbers,
					CommonFunctions.stringToDate(btsStartMonthString, CommonFunctions.DATE_DB_FORMAT),
					CommonFunctions.stringToDate(btsEndMonthString, CommonFunctions.DATE_DB_FORMAT));
			//log.debug("Start reformat");
			returnList = View_AccountSummary.reformatCasaSummaryList(returnList, null);
			//log.debug("Completed reformat");
			return new ResponseEntity<>(returnList, HttpStatus.OK);
		} catch (Exception ex){
			log.error("retrieveCasaSummary error occured!",ex);
			throw new Exception(ex);
			//return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
