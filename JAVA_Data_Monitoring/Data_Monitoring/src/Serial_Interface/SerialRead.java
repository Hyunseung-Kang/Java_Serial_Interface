package Serial_Interface;


import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class SerialRead implements Runnable{
	InputStream in;
	
	
	public SerialRead(InputStream in) {
		this.in = in;
	}
	
	public static double convertToDouble(byte[] array) {
		ByteBuffer buffer = ByteBuffer.wrap(array);
		return buffer.getDouble();
	}
	
	private boolean stop;
	public float value;
	public void setStop(boolean stop) {
		this.stop = stop;
		System.out.println("STOPSTOP");
	}
	
	
	@Override
	public void run() {
		try {
			
			while(!stop) {
				
				byte[] buffer = new byte[8];
				byte[] data = new byte[4];
	
				if(this.in.read(buffer)==8) {
					if(((buffer[0]&0xff) == 170)&&((buffer[1]&0xff) == 255) 
							&&((buffer[7]&0xff)==187) && ((buffer[6]&0xff)==187)) {
						for(int i=0; i<4; i++) {
							data[3-i] = buffer[i+2];
						}
					}
					int intvalue= 0;
					for(byte b:data) {
						intvalue = (intvalue<<8)+(b&0xFF);
					}
					value = Float.intBitsToFloat(intvalue);
				}
				Thread.sleep(100);
			}
		}
		catch (IOException|InterruptedException e) {
			e.printStackTrace();
		}
	}
}
