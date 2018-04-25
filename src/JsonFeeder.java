import java.io.FileReader;
import java.io.IOException;

import javax.print.DocFlavor.READER;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JsonFeeder {
	private static final long FILE_READ_PERIOD_MS = 4000;
	String path;
	public JsonFeeder(String path) {
		this.path=path+"input.json";
	}
	long size=0;
	JSONParser parser = new JSONParser();
	long targetChunk = 0;

	private Object readFile() {
		boolean firstItteratio=true;
		while (true) {
			
			if(!firstItteratio){
				try {
					Thread.sleep(FILE_READ_PERIOD_MS);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			firstItteratio=false;
			
			Object obj = null;
			
			try {
			String jsonString=FileHandler.readFile(path);
			jsonString = "[ "+jsonString+" ]";
			obj = parser.parse(jsonString);
				
			} catch (IOException | ParseException e) {
				System.err.println("Could not open file "+path+" waiting for "+FILE_READ_PERIOD_MS+" ms...");
				continue;
			
			}
		//	System.out.println("file: "+obj.toString());
			/* See if file contains the chunk that we have come to */
			JSONArray array = (JSONArray) obj;
			size=getNumberOfTokens(array);
			for (int i = 0; i < array.size(); i++) {
				Object chunkJson = (Object) ((JSONObject)array.get(i)).get("chunk");
				if (chunkJson != null) {
					long chunk = (long) chunkJson;
					if (chunk == targetChunk) {
					//	System.out.println("Found target chunk "+targetChunk);
						targetChunk++;
						return (Object) ((JSONObject) (array.get(i))).get("drawing");
					}
				}
			}
			
			/*Did not find the target chunk, wait a bit and try again...*/

		}
	}
	private long getNumberOfTokens(JSONArray array) {
		long size=0;
		for (int i = 0; i < array.size(); i++) {
			size+=((JSONArray)((JSONObject)array.get(i)).get("drawing")).size();
		}
		return size;
	}
	JSONArray chunkArray=null;
	int chunkReadTarget=0;
	
	
	/*This function returns the next token in the input file, it will block if there
	 * are until there are avalible tokens. */
	public JSONObject pop() {
		if( chunkArray==null){
			chunkArray= (JSONArray) readFile();
			chunkReadTarget=0;
		}
		/*Are all tokens  in this chunk consumed? if so fetch next chunk...*/
		
		if(chunkReadTarget==chunkArray.size()){
			//System.out.println("READ");
			chunkArray= (JSONArray) readFile();
			chunkReadTarget=0;
		}
		
		/*Get the token and set the target for the next call...*/
		JSONObject ret = (JSONObject) chunkArray.get(chunkReadTarget);
		chunkReadTarget++;
	//	System.out.println(chunkReadTarget+" of "+chunkArray.size());
	//	System.out.println(">>>>>"+ret.toJSONString()+"<<<<<<<<<expect");

	//	System.out.println(chunkReadTarget+" "+ret.toJSONString());
		return ret;
	}
/*
	public static void main(String[] args) {
		JsonFeeder j = new JsonFeeder("testJson.json");

		while (true) {
			System.out.println("Got json chunk: " + j.pop().toJSONString());
		}
	}
*/


	public double size() {
		// TODO Auto-generated method stub
		return size; 
		
	}
}
