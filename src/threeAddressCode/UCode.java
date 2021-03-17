package threeAddressCode;

public class UCode extends Code {

	private Op op;
	private Arg arg1; 
	private Object result; 
	
	public UCode(Op op,Arg arg1) {
		this.op = op;
		this.arg1 = arg1;
	}
	public String print() {
		String s = String.format("%s	%s", op, arg1);
		return s;
		
	}

}
