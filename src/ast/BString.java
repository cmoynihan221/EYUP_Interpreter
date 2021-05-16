package ast;

public class BString  extends Object{
	private String message;
	public BString(String message) {
		this.message = message;
	}
	
	public String toString(){
		return message;
	}
}
