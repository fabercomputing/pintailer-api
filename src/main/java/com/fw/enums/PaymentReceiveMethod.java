package com.fw.enums;

/**
 * 
 * @author Narendra Gurjar
 *
 */
public enum PaymentReceiveMethod {

	DIRECT_DEPOSIT((int) 1, "DIRECT_DEPOSIT"), PAYMENT_GATEWAY((int) 2, "PAYMENT_GATEWAY"), UPI((int) 3, "UPI");

	private final int val;
	private final String dbString;

	private PaymentReceiveMethod(int val, String dbString) {
		this.val = val;
		this.dbString = dbString;
	}

	public int getValue() {
		return val;
	}

	public static PaymentReceiveMethod fromInt(int intVal) {
		switch (intVal) {
		case 1:
			return (DIRECT_DEPOSIT);
		case 2:
			return (PAYMENT_GATEWAY);
		case 3:
			return (UPI);

		}
		throw new RuntimeException("InValid Payment Receiving Method: " + intVal);
	}

	public String toDbString() {
		return (this.dbString);
	}

	public static PaymentReceiveMethod fromString(String str) {
		if ("DIRECT_DEPOSIT".equalsIgnoreCase(str)) {
			return (DIRECT_DEPOSIT);
		} else if ("PAYMENT_GATEWAY".equalsIgnoreCase(str)) {
			return (PAYMENT_GATEWAY);
		} else if ("UPI".equalsIgnoreCase(str)) {
			return (UPI);
		} else {
			throw new RuntimeException("No such REce type:" + str);
		}
	}

}
