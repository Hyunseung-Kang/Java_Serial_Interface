import java.awt.BorderLayout;  
import java.awt.Color;  
import java.io.IOException;
import java.util.ArrayList;  
import java.util.LinkedList;

import javax.swing.JPanel;   
import javax.swing.*;	//토글버튼

import org.jfree.chart.ChartFactory;  
import org.jfree.chart.ChartPanel;  
import org.jfree.chart.JFreeChart;  
import org.jfree.chart.plot.PlotOrientation;  
import org.jfree.chart.plot.XYPlot;  
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;  
import org.jfree.data.xy.XYSeries;  
import org.jfree.data.xy.XYSeriesCollection;

import Serial_Interface.Serial_Com;
public class GUI extends javax.swing.JFrame{
	
	// 아래 serialVersionUID 값을 부여하지 않을 경우 컴파일러가 계산한 값을 부여한다.
	// 그리고 경우에 따라 제대로 객체를 불러올 수 없는 문제가 발생할 수 있으므로 이를 지정해주는 습관을 들이자.
	private static final long serialVersionUID = 1L;

	static double sensor1_thr = 1.2;		// Client에서 서버로 데이터를 전송하는 임계값
	static double sensor2_thr = 0.7;
	static int data_num = 50;				// GUI에 그래프를 나타내기 위한 데이터 수
	
	// GUI의 컴포넌트 선언
	public javax.swing.JButton Clear;
	public Serial_Interface.Serial_Com port1, port2;
	private ArrayList<String> serial_port_names;
	private String[] ComPort_List;
	private String Sensor1_Port, Sensor2_Port;
	private JToggleButton Sensor1_Connect;
	private JToggleButton Sensor2_Connect;
	private JToggleButton Server_Connect;
	private javax.swing.JTextField ip_addr;
	private javax.swing.JTextArea ImGraph;
	private javax.swing.JTextArea jTextArea2; 
	public javax.swing.JPanel graph; 
	private javax.swing.JLabel IP_label;
	private javax.swing.JLabel SERVER;  
	private javax.swing.JLabel SENSOR1;  
	private javax.swing.JLabel SENSOR2;  
	private javax.swing.JScrollPane ImgPane;
	private javax.swing.JScrollPane TextPane;
	private javax.swing.JTabbedPane jTabbedPane1;
	private Client client;
	private javax.swing.JComboBox<String> port1List;
	private javax.swing.JComboBox<String> port2List;
    private boolean sensor1_stop;
    private boolean sensor2_stop;
	private XYSeriesCollection dataset;
	private JFreeChart chart;
	private ChartPanel ch;
	private XYSeries sensor1_graph;
	private XYSeries sensor2_graph;
	private Thread sensor1_thread;
	private Thread sensor2_thread;
	private LinkedList<Float> sensor1_fifo;
	private LinkedList<Float> sensor2_fifo;
	
	
	
	@Override
	public String getTitle() {
		return ("External Device Monitoring");
	}
	
	// 생성자 구성
	public GUI() {
		initComponents();
		graph_init();
	}
    
	
	// Graph panel 초기화
	private void graph_init() {
		dataset = new XYSeriesCollection();  
		chart = ChartFactory.createXYLineChart("", "Time", "Data", dataset, PlotOrientation.VERTICAL, true, true, false);
		ch = new ChartPanel(chart);  
		sensor1_graph = new XYSeries("Sensor1");  
	    sensor2_graph = new XYSeries("Sensor2");
	    sensor1_thread = new get_sensor1_data();
	    sensor2_thread = new get_sensor2_data();
		sensor1_fifo = new LinkedList<Float>();
		sensor2_fifo = new LinkedList<Float>();
		queue_initialize(sensor1_fifo, sensor2_fifo);
		
		graph.setLayout(new BorderLayout());   
		JPanel topPanel = new JPanel();  
		graph.add(topPanel,BorderLayout.NORTH );
		dataset.addSeries(sensor1_graph);
		dataset.addSeries(sensor2_graph);
		
		graph.add(new ChartPanel(chart),BorderLayout.BEFORE_LINE_BEGINS);
		     XYPlot xyPlot = chart.getXYPlot();  
		     xyPlot.setDomainGridlinePaint(Color.red);  
		     xyPlot.setRangeGridlinePaint(Color.blue);  
		     xyPlot.setBackgroundPaint(Color.white);
		     org.jfree.chart.axis.ValueAxis rangeAxis = xyPlot.getRangeAxis();  
		     XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();  
		     renderer.setSeriesLinesVisible(1, true);  
		     renderer.setSeriesShapesVisible(1, false);
		     renderer.setSeriesLinesVisible(0, true);  
		     renderer.setSeriesShapesVisible(0, false);
		     xyPlot.setRenderer(renderer);
		     rangeAxis.setAutoRange(true);
		     rangeAxis.setAutoRangeMinimumSize(0.1);
		     graph.setVisible(true); 
	}
	
	// Clear버튼 입력시 그래프 초기화
	private void queue_initialize(LinkedList<Float> queue1, LinkedList<Float> queue2) {
		queue1.clear();
		queue2.clear();
		for(int i=0; i<data_num; i++) {
			queue1.add(0.0f);
			queue2.add(0.0f);
		}
	}
	
	// Sensor1의 시리얼통신을 통해 전달받은 data를 도식화
	class get_sensor1_data extends Thread{
		static double prev_data = 0.0;
		static int X = 0;
		static double Y_1=0;
		public void run() {
			while(!sensor1_stop) {
				float data = port1.reading.value;
				if (data > sensor1_thr && prev_data != data) {
					client.send_data(data, "sensor1");
					prev_data = data;
				}
				System.out.println("sensor1: "+data);
				sensor1_fifo.removeFirst();
				sensor1_fifo.add(data);
				
				float[] array = new float[sensor1_fifo.size()];
				sensor1_graph.clear();
				for(int i=0; i<sensor1_fifo.size(); i++) {
					array[i] = sensor1_fifo.get(i);
					X=i;
					Y_1 = sensor1_fifo.get(i);
					sensor1_graph.add(X, Y_1);
					ch.repaint();
				}
				System.out.println("");
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			     graph.setVisible(true); 
			}
		}
	}
	
	// Sensor2의 시리얼통신을 통해 전달받은 data를 도식화
	class get_sensor2_data extends Thread{
		static double prev_data = 0.0;
		static int X = 0;
		static double Y_2=0;
		public void run() {
			while(!sensor2_stop) {
				float data = port2.reading.value;
				if (data > sensor2_thr && prev_data != data) {
					client.send_data(data, "sensor2");
					prev_data = data;
				}
				
				System.out.println("sensor2 data: "+data);
				sensor2_fifo.removeFirst();
				sensor2_fifo.add(data);
				
				float[] array = new float[sensor2_fifo.size()];
				sensor2_graph.clear();
				for(int i=0; i<sensor2_fifo.size(); i++) {
					array[i] = sensor2_fifo.get(i);
					X=i;
					Y_2 = sensor2_fifo.get(i);
					sensor2_graph.add(X, Y_2);
					ch.repaint();
				}
				System.out.println("");
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			     graph.setVisible(true);  
			}
		}
	}
	
	// GUI 컴포넌트 초기화
	@SuppressWarnings("unchecked")
	private void initComponents() {
		SERVER = new javax.swing.JLabel();
		SENSOR1 = new javax.swing.JLabel();
		SENSOR2 = new javax.swing.JLabel();
		
		IP_label = new javax.swing.JLabel();
		client = new Client();
		
	    Sensor1_Connect = new JToggleButton();
	    Sensor2_Connect = new JToggleButton();
	    Server_Connect = new JToggleButton();
		Clear = new javax.swing.JButton();
		
	    port1 = new Serial_Com();
	    port2 = new Serial_Com();
	    
	    ip_addr = new javax.swing.JTextField();  
	    ip_addr.setText("127.0.0.1");
	      
	    port1List = new javax.swing.JComboBox<>();  
	    port2List = new javax.swing.JComboBox<>();   
	    
	    jTabbedPane1 = new javax.swing.JTabbedPane();
	    ImgPane = new javax.swing.JScrollPane();  
	    TextPane = new javax.swing.JScrollPane();

	    ImGraph = new javax.swing.JTextArea();
	    jTextArea2 = new javax.swing.JTextArea();
	 
	    graph = new javax.swing.JPanel();

	    setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);  
	    setBackground(new java.awt.Color(204, 204, 204));  

	    SERVER.setFont(new java.awt.Font("Tahoma", 1, 18)); 
	    SERVER.setForeground(new java.awt.Color(0, 0, 0));  
	    SERVER.setText("SERVER");  
	    SENSOR1.setFont(new java.awt.Font("Tahoma", 1, 18));
	    SENSOR1.setForeground(new java.awt.Color(0, 0, 0));  
	    SENSOR1.setText("Sensor1");  
	    SENSOR2.setFont(new java.awt.Font("Tahoma", 1, 18));
	    SENSOR2.setForeground(new java.awt.Color(0, 0, 0));  
	    SENSOR2.setText("Sensor2");

	    IP_label.setFont(new java.awt.Font("Tahoma", 1, 14));
	    IP_label.setForeground(new java.awt.Color(0, 0, 0));  
	    IP_label.setText("IP");
	    
	    
	    // 버튼에 대한 정의 및 이벤트 처리함수 정의
	    Sensor1_Connect.setFont(new java.awt.Font("Tahoma", 0, 14));
	    Sensor1_Connect.setText("Connect1");  
	    Sensor1_Connect.addActionListener(new java.awt.event.ActionListener() {  
	       public void actionPerformed(java.awt.event.ActionEvent evt) {  
	         Sensor1_Connect_Performed(evt);
	       }
	    });
	    
	    Sensor2_Connect.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N  
	    Sensor2_Connect.setText("Connect2");  
	    Sensor2_Connect.addActionListener(new java.awt.event.ActionListener() {  
	       public void actionPerformed(java.awt.event.ActionEvent evt) {  
	         Sensor2_Connect_Performed(evt);  
	       }  
	     });
	    
	    Server_Connect.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N  
	    Server_Connect.setText("Connect Server");  
	    Server_Connect.addActionListener(new java.awt.event.ActionListener() {  
	       public void actionPerformed(java.awt.event.ActionEvent evt) {  
	         Server_Connect_Performed(evt);  
	       }  
	     });
	    
	    Clear.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N  
	    Clear.setText("CLEAR");  
	    Clear.addActionListener(new java.awt.event.ActionListener() {  
	      public void actionPerformed(java.awt.event.ActionEvent evt) {  
	         ClearActionPerformed(evt);  
	       }  
	    });  
	    
	    ip_addr.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));  
	    ip_addr.addActionListener(new java.awt.event.ActionListener() {  
	    	public void actionPerformed(java.awt.event.ActionEvent evt) {  
	    	}
	     });  
	     
	     port1List.addActionListener(new java.awt.event.ActionListener() {  
	    	 public void actionPerformed(java.awt.event.ActionEvent evt) {  
		         port1ListActionPerformed(evt);  
		       }  
		     });
	     port2List.addActionListener(new java.awt.event.ActionListener() {
		       public void actionPerformed(java.awt.event.ActionEvent evt) {
		         port2ListActionPerformed(evt);
		       }
		     });
	     
	     
	     // 그래프를 띄우기 위한 panel 및 그래프 정의
	     jTabbedPane1.setTabPlacement(javax.swing.JTabbedPane.RIGHT);  
	     jTabbedPane1.setFont(new java.awt.Font("Tahoma", 0, 14));
	     ImGraph.setColumns(20);  
	     ImGraph.setFont(new java.awt.Font("Tahoma", 0, 14));
	     ImGraph.setRows(5);
	     
	     ImgPane.setViewportView(ImGraph);  
	     
	     javax.swing.GroupLayout graphLayout = new javax.swing.GroupLayout(graph);  
	     graph.setLayout(graphLayout);  
	     graphLayout.setHorizontalGroup(  
	       graphLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)  
	       .addGap(0, 683, Short.MAX_VALUE)  
	     );  
	     graphLayout.setVerticalGroup(  
	       graphLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)  
	       .addGap(0, 573, Short.MAX_VALUE)  
	     );
	     jTabbedPane1.addTab("Plot", graph);  
	     
	     
	     // 시리얼 통신으로 연결된 COM PORT가 없을 시 출력할 항목 정의
	     port1List.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Sensor1", "COMPORT1" }));
	     port2List.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Sensor2", "COMPORT2" }));
	     
	     
	     jTextArea2.setBackground(new java.awt.Color(255, 243, 21));  
	     jTextArea2.setColumns(20);  // Area에 가로/세로 몇개의 텍스트를 작성할 수 있는지 설정
	     jTextArea2.setRows(5);  
	     
	     TextPane.setViewportView(jTextArea2);
	     
	     javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());  
	     getContentPane().setLayout(layout);
	     layout.setAutoCreateGaps(true);
	     
	     // 리스트박스의 구성 추가
	     serial_port_names = port1.get_port_names();
	     ComPort_List = new String[serial_port_names.size()];
	     for(int i=0; i<serial_port_names.size(); i++) {
	    	 ComPort_List[i] = serial_port_names.get(i);
	     }
	     port1List.setModel(new javax.swing.DefaultComboBoxModel<>(ComPort_List));
	     port2List.setModel(new javax.swing.DefaultComboBoxModel<>(ComPort_List));
	     
	     
	     
	     
	     // 컴포넌트의 레이아웃 정의
	     layout.setHorizontalGroup(  
	       layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING) 
	       .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
	    		   .addComponent(jTabbedPane1)	  		       
	  	           .addGroup(layout.createSequentialGroup()
	  	        		 .addComponent(IP_label)
	  	        		 .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	  	        				 .addComponent(SERVER, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
	  	    	        		 .addComponent(ip_addr, 150, 150, 150)
	  	    	        		 .addComponent(SENSOR1)
	  	    	        		 .addComponent(port1List, 100, 100, 100)
	  	    	        		 .addComponent(Sensor1_Connect, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
	  	    	        		 .addComponent(Clear, 150, 150, 150)
	  	    	        		 )
	  	        		 .addGap(20, 20, 20)
		        		 .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
		        				 .addComponent(Server_Connect, 150, 150, 150)
		        				 .addComponent(SENSOR2, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
		        				 .addComponent(Sensor2_Connect, 100, 100, 100)
		        				 .addComponent(port2List, 100, 100, 100))
		        		 .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
		        		 	.addGap(20, 20, 20))
		        		 )
    		 )
	         .addComponent(TextPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 950, Short.MAX_VALUE) 
	     );
	     
	     layout.setVerticalGroup(
	    		 layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	    		 .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
	    				 .addGap(50, 50, 50)
	    		         .addComponent(SERVER, 40, 40, 40)
	    		         .addGap(15, 15, 15)
	    		         .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	    		         .addComponent(ip_addr, 30, 30, 30)
	    		         .addComponent(IP_label)
	    		         .addComponent(Server_Connect))
	    		         .addGap(50, 50, 50)
	    		         .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	    		        		 .addComponent(SENSOR1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)  
	    		    	         .addComponent(SENSOR2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
	    		    	         .addGap(10, 10, 10)
	    		    	         .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	    		    	        		 .addComponent(port1List, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
	    		    	    	         .addComponent(port2List, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
	    		    	    	         .addGap(10, 10, 10)
	    		    	    	         .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	    		    	    	        		 .addComponent(Sensor2_Connect)
	    		    	    	    	         .addComponent(Sensor1_Connect))
	    		    	    	    	         .addGap(70, 70, 70)
	    		    	    	    	         .addComponent(Clear, 50, 50, 50))
	    		 .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING)  
	  	         .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	  	        		.addComponent(TextPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 578, Short.MAX_VALUE))
	  	         );
	     pack();  
	   }// </editor-fold>//GEN-END:initComponents  


	
	
	// 이벤트 처리함수 구현
	
	   private void Sensor1_Connect_Performed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RunActionPerformed
		   Sensor1_Port = (String) port1List.getSelectedItem();
		   if(Sensor1_Connect.isSelected() && (Sensor1_Port != Sensor2_Port)) {
			   try {
				   port1.connect(Sensor1_Port);
				   sensor1_stop = false;
				   sensor1_thread = new get_sensor1_data();
				   sensor1_thread.start();
			   }	catch(Exception e) {
				   Sensor1_Connect.setSelected(false);
				   e.printStackTrace();
			   }
		   }
		   else {
			   Sensor1_Connect.setSelected(false);
			   sensor1_stop = true;
			   port1.disconnect();
		   }
	   }//GEN-LAST:event_Sensor1_Connect_Performed
	   private void Sensor2_Connect_Performed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RunActionPerformed 
		   Sensor2_Port = (String) port2List.getSelectedItem();
		   if(Sensor2_Connect.isSelected() && (Sensor2_Port != Sensor1_Port)) {
			   try {
				   port2.connect(Sensor2_Port);
				   sensor2_stop = false;
				   sensor2_thread = new get_sensor2_data();
				   sensor2_thread.start();
			   }	catch(Exception e) {
				   Sensor2_Connect.setSelected(false);
				   e.printStackTrace();
			   }
		   }
		   else {

			   Sensor2_Connect.setSelected(false);
			   sensor2_stop = true;
			   port2.disconnect();
		   }
	   }//GEN-LAST:event_Sensor2_Connect_Performed
	   private void Server_Connect_Performed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RunActionPerformed  
		   if(Server_Connect.isSelected()) {
			   String ip_address = ip_addr.getText();
			   try {
				   client.connect_to_server(ip_address);
			   }	catch(IOException e) {
				   Server_Connect.setSelected(false);
					e.printStackTrace();
			   }
		   }
	   }//GEN-LAST:event_Server_Connect_Performed

	   private void ClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ClearActionPerformed  
		 sensor1_graph.clear();
		 sensor2_graph.clear();

		 queue_initialize(sensor1_fifo, sensor2_fifo);
	     graph.setVisible(true);  
	   }//GEN-LAST:event_ClearActionPerformed  

	   private void port1ListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_portListActionPerformed

		   }//GEN-LAST:event_port1ListActionPerformed  
	   private void port2ListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_portListActionPerformed  
		     // TODO add your handling code here:  
		   }//GEN-LAST:event_port2ListActionPerformed  


	     
	   public static void main(String args[]) {  

	     /* Create and display the form */  
	     java.awt.EventQueue.invokeLater(new Runnable() {  
	       public void run() {
	    	   new GUI().setVisible(true);
	       }  
	     });  
	   }
}
