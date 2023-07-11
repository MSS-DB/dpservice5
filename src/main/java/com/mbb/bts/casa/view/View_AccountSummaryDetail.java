package com.mbb.bts.casa.view;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.mbb.bts.casa.util.CommonFunctions;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@JsonIgnoreProperties
public class View_AccountSummaryDetail {

	@ApiModelProperty(notes = "The startMonthDate in String of CASA Account Summary", example = "2021-01-01", position = 2)
	private String startMonthDate;

	private String accountType;

	@ApiModelProperty(notes = "The openBalance of startMonthDateString of CASA Account Summary", example = "100.00", position = 8)
	private BigDecimal openBalance;

	@ApiModelProperty(notes = "The total debitAmount of the month referring to the startMonthDateString of CASA Account Summary", example = "100.00", position = 9)
	private BigDecimal debitAmount;

	@ApiModelProperty(notes = "The total debitCount of the month referring to the startMonthDateString of CASA Account Summary", example = "1", position = 15)
	private Long debitCount;

	@ApiModelProperty(notes = "The creditAmount of the month referring to the startMonthDateString of CASA Account Summary", example = "100.00", position = 21)
	private BigDecimal creditAmount;

	@ApiModelProperty(notes = "The creditCount of the month referring to the startMonthDateString of CASA Account Summary", example = "1", position = 27)
	private Long creditCount;
	
	public View_AccountSummaryDetail(){
		
	}
	
	public View_AccountSummaryDetail(String startMonthDate, BigDecimal openBalance, 
			BigDecimal debitAmount, Long debitCount, BigDecimal creditAmount, Long creditCount) throws Exception {

		this.startMonthDate = startMonthDate;
		this.openBalance = openBalance;
		this.debitAmount = CommonFunctions.defaultZeroIfEmpty(debitAmount);
		this.debitCount = CommonFunctions.defaultZeroIfEmpty(debitCount);
		this.creditAmount = CommonFunctions.defaultZeroIfEmpty(creditAmount);
		this.creditCount = CommonFunctions.defaultZeroIfEmpty(creditCount);
	}
	
}
