package com.paymentchain.transaction.entities;

public enum Status {
	PENDING("01", "Pending"),
	LIQUIDATED("02", "Liquidated"),
	REJECTED("03", "Rejected"),
	CANCELLED("04", "Cancelled");

	private final String code;
	private final String description;

	Status(String code, String description) {
			this.code = code;
			this.description = description;
	}

	public String getCode() {
			return code;
	}

	public String getDescripTion() {
			return description;
	}
}
