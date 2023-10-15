package com.paymentchain.transaction.entities;

public enum Channel {
	WEB("WEB"),
	ATM("ATM"),
	OFFICE("OFFICE");

	private final String description;

	Channel(String description) {
			this.description = description;
	}

	public String getDescription() {
			return description;
	}
}
