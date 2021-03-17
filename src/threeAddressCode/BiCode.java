package threeAddressCode;

public class BiCode extends Code{

	private Op op;
	private Arg arg1; 
	private Arg arg2;
	private int resultIndex; 
	
	public BiCode(Op op, Arg arg1,Arg arg2) {
		this.op = op;
		this.arg1 = arg1;
		this.arg2 = arg2;
	}
	
	public void print(int index) {
		System.out.printf("s% - s% - s% -s%",Integer.toString(index),arg1.toString(),op.toString(),arg2.toString());
	}

}
