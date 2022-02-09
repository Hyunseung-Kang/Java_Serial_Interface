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
	
	public static String byteArrayToHexaString(byte[] bytes) {
		StringBuilder builder = new StringBuilder();
		for(byte data:bytes) {
			//System.out.print("data before: "+data);
			//System.out.print("\tdata: "+String.format("%02X", data));
			builder.append(String.format("%02X ", data));
		}
		return builder.toString();
	}
	
	@Override
	public void run() {
		
		byte[] call = {(byte)0x5a, 0x04, 0x05, 0x00, (byte)0xfe};
		
		try {
			while(!stop) {
				for(int i=0; i<5; i++) {
					out.write(call[i]);
				}
				Thread.sleep(100);
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}