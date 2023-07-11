package com.mbb.bts.casa.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * The persistent class for the CASA_HISTORY_CA database table.
 * 
 */
@Entity
@Table(name = "CASA_HISTORY_CA")
@Data
public class CATransactionHistory implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "BTS_ID")
	private Long btsId;
	
	@Column(name = "WO_ACCT_NUMBER")
	private String accountNumber;

	@Column(name = "BTS_TRANSFORMED_WO_AMOUNT")
	private BigDecimal transactionAmount;

	@Column(name = "BTS_TRANSFORMED_WO_PROCESS_DATE")
	private String transactionDate;
	
	@Column(name = "BTS_TRANSFORMED_TRANSACTION_DESCRIPTION")
	private String transactionDescription;
	
	@Column(name = "WO_TRANS_TYPE_IND")
	private String transactionType;
	
	@Column(name = "BTS_TRANSFORMED_WO_PROCESS_DATE_TIME")
	private String transactionDateTime;
	
	public CATransactionHistory() {
	}

}