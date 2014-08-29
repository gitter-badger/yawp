package endpoint.utils;

public enum HttpVerb {

	GET, POST, PUT, PATCH, DELETE;

	public static HttpVerb getFromString(String method) {
		String methodLowerCase = method.toUpperCase();
		return valueOf(methodLowerCase);
	}

}
