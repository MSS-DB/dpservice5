package com.mbb.bts.casa.util;

public class CicsSocketConstants {

	CicsSocketConstants() {

	}

	public static final String SERVICE_CODE_CURRENT_ACCOUNT_HISTORY = "DC0001";
	public static final String SERVICE_CODE_SAVINGS_ACCOUNT_HISTORY = "DC0002";
	public static final String ACCOUNT_TYPE_CURRENT_ACCOUNT_HISTORY = "D";
	public static final String ACCOUNT_TYPE_SAVINGS_ACCOUNT_HISTORY = "S";
	public static final int ACCOUNT_NUMBER_LENGTH = 19;
	public static final char ACCOUNT_NUMBER_APPEND_CHAR = '0';
	public static final String DEFAULT_RETRIEVAL_REFERENCE_NO = "000000000000000000000000";
	public static final String CICS_DATE_FRORMAT = "yyyyMMdd";

	public static final String CREDIT_INDICATOR = "C";
	public static final String DEBIT_INDICATOR = "D";
}
