import java.io.*;
import java.net.*;

public class Client {
	public Client() {
		ServerPort = 7779;
		DelayTime = 1000;
		socket = null;
		out = null;
	}
	private String ServerIP;
	private int ServerPort;
	private int DelayTime;
	private InetSocketAddress Svr;
	private Socket socket;
	private PrintWriter out;
	
	public void connect_to_server(String addr) {
		
	}
	
	
}
