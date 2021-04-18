package core;


import java.io.IOException;
import core.Loop;
@SuppressWarnings("unused")
public class Launcher {
	private static String cmd = "cmd.exe /c cd bin & start ";
	private static String eyup = "cmd.exe /k java -cp .;eyup.jar core.Loop";
	@SuppressWarnings("unused")
	private static String exit = "taskkill /f /im cmd.exe";
	//ecore.Loop.main(new String[] {});
	public static void main(String[] args) throws IOException {
		Runtime rt = Runtime.getRuntime();
		try {
			Process process = rt.exec(cmd+eyup);

			int exitCode = process.waitFor();
			rt.exec(exit);
			if (exitCode != 0) {
				System.err.println("CMD: abnormal termination");}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
