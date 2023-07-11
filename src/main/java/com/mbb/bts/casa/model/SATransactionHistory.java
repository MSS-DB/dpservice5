package com.mbb.bts.casa.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * The persistent class for the CASA_HISTORY_SA database table.
 * 
 */
@Entity
@Table(name = "CASA_HISTORY_SA")
@Data
public class SATransactionHistory implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "BTS_ID")
	private Long btsId;

	@Column(name = "WO_JACCT")
	private String accountNumber;

	@Column(name = "BTS_TRANSFORMED_WO_JTAMT")
	private BigDecimal transactionAmount;

	@Column(name = "BTS_TRANSFORMED_WO_JDATE_WO_JTIME")
	private String transactionDateTime;

	@Column(name = "WO_JCUSTREF")
	private String customerReference;

	@Column(name = "WO_JPYMTDTL")
	private String paymentDetail;

	@Column(name = "WO_JSDRNAME")
	private String senderName;

	@Column(name = "WO_JCID")
	private String transactionIndicator;

	public SATransactionHistory() {
	}

}