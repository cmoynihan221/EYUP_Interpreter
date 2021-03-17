package threeAddressCode;

public class LiteralArg extends Arg {
	private Object value;
	public LiteralArg(Object value) {
		this.value = value;
	}
	public Object get() {
		return value;
	}

}
