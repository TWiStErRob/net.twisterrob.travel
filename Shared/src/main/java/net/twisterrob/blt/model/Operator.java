package net.twisterrob.blt.model;

public enum Operator {
	LUL("London Underground"),
	DLR("Dockland Light Railway"),
	THC("Thames Clippers"),
	TRAM("London Tramlink"),
	CAB("Emirates Air Line"),
	LU("LONDON UNITED BUSWAYS LIMITED");

	private String tradingName;

	Operator(String tradingName) {
		this.tradingName = tradingName;
	}

	public String getTradingName() {
		return tradingName;
	}
}
