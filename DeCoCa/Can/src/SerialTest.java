import java.awt.FlowLayout;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

public class SerialTest implements SerialPortEventListener {
	public static String ss;
	public static DataOutputStream bos = null;
	private SerialPort serialPort;
	private CommPortIdentifier portIdentifier;
	private CommPort commPort;
	private BufferedInputStream bin;
	private InputStream in;
	private OutputStream out;
	static FrameExam fe;
	static String data;
	static String[] status = { "", "012", "345", "67", "89", "1", "0", "1", "1", "999", "888", "99", "88"};
	Socket socket;
	boolean rflag = true;
	static SerialTest st;

	public SerialTest() {
	}

	public SerialTest(String portName) throws NoSuchPortException {
		portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
		System.out.println("Connect ComPort!");
		try {
			connectSerial();
			System.out.println("Connect OK !!");
			(new Thread(new SerialWriter())).start();
		} catch (Exception e) {
			System.out.println("Connect Fail !!");
			e.printStackTrace();
		}
	}

	public void setPort(String portName) throws NoSuchPortException {
		portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
		System.out.println("Connect ComPort!");
		try {
			connectSerial();
			System.out.println("Connect OK !!");
			(new Thread(new SerialWriter())).start();
		} catch (Exception e) {
			System.out.println("Connect Fail !!");
			e.printStackTrace();
		}
	}

	private class SerialWriter implements Runnable {
		String data;

		public SerialWriter() {
			// �� �����Ұ� �޼�����.
			// �̰� �� ���ָ� �� ��.
			this.data = ":G11A9\r";
		}

		public SerialWriter(String serialData) {
			/*
			 * W28: �����͸� ��ڴٴ� �� W28 00000000 000000000000 id data :W28 00000000 000000000000
			 * 53 checksum \r
			 */
			String sdata = sendDataFormat(serialData);
			System.out.println(sdata);
			this.data = sdata;
		}

		public String sendDataFormat(String serialData) {
			serialData = serialData.toUpperCase();
			System.out.println(serialData + "@@");
			char c[] = serialData.toCharArray();
			int cdata = 0;
			for (char cc : c) {
				cdata += cc;
			}
			// ��Ʈ����
			System.out.println("before 0xff : " + cdata);
			cdata = (cdata & 0xFF);
			System.out.println("after 0xff : " + cdata);
			String returnData = ":";
			returnData += serialData + Integer.toHexString(cdata).toUpperCase();
			returnData += "\r";
			return returnData;
		}

		public void run() {
			try {
				byte[] inputData = data.getBytes();
				out.write(inputData);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void connectSerial() throws Exception {
		// �ٸ� ���̰� ���� �ִ�.
		if (portIdentifier.isCurrentlyOwned()) {
			System.out.println("Error: Port is currently in use");
		} else {
			commPort = portIdentifier.open(this.getClass().getName(), 100);
			if (commPort instanceof SerialPort) {
				serialPort = (SerialPort) commPort;
				serialPort.addEventListener(this);
				serialPort.notifyOnDataAvailable(true);
				serialPort.setSerialPortParams(921600, // ��żӵ�(Serial �ӵ�) ���� �޶� ��� ����.
						SerialPort.DATABITS_8, // ������ ��Ʈ
						SerialPort.STOPBITS_1, // stop ��Ʈ
						SerialPort.PARITY_NONE); // �и�Ƽ
				// �����ϴ� �����͸� �����ϰڴٴ� ����.
				in = serialPort.getInputStream();
				bin = new BufferedInputStream(in);
				// �����͸� Serial�� �� �� ����.
				out = serialPort.getOutputStream();

			} else {
				System.out.println("Error: Only serial ports are handled by this example.");
			}
		}
	}

	public void sendData(String data) {
		SerialWriter sw = new SerialWriter(data);
		new Thread(sw).start();
	}

	public static void main(String[] args) {
		fe = new FrameExam();
		
		Runnable r = new Runnable() {
			@Override
			public void run() {
				try {
					st = new SerialTest("COM12");
					while (true) {
						st.sendData("W28" + statustoString());
						fe.tx1.setText(status[1]);
						fe.tx2.setText(status[2]);
						fe.tx3.setText(status[3]);
						fe.tx4.setText(status[4]);
						fe.tx5.setText(status[5]);
						fe.tx6.setText(status[6]);
						fe.tx7.setText(status[7]);
						fe.tx8.setText(status[8]);
						Thread.sleep(1500);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		Thread t = new Thread(r);
		t.start();

	}

	public static String statustoString() {
		String str = "";
		for (int i = 1; i < 13; i++) {
			str += status[i];
		}
		return str;
	}

	@Override
	public void serialEvent(SerialPortEvent event) {
		switch (event.getEventType()) {
		case SerialPortEvent.BI:
		case SerialPortEvent.OE:
		case SerialPortEvent.FE:
		case SerialPortEvent.PE:
		case SerialPortEvent.CD:
		case SerialPortEvent.CTS:
		case SerialPortEvent.DSR:
		case SerialPortEvent.RI:
		case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
			break;
		case SerialPortEvent.DATA_AVAILABLE:
			byte[] readBuffer = new byte[128];
			try {
				while (bin.available() > 0) {
					int numBytes = bin.read(readBuffer);
				}
				ss = new String(readBuffer);
				boolean check = checkSerialData(ss);
				System.out.println("ResultCheck:" + check + "||");
				System.out.println("Receive Low Data:" + ss + "||");
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}
	}

	public boolean checkSerialData(String data) {
		boolean check = false;
		// :U2800000050000000000000002046
		String checkData = data.substring(1, 28);
		checkData = data.substring(1, 28);
		String checkSum = data.substring(28, 30);
		if (data.charAt(1) == 'U') {
			for (int i = 1; i <= 2; i++) {
				status[i] = data.substring(i * 3 + 1, i * 3 + 4);
			}
			for (int i = 1; i <= 2; i++) {
				status[i + 2] = data.substring(i * 2 + 8, i * 2 + 10);
			}
			for (int i=1;i<=4;i++) {
				status[i+4]=data.substring(i + 13,i+1 + 13);
			}
			for (int i = 1; i <= 2; i++) {
				status[i+8] = data.substring(i * 3 + 15, i * 3 + 18);
			}
			for (int i = 1; i <= 2; i++) {
				status[i + 10] = data.substring(i * 2 +22, i * 2 + 24);
			}
		}
		char c[] = checkData.toCharArray();
		int cdata = 0;
		for (char cc : c) {
			cdata += cc;
		}
		cdata = (cdata & 0xFF);
		String serialCheckSum = Integer.toHexString(cdata).toUpperCase();
		if (serialCheckSum.trim().equals(checkSum)) {
			check = true;
		}
		return check;
	}
}

class FrameExam extends JFrame {
	JLabel lbl;
	JLabel la1,la2,la3,la4,la5,la6,la7,la8,la9,la10,la11,la12;
	JTextField tx1,tx2,tx3,tx4,tx5,tx6,tx7,tx8,tx9,tx10,tx11,tx12;
	JPanel statuspanel, panel;
	JTextArea content;
	String[] status;

	public void changeStat() {
		tx1.setText(status[0]);
		tx2.setText(status[1]);
		tx3.setText(status[2]);
		tx4.setText(status[3]);
		tx5.setText(status[4]);
		tx6.setText(status[5]);
		tx7.setText(status[6]);
		tx8.setText(status[7]);
	}

	public void setStatus(String[] stat) {
		this.status = stat;
	}

	public FrameExam() {
		super("���� status");
		// FlowLayout���
		setLayout(new FlowLayout());
		// Border�� ���� ����
		EtchedBorder eborder = new EtchedBorder();
		// ���̺� ����
		lbl = new JLabel("                ���� �����Դϴ�.              ");
		// ���̺� ���� �����
		lbl.setBorder(eborder);
		// �г��߰�
		statuspanel = new JPanel();
		// ���̺� �߰�
		statuspanel.add(lbl);
		add(statuspanel);
		////////////////////////////////////////////////
		// id�гΰ� pw �гλ���
		panel = new JPanel();
		la1 = new JLabel("���͸�");
        la2 = new JLabel("�ڵ�����");
        la3 = new JLabel("�ӵ�");
        la4 = new JLabel("Ÿ�̾� ����� �տ�");
        la5 = new JLabel("Ÿ�̾� ����� �տ�");
        la6 = new JLabel("Ÿ�̾� ����� �޿�");
        la7 = new JLabel("Ÿ�̾� ����� �޿�");
        la8 = new JLabel("���οµ�");
        la9 = new JLabel("������");
        la10 = new JLabel("������Ʈ");
        la11 = new JLabel("�극��ũ");
        la12 = new JLabel("����");
        
        // id�ؽ�Ʈ�ʵ�� pw�ؽ�Ʈ �ʵ� ����
        tx1 = new JTextField(20);
        tx2 = new JTextField(20);
        tx3 = new JTextField(20);
        tx4 = new JTextField(20);
        tx5 = new JTextField(20);
        tx6 = new JTextField(20);
        tx7 = new JTextField(20);
        tx8 = new JTextField(20);
        tx9 = new JTextField(20);
        tx10 = new JTextField(20);
        tx11 = new JTextField(20);
        tx12 = new JTextField(20);
        tx1.setText("���͸�");
        add(la1); add(tx1);
        add( la2 ); add( tx2 );
        add( la3 ); add( tx3 );
        add( la4 ); add( tx4 );
        add( la5 ); add( tx5 );
        add( la6); add( tx6 );
        add( la7); add( tx7 );
        add( la8 ); add( tx8 );
        add( la9 ); add( tx9 );
        add( la10 ); add( tx10 );
        add( la11 ); add( tx11 );
        add( la12 ); add( tx12 );
		setSize(250, 700);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
}