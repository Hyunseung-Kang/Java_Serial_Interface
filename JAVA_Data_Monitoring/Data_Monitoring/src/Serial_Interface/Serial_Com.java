package Serial_Interface;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.ArrayList;


public class Serial_Com {
	public Serial_Com() {
		super();
	}

	private CommPort commPort;
	private InputStream in;
	private OutputStream out;
	public SerialRead reading;
	public SerialWrite writing;
	
	// 시리얼 통신으로 연결된 COM_PORT의 항목을 얻기 위한 함구 구현
	public ArrayList<String> get_port_names(){
		Enumeration ports = CommPortIdentifier.getPortIdentifiers();
		ArrayList port_names = new ArrayList();
		while(ports.hasMoreElements()) {
			CommPortIdentifier port = (CommPortIdentifier)ports.nextElement();
			String type;
			switch(port.getPortType()) {
			case CommPortIdentifier.PORT_PARALLEL:
				break;
			case CommPortIdentifier.PORT_SERIAL:
				port_names.add(port.getName());
				System.out.println("PORT NAME: "+port.getName());
				break;
			default:
				break;
			}
		}
		return port_names;
	}
	
	// 시리얼 통신을 종료하기 위한 함수 구현
	public void disconnect() {
		commPort.close();
		reading.setStop(true);
		writing.setStop(true);
	}
	
	// 시리얼 통신을 연결하기 위한 함수 구현
	public void connect(String portName) throws Exception{
		CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
		if(portIdentifier.isCurrentlyOwned()) {
			System.out.println("Error: Port is currently in use");
		}
		else {
			commPort = portIdentifier.open(this.getClass().getName(),2000);
			//// this.getClass().getName()은 해당 클래스의 이름을 출력하며 여기에선 Serial 출력
			System.out.println("this.getClass().getName(): "+this.getClass().getName());
			System.out.println("commPort: "+commPort);
			if(commPort instanceof SerialPort) {
				SerialPort serialPort = (SerialPort) commPort;
				serialPort.setSerialPortParams(9600,  SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
				
				in = serialPort.getInputStream();
				out = serialPort.getOutputStream();
				
				reading = new SerialRead(in);
				writing = new SerialWrite(out);
				new Thread(reading).start();
				new Thread(writing).start();
			}
			else {
				System.out.println("Error: Only Serial Ports are handled by this example");
			}
		}
	}
	
}
