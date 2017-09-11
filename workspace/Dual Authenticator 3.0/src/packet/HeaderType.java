package packet;

public enum HeaderType {
	REQ, ACK, ERR;
	
	public static HeaderType getFromString(String headerName) {
		try {
			return HeaderType.valueOf(headerName.toUpperCase());
		} catch (IllegalArgumentException e) {
			return ERR;
		}
	}
}
