package core;


@SuppressWarnings("serial")
public class Leave extends RuntimeException{
	final Object value;
	public Leave(Object value) {
		super(null,null,false,false);
		this.value = value;
	}

}
