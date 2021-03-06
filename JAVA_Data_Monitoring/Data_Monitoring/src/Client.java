import java.io.*;
import java.net.*;

public class Client {
	public Client() {
		ServerPort = 7779;
		DelayTime = 1000;
		socket = null;
		Svr = null;
		out = null;
	}
	private String ServerIP;
	private int ServerPort;
	private int DelayTime;
	private InetSocketAddress Svr;
	private Socket socket;
	private PrintWriter out;
	
	public void connect_to_server(String addr) throws IOException {
		socket = new Socket();
		ServerIP = addr;
		Svr = new InetSocketAddress(ServerIP, ServerPort);
		socket.connect(Svr, DelayTime);
		System.out.printf("???? ????[%s] [%d]\n",  ServerIP, ServerPort);
	}
	
	public void send_data(float data, String sensor) {
		try {
			out = new PrintWriter(socket.getOutputStream(), true);
			String out_string = sensor +"\t"+ String.valueOf(data);
			out.println(out_string);
			System.out.println("Client Sending: "+out_string);
		}	catch(Exception e) {
			
		}
	}
	public void disconnect_to_server() {
		try {
			socket.close();
		}	catch (IOException e) {
			
		}
	}
}
