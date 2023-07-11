package com.mbb.bts.casa.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * The persistent class for the CASA_BALANCE_CA database table.
 * 
 */
@Entity
@Table(name = "CASA_BALANCE_CA")
@Data
public class CABalance implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "BTS_ID")
	private long btsId;
	
	@Column(name = "WI_ACCOUNT_NUMBER")
	private String accountNumber;

	@Column(name = "BTS_TRANSFORMED_WI_CURRENT_BALANCE")
	private BigDecimal currentBalance;
	
	@Column(name = "BTS_TRANSFORMED_WI_TODAY_START_BAL")
	private BigDecimal startBalance;
	
	@Column( name = "BTS_TRANSFORMED_WI_WORK_DATE1")
	private String workDate;
	
	@Column( name = "BTS_IS_SUNDAY")
	private Boolean btsIsSunday;
	
	@Column( name = "BTS_IS_EOMONTH")
	private Boolean btsIsEOMonth;
	
	public CABalance() {
	}
}
