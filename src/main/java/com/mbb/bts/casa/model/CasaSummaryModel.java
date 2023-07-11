package com.mbb.bts.casa.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "BTS_CASA_SUMMARY")
@Data
public class CasaSummaryModel implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "BTS_ID")
	private Long btsId;
	
	@Column(name = "BTS_MONTH_START_DATE")
	private Date btsMonthStartDate;
	
	@Column(name = "ACCOUNT_NUMBER")
	private String accountNumber;

	//@Column(name = "ACCOUNT_TYPE")
	//private String accountType;

	@Column(name = "OPEN_BALANCE")
	private BigDecimal openBalance;
	
	@Column(name = "DEBIT_AMOUNT")
	private BigDecimal debitAmount;

	@Column(name = "DEBIT_COUNT")
	private Long debitCount;

	@Column(name = "CREDIT_AMOUNT")
	private BigDecimal creditAmount;

	@Column(name = "CREDIT_COUNT")
	private Long creditCount;

}
