package packet;

public enum HeaderName {
	KEY, SDV, ERR;
	
	public static HeaderName getFromString(String headerName) {
		try {
			return HeaderName.valueOf(headerName.toUpperCase());
		} catch (IllegalArgumentException e) {
			return ERR;
		}
	}
}
