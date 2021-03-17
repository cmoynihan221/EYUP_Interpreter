package threeAddressCode;

import java.util.ArrayList;

public class Program {
	ArrayList<Code> program = new ArrayList<Code>();
	int counter;
	public Program() {
		
	}
	public void addCode(Code newCode) {
		counter++;
		program.add(newCode);
	}
	public int pIndex() {
		return counter;
	}
	public void print() {
		for (int i = 0; i>program.size();i++) {
			System.out.printf("%s	%s", i, program.get(i).print());
		}
	}
}
