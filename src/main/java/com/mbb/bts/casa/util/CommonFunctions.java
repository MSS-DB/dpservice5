package com.mbb.bts.casa.util;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CommonFunctions {

	public static final String DATE_DB_FORMAT = "yyyy-MM-dd";

	
	public static String leftPad(String input, int length, String fill) {
		StringBuilder sb = new StringBuilder(input);
		for (int i = 0; i < length; i++) {
			sb.insert(0, fill);
		}
		return sb.toString();
	}
	

	/**
	 * This method prepare a date object in String using given format
	 * 
	 * @param Date date
	 * @param String format
	 */
	public static String dateToString( Date date, String format ) {
		if (date == null){
			return null;
		}
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
		return simpleDateFormat.format(date);
	}
	
	/**
	 * This method returns Date object from string input with given format
	 * 
	 * @param String date
	 * @param String format
	 * @return Date {@link Date}
	 * @throws ParseException 
	 */
	public static Date stringToDate(String date, String format) throws ParseException {
		
			if (date == null){
				return null;
			}
			return new SimpleDateFormat(format).parse(date);
	
	}
	

	/**
	 * This method defaultZeroIfEmpty
	 * 
	 * @param BigDecimal input
	 */
	public static BigDecimal defaultZeroIfEmpty(BigDecimal input) {
		if ( input == null )
			return BigDecimal.ZERO;
		return input;
	}
	

	/**
	 * This method defaultZeroIfEmpty
	 * 
	 * @param Long input
	 */
	public static Long defaultZeroIfEmpty(Long input) {
		if ( input == null )
			return (long)0;
		return input;
	}
	

}
