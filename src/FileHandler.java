import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileHandler {
	
	public static String readFile(String path) throws IOException{
		

				  byte[] encoded = Files.readAllBytes(Paths.get(path));
				  return new String(encoded, StandardCharsets.UTF_8);
				
	}
	
	public static void writeToPublicLog(String msg){
		PrintWriter out;
		try {
			out = new PrintWriter(TokenFeeder.currentPath+"status.txt");
			out.println(msg);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
