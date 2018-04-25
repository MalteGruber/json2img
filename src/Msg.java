
public class Msg {

	String loc = "";

	public boolean ENABLE_DEBUG = false;

	public Msg(String string) {
		this.loc = string;
	}

	public Msg(String msg, boolean b) {
		this(msg);
		ENABLE_DEBUG=b;
		
	}

	public void w(String msg) {
		System.err.println(loc + ": " + msg);
	}

	public void e(String msg) {
		System.err.println("[!!ERROR " + loc+"]" + msg);
	}

	public void wtf(String msg) {
		System.err.println("[!!WTF " + loc + "]" + msg);
	}

	public void d(Object msg) {
		if(ENABLE_DEBUG){
		System.err.println("[DEBUG from " + loc + "]" + msg);
		}
	}

}
