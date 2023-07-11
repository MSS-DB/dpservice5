package com.mbb.bts.pfm.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "BTS_HISTORY")
@Data
public class CasaTransactionHistory implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "BTS_ID")
	private String btsId;

	@Column(name = "BTS_PRODUCT_TYPE_CODE") 
	private String btsProductTypeCode;
	 
	@Column(name = "ACCOUNT_NUMBER")
	private String accountNumber;

	@Column(name = "BTS_TRANSFORMED_AMOUNT")
	private String transactionAmount;

	@Column(name = "EFFECTIVE_DATE")
	private java.util.Date transactionDate;

	@Column(name = "BTS_DESCRIPTION")
	private String transactionDescription;

	@Column(name = "TRANS_IND")
	private String transactionIndicator;

}
