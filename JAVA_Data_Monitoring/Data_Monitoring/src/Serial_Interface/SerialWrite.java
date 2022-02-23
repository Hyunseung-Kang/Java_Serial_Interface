package Serial_Interface;

import java.io.IOException;
import java.io.OutputStream;


public class SerialWrite implements Runnable{
	
	OutputStream out;
	
	public SerialWrite(OutputStream out) {
		this.out = out;		
	}
	
	private boolean stop;
	public void setStop(boolean stop) {
		this.stop = stop;
	}
	
	
	@Override
	public void run() {
		
		// 디바이스에 데이터를 요청
		byte[] call = {(byte)0x5a, 0x04, 0x05, 0x00};
		
		try {
			while(!stop) {
				for(int i=0; i<4; i++) {
					out.write(call[i]);
				}
				Thread.sleep(100);
			}
		}
		catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

}
