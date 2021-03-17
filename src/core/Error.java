package core;


public class Error {
	enum errorType {
		TOKEN,
		SYNTAX,
		TYPE
	}
	
	public static void tokenError(String character) {
		System.out.println(String.format("Token Error at character: %s", character));
	}
	
	public static void parseError(String error) {
		System.out.println("Parse Error:"+error);
	}
	public static void runtimeError(String error) {
		System.out.println(error);
	}
}
