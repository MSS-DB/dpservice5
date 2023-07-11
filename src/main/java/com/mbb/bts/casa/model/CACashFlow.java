package com.mbb.bts.casa.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

/**
 * The persistent class for the CASA_HISTORY_CA / CASA_CASHFLOW_CA_WEEKLY/ CASA_CASHFLOW_CA_MONTHLY database table.
 * 
 */
@Entity
@Data
public class CACashFlow implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "BTS_ID")
	private long btsId;

	@Column(name = "WO_ACCT_NUMBER")
	private String accountNumber;

	@Column(name = "BTS_CASHFLOW_DATE")
	private String startDate;

	@Column(name = "BTS_TRANSFORMED_CASH_IN")
	private BigDecimal cashIn;

	@Column(name = "BTS_TRANSFORMED_CASH_OUT")
	private BigDecimal cashOut;

	public CACashFlow() {
	}

}