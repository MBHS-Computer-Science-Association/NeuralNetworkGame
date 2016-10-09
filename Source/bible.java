import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class bible {
	public static void main(String args[]) throws FileNotFoundException {
		char[] alph = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ!#$%& '()*+,-./:;?\"\n".toCharArray();
		NEAT n = new NEAT(alph.length, alph.length);
		Scanner scanny = new Scanner(new File("kjvdat.txt"));
		while(scanny.hasNextLine()) {
			String s = scanny.nextLine();
		}
	}
}
