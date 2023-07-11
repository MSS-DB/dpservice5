package com.mbb.bts.casa.view;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.mbb.bts.casa.util.CommonFunctions;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Accessors(chain = true)
@ApiModel(description = "This is a object for CASA Account Summary view")
public class View_AccountSummary {

	@ApiModelProperty(notes = "The accountNumber of CASA Account Summary", example = "100.00", position = 1)
	private String accountNumber;

	@ApiModelProperty(notes = "The startMonthDate in String of CASA Account Summary", example = "2021-01-01", position = 2)
	private String startMonthDate;

	@ApiModelProperty(notes = "The endMonthDate in String of CASA Account Summary", example = "2021-06-01", position = 2)
	private String endMonthDate;
	
	@JsonIgnore
	private String accountType;

	@JsonIgnore
	private BigDecimal openBalance;

	@JsonIgnore
	private BigDecimal debitAmount;

	@JsonIgnore
	private Long debitCount;

	@JsonIgnore
	private BigDecimal creditAmount;

	@JsonIgnore
	private Long creditCount;
	
	@ApiModelProperty(notes = "The summary list by month in CASA Account Summary", example = "2021-06-01", position = 2)
	private List<View_AccountSummaryDetail> summaryList;
	
	private BigDecimal totalDebitAmount=BigDecimal.ZERO;
	private Long totalDebitCount=(long) 0;
	private BigDecimal totalCreditAmount=BigDecimal.ZERO;
	private Long totalCreditCount=(long) 0;
	
	// commented requirement for sort past2month most 4 most active account
	//private BigDecimal totalPast2MonthDebitCreditAmount;
	//private Long totalPast2MonthDebitCreditCount;
	
	public View_AccountSummary(){
		
	}
	
	public View_AccountSummary(Date monthStartDate, String accountNumber, BigDecimal openBalance, 
			BigDecimal debitAmount, Long debitCount, BigDecimal creditAmount, Long creditCount) throws Exception {
		this.startMonthDate = CommonFunctions.dateToString(monthStartDate,CommonFunctions.DATE_DB_FORMAT);
		this.accountNumber = accountNumber;
		this.openBalance = openBalance;
		this.debitAmount = debitAmount;
		this.debitCount = debitCount;
		this.creditAmount = creditAmount;
		this.creditCount = creditCount;
	}
	
	public static List<View_AccountSummary> reformatCasaSummaryList(List<View_AccountSummary> View_AccountSummaryList,Integer limit) throws Exception{
		
		HashMap<String,View_AccountSummary> map = new HashMap<>();
		
		for (View_AccountSummary summary : View_AccountSummaryList){
			View_AccountSummary mergedView_AccountSummary = map.get(summary.getAccountNumber());
			mergedView_AccountSummary = summary.mergeCasaSummary(mergedView_AccountSummary);
			map.put(summary.getAccountNumber(), mergedView_AccountSummary);
		}
		
		List<View_AccountSummary> reformatList = new ArrayList<>(map.values());

		// commented requirement for sort past2month most 4 most active account
/*		reformatList.sort(new SortView_AccountSummary());
		
		if (limit != null){
			List<View_AccountSummary> limitList = new ArrayList<View_AccountSummary>();
			for (View_AccountSummary reformatView : reformatList){
				limitList.add(reformatView);
				
				if(limitList.size()==limit){
					break;
				}
			}
			return limitList;
		}
*/
		return reformatList;
		
		
	}
	
	public View_AccountSummary mergeCasaSummary(View_AccountSummary mergedView_AccountSummary) throws Exception{
		List<View_AccountSummaryDetail> mergedSummaryList = null;
		if (mergedView_AccountSummary == null){
			mergedView_AccountSummary = new View_AccountSummary();
			mergedView_AccountSummary.setAccountNumber(this.getAccountNumber());
			mergedView_AccountSummary.setAccountType(this.getAccountType());

			mergedSummaryList = new ArrayList<>();
		} else {
			mergedSummaryList = mergedView_AccountSummary.getSummaryList();
		}
		
		mergedSummaryList.add(new View_AccountSummaryDetail(this.getStartMonthDate(),this.getOpenBalance(),this.getDebitAmount(),this.getDebitCount(),this.getCreditAmount(),this.getCreditCount()));
		mergedView_AccountSummary.setSummaryList(mergedSummaryList);
		
		BigDecimal totalCreditAmountLocal = mergedView_AccountSummary.getTotalCreditAmount().add(CommonFunctions.defaultZeroIfEmpty(this.getCreditAmount()));
		mergedView_AccountSummary.setTotalCreditAmount(totalCreditAmountLocal);
		Long totalCreditCountLocal = mergedView_AccountSummary.getTotalCreditCount() + CommonFunctions.defaultZeroIfEmpty(this.getCreditCount());
		mergedView_AccountSummary.setTotalCreditCount(totalCreditCountLocal);
		BigDecimal totalDebitAmountLocal = mergedView_AccountSummary.getTotalDebitAmount().add(CommonFunctions.defaultZeroIfEmpty(this.getDebitAmount()));
		mergedView_AccountSummary.setTotalDebitAmount(totalDebitAmountLocal);
		Long totalDebitCountLocal = mergedView_AccountSummary.getTotalDebitCount() + CommonFunctions.defaultZeroIfEmpty(this.getDebitCount());
		mergedView_AccountSummary.setTotalDebitCount(totalDebitCountLocal);
		/*
		// commented requirement for sort past2month most 4 most active account
		if (mergedView_AccountSummary.getSummaryList().size() <= 2){
			mergedView_AccountSummary.setTotalPast2MonthDebitCreditAmount(mergedView_AccountSummary.getTotalDebitAmount().add(mergedView_AccountSummary.getTotalCreditAmount()));
			mergedView_AccountSummary.setTotalPast2MonthDebitCreditCount(mergedView_AccountSummary.getTotalDebitCount()+mergedView_AccountSummary.getTotalCreditCount());
		}
		*/
		return mergedView_AccountSummary;
	}

}
