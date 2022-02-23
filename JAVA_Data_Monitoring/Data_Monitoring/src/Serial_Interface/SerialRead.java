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
				
				// 8바이트의 데이터를 디바이스로부터 수신
				byte[] buffer = new byte[8];
				
				// 4바이트의 헤더, 4바이트의 유효데이터
				byte[] data = new byte[4];
	
				if(this.in.read(buffer)==8) {
					// 헤더 확인을 통해 유요한 데이터 구분
					if(((buffer[0]&0xff) == 90)&&((buffer[1]&0xff) == 4) 
							&&((buffer[2]&0xff)==5) && ((buffer[3]&0xff)==0)) {
						for(int i=0; i<4; i++) {
							data[3-i] = buffer[i+4];
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
