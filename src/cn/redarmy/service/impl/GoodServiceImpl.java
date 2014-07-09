package cn.redarmy.service.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import cn.redarmy.domain.CommentInfo;
import cn.redarmy.domain.CreditAndCharmForTask;
import cn.redarmy.domain.CustomerInfo;
import cn.redarmy.domain.DistanceDetail;
import cn.redarmy.domain.TaskIcon;
import cn.redarmy.domain.TaskInfo;
import cn.redarmy.domain.TaskInfoDetail;
import cn.redarmy.domain.UpdateData;
import cn.redarmy.service.GoodService.GoodService;

final class GetConnection {
	// static
	/**
	 * Uses DriverManager.
	 */
	static Connection getSimpleConnection() throws SQLException {
		Connection conn = null;
		// See your driver documentation for the proper format of this string :
		String DB_CONN_STRING = "jdbc:mysql://localhost:3306/renpindatabase";
		// Provided by your driver documentation. In this case, a MySql driver
		// is used :
		String DRIVER_CLASS_NAME = "com.mysql.jdbc.Driver";
		String USER_NAME = "root";
		String PASSWORD = "P%mysql154";
		conn = DriverManager.getConnection(DB_CONN_STRING, USER_NAME, PASSWORD);

		try {
			Class.forName(DRIVER_CLASS_NAME).newInstance();
		} catch (Exception ex) {
			// log("Check classpath. Cannot load db driver: " +
			// DRIVER_CLASS_NAME);
		}

		try {
			conn = DriverManager.getConnection(DB_CONN_STRING, USER_NAME,
					PASSWORD);
		} catch (SQLException e) {

			// log("Driver loaded, but cannot connect to db: " +
			// DB_CONN_STRING);
			// e.printStackTrace();
		}
		return conn;
	}

	@SuppressWarnings("unused")
	private static void log(Object aObject) {
		// System.out.println(aObject);
	}
}

public class GoodServiceImpl implements GoodService {
	private static final long nTimeSeconds = 7 * 24 * 3600;
	private static final String strExpertPersonTimeEnd = "20:00:00";
	private static final long nLimitTimeSeconds = 60;

	// �����ַ���id����һ��list<String>����
	private HashMap<String, Integer> GetID(String strID) {
		HashMap<String, Integer> mapid = new HashMap<String, Integer>();
		String strIDTemp = strID;
		if (strIDTemp != null) {
			if (!strIDTemp.equals("")) {
				int nindex = 0;
				while (!strIDTemp.equals("")) {
					nindex = strIDTemp.indexOf(',');
					if (nindex != -1) {
						String strid = strIDTemp.substring(0, nindex);
						mapid.put(strid, 0);
						strIDTemp = strIDTemp.substring(nindex + 1);
					} else {
						mapid.put(strIDTemp, 0);
						strIDTemp = "";
					}
				}
			}
		}
		return mapid;
	}

	@Override
	public int SendAdviceText(String strAdvice) {
		int nRet = 0;
		Connection connect = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			connect = GetConnection.getSimpleConnection();
			stmt = connect.createStatement();

			String strTaskAnnounceTime = "";
			// ���µ�������뵽���ݿ���
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy��MM��dd��HH:mm:ss");
			Date curDate = new Date(System.currentTimeMillis());// ��ȡ��ǰʱ��
			strTaskAnnounceTime = formatter.format(curDate);

			String strSQL = "insert into renpin_advice_table values('";
			strSQL += strAdvice;
			strSQL += "','";
			strSQL += strTaskAnnounceTime;
			strSQL += "')";
			stmt.execute(strSQL);
			nRet = 1;
		} catch (Exception e) {
			// System.out.print("get data error!");
			// e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connect != null) {
				try {
					connect.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return nRet;
	}

	@Override
	public List<TaskInfo> GetTaskInfo(String strType) {
		List<TaskInfo> tasks = new ArrayList<TaskInfo>();
		Connection connect = null;
		Statement stmt = null;
		ResultSet rs = null;
		Statement stmt1 = null;
		ResultSet rs1 = null;
		try {
			connect = GetConnection.getSimpleConnection();
			stmt = connect.createStatement();
			stmt1 = connect.createStatement();
			String strSQL = "";
			if (strType.equals("1")) {
				strSQL = "select * from task_help_opera_table";
			} else if (strType.equals("2")) {
				strSQL = "select * from task_share_opera_table";
			}
			rs = stmt.executeQuery(strSQL);
			tasks.clear();
			// ��ȡϵͳ��ǰʱ��
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy��MM��dd��HH:mm:ss");
			Date curDate = new Date(System.currentTimeMillis());// ��ȡ��ǰʱ��
			String strCurrentTime = formatter.format(curDate);

			while (rs.next()) {
				// ���񷢲�ʱ��
				String strAnnounceTime = rs.getString(18);
				Date d1 = formatter.parse(strCurrentTime);
				Date d2 = formatter.parse(strAnnounceTime);
				long diff = d1.getTime() - d2.getTime();// �����õ��Ĳ�ֵ��΢�뼶��

				// �������������˵�����
				String strAnnounceName = rs.getString(2);
				// �������������˵�ͷ��
				String strAnnounceImage = "";
				// ��ȡ�û�ͷ��
				strSQL = "select * from customer_info_table where Account = '";
				strSQL += strAnnounceName;
				strSQL += "'";
				rs1 = stmt1.executeQuery(strSQL);
				while (rs1.next()) {
					strAnnounceImage = rs1.getString(1);
				}

				if (rs1 != null) {
					rs1.close();
					rs1 = null;
				}

				TaskInfo task = new TaskInfo(rs.getString(16),
						strAnnounceImage, rs.getString(28), rs.getString(15),
						rs.getInt(1) + "", rs.getDouble(20), rs.getDouble(21),
						strAnnounceTime, rs.getString(23), rs.getString(17),
						diff / 1000 + "", rs.getString(29), rs.getInt(24),
						rs.getInt(25), rs.getInt(14), rs.getInt(4),
						rs.getInt(5), rs.getInt(6), rs.getInt(7), rs.getInt(8),
						0, rs.getString(10), rs.getString(11),
						rs.getString(12), rs.getString(13), strAnnounceName,
						rs.getString(3));
				tasks.add(task);
			}
		} catch (Exception e) {
			// System.out.print("get data error!");
			// e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (rs1 != null) {
				try {
					rs1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt1 != null) {
				try {
					stmt1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connect != null) {
				try {
					connect.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return tasks;
	}

	@Override
	public int RegisterCustomer(String strBase64Icon, String strPersonName,
			String strPassword, String strCreditValue, String strCharmValue,
			String strNickName, String strSex) {
		int nRetType = 1;// Ϊ0��ʾ�ɹ�,1��ʾʧ��,2��ʾ�˺�����
		Connection connect = null;
		Statement stmt = null;
		ResultSet rs = null;
		boolean bIsFind = false;
		try {
			connect = GetConnection.getSimpleConnection();
			stmt = connect.createStatement();
			// ���ж�Ҫע����˺��Ƿ����
			String strSQL = "select * from customer_info_table where Account = '";
			strSQL += strPersonName;
			strSQL += "'";
			rs = stmt.executeQuery(strSQL);
			while (rs.next()) {
				// ����ҵ���
				bIsFind = true;
				nRetType = 2;
				break;
			}
			// ���û���ҵ�,��ô��ע���û�
			if (!bIsFind) {

				String strTaskAnnounceTime = "";
				// ���µ�������뵽���ݿ���
				SimpleDateFormat formatter = new SimpleDateFormat(
						"yyyy��MM��dd��HH:mm:ss");
				Date curDate = new Date(System.currentTimeMillis());// ��ȡ��ǰʱ��
				strTaskAnnounceTime = formatter.format(curDate);

				int nCreditValue = Integer.parseInt(strCreditValue);
				int nCharmValue = Integer.parseInt(strCharmValue);
				strSQL = "insert into customer_info_table values('";
				strSQL += strBase64Icon;
				strSQL += "','";
				strSQL += strPersonName;
				strSQL += "','";
				strSQL += strPassword;
				strSQL += "','','','','','";
				strSQL += strSex;
				strSQL += "','','0','0','1','";
				strSQL += nCreditValue;
				strSQL += "','";
				strSQL += nCharmValue;
				strSQL += "','";
				strSQL += strNickName;
				strSQL += "','','";
				strSQL += strTaskAnnounceTime;
				strSQL += "')";
				stmt.execute(strSQL);
				nRetType = 0;
			}
		} catch (Exception e) {
			// System.out.print("get data error!");
			// e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connect != null) {
				try {
					connect.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return nRetType;
	}

	@Override
	public CustomerInfo LogIn(String strPersonName, String strPassword) {
		CustomerInfo customer = null;
		Connection connect = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			connect = GetConnection.getSimpleConnection();
			stmt = connect.createStatement();
			// ���ж�Ҫע����˺��Ƿ����
			String strSQL = "select * from customer_info_table where Account = '";
			strSQL += strPersonName;
			strSQL += "'";
			strSQL += " and Password = '";
			strSQL += strPassword;
			strSQL += "'";
			rs = stmt.executeQuery(strSQL);
			while (rs.next()) {
				customer = new CustomerInfo(rs.getString(8), rs.getString(9),
						rs.getInt(10), rs.getInt(11), rs.getString(1),
						rs.getInt(13), rs.getInt(14), rs.getString(2),
						rs.getString(15));
				break;
			}
		} catch (Exception e) {
			// System.out.print("get data error!");
			// e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connect != null) {
				try {
					connect.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return customer;
	}

	@Override
	public int AddTaskInfo(String strTaskType, String strRegionName,
			String strTaskTitle, String strTaskDeatial,
			String strTaskAnnounceTime, String strTaskImpleTime,
			String strTaskAccountName, String strImplementAccountName,
			String strTaskAccountTrueName, String strImplementAccountTrueName,
			String strLongitude, String strLatidude, String strTaskAccountIcon,
			String strLimitTime,
			String strIcon1,// ��һ��ѹ��ͼƬ(��������)
			String strIcon2, String strIcon3, String strIcon4, String strIcon5,
			String strIcon6) {
		int nRetType = 0;// Ϊ0��ʾʧ��
		Connection connect = null;
		Statement stmt = null;
		ResultSet rs = null;
		int nTaskCount = 0;
		try {
			connect = GetConnection.getSimpleConnection();
			stmt = connect.createStatement();
			String strSQL = "";
			// ���ж�Ҫע����˺��Ƿ����
			strSQL = "select * from customer_info_table where Account = '";
			strSQL += strTaskAccountTrueName;
			strSQL += "'";
			rs = stmt.executeQuery(strSQL);
			boolean bFind = false;
			while (rs.next()) {
				bFind = true;
				break;
			}
			if (rs != null) {
				rs.close();
				rs = null;
			}

			if (bFind) {
				// ���������Ϊ��������
				if (strTaskType.equals("1")) {
					// �Ȼ�ȡ��������
					strSQL = "select * from task_help_opera_table";
					rs = stmt.executeQuery(strSQL);
					while (rs.next()) {
						rs.last();
						nTaskCount = rs.getRow();
						break;
					}
					nTaskCount += 1;
					String strTaskCount = nTaskCount + "";
					// ���µ�������뵽���ݿ���
					SimpleDateFormat formatter = new SimpleDateFormat(
							"yyyy��MM��dd��HH:mm:ss");
					Date curDate = new Date(System.currentTimeMillis());// ��ȡ��ǰʱ��
					strTaskAnnounceTime = formatter.format(curDate);
					int nTaskType = Integer.parseInt(strTaskType);
					String strCommentTableName = strTaskCount + "_h1";
					// �������۱�
					strSQL = "create table if not exists ";
					strSQL += strCommentTableName;
					strSQL += "(";
					strSQL += "TaskKey varchar(20),TaskTalkTime varchar(20),"
							+ "TaskTalkPersonName varchar(20),"
							+ "TaskTalkAcceptPeraonName varchar(20),"
							+ "TaskTalkContent varchar(140),"
							+ "TaskAccountImage MEDIUMTEXT,"
							+ "TaskTalkPersonToAcceptPersonImage MEDIUMTEXT,"
							+ "TaskTalkPersonToAcceptPersonLargeImage MEDIUMTEXT,"
							+ "CommentIndex Integer(20),TaskTalkPersonTrueName varchar(20),"
							+ "TaskTalkAcceptPeraonTrueName varchar(20),"
							+ "SecretType Integer(1))";
					stmt.execute(strSQL);
					// ����Ӧ�����ݲ��뵽�����������
					strSQL = "insert into task_help_opera_table values('";
					strSQL += nTaskCount;
					strSQL += "','";
					strSQL += strTaskAccountTrueName;
					strSQL += "','";
					strSQL += strImplementAccountTrueName;
					strSQL += "','1','1','1','1','1','";
					strSQL += strCommentTableName;
					strSQL += "','','','','','";
					strSQL += nTaskType;
					strSQL += "','";
					strSQL += strTaskTitle;
					strSQL += "','";
					strSQL += strRegionName;
					strSQL += "','";
					strSQL += strTaskDeatial;
					strSQL += "','";
					strSQL += strTaskAnnounceTime;
					strSQL += "','";
					strSQL += strTaskImpleTime;
					strSQL += "','";
					strSQL += strLongitude;
					strSQL += "','";
					strSQL += strLatidude;
					// strSQL += "','";
					// strSQL += strTaskAccountIcon;
					strSQL += "','','";
					strSQL += strLimitTime;
					strSQL += "','1','1','0','0','";
					strSQL += strTaskAccountName;
					strSQL += "','";
					strSQL += strImplementAccountName;
					strSQL += "','1','0','0','0','������','�Ϻ�')";
					stmt.execute(strSQL);
					if (rs != null) {
						rs.close();
						rs = null;
					}
					// ������С��ͼƬ
					strSQL = "insert into task_help_smallicon values('";
					strSQL += nTaskCount;
					strSQL += "','";
					strSQL += strIcon1;
					strSQL += "','";
					strSQL += strIcon2;
					strSQL += "','";
					strSQL += strIcon3;
					strSQL += "','";
					strSQL += strIcon4;
					strSQL += "','";
					strSQL += strIcon5;
					strSQL += "','";
					strSQL += strIcon6;
					strSQL += "')";
					stmt.execute(strSQL);

					// ������Ӧ��¼
					strSQL = "insert into customer_comment_table values('";
					strSQL += nTaskCount;
					strSQL += "','";
					strSQL += strTaskAccountTrueName;
					strSQL += "','1','1')";
					stmt.execute(strSQL);
					// �����±�־��Ϊ����״̬,֪ͨ�����û�
					strSQL = "update customer_info_table";
					strSQL += " set UpdateSignal = '1'";
					stmt.executeUpdate(strSQL);

					nRetType = nTaskCount;
				} else if (strTaskType.equals("2")) {// ���������Ϊ����
					// �Ȼ�ȡ��������
					strSQL = "select * from task_share_opera_table";
					rs = stmt.executeQuery(strSQL);
					while (rs.next()) {
						rs.last();
						nTaskCount = rs.getRow();
						break;
					}
					nTaskCount += 1;
					String strTaskCount = nTaskCount + "";
					// ���µ�������뵽���ݿ���
					SimpleDateFormat formatter = new SimpleDateFormat(
							"yyyy��MM��dd��HH:mm:ss");
					Date curDate = new Date(System.currentTimeMillis());// ��ȡ��ǰʱ��
					strTaskAnnounceTime = formatter.format(curDate);
					int nTaskType = Integer.parseInt(strTaskType);
					String strCommentTableName = strTaskCount + "_s1";
					// �������۱�
					strSQL = "create table if not exists ";
					strSQL += strCommentTableName;
					strSQL += "(";
					strSQL += "TaskKey varchar(20),TaskTalkTime varchar(20),"
							+ "TaskTalkPersonName varchar(20),"
							+ "TaskTalkAcceptPeraonName varchar(20),"
							+ "TaskTalkContent varchar(140),"
							+ "TaskAccountImage MEDIUMTEXT,"
							+ "TaskTalkPersonToAcceptPersonImage MEDIUMTEXT,"
							+ "TaskTalkPersonToAcceptPersonLargeImage MEDIUMTEXT,"
							+ "CommentIndex Integer(20),"
							+ "TaskTalkPersonTrueName varchar(20),"
							+ "TaskTalkAcceptPeraonTrueName varchar(20),"
							+ "SecretType Integer(1))";
					stmt.execute(strSQL);
					// ����Ӧ�����ݲ��뵽�����������
					strSQL = "insert into task_share_opera_table values('";
					strSQL += nTaskCount;
					strSQL += "','";
					strSQL += strTaskAccountTrueName;
					strSQL += "','";
					strSQL += strImplementAccountTrueName;
					strSQL += "','1','1','1','1','1','";
					strSQL += strCommentTableName;
					strSQL += "','','','','','";
					strSQL += nTaskType;
					strSQL += "','";
					strSQL += strTaskTitle;
					strSQL += "','";
					strSQL += strRegionName;
					strSQL += "','";
					strSQL += strTaskDeatial;
					strSQL += "','";
					strSQL += strTaskAnnounceTime;
					strSQL += "','";
					strSQL += strTaskImpleTime;
					strSQL += "','";
					strSQL += strLongitude;
					strSQL += "','";
					strSQL += strLatidude;
					// strSQL += "','";
					// strSQL += strTaskAccountIcon;
					strSQL += "','','";
					strSQL += strLimitTime;
					strSQL += "','1','1','0','0','";
					strSQL += strTaskAccountName;
					strSQL += "','";
					strSQL += strImplementAccountName;
					strSQL += "','1','0','0','0','������','�Ϻ�')";
					stmt.execute(strSQL);

					if (rs != null) {
						rs.close();
						rs = null;
					}
					// ������С��ͼƬ
					strSQL = "insert into task_share_smallicon values('";
					strSQL += nTaskCount;
					strSQL += "','";
					strSQL += strIcon1;
					strSQL += "','";
					strSQL += strIcon2;
					strSQL += "','";
					strSQL += strIcon3;
					strSQL += "','";
					strSQL += strIcon4;
					strSQL += "','";
					strSQL += strIcon5;
					strSQL += "','";
					strSQL += strIcon6;
					strSQL += "')";
					stmt.execute(strSQL);

					// ������Ӧ��¼
					strSQL = "insert into customer_comment_table values('";
					strSQL += nTaskCount;
					strSQL += "','";
					strSQL += strTaskAccountTrueName;
					strSQL += "','1','2')";
					stmt.execute(strSQL);
					// �����±�־��Ϊ����״̬,֪ͨ�����û�
					strSQL = "update customer_info_table";
					strSQL += " set UpdateSignal = '1'";
					stmt.executeUpdate(strSQL);

					nRetType = nTaskCount;
				}
			}
		} catch (Exception e) {
			// System.out.print("get data error!");
			// e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connect != null) {
				try {
					connect.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return nRetType;
	}

	@Override
	public int SelectTask(String strTaskId, String strPersonName,
			String strType, String strPersonNickName) {
		int nRetType = 0;// 0��ʾʧ��,Ϊ1��ʾ�ɹ�,2��ʾ����������������
		Connection connect = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			connect = GetConnection.getSimpleConnection();
			stmt = connect.createStatement();
			int nTaskId = Integer.parseInt(strTaskId);
			String strSQL = "";
			int nCreditValue = 1;
			int nCharmValue = 0;
			// ���ȸ���������ȡ��Ʒֵ����ֵ
			strSQL = "select * from customer_info_table where Account = '";
			strSQL += strPersonName;
			strSQL += "'";
			rs = stmt.executeQuery(strSQL);
			while (rs.next()) {
				nCreditValue = rs.getInt(13);
				nCharmValue = rs.getInt(14);
				break;
			}

			if (rs != null) {
				rs.close();
				rs = null;
			}
			// ���Ϊ��������
			if (strType.equals("1")) {
				// ���жϸ������Ƿ�������ѡ����
				strSQL = "select * from task_help_opera_table where TaskKey = '";
				strSQL += nTaskId;
				strSQL += "'";
				rs = stmt.executeQuery(strSQL);
				String strTaskAccountName = "";
				int nTaskSelectType = 1;

				int nTaskCreditValue = 0;
				int nTaskCharmValue = 0;
				while (rs.next()) {
					// ��ȡ����������
					nTaskSelectType = rs.getInt(4);
					// ��ȡ���񷢲���
					strTaskAccountName = rs.getString(2);
					nTaskCreditValue = rs.getInt(30);
					nTaskCharmValue = rs.getInt(31);
					break;
				}
				// �����Ʒֵ����ֵ����
				if (nCreditValue < nTaskCreditValue
						|| nCharmValue < nTaskCharmValue) {
					nRetType = 0;
				} else {
					// ���Ϊ��,�����������û��������ѡ��
					if (1 == nTaskSelectType) {
						// ����task_opera_table��
						strSQL = "update task_help_opera_table";
						strSQL += " set TaskSelectType = '2',TaskImplementStatus = '2',TaskImplementAccount = '";
						strSQL += strPersonName;
						strSQL += "',TaskImplementAccountNickName = '";
						strSQL += strPersonNickName;
						strSQL += "' where TaskKey = '";
						strSQL += nTaskId;
						strSQL += "'";
						stmt.executeUpdate(strSQL);

						// �����±�־��Ϊ����״̬
						strSQL = "update customer_info_table";
						strSQL += " set UpdateSignal = '1' where Account = '";
						strSQL += strTaskAccountName;
						strSQL += "' or Account = '";
						strSQL += strPersonName;
						strSQL += "'";
						stmt.executeUpdate(strSQL);

						nRetType = 1;
					} else {
						nRetType = 2;
					}
				}
			} else if (strType.equals("2")) {// ���Ϊ��������
				// ���жϸ������Ƿ�������ѡ����
				strSQL = "select * from task_share_opera_table where TaskKey = '";
				strSQL += nTaskId;
				strSQL += "'";
				rs = stmt.executeQuery(strSQL);
				String strTaskAccountName = "";
				int nTaskSelectType = 1;

				int nTaskCreditValue = 0;
				int nTaskCharmValue = 0;

				while (rs.next()) {
					// ��ȡ����������
					nTaskSelectType = rs.getInt(4);
					// ��ȡ���񷢲���
					strTaskAccountName = rs.getString(2);
					nTaskCreditValue = rs.getInt(30);
					nTaskCharmValue = rs.getInt(31);
					break;
				}
				// �����Ʒֵ����ֵ����
				if (nCreditValue < nTaskCreditValue
						|| nCharmValue < nTaskCharmValue) {
					nRetType = 0;
				} else {
					// ���Ϊ��,�����������û��������ѡ��
					if (1 == nTaskSelectType) {
						// ����task_opera_table��
						strSQL = "update task_share_opera_table";
						strSQL += " set TaskSelectType = '2',TaskImplementStatus = '2',TaskImplementAccount = '";
						strSQL += strPersonName;
						strSQL += "',TaskImplementAccountNickName = '";
						strSQL += strPersonNickName;
						strSQL += "' where TaskKey = '";
						strSQL += nTaskId;
						strSQL += "'";
						stmt.executeUpdate(strSQL);

						// �����±�־��Ϊ����״̬
						strSQL = "update customer_info_table";
						strSQL += " set UpdateSignal = '1' where Account = '";
						strSQL += strTaskAccountName;
						strSQL += "' or Account = '";
						strSQL += strPersonName;
						strSQL += "'";
						stmt.executeUpdate(strSQL);

						nRetType = 1;
					} else {
						nRetType = 2;
					}
				}
			}
		} catch (Exception e) {
			// System.out.print("get data error!");
			// e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connect != null) {
				try {
					connect.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return nRetType;
	}

	@Override
	public List<TaskInfo> GetMsgInfoNum(String strAnnounceName) {
		List<TaskInfo> dynamicnews = new ArrayList<TaskInfo>();
		int nNum = 0;
		Connection connect = null;
		Statement stmt = null;
		ResultSet rs = null;
		Statement stmt1 = null;
		ResultSet rs1 = null;
		try {
			connect = GetConnection.getSimpleConnection();
			stmt = connect.createStatement();
			stmt1 = connect.createStatement();
			// strAnnounceName��Ϊ������ʱ�Ķ�̬
			String strSQL = "select * from task_help_opera_table where TaskAccount = '";
			strSQL += strAnnounceName;
			strSQL += "'";
			strSQL += " and (TaskSelectType = '2' or TaskFinishType = '2' or TaskImplementCommentType = '2') and TaskType = '1'";
			rs = stmt.executeQuery(strSQL);
			while (rs.next()) {
				rs.last();
				nNum = rs.getRow();
			}

			if (rs != null) {
				rs.close();
				rs = null;
			}

			// strAnnounceName��Ϊ�����ߵĶ�̬
			strSQL = "select * from task_share_opera_table where TaskAccount = '";
			strSQL += strAnnounceName;
			strSQL += "'";
			strSQL += " and (TaskSelectType = '2' or TaskVerifiType = '2' or TaskImplementCommentType = '2') and TaskType = '2'";
			rs = stmt.executeQuery(strSQL);
			while (rs.next()) {
				rs.last();
				nNum += rs.getRow();
			}

			if (rs != null) {
				rs.close();
				rs = null;
			}

			// strAnnounceName��Ϊִ���ߵĶ�̬
			strSQL = "select * from task_help_opera_table where TaskImplementAccount = '";
			strSQL += strAnnounceName;
			strSQL += "'";
			strSQL += " and (TaskVerifiType = '2' or TaskAnnounceCommentType = '2') and TaskType = '1'";
			rs = stmt.executeQuery(strSQL);
			while (rs.next()) {
				rs.last();
				nNum += rs.getRow();
			}

			if (rs != null) {
				rs.close();
				rs = null;
			}

			// strAnnounceName��Ϊ�����ߵĶ�̬
			strSQL = "select * from task_share_opera_table where TaskImplementAccount = '";
			strSQL += strAnnounceName;
			strSQL += "'";
			strSQL += " and (TaskFinishType = '2' or TaskAnnounceCommentType = '2') and TaskType = '2'";
			rs = stmt.executeQuery(strSQL);
			while (rs.next()) {
				rs.last();
				nNum += rs.getRow();
			}

			if (rs != null) {
				rs.close();
				rs = null;
			}
			// ��������������������
			strSQL = "select * from task_help_opera_table where TaskAccount = '";
			strSQL += strAnnounceName;
			strSQL += "' or TaskImplementAccount = '";
			strSQL += strAnnounceName;
			strSQL += "' union select * from task_share_opera_table where TaskAccount = '";
			strSQL += strAnnounceName;
			strSQL += "' or TaskImplementAccount = '";
			strSQL += strAnnounceName;
			strSQL += "' order by TaskAnnounceTime DESC";
			rs = stmt.executeQuery(strSQL);
			while (rs.next()) {
				// ��ȡϵͳ��ǰʱ��
				SimpleDateFormat formatter = new SimpleDateFormat(
						"yyyy��MM��dd��HH:mm:ss");
				Date curDate = new Date(System.currentTimeMillis());// ��ȡ��ǰʱ��
				String strCurrentTime = formatter.format(curDate);
				// ���񷢲�ʱ��
				String strAnnounceTime = rs.getString(18);
				Date d1 = formatter.parse(strCurrentTime);
				Date d2 = formatter.parse(strAnnounceTime);
				long diff = d1.getTime() - d2.getTime();// �����õ��Ĳ�ֵ��΢�뼶��
				// ��ȡ������������������
				String strAnnounceName1 = rs.getString(2);
				// �������������˵�ͷ��
				String strAnnounceImage = "";
				// ��ȡ�û�ͷ��
				strSQL = "select * from customer_info_table where Account = '";
				strSQL += strAnnounceName1;
				strSQL += "'";
				rs1 = stmt1.executeQuery(strSQL);
				while (rs1.next()) {
					strAnnounceImage = rs1.getString(1);
				}
				if (rs1 != null) {
					rs1.close();
					rs1 = null;
				}

				TaskInfo task = new TaskInfo(rs.getString(16),
						strAnnounceImage, rs.getString(28), rs.getString(15),
						rs.getInt(1) + "", rs.getDouble(20), rs.getDouble(21),
						strAnnounceTime, rs.getString(23), rs.getString(17),
						diff / 1000 + "", rs.getString(29), rs.getInt(24),
						rs.getInt(25), rs.getInt(14), rs.getInt(4),
						rs.getInt(5), rs.getInt(6), rs.getInt(7), rs.getInt(8),
						nNum, rs.getString(10), rs.getString(11),
						rs.getString(12), rs.getString(13), strAnnounceName1,
						rs.getString(3));
				dynamicnews.add(task);
			}

			if (rs != null) {
				rs.close();
				rs = null;
			}

		} catch (Exception e) {
			// System.out.print("get data error!");
			// e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (rs1 != null) {
				try {
					rs1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt1 != null) {
				try {
					stmt1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connect != null) {
				try {
					connect.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return dynamicnews;
	}

	@Override
	public List<TaskInfo> GetMsgInfoNumNew(String strAnnounceName) {
		List<TaskInfo> dynamicnews = new ArrayList<TaskInfo>();
		int nNum = 0;
		Connection connect = null;
		Statement stmt = null;
		ResultSet rs = null;
		Statement stmt1 = null;
		ResultSet rs1 = null;
		Statement stmt2 = null;
		ResultSet rs2 = null;
		try {
			connect = GetConnection.getSimpleConnection();
			stmt = connect.createStatement();
			stmt1 = connect.createStatement();
			stmt2 = connect.createStatement();
			// ��������
			int nTaskType = 0;
			// ����id��
			int nTaskId = 0;
			// ��Ϣ״̬
			int nMessageStatus = 0;
			// ��ȡ����Ϣ������
			String strSQL = "select * from customer_comment_table where CustomerName = '";
			strSQL += strAnnounceName;
			strSQL += "' and TaskCommentType = '2'";
			rs = stmt.executeQuery(strSQL);
			while (rs.next()) {
				rs.last();
				nNum = rs.getRow();
			}
			if (rs != null) {
				rs.close();
				rs = null;
			}
			// ���ݱ�customer_comment_table��ȡ��ָ���û���ص�����
			strSQL = "select * from customer_comment_table where CustomerName = '";
			strSQL += strAnnounceName;
			strSQL += "'";
			rs = stmt.executeQuery(strSQL);
			rs.afterLast();
			while (rs.previous()) {
				nTaskType = rs.getInt(4);
				nTaskId = rs.getInt(1);
				nMessageStatus = rs.getInt(3);
				if (1 == nTaskType) {
					// ��Ϣ״̬
					int nStatus = nMessageStatus;
					String strName = "";
					// ��ȡϵͳ��ǰʱ��
					SimpleDateFormat formatter = new SimpleDateFormat(
							"yyyy��MM��dd��HH:mm:ss");
					Date curDate = new Date(System.currentTimeMillis());// ��ȡ��ǰʱ��
					String strCurrentTime = formatter.format(curDate);

					strSQL = "select * from task_help_opera_table where TaskKey = '";
					strSQL += nTaskId;
					strSQL += "'";
					rs1 = stmt1.executeQuery(strSQL);
					while (rs1.next()) {
						// ����id��
						int nid = rs1.getInt(1);
						// ������ߵ�id����ͬ
						if (nTaskId != nid) {
							nStatus = 1;
						}
						strName = rs1.getString(2);

						// �������������˵�ͷ��
						String strAnnounceImage = "";
						// ��ȡ�û�ͷ��
						strSQL = "select * from customer_info_table where Account = '";
						strSQL += strName;
						strSQL += "'";
						rs2 = stmt2.executeQuery(strSQL);
						while (rs2.next()) {
							strAnnounceImage = rs2.getString(1);
						}
						if (rs2 != null) {
							rs2.close();
							rs2 = null;
						}
						// ���񷢲�ʱ��
						String strAnnounceTime = rs1.getString(18);
						Date d1 = formatter.parse(strCurrentTime);
						Date d2 = formatter.parse(strAnnounceTime);
						long diff = d1.getTime() - d2.getTime();// �����õ��Ĳ�ֵ��΢�뼶��

						TaskInfo task = new TaskInfo(rs1.getString(16),
								strAnnounceImage, rs1.getString(28),
								rs1.getString(15), rs1.getInt(1) + "",
								rs1.getDouble(20), rs1.getDouble(21),
								strAnnounceTime, rs1.getString(23),
								rs1.getString(17), diff / 1000 + "",
								rs1.getString(29), rs1.getInt(24),
								rs1.getInt(25), rs1.getInt(14), rs1.getInt(4),
								rs1.getInt(5), rs1.getInt(6), nStatus,
								rs1.getInt(8), nNum, rs1.getString(10),
								rs1.getString(11), rs1.getString(12),
								rs1.getString(13), strName, rs1.getString(3));
						dynamicnews.add(task);
					}
					if (rs1 != null) {
						rs1.close();
						rs1 = null;
					}

				} else if (2 == nTaskType) {
					// ��Ϣ״̬
					int nStatus = nMessageStatus;
					String strName = "";
					// ��ȡϵͳ��ǰʱ��
					SimpleDateFormat formatter = new SimpleDateFormat(
							"yyyy��MM��dd��HH:mm:ss");
					Date curDate = new Date(System.currentTimeMillis());// ��ȡ��ǰʱ��
					String strCurrentTime = formatter.format(curDate);

					strSQL = "select * from task_share_opera_table where TaskKey = '";
					strSQL += nTaskId;
					strSQL += "'";
					rs1 = stmt1.executeQuery(strSQL);
					while (rs1.next()) {
						// ����id��
						int nid = rs1.getInt(1);
						// ������ߵ�id����ͬ,˵������������ɵ�ǰ�û�������
						if (nTaskId != nid) {
							nStatus = 1;
						}
						strName = rs1.getString(2);

						// �������������˵�ͷ��
						String strAnnounceImage = "";
						// ��ȡ�û�ͷ��
						strSQL = "select * from customer_info_table where Account = '";
						strSQL += strName;
						strSQL += "'";
						rs2 = stmt2.executeQuery(strSQL);
						while (rs2.next()) {
							strAnnounceImage = rs2.getString(1);
						}
						if (rs2 != null) {
							rs2.close();
							rs2 = null;
						}
						// ���񷢲�ʱ��
						String strAnnounceTime = rs1.getString(18);
						Date d1 = formatter.parse(strCurrentTime);
						Date d2 = formatter.parse(strAnnounceTime);
						long diff = d1.getTime() - d2.getTime();// �����õ��Ĳ�ֵ��΢�뼶��

						TaskInfo task = new TaskInfo(rs1.getString(16),
								strAnnounceImage, rs1.getString(28),
								rs1.getString(15), rs1.getInt(1) + "",
								rs1.getDouble(20), rs1.getDouble(21),
								strAnnounceTime, rs1.getString(23),
								rs1.getString(17), diff / 1000 + "",
								rs1.getString(29), rs1.getInt(24),
								rs1.getInt(25), rs1.getInt(14), rs1.getInt(4),
								rs1.getInt(5), rs1.getInt(6), nStatus,
								rs1.getInt(8), nNum, rs1.getString(10),
								rs1.getString(11), rs1.getString(12),
								rs1.getString(13), strName, rs1.getString(3));
						dynamicnews.add(task);
					}
					if (rs1 != null) {
						rs1.close();
						rs1 = null;
					}
				}
			}

		} catch (Exception e) {
			// System.out.print("get data error!");
			// e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (rs1 != null) {
				try {
					rs1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt1 != null) {
				try {
					stmt1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (rs2 != null) {
				try {
					rs1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt2 != null) {
				try {
					stmt1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connect != null) {
				try {
					connect.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return dynamicnews;
	}

	@Override
	public int UpdateTaskSelectType(String strAnnounceName, int nValue,
			String strTaskId, String strType) {
		int nRet = 0;
		Connection connect = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			connect = GetConnection.getSimpleConnection();
			stmt = connect.createStatement();
			int nTaskId = Integer.parseInt(strTaskId);
			if (strType.equals("1")) {
				// �������ݿ��(��Է�����)
				String strSQL = "update task_help_opera_table";
				strSQL += " set TaskSelectType = '";
				strSQL += nValue;
				strSQL += "' where TaskAccount = '";
				strSQL += strAnnounceName;
				strSQL += "' and TaskKey = '";
				strSQL += nTaskId;
				strSQL += "'";
				stmt.executeUpdate(strSQL);

				// �����±�־��Ϊ����״̬
				strSQL = "update customer_info_table";
				strSQL += " set UpdateSignal = '1' where Account = '";
				strSQL += strAnnounceName;
				strSQL += "'";
				stmt.executeUpdate(strSQL);

				nRet = 1;
			} else if (strType.equals("2")) {
				// �������ݿ��(��Է�����)
				String strSQL = "update task_share_opera_table";
				strSQL += " set TaskSelectType = '";
				strSQL += nValue;
				strSQL += "' where TaskAccount = '";
				strSQL += strAnnounceName;
				strSQL += "' and TaskKey = '";
				strSQL += nTaskId;
				strSQL += "'";
				stmt.executeUpdate(strSQL);

				// �����±�־��Ϊ����״̬
				strSQL = "update customer_info_table";
				strSQL += " set UpdateSignal = '1' where Account = '";
				strSQL += strAnnounceName;
				strSQL += "'";
				stmt.executeUpdate(strSQL);
				nRet = 1;
			}

		} catch (Exception e) {
			// System.out.print("get data error!");
			// e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connect != null) {
				try {
					connect.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return nRet;
	}

	@Override
	public int UpdateTaskFinishType(int nTaskType, String strAnnounceName,
			int nValue, String strTaskId, String strBase64Image,
			String strCommentContent) {
		int nRet = 0;
		Connection connect = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			connect = GetConnection.getSimpleConnection();
			stmt = connect.createStatement();
			int nTaskId = Integer.parseInt(strTaskId);
			String strSQL = "";

			// ���µ�������뵽���ݿ���
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy��MM��dd��HH:mm:ss");
			Date curDate = new Date(System.currentTimeMillis());// ��ȡ��ǰʱ��
			String strTaskImplementTime = formatter.format(curDate);

			// ��������,��ʱstrAnnounceNameΪ����������
			if (1 == nTaskType) {
				if (2 == nValue) {
					strSQL = "update task_help_opera_table";
					strSQL += " set TaskFinishType = '";
					strSQL += nValue;
					strSQL += "',TaskAccountCommentContent = '";
					strSQL += strCommentContent;
					strSQL += "',TaskAccountImage = '";
					strSQL += strBase64Image;
					strSQL += "',TaskImplementTime = '";
					strSQL += strTaskImplementTime;
					strSQL += "' where TaskAccount = '";
					strSQL += strAnnounceName;
					strSQL += "' and TaskKey = '";
					strSQL += nTaskId;
					strSQL += "'";
				} else {
					strSQL = "update task_help_opera_table";
					strSQL += " set TaskFinishType = '";
					strSQL += nValue;
					strSQL += "' where TaskAccount = '";
					strSQL += strAnnounceName;
					strSQL += "' and TaskKey = '";
					strSQL += nTaskId;
					strSQL += "'";
				}
				stmt.executeUpdate(strSQL);

			} else if (2 == nTaskType) {// ��������,��ʱstrAnnounceNameΪ���շ�����
				if (2 == nValue) {
					strSQL = "update task_share_opera_table";
					strSQL += " set TaskFinishType = '";
					strSQL += nValue;
					strSQL += "',TaskAccountCommentContent = '";
					strSQL += strCommentContent;
					strSQL += "',TaskAccountImage = '";
					strSQL += strBase64Image;
					strSQL += "',TaskImplementTime = '";
					strSQL += strTaskImplementTime;
					strSQL += "' where TaskImplementAccount = '";
					strSQL += strAnnounceName;
					strSQL += "' and TaskKey = '";
					strSQL += nTaskId;
					strSQL += "'";
				} else {
					strSQL = "update task_share_opera_table";
					strSQL += " set TaskFinishType = '";
					strSQL += nValue;
					strSQL += "' where TaskImplementAccount = '";
					strSQL += strAnnounceName;
					strSQL += "' and TaskKey = '";
					strSQL += nTaskId;
					strSQL += "'";
				}
				stmt.executeUpdate(strSQL);
			}

			// �����±�־��Ϊ����״̬
			strSQL = "update customer_info_table";
			strSQL += " set UpdateSignal = '1' where Account = '";
			strSQL += strAnnounceName;
			strSQL += "'";
			stmt.executeUpdate(strSQL);
			nRet = 1;
		} catch (Exception e) {
			// System.out.print("get data error!");
			// e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connect != null) {
				try {
					connect.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return nRet;
	}

	@Override
	public int UpdateTaskImplementCommentType(String strAnnounceName,
			int nValue, String strTaskId, String strType) {
		int nRet = 0;
		Connection connect = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			connect = GetConnection.getSimpleConnection();
			stmt = connect.createStatement();
			int nTaskId = Integer.parseInt(strTaskId);
			if (strType.equals("1")) {
				String strSQL = "update task_help_opera_table";
				strSQL += " set TaskImplementCommentType = '";
				strSQL += nValue;
				strSQL += "' where TaskAccount = '";
				strSQL += strAnnounceName;
				strSQL += "' and TaskKey = '";
				strSQL += nTaskId;
				strSQL += "'";
				stmt.executeUpdate(strSQL);

				// �����±�־��Ϊ����״̬
				strSQL = "update customer_info_table";
				strSQL += " set UpdateSignal = '1' where Account = '";
				strSQL += strAnnounceName;
				strSQL += "'";
				stmt.executeUpdate(strSQL);

				nRet = 1;
			} else if (strType.equals("2")) {
				String strSQL = "update task_share_opera_table";
				strSQL += " set TaskImplementCommentType = '";
				strSQL += nValue;
				strSQL += "' where TaskAccount = '";
				strSQL += strAnnounceName;
				strSQL += "' and TaskKey = '";
				strSQL += nTaskId;
				strSQL += "'";
				stmt.executeUpdate(strSQL);

				// �����±�־��Ϊ����״̬
				strSQL = "update customer_info_table";
				strSQL += " set UpdateSignal = '1' where Account = '";
				strSQL += strAnnounceName;
				strSQL += "'";
				stmt.executeUpdate(strSQL);

				nRet = 1;
			}
		} catch (Exception e) {
			// System.out.print("get data error!");
			// e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connect != null) {
				try {
					connect.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return nRet;
	}

	@Override
	public int UpdateTaskVerifiType(int nTaskType, String strAnnounceName,
			int nValue, String strTaskId, String strBase64Image,
			String strCommentContent) {
		int nRet = 0;
		Connection connect = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			connect = GetConnection.getSimpleConnection();
			stmt = connect.createStatement();
			int nTaskId = Integer.parseInt(strTaskId);
			String strSQL = "";
			if (1 == nTaskType) {
				if (2 == nValue) {
					// �������ݿ�(���ִ����)
					strSQL = "update task_help_opera_table";
					strSQL += " set TaskVerifiType = '";
					strSQL += nValue;
					strSQL += "',TaskImplementCommentContent = '";
					strSQL += strCommentContent;
					strSQL += "',TaskImplementImage = '";
					strSQL += strBase64Image;
					strSQL += "' where TaskImplementAccount = '";
					strSQL += strAnnounceName;
					strSQL += "' and TaskKey = '";
					strSQL += nTaskId;
					strSQL += "'";
				} else {
					// �������ݿ�(���ִ����)
					strSQL = "update task_help_opera_table";
					strSQL += " set TaskVerifiType = '";
					strSQL += nValue;
					strSQL += "' where TaskImplementAccount = '";
					strSQL += strAnnounceName;
					strSQL += "' and TaskKey = '";
					strSQL += nTaskId;
					strSQL += "'";
				}
				stmt.executeUpdate(strSQL);

			} else if (2 == nTaskType) {
				if (2 == nValue) {
					// �������ݿ�(��Է�����)
					strSQL = "update task_share_opera_table";
					strSQL += " set TaskVerifiType = '";
					strSQL += nValue;
					strSQL += "',TaskImplementCommentContent = '";
					strSQL += strCommentContent;
					strSQL += "',TaskImplementImage = '";
					strSQL += strBase64Image;
					strSQL += "' where TaskAccount = '";
					strSQL += strAnnounceName;
					strSQL += "' and TaskKey = '";
					strSQL += nTaskId;
					strSQL += "'";
				} else {
					// �������ݿ�(��Է�����)
					strSQL = "update task_share_opera_table";
					strSQL += " set TaskVerifiType = '";
					strSQL += nValue;
					strSQL += "' where TaskAccount = '";
					strSQL += strAnnounceName;
					strSQL += "' and TaskKey = '";
					strSQL += nTaskId;
					strSQL += "'";
				}

				stmt.executeUpdate(strSQL);
			}

			// �����±�־��Ϊ����״̬
			strSQL = "update customer_info_table";
			strSQL += " set UpdateSignal = '1' where Account = '";
			strSQL += strAnnounceName;
			strSQL += "'";
			stmt.executeUpdate(strSQL);

			nRet = 1;
		} catch (Exception e) {
			// System.out.print("get data error!");
			// e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connect != null) {
				try {
					connect.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return nRet;
	}

	@Override
	public int UpdateTaskAnnounceCommentType(String strAnnounceName,
			int nValue, String strTaskId, String strType) {
		int nRet = 0;
		Connection connect = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			connect = GetConnection.getSimpleConnection();
			stmt = connect.createStatement();
			int nTaskId = Integer.parseInt(strTaskId);
			String strSQL = "";
			if (strType.equals("1")) {
				strSQL = "update task_help_opera_table";
				strSQL += " set TaskAnnounceCommentType = '";
				strSQL += nValue;
				strSQL += "' where TaskImplementAccount = '";
				strSQL += strAnnounceName;
				strSQL += "' and TaskKey = '";
				strSQL += nTaskId;
				strSQL += "'";
				stmt.executeUpdate(strSQL);
			} else if (strType.equals("2")) {
				strSQL = "update task_share_opera_table";
				strSQL += " set TaskAnnounceCommentType = '";
				strSQL += nValue;
				strSQL += "' where TaskImplementAccount = '";
				strSQL += strAnnounceName;
				strSQL += "' and TaskKey = '";
				strSQL += nTaskId;
				strSQL += "'";
				stmt.executeUpdate(strSQL);
			}
			// �����±�־��Ϊ����״̬
			strSQL = "update customer_info_table";
			strSQL += " set UpdateSignal = '1' where Account = '";
			strSQL += strAnnounceName;
			strSQL += "'";
			stmt.executeUpdate(strSQL);

			nRet = 1;
		} catch (Exception e) {
			// System.out.print("get data error!");
			// e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connect != null) {
				try {
					connect.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return nRet;
	}

	@Override
	public List<CommentInfo> GetCommentsForTask(String strTaskId, String strType) {
		List<CommentInfo> commentinfos = new ArrayList<CommentInfo>();
		Connection connect = null;
		Statement stmt = null;
		Statement stmt1 = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		try {
			connect = GetConnection.getSimpleConnection();
			stmt = connect.createStatement();
			stmt1 = connect.createStatement();
			// �����˵�����
			String strCommentPersonName;
			// �������˵�����
			String strCommentReceivePersonName;
			// ����ʱ��
			String strCommentTime;
			// ��������
			String strCommentContent;
			// ѹ��ͼƬ
			String strSmallImage;
			// �Ŵ�ͼƬ
			// String strLargeImage;
			// ���ۺ�
			int nCommentIndex;
			// �����˵�����
			String strCommentPersonTrueName;
			// �������˵�����
			String strCommentReceivePersonTrueName;

			int nTaskId = Integer.parseInt(strTaskId);
			String strCommentTableName = "";
			if (strType.equals("1")) {
				// ��������id��ȡ�����۱�����
				String strSQL = "select * from task_help_opera_table";
				strSQL += " where TaskKey = '";
				strSQL += nTaskId;
				strSQL += "'";
				rs = stmt.executeQuery(strSQL);
				while (rs.next()) {
					strCommentTableName = rs.getString(9);
					break;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
				// ���ݻ�ȡ�������۱����ƻ�ȡ����Ӧ����
				strSQL = "select * from ";
				strSQL += strCommentTableName;
				rs = stmt.executeQuery(strSQL);
				while (rs.next()) {
					// �����۱���ȡ������
					strCommentPersonName = rs.getString(3);
					strCommentReceivePersonName = rs.getString(4);
					strCommentTime = rs.getString(2);
					strCommentContent = rs.getString(5);
					strSmallImage = rs.getString(7);
					nCommentIndex = rs.getInt(9);
					strCommentPersonTrueName = rs.getString(10);
					strCommentReceivePersonTrueName = rs.getString(11);

					// �����˵�ͷ��
					String strAnnounceImage = "";
					// ����������
					String strAnnounceName = rs.getString(10);
					// ��ȡ�û�ͷ��
					strSQL = "select * from customer_info_table where Account = '";
					strSQL += strAnnounceName;
					strSQL += "'";
					rs1 = stmt1.executeQuery(strSQL);
					while (rs1.next()) {
						strAnnounceImage = rs1.getString(1);
					}
					if (rs1 != null) {
						rs1.close();
						rs1 = null;
					}

					CommentInfo comment = new CommentInfo(strAnnounceImage,
							strCommentPersonName, strCommentReceivePersonName,
							strCommentTime, strCommentContent, strSmallImage,
							nCommentIndex, strCommentPersonTrueName,
							strCommentReceivePersonTrueName);
					commentinfos.add(comment);
				}
			} else if (strType.equals("2")) {
				// ��������id��ȡ�����۱�����
				String strSQL = "select * from task_share_opera_table";
				strSQL += " where TaskKey = '";
				strSQL += nTaskId;
				strSQL += "'";
				rs = stmt.executeQuery(strSQL);
				while (rs.next()) {
					strCommentTableName = rs.getString(9);
					break;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
				// ���ݻ�ȡ�������۱����ƻ�ȡ����Ӧ����
				strSQL = "select * from ";
				strSQL += strCommentTableName;
				rs = stmt.executeQuery(strSQL);
				while (rs.next()) {
					// �����۱���ȡ������
					strCommentPersonName = rs.getString(3);
					strCommentReceivePersonName = rs.getString(4);
					strCommentTime = rs.getString(2);
					strCommentContent = rs.getString(5);
					strSmallImage = rs.getString(7);
					// strLargeImage = rs.getString(8);
					nCommentIndex = rs.getInt(9);
					strCommentPersonTrueName = rs.getString(10);
					strCommentReceivePersonTrueName = rs.getString(11);

					// �������������˵�ͷ��
					String strAnnounceImage = "";
					// ��ȡ������������������
					String strAnnounceName = rs.getString(10);
					// ��ȡ�û�ͷ��
					strSQL = "select * from customer_info_table where Account = '";
					strSQL += strAnnounceName;
					strSQL += "'";
					rs1 = stmt1.executeQuery(strSQL);
					while (rs1.next()) {
						strAnnounceImage = rs1.getString(1);
					}
					if (rs1 != null) {
						rs1.close();
						rs1 = null;
					}

					CommentInfo comment = new CommentInfo(strAnnounceImage,
							strCommentPersonName, strCommentReceivePersonName,
							strCommentTime, strCommentContent, strSmallImage,
							nCommentIndex, strCommentPersonTrueName,
							strCommentReceivePersonTrueName);
					commentinfos.add(comment);
				}
			}
		} catch (Exception e) {
			// System.out.print("get data error!");
			// e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (rs1 != null) {
				try {
					rs1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt1 != null) {
				try {
					stmt1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connect != null) {
				try {
					connect.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return commentinfos;
	}

	public List<CommentInfo> GetCommentsForTaskNew(String strTaskId,
			String strType, String strCustomerName) {
		List<CommentInfo> commentinfos = new ArrayList<CommentInfo>();
		Connection connect = null;
		Statement stmt = null;
		Statement stmt1 = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		try {
			connect = GetConnection.getSimpleConnection();
			stmt = connect.createStatement();
			stmt1 = connect.createStatement();
			// ������
			String strAccountName = "";
			// �����˵�����
			String strCommentPersonName;
			// �������˵�����
			String strCommentReceivePersonName;
			// ����ʱ��
			String strCommentTime;
			// ��������
			String strCommentContent;
			// ѹ��ͼƬ
			String strSmallImage;
			// ���ۺ�
			int nCommentIndex;
			// �����˵�����
			String strCommentPersonTrueName;
			// �������˵�����
			String strCommentReceivePersonTrueName;
			// ������������(�Ƿ������Ļ�)
			int nCommentType;

			int nTaskId = Integer.parseInt(strTaskId);
			String strCommentTableName = "";
			if (strType.equals("1")) {
				// ��������id��ȡ�����۱�����
				String strSQL = "select * from task_help_opera_table";
				strSQL += " where TaskKey = '";
				strSQL += nTaskId;
				strSQL += "'";
				rs = stmt.executeQuery(strSQL);
				while (rs.next()) {
					strCommentTableName = rs.getString(9);
					strAccountName = rs.getString(2);
					break;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
				// ���ݻ�ȡ�������۱����ƻ�ȡ����Ӧ����
				strSQL = "select * from ";
				strSQL += strCommentTableName;
				strSQL += " ORDER BY TaskTalkTime asc";
				rs = stmt.executeQuery(strSQL);
				while (rs.next()) {
					strCommentPersonTrueName = rs.getString(10);
					strCommentReceivePersonTrueName = rs.getString(11);
					// ��������(�Ƿ������Ļ�)
					nCommentType = rs.getInt(12);
					// �����ǰ�û������������۵������ߺͽ������Լ���ǰ�û����Ƿ�����,ͬʱ�������������Ļ�,��ô�Ͳ��洢��Ӧ������
					if (!strCustomerName.equals(strCommentPersonTrueName)
							&& !strCustomerName
									.equals(strCommentReceivePersonTrueName)
							&& !strCustomerName.equals(strAccountName)
							&& 1 == nCommentType) {
						strCommentContent = "";
						strSmallImage = "";
					} else {
						strCommentContent = rs.getString(5);
						strSmallImage = rs.getString(7);
					}
					strCommentTime = rs.getString(2);
					nCommentIndex = rs.getInt(9);
					strCommentPersonName = rs.getString(3);
					strCommentReceivePersonName = rs.getString(4);
					// �����˵�ͷ��
					String strAnnounceImage = "";
					// ����������
					String strAnnounceName = rs.getString(10);
					// ��ȡ�û�ͷ��
					strSQL = "select * from customer_info_table where Account = '";
					strSQL += strAnnounceName;
					strSQL += "'";
					rs1 = stmt1.executeQuery(strSQL);
					while (rs1.next()) {
						strAnnounceImage = rs1.getString(1);
					}
					if (rs1 != null) {
						rs1.close();
						rs1 = null;
					}

					CommentInfo comment = new CommentInfo(strAnnounceImage,
							strCommentPersonName, strCommentReceivePersonName,
							strCommentTime, strCommentContent, strSmallImage,
							nCommentIndex, strCommentPersonTrueName,
							strCommentReceivePersonTrueName);
					commentinfos.add(comment);
				}
			} else if (strType.equals("2")) {
				// ��������id��ȡ�����۱�����
				String strSQL = "select * from task_share_opera_table";
				strSQL += " where TaskKey = '";
				strSQL += nTaskId;
				strSQL += "'";
				rs = stmt.executeQuery(strSQL);
				while (rs.next()) {
					strCommentTableName = rs.getString(9);
					strAccountName = rs.getString(2);
					break;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
				// ���ݻ�ȡ�������۱����ƻ�ȡ����Ӧ����
				strSQL = "select * from ";
				strSQL += strCommentTableName;
				rs = stmt.executeQuery(strSQL);
				while (rs.next()) {
					strCommentPersonTrueName = rs.getString(10);
					strCommentReceivePersonTrueName = rs.getString(11);
					// ��������(�Ƿ������Ļ�)
					nCommentType = rs.getInt(12);
					// �����ǰ�û������������۵������ߺͽ������Լ���ǰ�û����Ƿ�����,ͬʱ�������������Ļ�,��ô�Ͳ��洢��Ӧ������
					if (!strCustomerName.equals(strCommentPersonTrueName)
							&& !strCustomerName
									.equals(strCommentReceivePersonTrueName)
							&& !strCustomerName.equals(strAccountName)
							&& 1 == nCommentType) {
						strCommentContent = "";
						strSmallImage = "";
					} else {
						strCommentContent = rs.getString(5);
						strSmallImage = rs.getString(7);
					}
					strCommentTime = rs.getString(2);
					nCommentIndex = rs.getInt(9);
					strCommentPersonName = rs.getString(3);
					strCommentReceivePersonName = rs.getString(4);

					// �������������˵�ͷ��
					String strAnnounceImage = "";
					// ��ȡ������������������
					String strAnnounceName = rs.getString(10);
					// ��ȡ�û�ͷ��
					strSQL = "select * from customer_info_table where Account = '";
					strSQL += strAnnounceName;
					strSQL += "'";
					rs1 = stmt1.executeQuery(strSQL);
					while (rs1.next()) {
						strAnnounceImage = rs1.getString(1);
					}
					if (rs1 != null) {
						rs1.close();
						rs1 = null;
					}

					CommentInfo comment = new CommentInfo(strAnnounceImage,
							strCommentPersonName, strCommentReceivePersonName,
							strCommentTime, strCommentContent, strSmallImage,
							nCommentIndex, strCommentPersonTrueName,
							strCommentReceivePersonTrueName);
					commentinfos.add(comment);
				}
			}
		} catch (Exception e) {
			// System.out.print("get data error!");
			// e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (rs1 != null) {
				try {
					rs1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt1 != null) {
				try {
					stmt1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connect != null) {
				try {
					connect.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return commentinfos;
	}

	@Override
	public int SendCommentContent(String strTaskId,
			String strCommentPersonName, String strReceiveCommentPersonName,
			String strCommentContent, String strCommentPersonImage,
			String strType, String strCommentPersonNickName,
			String strReceiveCommentPersonNickName) {

		int nRet = 0;
		Connection connect = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			connect = GetConnection.getSimpleConnection();
			stmt = connect.createStatement();
			// ���۱�����
			String strCommentTableName = "";
			// ���񷢲�������
			String strTaskAnnouncePersonName = "";
			// ����ִ��������
			String strTaskImplePersonName = "";
			int nTaskId = Integer.parseInt(strTaskId);
			if (strType.equals("1")) {
				// �ȸ���strTaskId��ȡ�����۱�
				String strSQL = "select * from task_help_opera_table";
				strSQL += " where TaskKey = '";
				strSQL += nTaskId;
				strSQL += "'";
				rs = stmt.executeQuery(strSQL);
				while (rs.next()) {
					strCommentTableName = rs.getString(9);
					strTaskAnnouncePersonName = rs.getString(2);
					strTaskImplePersonName = rs.getString(3);
					break;
				}
				// ��������������񷢲���
				if (strCommentPersonName.equals(strTaskAnnouncePersonName)) {
					if (!strReceiveCommentPersonName.equals("")) {
						// ���ָ���˽�����,��ô�͸���������ʾ
						strSQL = "update task_help_opera_table";
						strSQL += " set TaskAnnounceCommentType = '2'";
						strSQL += " where TaskKey = '";
						strSQL += nTaskId;
						strSQL += "'";
						stmt.executeUpdate(strSQL);

						// �����±�־��Ϊ����״̬
						strSQL = "update customer_info_table";
						strSQL += " set UpdateSignal = '1' where Account = '";
						strSQL += strReceiveCommentPersonName;
						strSQL += "'";
						stmt.executeUpdate(strSQL);
					}
				} else if (strCommentPersonName.equals(strTaskImplePersonName)) {// ���������������ִ����
					// ���ָ���˽�����,��ô����ʲô��������Է���ʾ
					strSQL = "update task_help_opera_table";
					strSQL += " set TaskImplementCommentType = '2'";
					strSQL += " where TaskKey = '";
					strSQL += nTaskId;
					strSQL += "'";
					stmt.executeUpdate(strSQL);

					// �����±�־��Ϊ����״̬
					strSQL = "update customer_info_table";
					strSQL += " set UpdateSignal = '1' where Account = '";
					strSQL += strTaskAnnouncePersonName;
					strSQL += "'";
					stmt.executeUpdate(strSQL);
				}
				// ��ȡ��ǰʱ��
				SimpleDateFormat formatter = new SimpleDateFormat(
						"yyyy��MM��dd��HH:mm:ss");
				Date curDate = new Date(System.currentTimeMillis());// ��ȡ��ǰʱ��
				String strTaskAnnounceTime = formatter.format(curDate);

				// �����ݲ��뵽����
				strSQL = "insert into ";
				strSQL += strCommentTableName;
				strSQL += " values('";
				strSQL += strTaskId;
				strSQL += "','";
				strSQL += strTaskAnnounceTime;
				strSQL += "','";
				strSQL += strCommentPersonNickName;
				strSQL += "','";
				strSQL += strReceiveCommentPersonNickName;
				strSQL += "','";
				strSQL += strCommentContent;
				strSQL += "','','','','0','";
				strSQL += strCommentPersonName;
				strSQL += "','";
				strSQL += strReceiveCommentPersonName;
				strSQL += "','0')";
				stmt.execute(strSQL);

				nRet = 1;
			} else if (strType.equals("2")) {
				// �ȸ���strTaskId��ȡ�����۱�
				String strSQL = "select * from task_share_opera_table";
				strSQL += " where TaskKey = '";
				strSQL += nTaskId;
				strSQL += "'";
				rs = stmt.executeQuery(strSQL);
				while (rs.next()) {
					strCommentTableName = rs.getString(9);
					strTaskAnnouncePersonName = rs.getString(2);
					strTaskImplePersonName = rs.getString(3);
					break;
				}
				// ��������������񷢲���
				if (strCommentPersonName.equals(strTaskAnnouncePersonName)) {
					if (!strReceiveCommentPersonName.equals("")) {
						// ���ָ���˽�����,��ô�͸���������ʾ
						strSQL = "update task_share_opera_table";
						strSQL += " set TaskAnnounceCommentType = '2'";
						strSQL += " where TaskKey = '";
						strSQL += nTaskId;
						strSQL += "'";
						stmt.executeUpdate(strSQL);

						// �����±�־��Ϊ����״̬
						strSQL = "update customer_info_table";
						strSQL += " set UpdateSignal = '1' where Account = '";
						strSQL += strReceiveCommentPersonName;
						strSQL += "'";
						stmt.executeUpdate(strSQL);
					}
				} else if (strCommentPersonName.equals(strTaskImplePersonName)) {// ���������������ִ����
					// ���ָ���˽�����,��ô����ʲô��������Է���ʾ
					strSQL = "update task_share_opera_table";
					strSQL += " set TaskImplementCommentType = '2'";
					strSQL += " where TaskKey = '";
					strSQL += nTaskId;
					strSQL += "'";
					stmt.executeUpdate(strSQL);

					// �����±�־��Ϊ����״̬
					strSQL = "update customer_info_table";
					strSQL += " set UpdateSignal = '1' where Account = '";
					strSQL += strTaskAnnouncePersonName;
					strSQL += "'";
					stmt.executeUpdate(strSQL);
				}
				// ��ȡ��ǰʱ��
				SimpleDateFormat formatter = new SimpleDateFormat(
						"yyyy��MM��dd��HH:mm:ss");
				Date curDate = new Date(System.currentTimeMillis());// ��ȡ��ǰʱ��
				String strTaskAnnounceTime = formatter.format(curDate);

				// �����ݲ��뵽����
				strSQL = "insert into ";
				strSQL += strCommentTableName;
				strSQL += " values('";
				strSQL += nTaskId;
				strSQL += "','";
				strSQL += strTaskAnnounceTime;
				strSQL += "','";
				strSQL += strCommentPersonNickName;
				strSQL += "','";
				strSQL += strReceiveCommentPersonNickName;
				strSQL += "','";
				strSQL += strCommentContent;
				// strSQL += "',''";
				// strSQL += strCommentPersonImage;
				strSQL += "','','','','0','";
				strSQL += strCommentPersonName;
				strSQL += "','";
				strSQL += strReceiveCommentPersonName;
				strSQL += "','0')";
				stmt.execute(strSQL);

				nRet = 1;
			}
		} catch (Exception e) {
			// System.out.print("get data error!");
			// e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connect != null) {
				try {
					connect.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return nRet;

	}

	@Override
	public int SendCommentImage(String strTaskId, String strCommentPersonName,
			String strReceiveCommentPersonName, String strCommentPersonImage,
			String strSmallImage, String strLargeImage, String strType,
			String strCommentPersonNickName,
			String strReceiveCommentPersonNickName) {

		int nRet = 0;
		Connection connect = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			connect = GetConnection.getSimpleConnection();
			stmt = connect.createStatement();
			// ���۱�����
			String strCommentTableName = "";
			// ���񷢲�������
			String strTaskAnnouncePersonName = "";
			// ����ִ��������
			String strTaskImplePersonName = "";
			int nTaskId = Integer.parseInt(strTaskId);
			if (strType.equals("1")) {
				// �ȸ���strTaskId��ȡ�����۱�
				String strSQL = "select * from task_help_opera_table";
				strSQL += " where TaskKey = '";
				strSQL += nTaskId;
				strSQL += "'";
				rs = stmt.executeQuery(strSQL);
				while (rs.next()) {
					strCommentTableName = rs.getString(9);
					strTaskAnnouncePersonName = rs.getString(2);
					strTaskImplePersonName = rs.getString(3);
					break;
				}
				// ��������������񷢲���
				if (strCommentPersonName.equals(strTaskAnnouncePersonName)) {
					if (!strReceiveCommentPersonName.equals("")) {
						// ���ָ���˽�����,��ô�͸���������ʾ
						strSQL = "update task_help_opera_table";
						strSQL += " set TaskAnnounceCommentType = '2'";
						strSQL += " where TaskKey = '";
						strSQL += nTaskId;
						strSQL += "'";
						stmt.executeUpdate(strSQL);

						// �����±�־��Ϊ����״̬
						strSQL = "update customer_info_table";
						strSQL += " set UpdateSignal = '1' where Account = '";
						strSQL += strReceiveCommentPersonName;
						strSQL += "'";
						stmt.executeUpdate(strSQL);
					}
				} else if (strCommentPersonName.equals(strTaskImplePersonName)) {// ���������������ִ����
					// ���ָ���˽�����,��ô����ʲô��������Է���ʾ
					strSQL = "update task_help_opera_table";
					strSQL += " set TaskImplementCommentType = '2'";
					strSQL += " where TaskKey = '";
					strSQL += nTaskId;
					strSQL += "'";
					stmt.executeUpdate(strSQL);

					// �����±�־��Ϊ����״̬
					strSQL = "update customer_info_table";
					strSQL += " set UpdateSignal = '1' where Account = '";
					strSQL += strTaskAnnouncePersonName;
					strSQL += "'";
					stmt.executeUpdate(strSQL);
				}
				// ��ȡ��ǰʱ��
				SimpleDateFormat formatter = new SimpleDateFormat(
						"yyyy��MM��dd��HH:mm:ss");
				Date curDate = new Date(System.currentTimeMillis());// ��ȡ��ǰʱ��
				String strTaskAnnounceTime = formatter.format(curDate);

				if (rs != null) {
					rs.close();
					rs = null;
				}
				strSQL = "select * from ";
				strSQL += strCommentTableName;
				rs = stmt.executeQuery(strSQL);
				int nCount = 0;
				while (rs.next()) {
					rs.last();
					nCount = rs.getRow();
					break;
				}
				nCount++;
				// �����ݲ��뵽����
				strSQL = "insert into ";
				strSQL += strCommentTableName;
				strSQL += " values('";
				strSQL += strTaskId;
				strSQL += "','";
				strSQL += strTaskAnnounceTime;
				strSQL += "','";
				strSQL += strCommentPersonNickName;
				strSQL += "','";
				strSQL += strReceiveCommentPersonNickName;
				strSQL += "','','','";
				// strSQL += strCommentPersonImage;
				// strSQL += "','";
				strSQL += strSmallImage;
				strSQL += "','";
				strSQL += strLargeImage;
				strSQL += "','";
				strSQL += nCount;
				strSQL += "','";
				strSQL += strCommentPersonName;
				strSQL += "','";
				strSQL += strReceiveCommentPersonName;
				strSQL += "','0')";
				stmt.execute(strSQL);

				nRet = nCount;
			} else if (strType.equals("2")) {
				// �ȸ���strTaskId��ȡ�����۱�
				String strSQL = "select * from task_share_opera_table";
				strSQL += " where TaskKey = '";
				strSQL += nTaskId;
				strSQL += "'";
				rs = stmt.executeQuery(strSQL);
				while (rs.next()) {
					strCommentTableName = rs.getString(9);
					strTaskAnnouncePersonName = rs.getString(2);
					strTaskImplePersonName = rs.getString(3);
					break;
				}
				// ��������������񷢲���
				if (strCommentPersonName.equals(strTaskAnnouncePersonName)) {
					if (!strReceiveCommentPersonName.equals("")) {
						// ���ָ���˽�����,��ô�͸���������ʾ
						strSQL = "update task_share_opera_table";
						strSQL += " set TaskAnnounceCommentType = '2'";
						strSQL += " where TaskKey = '";
						strSQL += nTaskId;
						strSQL += "'";
						stmt.executeUpdate(strSQL);

						// �����±�־��Ϊ����״̬
						strSQL = "update customer_info_table";
						strSQL += " set UpdateSignal = '1' where Account = '";
						strSQL += strReceiveCommentPersonName;
						strSQL += "'";
						stmt.executeUpdate(strSQL);
					}
				} else if (strCommentPersonName.equals(strTaskImplePersonName)) {// ���������������ִ����
					// ���ָ���˽�����,��ô����ʲô��������Է���ʾ
					strSQL = "update task_share_opera_table";
					strSQL += " set TaskImplementCommentType = '2'";
					strSQL += " where TaskKey = '";
					strSQL += nTaskId;
					strSQL += "'";
					stmt.executeUpdate(strSQL);

					// �����±�־��Ϊ����״̬
					strSQL = "update customer_info_table";
					strSQL += " set UpdateSignal = '1' where Account = '";
					strSQL += strTaskAnnouncePersonName;
					strSQL += "'";
					stmt.executeUpdate(strSQL);
				}
				// ��ȡ��ǰʱ��
				SimpleDateFormat formatter = new SimpleDateFormat(
						"yyyy��MM��dd��HH:mm:ss");
				Date curDate = new Date(System.currentTimeMillis());// ��ȡ��ǰʱ��
				String strTaskAnnounceTime = formatter.format(curDate);

				if (rs != null) {
					rs.close();
					rs = null;
				}
				strSQL = "select * from ";
				strSQL += strCommentTableName;
				rs = stmt.executeQuery(strSQL);
				int nCount = 0;
				while (rs.next()) {
					rs.last();
					nCount = rs.getRow();
					break;
				}
				nCount++;
				// �����ݲ��뵽����
				strSQL = "insert into ";
				strSQL += strCommentTableName;
				strSQL += " values('";
				strSQL += strTaskId;
				strSQL += "','";
				strSQL += strTaskAnnounceTime;
				strSQL += "','";
				strSQL += strCommentPersonNickName;
				strSQL += "','";
				strSQL += strReceiveCommentPersonNickName;
				strSQL += "','','','";
				// strSQL += strCommentPersonImage;
				// strSQL += "','";
				strSQL += strSmallImage;
				strSQL += "','";
				strSQL += strLargeImage;
				strSQL += "','";
				strSQL += nCount;
				strSQL += "','";
				strSQL += strCommentPersonName;
				strSQL += "','";
				strSQL += strReceiveCommentPersonName;
				strSQL += "','0')";
				stmt.execute(strSQL);

				nRet = nCount;
			}
		} catch (Exception e) {
			// System.out.print("get data error!");
			// e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connect != null) {
				try {
					connect.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return nRet;
	}

	@Override
	public UpdateData GetUpdateSignal(String strCurrentAccountName) {
		UpdateData update = new UpdateData(-1, "");
		int nRet = -1;
		Connection connect = null;
		Statement stmt = null;
		ResultSet rs = null;
		Statement stmt1 = null;
		ResultSet rs1 = null;
		try {
			connect = GetConnection.getSimpleConnection();
			stmt = connect.createStatement();
			stmt1 = connect.createStatement();
			String strSQL = "";
			// ��ʼ��������ȥ�ж�����������
			// �����������
			strSQL = "select * from task_help_opera_table where TaskImplementAccount = '";
			strSQL += strCurrentAccountName;
			strSQL += "' or TaskAccount = '";
			strSQL += strCurrentAccountName;
			strSQL += "' union select * from task_share_opera_table where TaskImplementAccount = '";
			strSQL += strCurrentAccountName;
			strSQL += "' or TaskAccount = '";
			strSQL += strCurrentAccountName;
			strSQL += "'";
			// ����ִ����
			String strTaskImpleName1 = "";
			// ������
			String strTaskAccountName1 = "";
			// ������������
			int nTaskSelectType1 = 0;
			// ����ķ���ʱ��
			String strTaskAccountTime1 = "";
			// �����ʱ������(��)
			String strTimeLimit1 = "";
			// �����Ƿ��Ѿ�����
			int nTaskVerifiType1 = 0;
			// ����,1��ʾ����,2��ʾ����
			int nType = 0;
			// ��������id
			int nTaskId = 0;

			// ��ȡϵͳ��ǰʱ��
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy��MM��dd��HH:mm:ss");
			Date curDate = new Date(System.currentTimeMillis());// ��ȡ��ǰʱ��
			String strCurrentTime = formatter.format(curDate);
			rs = stmt.executeQuery(strSQL);
			while (rs.next()) {
				nTaskId = rs.getInt(1);
				strTaskImpleName1 = rs.getString(3);
				strTaskAccountName1 = rs.getString(2);
				nTaskSelectType1 = rs.getInt(4);
				strTaskAccountTime1 = rs.getString(18);
				strTimeLimit1 = rs.getString(23);
				long lTime = Long.parseLong(strTimeLimit1);
				nTaskVerifiType1 = rs.getInt(6);
				nType = rs.getInt(14);
				Date d1 = formatter.parse(strCurrentTime);
				Date d2 = formatter.parse(strTaskAccountTime1);
				long diff = d1.getTime() - d2.getTime();
				// ��������������������,ͬʱ�������û�н���
				if ((2 == nTaskSelectType1 || 3 == nTaskSelectType1)
						&& 1 == nTaskVerifiType1) {
					// �ж�����ӷ����������ǲ��ǳ�����ʱ�޵�7��
					if ((diff / 1000) >= (nTimeSeconds + lTime)) {
						int nSumCreditValue = 0;
						// �������������
						if (1 == nType) {
							// ���ý���״̬
							strSQL = "update task_help_opera_table";
							strSQL += " set TaskVerifiType = '2'";
							strSQL += ",CreditIncrValue = '1',CharmIncrValue = '0'";
							strSQL += " where TaskImplementAccount = '";
							strSQL += strTaskImpleName1;
							strSQL += "' and TaskKey = '";
							strSQL += nTaskId;
							strSQL += "'";
							stmt1.executeUpdate(strSQL);
							// ��ʱ������ִ������Ʒ��һ����
							// ��ȡ����Ʒֵ
							strSQL = "select * from customer_info_table where Account = '";
							strSQL += strTaskImpleName1;
							strSQL += "'";
							rs1 = stmt1.executeQuery(strSQL);
							while (rs1.next()) {
								nSumCreditValue = rs1.getInt(13) + 1;
							}
							// �����±�־��Ϊ����״̬
							strSQL = "update customer_info_table";
							strSQL += " set CreditValue = '";
							strSQL += nSumCreditValue;
							strSQL += "',";
							strSQL += "UpdateSignal = '1' where Account = '";
							strSQL += strTaskAccountName1;
							strSQL += "' or Account = '";
							strSQL += strTaskImpleName1;
							strSQL += "'";
							stmt1.executeUpdate(strSQL);
							// �ر�rs1
							if (rs1 != null) {
								rs1.close();
								rs1 = null;
							}
						} else if (2 == nType) {// ���Ϊ����
							// ���ý���״̬
							strSQL = "update task_share_opera_table";
							strSQL += " set TaskVerifiType = '2'";
							strSQL += ",CreditIncrValue = '1',CharmIncrValue = '0'";
							strSQL += " where TaskImplementAccount = '";
							strSQL += strTaskImpleName1;
							strSQL += "' and TaskKey = '";
							strSQL += nTaskId;
							strSQL += "'";
							stmt1.executeUpdate(strSQL);

							// ��ȡ����Ʒֵ
							strSQL = "select * from customer_info_table where Account = '";
							strSQL += strTaskAccountName1;
							strSQL += "'";
							rs1 = stmt1.executeQuery(strSQL);
							while (rs1.next()) {
								nSumCreditValue = rs1.getInt(13) + 1;
							}
							// �����±�־��Ϊ����״̬
							strSQL = "update customer_info_table";
							strSQL += " set CreditValue = '";
							strSQL += nSumCreditValue;
							strSQL += "',";
							strSQL += "UpdateSignal = '1' where Account = '";
							strSQL += strTaskAccountName1;
							strSQL += "' or Account = '";
							strSQL += strTaskImpleName1;
							strSQL += "'";
							stmt1.executeUpdate(strSQL);
							// �ر�rs1
							if (rs1 != null) {
								rs1.close();
								rs1 = null;
							}
						}
					}
				}
			}

			if (rs != null) {
				rs.close();
				rs = null;
			}

			// �����±�־��Ϊ����״̬
			strSQL = "select * from customer_info_table where Account = '";
			strSQL += strCurrentAccountName;
			strSQL += "'";
			rs = stmt.executeQuery(strSQL);
			while (rs.next()) {
				nRet = rs.getInt(12);
				break;
			}
			// }

			if (rs != null) {
				rs.close();
				rs = null;
			}
			String strStatusInfo = "";
			if (1 == nRet) {
				// �����������
				strSQL = "select * from task_help_opera_table where TaskImplementAccount = '";
				strSQL += strCurrentAccountName;
				strSQL += "' or TaskAccount = '";
				strSQL += strCurrentAccountName;
				strSQL += "'";
				rs = stmt.executeQuery(strSQL);
				while (rs.next()) {
					String strAccounceName = rs.getString(2);
					// String strId = rs.getString(1);
					String strImpleAccouceName = rs.getString(3);

					String strAccounceNickName = rs.getString(28);
					// String strId = rs.getString(1);
					String strImpleAccouceNickName = rs.getString(29);
					// ��������id��ȡ�����������
					String strTitle = rs.getString(15);
					// ��������id��ȡ��������
					int nTaskType = rs.getInt(14);
					// �������ݿ�ʼ
					// boolean bIsTure = false;
					int nTaskSelectType = rs.getInt(4);
					int nTaskFinishType = rs.getInt(5);
					int nTaskVerifiType = rs.getInt(6);
					int nTaskAnnounceCommentType = rs.getInt(7);
					int nTaskImplementCommentType = rs.getInt(8);
					// �������������ɵ�ǰ�û�������
					if (strCurrentAccountName.equals(strAccounceName)) {
						boolean b3 = !strImpleAccouceName.equals("");
						if (b3) {
							if (2 == nTaskSelectType) {
								if (1 == nTaskType) {
									strStatusInfo += strImpleAccouceNickName
											+ "�������㷢���ı���Ϊ'" + strTitle + "'������";
								}
							}
							if (1 == nTaskType) {
								if (2 == nTaskFinishType) {
									strStatusInfo += strImpleAccouceNickName
											+ "������㷢���ı���Ϊ'" + strTitle + "'������";
								}
							}
							if (2 == nTaskImplementCommentType) {
								if (1 == nTaskType) {
									strStatusInfo += strImpleAccouceNickName
											+ "���㷢���ı���Ϊ'" + strTitle
											+ "'������������";
								}

							}

						}
					} else if (strCurrentAccountName
							.equals(strImpleAccouceName)) {// �������������ɵ�ǰ�û�ִ�еĻ��߽��շ�����

						if (1 == nTaskType) {
							if (2 == nTaskVerifiType) {
								strStatusInfo += strAccounceNickName
										+ "����ִ�еı���Ϊ'" + strTitle + "'������������";
							}
						}
						if (2 == nTaskAnnounceCommentType) {
							if (1 == nTaskType) {
								strStatusInfo += strAccounceNickName
										+ "����ִ�еı���Ϊ'" + strTitle + "'������������";
							}

						}

					}
					// �������ݽ�β
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
				// ��Է�������
				strSQL = "select * from task_share_opera_table where TaskImplementAccount = '";
				strSQL += strCurrentAccountName;
				strSQL += "' or TaskAccount = '";
				strSQL += strCurrentAccountName;
				strSQL += "'";
				rs = stmt.executeQuery(strSQL);
				while (rs.next()) {
					String strAccounceName = rs.getString(2);
					// String strId = rs.getString(1);
					String strImpleAccouceName = rs.getString(3);

					String strAccounceNickName = rs.getString(28);
					// String strId = rs.getString(1);
					String strImpleAccouceNickName = rs.getString(29);
					// ��������id��ȡ�����������
					String strTitle = rs.getString(15);
					// ��������id��ȡ��������
					int nTaskType = rs.getInt(14);
					// �������ݿ�ʼ
					// boolean bIsTure = false;
					int nTaskSelectType = rs.getInt(4);
					int nTaskFinishType = rs.getInt(5);
					int nTaskVerifiType = rs.getInt(6);
					int nTaskAnnounceCommentType = rs.getInt(7);
					int nTaskImplementCommentType = rs.getInt(8);
					// �������������ɵ�ǰ�û������Ļ������ɵ�ǰ�û�������
					if (strCurrentAccountName.equals(strAccounceName)) {
						boolean b3 = !strImpleAccouceName.equals("");
						if (b3) {
							if (2 == nTaskSelectType) {

								if (2 == nTaskType) {
									strStatusInfo += strImpleAccouceNickName
											+ "�������㷢���ı���Ϊ'" + strTitle + "'�ķ���";
								}
							}
							if (2 == nTaskType) {
								if (2 == nTaskVerifiType) {
									strStatusInfo += strImpleAccouceNickName
											+ "��������ı���Ϊ" + strTitle + "'�ķ���������";
								}
							}
							if (2 == nTaskImplementCommentType) {
								if (2 == nTaskType) {
									strStatusInfo += strImpleAccouceNickName
											+ "���㷢���ı���Ϊ'" + strTitle
											+ "'�ķ���������";
								}
							}
						}
					} else if (strCurrentAccountName
							.equals(strImpleAccouceName)) {// �������������ɵ�ǰ�û�ִ�еĻ��߽��շ�����

						if (2 == nTaskType) {
							if (2 == nTaskFinishType) {
								strStatusInfo += strAccounceNickName
										+ "����˱���Ϊ'" + strTitle + "'�ķ���";
							}
						}
						if (2 == nTaskAnnounceCommentType) {
							if (2 == nTaskType) {
								strStatusInfo += strAccounceNickName
										+ "������յı���Ϊ'" + strTitle + "'�ķ���������";
							}
						}
					}
					// �������ݽ�β
				}
			}
			update.setnUpdateSignal(nRet);
			update.setstrUpdateDescribe(strStatusInfo);

		} catch (Exception e) {
			System.out.print("get data error!");
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (rs1 != null) {
				try {
					rs1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt1 != null) {
				try {
					stmt1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connect != null) {
				try {
					connect.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return update;
	}

	@Override
	public int SetUpdateSignal(String strCurrentAccountName, int nSignal) {
		int nRet = 0;
		Connection connect = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			connect = GetConnection.getSimpleConnection();
			stmt = connect.createStatement();
			// �����±�־��Ϊ����״̬
			String strSQL = "update customer_info_table";
			strSQL += " set UpdateSignal = '";
			strSQL += nSignal;
			strSQL += "' where Account = '";
			strSQL += strCurrentAccountName;
			strSQL += "'";
			stmt.executeUpdate(strSQL);
			nRet = 1;
		} catch (Exception e) {
			// System.out.print("get data error!");
			// e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connect != null) {
				try {
					connect.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return nRet;
	}

	@Override
	public int SetCreditValue(String strPersonName, int nCreditValue) {
		int nRet = 0;
		Connection connect = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			connect = GetConnection.getSimpleConnection();
			stmt = connect.createStatement();
			// �����±�־��Ϊ����״̬
			String strSQL = "update customer_info_table";
			strSQL += " set CreditValue = '";
			strSQL += nCreditValue;
			strSQL += "' where Account = '";
			strSQL += strPersonName;
			strSQL += "'";
			stmt.executeUpdate(strSQL);
			nRet = 1;
		} catch (Exception e) {
			// System.out.print("get data error!");
			// e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connect != null) {
				try {
					connect.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return nRet;
	}

	@Override
	public int SetCharmValue(String strPersonName, int nCharmValue) {
		int nRet = 0;
		Connection connect = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			connect = GetConnection.getSimpleConnection();
			stmt = connect.createStatement();
			// �����±�־��Ϊ����״̬
			String strSQL = "update customer_info_table";
			strSQL += " set CharmValue = '";
			strSQL += nCharmValue;
			strSQL += "' where Account = '";
			strSQL += strPersonName;
			strSQL += "'";
			stmt.executeUpdate(strSQL);
			nRet = 1;
		} catch (Exception e) {
			// System.out.print("get data error!");
			// e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connect != null) {
				try {
					connect.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return nRet;
	}

	@Override
	public int GetCreditValue(String strPersonName) {
		int nRet = -1;
		Connection connect = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			connect = GetConnection.getSimpleConnection();
			stmt = connect.createStatement();
			// �����±�־��Ϊ����״̬
			String strSQL = "select * from customer_info_table";
			strSQL += " where Account = '";
			strSQL += strPersonName;
			strSQL += "'";
			rs = stmt.executeQuery(strSQL);
			while (rs.next()) {
				nRet = rs.getInt(13);
			}
		} catch (Exception e) {
			// System.out.print("get data error!");
			// e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connect != null) {
				try {
					connect.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return nRet;
	}

	@Override
	public int GetCharmValue(String strPersonName) {
		int nRet = -1;
		Connection connect = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			connect = GetConnection.getSimpleConnection();
			stmt = connect.createStatement();
			// �����±�־��Ϊ����״̬
			String strSQL = "select * from customer_info_table";
			strSQL += " where Account = '";
			strSQL += strPersonName;
			strSQL += "'";
			rs = stmt.executeQuery(strSQL);
			while (rs.next()) {
				nRet = rs.getInt(14);
			}
		} catch (Exception e) {
			// System.out.print("get data error!");
			// e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connect != null) {
				try {
					connect.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return nRet;
	}

	@Override
	public int AddCreditValue(String strPersonName, int nIncrValue) {
		int nRet = 0;
		Connection connect = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			connect = GetConnection.getSimpleConnection();
			stmt = connect.createStatement();
			int nSumCreditValue = 0;
			if (nIncrValue > 0) {
				// ��ȡ����Ʒֵ
				String strSQL = "select * from customer_info_table where Account = '";
				strSQL += strPersonName;
				strSQL += "'";
				rs = stmt.executeQuery(strSQL);
				while (rs.next()) {
					nSumCreditValue = rs.getInt(13) + nIncrValue;
				}
				// �����±�־��Ϊ����״̬
				strSQL = "update customer_info_table";
				strSQL += " set CreditValue = '";
				strSQL += nSumCreditValue;
				strSQL += "' where Account = '";
				strSQL += strPersonName;
				strSQL += "'";
				stmt.executeUpdate(strSQL);
				nRet = 1;
			} else if (0 == nIncrValue) {
				nRet = 1;
			}

		} catch (Exception e) {
			// System.out.print("get data error!");
			// e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connect != null) {
				try {
					connect.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return nRet;
	}

	@Override
	public int AddCharmValue(String strPersonName, int nIncrValue) {
		int nRet = 0;
		Connection connect = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			connect = GetConnection.getSimpleConnection();
			stmt = connect.createStatement();
			int nSumCreditValue = 0;
			if (nIncrValue > 0) {
				// ��ȡ����ֵ
				String strSQL = "select * from customer_info_table where Account = '";
				strSQL += strPersonName;
				strSQL += "'";
				rs = stmt.executeQuery(strSQL);
				while (rs.next()) {
					nSumCreditValue = rs.getInt(14) + nIncrValue;
				}
				// �����±�־��Ϊ����״̬
				strSQL = "update customer_info_table";
				strSQL += " set CharmValue = '";
				strSQL += nSumCreditValue;
				strSQL += "' where Account = '";
				strSQL += strPersonName;
				strSQL += "'";
				stmt.executeUpdate(strSQL);
				nRet = 1;
			} else if (0 == nIncrValue) {
				nRet = 0;
			}

		} catch (Exception e) {
			// System.out.print("get data error!");
			// e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connect != null) {
				try {
					connect.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return nRet;
	}

	@Override
	public long GetTaskRemainTime(String strTaskId, String strType) {
		long lRemainTime = -1;
		Connection connect = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			connect = GetConnection.getSimpleConnection();
			stmt = connect.createStatement();
			int nTaskId = Integer.parseInt(strTaskId);
			String strSQL = "";
			if (strType.equals("1")) {
				strSQL = "select * from task_help_opera_table where TaskKey = '";
				strSQL += nTaskId;
				strSQL += "'";
				rs = stmt.executeQuery(strSQL);
				// ��ȡϵͳ��ǰʱ��
				SimpleDateFormat formatter = new SimpleDateFormat(
						"yyyy��MM��dd��HH:mm:ss");
				Date curDate = new Date(System.currentTimeMillis());// ��ȡ��ǰʱ��
				String strCurrentTime = formatter.format(curDate);

				while (rs.next()) {
					// ���񷢲�ʱ��
					String strAnnounceTime = rs.getString(18);
					Date d1 = formatter.parse(strCurrentTime);
					Date d2 = formatter.parse(strAnnounceTime);
					long diff = d1.getTime() - d2.getTime();// �����õ��Ĳ�ֵ��΢�뼶��
					// ��ȡ��ʣ����ʱ��
					String strTimeLimit = rs.getString(23);
					long lTimeLimit = Long.parseLong(strTimeLimit);
					lRemainTime = lTimeLimit - diff / 1000;
					break;
				}
			} else if (strType.equals("2")) {
				strSQL = "select * from task_share_opera_table where TaskKey = '";
				strSQL += nTaskId;
				strSQL += "'";
				rs = stmt.executeQuery(strSQL);
				// ��ȡϵͳ��ǰʱ��
				SimpleDateFormat formatter = new SimpleDateFormat(
						"yyyy��MM��dd��HH:mm:ss");
				Date curDate = new Date(System.currentTimeMillis());// ��ȡ��ǰʱ��
				String strCurrentTime = formatter.format(curDate);

				while (rs.next()) {
					// ���񷢲�ʱ��
					String strAnnounceTime = rs.getString(18);
					Date d1 = formatter.parse(strCurrentTime);
					Date d2 = formatter.parse(strAnnounceTime);
					long diff = d1.getTime() - d2.getTime();// �����õ��Ĳ�ֵ��΢�뼶��
					// ��ȡ��ʣ����ʱ��
					String strTimeLimit = rs.getString(23);
					long lTimeLimit = Long.parseLong(strTimeLimit);
					lRemainTime = lTimeLimit - diff / 1000;
					break;
				}
			}

		} catch (Exception e) {
			// System.out.print("get data error!");
			// e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connect != null) {
				try {
					connect.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return lRemainTime;
	}

	// ����û�е��ڵ������������������,nLimitΪһ�μ��ص������������,nTypeΪ����,1��ʾ����,
	// 2��ʾ����,nMaxTaskId��ʾ����ܼ��ص��������
	@Override
	public List<TaskInfo> LoadTaskData(int nLimit, int nType, int nMaxTaskId) {
		List<TaskInfo> tasks = new ArrayList<TaskInfo>();
		Connection connect = null;
		Statement stmt = null;
		ResultSet rs = null;
		Statement stmt1 = null;
		ResultSet rs1 = null;
		try {
			connect = GetConnection.getSimpleConnection();
			stmt = connect.createStatement();
			stmt1 = connect.createStatement();
			String strSQL = "";
			if (1 == nType) {
				strSQL = "select * from task_help_opera_table where TaskKey < ";
				strSQL += nMaxTaskId;
				strSQL += " order by TaskKey desc Limit ";
				strSQL += 0;
				strSQL += ",";
				strSQL += nLimit;
			} else if (2 == nType) {
				strSQL = "select * from task_share_opera_table where TaskKey < ";
				strSQL += nMaxTaskId;
				strSQL += " order by TaskKey desc Limit ";
				strSQL += 0;
				strSQL += ",";
				strSQL += nLimit;
			}
			rs = stmt.executeQuery(strSQL);
			tasks.clear();
			// ��ȡϵͳ��ǰʱ��
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy��MM��dd��HH:mm:ss");
			Date curDate = new Date(System.currentTimeMillis());// ��ȡ��ǰʱ��
			String strCurrentTime = formatter.format(curDate);

			while (rs.next()) {
				// ���񷢲�ʱ��
				String strAnnounceTime = rs.getString(18);
				Date d1 = formatter.parse(strCurrentTime);
				Date d2 = formatter.parse(strAnnounceTime);
				long diff = d1.getTime() - d2.getTime();// �����õ��Ĳ�ֵ��΢�뼶��

				// �������������˵�ͷ��
				String strAnnounceImage = "";
				// ��ȡ������������������
				String strAnnounceName = rs.getString(2);
				// ��ȡ�û�ͷ��
				strSQL = "select * from customer_info_table where Account = '";
				strSQL += strAnnounceName;
				strSQL += "'";
				rs1 = stmt1.executeQuery(strSQL);
				while (rs1.next()) {
					strAnnounceImage = rs1.getString(1);
				}
				if (rs1 != null) {
					rs1.close();
					rs1 = null;
				}
				// long nLimitTime = Long.parseLong(rs.getString(23));
				// �ж��Ƿ���,���û�е���
				// if (diff / 1000 - nLimitTime < 0) {
				TaskInfo task = new TaskInfo(rs.getString(16),
						strAnnounceImage, rs.getString(28), rs.getString(15),
						rs.getInt(1) + "", rs.getDouble(20), rs.getDouble(21),
						strAnnounceTime, rs.getString(23), rs.getString(17),
						diff / 1000 + "", rs.getString(29), rs.getInt(24),
						rs.getInt(25), rs.getInt(14), rs.getInt(4),
						rs.getInt(5), rs.getInt(6), rs.getInt(7), rs.getInt(8),
						0, rs.getString(10), rs.getString(11),
						rs.getString(12), rs.getString(13), strAnnounceName,
						rs.getString(3));
				tasks.add(task);
				// }

			}
		} catch (Exception e) {
			// System.out.print("get data error!");
			// e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (rs1 != null) {
				try {
					rs1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt1 != null) {
				try {
					stmt1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connect != null) {
				try {
					connect.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return tasks;
	}

	@Override
	public List<TaskInfo> UpdateTaskData(int nLimit, int nType, int nMaxTaskId) {
		List<TaskInfo> tasks = new ArrayList<TaskInfo>();
		Connection connect = null;
		Statement stmt = null;
		ResultSet rs = null;
		Statement stmt1 = null;
		ResultSet rs1 = null;
		try {
			connect = GetConnection.getSimpleConnection();
			stmt = connect.createStatement();
			stmt1 = connect.createStatement();
			String strSQL = "";
			if (1 == nType) {
				strSQL = "select * from task_help_opera_table where TaskKey > ";
				strSQL += nMaxTaskId;
				strSQL += " order by TaskKey desc Limit ";
				strSQL += 0;
				strSQL += ",";
				strSQL += nLimit;
			} else if (2 == nType) {
				strSQL = "select * from task_share_opera_table where TaskKey > ";
				strSQL += nMaxTaskId;
				strSQL += " order by TaskKey desc Limit ";
				strSQL += 0;
				strSQL += ",";
				strSQL += nLimit;
			}
			rs = stmt.executeQuery(strSQL);
			tasks.clear();
			// ��ȡϵͳ��ǰʱ��
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy��MM��dd��HH:mm:ss");
			Date curDate = new Date(System.currentTimeMillis());// ��ȡ��ǰʱ��
			String strCurrentTime = formatter.format(curDate);

			while (rs.next()) {
				// ���񷢲�ʱ��
				String strAnnounceTime = rs.getString(18);
				Date d1 = formatter.parse(strCurrentTime);
				Date d2 = formatter.parse(strAnnounceTime);
				long diff = d1.getTime() - d2.getTime();// �����õ��Ĳ�ֵ��΢�뼶��

				// �������������˵�ͷ��
				String strAnnounceImage = "";
				// ��ȡ������������������
				String strAnnounceName = rs.getString(2);
				// ��ȡ�û�ͷ��
				strSQL = "select * from customer_info_table where Account = '";
				strSQL += strAnnounceName;
				strSQL += "'";
				rs1 = stmt1.executeQuery(strSQL);
				while (rs1.next()) {
					strAnnounceImage = rs1.getString(1);
				}
				if (rs1 != null) {
					rs1.close();
					rs1 = null;
				}
				// long nLimitTime = Long.parseLong(rs.getString(23));
				// �ж��Ƿ���,���û�е���
				// if (diff / 1000 - nLimitTime < 0) {
				TaskInfo task = new TaskInfo(rs.getString(16),
						strAnnounceImage, rs.getString(28), rs.getString(15),
						rs.getInt(1) + "", rs.getDouble(20), rs.getDouble(21),
						strAnnounceTime, rs.getString(23), rs.getString(17),
						diff / 1000 + "", rs.getString(29), rs.getInt(24),
						rs.getInt(25), rs.getInt(14), rs.getInt(4),
						rs.getInt(5), rs.getInt(6), rs.getInt(7), rs.getInt(8),
						0, rs.getString(10), rs.getString(11),
						rs.getString(12), rs.getString(13), strAnnounceName,
						rs.getString(3));
				tasks.add(task);
				// }
			}
		} catch (Exception e) {
			// System.out.print("get data error!");
			// e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (rs1 != null) {
				try {
					rs1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt1 != null) {
				try {
					stmt1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connect != null) {
				try {
					connect.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return tasks;
	}

	@Override
	public int UploadLargeImage(String strTaskId, String strTaskType,
			String strIcon1, String strIcon2, String strIcon3, String strIcon4,
			String strIcon5, String strIcon6) {

		int nRetType = 0;// 0��ʾʧ��,1��ʾ�ɹ�
		Connection connect = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			connect = GetConnection.getSimpleConnection();
			stmt = connect.createStatement();
			int nTaskId = Integer.parseInt(strTaskId);
			String strSQL = "";
			if (strTaskType.equals("1")) {// ����
				strSQL = "insert into task_help_largeicon values('";
				strSQL += nTaskId;
				strSQL += "','";
				strSQL += strIcon1;
				strSQL += "','";
				strSQL += strIcon2;
				strSQL += "','";
				strSQL += strIcon3;
				strSQL += "','";
				strSQL += strIcon4;
				strSQL += "','";
				strSQL += strIcon5;
				strSQL += "','";
				strSQL += strIcon6;
				strSQL += "')";
				stmt.execute(strSQL);
				nRetType = 1;
			} else if (strTaskType.equals("2")) {
				strSQL = "insert into task_share_largeicon values('";
				strSQL += nTaskId;
				strSQL += "','";
				strSQL += strIcon1;
				strSQL += "','";
				strSQL += strIcon2;
				strSQL += "','";
				strSQL += strIcon3;
				strSQL += "','";
				strSQL += strIcon4;
				strSQL += "','";
				strSQL += strIcon5;
				strSQL += "','";
				strSQL += strIcon6;
				strSQL += "')";
				stmt.execute(strSQL);
				nRetType = 1;
			}
		} catch (Exception e) {
			// System.out.print("get data error!");
			// e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connect != null) {
				try {
					connect.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return nRetType;
	}

	@Override
	public int UploadTaskFinishTypeForLargeImage(String strTaskId,
			String strTaskType, String strBase64Image) {
		int nRetType = 0;// 0��ʾʧ��,1��ʾ�ɹ�
		Connection connect = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			connect = GetConnection.getSimpleConnection();
			stmt = connect.createStatement();
			int nTaskId = Integer.parseInt(strTaskId);
			String strSQL = "";
			if (strTaskType.equals("1")) {// ����
				strSQL = "insert into task_help_icon_eachother values('";
				strSQL += nTaskId;
				strSQL += "','";
				strSQL += 1;
				strSQL += "','";
				strSQL += strBase64Image;
				strSQL += "')";
				stmt.execute(strSQL);
				nRetType = 1;
			} else if (strTaskType.equals("2")) {// ����
				strSQL = "insert into task_share_icon_eachother values('";
				strSQL += nTaskId;
				strSQL += "','";
				strSQL += 0;
				strSQL += "','";
				strSQL += strBase64Image;
				strSQL += "')";
				stmt.execute(strSQL);
				nRetType = 1;
			}
		} catch (Exception e) {
			// System.out.print("get data error!");
			// e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connect != null) {
				try {
					connect.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return nRetType;
	}

	@Override
	public int UploadTaskVerifiTypeForLargeImage(String strTaskId,
			String strTaskType, String strBase64Image) {
		int nRetType = 0;// 0��ʾʧ��,1��ʾ�ɹ�
		Connection connect = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			connect = GetConnection.getSimpleConnection();
			stmt = connect.createStatement();
			int nTaskId = Integer.parseInt(strTaskId);
			String strSQL = "";
			if (strTaskType.equals("1")) {// ����
				// ���жϸ������Ƿ��Ѿ����뵽����
				strSQL = "select * from task_help_icon_eachother where TaskKey = '";
				strSQL += strTaskId;
				strSQL += "'";
				rs = stmt.executeQuery(strSQL);
				boolean bFind = false;
				while (rs.next()) {
					bFind = true;
					break;
				}
				if (!bFind) {// ���û�в���,��ô�Ͳ���
					strSQL = "insert into task_help_icon_eachother values('";
					strSQL += nTaskId;
					strSQL += "','";
					strSQL += 0;
					strSQL += "','";
					strSQL += strBase64Image;
					strSQL += "')";
					stmt.execute(strSQL);
					nRetType = 1;
				}

			} else if (strTaskType.equals("2")) {// ����
				// ���жϸ������Ƿ��Ѿ����뵽����
				strSQL = "select * from task_share_icon_eachother where TaskKey = '";
				strSQL += strTaskId;
				strSQL += "'";
				rs = stmt.executeQuery(strSQL);
				boolean bFind = false;
				while (rs.next()) {
					bFind = true;
					break;
				}
				if (!bFind) {
					strSQL = "insert into task_share_icon_eachother values('";
					strSQL += nTaskId;
					strSQL += "','";
					strSQL += 1;
					strSQL += "','";
					strSQL += strBase64Image;
					strSQL += "')";
					stmt.execute(strSQL);
					nRetType = 1;
				}

			}
		} catch (Exception e) {
			// System.out.print("get data error!");
			// e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connect != null) {
				try {
					connect.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return nRetType;
	}

	@Override
	public TaskIcon GetTaskSmallIcon(String strTaskId, String strTaskType) {
		TaskIcon taskicon = null;
		Connection connect = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			connect = GetConnection.getSimpleConnection();
			stmt = connect.createStatement();
			int nTaskId = Integer.parseInt(strTaskId);
			String strSQL = "";
			if (strTaskType.equals("1")) {// ����
				strSQL = "select * from task_help_smallicon where TaskKey = '";
				strSQL += nTaskId;
				strSQL += "'";
				rs = stmt.executeQuery(strSQL);
				while (rs.next()) {
					taskicon = new TaskIcon(rs.getString(2), rs.getString(3),
							rs.getString(4), rs.getString(5), rs.getString(6),
							rs.getString(7));
					break;
				}
			} else if (strTaskType.equals("2")) {// ����
				strSQL = "select * from task_share_smallicon where TaskKey = '";
				strSQL += nTaskId;
				strSQL += "'";
				rs = stmt.executeQuery(strSQL);
				while (rs.next()) {
					taskicon = new TaskIcon(rs.getString(2), rs.getString(3),
							rs.getString(4), rs.getString(5), rs.getString(6),
							rs.getString(7));
					break;
				}
			}
		} catch (Exception e) {
			// System.out.print("get data error!");
			// e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connect != null) {
				try {
					connect.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return taskicon;
	}

	@Override
	public String GetTaskLargeIcon(String strTaskId, String strTaskType,
			int nIconId) {
		String strtaskicon = null;
		Connection connect = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			connect = GetConnection.getSimpleConnection();
			stmt = connect.createStatement();
			int nTaskId = Integer.parseInt(strTaskId);
			String strSQL = "";
			if (strTaskType.equals("1")) {// ����
				strSQL = "select * from task_help_largeicon where TaskKey = '";
				strSQL += nTaskId;
				strSQL += "'";
				rs = stmt.executeQuery(strSQL);
				while (rs.next()) {
					strtaskicon = rs.getString(nIconId + 1);
					break;
				}
			} else if (strTaskType.equals("2")) {// ����
				strSQL = "select * from task_share_largeicon where TaskKey = '";
				strSQL += nTaskId;
				strSQL += "'";
				rs = stmt.executeQuery(strSQL);
				while (rs.next()) {
					strtaskicon = rs.getString(nIconId + 1);
					break;
				}
			}
		} catch (Exception e) {
			// System.out.print("get data error!");
			// e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connect != null) {
				try {
					connect.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return strtaskicon;
	}

	@Override
	public String GetTaskFinishLargeImage(String strTaskId, String strTaskType) {
		String strtaskicon = null;
		Connection connect = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			connect = GetConnection.getSimpleConnection();
			stmt = connect.createStatement();
			int nTaskId = Integer.parseInt(strTaskId);
			String strSQL = "";
			if (strTaskType.equals("1")) {// ����
				strSQL = "select * from task_help_icon_eachother where TaskKey = '";
				strSQL += nTaskId;
				strSQL += "' and IconType = '1'";
				rs = stmt.executeQuery(strSQL);
				while (rs.next()) {
					strtaskicon = rs.getString(3);
					break;
				}
			} else if (strTaskType.equals("2")) {// ����
				strSQL = "select * from task_share_icon_eachother where TaskKey = '";
				strSQL += nTaskId;
				strSQL += "' and IconType = '0'";
				rs = stmt.executeQuery(strSQL);
				while (rs.next()) {
					strtaskicon = rs.getString(3);
					break;
				}
			}
		} catch (Exception e) {
			// System.out.print("get data error!");
			// e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connect != null) {
				try {
					connect.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return strtaskicon;
	}

	@Override
	public String GetTaskVerifiLargeImage(String strTaskId, String strTaskType) {
		String strtaskicon = null;
		Connection connect = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			connect = GetConnection.getSimpleConnection();
			stmt = connect.createStatement();
			int nTaskId = Integer.parseInt(strTaskId);
			String strSQL = "";
			if (strTaskType.equals("1")) {// ����
				strSQL = "select * from task_help_icon_eachother where TaskKey = '";
				strSQL += nTaskId;
				strSQL += "' and IconType = '0'";
				rs = stmt.executeQuery(strSQL);
				while (rs.next()) {
					strtaskicon = rs.getString(3);
					break;
				}
			} else if (strTaskType.equals("2")) {// ����
				strSQL = "select * from task_share_icon_eachother where TaskKey = '";
				strSQL += nTaskId;
				strSQL += "' and IconType = '1'";
				rs = stmt.executeQuery(strSQL);
				while (rs.next()) {
					strtaskicon = rs.getString(3);
					break;
				}
			}
		} catch (Exception e) {
			// System.out.print("get data error!");
			// e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connect != null) {
				try {
					connect.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return strtaskicon;
	}

	@Override
	public String GetCommentsLargeImage(String strTaskId, String strType,
			int nCommentIndex) {
		String strLargeImage = "";
		Connection connect = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			connect = GetConnection.getSimpleConnection();
			stmt = connect.createStatement();

			int nTaskId = Integer.parseInt(strTaskId);
			String strCommentTableName = "";
			if (strType.equals("1")) {
				// ��������id��ȡ�����۱�����
				String strSQL = "select * from task_help_opera_table";
				strSQL += " where TaskKey = '";
				strSQL += nTaskId;
				strSQL += "'";
				rs = stmt.executeQuery(strSQL);
				while (rs.next()) {
					strCommentTableName = rs.getString(9);
					break;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
				// ���ݻ�ȡ�������۱����ƻ�ȡ����Ӧ����
				strSQL = "select * from ";
				strSQL += strCommentTableName;
				strSQL += " where CommentIndex = '";
				strSQL += nCommentIndex;
				strSQL += "'";
				rs = stmt.executeQuery(strSQL);
				while (rs.next()) {
					strLargeImage = rs.getString(8);
					break;
				}
			} else if (strType.equals("2")) {
				// ��������id��ȡ�����۱�����
				String strSQL = "select * from task_share_opera_table";
				strSQL += " where TaskKey = '";
				strSQL += nTaskId;
				strSQL += "'";
				rs = stmt.executeQuery(strSQL);
				while (rs.next()) {
					strCommentTableName = rs.getString(9);
					break;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
				// ���ݻ�ȡ�������۱����ƻ�ȡ����Ӧ����
				strSQL = "select * from ";
				strSQL += strCommentTableName;
				strSQL += " where CommentIndex = '";
				strSQL += nCommentIndex;
				strSQL += "'";
				rs = stmt.executeQuery(strSQL);
				while (rs.next()) {
					strLargeImage = rs.getString(8);
					break;
				}
			}
		} catch (Exception e) {
			// System.out.print("get data error!");
			// e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connect != null) {
				try {
					connect.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return strLargeImage;
	}

	@Override
	public int ResetTaskStatue(String strTaskId, String strType) {
		int nRet = 0;
		Connection connect = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			connect = GetConnection.getSimpleConnection();
			stmt = connect.createStatement();

			String strSQL = "";
			int nTaskId = Integer.parseInt(strTaskId);
			// ���µ�������뵽���ݿ���
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy��MM��dd��HH:mm:ss");
			Date curDate = new Date(System.currentTimeMillis());// ��ȡ��ǰʱ��
			String strTaskAnnounceTime = formatter.format(curDate);
			// �������������
			if (strType.equals("1")) {
				// ���жϸ������Ƿ��Ѿ�����
				strSQL = "select * from task_help_opera_table where TaskKey = '";
				strSQL += nTaskId;
				strSQL += "'";
				rs = stmt.executeQuery(strSQL);
				int nTaskStatus = 1;
				while (rs.next()) {
					nTaskStatus = rs.getInt(6);
					break;
				}
				// �������û�н���
				if (1 == nTaskStatus) {
					if (rs != null) {
						rs.close();
						rs = null;
					}
					// �Ƚ�����״̬����Ϊû������״̬
					strSQL = "update task_help_opera_table";
					strSQL += " set TaskSelectType = '1',TaskImplementAccount = '',TaskImplementAccountNickName = '',TaskFinishType = '1',"
							+ "TaskAnnounceCommentType = '1',TaskImplementCommentType = '1',"
							+ "TaskAccountCommentContent = '',TaskAccountImage = '',TaskImplementCommentContent = '',"
							+ "TaskImplementImage = '',TaskImplementTime = '',TaskAnnounceTime = '";
					strSQL += strTaskAnnounceTime;
					strSQL += "',TaskStatus = '1',TaskImplementStatus = '1' where TaskKey = '";
					strSQL += nTaskId;
					strSQL += "' and TaskVerifiType = '1'";
					stmt.executeUpdate(strSQL);

					// ��ȡ���۱�����
					strSQL = "select * from task_help_opera_table where TaskKey = '";
					strSQL += nTaskId;
					strSQL += "'";
					rs = stmt.executeQuery(strSQL);
					String strTabName = "";
					while (rs.next()) {
						strTabName = rs.getString(9);
						break;
					}
					// ɾ�����۱��е�����
					strSQL = "delete from ";
					strSQL += strTabName;
					stmt.executeUpdate(strSQL);
					// ɾ��task_help_icon_eachother���е���ӦͼƬ����
					strSQL = "delete from task_help_icon_eachother where TaskKey = '";
					strSQL += nTaskId;
					strSQL += "'";
					stmt.executeUpdate(strSQL);

					// �����±�־��Ϊ����״̬,֪ͨ�����û�
					strSQL = "update customer_info_table";
					strSQL += " set UpdateSignal = '1'";
					stmt.executeUpdate(strSQL);
					nRet = 1;
				}

			} else if (strType.equals("2")) {// ����Ƿ�������
				// ���жϸ������Ƿ��Ѿ�����
				strSQL = "select * from task_share_opera_table where TaskKey = '";
				strSQL += nTaskId;
				strSQL += "'";
				rs = stmt.executeQuery(strSQL);
				int nTaskStatus = 1;
				while (rs.next()) {
					nTaskStatus = rs.getInt(6);
					break;
				}
				if (1 == nTaskStatus) {
					if (rs != null) {
						rs.close();
						rs = null;
					}
					// �Ƚ�����״̬����Ϊû������״̬
					strSQL = "update task_share_opera_table";
					strSQL += " set TaskSelectType = '1',TaskImplementAccount = '',TaskImplementAccountNickName = '',TaskFinishType = '1',TaskVerifiType = '1',"
							+ "TaskAnnounceCommentType = '1',TaskImplementCommentType = '1',"
							+ "TaskAccountCommentContent = '',TaskAccountImage = '',TaskImplementCommentContent = '',"
							+ "TaskImplementImage = '',TaskImplementTime = '',TaskAnnounceTime = '";
					strSQL += strTaskAnnounceTime;
					strSQL += "',TaskStatus = '1',TaskImplementStatus = '1' where TaskKey = '";
					strSQL += nTaskId;
					strSQL += "'";
					stmt.executeUpdate(strSQL);
					// ��ȡ���۱�����
					strSQL = "select * from task_share_opera_table where TaskKey = '";
					strSQL += nTaskId;
					strSQL += "'";
					rs = stmt.executeQuery(strSQL);
					String strTabName = "";
					while (rs.next()) {
						strTabName = rs.getString(9);
					}
					// ɾ�����۱��е�����
					strSQL = "delete from ";
					strSQL += strTabName;
					stmt.executeUpdate(strSQL);
					// ɾ��task_share_icon_eachother���е���ӦͼƬ����
					strSQL = "delete from task_share_icon_eachother where TaskKey = '";
					strSQL += nTaskId;
					strSQL += "'";
					stmt.executeUpdate(strSQL);

					// �����±�־��Ϊ����״̬,֪ͨ�����û�
					strSQL = "update customer_info_table";
					strSQL += " set UpdateSignal = '1'";
					stmt.executeUpdate(strSQL);
					nRet = 1;
				}

			}
		} catch (Exception e) {
			// System.out.print("get data error!");
			// e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connect != null) {
				try {
					connect.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return nRet;
	}

	@Override
	public int RecordCreditAndCharmForTask(String strTaskId, String strType,
			int nCredit, int nCharm) {
		int nRet = 0;
		Connection connect = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			connect = GetConnection.getSimpleConnection();
			stmt = connect.createStatement();
			int nTaskId = Integer.parseInt(strTaskId);
			String strSQL = "";
			if (strType.equals("1")) {
				strSQL = "update task_help_opera_table";
				strSQL += " set CreditIncrValue = '";
				strSQL += nCredit;
				strSQL += "',CharmIncrValue = '";
				strSQL += nCharm;
				strSQL += "' where TaskKey = '";
				strSQL += nTaskId;
				strSQL += "'";
				stmt.executeUpdate(strSQL);
			} else if (strType.equals("2")) {
				strSQL = "update task_share_opera_table";
				strSQL += " set CreditIncrValue = '";
				strSQL += nCredit;
				strSQL += "',CharmIncrValue = '";
				strSQL += nCharm;
				strSQL += "' where TaskKey = '";
				strSQL += nTaskId;
				strSQL += "'";
				stmt.executeUpdate(strSQL);
			}
			nRet = 1;
		} catch (Exception e) {
			// System.out.print("get data error!");
			// e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connect != null) {
				try {
					connect.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return nRet;
	}

	@Override
	public CreditAndCharmForTask GetCreditAndCharmForTask(String strTaskId,
			String strType) {

		CreditAndCharmForTask taskvalue = null;
		Connection connect = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			connect = GetConnection.getSimpleConnection();
			stmt = connect.createStatement();
			int nTaskId = Integer.parseInt(strTaskId);
			String strSQL = "";
			if (strType.equals("1")) {
				strSQL = "select * from task_help_opera_table";
				strSQL += " where TaskKey = '";
				strSQL += nTaskId;
				strSQL += "'";
				rs = stmt.executeQuery(strSQL);
				while (rs.next()) {
					taskvalue = new CreditAndCharmForTask(rs.getInt(26),
							rs.getInt(27));
					break;
				}

			} else if (strType.equals("2")) {
				strSQL = "select * from task_share_opera_table";
				strSQL += " where TaskKey = '";
				strSQL += nTaskId;
				strSQL += "'";
				rs = stmt.executeQuery(strSQL);
				while (rs.next()) {
					taskvalue = new CreditAndCharmForTask(rs.getInt(26),
							rs.getInt(27));
					break;
				}
			}
		} catch (Exception e) {
			// System.out.print("get data error!");
			// e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connect != null) {
				try {
					connect.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return taskvalue;
	}

	@Override
	public int JudgeCustomerValidity(String strName, String strPassword) {
		int nRetType = 0;// Ϊ0��ʾʧ��,1��ʾ���˺�,2��ʾ�˺�����
		Connection connect = null;
		Statement stmt = null;
		ResultSet rs = null;
		boolean bIsFind = false;
		try {
			connect = GetConnection.getSimpleConnection();
			stmt = connect.createStatement();
			// ���ж�Ҫע����˺��Ƿ����
			String strSQL = "select * from customer_info_table where Account = '";
			strSQL += strName;
			strSQL += "'";
			rs = stmt.executeQuery(strSQL);
			while (rs.next()) {
				// ����ҵ���
				bIsFind = true;
				break;
			}
			if (bIsFind) {
				nRetType = 2;
			} else {
				nRetType = 1;
			}
		} catch (Exception e) {
			// System.out.print("get data error!");
			// e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connect != null) {
				try {
					connect.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return nRetType;
	}

	@Override
	public int RegisterCustomerDetail(String strBase64Icon,
			String strPersonName, String strPassword, String strCreditValue,
			String strCharmValue, String strNickName, String strSex,
			String strLargeImage) {
		int nRetType = 1;// Ϊ0��ʾ�ɹ�,1��ʾʧ��,2��ʾ�˺�����
		Connection connect = null;
		Statement stmt = null;
		ResultSet rs = null;
		boolean bIsFind = false;
		try {
			connect = GetConnection.getSimpleConnection();
			stmt = connect.createStatement();
			// ���ж�Ҫע����˺��Ƿ����
			String strSQL = "select * from customer_info_table where Account = '";
			strSQL += strPersonName;
			strSQL += "'";
			rs = stmt.executeQuery(strSQL);
			while (rs.next()) {
				// ����ҵ���
				bIsFind = true;
				nRetType = 2;
				break;
			}
			// ���û���ҵ�,��ô��ע���û�
			if (!bIsFind) {

				String strTaskAnnounceTime = "";
				// ���µ�������뵽���ݿ���
				SimpleDateFormat formatter = new SimpleDateFormat(
						"yyyy��MM��dd��HH:mm:ss");
				Date curDate = new Date(System.currentTimeMillis());// ��ȡ��ǰʱ��
				strTaskAnnounceTime = formatter.format(curDate);

				int nCreditValue = Integer.parseInt(strCreditValue);
				int nCharmValue = Integer.parseInt(strCharmValue);
				strSQL = "insert into customer_info_table values('";
				strSQL += strBase64Icon;
				strSQL += "','";
				strSQL += strPersonName;
				strSQL += "','";
				strSQL += strPassword;
				strSQL += "','','','','','";
				strSQL += strSex;
				strSQL += "','','0','0','1','";
				strSQL += nCreditValue;
				strSQL += "','";
				strSQL += nCharmValue;
				strSQL += "','";
				strSQL += strNickName;
				strSQL += "','";
				strSQL += strLargeImage;
				strSQL += "','";
				strSQL += strTaskAnnounceTime;
				strSQL += "')";
				stmt.execute(strSQL);
				nRetType = 0;
			}
		} catch (Exception e) {
			// e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connect != null) {
				try {
					connect.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return nRetType;
	}

	@Override
	public int AddTaskInfoForLimit(String strTaskType, String strRegionName,
			String strTaskTitle, String strTaskDeatial,
			String strTaskAnnounceTime, String strTaskImpleTime,
			String strTaskAccountName, String strImplementAccountName,
			String strTaskAccountTrueName, String strImplementAccountTrueName,
			String strLongitude, String strLatidude, String strTaskAccountIcon,
			String strLimitTime, String strIcon1, String strIcon2,
			String strIcon3, String strIcon4, String strIcon5, String strIcon6,
			int nCreditValue, int nCharmValue) {
		int nRetType = 0;// Ϊ0��ʾʧ��
		Connection connect = null;
		Statement stmt = null;
		ResultSet rs = null;
		int nTaskCount = 0;
		try {
			connect = GetConnection.getSimpleConnection();
			stmt = connect.createStatement();
			String strSQL = "";
			// ���ж�Ҫע����˺��Ƿ����
			strSQL = "select * from customer_info_table where Account = '";
			strSQL += strTaskAccountTrueName;
			strSQL += "'";
			rs = stmt.executeQuery(strSQL);
			boolean bFind = false;
			while (rs.next()) {
				bFind = true;
				break;
			}
			if (rs != null) {
				rs.close();
				rs = null;
			}

			if (bFind) {
				// ���������Ϊ��������
				if (strTaskType.equals("1")) {
					// �Ȼ�ȡ��������
					strSQL = "select * from task_help_opera_table";
					rs = stmt.executeQuery(strSQL);
					while (rs.next()) {
						rs.last();
						nTaskCount = rs.getRow();
						break;
					}
					nTaskCount += 1;
					String strTaskCount = nTaskCount + "";
					// ���µ�������뵽���ݿ���
					SimpleDateFormat formatter = new SimpleDateFormat(
							"yyyy��MM��dd��HH:mm:ss");
					Date curDate = new Date(System.currentTimeMillis());// ��ȡ��ǰʱ��
					strTaskAnnounceTime = formatter.format(curDate);
					int nTaskType = Integer.parseInt(strTaskType);
					String strCommentTableName = strTaskCount + "_h1";
					// �������۱�
					strSQL = "create table if not exists ";
					strSQL += strCommentTableName;
					strSQL += "(";
					strSQL += "TaskKey varchar(20),TaskTalkTime varchar(20),"
							+ "TaskTalkPersonName varchar(20),"
							+ "TaskTalkAcceptPeraonName varchar(20),"
							+ "TaskTalkContent varchar(140),"
							+ "TaskAccountImage MEDIUMTEXT,"
							+ "TaskTalkPersonToAcceptPersonImage MEDIUMTEXT,"
							+ "TaskTalkPersonToAcceptPersonLargeImage MEDIUMTEXT,"
							+ "CommentIndex Integer(20),TaskTalkPersonTrueName varchar(20),"
							+ "TaskTalkAcceptPeraonTrueName varchar(20),"
							+ "SecretType Integer(1))";
					stmt.execute(strSQL);
					// ����Ӧ�����ݲ��뵽�����������
					strSQL = "insert into task_help_opera_table values('";
					strSQL += nTaskCount;
					strSQL += "','";
					strSQL += strTaskAccountTrueName;
					strSQL += "','";
					strSQL += strImplementAccountTrueName;
					strSQL += "','1','1','1','1','1','";
					strSQL += strCommentTableName;
					strSQL += "','','','','','";
					strSQL += nTaskType;
					strSQL += "','";
					strSQL += strTaskTitle;
					strSQL += "','";
					strSQL += strRegionName;
					strSQL += "','";
					strSQL += strTaskDeatial;
					strSQL += "','";
					strSQL += strTaskAnnounceTime;
					strSQL += "','";
					strSQL += strTaskImpleTime;
					strSQL += "','";
					strSQL += strLongitude;
					strSQL += "','";
					strSQL += strLatidude;
					strSQL += "','','";
					strSQL += strLimitTime;
					strSQL += "','1','1','0','0','";
					strSQL += strTaskAccountName;
					strSQL += "','";
					strSQL += strImplementAccountName;
					strSQL += "','";
					strSQL += nCreditValue;
					strSQL += "','";
					strSQL += nCharmValue;
					strSQL += "','0','0','������','�Ϻ�')";
					stmt.execute(strSQL);
					if (rs != null) {
						rs.close();
						rs = null;
					}
					// ������С��ͼƬ
					strSQL = "insert into task_help_smallicon values('";
					strSQL += nTaskCount;
					strSQL += "','";
					strSQL += strIcon1;
					strSQL += "','";
					strSQL += strIcon2;
					strSQL += "','";
					strSQL += strIcon3;
					strSQL += "','";
					strSQL += strIcon4;
					strSQL += "','";
					strSQL += strIcon5;
					strSQL += "','";
					strSQL += strIcon6;
					strSQL += "')";
					stmt.execute(strSQL);

					// ������Ӧ��¼
					strSQL = "insert into customer_comment_table values('";
					strSQL += nTaskCount;
					strSQL += "','";
					strSQL += strTaskAccountTrueName;
					strSQL += "','1','1')";
					stmt.execute(strSQL);
					// �����±�־��Ϊ����״̬,֪ͨ�����û�
					strSQL = "update customer_info_table";
					strSQL += " set UpdateSignal = '1'";
					stmt.executeUpdate(strSQL);

					nRetType = nTaskCount;
				} else if (strTaskType.equals("2")) {// ���������Ϊ����
					// �Ȼ�ȡ��������
					strSQL = "select * from task_share_opera_table";
					rs = stmt.executeQuery(strSQL);
					while (rs.next()) {
						rs.last();
						nTaskCount = rs.getRow();
						break;
					}
					nTaskCount += 1;
					String strTaskCount = nTaskCount + "";
					// ���µ�������뵽���ݿ���
					SimpleDateFormat formatter = new SimpleDateFormat(
							"yyyy��MM��dd��HH:mm:ss");
					Date curDate = new Date(System.currentTimeMillis());// ��ȡ��ǰʱ��
					strTaskAnnounceTime = formatter.format(curDate);
					int nTaskType = Integer.parseInt(strTaskType);
					String strCommentTableName = strTaskCount + "_s1";
					// �������۱�
					strSQL = "create table if not exists ";
					strSQL += strCommentTableName;
					strSQL += "(";
					strSQL += "TaskKey varchar(20),TaskTalkTime varchar(20),"
							+ "TaskTalkPersonName varchar(20),"
							+ "TaskTalkAcceptPeraonName varchar(20),"
							+ "TaskTalkContent varchar(140),"
							+ "TaskAccountImage MEDIUMTEXT,"
							+ "TaskTalkPersonToAcceptPersonImage MEDIUMTEXT,"
							+ "TaskTalkPersonToAcceptPersonLargeImage MEDIUMTEXT,"
							+ "CommentIndex Integer(20),"
							+ "TaskTalkPersonTrueName varchar(20),"
							+ "TaskTalkAcceptPeraonTrueName varchar(20),"
							+ "SecretType Integer(1))";
					stmt.execute(strSQL);
					// ����Ӧ�����ݲ��뵽�����������
					strSQL = "insert into task_share_opera_table values('";
					strSQL += nTaskCount;
					strSQL += "','";
					strSQL += strTaskAccountTrueName;
					strSQL += "','";
					strSQL += strImplementAccountTrueName;
					strSQL += "','1','1','1','1','1','";
					strSQL += strCommentTableName;
					strSQL += "','','','','','";
					strSQL += nTaskType;
					strSQL += "','";
					strSQL += strTaskTitle;
					strSQL += "','";
					strSQL += strRegionName;
					strSQL += "','";
					strSQL += strTaskDeatial;
					strSQL += "','";
					strSQL += strTaskAnnounceTime;
					strSQL += "','";
					strSQL += strTaskImpleTime;
					strSQL += "','";
					strSQL += strLongitude;
					strSQL += "','";
					strSQL += strLatidude;
					strSQL += "','','";
					strSQL += strLimitTime;
					strSQL += "','1','1','0','0','";
					strSQL += strTaskAccountName;
					strSQL += "','";
					strSQL += strImplementAccountName;
					strSQL += "','";
					strSQL += nCreditValue;
					strSQL += "','";
					strSQL += nCharmValue;
					strSQL += "','0','0','������','�Ϻ�')";
					stmt.execute(strSQL);
					if (rs != null) {
						rs.close();
						rs = null;
					}
					// ������С��ͼƬ
					strSQL = "insert into task_share_smallicon values('";
					strSQL += nTaskCount;
					strSQL += "','";
					strSQL += strIcon1;
					strSQL += "','";
					strSQL += strIcon2;
					strSQL += "','";
					strSQL += strIcon3;
					strSQL += "','";
					strSQL += strIcon4;
					strSQL += "','";
					strSQL += strIcon5;
					strSQL += "','";
					strSQL += strIcon6;
					strSQL += "')";
					stmt.execute(strSQL);

					// ������Ӧ��¼
					strSQL = "insert into customer_comment_table values('";
					strSQL += nTaskCount;
					strSQL += "','";
					strSQL += strTaskAccountTrueName;
					strSQL += "','1','2')";
					stmt.execute(strSQL);
					// �����±�־��Ϊ����״̬,֪ͨ�����û�
					strSQL = "update customer_info_table";
					strSQL += " set UpdateSignal = '1'";
					stmt.executeUpdate(strSQL);
					nRetType = nTaskCount;

					// ���ӷ�����¼
					boolean bIsFind = false;
					formatter = new SimpleDateFormat("yyyy��MM��dd��");
					curDate = new Date(System.currentTimeMillis());// ��ȡ��ǰʱ��
					String strCurrentTime1 = formatter.format(curDate);
					int nTimes = 0;
					// ���жϱ����Ƿ��е���ļ�¼
					strSQL = "select ShareTimes from customer_share_expert_table where ShareDate = '";
					strSQL += strCurrentTime1;
					strSQL += "' and Account = '";
					strSQL += strTaskAccountTrueName;
					strSQL += "'";
					rs = stmt.executeQuery(strSQL);
					while (rs.next()) {
						bIsFind = true;
						nTimes = rs.getInt(1);
						break;
					}
					// ����ҵ���
					if (bIsFind) {
						// ������������һ
						nTimes += 1;
						// �����ͱ�־��Ϊ������
						strSQL = "update customer_share_expert_table";
						strSQL += " set ShareTimes = '";
						strSQL += nTimes;
						strSQL += "' where ShareDate = '";
						strSQL += strCurrentTime1;
						strSQL += "' and Account = '";
						strSQL += strTaskAccountTrueName;
						strSQL += "'";
						stmt.executeUpdate(strSQL);
					} else {// ���û���ҵ�,��ô�����¼����
						strSQL = "insert into customer_share_expert_table values('";
						strSQL += strTaskAccountTrueName;
						strSQL += "','";
						strSQL += strTaskAccountName;
						strSQL += "','";
						strSQL += strCurrentTime1;
						strSQL += "','1','1')";
						stmt.execute(strSQL);
					}
				}
			}

		} catch (Exception e) {
			// System.out.print("get data error!");
			// e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connect != null) {
				try {
					connect.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return nRetType;
	}

	@Override
	public List<TaskInfoDetail> GetTaskInfoForLimit(String strType) {
		List<TaskInfoDetail> tasks = new ArrayList<TaskInfoDetail>();
		Connection connect = null;
		Statement stmt = null;
		ResultSet rs = null;
		Statement stmt1 = null;
		ResultSet rs1 = null;
		try {
			connect = GetConnection.getSimpleConnection();
			stmt = connect.createStatement();
			stmt1 = connect.createStatement();
			String strSQL = "";
			if (strType.equals("1")) {
				strSQL = "select * from task_help_opera_table";
			} else if (strType.equals("2")) {
				strSQL = "select * from task_share_opera_table";
			}
			rs = stmt.executeQuery(strSQL);
			tasks.clear();
			// ��ȡϵͳ��ǰʱ��
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy��MM��dd��HH:mm:ss");
			Date curDate = new Date(System.currentTimeMillis());// ��ȡ��ǰʱ��
			String strCurrentTime = formatter.format(curDate);

			while (rs.next()) {
				// ���񷢲�ʱ��
				String strAnnounceTime = rs.getString(18);
				Date d1 = formatter.parse(strCurrentTime);
				Date d2 = formatter.parse(strAnnounceTime);
				long diff = d1.getTime() - d2.getTime();// �����õ��Ĳ�ֵ��΢�뼶��

				// �������������˵�����
				String strAnnounceName = rs.getString(2);
				// �������������˵�ͷ��
				String strAnnounceImage = "";
				// ��ȡ�û�ͷ��
				strSQL = "select * from customer_info_table where Account = '";
				strSQL += strAnnounceName;
				strSQL += "'";
				rs1 = stmt1.executeQuery(strSQL);
				while (rs1.next()) {
					strAnnounceImage = rs1.getString(1);
				}

				if (rs1 != null) {
					rs1.close();
					rs1 = null;
				}

				int nRecordNum = 0;
				// ��ȡ��������
				String strCommentTableName = rs.getString(9);
				strSQL = "select count(*) from ";
				strSQL += strCommentTableName;
				rs1 = stmt1.executeQuery(strSQL);
				while (rs1.next()) {
					nRecordNum = rs1.getInt(1);
				}
				if (rs1 != null) {
					rs1.close();
					rs1 = null;
				}

				TaskInfoDetail task = new TaskInfoDetail(rs.getString(16),
						strAnnounceImage, rs.getString(28), rs.getString(15),
						rs.getInt(1) + "", rs.getDouble(20), rs.getDouble(21),
						strAnnounceTime, rs.getString(23), rs.getString(17),
						diff / 1000 + "", rs.getString(29), rs.getInt(24),
						rs.getInt(25), rs.getInt(14), rs.getInt(4),
						rs.getInt(5), rs.getInt(6), rs.getInt(7), rs.getInt(8),
						0, rs.getString(10), rs.getString(11),
						rs.getString(12), rs.getString(13), strAnnounceName,
						rs.getString(3), rs.getInt(30), rs.getInt(31),
						rs.getInt(33), nRecordNum, rs.getInt(32));
				tasks.add(task);
			}
		} catch (Exception e) {
			// System.out.print("get data error!");
			// e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (rs1 != null) {
				try {
					rs1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt1 != null) {
				try {
					stmt1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connect != null) {
				try {
					connect.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return tasks;
	}

	@Override
	public List<TaskInfoDetail> LoadTaskDataForLimit(int nLimit, int nType,
			int nMaxTaskId) {
		List<TaskInfoDetail> tasks = new ArrayList<TaskInfoDetail>();
		Connection connect = null;
		Statement stmt = null;
		ResultSet rs = null;
		Statement stmt1 = null;
		ResultSet rs1 = null;
		try {
			connect = GetConnection.getSimpleConnection();
			stmt = connect.createStatement();
			stmt1 = connect.createStatement();
			String strSQL = "";
			if (1 == nType) {
				strSQL = "select * from task_help_opera_table where TaskKey < ";
				strSQL += nMaxTaskId;
				strSQL += " order by TaskKey desc Limit ";
				strSQL += 0;
				strSQL += ",";
				strSQL += nLimit;
			} else if (2 == nType) {
				strSQL = "select * from task_share_opera_table where TaskKey < ";
				strSQL += nMaxTaskId;
				strSQL += " order by TaskKey desc Limit ";
				strSQL += 0;
				strSQL += ",";
				strSQL += nLimit;
			}
			rs = stmt.executeQuery(strSQL);
			tasks.clear();
			// ��ȡϵͳ��ǰʱ��
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy��MM��dd��HH:mm:ss");
			Date curDate = new Date(System.currentTimeMillis());// ��ȡ��ǰʱ��
			String strCurrentTime = formatter.format(curDate);

			while (rs.next()) {
				// ���񷢲�ʱ��
				String strAnnounceTime = rs.getString(18);
				Date d1 = formatter.parse(strCurrentTime);
				Date d2 = formatter.parse(strAnnounceTime);
				long diff = d1.getTime() - d2.getTime();// �����õ��Ĳ�ֵ��΢�뼶��

				// �������������˵�ͷ��
				String strAnnounceImage = "";
				// ��ȡ������������������
				String strAnnounceName = rs.getString(2);
				// ��ȡ�û�ͷ��
				strSQL = "select * from customer_info_table where Account = '";
				strSQL += strAnnounceName;
				strSQL += "'";
				rs1 = stmt1.executeQuery(strSQL);
				while (rs1.next()) {
					strAnnounceImage = rs1.getString(1);
				}
				if (rs1 != null) {
					rs1.close();
					rs1 = null;
				}
				int nRecordNum = 0;
				// ��ȡ��������
				String strCommentTableName = rs.getString(9);
				strSQL = "select count(*) from ";
				strSQL += strCommentTableName;
				rs1 = stmt1.executeQuery(strSQL);
				while (rs1.next()) {
					nRecordNum = rs1.getInt(1);
				}
				if (rs1 != null) {
					rs1.close();
					rs1 = null;
				}
				// long nLimitTime = Long.parseLong(rs.getString(23));
				// �ж��Ƿ���,���û�е���
				// if (diff / 1000 - nLimitTime < 0) {
				TaskInfoDetail task = new TaskInfoDetail(rs.getString(16),
						strAnnounceImage, rs.getString(28), rs.getString(15),
						rs.getInt(1) + "", rs.getDouble(20), rs.getDouble(21),
						strAnnounceTime, rs.getString(23), rs.getString(17),
						diff / 1000 + "", rs.getString(29), rs.getInt(24),
						rs.getInt(25), rs.getInt(14), rs.getInt(4),
						rs.getInt(5), rs.getInt(6), rs.getInt(7), rs.getInt(8),
						0, rs.getString(10), rs.getString(11),
						rs.getString(12), rs.getString(13), strAnnounceName,
						rs.getString(3), rs.getInt(30), rs.getInt(31),
						rs.getInt(33), nRecordNum, rs.getInt(32));
				tasks.add(task);
				// }

			}
		} catch (Exception e) {
			// System.out.print("get data error!");
			// e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (rs1 != null) {
				try {
					rs1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt1 != null) {
				try {
					stmt1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connect != null) {
				try {
					connect.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return tasks;
	}

	@Override
	public List<TaskInfoDetail> UpdateTaskDataForLimit(int nLimit, int nType,
			int nMaxTaskId) {
		List<TaskInfoDetail> tasks = new ArrayList<TaskInfoDetail>();
		Connection connect = null;
		Statement stmt = null;
		ResultSet rs = null;
		Statement stmt1 = null;
		ResultSet rs1 = null;
		try {
			connect = GetConnection.getSimpleConnection();
			stmt = connect.createStatement();
			stmt1 = connect.createStatement();
			String strSQL = "";
			if (1 == nType) {
				strSQL = "select * from task_help_opera_table where TaskKey > ";
				strSQL += nMaxTaskId;
				strSQL += " order by TaskKey desc Limit ";
				strSQL += 0;
				strSQL += ",";
				strSQL += nLimit;
			} else if (2 == nType) {
				strSQL = "select * from task_share_opera_table where TaskKey > ";
				strSQL += nMaxTaskId;
				strSQL += " order by TaskKey desc Limit ";
				strSQL += 0;
				strSQL += ",";
				strSQL += nLimit;
			}
			rs = stmt.executeQuery(strSQL);
			tasks.clear();
			// ��ȡϵͳ��ǰʱ��
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy��MM��dd��HH:mm:ss");
			Date curDate = new Date(System.currentTimeMillis());// ��ȡ��ǰʱ��
			String strCurrentTime = formatter.format(curDate);

			while (rs.next()) {
				// ���񷢲�ʱ��
				String strAnnounceTime = rs.getString(18);
				Date d1 = formatter.parse(strCurrentTime);
				Date d2 = formatter.parse(strAnnounceTime);
				long diff = d1.getTime() - d2.getTime();// �����õ��Ĳ�ֵ��΢�뼶��

				// �������������˵�ͷ��
				String strAnnounceImage = "";
				// ��ȡ������������������
				String strAnnounceName = rs.getString(2);
				// ��ȡ�û�ͷ��
				strSQL = "select * from customer_info_table where Account = '";
				strSQL += strAnnounceName;
				strSQL += "'";
				rs1 = stmt1.executeQuery(strSQL);
				while (rs1.next()) {
					strAnnounceImage = rs1.getString(1);
				}
				if (rs1 != null) {
					rs1.close();
					rs1 = null;
				}
				int nRecordNum = 0;
				// ��ȡ��������
				String strCommentTableName = rs.getString(9);
				strSQL = "select count(*) from ";
				strSQL += strCommentTableName;
				rs1 = stmt1.executeQuery(strSQL);
				while (rs1.next()) {
					nRecordNum = rs1.getInt(1);
				}

				if (rs1 != null) {
					rs1.close();
					rs1 = null;
				}
				// long nLimitTime = Long.parseLong(rs.getString(23));
				// �ж��Ƿ���,���û�е���
				// if (diff / 1000 - nLimitTime < 0) {
				TaskInfoDetail task = new TaskInfoDetail(rs.getString(16),
						strAnnounceImage, rs.getString(28), rs.getString(15),
						rs.getInt(1) + "", rs.getDouble(20), rs.getDouble(21),
						strAnnounceTime, rs.getString(23), rs.getString(17),
						diff / 1000 + "", rs.getString(29), rs.getInt(24),
						rs.getInt(25), rs.getInt(14), rs.getInt(4),
						rs.getInt(5), rs.getInt(6), rs.getInt(7), rs.getInt(8),
						0, rs.getString(10), rs.getString(11),
						rs.getString(12), rs.getString(13), strAnnounceName,
						rs.getString(3), rs.getInt(30), rs.getInt(31),
						rs.getInt(33), nRecordNum, rs.getInt(32));
				tasks.add(task);
				// }
			}
		} catch (Exception e) {
			// System.out.print("get data error!");
			// e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (rs1 != null) {
				try {
					rs1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt1 != null) {
				try {
					stmt1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connect != null) {
				try {
					connect.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return tasks;
	}

	@Override
	public int ChangePersonImage(String strPersonName, String strSmallImage,
			String strLargeImage) {
		int nRetType = 0;
		Connection connect = null;
		Statement stmt = null;
		ResultSet rs = null;
		boolean bIsFind = false;
		try {
			connect = GetConnection.getSimpleConnection();
			stmt = connect.createStatement();
			// ���ж�Ҫע����˺��Ƿ����
			String strSQL = "select * from customer_info_table where Account = '";
			strSQL += strPersonName;
			strSQL += "'";
			rs = stmt.executeQuery(strSQL);
			while (rs.next()) {
				// ����ҵ���
				bIsFind = true;
				break;
			}
			// ����ҵ����û��ͽ���ͼƬ�޸�
			if (bIsFind) {
				// �����û�ͼ��
				strSQL = "update customer_info_table";
				strSQL += " set AccountIcon = '";
				strSQL += strSmallImage;
				strSQL += "',AccountLargeIcon = '";
				strSQL += strLargeImage;
				strSQL += "' where Account = '";
				strSQL += strPersonName;
				strSQL += "'";
				stmt.executeUpdate(strSQL);
				nRetType = 1;
			} else {
				nRetType = 2;
			}
		} catch (Exception e) {
			// System.out.print("get data error!");
			// e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connect != null) {
				try {
					connect.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return nRetType;
	}

	// ִ�д洢����
	@Override
	public List<DistanceDetail> execProcQuery(final String procName,
			final Object[] params, final String[] resultParams) {

		List<DistanceDetail> resultMapList = null;
		Connection connect = null;
		CallableStatement stmt = null;
		ResultSet rs = null;
		Statement stmt1 = null;
		ResultSet rs1 = null;
		try {
			connect = GetConnection.getSimpleConnection();
			stmt = connect.prepareCall(procName);
			stmt1 = connect.createStatement();
			DistanceDetail resultMap = null;
			if (params != null) {
				for (int i = 0; i < params.length; i++) {
					stmt.setObject(i + 1, params[i]);
				}
			}
			rs = stmt.executeQuery();

			// ��ȡϵͳ��ǰʱ��
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy��MM��dd��HH:mm:ss");
			Date curDate = new Date(System.currentTimeMillis());// ��ȡ��ǰʱ��
			String strCurrentTime = formatter.format(curDate);

			while (rs.next()) {
				if (resultMapList == null) {
					resultMapList = new ArrayList<DistanceDetail>();
				}
				// ���񷢲�ʱ��
				String strAnnounceTime = rs.getString(18);
				Date d1 = formatter.parse(strCurrentTime);
				Date d2 = formatter.parse(strAnnounceTime);
				long diff = d1.getTime() - d2.getTime();// �����õ��Ĳ�ֵ��΢�뼶��

				// �������������˵�ͷ��
				String strAnnounceImage = "";
				// ��ȡ������������������
				String strAnnounceName = rs.getString(2);
				// ��ȡ�û�ͷ��
				String strSQL = "select * from customer_info_table where Account = '";
				strSQL += strAnnounceName;
				strSQL += "'";
				rs1 = stmt1.executeQuery(strSQL);
				while (rs1.next()) {
					strAnnounceImage = rs1.getString(1);
				}
				if (rs1 != null) {
					rs1.close();
					rs1 = null;
				}
				int nRecordNum = 0;
				// ��ȡ��������
				String strCommentTableName = rs.getString(9);
				strSQL = "select count(*) from ";
				strSQL += strCommentTableName;
				rs1 = stmt1.executeQuery(strSQL);
				while (rs1.next()) {
					nRecordNum = rs1.getInt(1);
				}

				if (rs1 != null) {
					rs1.close();
					rs1 = null;
				}

				resultMap = new DistanceDetail(rs.getString(16),
						strAnnounceImage, rs.getString(28), rs.getString(15),
						rs.getInt(1) + "", rs.getDouble(20), rs.getDouble(21),
						rs.getString(18), rs.getString(23), rs.getString(17),
						diff / 1000 + "", rs.getString(29), rs.getInt(24),
						rs.getInt(25), rs.getInt(14), rs.getInt(4),
						rs.getInt(5), rs.getInt(6), rs.getInt(7), rs.getInt(8),
						0, rs.getString(10), rs.getString(11),
						rs.getString(12), rs.getString(13), rs.getString(2),
						rs.getString(3), rs.getInt(30), rs.getInt(31),
						rs.getInt(36), rs.getInt(33), nRecordNum, rs.getInt(32));
				resultMapList.add(resultMap);
			}
		} catch (Exception e) {
			// System.out.print("get data error!");
			// e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (rs1 != null) {
				try {
					rs1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt1 != null) {
				try {
					stmt1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connect != null) {
				try {
					connect.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return resultMapList;
	}

	@Override
	public List<DistanceDetail> getHelpNearData(Double longitude,
			Double latitude, Double dist, String strDistrictName) {
		List<DistanceDetail> resultMapList = null;
		try {
			// ���ò�ѯ�������ݴ洢����
			resultMapList = execProcQuery(
					"{CALL pro_test_UpdateHelpNearData(?,?,?,?)}",// Ҫ���õĴ洢����
					new Object[] { longitude, latitude, dist, strDistrictName }, // �����б�
					new String[] { "TaskKey", "distance" });// Ҫ��ȡ��ֵ key
		} catch (Exception e) {
			// System.out.print("get data error!");
			// e.printStackTrace();
		}
		return resultMapList;
	}

	@Override
	public List<DistanceDetail> getShareNearData(Double longitude,
			Double latitude, Double dist, String strDistrictName) {
		List<DistanceDetail> resultMapList = null;
		try {
			// ���ò�ѯ�������ݴ洢����
			resultMapList = execProcQuery(
					"{CALL pro_test_UpdateShareNearData(?,?,?,?)}",// Ҫ���õĴ洢����
					new Object[] { longitude, latitude, dist, strDistrictName }, // �����б�
					new String[] { "TaskKey", "distance" });// Ҫ��ȡ��ֵ key
		} catch (Exception e) {
			// System.out.print("get data error!");
			// e.printStackTrace();
		}
		return resultMapList;
	}

	@Override
	public List<DistanceDetail> LoadHelpNearData(Double longitude,
			Double latitude, Double dist, String strDistrictName, String strTime) {
		List<DistanceDetail> resultMapList = null;
		try {
			// ���ò�ѯ�������ݴ洢����
			resultMapList = execProcQuery(
					"{CALL pro_test_LoadHelpNearData(?,?,?,?,?)}",// Ҫ���õĴ洢����
					new Object[] { longitude, latitude, dist, strDistrictName,
							strTime }, // �����б�
					new String[] { "TaskKey", "distance" });// Ҫ��ȡ��ֵ key
		} catch (Exception e) {
			// System.out.print("get data error!");
			// e.printStackTrace();
		}
		return resultMapList;
	}

	@Override
	public List<DistanceDetail> LoadShareNearData(Double longitude,
			Double latitude, Double dist, String strDistrictName, String strTime) {
		List<DistanceDetail> resultMapList = null;
		try {
			// ���ò�ѯ�������ݴ洢����
			resultMapList = execProcQuery(
					"{CALL pro_test_LoadShareNearData(?,?,?,?,?)}",// Ҫ���õĴ洢����
					new Object[] { longitude, latitude, dist, strDistrictName,
							strTime }, // �����б�
					new String[] { "TaskKey", "distance" });// Ҫ��ȡ��ֵ key
		} catch (Exception e) {
			// System.out.print("get data error!");
			// e.printStackTrace();
		}
		return resultMapList;
	}

	@Override
	public UpdateData GetUpdateSignalNew(String strCurrentAccountName) {

		UpdateData update = new UpdateData(-1, "");
		int nRet = -1;
		Connection connect = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			connect = GetConnection.getSimpleConnection();
			stmt = connect.createStatement();
			String strSQL = "";
			// �����±�־��Ϊ����״̬
			strSQL = "select UpdateSignal from customer_info_table where Account = '";
			strSQL += strCurrentAccountName;
			strSQL += "'";
			rs = stmt.executeQuery(strSQL);
			while (rs.next()) {
				nRet = rs.getInt(1);
				break;
			}
			if (rs != null) {
				rs.close();
				rs = null;
			}

			String strStatusInfo = "";
			if (1 == nRet) {
				nRet = 1; // �����Ƿ��и�������
				strSQL = "select * from customer_comment_table where CustomerName = '";
				strSQL += strCurrentAccountName;
				strSQL += "' and TaskCommentType = '2'";
				rs = stmt.executeQuery(strSQL);
				while (rs.next()) {
					strStatusInfo = "��������Ϣ";
					break;
				}
				update.setnUpdateSignal(nRet);
				update.setstrUpdateDescribe(strStatusInfo);
			}

		} catch (Exception e) {
			System.out.print("get data error!");
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connect != null) {
				try {
					connect.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return update;
	}

	@Override
	public int UpdateCommentType(String strAnnounceName, int nValue,
			String strTaskId, String strType) {
		int nRet = 0;
		Connection connect = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			connect = GetConnection.getSimpleConnection();
			stmt = connect.createStatement();
			int nTaskId = Integer.parseInt(strTaskId);
			int nCurType = -1;
			boolean bFind = false;
			// ���жϱ����Ƿ�����Ӧ�ļ�¼
			String strSQL = "select * from customer_comment_table where TaskKey = '";
			strSQL += strTaskId;
			strSQL += "' and CustomerName = '";
			strSQL += strAnnounceName;
			strSQL += "' and TaskType = '";
			strSQL += strType;
			strSQL += "'";
			rs = stmt.executeQuery(strSQL);
			while (rs.next()) {
				// ��ȡ����״̬
				nCurType = rs.getInt(3);
				bFind = true;
			}
			// ����ҵ���,��ô��ֱ�Ӹ���״̬
			if (bFind) {
				// ���״̬����ͬ�͸���״̬
				if (nCurType != nValue) {
					strSQL = "update customer_comment_table";
					strSQL += " set TaskCommentType = '";
					strSQL += nValue;
					strSQL += "' where CustomerName = '";
					strSQL += strAnnounceName;
					strSQL += "' and TaskKey = '";
					strSQL += nTaskId;
					strSQL += "' and TaskType = '";
					strSQL += strType;
					strSQL += "'";
					stmt.executeUpdate(strSQL);
					// ���ֵΪ2,��Ҫ֪ͨ��Ӧ���û�
					if (2 == nValue) {
						// �����±�־��Ϊ����״̬
						strSQL = "update customer_info_table";
						strSQL += " set UpdateSignal = '1' where Account = '";
						strSQL += strAnnounceName;
						strSQL += "'";
						stmt.executeUpdate(strSQL);
					}
					nRet = 1;
				}
			} else {// ���û���ҵ�,��ô�Ͳ�������
				strSQL = "insert into customer_comment_table values('";
				strSQL += nTaskId;
				strSQL += "','";
				strSQL += strAnnounceName;
				strSQL += "','";
				strSQL += nValue;
				strSQL += "','";
				strSQL += strType;
				strSQL += "')";
				stmt.execute(strSQL);

				// ���ֵΪ2,��Ҫ֪ͨ��Ӧ���û�
				if (2 == nValue) {
					// �����±�־��Ϊ����״̬
					strSQL = "update customer_info_table";
					strSQL += " set UpdateSignal = '1' where Account = '";
					strSQL += strAnnounceName;
					strSQL += "'";
					stmt.executeUpdate(strSQL);
				}
				nRet = 1;
			}
		} catch (Exception e) {
			// System.out.print("get data error!");
			// e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connect != null) {
				try {
					connect.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return nRet;
	}

	@Override
	public int SendCommentContentNewSecret(String strTaskId,
			String strCommentPersonName, String strReceiveCommentPersonName,
			String strCommentContent, String strType,
			String strCommentPersonNickName,
			String strReceiveCommentPersonNickName, int nSecretType) {
		int nRet = 0;
		Connection connect = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			connect = GetConnection.getSimpleConnection();
			stmt = connect.createStatement();
			// ���۱�����
			String strCommentTableName = "";
			boolean bFind = false;
			int nTaskId = Integer.parseInt(strTaskId);
			if (strType.equals("1")) {
				// �ȸ���strTaskId��ȡ�����۱�
				String strSQL = "select * from task_help_opera_table";
				strSQL += " where TaskKey = '";
				strSQL += nTaskId;
				strSQL += "'";
				rs = stmt.executeQuery(strSQL);
				while (rs.next()) {
					strCommentTableName = rs.getString(9);
					// strTaskAnnouncePersonName = rs.getString(2);
					break;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
				// ���ȵ����в����ǲ����������˹����������ļ�¼
				if (!strCommentPersonName.equals("")) {
					strSQL = "select * from customer_comment_table where TaskKey = '";
					strSQL += strTaskId;
					strSQL += "' and CustomerName = '";
					strSQL += strCommentPersonName;
					strSQL += "' and TaskType = '1'";
					rs = stmt.executeQuery(strSQL);
					while (rs.next()) {
						bFind = true;
						break;
					}
					// ���û���ҵ�,��ô�ͽ�������¼���뵽����
					if (!bFind) {
						strSQL = "insert into customer_comment_table values('";
						strSQL += nTaskId;
						strSQL += "','";
						strSQL += strCommentPersonName;
						strSQL += "','1','1')";
						stmt.execute(strSQL);
					}
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
				bFind = false;
				// ���ұ����Ƿ��н������۵��˼�¼,���û�оͲ�����Ӧ��¼
				if (!strReceiveCommentPersonName.equals("")) {
					strSQL = "select * from customer_comment_table where TaskKey = '";
					strSQL += strTaskId;
					strSQL += "' and CustomerName = '";
					strSQL += strReceiveCommentPersonName;
					strSQL += "' and TaskType = '1'";
					rs = stmt.executeQuery(strSQL);
					while (rs.next()) {
						bFind = true;
						break;
					}
					// ���û���ҵ�,��ô�ͽ�������¼���뵽����
					if (!bFind) {
						strSQL = "insert into customer_comment_table values('";
						strSQL += nTaskId;
						strSQL += "','";
						strSQL += strReceiveCommentPersonName;
						strSQL += "','2','1')";
						stmt.execute(strSQL);
					} else {// ����ҵ���,�͸���״̬
						strSQL = "update customer_comment_table";
						strSQL += " set TaskCommentType = '2' where TaskKey = '";
						strSQL += strTaskId;
						strSQL += "' and CustomerName = '";
						strSQL += strReceiveCommentPersonName;
						strSQL += "' and TaskType = '1'";
						stmt.executeUpdate(strSQL);
					}
					// �����±�־��Ϊ����״̬
					strSQL = "update customer_info_table";
					strSQL += " set UpdateSignal = '1' where Account = '";
					strSQL += strReceiveCommentPersonName;
					strSQL += "'";
					stmt.executeUpdate(strSQL);
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
				// ��ȡ��ǰʱ��
				SimpleDateFormat formatter = new SimpleDateFormat(
						"yyyy��MM��dd��HH:mm:ss");
				Date curDate = new Date(System.currentTimeMillis());// ��ȡ��ǰʱ��
				String strTaskAnnounceTime = formatter.format(curDate);

				// �����ݲ��뵽����
				strSQL = "insert into ";
				strSQL += strCommentTableName;
				strSQL += " values('";
				strSQL += strTaskId;
				strSQL += "','";
				strSQL += strTaskAnnounceTime;
				strSQL += "','";
				strSQL += strCommentPersonNickName;
				strSQL += "','";
				strSQL += strReceiveCommentPersonNickName;
				strSQL += "','";
				strSQL += strCommentContent;
				strSQL += "','','','','0','";
				strSQL += strCommentPersonName;
				strSQL += "','";
				strSQL += strReceiveCommentPersonName;
				strSQL += "','";
				strSQL += nSecretType;
				strSQL += "')";
				stmt.execute(strSQL);

				nRet = 1;
			} else if (strType.equals("2")) {
				// �ȸ���strTaskId��ȡ�����۱�
				String strSQL = "select * from task_share_opera_table";
				strSQL += " where TaskKey = '";
				strSQL += nTaskId;
				strSQL += "'";
				rs = stmt.executeQuery(strSQL);
				while (rs.next()) {
					strCommentTableName = rs.getString(9);
					// strTaskAnnouncePersonName = rs.getString(2);
					break;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
				// ���ȵ����в����ǲ����������˹����������ļ�¼
				if (!strCommentPersonName.equals("")) {
					strSQL = "select * from customer_comment_table where TaskKey = '";
					strSQL += strTaskId;
					strSQL += "' and CustomerName = '";
					strSQL += strCommentPersonName;
					strSQL += "' and TaskType = '2'";
					rs = stmt.executeQuery(strSQL);
					while (rs.next()) {
						bFind = true;
						break;
					}
					// ���û���ҵ�,��ô�ͽ�������¼���뵽����
					if (!bFind) {
						strSQL = "insert into customer_comment_table values('";
						strSQL += nTaskId;
						strSQL += "','";
						strSQL += strCommentPersonName;
						strSQL += "','1','2')";
						stmt.execute(strSQL);
					}
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
				bFind = false;
				// ���ұ����Ƿ��н������۵��˼�¼,���û�оͲ�����Ӧ��¼
				if (!strReceiveCommentPersonName.equals("")) {
					strSQL = "select * from customer_comment_table where TaskKey = '";
					strSQL += strTaskId;
					strSQL += "' and CustomerName = '";
					strSQL += strReceiveCommentPersonName;
					strSQL += "' and TaskType = '2'";
					rs = stmt.executeQuery(strSQL);
					while (rs.next()) {
						bFind = true;
						break;
					}
					// ���û���ҵ�,��ô�ͽ�������¼���뵽����
					if (!bFind) {
						strSQL = "insert into customer_comment_table values('";
						strSQL += nTaskId;
						strSQL += "','";
						strSQL += strReceiveCommentPersonName;
						strSQL += "','2','2')";
						stmt.execute(strSQL);
					} else {// ����ҵ���,�͸���״̬
						strSQL = "update customer_comment_table";
						strSQL += " set TaskCommentType = '2' where TaskKey = '";
						strSQL += strTaskId;
						strSQL += "' and CustomerName = '";
						strSQL += strReceiveCommentPersonName;
						strSQL += "' and TaskType = '2'";
						stmt.executeUpdate(strSQL);
					}
					// �����±�־��Ϊ����״̬
					strSQL = "update customer_info_table";
					strSQL += " set UpdateSignal = '1' where Account = '";
					strSQL += strReceiveCommentPersonName;
					strSQL += "'";
					stmt.executeUpdate(strSQL);
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
				// ��ȡ��ǰʱ��
				SimpleDateFormat formatter = new SimpleDateFormat(
						"yyyy��MM��dd��HH:mm:ss");
				Date curDate = new Date(System.currentTimeMillis());// ��ȡ��ǰʱ��
				String strTaskAnnounceTime = formatter.format(curDate);

				// �����ݲ��뵽����
				strSQL = "insert into ";
				strSQL += strCommentTableName;
				strSQL += " values('";
				strSQL += nTaskId;
				strSQL += "','";
				strSQL += strTaskAnnounceTime;
				strSQL += "','";
				strSQL += strCommentPersonNickName;
				strSQL += "','";
				strSQL += strReceiveCommentPersonNickName;
				strSQL += "','";
				strSQL += strCommentContent;
				strSQL += "','','','','0','";
				strSQL += strCommentPersonName;
				strSQL += "','";
				strSQL += strReceiveCommentPersonName;
				strSQL += "','";
				strSQL += nSecretType;
				strSQL += "')";
				stmt.execute(strSQL);

				nRet = 1;
			}
		} catch (Exception e) {
			// System.out.print("get data error!");
			// e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connect != null) {
				try {
					connect.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return nRet;
	}

	@Override
	public int SendCommentContentNew(String strTaskId,
			String strCommentPersonName, String strReceiveCommentPersonName,
			String strCommentContent, String strType,
			String strCommentPersonNickName,
			String strReceiveCommentPersonNickName) {
		int nRet = 0;
		// String strTime = "";
		Connection connect = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			connect = GetConnection.getSimpleConnection();
			stmt = connect.createStatement();
			// ���۱�����
			String strCommentTableName = "";
			// ���񷢲�������
			// String strTaskAnnouncePersonName = "";
			boolean bFind = false;
			int nTaskId = Integer.parseInt(strTaskId);
			if (strType.equals("1")) {
				// �ȸ���strTaskId��ȡ�����۱�
				String strSQL = "select * from task_help_opera_table";
				strSQL += " where TaskKey = '";
				strSQL += nTaskId;
				strSQL += "'";
				rs = stmt.executeQuery(strSQL);
				while (rs.next()) {
					strCommentTableName = rs.getString(9);
					// strTaskAnnouncePersonName = rs.getString(2);
					break;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
				// ���ȵ����в����ǲ����������˹����������ļ�¼
				if (!strCommentPersonName.equals("")) {
					strSQL = "select * from customer_comment_table where TaskKey = '";
					strSQL += strTaskId;
					strSQL += "' and CustomerName = '";
					strSQL += strCommentPersonName;
					strSQL += "' and TaskType = '1'";
					rs = stmt.executeQuery(strSQL);
					while (rs.next()) {
						bFind = true;
						break;
					}
					// ���û���ҵ�,��ô�ͽ�������¼���뵽����
					if (!bFind) {
						strSQL = "insert into customer_comment_table values('";
						strSQL += nTaskId;
						strSQL += "','";
						strSQL += strCommentPersonName;
						strSQL += "','1','1')";
						stmt.execute(strSQL);
					}
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
				bFind = false;
				// ���ұ����Ƿ��н������۵��˼�¼,���û�оͲ�����Ӧ��¼
				if (!strReceiveCommentPersonName.equals("")) {
					strSQL = "select * from customer_comment_table where TaskKey = '";
					strSQL += strTaskId;
					strSQL += "' and CustomerName = '";
					strSQL += strReceiveCommentPersonName;
					strSQL += "' and TaskType = '1'";
					rs = stmt.executeQuery(strSQL);
					while (rs.next()) {
						bFind = true;
						break;
					}
					// ���û���ҵ�,��ô�ͽ�������¼���뵽����
					if (!bFind) {
						strSQL = "insert into customer_comment_table values('";
						strSQL += nTaskId;
						strSQL += "','";
						strSQL += strReceiveCommentPersonName;
						strSQL += "','2','1')";
						stmt.execute(strSQL);
					} else {// ����ҵ���,�͸���״̬
						strSQL = "update customer_comment_table";
						strSQL += " set TaskCommentType = '2' where TaskKey = '";
						strSQL += strTaskId;
						strSQL += "' and CustomerName = '";
						strSQL += strReceiveCommentPersonName;
						strSQL += "' and TaskType = '1'";
						stmt.executeUpdate(strSQL);
					}
					// �����±�־��Ϊ����״̬
					strSQL = "update customer_info_table";
					strSQL += " set UpdateSignal = '1' where Account = '";
					strSQL += strReceiveCommentPersonName;
					strSQL += "'";
					stmt.executeUpdate(strSQL);
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
				// ��ȡ��ǰʱ��
				SimpleDateFormat formatter = new SimpleDateFormat(
						"yyyy��MM��dd��HH:mm:ss");
				Date curDate = new Date(System.currentTimeMillis());// ��ȡ��ǰʱ��
				String strTaskAnnounceTime = formatter.format(curDate);

				// �����ݲ��뵽����
				strSQL = "insert into ";
				strSQL += strCommentTableName;
				strSQL += " values('";
				strSQL += strTaskId;
				strSQL += "','";
				strSQL += strTaskAnnounceTime;
				strSQL += "','";
				strSQL += strCommentPersonNickName;
				strSQL += "','";
				strSQL += strReceiveCommentPersonNickName;
				strSQL += "','";
				strSQL += strCommentContent;
				strSQL += "','','','','0','";
				strSQL += strCommentPersonName;
				strSQL += "','";
				strSQL += strReceiveCommentPersonName;
				strSQL += "','0')";
				stmt.execute(strSQL);

				nRet = 1;
			} else if (strType.equals("2")) {
				// �ȸ���strTaskId��ȡ�����۱�
				String strSQL = "select * from task_share_opera_table";
				strSQL += " where TaskKey = '";
				strSQL += nTaskId;
				strSQL += "'";
				rs = stmt.executeQuery(strSQL);
				while (rs.next()) {
					strCommentTableName = rs.getString(9);
					// strTaskAnnouncePersonName = rs.getString(2);
					break;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
				// ���ȵ����в����ǲ����������˹����������ļ�¼
				if (!strCommentPersonName.equals("")) {
					strSQL = "select * from customer_comment_table where TaskKey = '";
					strSQL += strTaskId;
					strSQL += "' and CustomerName = '";
					strSQL += strCommentPersonName;
					strSQL += "' and TaskType = '2'";
					rs = stmt.executeQuery(strSQL);
					while (rs.next()) {
						bFind = true;
						break;
					}
					// ���û���ҵ�,��ô�ͽ�������¼���뵽����
					if (!bFind) {
						strSQL = "insert into customer_comment_table values('";
						strSQL += nTaskId;
						strSQL += "','";
						strSQL += strCommentPersonName;
						strSQL += "','1','2')";
						stmt.execute(strSQL);
					}
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
				bFind = false;
				// ���ұ����Ƿ��н������۵��˼�¼,���û�оͲ�����Ӧ��¼
				if (!strReceiveCommentPersonName.equals("")) {
					strSQL = "select * from customer_comment_table where TaskKey = '";
					strSQL += strTaskId;
					strSQL += "' and CustomerName = '";
					strSQL += strReceiveCommentPersonName;
					strSQL += "' and TaskType = '2'";
					rs = stmt.executeQuery(strSQL);
					while (rs.next()) {
						bFind = true;
						break;
					}
					// ���û���ҵ�,��ô�ͽ�������¼���뵽����
					if (!bFind) {
						strSQL = "insert into customer_comment_table values('";
						strSQL += nTaskId;
						strSQL += "','";
						strSQL += strReceiveCommentPersonName;
						strSQL += "','2','2')";
						stmt.execute(strSQL);
					} else {// ����ҵ���,�͸���״̬
						strSQL = "update customer_comment_table";
						strSQL += " set TaskCommentType = '2' where TaskKey = '";
						strSQL += strTaskId;
						strSQL += "' and CustomerName = '";
						strSQL += strReceiveCommentPersonName;
						strSQL += "' and TaskType = '2'";
						stmt.executeUpdate(strSQL);
					}
					// �����±�־��Ϊ����״̬
					strSQL = "update customer_info_table";
					strSQL += " set UpdateSignal = '1' where Account = '";
					strSQL += strReceiveCommentPersonName;
					strSQL += "'";
					stmt.executeUpdate(strSQL);
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
				// ��ȡ��ǰʱ��
				SimpleDateFormat formatter = new SimpleDateFormat(
						"yyyy��MM��dd��HH:mm:ss");
				Date curDate = new Date(System.currentTimeMillis());// ��ȡ��ǰʱ��
				String strTaskAnnounceTime = formatter.format(curDate);

				// �����ݲ��뵽����
				strSQL = "insert into ";
				strSQL += strCommentTableName;
				strSQL += " values('";
				strSQL += nTaskId;
				strSQL += "','";
				strSQL += strTaskAnnounceTime;
				strSQL += "','";
				strSQL += strCommentPersonNickName;
				strSQL += "','";
				strSQL += strReceiveCommentPersonNickName;
				strSQL += "','";
				strSQL += strCommentContent;
				strSQL += "','','','','0','";
				strSQL += strCommentPersonName;
				strSQL += "','";
				strSQL += strReceiveCommentPersonName;
				strSQL += "','0')";
				stmt.execute(strSQL);

				nRet = 1;
			}
		} catch (Exception e) {
			// System.out.print("get data error!");
			// e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connect != null) {
				try {
					connect.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return nRet;
	}

	@Override
	public int SendCommentImageNew(String strTaskId,
			String strCommentPersonName, String strReceiveCommentPersonName,
			String strSmallImage, String strLargeImage, String strType,
			String strCommentPersonNickName,
			String strReceiveCommentPersonNickName) {
		int nRet = 0;
		Connection connect = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			connect = GetConnection.getSimpleConnection();
			stmt = connect.createStatement();
			// ���۱�����
			String strCommentTableName = "";
			// ���񷢲�������
			// String strTaskAnnouncePersonName = "";
			int nTaskId = Integer.parseInt(strTaskId);
			boolean bFind = false;
			if (strType.equals("1")) {
				// �ȸ���strTaskId��ȡ�����۱�
				String strSQL = "select * from task_help_opera_table";
				strSQL += " where TaskKey = '";
				strSQL += nTaskId;
				strSQL += "'";
				rs = stmt.executeQuery(strSQL);
				while (rs.next()) {
					strCommentTableName = rs.getString(9);
					// strTaskAnnouncePersonName = rs.getString(2);
					break;
				}
				// ���ȵ����в����ǲ����������˹����������ļ�¼
				if (!strCommentPersonName.equals("")) {
					strSQL = "select * from customer_comment_table where TaskKey = '";
					strSQL += strTaskId;
					strSQL += "' and CustomerName = '";
					strSQL += strCommentPersonName;
					strSQL += "' and TaskType = '1'";
					rs = stmt.executeQuery(strSQL);
					while (rs.next()) {
						bFind = true;
						break;
					}
					// ���û���ҵ�,��ô�ͽ�������¼���뵽����
					if (!bFind) {
						strSQL = "insert into customer_comment_table values('";
						strSQL += nTaskId;
						strSQL += "','";
						strSQL += strCommentPersonName;
						strSQL += "','1','1')";
						stmt.execute(strSQL);
					}
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
				bFind = false;
				// ���ұ����Ƿ��н������۵��˼�¼,���û�оͲ�����Ӧ��¼
				if (!strReceiveCommentPersonName.equals("")) {
					strSQL = "select * from customer_comment_table where TaskKey = '";
					strSQL += strTaskId;
					strSQL += "' and CustomerName = '";
					strSQL += strReceiveCommentPersonName;
					strSQL += "' and TaskType = '1'";
					rs = stmt.executeQuery(strSQL);
					while (rs.next()) {
						bFind = true;
						break;
					}
					// ���û���ҵ�,��ô�ͽ�������¼���뵽����
					if (!bFind) {
						strSQL = "insert into customer_comment_table values('";
						strSQL += nTaskId;
						strSQL += "','";
						strSQL += strReceiveCommentPersonName;
						strSQL += "','2','1')";
						stmt.execute(strSQL);
					} else {// ����ҵ���,�͸���״̬
						strSQL = "update customer_comment_table";
						strSQL += " set TaskCommentType = '2' where TaskKey = '";
						strSQL += strTaskId;
						strSQL += "' and CustomerName = '";
						strSQL += strReceiveCommentPersonName;
						strSQL += "' and TaskType = '1'";
						stmt.executeUpdate(strSQL);
					}
					// �����±�־��Ϊ����״̬
					strSQL = "update customer_info_table";
					strSQL += " set UpdateSignal = '1' where Account = '";
					strSQL += strReceiveCommentPersonName;
					strSQL += "'";
					stmt.executeUpdate(strSQL);
				}
				// ��ȡ��ǰʱ��
				SimpleDateFormat formatter = new SimpleDateFormat(
						"yyyy��MM��dd��HH:mm:ss");
				Date curDate = new Date(System.currentTimeMillis());// ��ȡ��ǰʱ��
				String strTaskAnnounceTime = formatter.format(curDate);

				if (rs != null) {
					rs.close();
					rs = null;
				}
				strSQL = "select * from ";
				strSQL += strCommentTableName;
				rs = stmt.executeQuery(strSQL);
				int nCount = 0;
				while (rs.next()) {
					rs.last();
					nCount = rs.getRow();
					break;
				}
				nCount++;
				// �����ݲ��뵽����
				strSQL = "insert into ";
				strSQL += strCommentTableName;
				strSQL += " values('";
				strSQL += strTaskId;
				strSQL += "','";
				strSQL += strTaskAnnounceTime;
				strSQL += "','";
				strSQL += strCommentPersonNickName;
				strSQL += "','";
				strSQL += strReceiveCommentPersonNickName;
				strSQL += "','','','";
				strSQL += strSmallImage;
				strSQL += "','";
				strSQL += strLargeImage;
				strSQL += "','";
				strSQL += nCount;
				strSQL += "','";
				strSQL += strCommentPersonName;
				strSQL += "','";
				strSQL += strReceiveCommentPersonName;
				strSQL += "','0')";
				stmt.execute(strSQL);

				nRet = nCount;
			} else if (strType.equals("2")) {
				// �ȸ���strTaskId��ȡ�����۱�
				String strSQL = "select * from task_share_opera_table";
				strSQL += " where TaskKey = '";
				strSQL += nTaskId;
				strSQL += "'";
				rs = stmt.executeQuery(strSQL);
				while (rs.next()) {
					strCommentTableName = rs.getString(9);
					// strTaskAnnouncePersonName = rs.getString(2);
					break;
				}
				// �ȸ���strTaskId��ȡ�����۱�
				strSQL = "select * from task_share_opera_table";
				strSQL += " where TaskKey = '";
				strSQL += nTaskId;
				strSQL += "'";
				rs = stmt.executeQuery(strSQL);
				while (rs.next()) {
					strCommentTableName = rs.getString(9);
					// strTaskAnnouncePersonName = rs.getString(2);
					break;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
				// ���ȵ����в����ǲ����������˹����������ļ�¼
				if (!strCommentPersonName.equals("")) {
					strSQL = "select * from customer_comment_table where TaskKey = '";
					strSQL += strTaskId;
					strSQL += "' and CustomerName = '";
					strSQL += strCommentPersonName;
					strSQL += "' and TaskType = '2'";
					rs = stmt.executeQuery(strSQL);
					while (rs.next()) {
						bFind = true;
						break;
					}
					// ���û���ҵ�,��ô�ͽ�������¼���뵽����
					if (!bFind) {
						strSQL = "insert into customer_comment_table values('";
						strSQL += nTaskId;
						strSQL += "','";
						strSQL += strCommentPersonName;
						strSQL += "','1','2')";
						stmt.execute(strSQL);
					}
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
				bFind = false;
				// ���ұ����Ƿ��н������۵��˼�¼,���û�оͲ�����Ӧ��¼
				if (!strReceiveCommentPersonName.equals("")) {
					strSQL = "select * from customer_comment_table where TaskKey = '";
					strSQL += strTaskId;
					strSQL += "' and CustomerName = '";
					strSQL += strReceiveCommentPersonName;
					strSQL += "' and TaskType = '2'";
					rs = stmt.executeQuery(strSQL);
					while (rs.next()) {
						bFind = true;
						break;
					}
					// ���û���ҵ�,��ô�ͽ�������¼���뵽����
					if (!bFind) {
						strSQL = "insert into customer_comment_table values('";
						strSQL += nTaskId;
						strSQL += "','";
						strSQL += strReceiveCommentPersonName;
						strSQL += "','2','2')";
						stmt.execute(strSQL);
					} else {// ����ҵ���,�͸���״̬
						strSQL = "update customer_comment_table";
						strSQL += " set TaskCommentType = '2' where TaskKey = '";
						strSQL += strTaskId;
						strSQL += "' and CustomerName = '";
						strSQL += strReceiveCommentPersonName;
						strSQL += "' and TaskType = '2'";
						stmt.executeUpdate(strSQL);
					}
					// �����±�־��Ϊ����״̬
					strSQL = "update customer_info_table";
					strSQL += " set UpdateSignal = '1' where Account = '";
					strSQL += strReceiveCommentPersonName;
					strSQL += "'";
					stmt.executeUpdate(strSQL);
				}
				// ��ȡ��ǰʱ��
				SimpleDateFormat formatter = new SimpleDateFormat(
						"yyyy��MM��dd��HH:mm:ss");
				Date curDate = new Date(System.currentTimeMillis());// ��ȡ��ǰʱ��
				String strTaskAnnounceTime = formatter.format(curDate);

				if (rs != null) {
					rs.close();
					rs = null;
				}
				strSQL = "select * from ";
				strSQL += strCommentTableName;
				rs = stmt.executeQuery(strSQL);
				int nCount = 0;
				while (rs.next()) {
					rs.last();
					nCount = rs.getRow();
					break;
				}
				nCount++;
				// �����ݲ��뵽����
				strSQL = "insert into ";
				strSQL += strCommentTableName;
				strSQL += " values('";
				strSQL += strTaskId;
				strSQL += "','";
				strSQL += strTaskAnnounceTime;
				strSQL += "','";
				strSQL += strCommentPersonNickName;
				strSQL += "','";
				strSQL += strReceiveCommentPersonNickName;
				strSQL += "','','','";
				strSQL += strSmallImage;
				strSQL += "','";
				strSQL += strLargeImage;
				strSQL += "','";
				strSQL += nCount;
				strSQL += "','";
				strSQL += strCommentPersonName;
				strSQL += "','";
				strSQL += strReceiveCommentPersonName;
				strSQL += "','0')";
				stmt.execute(strSQL);

				nRet = nCount;
			}
		} catch (Exception e) {
			// System.out.print("get data error!");
			// e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connect != null) {
				try {
					connect.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return nRet;
	}

	@Override
	public int PraiseToTaskOrShare(String strTaskId, String strPersonName,
			String strReceivePersonName, String strType) {
		int nRet = 0;
		Connection connect = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			connect = GetConnection.getSimpleConnection();
			stmt = connect.createStatement();
			int nId = Integer.parseInt(strTaskId);
			// ���ж�֮ǰ�ǲ��ǵ����
			String strSQL = "select * from customer_praise_table where TaskKey = '";
			strSQL += strTaskId;
			strSQL += "' and PraisePersonName = '";
			strSQL += strPersonName;
			strSQL += "' and TaskType = '";
			strSQL += strType;
			strSQL += "'";
			rs = stmt.executeQuery(strSQL);
			// �������
			while (rs.next()) {
				nRet = 2;
				break;
			}
			if (rs != null) {
				rs.close();
				rs = null;
			}
			// ���û���ҵ�
			if (nRet != 2) {
				int nSumCreditValue = 0;
				// ���Ƚ���ֵ��1
				// ��ȡ����ֵ
				strSQL = "select * from customer_info_table where Account = '";
				strSQL += strReceivePersonName;
				strSQL += "'";
				rs = stmt.executeQuery(strSQL);
				while (rs.next()) {
					nSumCreditValue = rs.getInt(14) + 1;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
				// �����±�־��Ϊ����״̬
				strSQL = "update customer_info_table";
				strSQL += " set CharmValue = '";
				strSQL += nSumCreditValue;
				strSQL += "' where Account = '";
				strSQL += strReceivePersonName;
				strSQL += "'";
				stmt.executeUpdate(strSQL);
				// ��������ͷ����е���ֵ
				if (strType.equals("1")) {
					int nCharmValue = 0;
					// �Ȼ�ȡ���������ֵ
					strSQL = "select TaskCharmValue from task_help_opera_table where TaskKey = '";
					strSQL += nId;
					strSQL += "'";
					rs = stmt.executeQuery(strSQL);
					while (rs.next()) {
						nCharmValue = rs.getInt(1);
						nCharmValue++;
					}
					if (nCharmValue > 0) {
						strSQL = "update task_help_opera_table set TaskCharmValue = '";
						strSQL += nCharmValue;
						strSQL += "' where TaskKey = '";
						strSQL += nId;
						strSQL += "'";
						stmt.executeUpdate(strSQL);
					}

				} else if (strType.equals("2")) {
					int nCharmValue = 0;
					// �Ȼ�ȡ���������ֵ
					strSQL = "select TaskCharmValue from task_share_opera_table where TaskKey = '";
					strSQL += nId;
					strSQL += "'";
					rs = stmt.executeQuery(strSQL);
					while (rs.next()) {
						nCharmValue = rs.getInt(1);
						nCharmValue++;
					}
					if (nCharmValue > 0) {
						strSQL = "update task_share_opera_table set TaskCharmValue = '";
						strSQL += nCharmValue;
						strSQL += "' where TaskKey = '";
						strSQL += nId;
						strSQL += "'";
						stmt.executeUpdate(strSQL);
					}
				}
				nRet = 1;

				int nType = Integer.parseInt(strType);
				// ������޼�¼
				strSQL = "insert into customer_praise_table values('";
				strSQL += nId;
				strSQL += "','";
				strSQL += strPersonName;
				strSQL += "','";
				strSQL += nType;
				strSQL += "')";
				stmt.execute(strSQL);
			}
		} catch (Exception e) {
			// System.out.print("get data error!");
			// e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connect != null) {
				try {
					connect.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return nRet;
	}

	@Override
	public int AddBrowseTimes(String strTaskId, String strType) {
		int nRet = 0;
		Connection connect = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			connect = GetConnection.getSimpleConnection();
			stmt = connect.createStatement();
			int nId = Integer.parseInt(strTaskId);
			int nBrowseTime = 0;
			// �Ȼ�ȡ�������
			if (strType.equals("1")) {
				String strSQL = "select BrowseTime from task_help_opera_table where"
						+ " TaskKey = '";
				strSQL += nId;
				strSQL += "'";
				rs = stmt.executeQuery(strSQL);
				while (rs.next()) {
					nBrowseTime = rs.getInt(1);
					nBrowseTime += 1;
				}
				// ����ҵ���
				if (nBrowseTime > 0) {
					// �����������
					strSQL = "update task_help_opera_table";
					strSQL += " set BrowseTime = '";
					strSQL += nBrowseTime;
					strSQL += "' where TaskKey = '";
					strSQL += nId;
					strSQL += "'";
					stmt.executeUpdate(strSQL);
					nRet = nBrowseTime;
				}

			} else if (strType.equals("2")) {
				String strSQL = "select BrowseTime from task_share_opera_table where"
						+ " TaskKey = '";
				strSQL += nId;
				strSQL += "'";
				rs = stmt.executeQuery(strSQL);
				while (rs.next()) {
					nBrowseTime = rs.getInt(1);
					nBrowseTime += 1;
				}
				// ����ҵ���
				if (nBrowseTime > 0) {
					// �����������
					strSQL = "update task_share_opera_table";
					strSQL += " set BrowseTime = '";
					strSQL += nBrowseTime;
					strSQL += "' where TaskKey = '";
					strSQL += nId;
					strSQL += "'";
					stmt.executeUpdate(strSQL);
					nRet = nBrowseTime;
				}
			}
		} catch (Exception e) {
			// System.out.print("get data error!");
			// e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connect != null) {
				try {
					connect.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return nRet;
	}

	@Override
	public List<TaskInfoDetail> GetMsgInfoNumNewDetail(String strAnnounceName) {
		List<TaskInfoDetail> dynamicnews = new ArrayList<TaskInfoDetail>();
		int nNum = 0;
		Connection connect = null;
		Statement stmt = null;
		ResultSet rs = null;
		Statement stmt1 = null;
		ResultSet rs1 = null;
		Statement stmt2 = null;
		ResultSet rs2 = null;
		try {
			connect = GetConnection.getSimpleConnection();
			stmt = connect.createStatement();
			stmt1 = connect.createStatement();
			stmt2 = connect.createStatement();
			// ��������
			int nTaskType = 0;
			// ����id��
			int nTaskId = 0;
			// ��Ϣ״̬
			int nMessageStatus = 0;
			// ��ȡ����Ϣ������
			String strSQL = "select * from customer_comment_table where CustomerName = '";
			strSQL += strAnnounceName;
			strSQL += "' and TaskCommentType = '2'";
			rs = stmt.executeQuery(strSQL);
			while (rs.next()) {
				rs.last();
				nNum = rs.getRow();
			}
			if (rs != null) {
				rs.close();
				rs = null;
			}

			// ���ݱ�customer_comment_table��ȡ��ָ���û���ص�����
			strSQL = "select * from customer_comment_table where CustomerName = '";
			strSQL += strAnnounceName;
			strSQL += "'";
			rs = stmt.executeQuery(strSQL);
			rs.afterLast();
			while (rs.previous()) {
				nTaskType = rs.getInt(4);
				nTaskId = rs.getInt(1);
				nMessageStatus = rs.getInt(3);
				if (1 == nTaskType) {
					// ��Ϣ״̬
					int nStatus = nMessageStatus;
					String strName = "";
					// ��ȡϵͳ��ǰʱ��
					SimpleDateFormat formatter = new SimpleDateFormat(
							"yyyy��MM��dd��HH:mm:ss");
					Date curDate = new Date(System.currentTimeMillis());// ��ȡ��ǰʱ��
					String strCurrentTime = formatter.format(curDate);

					strSQL = "select * from task_help_opera_table where TaskKey = '";
					strSQL += nTaskId;
					strSQL += "'";
					rs1 = stmt1.executeQuery(strSQL);
					while (rs1.next()) {
						// ����id��
						int nid = rs1.getInt(1);
						// ������ߵ�id����ͬ
						if (nTaskId != nid) {
							nStatus = 1;
						}
						strName = rs1.getString(2);

						// �������������˵�ͷ��
						String strAnnounceImage = "";
						// ��ȡ�û�ͷ��
						strSQL = "select * from customer_info_table where Account = '";
						strSQL += strName;
						strSQL += "'";
						rs2 = stmt2.executeQuery(strSQL);
						while (rs2.next()) {
							strAnnounceImage = rs2.getString(1);
						}
						if (rs2 != null) {
							rs2.close();
							rs2 = null;
						}

						int nRecordNum = 0;
						// ��ȡ��������
						String strCommentTableName = rs1.getString(9);
						strSQL = "select count(*) from ";
						strSQL += strCommentTableName;
						rs2 = stmt2.executeQuery(strSQL);
						while (rs2.next()) {
							nRecordNum = rs2.getInt(1);
						}

						if (rs2 != null) {
							rs2.close();
							rs2 = null;
						}

						// ���񷢲�ʱ��
						String strAnnounceTime = rs1.getString(18);
						Date d1 = formatter.parse(strCurrentTime);
						Date d2 = formatter.parse(strAnnounceTime);
						long diff = d1.getTime() - d2.getTime();// �����õ��Ĳ�ֵ��΢�뼶��

						TaskInfoDetail task = new TaskInfoDetail(
								rs1.getString(16), strAnnounceImage,
								rs1.getString(28), rs1.getString(15),
								rs1.getInt(1) + "", rs1.getDouble(20),
								rs1.getDouble(21), strAnnounceTime,
								rs1.getString(23), rs1.getString(17), diff
										/ 1000 + "", rs1.getString(29),
								rs1.getInt(24), rs1.getInt(25), rs1.getInt(14),
								rs1.getInt(4), rs1.getInt(5), rs1.getInt(6),
								nStatus, rs1.getInt(8), nNum,
								rs1.getString(10), rs1.getString(11),
								rs1.getString(12), rs1.getString(13), strName,
								rs1.getString(3), rs1.getInt(30),
								rs1.getInt(31), rs1.getInt(33), nRecordNum,
								rs1.getInt(32));
						dynamicnews.add(task);
					}
					if (rs1 != null) {
						rs1.close();
						rs1 = null;
					}

				} else if (2 == nTaskType) {

					if (rs1 != null) {
						rs1.close();
						rs1 = null;
					}
					// ��Ϣ״̬
					int nStatus = nMessageStatus;
					String strName = "";
					// ��ȡϵͳ��ǰʱ��
					SimpleDateFormat formatter = new SimpleDateFormat(
							"yyyy��MM��dd��HH:mm:ss");
					Date curDate = new Date(System.currentTimeMillis());// ��ȡ��ǰʱ��
					String strCurrentTime = formatter.format(curDate);

					strSQL = "select * from task_share_opera_table where TaskKey = '";
					strSQL += nTaskId;
					strSQL += "'";
					rs1 = stmt1.executeQuery(strSQL);
					while (rs1.next()) {
						// ����id��
						int nid = rs1.getInt(1);
						// ������ߵ�id����ͬ,˵������������ɵ�ǰ�û�������
						if (nTaskId != nid) {
							nStatus = 1;
						}
						strName = rs1.getString(2);

						// �������������˵�ͷ��
						String strAnnounceImage = "";
						// ��ȡ�û�ͷ��
						strSQL = "select * from customer_info_table where Account = '";
						strSQL += strName;
						strSQL += "'";
						rs2 = stmt2.executeQuery(strSQL);
						while (rs2.next()) {
							strAnnounceImage = rs2.getString(1);
						}
						if (rs2 != null) {
							rs2.close();
							rs2 = null;
						}

						int nRecordNum = 0;
						// ��ȡ��������
						String strCommentTableName = rs1.getString(9);
						strSQL = "select count(*) from ";
						strSQL += strCommentTableName;
						rs2 = stmt2.executeQuery(strSQL);
						while (rs2.next()) {
							nRecordNum = rs2.getInt(1);
						}
						if (rs2 != null) {
							rs2.close();
							rs2 = null;
						}
						// ���񷢲�ʱ��
						String strAnnounceTime = rs1.getString(18);
						Date d1 = formatter.parse(strCurrentTime);
						Date d2 = formatter.parse(strAnnounceTime);
						long diff = d1.getTime() - d2.getTime();// �����õ��Ĳ�ֵ��΢�뼶��

						TaskInfoDetail task = new TaskInfoDetail(
								rs1.getString(16), strAnnounceImage,
								rs1.getString(28), rs1.getString(15),
								rs1.getInt(1) + "", rs1.getDouble(20),
								rs1.getDouble(21), strAnnounceTime,
								rs1.getString(23), rs1.getString(17), diff
										/ 1000 + "", rs1.getString(29),
								rs1.getInt(24), rs1.getInt(25), rs1.getInt(14),
								rs1.getInt(4), rs1.getInt(5), rs1.getInt(6),
								nStatus, rs1.getInt(8), nNum,
								rs1.getString(10), rs1.getString(11),
								rs1.getString(12), rs1.getString(13), strName,
								rs1.getString(3), rs1.getInt(30),
								rs1.getInt(31), rs1.getInt(33), nRecordNum,
								rs1.getInt(32));
						dynamicnews.add(task);
					}
					if (rs1 != null) {
						rs1.close();
						rs1 = null;
					}
				}
			}

		} catch (Exception e) {
			// System.out.print("get data error!");
			// e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (rs1 != null) {
				try {
					rs1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt1 != null) {
				try {
					stmt1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (rs2 != null) {
				try {
					rs1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt2 != null) {
				try {
					stmt1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connect != null) {
				try {
					connect.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return dynamicnews;
	}

	@Override
	public int AddTaskInfoForLimitNew(String strTaskType, String strRegionName,
			String strTaskTitle, String strTaskDeatial,
			String strTaskAnnounceTime, String strTaskImpleTime,
			String strTaskAccountName, String strImplementAccountName,
			String strTaskAccountTrueName, String strImplementAccountTrueName,
			String strLongitude, String strLatidude, String strTaskAccountIcon,
			String strLimitTime, String strIcon1, String strIcon2,
			String strIcon3, String strIcon4, String strIcon5, String strIcon6,
			int nCreditValue, int nCharmValue, String strDistrictName,
			String strCityName) {
		int nRetType = 0;// Ϊ0��ʾʧ��
		Connection connect = null;
		Statement stmt = null;
		ResultSet rs = null;
		int nTaskCount = 0;
		try {
			connect = GetConnection.getSimpleConnection();
			stmt = connect.createStatement();
			String strSQL = "";
			// ���ж�Ҫע����˺��Ƿ����
			strSQL = "select * from customer_info_table where Account = '";
			strSQL += strTaskAccountTrueName;
			strSQL += "'";
			rs = stmt.executeQuery(strSQL);
			boolean bFind = false;
			while (rs.next()) {
				bFind = true;
				break;
			}
			if (rs != null) {
				rs.close();
				rs = null;
			}

			if (bFind) {
				// ���������Ϊ��������
				if (strTaskType.equals("1")) {
					// �Ȼ�ȡ��������
					strSQL = "select * from task_help_opera_table";
					rs = stmt.executeQuery(strSQL);
					while (rs.next()) {
						rs.last();
						nTaskCount = rs.getRow();
						break;
					}
					nTaskCount += 1;
					String strTaskCount = nTaskCount + "";
					// ���µ�������뵽���ݿ���
					SimpleDateFormat formatter = new SimpleDateFormat(
							"yyyy��MM��dd��HH:mm:ss");
					Date curDate = new Date(System.currentTimeMillis());// ��ȡ��ǰʱ��
					strTaskAnnounceTime = formatter.format(curDate);
					int nTaskType = Integer.parseInt(strTaskType);
					String strCommentTableName = strTaskCount + "_h1";
					// �������۱�
					strSQL = "create table if not exists ";
					strSQL += strCommentTableName;
					strSQL += "(";
					strSQL += "TaskKey varchar(20),TaskTalkTime varchar(20),"
							+ "TaskTalkPersonName varchar(20),"
							+ "TaskTalkAcceptPeraonName varchar(20),"
							+ "TaskTalkContent varchar(140),"
							+ "TaskAccountImage MEDIUMTEXT,"
							+ "TaskTalkPersonToAcceptPersonImage MEDIUMTEXT,"
							+ "TaskTalkPersonToAcceptPersonLargeImage MEDIUMTEXT,"
							+ "CommentIndex Integer(20),TaskTalkPersonTrueName varchar(20),"
							+ "TaskTalkAcceptPeraonTrueName varchar(20),"
							+ "SecretType Integer(1))";
					stmt.execute(strSQL);
					// ����Ӧ�����ݲ��뵽�����������
					strSQL = "insert into task_help_opera_table values('";
					strSQL += nTaskCount;
					strSQL += "','";
					strSQL += strTaskAccountTrueName;
					strSQL += "','";
					strSQL += strImplementAccountTrueName;
					strSQL += "','1','1','1','1','1','";
					strSQL += strCommentTableName;
					strSQL += "','','','','','";
					strSQL += nTaskType;
					strSQL += "','";
					strSQL += strTaskTitle;
					strSQL += "','";
					strSQL += strRegionName;
					strSQL += "','";
					strSQL += strTaskDeatial;
					strSQL += "','";
					strSQL += strTaskAnnounceTime;
					strSQL += "','";
					strSQL += strTaskImpleTime;
					strSQL += "','";
					strSQL += strLongitude;
					strSQL += "','";
					strSQL += strLatidude;
					strSQL += "','','";
					strSQL += strLimitTime;
					strSQL += "','1','1','0','0','";
					strSQL += strTaskAccountName;
					strSQL += "','";
					strSQL += strImplementAccountName;
					strSQL += "','";
					strSQL += nCreditValue;
					strSQL += "','";
					strSQL += nCharmValue;
					strSQL += "','0','0','";
					strSQL += strDistrictName;
					strSQL += "','";
					strSQL += strCityName;
					strSQL += "')";
					stmt.execute(strSQL);
					if (rs != null) {
						rs.close();
						rs = null;
					}
					// ������С��ͼƬ
					strSQL = "insert into task_help_smallicon values('";
					strSQL += nTaskCount;
					strSQL += "','";
					strSQL += strIcon1;
					strSQL += "','";
					strSQL += strIcon2;
					strSQL += "','";
					strSQL += strIcon3;
					strSQL += "','";
					strSQL += strIcon4;
					strSQL += "','";
					strSQL += strIcon5;
					strSQL += "','";
					strSQL += strIcon6;
					strSQL += "')";
					stmt.execute(strSQL);

					// ������Ӧ��¼
					strSQL = "insert into customer_comment_table values('";
					strSQL += nTaskCount;
					strSQL += "','";
					strSQL += strTaskAccountTrueName;
					strSQL += "','1','1')";
					stmt.execute(strSQL);
					// �����±�־��Ϊ����״̬,֪ͨ�����û�
					strSQL = "update customer_info_table";
					strSQL += " set UpdateSignal = '1'";
					stmt.executeUpdate(strSQL);

					nRetType = nTaskCount;
				} else if (strTaskType.equals("2")) {// ���������Ϊ����
					// �Ȼ�ȡ��������
					strSQL = "select * from task_share_opera_table";
					rs = stmt.executeQuery(strSQL);
					while (rs.next()) {
						rs.last();
						nTaskCount = rs.getRow();
						break;
					}
					nTaskCount += 1;
					String strTaskCount = nTaskCount + "";
					// ���µ�������뵽���ݿ���
					SimpleDateFormat formatter = new SimpleDateFormat(
							"yyyy��MM��dd��HH:mm:ss");
					Date curDate = new Date(System.currentTimeMillis());// ��ȡ��ǰʱ��
					strTaskAnnounceTime = formatter.format(curDate);
					int nTaskType = Integer.parseInt(strTaskType);
					String strCommentTableName = strTaskCount + "_s1";
					// �������۱�
					strSQL = "create table if not exists ";
					strSQL += strCommentTableName;
					strSQL += "(";
					strSQL += "TaskKey varchar(20),TaskTalkTime varchar(20),"
							+ "TaskTalkPersonName varchar(20),"
							+ "TaskTalkAcceptPeraonName varchar(20),"
							+ "TaskTalkContent varchar(140),"
							+ "TaskAccountImage MEDIUMTEXT,"
							+ "TaskTalkPersonToAcceptPersonImage MEDIUMTEXT,"
							+ "TaskTalkPersonToAcceptPersonLargeImage MEDIUMTEXT,"
							+ "CommentIndex Integer(20),"
							+ "TaskTalkPersonTrueName varchar(20),"
							+ "TaskTalkAcceptPeraonTrueName varchar(20),"
							+ "SecretType Integer(1))";
					stmt.execute(strSQL);
					// ����Ӧ�����ݲ��뵽�����������
					strSQL = "insert into task_share_opera_table values('";
					strSQL += nTaskCount;
					strSQL += "','";
					strSQL += strTaskAccountTrueName;
					strSQL += "','";
					strSQL += strImplementAccountTrueName;
					strSQL += "','1','1','1','1','1','";
					strSQL += strCommentTableName;
					strSQL += "','','','','','";
					strSQL += nTaskType;
					strSQL += "','";
					strSQL += strTaskTitle;
					strSQL += "','";
					strSQL += strRegionName;
					strSQL += "','";
					strSQL += strTaskDeatial;
					strSQL += "','";
					strSQL += strTaskAnnounceTime;
					strSQL += "','";
					strSQL += strTaskImpleTime;
					strSQL += "','";
					strSQL += strLongitude;
					strSQL += "','";
					strSQL += strLatidude;
					strSQL += "','','";
					strSQL += strLimitTime;
					strSQL += "','1','1','0','0','";
					strSQL += strTaskAccountName;
					strSQL += "','";
					strSQL += strImplementAccountName;
					strSQL += "','";
					strSQL += nCreditValue;
					strSQL += "','";
					strSQL += nCharmValue;
					strSQL += "','0','0','";
					strSQL += strDistrictName;
					strSQL += "','";
					strSQL += strCityName;
					strSQL += "')";
					stmt.execute(strSQL);
					if (rs != null) {
						rs.close();
						rs = null;
					}
					// ������С��ͼƬ
					strSQL = "insert into task_share_smallicon values('";
					strSQL += nTaskCount;
					strSQL += "','";
					strSQL += strIcon1;
					strSQL += "','";
					strSQL += strIcon2;
					strSQL += "','";
					strSQL += strIcon3;
					strSQL += "','";
					strSQL += strIcon4;
					strSQL += "','";
					strSQL += strIcon5;
					strSQL += "','";
					strSQL += strIcon6;
					strSQL += "')";
					stmt.execute(strSQL);

					// ������Ӧ��¼
					strSQL = "insert into customer_comment_table values('";
					strSQL += nTaskCount;
					strSQL += "','";
					strSQL += strTaskAccountTrueName;
					strSQL += "','1','2')";
					stmt.execute(strSQL);
					// �����±�־��Ϊ����״̬,֪ͨ�����û�
					strSQL = "update customer_info_table";
					strSQL += " set UpdateSignal = '1'";
					stmt.executeUpdate(strSQL);

					nRetType = nTaskCount;
				}
			}
		} catch (Exception e) {
			// System.out.print("get data error!");
			// e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connect != null) {
				try {
					connect.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return nRetType;
	}

	@Override
	public int UpdateItemDetailText(String strTaskId, String strType,
			String strText) {
		int nRet = 0;
		Connection connect = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			connect = GetConnection.getSimpleConnection();
			stmt = connect.createStatement();
			int nId = Integer.parseInt(strTaskId);
			// ���Ϊ����
			if (strType.equals("1")) {
				// �޸������е�������Ϣ
				String strSQL = "update task_help_opera_table set TaskDeail = '";
				strSQL += strText;
				strSQL += "' where TaskKey = '";
				strSQL += nId;
				strSQL += "'";
				stmt.executeUpdate(strSQL);
				nRet = 1;
			} else if (strType.equals("2")) {// ���Ϊ����
				String strSQL = "update task_share_opera_table set TaskDeail = '";
				strSQL += strText;
				strSQL += "' where TaskKey = '";
				strSQL += nId;
				strSQL += "'";
				stmt.executeUpdate(strSQL);
				nRet = 1;
			}
		} catch (Exception e) {
			// System.out.print("get data error!");
			// e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connect != null) {
				try {
					connect.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return nRet;
	}

	@Override
	public int UpdateItemTitleText(String strTaskId, String strType,
			String strText) {
		int nRet = 0;
		Connection connect = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			connect = GetConnection.getSimpleConnection();
			stmt = connect.createStatement();
			int nId = Integer.parseInt(strTaskId);
			// ���Ϊ����
			if (strType.equals("1")) {
				// �޸������е�������Ϣ
				String strSQL = "update task_help_opera_table set TaskTitle = '";
				strSQL += strText;
				strSQL += "' where TaskKey = '";
				strSQL += nId;
				strSQL += "'";
				stmt.executeUpdate(strSQL);
				nRet = 1;
			} else if (strType.equals("2")) {// ���Ϊ����
				String strSQL = "update task_share_opera_table set TaskTitle = '";
				strSQL += strText;
				strSQL += "' where TaskKey = '";
				strSQL += nId;
				strSQL += "'";
				stmt.executeUpdate(strSQL);
				nRet = 1;
			}
		} catch (Exception e) {
			// System.out.print("get data error!");
			// e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connect != null) {
				try {
					connect.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return nRet;
	}

	@Override
	public List<CommentInfo> GetCommentsForTaskNew1(String strTaskId,
			String strType, String strCustomerName, String strTime) {
		List<CommentInfo> commentinfos = new ArrayList<CommentInfo>();
		Connection connect = null;
		Statement stmt = null;
		Statement stmt1 = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		try {
			connect = GetConnection.getSimpleConnection();
			stmt = connect.createStatement();
			stmt1 = connect.createStatement();
			// ������
			String strAccountName = "";
			// �����˵�����
			String strCommentPersonName;
			// �������˵�����
			String strCommentReceivePersonName;
			// ����ʱ��
			String strCommentTime;
			// ��������
			String strCommentContent;
			// ѹ��ͼƬ
			String strSmallImage;
			// ���ۺ�
			int nCommentIndex;
			// �����˵�����
			String strCommentPersonTrueName;
			// �������˵�����
			String strCommentReceivePersonTrueName;
			// ������������(�Ƿ������Ļ�)
			int nCommentType;

			int nTaskId = Integer.parseInt(strTaskId);
			String strCommentTableName = "";
			if (strType.equals("1")) {
				// ��������id��ȡ�����۱�����
				String strSQL = "select * from task_help_opera_table";
				strSQL += " where TaskKey = '";
				strSQL += nTaskId;
				strSQL += "'";
				rs = stmt.executeQuery(strSQL);
				while (rs.next()) {
					strCommentTableName = rs.getString(9);
					strAccountName = rs.getString(2);
					break;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
				// ���ݻ�ȡ�������۱����ƻ�ȡ����Ӧ����
				strSQL = "select * from ";
				strSQL += strCommentTableName;
				strSQL += " where TaskTalkTime >= '";
				strSQL += strTime;
				strSQL += "' ORDER BY TaskTalkTime asc";
				rs = stmt.executeQuery(strSQL);
				while (rs.next()) {
					strCommentPersonTrueName = rs.getString(10);
					strCommentReceivePersonTrueName = rs.getString(11);
					// ��������(�Ƿ������Ļ�)
					nCommentType = rs.getInt(12);
					// �����ǰ�û������������۵������ߺͽ������Լ���ǰ�û����Ƿ�����,ͬʱ�������������Ļ�,��ô�Ͳ��洢��Ӧ������
					if (!strCustomerName.equals(strCommentPersonTrueName)
							&& !strCustomerName
									.equals(strCommentReceivePersonTrueName)
							&& !strCustomerName.equals(strAccountName)
							&& 1 == nCommentType) {
						strCommentContent = "";
						strSmallImage = "";
					} else {
						strCommentContent = rs.getString(5);
						strSmallImage = rs.getString(7);
					}
					strCommentTime = rs.getString(2);
					nCommentIndex = rs.getInt(9);
					strCommentPersonName = rs.getString(3);
					strCommentReceivePersonName = rs.getString(4);
					// �����˵�ͷ��
					String strAnnounceImage = "";
					// ����������
					String strAnnounceName = rs.getString(10);
					// ��ȡ�û�ͷ��
					strSQL = "select AccountIcon from customer_info_table where Account = '";
					strSQL += strAnnounceName;
					strSQL += "'";
					rs1 = stmt1.executeQuery(strSQL);
					while (rs1.next()) {
						strAnnounceImage = rs1.getString(1);
					}
					if (rs1 != null) {
						rs1.close();
						rs1 = null;
					}

					CommentInfo comment = new CommentInfo(strAnnounceImage,
							strCommentPersonName, strCommentReceivePersonName,
							strCommentTime, strCommentContent, strSmallImage,
							nCommentIndex, strCommentPersonTrueName,
							strCommentReceivePersonTrueName);
					commentinfos.add(comment);
				}
			} else if (strType.equals("2")) {
				// ��������id��ȡ�����۱�����
				String strSQL = "select * from task_share_opera_table";
				strSQL += " where TaskKey = '";
				strSQL += nTaskId;
				strSQL += "'";
				rs = stmt.executeQuery(strSQL);
				while (rs.next()) {
					strCommentTableName = rs.getString(9);
					strAccountName = rs.getString(2);
					break;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
				// ���ݻ�ȡ�������۱����ƻ�ȡ����Ӧ����
				strSQL = "select * from ";
				strSQL += strCommentTableName;
				strSQL += " where TaskTalkTime >= '";
				strSQL += strTime;
				strSQL += "' ORDER BY TaskTalkTime asc";

				rs = stmt.executeQuery(strSQL);
				while (rs.next()) {
					strCommentPersonTrueName = rs.getString(10);
					strCommentReceivePersonTrueName = rs.getString(11);
					// ��������(�Ƿ������Ļ�)
					nCommentType = rs.getInt(12);
					// �����ǰ�û������������۵������ߺͽ������Լ���ǰ�û����Ƿ�����,ͬʱ�������������Ļ�,��ô�Ͳ��洢��Ӧ������
					if (!strCustomerName.equals(strCommentPersonTrueName)
							&& !strCustomerName
									.equals(strCommentReceivePersonTrueName)
							&& !strCustomerName.equals(strAccountName)
							&& 1 == nCommentType) {
						strCommentContent = "";
						strSmallImage = "";
					} else {
						strCommentContent = rs.getString(5);
						strSmallImage = rs.getString(7);
					}
					strCommentTime = rs.getString(2);
					nCommentIndex = rs.getInt(9);
					strCommentPersonName = rs.getString(3);
					strCommentReceivePersonName = rs.getString(4);

					// �������������˵�ͷ��
					String strAnnounceImage = "";
					// ��ȡ������������������
					String strAnnounceName = rs.getString(10);
					// ��ȡ�û�ͷ��
					strSQL = "select AccountIcon from customer_info_table where Account = '";
					strSQL += strAnnounceName;
					strSQL += "'";
					rs1 = stmt1.executeQuery(strSQL);
					while (rs1.next()) {
						strAnnounceImage = rs1.getString(1);
					}
					if (rs1 != null) {
						rs1.close();
						rs1 = null;
					}

					CommentInfo comment = new CommentInfo(strAnnounceImage,
							strCommentPersonName, strCommentReceivePersonName,
							strCommentTime, strCommentContent, strSmallImage,
							nCommentIndex, strCommentPersonTrueName,
							strCommentReceivePersonTrueName);
					commentinfos.add(comment);
				}
			}
		} catch (Exception e) {
			// System.out.print("get data error!");
			// e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (rs1 != null) {
				try {
					rs1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt1 != null) {
				try {
					stmt1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connect != null) {
				try {
					connect.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return commentinfos;
	}

	@Override
	public String SendCommentImageNewTime(String strTaskId,
			String strCommentPersonName, String strReceiveCommentPersonName,
			String strSmallImage, String strLargeImage, String strType,
			String strCommentPersonNickName,
			String strReceiveCommentPersonNickName) {
		String strTime = "";
		Connection connect = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			connect = GetConnection.getSimpleConnection();
			stmt = connect.createStatement();
			// ���۱�����
			String strCommentTableName = "";
			// ���񷢲�������
			// String strTaskAnnouncePersonName = "";
			int nTaskId = Integer.parseInt(strTaskId);
			boolean bFind = false;
			if (strType.equals("1")) {
				// �ȸ���strTaskId��ȡ�����۱�
				String strSQL = "select * from task_help_opera_table";
				strSQL += " where TaskKey = '";
				strSQL += nTaskId;
				strSQL += "'";
				rs = stmt.executeQuery(strSQL);
				while (rs.next()) {
					strCommentTableName = rs.getString(9);
					// strTaskAnnouncePersonName = rs.getString(2);
					break;
				}
				// ���ȵ����в����ǲ����������˹����������ļ�¼
				if (!strCommentPersonName.equals("")) {
					strSQL = "select * from customer_comment_table where TaskKey = '";
					strSQL += strTaskId;
					strSQL += "' and CustomerName = '";
					strSQL += strCommentPersonName;
					strSQL += "' and TaskType = '1'";
					rs = stmt.executeQuery(strSQL);
					while (rs.next()) {
						bFind = true;
						break;
					}
					// ���û���ҵ�,��ô�ͽ�������¼���뵽����
					if (!bFind) {
						strSQL = "insert into customer_comment_table values('";
						strSQL += nTaskId;
						strSQL += "','";
						strSQL += strCommentPersonName;
						strSQL += "','1','1')";
						stmt.execute(strSQL);
					}
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
				bFind = false;
				// ���ұ����Ƿ��н������۵��˼�¼,���û�оͲ�����Ӧ��¼
				if (!strReceiveCommentPersonName.equals("")) {
					strSQL = "select * from customer_comment_table where TaskKey = '";
					strSQL += strTaskId;
					strSQL += "' and CustomerName = '";
					strSQL += strReceiveCommentPersonName;
					strSQL += "' and TaskType = '1'";
					rs = stmt.executeQuery(strSQL);
					while (rs.next()) {
						bFind = true;
						break;
					}
					// ���û���ҵ�,��ô�ͽ�������¼���뵽����
					if (!bFind) {
						strSQL = "insert into customer_comment_table values('";
						strSQL += nTaskId;
						strSQL += "','";
						strSQL += strReceiveCommentPersonName;
						strSQL += "','2','1')";
						stmt.execute(strSQL);
					} else {// ����ҵ���,�͸���״̬
						strSQL = "update customer_comment_table";
						strSQL += " set TaskCommentType = '2' where TaskKey = '";
						strSQL += strTaskId;
						strSQL += "' and CustomerName = '";
						strSQL += strReceiveCommentPersonName;
						strSQL += "' and TaskType = '1'";
						stmt.executeUpdate(strSQL);
					}
					// �����±�־��Ϊ����״̬
					strSQL = "update customer_info_table";
					strSQL += " set UpdateSignal = '1' where Account = '";
					strSQL += strReceiveCommentPersonName;
					strSQL += "'";
					stmt.executeUpdate(strSQL);
				}
				// ��ȡ��ǰʱ��
				SimpleDateFormat formatter = new SimpleDateFormat(
						"yyyy��MM��dd��HH:mm:ss");
				Date curDate = new Date(System.currentTimeMillis());// ��ȡ��ǰʱ��
				String strTaskAnnounceTime = formatter.format(curDate);

				if (rs != null) {
					rs.close();
					rs = null;
				}
				strSQL = "select * from ";
				strSQL += strCommentTableName;
				rs = stmt.executeQuery(strSQL);
				int nCount = 0;
				while (rs.next()) {
					rs.last();
					nCount = rs.getRow();
					break;
				}
				nCount++;
				// �����ݲ��뵽����
				strSQL = "insert into ";
				strSQL += strCommentTableName;
				strSQL += " values('";
				strSQL += strTaskId;
				strSQL += "','";
				strSQL += strTaskAnnounceTime;
				strSQL += "','";
				strSQL += strCommentPersonNickName;
				strSQL += "','";
				strSQL += strReceiveCommentPersonNickName;
				strSQL += "','','','";
				strSQL += strSmallImage;
				strSQL += "','";
				strSQL += strLargeImage;
				strSQL += "','";
				strSQL += nCount;
				strSQL += "','";
				strSQL += strCommentPersonName;
				strSQL += "','";
				strSQL += strReceiveCommentPersonName;
				strSQL += "','0')";
				stmt.execute(strSQL);

				strTime = strTaskAnnounceTime;
			} else if (strType.equals("2")) {
				// �ȸ���strTaskId��ȡ�����۱�
				String strSQL = "select * from task_share_opera_table";
				strSQL += " where TaskKey = '";
				strSQL += nTaskId;
				strSQL += "'";
				rs = stmt.executeQuery(strSQL);
				while (rs.next()) {
					strCommentTableName = rs.getString(9);
					// strTaskAnnouncePersonName = rs.getString(2);
					break;
				}
				// �ȸ���strTaskId��ȡ�����۱�
				strSQL = "select * from task_share_opera_table";
				strSQL += " where TaskKey = '";
				strSQL += nTaskId;
				strSQL += "'";
				rs = stmt.executeQuery(strSQL);
				while (rs.next()) {
					strCommentTableName = rs.getString(9);
					// strTaskAnnouncePersonName = rs.getString(2);
					break;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
				// ���ȵ����в����ǲ����������˹����������ļ�¼
				if (!strCommentPersonName.equals("")) {
					strSQL = "select * from customer_comment_table where TaskKey = '";
					strSQL += strTaskId;
					strSQL += "' and CustomerName = '";
					strSQL += strCommentPersonName;
					strSQL += "' and TaskType = '2'";
					rs = stmt.executeQuery(strSQL);
					while (rs.next()) {
						bFind = true;
						break;
					}
					// ���û���ҵ�,��ô�ͽ�������¼���뵽����
					if (!bFind) {
						strSQL = "insert into customer_comment_table values('";
						strSQL += nTaskId;
						strSQL += "','";
						strSQL += strCommentPersonName;
						strSQL += "','1','2')";
						stmt.execute(strSQL);
					}
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
				bFind = false;
				// ���ұ����Ƿ��н������۵��˼�¼,���û�оͲ�����Ӧ��¼
				if (!strReceiveCommentPersonName.equals("")) {
					strSQL = "select * from customer_comment_table where TaskKey = '";
					strSQL += strTaskId;
					strSQL += "' and CustomerName = '";
					strSQL += strReceiveCommentPersonName;
					strSQL += "' and TaskType = '2'";
					rs = stmt.executeQuery(strSQL);
					while (rs.next()) {
						bFind = true;
						break;
					}
					// ���û���ҵ�,��ô�ͽ�������¼���뵽����
					if (!bFind) {
						strSQL = "insert into customer_comment_table values('";
						strSQL += nTaskId;
						strSQL += "','";
						strSQL += strReceiveCommentPersonName;
						strSQL += "','2','2')";
						stmt.execute(strSQL);
					} else {// ����ҵ���,�͸���״̬
						strSQL = "update customer_comment_table";
						strSQL += " set TaskCommentType = '2' where TaskKey = '";
						strSQL += strTaskId;
						strSQL += "' and CustomerName = '";
						strSQL += strReceiveCommentPersonName;
						strSQL += "' and TaskType = '2'";
						stmt.executeUpdate(strSQL);
					}
					// �����±�־��Ϊ����״̬
					strSQL = "update customer_info_table";
					strSQL += " set UpdateSignal = '1' where Account = '";
					strSQL += strReceiveCommentPersonName;
					strSQL += "'";
					stmt.executeUpdate(strSQL);
				}
				// ��ȡ��ǰʱ��
				SimpleDateFormat formatter = new SimpleDateFormat(
						"yyyy��MM��dd��HH:mm:ss");
				Date curDate = new Date(System.currentTimeMillis());// ��ȡ��ǰʱ��
				String strTaskAnnounceTime = formatter.format(curDate);

				if (rs != null) {
					rs.close();
					rs = null;
				}
				strSQL = "select * from ";
				strSQL += strCommentTableName;
				rs = stmt.executeQuery(strSQL);
				int nCount = 0;
				while (rs.next()) {
					rs.last();
					nCount = rs.getRow();
					break;
				}
				nCount++;
				// �����ݲ��뵽����
				strSQL = "insert into ";
				strSQL += strCommentTableName;
				strSQL += " values('";
				strSQL += strTaskId;
				strSQL += "','";
				strSQL += strTaskAnnounceTime;
				strSQL += "','";
				strSQL += strCommentPersonNickName;
				strSQL += "','";
				strSQL += strReceiveCommentPersonNickName;
				strSQL += "','','','";
				strSQL += strSmallImage;
				strSQL += "','";
				strSQL += strLargeImage;
				strSQL += "','";
				strSQL += nCount;
				strSQL += "','";
				strSQL += strCommentPersonName;
				strSQL += "','";
				strSQL += strReceiveCommentPersonName;
				strSQL += "','0')";
				stmt.execute(strSQL);

				strTime = strTaskAnnounceTime;
			}
		} catch (Exception e) {
			// System.out.print("get data error!");
			// e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connect != null) {
				try {
					connect.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return strTime;
	}

	@Override
	public String SendCommentContentNewSecretTime(String strTaskId,
			String strCommentPersonName, String strReceiveCommentPersonName,
			String strCommentContent, String strType,
			String strCommentPersonNickName,
			String strReceiveCommentPersonNickName, int nSecretType) {
		String strTime = "";
		Connection connect = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			connect = GetConnection.getSimpleConnection();
			stmt = connect.createStatement();
			// ���۱�����
			String strCommentTableName = "";
			boolean bFind = false;
			int nTaskId = Integer.parseInt(strTaskId);
			if (strType.equals("1")) {
				// �ȸ���strTaskId��ȡ�����۱�
				String strSQL = "select * from task_help_opera_table";
				strSQL += " where TaskKey = '";
				strSQL += nTaskId;
				strSQL += "'";
				rs = stmt.executeQuery(strSQL);
				while (rs.next()) {
					strCommentTableName = rs.getString(9);
					// strTaskAnnouncePersonName = rs.getString(2);
					break;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
				// ���ȵ����в����ǲ����������˹����������ļ�¼
				if (!strCommentPersonName.equals("")) {
					strSQL = "select * from customer_comment_table where TaskKey = '";
					strSQL += strTaskId;
					strSQL += "' and CustomerName = '";
					strSQL += strCommentPersonName;
					strSQL += "' and TaskType = '1'";
					rs = stmt.executeQuery(strSQL);
					while (rs.next()) {
						bFind = true;
						break;
					}
					// ���û���ҵ�,��ô�ͽ�������¼���뵽����
					if (!bFind) {
						strSQL = "insert into customer_comment_table values('";
						strSQL += nTaskId;
						strSQL += "','";
						strSQL += strCommentPersonName;
						strSQL += "','1','1')";
						stmt.execute(strSQL);
					}
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
				bFind = false;
				// ���ұ����Ƿ��н������۵��˼�¼,���û�оͲ�����Ӧ��¼
				if (!strReceiveCommentPersonName.equals("")) {
					strSQL = "select * from customer_comment_table where TaskKey = '";
					strSQL += strTaskId;
					strSQL += "' and CustomerName = '";
					strSQL += strReceiveCommentPersonName;
					strSQL += "' and TaskType = '1'";
					rs = stmt.executeQuery(strSQL);
					while (rs.next()) {
						bFind = true;
						break;
					}
					// ���û���ҵ�,��ô�ͽ�������¼���뵽����
					if (!bFind) {
						strSQL = "insert into customer_comment_table values('";
						strSQL += nTaskId;
						strSQL += "','";
						strSQL += strReceiveCommentPersonName;
						strSQL += "','2','1')";
						stmt.execute(strSQL);
					} else {// ����ҵ���,�͸���״̬
						strSQL = "update customer_comment_table";
						strSQL += " set TaskCommentType = '2' where TaskKey = '";
						strSQL += strTaskId;
						strSQL += "' and CustomerName = '";
						strSQL += strReceiveCommentPersonName;
						strSQL += "' and TaskType = '1'";
						stmt.executeUpdate(strSQL);
					}
					// �����±�־��Ϊ����״̬
					strSQL = "update customer_info_table";
					strSQL += " set UpdateSignal = '1' where Account = '";
					strSQL += strReceiveCommentPersonName;
					strSQL += "'";
					stmt.executeUpdate(strSQL);
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
				// ��ȡ��ǰʱ��
				SimpleDateFormat formatter = new SimpleDateFormat(
						"yyyy��MM��dd��HH:mm:ss");
				Date curDate = new Date(System.currentTimeMillis());// ��ȡ��ǰʱ��
				String strTaskAnnounceTime = formatter.format(curDate);

				// �����ݲ��뵽����
				strSQL = "insert into ";
				strSQL += strCommentTableName;
				strSQL += " values('";
				strSQL += strTaskId;
				strSQL += "','";
				strSQL += strTaskAnnounceTime;
				strSQL += "','";
				strSQL += strCommentPersonNickName;
				strSQL += "','";
				strSQL += strReceiveCommentPersonNickName;
				strSQL += "','";
				strSQL += strCommentContent;
				strSQL += "','','','','0','";
				strSQL += strCommentPersonName;
				strSQL += "','";
				strSQL += strReceiveCommentPersonName;
				strSQL += "','";
				strSQL += nSecretType;
				strSQL += "')";
				stmt.execute(strSQL);

				strTime = strTaskAnnounceTime;
			} else if (strType.equals("2")) {
				// �ȸ���strTaskId��ȡ�����۱�
				String strSQL = "select * from task_share_opera_table";
				strSQL += " where TaskKey = '";
				strSQL += nTaskId;
				strSQL += "'";
				rs = stmt.executeQuery(strSQL);
				while (rs.next()) {
					strCommentTableName = rs.getString(9);
					// strTaskAnnouncePersonName = rs.getString(2);
					break;
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
				// ���ȵ����в����ǲ����������˹����������ļ�¼
				if (!strCommentPersonName.equals("")) {
					strSQL = "select * from customer_comment_table where TaskKey = '";
					strSQL += strTaskId;
					strSQL += "' and CustomerName = '";
					strSQL += strCommentPersonName;
					strSQL += "' and TaskType = '2'";
					rs = stmt.executeQuery(strSQL);
					while (rs.next()) {
						bFind = true;
						break;
					}
					// ���û���ҵ�,��ô�ͽ�������¼���뵽����
					if (!bFind) {
						strSQL = "insert into customer_comment_table values('";
						strSQL += nTaskId;
						strSQL += "','";
						strSQL += strCommentPersonName;
						strSQL += "','1','2')";
						stmt.execute(strSQL);
					}
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
				bFind = false;
				// ���ұ����Ƿ��н������۵��˼�¼,���û�оͲ�����Ӧ��¼
				if (!strReceiveCommentPersonName.equals("")) {
					strSQL = "select * from customer_comment_table where TaskKey = '";
					strSQL += strTaskId;
					strSQL += "' and CustomerName = '";
					strSQL += strReceiveCommentPersonName;
					strSQL += "' and TaskType = '2'";
					rs = stmt.executeQuery(strSQL);
					while (rs.next()) {
						bFind = true;
						break;
					}
					// ���û���ҵ�,��ô�ͽ�������¼���뵽����
					if (!bFind) {
						strSQL = "insert into customer_comment_table values('";
						strSQL += nTaskId;
						strSQL += "','";
						strSQL += strReceiveCommentPersonName;
						strSQL += "','2','2')";
						stmt.execute(strSQL);
					} else {// ����ҵ���,�͸���״̬
						strSQL = "update customer_comment_table";
						strSQL += " set TaskCommentType = '2' where TaskKey = '";
						strSQL += strTaskId;
						strSQL += "' and CustomerName = '";
						strSQL += strReceiveCommentPersonName;
						strSQL += "' and TaskType = '2'";
						stmt.executeUpdate(strSQL);
					}
					// �����±�־��Ϊ����״̬
					strSQL = "update customer_info_table";
					strSQL += " set UpdateSignal = '1' where Account = '";
					strSQL += strReceiveCommentPersonName;
					strSQL += "'";
					stmt.executeUpdate(strSQL);
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
				// ��ȡ��ǰʱ��
				SimpleDateFormat formatter = new SimpleDateFormat(
						"yyyy��MM��dd��HH:mm:ss");
				Date curDate = new Date(System.currentTimeMillis());// ��ȡ��ǰʱ��
				String strTaskAnnounceTime = formatter.format(curDate);

				// �����ݲ��뵽����
				strSQL = "insert into ";
				strSQL += strCommentTableName;
				strSQL += " values('";
				strSQL += nTaskId;
				strSQL += "','";
				strSQL += strTaskAnnounceTime;
				strSQL += "','";
				strSQL += strCommentPersonNickName;
				strSQL += "','";
				strSQL += strReceiveCommentPersonNickName;
				strSQL += "','";
				strSQL += strCommentContent;
				strSQL += "','','','','0','";
				strSQL += strCommentPersonName;
				strSQL += "','";
				strSQL += strReceiveCommentPersonName;
				strSQL += "','";
				strSQL += nSecretType;
				strSQL += "')";
				stmt.execute(strSQL);

				strTime = strTaskAnnounceTime;
			}
		} catch (Exception e) {
			// System.out.print("get data error!");
			// e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connect != null) {
				try {
					connect.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return strTime;
	}

	@Override
	public String GetFirstSmallIcon(String strTaskId, String strTaskType) {
		String strImage = "";
		Connection connect = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			connect = GetConnection.getSimpleConnection();
			stmt = connect.createStatement();
			int nTaskId = Integer.parseInt(strTaskId);
			String strSQL = "";
			if (strTaskType.equals("1")) {// ����
				strSQL = "select * from task_help_smallicon where TaskKey = '";
				strSQL += nTaskId;
				strSQL += "'";
				rs = stmt.executeQuery(strSQL);
				while (rs.next()) {
					strImage = rs.getString(2);
					break;
				}
			} else if (strTaskType.equals("2")) {// ����
				strSQL = "select * from task_share_smallicon where TaskKey = '";
				strSQL += nTaskId;
				strSQL += "'";
				rs = stmt.executeQuery(strSQL);
				while (rs.next()) {
					strImage = rs.getString(2);
					break;
				}
			}
		} catch (Exception e) {
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connect != null) {
				try {
					connect.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return strImage;
	}

	@Override
	public TaskIcon GetOtherSmallIcon(String strTaskId, String strTaskType) {
		TaskIcon taskicon = null;
		Connection connect = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			connect = GetConnection.getSimpleConnection();
			stmt = connect.createStatement();
			int nTaskId = Integer.parseInt(strTaskId);
			String strSQL = "";
			if (strTaskType.equals("1")) {// ����
				strSQL = "select * from task_help_smallicon where TaskKey = '";
				strSQL += nTaskId;
				strSQL += "'";
				rs = stmt.executeQuery(strSQL);
				while (rs.next()) {
					taskicon = new TaskIcon(rs.getString(2), rs.getString(3),
							rs.getString(4), rs.getString(5), rs.getString(6),
							rs.getString(7));
					break;
				}
			} else if (strTaskType.equals("2")) {// ����
				strSQL = "select * from task_share_smallicon where TaskKey = '";
				strSQL += nTaskId;
				strSQL += "'";
				rs = stmt.executeQuery(strSQL);
				while (rs.next()) {
					taskicon = new TaskIcon("", rs.getString(3),
							rs.getString(4), rs.getString(5), rs.getString(6),
							rs.getString(7));
					break;
				}
			}
		} catch (Exception e) {
			// System.out.print("get data error!");
			// e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connect != null) {
				try {
					connect.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return taskicon;
	}

	@Override
	public List<TaskInfoDetail> GetMsgInfoNumNewDetail1(String strAnnounceName) {
		List<TaskInfoDetail> dynamicnews = new ArrayList<TaskInfoDetail>();
		int nNum = 0;
		Connection connect = null;
		Statement stmt = null;
		ResultSet rs = null;
		Statement stmt1 = null;
		ResultSet rs1 = null;
		Statement stmt2 = null;
		ResultSet rs2 = null;
		try {
			connect = GetConnection.getSimpleConnection();
			stmt = connect.createStatement();
			stmt1 = connect.createStatement();
			stmt2 = connect.createStatement();
			// ��������
			int nTaskType = 0;
			// ����id��
			int nTaskId = 0;
			// ��Ϣ״̬
			int nMessageStatus = 0;
			// ��ȡ����Ϣ������
			String strSQL = "select * from customer_comment_table where CustomerName = '";
			strSQL += strAnnounceName;
			strSQL += "' and TaskCommentType = '2'";
			rs = stmt.executeQuery(strSQL);
			while (rs.next()) {
				rs.last();
				nNum = rs.getRow();
			}
			if (rs != null) {
				rs.close();
				rs = null;
			}

			SimpleDateFormat shareformatter1 = new SimpleDateFormat(
					"yyyy��MM��dd��");
			Date sharecurDate = new Date(System.currentTimeMillis());// ��ȡ��ǰʱ��
			String strCurrentTime1 = shareformatter1.format(sharecurDate);
			// ���ҷ���������Ϣ
			strSQL = "select * from customer_share_expert_table where ShareDate = '";
			strSQL += strCurrentTime1;
			strSQL += "' ORDER BY ShareTimes desc Limit 0,1";
			rs1 = stmt.executeQuery(strSQL);
			while (rs1.next()) {
				String strPersonName = rs1.getString(1);
				String strPersonNickName = rs1.getString(2);
				int nShareTimes = rs1.getInt(4);
				int nStatusType = rs1.getInt(5);

				if (1 == nStatusType) {
					nNum += 1;
				}
				// ���˵�ͷ��
				String strAnnounceImage = "";
				// ��ȡ�û�ͷ��
				strSQL = "select AccountIcon from customer_info_table where Account = '";
				strSQL += strPersonName;
				strSQL += "'";
				rs2 = stmt2.executeQuery(strSQL);
				while (rs2.next()) {
					strAnnounceImage = rs2.getString(1);
				}
				if (rs2 != null) {
					rs2.close();
					rs2 = null;
				}
				TaskInfoDetail task = new TaskInfoDetail(strCurrentTime1,
						strAnnounceImage, strPersonNickName, "", "", 0, 0, "",
						"", "", "", "", 0, 0, 0, 0, 0, 0, nStatusType, 0, nNum,
						"", "", "", "", strPersonName, "", 0, 0, 0,
						nShareTimes, 0);
				dynamicnews.add(task);

				if (1 == nStatusType) {
					// �����ͱ�־��Ϊ������
					strSQL = "update customer_share_expert_table";
					strSQL += " set StatusType = '2' where ShareDate = '";
					strSQL += strCurrentTime1;
					strSQL += "'";
					stmt.executeUpdate(strSQL);
				}
				break;
			}
			if (rs1 != null) {
				rs1.close();
				rs1 = null;
			}
			// ���ݱ�customer_comment_table��ȡ��ָ���û���ص�����
			strSQL = "select * from customer_comment_table where CustomerName = '";
			strSQL += strAnnounceName;
			strSQL += "'";
			rs = stmt.executeQuery(strSQL);
			rs.afterLast();
			while (rs.previous()) {
				nTaskType = rs.getInt(4);
				nTaskId = rs.getInt(1);
				nMessageStatus = rs.getInt(3);
				if (1 == nTaskType) {
					// ��Ϣ״̬
					int nStatus = nMessageStatus;
					String strName = "";
					// ��ȡϵͳ��ǰʱ��
					SimpleDateFormat formatter = new SimpleDateFormat(
							"yyyy��MM��dd��HH:mm:ss");
					Date curDate = new Date(System.currentTimeMillis());// ��ȡ��ǰʱ��
					String strCurrentTime = formatter.format(curDate);

					strSQL = "select * from task_help_opera_table where TaskKey = '";
					strSQL += nTaskId;
					strSQL += "'";
					rs1 = stmt1.executeQuery(strSQL);
					while (rs1.next()) {
						// ����id��
						int nid = rs1.getInt(1);
						// ������ߵ�id����ͬ
						if (nTaskId != nid) {
							nStatus = 1;
						}
						strName = rs1.getString(2);

						// �������������˵�ͷ��
						String strAnnounceImage = "";
						// ��ȡ�û�ͷ��
						strSQL = "select * from customer_info_table where Account = '";
						strSQL += strName;
						strSQL += "'";
						rs2 = stmt2.executeQuery(strSQL);
						while (rs2.next()) {
							strAnnounceImage = rs2.getString(1);
						}
						if (rs2 != null) {
							rs2.close();
							rs2 = null;
						}

						int nRecordNum = 0;
						// ��ȡ��������
						String strCommentTableName = rs1.getString(9);
						strSQL = "select count(*) from ";
						strSQL += strCommentTableName;
						rs2 = stmt2.executeQuery(strSQL);
						while (rs2.next()) {
							nRecordNum = rs2.getInt(1);
						}

						if (rs2 != null) {
							rs2.close();
							rs2 = null;
						}

						// ���񷢲�ʱ��
						String strAnnounceTime = rs1.getString(18);
						Date d1 = formatter.parse(strCurrentTime);
						Date d2 = formatter.parse(strAnnounceTime);
						long diff = d1.getTime() - d2.getTime();// �����õ��Ĳ�ֵ��΢�뼶��

						TaskInfoDetail task = new TaskInfoDetail(
								rs1.getString(16), strAnnounceImage,
								rs1.getString(28), rs1.getString(15),
								rs1.getInt(1) + "", rs1.getDouble(20),
								rs1.getDouble(21), strAnnounceTime,
								rs1.getString(23), rs1.getString(17), diff
										/ 1000 + "", rs1.getString(29),
								rs1.getInt(24), rs1.getInt(25), rs1.getInt(14),
								rs1.getInt(4), rs1.getInt(5), rs1.getInt(6),
								nStatus, rs1.getInt(8), nNum,
								rs1.getString(10), rs1.getString(11),
								rs1.getString(12), rs1.getString(13), strName,
								rs1.getString(3), rs1.getInt(30),
								rs1.getInt(31), rs1.getInt(33), nRecordNum,
								rs1.getInt(32));
						dynamicnews.add(task);
					}
					if (rs1 != null) {
						rs1.close();
						rs1 = null;
					}

				} else if (2 == nTaskType) {

					if (rs1 != null) {
						rs1.close();
						rs1 = null;
					}
					// ��Ϣ״̬
					int nStatus = nMessageStatus;
					String strName = "";
					// ��ȡϵͳ��ǰʱ��
					SimpleDateFormat formatter = new SimpleDateFormat(
							"yyyy��MM��dd��HH:mm:ss");
					Date curDate = new Date(System.currentTimeMillis());// ��ȡ��ǰʱ��
					String strCurrentTime = formatter.format(curDate);

					strSQL = "select * from task_share_opera_table where TaskKey = '";
					strSQL += nTaskId;
					strSQL += "'";
					rs1 = stmt1.executeQuery(strSQL);
					while (rs1.next()) {
						// ����id��
						int nid = rs1.getInt(1);
						// ������ߵ�id����ͬ,˵������������ɵ�ǰ�û�������
						if (nTaskId != nid) {
							nStatus = 1;
						}
						strName = rs1.getString(2);

						// �������������˵�ͷ��
						String strAnnounceImage = "";
						// ��ȡ�û�ͷ��
						strSQL = "select * from customer_info_table where Account = '";
						strSQL += strName;
						strSQL += "'";
						rs2 = stmt2.executeQuery(strSQL);
						while (rs2.next()) {
							strAnnounceImage = rs2.getString(1);
						}
						if (rs2 != null) {
							rs2.close();
							rs2 = null;
						}

						int nRecordNum = 0;
						// ��ȡ��������
						String strCommentTableName = rs1.getString(9);
						strSQL = "select count(*) from ";
						strSQL += strCommentTableName;
						rs2 = stmt2.executeQuery(strSQL);
						while (rs2.next()) {
							nRecordNum = rs2.getInt(1);
						}
						if (rs2 != null) {
							rs2.close();
							rs2 = null;
						}
						// ���񷢲�ʱ��
						String strAnnounceTime = rs1.getString(18);
						Date d1 = formatter.parse(strCurrentTime);
						Date d2 = formatter.parse(strAnnounceTime);
						long diff = d1.getTime() - d2.getTime();// �����õ��Ĳ�ֵ��΢�뼶��

						TaskInfoDetail task = new TaskInfoDetail(
								rs1.getString(16), strAnnounceImage,
								rs1.getString(28), rs1.getString(15),
								rs1.getInt(1) + "", rs1.getDouble(20),
								rs1.getDouble(21), strAnnounceTime,
								rs1.getString(23), rs1.getString(17), diff
										/ 1000 + "", rs1.getString(29),
								rs1.getInt(24), rs1.getInt(25), rs1.getInt(14),
								rs1.getInt(4), rs1.getInt(5), rs1.getInt(6),
								nStatus, rs1.getInt(8), nNum,
								rs1.getString(10), rs1.getString(11),
								rs1.getString(12), rs1.getString(13), strName,
								rs1.getString(3), rs1.getInt(30),
								rs1.getInt(31), rs1.getInt(33), nRecordNum,
								rs1.getInt(32));
						dynamicnews.add(task);
					}
					if (rs1 != null) {
						rs1.close();
						rs1 = null;
					}
				}
			}

		} catch (Exception e) {
			// System.out.print("get data error!");
			// e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (rs1 != null) {
				try {
					rs1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt1 != null) {
				try {
					stmt1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (rs2 != null) {
				try {
					rs1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt2 != null) {
				try {
					stmt1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connect != null) {
				try {
					connect.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return dynamicnews;
	}

	@Override
	public UpdateData GetUpdateSignalNew1(String strCurrentAccountName) {
		UpdateData update = new UpdateData(-1, "");
		int nRet = -1;
		Connection connect = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			connect = GetConnection.getSimpleConnection();
			stmt = connect.createStatement();
			String strSQL = "";
			// �����±�־��Ϊ����״̬
			strSQL = "select UpdateSignal from customer_info_table where Account = '";
			strSQL += strCurrentAccountName;
			strSQL += "'";
			rs = stmt.executeQuery(strSQL);
			while (rs.next()) {
				nRet = rs.getInt(1);
				break;
			}
			if (rs != null) {
				rs.close();
				rs = null;
			}

			int nStatusType = -1;
			// ��ȡϵͳ��ǰʱ��
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy��MM��dd��HH:mm:ss");
			Date curDate = new Date(System.currentTimeMillis());// ��ȡ��ǰʱ��
			String strCurrentTime = formatter.format(curDate);

			SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy��MM��dd��");
			curDate = new Date(System.currentTimeMillis());// ��ȡ��ǰʱ��
			String strCurrentTime1 = formatter1.format(curDate);
			String strEndTime = strCurrentTime1 + strExpertPersonTimeEnd;

			Date d1 = formatter.parse(strCurrentTime);
			Date d2 = formatter.parse(strEndTime);
			long diff = d1.getTime() - d2.getTime();// �����õ��Ĳ�ֵ��΢�뼶��
			// �жϵ�ǰʱ���Ƿ������ʹ���ʱ��,ֻҪ���ʱ�䲻����1����,����Ϊ��������ʱ��
			if (diff >= 0 && diff / 1000 <= nLimitTimeSeconds) {

				// ���ҷ���������Ϣ
				strSQL = "select StatusType from customer_share_expert_table where ShareDate = '";
				strSQL += strCurrentTime1;
				strSQL += "'";
				rs = stmt.executeQuery(strSQL);
				while (rs.next()) {
					nStatusType = rs.getInt(1);
				}
			}

			String strStatusInfo = "";
			if (1 == nRet || 1 == nStatusType) {
				nRet = 1; // �����Ƿ��и�������
				strSQL = "select * from customer_comment_table where CustomerName = '";
				strSQL += strCurrentAccountName;
				strSQL += "' and TaskCommentType = '2'";
				rs = stmt.executeQuery(strSQL);
				while (rs.next()) {
					strStatusInfo = "��������Ϣ";
					break;
				}
				if (1 == nStatusType) {
					strStatusInfo = "��������Ϣ";
				}
				update.setnUpdateSignal(nRet);
				update.setstrUpdateDescribe(strStatusInfo);
			}

		} catch (Exception e) {
			System.out.print("get data error!");
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connect != null) {
				try {
					connect.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return update;
	}

	@Override
	public List<TaskInfoDetail> GetMsgInfoNumNewDetail2(String strAnnounceName,
			String strID) {
		List<TaskInfoDetail> dynamicnews = new ArrayList<TaskInfoDetail>();
		int nNum = 0;
		Connection connect = null;
		Statement stmt = null;
		ResultSet rs = null;
		Statement stmt1 = null;
		ResultSet rs1 = null;
		Statement stmt2 = null;
		ResultSet rs2 = null;
		try {
			connect = GetConnection.getSimpleConnection();
			stmt = connect.createStatement();
			stmt1 = connect.createStatement();
			stmt2 = connect.createStatement();
			// ��������
			int nTaskType = 0;
			// ����id��
			int nTaskId = 0;
			// ��Ϣ״̬
			int nMessageStatus = 0;
			// ��ȡ����Ϣ������
			String strSQL = "select * from customer_comment_table where CustomerName = '";
			strSQL += strAnnounceName;
			strSQL += "' and TaskCommentType = '2'";
			rs = stmt.executeQuery(strSQL);
			while (rs.next()) {
				rs.last();
				nNum = rs.getRow();
			}
			if (rs != null) {
				rs.close();
				rs = null;
			}

			// ��ȡid����
			HashMap<String, Integer> mapid = GetID(strID);

			SimpleDateFormat shareformatter1 = new SimpleDateFormat(
					"yyyy��MM��dd��");
			Date sharecurDate = new Date(System.currentTimeMillis());// ��ȡ��ǰʱ��
			String strCurrentTime1 = shareformatter1.format(sharecurDate);
			// ���ҷ���������Ϣ
			strSQL = "select * from customer_share_expert_table where ShareDate = '";
			strSQL += strCurrentTime1;
			strSQL += "' ORDER BY ShareTimes desc Limit 0,1";
			rs1 = stmt.executeQuery(strSQL);
			while (rs1.next()) {
				String strPersonName = rs1.getString(1);
				String strPersonNickName = rs1.getString(2);
				int nShareTimes = rs1.getInt(4);
				int nStatusType = rs1.getInt(5);
				if (1 == nStatusType) {
					nNum += 1;
				}
				// ���˵�ͷ��
				String strAnnounceImage = "";
				// ��ȡ�û�ͷ��
				strSQL = "select AccountIcon from customer_info_table where Account = '";
				strSQL += strPersonName;
				strSQL += "'";
				rs2 = stmt2.executeQuery(strSQL);
				while (rs2.next()) {
					strAnnounceImage = rs2.getString(1);
				}
				if (rs2 != null) {
					rs2.close();
					rs2 = null;
				}
				TaskInfoDetail task = new TaskInfoDetail(strCurrentTime1,
						strAnnounceImage, strPersonNickName, "", "", 0, 0, "",
						"", "", "", "", 0, 0, 0, 0, 0, 0, nStatusType, 0, nNum,
						"", "", "", "", strPersonName, "", 0, 0, 0,
						nShareTimes, 0);
				dynamicnews.add(task);

				if (1 == nStatusType) {
					// �����ͱ�־��Ϊ������
					strSQL = "update customer_share_expert_table";
					strSQL += " set StatusType = '2' where ShareDate = '";
					strSQL += strCurrentTime1;
					strSQL += "'";
					stmt.executeUpdate(strSQL);
				}
				break;
			}
			if (rs1 != null) {
				rs1.close();
				rs1 = null;
			}
			// ���ݱ�customer_comment_table��ȡ��ָ���û���ص�����
			strSQL = "select * from customer_comment_table where CustomerName = '";
			strSQL += strAnnounceName;
			strSQL += "'";
			rs = stmt.executeQuery(strSQL);
			rs.afterLast();
			while (rs.previous()) {
				nTaskType = rs.getInt(4);
				nTaskId = rs.getInt(1);
				nMessageStatus = rs.getInt(3);
				// �ж�id��ָ���������Ƿ���Ҫ���ص��ͻ���
				if (mapid.containsKey("" + nTaskId) && nMessageStatus != 2) {
					// �������Ҫ��ô�Ͳ�ִ����������
					continue;
				}
				if (1 == nTaskType) {
					// ��Ϣ״̬
					int nStatus = nMessageStatus;
					String strName = "";
					// ��ȡϵͳ��ǰʱ��
					SimpleDateFormat formatter = new SimpleDateFormat(
							"yyyy��MM��dd��HH:mm:ss");
					Date curDate = new Date(System.currentTimeMillis());// ��ȡ��ǰʱ��
					String strCurrentTime = formatter.format(curDate);

					strSQL = "select * from task_help_opera_table where TaskKey = '";
					strSQL += nTaskId;
					strSQL += "'";
					rs1 = stmt1.executeQuery(strSQL);
					while (rs1.next()) {
						// ����id��
						int nid = rs1.getInt(1);
						// ������ߵ�id����ͬ
						if (nTaskId != nid) {
							nStatus = 1;
						}
						strName = rs1.getString(2);

						// �������������˵�ͷ��
						String strAnnounceImage = "";
						// ��ȡ�û�ͷ��
						strSQL = "select * from customer_info_table where Account = '";
						strSQL += strName;
						strSQL += "'";
						rs2 = stmt2.executeQuery(strSQL);
						while (rs2.next()) {
							strAnnounceImage = rs2.getString(1);
						}
						if (rs2 != null) {
							rs2.close();
							rs2 = null;
						}

						int nRecordNum = 0;
						// ��ȡ��������
						String strCommentTableName = rs1.getString(9);
						strSQL = "select count(*) from ";
						strSQL += strCommentTableName;
						rs2 = stmt2.executeQuery(strSQL);
						while (rs2.next()) {
							nRecordNum = rs2.getInt(1);
						}

						if (rs2 != null) {
							rs2.close();
							rs2 = null;
						}

						// ���񷢲�ʱ��
						String strAnnounceTime = rs1.getString(18);
						Date d1 = formatter.parse(strCurrentTime);
						Date d2 = formatter.parse(strAnnounceTime);
						long diff = d1.getTime() - d2.getTime();// �����õ��Ĳ�ֵ��΢�뼶��

						TaskInfoDetail task = new TaskInfoDetail(
								rs1.getString(16), strAnnounceImage,
								rs1.getString(28), rs1.getString(15),
								rs1.getInt(1) + "", rs1.getDouble(20),
								rs1.getDouble(21), strAnnounceTime,
								rs1.getString(23), rs1.getString(17), diff
										/ 1000 + "", rs1.getString(29),
								rs1.getInt(24), rs1.getInt(25), rs1.getInt(14),
								rs1.getInt(4), rs1.getInt(5), rs1.getInt(6),
								nStatus, rs1.getInt(8), nNum,
								rs1.getString(10), rs1.getString(11),
								rs1.getString(12), rs1.getString(13), strName,
								rs1.getString(3), rs1.getInt(30),
								rs1.getInt(31), rs1.getInt(33), nRecordNum,
								rs1.getInt(32));
						dynamicnews.add(task);
					}
					if (rs1 != null) {
						rs1.close();
						rs1 = null;
					}

				} else if (2 == nTaskType) {

					if (rs1 != null) {
						rs1.close();
						rs1 = null;
					}
					// ��Ϣ״̬
					int nStatus = nMessageStatus;
					String strName = "";
					// ��ȡϵͳ��ǰʱ��
					SimpleDateFormat formatter = new SimpleDateFormat(
							"yyyy��MM��dd��HH:mm:ss");
					Date curDate = new Date(System.currentTimeMillis());// ��ȡ��ǰʱ��
					String strCurrentTime = formatter.format(curDate);

					strSQL = "select * from task_share_opera_table where TaskKey = '";
					strSQL += nTaskId;
					strSQL += "'";
					rs1 = stmt1.executeQuery(strSQL);
					while (rs1.next()) {
						// ����id��
						int nid = rs1.getInt(1);
						// ������ߵ�id����ͬ,˵������������ɵ�ǰ�û�������
						if (nTaskId != nid) {
							nStatus = 1;
						}
						strName = rs1.getString(2);

						// �������������˵�ͷ��
						String strAnnounceImage = "";
						// ��ȡ�û�ͷ��
						strSQL = "select * from customer_info_table where Account = '";
						strSQL += strName;
						strSQL += "'";
						rs2 = stmt2.executeQuery(strSQL);
						while (rs2.next()) {
							strAnnounceImage = rs2.getString(1);
						}
						if (rs2 != null) {
							rs2.close();
							rs2 = null;
						}

						int nRecordNum = 0;
						// ��ȡ��������
						String strCommentTableName = rs1.getString(9);
						strSQL = "select count(*) from ";
						strSQL += strCommentTableName;
						rs2 = stmt2.executeQuery(strSQL);
						while (rs2.next()) {
							nRecordNum = rs2.getInt(1);
						}
						if (rs2 != null) {
							rs2.close();
							rs2 = null;
						}
						// ���񷢲�ʱ��
						String strAnnounceTime = rs1.getString(18);
						Date d1 = formatter.parse(strCurrentTime);
						Date d2 = formatter.parse(strAnnounceTime);
						long diff = d1.getTime() - d2.getTime();// �����õ��Ĳ�ֵ��΢�뼶��

						TaskInfoDetail task = new TaskInfoDetail(
								rs1.getString(16), strAnnounceImage,
								rs1.getString(28), rs1.getString(15),
								rs1.getInt(1) + "", rs1.getDouble(20),
								rs1.getDouble(21), strAnnounceTime,
								rs1.getString(23), rs1.getString(17), diff
										/ 1000 + "", rs1.getString(29),
								rs1.getInt(24), rs1.getInt(25), rs1.getInt(14),
								rs1.getInt(4), rs1.getInt(5), rs1.getInt(6),
								nStatus, rs1.getInt(8), nNum,
								rs1.getString(10), rs1.getString(11),
								rs1.getString(12), rs1.getString(13), strName,
								rs1.getString(3), rs1.getInt(30),
								rs1.getInt(31), rs1.getInt(33), nRecordNum,
								rs1.getInt(32));
						dynamicnews.add(task);
					}
					if (rs1 != null) {
						rs1.close();
						rs1 = null;
					}
				}
			}

		} catch (Exception e) {
			// System.out.print("get data error!");
			// e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (rs1 != null) {
				try {
					rs1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt1 != null) {
				try {
					stmt1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (rs2 != null) {
				try {
					rs1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt2 != null) {
				try {
					stmt1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connect != null) {
				try {
					connect.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return dynamicnews;
	}

	@Override
	public int DeleteMineDynamicInfo(String strTaskId, String strType,
			String strPersonName) {
		int nRet = 0;
		Connection connect = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			connect = GetConnection.getSimpleConnection();
			stmt = connect.createStatement();
			int nTaskId = Integer.parseInt(strTaskId);
			int nType = Integer.parseInt(strType);
			String strSQL = "";
			strSQL = "delete from customer_comment_table where TaskKey = '";
			strSQL += nTaskId;
			strSQL += "' and TaskType = '";
			strSQL += nType;
			strSQL += "' and CustomerName = '";
			strSQL += strPersonName;
			strSQL += "'";
			stmt.executeUpdate(strSQL);
			nRet = 1;
		} catch (Exception e) {
			// System.out.print("get data error!");
			// e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connect != null) {
				try {
					connect.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return nRet;
	}

	@Override
	public List<TaskInfoDetail> UpdateTaskDataForLimitNew(int nLimit,
			int nType, String strMaxTime) {
		List<TaskInfoDetail> tasks = new ArrayList<TaskInfoDetail>();
		Connection connect = null;
		Statement stmt = null;
		ResultSet rs = null;
		Statement stmt1 = null;
		ResultSet rs1 = null;
		try {
			connect = GetConnection.getSimpleConnection();
			stmt = connect.createStatement();
			stmt1 = connect.createStatement();
			String strSQL = "";
			if (1 == nType) {
				strSQL = "select * from task_help_opera_table where TaskAnnounceTime > '";
				strSQL += strMaxTime;
				strSQL += "' order by TaskAnnounceTime desc Limit ";
				strSQL += 0;
				strSQL += ",";
				strSQL += nLimit;
			} else if (2 == nType) {
				strSQL = "select * from task_share_opera_table where TaskAnnounceTime > '";
				strSQL += strMaxTime;
				strSQL += "' order by TaskAnnounceTime desc Limit ";
				strSQL += 0;
				strSQL += ",";
				strSQL += nLimit;
			}
			rs = stmt.executeQuery(strSQL);
			tasks.clear();
			// ��ȡϵͳ��ǰʱ��
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy��MM��dd��HH:mm:ss");
			Date curDate = new Date(System.currentTimeMillis());// ��ȡ��ǰʱ��
			String strCurrentTime = formatter.format(curDate);

			while (rs.next()) {
				// ���񷢲�ʱ��
				String strAnnounceTime = rs.getString(18);
				Date d1 = formatter.parse(strCurrentTime);
				Date d2 = formatter.parse(strAnnounceTime);
				long diff = d1.getTime() - d2.getTime();// �����õ��Ĳ�ֵ��΢�뼶��

				// �������������˵�ͷ��
				String strAnnounceImage = "";
				// ��ȡ������������������
				String strAnnounceName = rs.getString(2);
				// ��ȡ�û�ͷ��
				strSQL = "select * from customer_info_table where Account = '";
				strSQL += strAnnounceName;
				strSQL += "'";
				rs1 = stmt1.executeQuery(strSQL);
				while (rs1.next()) {
					strAnnounceImage = rs1.getString(1);
				}
				if (rs1 != null) {
					rs1.close();
					rs1 = null;
				}
				int nRecordNum = 0;
				// ��ȡ��������
				String strCommentTableName = rs.getString(9);
				strSQL = "select count(*) from ";
				strSQL += strCommentTableName;
				rs1 = stmt1.executeQuery(strSQL);
				while (rs1.next()) {
					nRecordNum = rs1.getInt(1);
				}

				if (rs1 != null) {
					rs1.close();
					rs1 = null;
				}
				// long nLimitTime = Long.parseLong(rs.getString(23));
				// �ж��Ƿ���,���û�е���
				// if (diff / 1000 - nLimitTime < 0) {
				TaskInfoDetail task = new TaskInfoDetail(rs.getString(16),
						strAnnounceImage, rs.getString(28), rs.getString(15),
						rs.getInt(1) + "", rs.getDouble(20), rs.getDouble(21),
						strAnnounceTime, rs.getString(23), rs.getString(17),
						diff / 1000 + "", rs.getString(29), rs.getInt(24),
						rs.getInt(25), rs.getInt(14), rs.getInt(4),
						rs.getInt(5), rs.getInt(6), rs.getInt(7), rs.getInt(8),
						0, rs.getString(10), rs.getString(11),
						rs.getString(12), rs.getString(13), strAnnounceName,
						rs.getString(3), rs.getInt(30), rs.getInt(31),
						rs.getInt(33), nRecordNum, rs.getInt(32));
				tasks.add(task);
				// }
			}
		} catch (Exception e) {
			// System.out.print("get data error!");
			// e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (rs1 != null) {
				try {
					rs1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt1 != null) {
				try {
					stmt1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connect != null) {
				try {
					connect.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return tasks;
	}

	@Override
	public List<TaskInfoDetail> LoadTaskDataForLimitNew(int nLimit, int nType,
			String strMaxTime) {
		List<TaskInfoDetail> tasks = new ArrayList<TaskInfoDetail>();
		Connection connect = null;
		Statement stmt = null;
		ResultSet rs = null;
		Statement stmt1 = null;
		ResultSet rs1 = null;
		try {
			connect = GetConnection.getSimpleConnection();
			stmt = connect.createStatement();
			stmt1 = connect.createStatement();
			String strSQL = "";
			if (1 == nType) {
				strSQL = "select * from task_help_opera_table where TaskAnnounceTime < '";
				strSQL += strMaxTime;
				strSQL += "' order by TaskAnnounceTime desc Limit ";
				strSQL += 0;
				strSQL += ",";
				strSQL += nLimit;
			} else if (2 == nType) {
				strSQL = "select * from task_share_opera_table where TaskAnnounceTime < '";
				strSQL += strMaxTime;
				strSQL += "' order by TaskAnnounceTime desc Limit ";
				strSQL += 0;
				strSQL += ",";
				strSQL += nLimit;
			}
			rs = stmt.executeQuery(strSQL);
			tasks.clear();
			// ��ȡϵͳ��ǰʱ��
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy��MM��dd��HH:mm:ss");
			Date curDate = new Date(System.currentTimeMillis());// ��ȡ��ǰʱ��
			String strCurrentTime = formatter.format(curDate);

			while (rs.next()) {
				// ���񷢲�ʱ��
				String strAnnounceTime = rs.getString(18);
				Date d1 = formatter.parse(strCurrentTime);
				Date d2 = formatter.parse(strAnnounceTime);
				long diff = d1.getTime() - d2.getTime();// �����õ��Ĳ�ֵ��΢�뼶��

				// �������������˵�ͷ��
				String strAnnounceImage = "";
				// ��ȡ������������������
				String strAnnounceName = rs.getString(2);
				// ��ȡ�û�ͷ��
				strSQL = "select * from customer_info_table where Account = '";
				strSQL += strAnnounceName;
				strSQL += "'";
				rs1 = stmt1.executeQuery(strSQL);
				while (rs1.next()) {
					strAnnounceImage = rs1.getString(1);
				}
				if (rs1 != null) {
					rs1.close();
					rs1 = null;
				}
				int nRecordNum = 0;
				// ��ȡ��������
				String strCommentTableName = rs.getString(9);
				strSQL = "select count(*) from ";
				strSQL += strCommentTableName;
				rs1 = stmt1.executeQuery(strSQL);
				while (rs1.next()) {
					nRecordNum = rs1.getInt(1);
				}
				if (rs1 != null) {
					rs1.close();
					rs1 = null;
				}
				// long nLimitTime = Long.parseLong(rs.getString(23));
				// �ж��Ƿ���,���û�е���
				// if (diff / 1000 - nLimitTime < 0) {
				TaskInfoDetail task = new TaskInfoDetail(rs.getString(16),
						strAnnounceImage, rs.getString(28), rs.getString(15),
						rs.getInt(1) + "", rs.getDouble(20), rs.getDouble(21),
						strAnnounceTime, rs.getString(23), rs.getString(17),
						diff / 1000 + "", rs.getString(29), rs.getInt(24),
						rs.getInt(25), rs.getInt(14), rs.getInt(4),
						rs.getInt(5), rs.getInt(6), rs.getInt(7), rs.getInt(8),
						0, rs.getString(10), rs.getString(11),
						rs.getString(12), rs.getString(13), strAnnounceName,
						rs.getString(3), rs.getInt(30), rs.getInt(31),
						rs.getInt(33), nRecordNum, rs.getInt(32));
				tasks.add(task);
				// }

			}
		} catch (Exception e) {
			// System.out.print("get data error!");
			// e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (rs1 != null) {
				try {
					rs1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt1 != null) {
				try {
					stmt1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connect != null) {
				try {
					connect.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return tasks;
	}
}