import java.io.*;
import java.net.*;

public class QuizClient {
	public static void main(String[] args) {
		String strServerIP = "127.0.0.1";
		int nServerPort = 7779;
		int nDelayTime = 1000;
		InetSocketAddress isaSvr = null;
		Socket socket = null;
		PrintWriter out = null;
		
		try {
			socket = new Socket();
			System.out.println("서버와 연결 시도");
			isaSvr = new InetSocketAddress(strServerIP, nServerPort);
			socket.connect(isaSvr, nDelayTime);
			System.out.printf("연결이 성공[%s] [%d]\n", strServerIP, nServerPort);
			while(true) {
			String a = "Hello";

			out = new PrintWriter(socket.getOutputStream(), true);
			out.println(a);
			Thread.sleep(1000);
			out.println("This is hs");
			}
		}	catch(Exception e) {
			
		}
		if(!socket.isClosed()) {
			try {
				socket.close();
			}	catch(IOException e1) {
				System.out.println("예외발생: "+e1.getLocalizedMessage());
			}
		}
	}
}
