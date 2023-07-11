package com.mbb.bts.casa.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ProductCode {

	CA("Current Account"), SA("Savings Account");

	private String type;

}

