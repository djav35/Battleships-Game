import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


public class Test {

	public static void main (String[] args) throws IOException{
		if (args.length > 0) {
			for (int i = 0; i < Integer.parseInt(args[0]); i++) {
				Process process = Runtime.getRuntime().exec("java Launcher autoplay");
			}
		}
		File file = new File("C:\\Users\\User\\eclipse-workspace\\Battleships Game\\bin\\scores.txt");
		BufferedReader br = new BufferedReader(new FileReader(file));
		String st = null;
		int n = 0;
		int i = 0;
		while ((st = br.readLine()) != null) {
			n += Integer.parseInt(st.substring("Player: CPU  Shots: ".length(), "Player: CPU  Shots: ".length() + 2));
			i++;
		}
		int avg = n/i;
		System.out.println("Average: " + avg + " (in " + i + " games)");
	}
}
