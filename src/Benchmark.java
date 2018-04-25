import java.util.ArrayList;

public class Benchmark {

	private static String names[]=new String[100];
	private static long starts[]=new long[100];
	private static long stops[]=new long[100];
	private static int maxId=0;
	private static boolean beSilent=true;
	
	public static void start(int id,String name){
		starts[id]= System.nanoTime();
		names[id]=name;
		stops[id]=-100000000;
		if(id>maxId)
			maxId=id;
	}
	public static void stop(int id){
		stops[id]= System.nanoTime();
	}
	public static void report(){
		if(beSilent)
			return;
		System.err.println("=======================================");

			for (int i = 0; i <=maxId; i++) {
				System.err.println(names[i]+": "+((stops[i]-starts[i])/1000000)+" ms");
			}
			
		System.err.println("=======================================");
	}
	public static void beSilent() {
		beSilent=true;
		
	}
}
