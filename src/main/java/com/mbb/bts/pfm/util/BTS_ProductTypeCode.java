package com.mbb.bts.pfm.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BTS_ProductTypeCode {
	CA("Current Account"), 
	SA("Savings Account"), 
	PSV("Premier Savings"), 
	CC("Credit Card"), 
	DC("Debit Card"),
	CW("");

	private String code;
}
