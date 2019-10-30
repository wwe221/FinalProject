package passive.dispatch;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Queue;

public class Dispatch {
	static Queue<Integer> q = new LinkedList<>();
	public static void main(String[] args) {
		int n=0;
		while (true) {
			System.out.println(n+"ȸ ���� ");
			try {
				Class.forName("oracle.jdbc.driver.OracleDriver");
				// Oracle DB�� ���� �� ���̴�! ����
			} catch (ClassNotFoundException e) {
				System.out.println("DRiver Loading err");
			}
			SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat format2 = new SimpleDateFormat("HH");
			SimpleDateFormat format3 = new SimpleDateFormat("mm");
			Calendar time = Calendar.getInstance();
			String today = format1.format(time.getTime());
			String hour = format2.format(time.getTime());
			String minute = format3.format(time.getTime());
			System.out.println(today + " " + hour + ":" + minute);
			int h = Integer.parseInt(hour);
			int a = minute.charAt(0) - '0' + 2;
			int up = a / 6;
			a %= 6;
			if (up >= 1) {
				h += 1;
				h %= 24;
			}
			String t = "'" + h + ":" + a + "%'";
			System.out.println(t);
			// 2. Connection 1521:��Ʈ workspace = db, pwd = db
			String id = "db";
			String pwd = "db";
			String url = "jdbc:oracle:thin:@70.12.60.94:1521:xe";
			Connection con = null;
			try {
				con = DriverManager.getConnection(url, id, pwd);
			} catch (SQLException e) {
				System.out.println("Connection err");
				System.out.println(e.getMessage());
			}
			// 3. SQL ����
			String sql = "Select * from RESERVATION where caldate ='" + today + "' and stime like " + t
					+ " and carid = 0 order by stime";
			// ���� ��¥�� 20����~ 30�� �� ���̿� ����ؾ� �ϴ� ������ �̾�
			PreparedStatement pstmt = null;
			// pstmt �� sql�� �����ؼ� return �� �޾ƿü� �ִ� �༮
			ResultSet rset = null;
			try {
				pstmt = con.prepareStatement(sql);
				pstmt.executeUpdate();
				rset = pstmt.executeQuery(); // pstmt�� return �ޱ�
				while (rset.next()) {
					int uid = rset.getInt("CALID");
					if (!q.contains(uid)) {
						q.add(uid);
					}
					String caldate = rset.getString("CALDATE");
					String stime = rset.getString("STIME");
					System.out.println(uid +"::"+ caldate + stime);
				}
				// ���⼭ Ư�� url�� ������ ������ thread�� ���� ��Ų��.
				Runnable r = new Runnable() {
					@Override
					public void run() {
						while (!q.isEmpty()) {
							URL uurl = null;
							URLConnection urlc = null;
							int calid = q.poll();
							String strUrl = "http://70.12.60.110/DeCoCa/allocation.mc?calid=" + calid;
							try {
								uurl = new URL(strUrl);
								urlc = (HttpURLConnection) uurl.openConnection();
								urlc.getInputStream();
							} catch (Exception e1) {
								e1.printStackTrace();
							}
						}
					}
				};
				new Thread(r).start();
			} catch (SQLException e) {
				System.out.println(" err");
				e.printStackTrace();
			}
			// 4. Close all
			try {
				pstmt.close();
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				Thread.sleep(300000); // 5�� �ֱ�� ���� �Ѵ�.
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
