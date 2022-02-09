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

	public ArrayList get_port_names(){
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
	
	public void disconnect() {
		commPort.close();
		reading.setStop(true);
		writing.setStop(true);
	}
	
	
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
				/////// 설정부분에서 InputStream에 대한 어떠한 정의가 추가적으로 필요하지 않을까?
				
				in = serialPort.getInputStream();
				//input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
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
