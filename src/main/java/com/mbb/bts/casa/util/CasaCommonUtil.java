package com.mbb.bts.casa.util;

import static com.mbb.bts.casa.util.CicsSocketConstants.CICS_DATE_FRORMAT;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.mbb.eclipse.common.util.CommonFunctions;

public class CasaCommonUtil {

	public static final String BTS_DATE_FRORMAT = "yyyy-MM-dd";

	CasaCommonUtil() {
	}

	public static String getTodayDateString(String format) {
		SimpleDateFormat sdfDate = new SimpleDateFormat(format);
		return sdfDate.format(new Date());
	}

	public static String formatCicsDateToString(String aDate) {
		try {
			SimpleDateFormat cicsFormat = new SimpleDateFormat(CICS_DATE_FRORMAT);
			SimpleDateFormat btsFormat = new SimpleDateFormat(BTS_DATE_FRORMAT);
			return btsFormat.format(cicsFormat.parse(aDate));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String formatBtsDateToString(java.util.Date aDate) {
		SimpleDateFormat sdfDate = new SimpleDateFormat(BTS_DATE_FRORMAT);
		return sdfDate.format(aDate);
	}

	public static BigDecimal formatCicsAmountToBigDecimal(String cicsAmount) {
		if (!CommonFunctions.isNullOrBlank(cicsAmount)) {
			Long balanceLong = Long.parseLong(cicsAmount.substring(1));

			if (0 == balanceLong)
				return BigDecimal.valueOf(0.00);

			String beforeDecimal = cicsAmount.substring(1, cicsAmount.length() - 2);
			String afterDecimal = cicsAmount.substring(cicsAmount.length() - 2);
			if (0 == Long.parseLong(beforeDecimal))
				return BigDecimal.valueOf(Double.parseDouble("0." + afterDecimal));
			else if (cicsAmount.charAt(0) == '+')
				return BigDecimal.valueOf(Double.parseDouble(Long.parseLong(beforeDecimal) + "." + afterDecimal));
			else
				return BigDecimal.valueOf(Double.parseDouble("-" + Long.parseLong(beforeDecimal) + "." + afterDecimal));
		} else {
			return BigDecimal.valueOf(0.00);
		}
	}

}
