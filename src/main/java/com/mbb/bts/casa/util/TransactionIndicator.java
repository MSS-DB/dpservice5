package com.mbb.bts.casa.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TransactionIndicator {

	ALL(""), IN("C"), OUT("D"), OTHER("N");

	private String type;

}
