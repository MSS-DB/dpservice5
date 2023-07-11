package com.mbb.bts.casa.service;


import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.mbb.bts.casa.repo.CasaSummaryRepo;
import com.mbb.bts.casa.view.View_AccountSummary;

@Service
public class CasaSummaryService  {

	@Autowired
	private CasaSummaryRepo repo;

	public List<View_AccountSummary> getSummaryList(List<String> accountNumbers, Date btsStartMonthString,Date btsEndMonthString) throws Exception {
		//ValidationHelper.validateCards(accountNumbers);

		if (StringUtils.isEmpty(btsStartMonthString) && StringUtils.isEmpty(btsEndMonthString)){
			//return repo.findCasaSummaryByDefaultMonth(PageRequest.of(0, BTSConfigProperties.pagingMaxItem),accountNumbers);
			//return repo.findCasaSummaryByDefaultMonth(accountNumbers);
			Calendar calender = Calendar.getInstance();
			calender.add(Calendar.MONTH, -6);
			return repo.findCasaSummaryByStartDate(accountNumbers,calender.getTime());
		} else if (StringUtils.isEmpty(btsStartMonthString)){
			return repo.findCasaSummaryByStartDate(accountNumbers,btsStartMonthString);
		}  else if (StringUtils.isEmpty(btsEndMonthString)){
			return repo.findCasaSummaryByEndDate(accountNumbers,btsEndMonthString);
		}
		return repo.findCasaSummary(accountNumbers,btsStartMonthString,btsEndMonthString);
	}

}
