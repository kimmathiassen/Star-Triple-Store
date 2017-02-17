package dk.aau.cs.qweb.main;

public class Config {

	private static boolean explain = false;
	
	public static void setExplainFlag() {
		explain = true;
	}
	
	public static boolean getExplainFlag() {
		return explain;
	}
}
