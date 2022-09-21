package rwcom;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;


import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

public class RWCom extends Frame{
    
    private static TextArea output = new TextArea("Output:");
    private static int countDevs = 0;
    private static ArrayList<String> devList = new ArrayList();
    
	public RWCom() {
		super("RWCom");
		
		setSize(400,230);
                setLocation(300,300);
		
		Button myButton = new Button("Press");
		add(output, BorderLayout.NORTH);	
		add(myButton, BorderLayout.SOUTH);
                
                countDevs = controllers.countDev();
                if (countDevs==0) {
                    System.out.println("Can't find devices /dev/ttyMXUSB");
                    System.exit(0);
                }
                for (int i=0; i<countDevs;i++) {
                    devList.add("/dev/ttyMXUSB" + i);
                    System.out.println(devList.get(i));
                }
                
		myButton.addActionListener((ActionEvent e) -> {
                    for (String str : devList) {                     
                        serialPort = new SerialPort(str);
                        try {
                            serialPort.openPort();
                            serialPort.setParams(SerialPort.BAUDRATE_115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
                            serialPort.setEventsMask(SerialPort.MASK_RXCHAR);
                            serialPort.writeBytes("get_rio_ga -v rioGA\r\n".getBytes());
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException ex) {                         
                            }
                            serialPort.writeBytes("riogainfo $rioGA\r\n".getBytes());
                            serialPort.addEventListener(new EventListener());
                            serialPort.closePort();
                        }
                        catch (SerialPortException ex) {
                            System.out.println(ex);
                        }
                    }
                });
	}
	private static SerialPort serialPort;

    public static void main(String[] args){
    	RWCom log = new RWCom();
		log.setVisible(true);
		
		log.addWindowListener(new WindowAdapter() {
                        @Override
			public void windowClosing(WindowEvent e) {
				 e.getWindow().dispose();
		}		
	});
    }

    private static class EventListener implements SerialPortEventListener  {
        @Override
        public void serialEvent(SerialPortEvent event) {
            if(event.isRXCHAR() && event.getEventValue() > 0){
                try {
                    String data = serialPort.readString(event.getEventValue());
                    output.setText(data);
                    System.out.print(data);
                }
                catch (SerialPortException ex) {
                    System.out.println(ex);
                }
            }
        }
    }
}