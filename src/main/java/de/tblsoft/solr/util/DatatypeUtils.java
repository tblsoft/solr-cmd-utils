package de.tblsoft.solr.util;


import com.google.common.base.Strings;

import java.util.HashMap;
import java.util.Map;

public class DatatypeUtils {

	public static String BOOLEAN = "boolean";
	public static String INTEGER = "integer";
	public static String LONG = "long";
	public static String DOUBLE = "double";
	public static String STRING = "string";


	private static Map<String, String> decisionTable = new HashMap<String, String>();
	static {
		decisionTable.put(INTEGER + INTEGER, INTEGER);
		decisionTable.put(LONG + LONG, LONG);
		decisionTable.put(DOUBLE + DOUBLE, DOUBLE);
		decisionTable.put(BOOLEAN + BOOLEAN, BOOLEAN);
		decisionTable.put(STRING + STRING, STRING);
		decisionTable.put(INTEGER + LONG, LONG);
		decisionTable.put(LONG + INTEGER, LONG);
		decisionTable.put(INTEGER + DOUBLE, DOUBLE);
		decisionTable.put(DOUBLE + INTEGER, DOUBLE);
		decisionTable.put(LONG + DOUBLE, DOUBLE);
		decisionTable.put(DOUBLE + LONG, DOUBLE);
		decisionTable.put(BOOLEAN + INTEGER, STRING);
		decisionTable.put(INTEGER + BOOLEAN, STRING);
		decisionTable.put(BOOLEAN + LONG, STRING);
		decisionTable.put(LONG + BOOLEAN, STRING);
		decisionTable.put(BOOLEAN + DOUBLE, STRING);
		decisionTable.put(DOUBLE + BOOLEAN, STRING);
		decisionTable.put(BOOLEAN + STRING, STRING);
		decisionTable.put(STRING + BOOLEAN, STRING);
		decisionTable.put(INTEGER + STRING, STRING);
		decisionTable.put(STRING + INTEGER, STRING);
		decisionTable.put(LONG + STRING, STRING);
		decisionTable.put(STRING + LONG, STRING);
		decisionTable.put(DOUBLE + STRING, STRING);
		decisionTable.put(STRING + DOUBLE, STRING);

	}

	public static String getBestDatatype(String dataType1, String dataType2) {
		String bestDataType = decisionTable.get(dataType1 + dataType2);
		if(bestDataType == null) {
			throw new RuntimeException("the decisiontable is not complete.");
		}
		return bestDataType;
	}

	public static boolean isInteger(String value) {
		boolean isNumber = org.apache.commons.lang3.math.NumberUtils.isNumber(value);
		if(!isNumber) {
			return false;
		}
		try {
			Double doubleValue = Double.valueOf(value);
			Integer intValue = doubleValue.intValue();
			if (doubleValue.doubleValue() == intValue.doubleValue()) {
				return true;
			}
		} catch (NumberFormatException e) {
			return false;
		}
		return false;
	}

	public static boolean isLong(String value) {
		boolean isNumber = org.apache.commons.lang3.math.NumberUtils.isNumber(value);
		if(!isNumber) {
			return false;
		}

		try {
			Double doubleValue = Double.valueOf(value);
			Long longValue = doubleValue.longValue();
			if (doubleValue.doubleValue() == longValue.doubleValue()) {
				return true;
			}
		} catch (NumberFormatException e) {
			return false;
		}
		return false;
	}

	public static boolean isNumber(String value) {
		return org.apache.commons.lang3.math.NumberUtils.isNumber(value);
	}

	public static boolean isBoolean(String value) {
		if(Strings.isNullOrEmpty(value)) {
			return false;
		}
		if("true".equals(value.toLowerCase())) {
			return true;
		}
		if("false".equals(value.toLowerCase())) {
			return true;
		}
		return false;
	}

	public static String estimateDatatype(String value) {
		if(isBoolean(value)) {
			return BOOLEAN;
		}
		if(isInteger(value)) {
			return INTEGER;
		}
		if(isLong(value)) {
			return LONG;
		}
		if(isNumber(value)) {
			return DOUBLE;
		}
		return STRING;

	}

}
