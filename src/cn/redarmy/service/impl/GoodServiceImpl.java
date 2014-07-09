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

	// 根据字符串id生成一个list<String>容器
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
			// 将新的任务插入到数据库中
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy年MM月dd日HH:mm:ss");
			Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
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
			// 获取系统当前时间
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy年MM月dd日HH:mm:ss");
			Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
			String strCurrentTime = formatter.format(curDate);

			while (rs.next()) {
				// 任务发布时间
				String strAnnounceTime = rs.getString(18);
				Date d1 = formatter.parse(strCurrentTime);
				Date d2 = formatter.parse(strAnnounceTime);
				long diff = d1.getTime() - d2.getTime();// 这样得到的差值是微秒级别

				// 任务或分享发布人的名称
				String strAnnounceName = rs.getString(2);
				// 任务或分享发布人的头像
				String strAnnounceImage = "";
				// 获取用户头像
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
		int nRetType = 1;// 为0表示成功,1表示失败,2表示账号重名
		Connection connect = null;
		Statement stmt = null;
		ResultSet rs = null;
		boolean bIsFind = false;
		try {
			connect = GetConnection.getSimpleConnection();
			stmt = connect.createStatement();
			// 先判断要注册的账号是否存在
			String strSQL = "select * from customer_info_table where Account = '";
			strSQL += strPersonName;
			strSQL += "'";
			rs = stmt.executeQuery(strSQL);
			while (rs.next()) {
				// 如果找到了
				bIsFind = true;
				nRetType = 2;
				break;
			}
			// 如果没有找到,那么就注册用户
			if (!bIsFind) {

				String strTaskAnnounceTime = "";
				// 将新的任务插入到数据库中
				SimpleDateFormat formatter = new SimpleDateFormat(
						"yyyy年MM月dd日HH:mm:ss");
				Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
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
			// 先判断要注册的账号是否存在
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
			String strIcon1,// 第一张压缩图片(下面类似)
			String strIcon2, String strIcon3, String strIcon4, String strIcon5,
			String strIcon6) {
		int nRetType = 0;// 为0表示失败
		Connection connect = null;
		Statement stmt = null;
		ResultSet rs = null;
		int nTaskCount = 0;
		try {
			connect = GetConnection.getSimpleConnection();
			stmt = connect.createStatement();
			String strSQL = "";
			// 先判断要注册的账号是否存在
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
				// 如果该任务为求助类型
				if (strTaskType.equals("1")) {
					// 先获取任务数量
					strSQL = "select * from task_help_opera_table";
					rs = stmt.executeQuery(strSQL);
					while (rs.next()) {
						rs.last();
						nTaskCount = rs.getRow();
						break;
					}
					nTaskCount += 1;
					String strTaskCount = nTaskCount + "";
					// 将新的任务插入到数据库中
					SimpleDateFormat formatter = new SimpleDateFormat(
							"yyyy年MM月dd日HH:mm:ss");
					Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
					strTaskAnnounceTime = formatter.format(curDate);
					int nTaskType = Integer.parseInt(strTaskType);
					String strCommentTableName = strTaskCount + "_h1";
					// 创建评论表
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
					// 将相应的数据插入到任务操作表中
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
					strSQL += "','1','0','0','0','闵行区','上海')";
					stmt.execute(strSQL);
					if (rs != null) {
						rs.close();
						rs = null;
					}
					// 插入缩小的图片
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

					// 插入相应记录
					strSQL = "insert into customer_comment_table values('";
					strSQL += nTaskCount;
					strSQL += "','";
					strSQL += strTaskAccountTrueName;
					strSQL += "','1','1')";
					stmt.execute(strSQL);
					// 将更新标志设为更新状态,通知所有用户
					strSQL = "update customer_info_table";
					strSQL += " set UpdateSignal = '1'";
					stmt.executeUpdate(strSQL);

					nRetType = nTaskCount;
				} else if (strTaskType.equals("2")) {// 如果该任务为分享
					// 先获取任务数量
					strSQL = "select * from task_share_opera_table";
					rs = stmt.executeQuery(strSQL);
					while (rs.next()) {
						rs.last();
						nTaskCount = rs.getRow();
						break;
					}
					nTaskCount += 1;
					String strTaskCount = nTaskCount + "";
					// 将新的任务插入到数据库中
					SimpleDateFormat formatter = new SimpleDateFormat(
							"yyyy年MM月dd日HH:mm:ss");
					Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
					strTaskAnnounceTime = formatter.format(curDate);
					int nTaskType = Integer.parseInt(strTaskType);
					String strCommentTableName = strTaskCount + "_s1";
					// 创建评论表
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
					// 将相应的数据插入到任务操作表中
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
					strSQL += "','1','0','0','0','闵行区','上海')";
					stmt.execute(strSQL);

					if (rs != null) {
						rs.close();
						rs = null;
					}
					// 插入缩小的图片
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

					// 插入相应记录
					strSQL = "insert into customer_comment_table values('";
					strSQL += nTaskCount;
					strSQL += "','";
					strSQL += strTaskAccountTrueName;
					strSQL += "','1','2')";
					stmt.execute(strSQL);
					// 将更新标志设为更新状态,通知所有用户
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
		int nRetType = 0;// 0表示失败,为1表示成功,2表示该任务已有人拿下
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
			// 首先根据人名获取人品值和赞值
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
			// 如果为求助任务
			if (strType.equals("1")) {
				// 先判断该任务是否被其他人选择了
				strSQL = "select * from task_help_opera_table where TaskKey = '";
				strSQL += nTaskId;
				strSQL += "'";
				rs = stmt.executeQuery(strSQL);
				String strTaskAccountName = "";
				int nTaskSelectType = 1;

				int nTaskCreditValue = 0;
				int nTaskCharmValue = 0;
				while (rs.next()) {
					// 获取任务处理类型
					nTaskSelectType = rs.getInt(4);
					// 获取任务发布人
					strTaskAccountName = rs.getString(2);
					nTaskCreditValue = rs.getInt(30);
					nTaskCharmValue = rs.getInt(31);
					break;
				}
				// 如果人品值或赞值不够
				if (nCreditValue < nTaskCreditValue
						|| nCharmValue < nTaskCharmValue) {
					nRetType = 0;
				} else {
					// 如果为空,则表明该任务还没有其他人选择
					if (1 == nTaskSelectType) {
						// 更新task_opera_table表
						strSQL = "update task_help_opera_table";
						strSQL += " set TaskSelectType = '2',TaskImplementStatus = '2',TaskImplementAccount = '";
						strSQL += strPersonName;
						strSQL += "',TaskImplementAccountNickName = '";
						strSQL += strPersonNickName;
						strSQL += "' where TaskKey = '";
						strSQL += nTaskId;
						strSQL += "'";
						stmt.executeUpdate(strSQL);

						// 将更新标志设为更新状态
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
			} else if (strType.equals("2")) {// 如果为分享任务
				// 先判断该任务是否被其他人选择了
				strSQL = "select * from task_share_opera_table where TaskKey = '";
				strSQL += nTaskId;
				strSQL += "'";
				rs = stmt.executeQuery(strSQL);
				String strTaskAccountName = "";
				int nTaskSelectType = 1;

				int nTaskCreditValue = 0;
				int nTaskCharmValue = 0;

				while (rs.next()) {
					// 获取任务处理类型
					nTaskSelectType = rs.getInt(4);
					// 获取任务发布人
					strTaskAccountName = rs.getString(2);
					nTaskCreditValue = rs.getInt(30);
					nTaskCharmValue = rs.getInt(31);
					break;
				}
				// 如果人品值或赞值不够
				if (nCreditValue < nTaskCreditValue
						|| nCharmValue < nTaskCharmValue) {
					nRetType = 0;
				} else {
					// 如果为空,则表明该任务还没有其他人选择
					if (1 == nTaskSelectType) {
						// 更新task_opera_table表
						strSQL = "update task_share_opera_table";
						strSQL += " set TaskSelectType = '2',TaskImplementStatus = '2',TaskImplementAccount = '";
						strSQL += strPersonName;
						strSQL += "',TaskImplementAccountNickName = '";
						strSQL += strPersonNickName;
						strSQL += "' where TaskKey = '";
						strSQL += nTaskId;
						strSQL += "'";
						stmt.executeUpdate(strSQL);

						// 将更新标志设为更新状态
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
			// strAnnounceName作为发布者时的动态
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

			// strAnnounceName作为分享者的动态
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

			// strAnnounceName作为执行者的动态
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

			// strAnnounceName作为接收者的动态
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
			// 针对求助任务和求助分享
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
				// 获取系统当前时间
				SimpleDateFormat formatter = new SimpleDateFormat(
						"yyyy年MM月dd日HH:mm:ss");
				Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
				String strCurrentTime = formatter.format(curDate);
				// 任务发布时间
				String strAnnounceTime = rs.getString(18);
				Date d1 = formatter.parse(strCurrentTime);
				Date d2 = formatter.parse(strAnnounceTime);
				long diff = d1.getTime() - d2.getTime();// 这样得到的差值是微秒级别
				// 获取任务或分享发布人名称
				String strAnnounceName1 = rs.getString(2);
				// 任务或分享发布人的头像
				String strAnnounceImage = "";
				// 获取用户头像
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
			// 任务类型
			int nTaskType = 0;
			// 任务id号
			int nTaskId = 0;
			// 消息状态
			int nMessageStatus = 0;
			// 获取新消息的条数
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
			// 根据表customer_comment_table获取与指定用户相关的数据
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
					// 消息状态
					int nStatus = nMessageStatus;
					String strName = "";
					// 获取系统当前时间
					SimpleDateFormat formatter = new SimpleDateFormat(
							"yyyy年MM月dd日HH:mm:ss");
					Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
					String strCurrentTime = formatter.format(curDate);

					strSQL = "select * from task_help_opera_table where TaskKey = '";
					strSQL += nTaskId;
					strSQL += "'";
					rs1 = stmt1.executeQuery(strSQL);
					while (rs1.next()) {
						// 任务id号
						int nid = rs1.getInt(1);
						// 如果两者的id不相同
						if (nTaskId != nid) {
							nStatus = 1;
						}
						strName = rs1.getString(2);

						// 任务或分享发布人的头像
						String strAnnounceImage = "";
						// 获取用户头像
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
						// 任务发布时间
						String strAnnounceTime = rs1.getString(18);
						Date d1 = formatter.parse(strCurrentTime);
						Date d2 = formatter.parse(strAnnounceTime);
						long diff = d1.getTime() - d2.getTime();// 这样得到的差值是微秒级别

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
					// 消息状态
					int nStatus = nMessageStatus;
					String strName = "";
					// 获取系统当前时间
					SimpleDateFormat formatter = new SimpleDateFormat(
							"yyyy年MM月dd日HH:mm:ss");
					Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
					String strCurrentTime = formatter.format(curDate);

					strSQL = "select * from task_share_opera_table where TaskKey = '";
					strSQL += nTaskId;
					strSQL += "'";
					rs1 = stmt1.executeQuery(strSQL);
					while (rs1.next()) {
						// 任务id号
						int nid = rs1.getInt(1);
						// 如果两者的id不相同,说明这个分享是由当前用户发布的
						if (nTaskId != nid) {
							nStatus = 1;
						}
						strName = rs1.getString(2);

						// 任务或分享发布人的头像
						String strAnnounceImage = "";
						// 获取用户头像
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
						// 任务发布时间
						String strAnnounceTime = rs1.getString(18);
						Date d1 = formatter.parse(strCurrentTime);
						Date d2 = formatter.parse(strAnnounceTime);
						long diff = d1.getTime() - d2.getTime();// 这样得到的差值是微秒级别

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
				// 更新数据库表(针对发布者)
				String strSQL = "update task_help_opera_table";
				strSQL += " set TaskSelectType = '";
				strSQL += nValue;
				strSQL += "' where TaskAccount = '";
				strSQL += strAnnounceName;
				strSQL += "' and TaskKey = '";
				strSQL += nTaskId;
				strSQL += "'";
				stmt.executeUpdate(strSQL);

				// 将更新标志设为更新状态
				strSQL = "update customer_info_table";
				strSQL += " set UpdateSignal = '1' where Account = '";
				strSQL += strAnnounceName;
				strSQL += "'";
				stmt.executeUpdate(strSQL);

				nRet = 1;
			} else if (strType.equals("2")) {
				// 更新数据库表(针对发布者)
				String strSQL = "update task_share_opera_table";
				strSQL += " set TaskSelectType = '";
				strSQL += nValue;
				strSQL += "' where TaskAccount = '";
				strSQL += strAnnounceName;
				strSQL += "' and TaskKey = '";
				strSQL += nTaskId;
				strSQL += "'";
				stmt.executeUpdate(strSQL);

				// 将更新标志设为更新状态
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

			// 将新的任务插入到数据库中
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy年MM月dd日HH:mm:ss");
			Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
			String strTaskImplementTime = formatter.format(curDate);

			// 求助任务,此时strAnnounceName为发布者名称
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

			} else if (2 == nTaskType) {// 分享任务,此时strAnnounceName为接收分享者
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

			// 将更新标志设为更新状态
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

				// 将更新标志设为更新状态
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

				// 将更新标志设为更新状态
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
					// 更新数据库(针对执行者)
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
					// 更新数据库(针对执行者)
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
					// 更新数据库(针对分享者)
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
					// 更新数据库(针对分享者)
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

			// 将更新标志设为更新状态
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
			// 将更新标志设为更新状态
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
			// 评论人的名称
			String strCommentPersonName;
			// 被评论人的名称
			String strCommentReceivePersonName;
			// 评论时间
			String strCommentTime;
			// 评论内容
			String strCommentContent;
			// 压缩图片
			String strSmallImage;
			// 放大图片
			// String strLargeImage;
			// 评论号
			int nCommentIndex;
			// 评论人的名称
			String strCommentPersonTrueName;
			// 被评论人的名称
			String strCommentReceivePersonTrueName;

			int nTaskId = Integer.parseInt(strTaskId);
			String strCommentTableName = "";
			if (strType.equals("1")) {
				// 根据任务id获取到评论表名称
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
				// 根据获取到的评论表名称获取到相应数据
				strSQL = "select * from ";
				strSQL += strCommentTableName;
				rs = stmt.executeQuery(strSQL);
				while (rs.next()) {
					// 从评论表中取出数据
					strCommentPersonName = rs.getString(3);
					strCommentReceivePersonName = rs.getString(4);
					strCommentTime = rs.getString(2);
					strCommentContent = rs.getString(5);
					strSmallImage = rs.getString(7);
					nCommentIndex = rs.getInt(9);
					strCommentPersonTrueName = rs.getString(10);
					strCommentReceivePersonTrueName = rs.getString(11);

					// 评论人的头像
					String strAnnounceImage = "";
					// 评论人名称
					String strAnnounceName = rs.getString(10);
					// 获取用户头像
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
				// 根据任务id获取到评论表名称
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
				// 根据获取到的评论表名称获取到相应数据
				strSQL = "select * from ";
				strSQL += strCommentTableName;
				rs = stmt.executeQuery(strSQL);
				while (rs.next()) {
					// 从评论表中取出数据
					strCommentPersonName = rs.getString(3);
					strCommentReceivePersonName = rs.getString(4);
					strCommentTime = rs.getString(2);
					strCommentContent = rs.getString(5);
					strSmallImage = rs.getString(7);
					// strLargeImage = rs.getString(8);
					nCommentIndex = rs.getInt(9);
					strCommentPersonTrueName = rs.getString(10);
					strCommentReceivePersonTrueName = rs.getString(11);

					// 任务或分享发布人的头像
					String strAnnounceImage = "";
					// 获取任务或分享发布人名称
					String strAnnounceName = rs.getString(10);
					// 获取用户头像
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
			// 发布人
			String strAccountName = "";
			// 评论人的名称
			String strCommentPersonName;
			// 被评论人的名称
			String strCommentReceivePersonName;
			// 评论时间
			String strCommentTime;
			// 评论内容
			String strCommentContent;
			// 压缩图片
			String strSmallImage;
			// 评论号
			int nCommentIndex;
			// 评论人的名称
			String strCommentPersonTrueName;
			// 被评论人的名称
			String strCommentReceivePersonTrueName;
			// 评论内容类型(是否是悄悄话)
			int nCommentType;

			int nTaskId = Integer.parseInt(strTaskId);
			String strCommentTableName = "";
			if (strType.equals("1")) {
				// 根据任务id获取到评论表名称
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
				// 根据获取到的评论表名称获取到相应数据
				strSQL = "select * from ";
				strSQL += strCommentTableName;
				strSQL += " ORDER BY TaskTalkTime asc";
				rs = stmt.executeQuery(strSQL);
				while (rs.next()) {
					strCommentPersonTrueName = rs.getString(10);
					strCommentReceivePersonTrueName = rs.getString(11);
					// 评论类型(是否是悄悄话)
					nCommentType = rs.getInt(12);
					// 如果当前用户不是这条评论的评论者和接收者以及当前用户不是发布人,同时这条评论是悄悄话,那么就不存储相应的内容
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
					// 评论人的头像
					String strAnnounceImage = "";
					// 评论人名称
					String strAnnounceName = rs.getString(10);
					// 获取用户头像
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
				// 根据任务id获取到评论表名称
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
				// 根据获取到的评论表名称获取到相应数据
				strSQL = "select * from ";
				strSQL += strCommentTableName;
				rs = stmt.executeQuery(strSQL);
				while (rs.next()) {
					strCommentPersonTrueName = rs.getString(10);
					strCommentReceivePersonTrueName = rs.getString(11);
					// 评论类型(是否是悄悄话)
					nCommentType = rs.getInt(12);
					// 如果当前用户不是这条评论的评论者和接收者以及当前用户不是发布人,同时这条评论是悄悄话,那么就不存储相应的内容
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

					// 任务或分享发布人的头像
					String strAnnounceImage = "";
					// 获取任务或分享发布人名称
					String strAnnounceName = rs.getString(10);
					// 获取用户头像
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
			// 评论表名称
			String strCommentTableName = "";
			// 任务发布人名称
			String strTaskAnnouncePersonName = "";
			// 任务执行人名称
			String strTaskImplePersonName = "";
			int nTaskId = Integer.parseInt(strTaskId);
			if (strType.equals("1")) {
				// 先根据strTaskId获取到评论表
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
				// 如果评论人是任务发布人
				if (strCommentPersonName.equals(strTaskAnnouncePersonName)) {
					if (!strReceiveCommentPersonName.equals("")) {
						// 如果指定了接收人,那么就给接收人提示
						strSQL = "update task_help_opera_table";
						strSQL += " set TaskAnnounceCommentType = '2'";
						strSQL += " where TaskKey = '";
						strSQL += nTaskId;
						strSQL += "'";
						stmt.executeUpdate(strSQL);

						// 将更新标志设为更新状态
						strSQL = "update customer_info_table";
						strSQL += " set UpdateSignal = '1' where Account = '";
						strSQL += strReceiveCommentPersonName;
						strSQL += "'";
						stmt.executeUpdate(strSQL);
					}
				} else if (strCommentPersonName.equals(strTaskImplePersonName)) {// 如果评论人是任务执行人
					// 如果指定了接收人,那么不管什么情况都给对方提示
					strSQL = "update task_help_opera_table";
					strSQL += " set TaskImplementCommentType = '2'";
					strSQL += " where TaskKey = '";
					strSQL += nTaskId;
					strSQL += "'";
					stmt.executeUpdate(strSQL);

					// 将更新标志设为更新状态
					strSQL = "update customer_info_table";
					strSQL += " set UpdateSignal = '1' where Account = '";
					strSQL += strTaskAnnouncePersonName;
					strSQL += "'";
					stmt.executeUpdate(strSQL);
				}
				// 获取当前时间
				SimpleDateFormat formatter = new SimpleDateFormat(
						"yyyy年MM月dd日HH:mm:ss");
				Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
				String strTaskAnnounceTime = formatter.format(curDate);

				// 将数据插入到表中
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
				// 先根据strTaskId获取到评论表
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
				// 如果评论人是任务发布人
				if (strCommentPersonName.equals(strTaskAnnouncePersonName)) {
					if (!strReceiveCommentPersonName.equals("")) {
						// 如果指定了接收人,那么就给接收人提示
						strSQL = "update task_share_opera_table";
						strSQL += " set TaskAnnounceCommentType = '2'";
						strSQL += " where TaskKey = '";
						strSQL += nTaskId;
						strSQL += "'";
						stmt.executeUpdate(strSQL);

						// 将更新标志设为更新状态
						strSQL = "update customer_info_table";
						strSQL += " set UpdateSignal = '1' where Account = '";
						strSQL += strReceiveCommentPersonName;
						strSQL += "'";
						stmt.executeUpdate(strSQL);
					}
				} else if (strCommentPersonName.equals(strTaskImplePersonName)) {// 如果评论人是任务执行人
					// 如果指定了接收人,那么不管什么情况都给对方提示
					strSQL = "update task_share_opera_table";
					strSQL += " set TaskImplementCommentType = '2'";
					strSQL += " where TaskKey = '";
					strSQL += nTaskId;
					strSQL += "'";
					stmt.executeUpdate(strSQL);

					// 将更新标志设为更新状态
					strSQL = "update customer_info_table";
					strSQL += " set UpdateSignal = '1' where Account = '";
					strSQL += strTaskAnnouncePersonName;
					strSQL += "'";
					stmt.executeUpdate(strSQL);
				}
				// 获取当前时间
				SimpleDateFormat formatter = new SimpleDateFormat(
						"yyyy年MM月dd日HH:mm:ss");
				Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
				String strTaskAnnounceTime = formatter.format(curDate);

				// 将数据插入到表中
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
			// 评论表名称
			String strCommentTableName = "";
			// 任务发布人名称
			String strTaskAnnouncePersonName = "";
			// 任务执行人名称
			String strTaskImplePersonName = "";
			int nTaskId = Integer.parseInt(strTaskId);
			if (strType.equals("1")) {
				// 先根据strTaskId获取到评论表
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
				// 如果评论人是任务发布人
				if (strCommentPersonName.equals(strTaskAnnouncePersonName)) {
					if (!strReceiveCommentPersonName.equals("")) {
						// 如果指定了接收人,那么就给接收人提示
						strSQL = "update task_help_opera_table";
						strSQL += " set TaskAnnounceCommentType = '2'";
						strSQL += " where TaskKey = '";
						strSQL += nTaskId;
						strSQL += "'";
						stmt.executeUpdate(strSQL);

						// 将更新标志设为更新状态
						strSQL = "update customer_info_table";
						strSQL += " set UpdateSignal = '1' where Account = '";
						strSQL += strReceiveCommentPersonName;
						strSQL += "'";
						stmt.executeUpdate(strSQL);
					}
				} else if (strCommentPersonName.equals(strTaskImplePersonName)) {// 如果评论人是任务执行人
					// 如果指定了接收人,那么不管什么情况都给对方提示
					strSQL = "update task_help_opera_table";
					strSQL += " set TaskImplementCommentType = '2'";
					strSQL += " where TaskKey = '";
					strSQL += nTaskId;
					strSQL += "'";
					stmt.executeUpdate(strSQL);

					// 将更新标志设为更新状态
					strSQL = "update customer_info_table";
					strSQL += " set UpdateSignal = '1' where Account = '";
					strSQL += strTaskAnnouncePersonName;
					strSQL += "'";
					stmt.executeUpdate(strSQL);
				}
				// 获取当前时间
				SimpleDateFormat formatter = new SimpleDateFormat(
						"yyyy年MM月dd日HH:mm:ss");
				Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
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
				// 将数据插入到表中
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
				// 先根据strTaskId获取到评论表
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
				// 如果评论人是任务发布人
				if (strCommentPersonName.equals(strTaskAnnouncePersonName)) {
					if (!strReceiveCommentPersonName.equals("")) {
						// 如果指定了接收人,那么就给接收人提示
						strSQL = "update task_share_opera_table";
						strSQL += " set TaskAnnounceCommentType = '2'";
						strSQL += " where TaskKey = '";
						strSQL += nTaskId;
						strSQL += "'";
						stmt.executeUpdate(strSQL);

						// 将更新标志设为更新状态
						strSQL = "update customer_info_table";
						strSQL += " set UpdateSignal = '1' where Account = '";
						strSQL += strReceiveCommentPersonName;
						strSQL += "'";
						stmt.executeUpdate(strSQL);
					}
				} else if (strCommentPersonName.equals(strTaskImplePersonName)) {// 如果评论人是任务执行人
					// 如果指定了接收人,那么不管什么情况都给对方提示
					strSQL = "update task_share_opera_table";
					strSQL += " set TaskImplementCommentType = '2'";
					strSQL += " where TaskKey = '";
					strSQL += nTaskId;
					strSQL += "'";
					stmt.executeUpdate(strSQL);

					// 将更新标志设为更新状态
					strSQL = "update customer_info_table";
					strSQL += " set UpdateSignal = '1' where Account = '";
					strSQL += strTaskAnnouncePersonName;
					strSQL += "'";
					stmt.executeUpdate(strSQL);
				}
				// 获取当前时间
				SimpleDateFormat formatter = new SimpleDateFormat(
						"yyyy年MM月dd日HH:mm:ss");
				Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
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
				// 将数据插入到表中
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
			// 开始针对这个人去判断任务的完成性
			// 针对求助任务
			strSQL = "select * from task_help_opera_table where TaskImplementAccount = '";
			strSQL += strCurrentAccountName;
			strSQL += "' or TaskAccount = '";
			strSQL += strCurrentAccountName;
			strSQL += "' union select * from task_share_opera_table where TaskImplementAccount = '";
			strSQL += strCurrentAccountName;
			strSQL += "' or TaskAccount = '";
			strSQL += strCurrentAccountName;
			strSQL += "'";
			// 任务执行人
			String strTaskImpleName1 = "";
			// 发布者
			String strTaskAccountName1 = "";
			// 任务的拿下情况
			int nTaskSelectType1 = 0;
			// 任务的发布时间
			String strTaskAccountTime1 = "";
			// 任务的时间限制(秒)
			String strTimeLimit1 = "";
			// 任务是否已经结束
			int nTaskVerifiType1 = 0;
			// 类型,1表示求助,2表示分享
			int nType = 0;
			// 任务或分享id
			int nTaskId = 0;

			// 获取系统当前时间
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy年MM月dd日HH:mm:ss");
			Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
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
				// 如果这个任务有人拿下了,同时这个任务还没有结束
				if ((2 == nTaskSelectType1 || 3 == nTaskSelectType1)
						&& 1 == nTaskVerifiType1) {
					// 判断任务从发布到现在是不是超过了时限的7天
					if ((diff / 1000) >= (nTimeSeconds + lTime)) {
						int nSumCreditValue = 0;
						// 如果是求助任务
						if (1 == nType) {
							// 设置结束状态
							strSQL = "update task_help_opera_table";
							strSQL += " set TaskVerifiType = '2'";
							strSQL += ",CreditIncrValue = '1',CharmIncrValue = '0'";
							strSQL += " where TaskImplementAccount = '";
							strSQL += strTaskImpleName1;
							strSQL += "' and TaskKey = '";
							strSQL += nTaskId;
							strSQL += "'";
							stmt1.executeUpdate(strSQL);
							// 此时对任务执行人人品加一操作
							// 先取出人品值
							strSQL = "select * from customer_info_table where Account = '";
							strSQL += strTaskImpleName1;
							strSQL += "'";
							rs1 = stmt1.executeQuery(strSQL);
							while (rs1.next()) {
								nSumCreditValue = rs1.getInt(13) + 1;
							}
							// 将更新标志设为更新状态
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
							// 关闭rs1
							if (rs1 != null) {
								rs1.close();
								rs1 = null;
							}
						} else if (2 == nType) {// 如果为分享
							// 设置结束状态
							strSQL = "update task_share_opera_table";
							strSQL += " set TaskVerifiType = '2'";
							strSQL += ",CreditIncrValue = '1',CharmIncrValue = '0'";
							strSQL += " where TaskImplementAccount = '";
							strSQL += strTaskImpleName1;
							strSQL += "' and TaskKey = '";
							strSQL += nTaskId;
							strSQL += "'";
							stmt1.executeUpdate(strSQL);

							// 先取出人品值
							strSQL = "select * from customer_info_table where Account = '";
							strSQL += strTaskAccountName1;
							strSQL += "'";
							rs1 = stmt1.executeQuery(strSQL);
							while (rs1.next()) {
								nSumCreditValue = rs1.getInt(13) + 1;
							}
							// 将更新标志设为更新状态
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
							// 关闭rs1
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

			// 将更新标志设为更新状态
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
				// 针对求助任务
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
					// 根据任务id获取任务标题名称
					String strTitle = rs.getString(15);
					// 根据任务id获取任务类型
					int nTaskType = rs.getInt(14);
					// 分析数据开始
					// boolean bIsTure = false;
					int nTaskSelectType = rs.getInt(4);
					int nTaskFinishType = rs.getInt(5);
					int nTaskVerifiType = rs.getInt(6);
					int nTaskAnnounceCommentType = rs.getInt(7);
					int nTaskImplementCommentType = rs.getInt(8);
					// 如果这个任务是由当前用户创建的
					if (strCurrentAccountName.equals(strAccounceName)) {
						boolean b3 = !strImpleAccouceName.equals("");
						if (b3) {
							if (2 == nTaskSelectType) {
								if (1 == nTaskType) {
									strStatusInfo += strImpleAccouceNickName
											+ "拿下了你发布的标题为'" + strTitle + "'的任务";
								}
							}
							if (1 == nTaskType) {
								if (2 == nTaskFinishType) {
									strStatusInfo += strImpleAccouceNickName
											+ "完成了你发布的标题为'" + strTitle + "'的任务";
								}
							}
							if (2 == nTaskImplementCommentType) {
								if (1 == nTaskType) {
									strStatusInfo += strImpleAccouceNickName
											+ "对你发布的标题为'" + strTitle
											+ "'的任务评论了";
								}

							}

						}
					} else if (strCurrentAccountName
							.equals(strImpleAccouceName)) {// 如果这个任务是由当前用户执行的或者接收分享的

						if (1 == nTaskType) {
							if (2 == nTaskVerifiType) {
								strStatusInfo += strAccounceNickName
										+ "对你执行的标题为'" + strTitle + "'的任务评价了";
							}
						}
						if (2 == nTaskAnnounceCommentType) {
							if (1 == nTaskType) {
								strStatusInfo += strAccounceNickName
										+ "对你执行的标题为'" + strTitle + "'的任务评论了";
							}

						}

					}
					// 分析数据结尾
				}
				if (rs != null) {
					rs.close();
					rs = null;
				}
				// 针对分享任务
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
					// 根据任务id获取任务标题名称
					String strTitle = rs.getString(15);
					// 根据任务id获取任务类型
					int nTaskType = rs.getInt(14);
					// 分析数据开始
					// boolean bIsTure = false;
					int nTaskSelectType = rs.getInt(4);
					int nTaskFinishType = rs.getInt(5);
					int nTaskVerifiType = rs.getInt(6);
					int nTaskAnnounceCommentType = rs.getInt(7);
					int nTaskImplementCommentType = rs.getInt(8);
					// 如果这个任务是由当前用户创建的或者是由当前用户分享的
					if (strCurrentAccountName.equals(strAccounceName)) {
						boolean b3 = !strImpleAccouceName.equals("");
						if (b3) {
							if (2 == nTaskSelectType) {

								if (2 == nTaskType) {
									strStatusInfo += strImpleAccouceNickName
											+ "抢下了你发布的标题为'" + strTitle + "'的分享";
								}
							}
							if (2 == nTaskType) {
								if (2 == nTaskVerifiType) {
									strStatusInfo += strImpleAccouceNickName
											+ "对你分享的标题为" + strTitle + "'的分享评价了";
								}
							}
							if (2 == nTaskImplementCommentType) {
								if (2 == nTaskType) {
									strStatusInfo += strImpleAccouceNickName
											+ "对你发布的标题为'" + strTitle
											+ "'的分享评论了";
								}
							}
						}
					} else if (strCurrentAccountName
							.equals(strImpleAccouceName)) {// 如果这个任务是由当前用户执行的或者接收分享的

						if (2 == nTaskType) {
							if (2 == nTaskFinishType) {
								strStatusInfo += strAccounceNickName
										+ "完成了标题为'" + strTitle + "'的分享";
							}
						}
						if (2 == nTaskAnnounceCommentType) {
							if (2 == nTaskType) {
								strStatusInfo += strAccounceNickName
										+ "对你接收的标题为'" + strTitle + "'的分享评论了";
							}
						}
					}
					// 分析数据结尾
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
			// 将更新标志设为更新状态
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
			// 将更新标志设为更新状态
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
			// 将更新标志设为更新状态
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
			// 将更新标志设为更新状态
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
			// 将更新标志设为更新状态
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
				// 先取出人品值
				String strSQL = "select * from customer_info_table where Account = '";
				strSQL += strPersonName;
				strSQL += "'";
				rs = stmt.executeQuery(strSQL);
				while (rs.next()) {
					nSumCreditValue = rs.getInt(13) + nIncrValue;
				}
				// 将更新标志设为更新状态
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
				// 先取出赞值
				String strSQL = "select * from customer_info_table where Account = '";
				strSQL += strPersonName;
				strSQL += "'";
				rs = stmt.executeQuery(strSQL);
				while (rs.next()) {
					nSumCreditValue = rs.getInt(14) + nIncrValue;
				}
				// 将更新标志设为更新状态
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
				// 获取系统当前时间
				SimpleDateFormat formatter = new SimpleDateFormat(
						"yyyy年MM月dd日HH:mm:ss");
				Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
				String strCurrentTime = formatter.format(curDate);

				while (rs.next()) {
					// 任务发布时间
					String strAnnounceTime = rs.getString(18);
					Date d1 = formatter.parse(strCurrentTime);
					Date d2 = formatter.parse(strAnnounceTime);
					long diff = d1.getTime() - d2.getTime();// 这样得到的差值是微秒级别
					// 获取还剩多少时间
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
				// 获取系统当前时间
				SimpleDateFormat formatter = new SimpleDateFormat(
						"yyyy年MM月dd日HH:mm:ss");
				Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
				String strCurrentTime = formatter.format(curDate);

				while (rs.next()) {
					// 任务发布时间
					String strAnnounceTime = rs.getString(18);
					Date d1 = formatter.parse(strCurrentTime);
					Date d2 = formatter.parse(strAnnounceTime);
					long diff = d1.getTime() - d2.getTime();// 这样得到的差值是微秒级别
					// 获取还剩多少时间
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

	// 加载没有到期的求助或分享任务数据,nLimit为一次加载的数据最大条数,nType为类型,1表示求助,
	// 2表示分享,nMaxTaskId表示最多能加载到的任务号
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
			// 获取系统当前时间
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy年MM月dd日HH:mm:ss");
			Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
			String strCurrentTime = formatter.format(curDate);

			while (rs.next()) {
				// 任务发布时间
				String strAnnounceTime = rs.getString(18);
				Date d1 = formatter.parse(strCurrentTime);
				Date d2 = formatter.parse(strAnnounceTime);
				long diff = d1.getTime() - d2.getTime();// 这样得到的差值是微秒级别

				// 任务或分享发布人的头像
				String strAnnounceImage = "";
				// 获取任务或分享发布人名称
				String strAnnounceName = rs.getString(2);
				// 获取用户头像
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
				// 判断是否到期,如果没有到期
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
			// 获取系统当前时间
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy年MM月dd日HH:mm:ss");
			Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
			String strCurrentTime = formatter.format(curDate);

			while (rs.next()) {
				// 任务发布时间
				String strAnnounceTime = rs.getString(18);
				Date d1 = formatter.parse(strCurrentTime);
				Date d2 = formatter.parse(strAnnounceTime);
				long diff = d1.getTime() - d2.getTime();// 这样得到的差值是微秒级别

				// 任务或分享发布人的头像
				String strAnnounceImage = "";
				// 获取任务或分享发布人名称
				String strAnnounceName = rs.getString(2);
				// 获取用户头像
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
				// 判断是否到期,如果没有到期
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

		int nRetType = 0;// 0表示失败,1表示成功
		Connection connect = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			connect = GetConnection.getSimpleConnection();
			stmt = connect.createStatement();
			int nTaskId = Integer.parseInt(strTaskId);
			String strSQL = "";
			if (strTaskType.equals("1")) {// 求助
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
		int nRetType = 0;// 0表示失败,1表示成功
		Connection connect = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			connect = GetConnection.getSimpleConnection();
			stmt = connect.createStatement();
			int nTaskId = Integer.parseInt(strTaskId);
			String strSQL = "";
			if (strTaskType.equals("1")) {// 求助
				strSQL = "insert into task_help_icon_eachother values('";
				strSQL += nTaskId;
				strSQL += "','";
				strSQL += 1;
				strSQL += "','";
				strSQL += strBase64Image;
				strSQL += "')";
				stmt.execute(strSQL);
				nRetType = 1;
			} else if (strTaskType.equals("2")) {// 分享
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
		int nRetType = 0;// 0表示失败,1表示成功
		Connection connect = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			connect = GetConnection.getSimpleConnection();
			stmt = connect.createStatement();
			int nTaskId = Integer.parseInt(strTaskId);
			String strSQL = "";
			if (strTaskType.equals("1")) {// 求助
				// 先判断该数据是否已经插入到表中
				strSQL = "select * from task_help_icon_eachother where TaskKey = '";
				strSQL += strTaskId;
				strSQL += "'";
				rs = stmt.executeQuery(strSQL);
				boolean bFind = false;
				while (rs.next()) {
					bFind = true;
					break;
				}
				if (!bFind) {// 如果没有插入,那么就插入
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

			} else if (strTaskType.equals("2")) {// 分享
				// 先判断该数据是否已经插入到表中
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
			if (strTaskType.equals("1")) {// 求助
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
			} else if (strTaskType.equals("2")) {// 分享
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
			if (strTaskType.equals("1")) {// 求助
				strSQL = "select * from task_help_largeicon where TaskKey = '";
				strSQL += nTaskId;
				strSQL += "'";
				rs = stmt.executeQuery(strSQL);
				while (rs.next()) {
					strtaskicon = rs.getString(nIconId + 1);
					break;
				}
			} else if (strTaskType.equals("2")) {// 分享
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
			if (strTaskType.equals("1")) {// 求助
				strSQL = "select * from task_help_icon_eachother where TaskKey = '";
				strSQL += nTaskId;
				strSQL += "' and IconType = '1'";
				rs = stmt.executeQuery(strSQL);
				while (rs.next()) {
					strtaskicon = rs.getString(3);
					break;
				}
			} else if (strTaskType.equals("2")) {// 分享
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
			if (strTaskType.equals("1")) {// 求助
				strSQL = "select * from task_help_icon_eachother where TaskKey = '";
				strSQL += nTaskId;
				strSQL += "' and IconType = '0'";
				rs = stmt.executeQuery(strSQL);
				while (rs.next()) {
					strtaskicon = rs.getString(3);
					break;
				}
			} else if (strTaskType.equals("2")) {// 分享
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
				// 根据任务id获取到评论表名称
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
				// 根据获取到的评论表名称获取到相应数据
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
				// 根据任务id获取到评论表名称
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
				// 根据获取到的评论表名称获取到相应数据
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
			// 将新的任务插入到数据库中
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy年MM月dd日HH:mm:ss");
			Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
			String strTaskAnnounceTime = formatter.format(curDate);
			// 如果是求助任务
			if (strType.equals("1")) {
				// 先判断该任务是否已经结束
				strSQL = "select * from task_help_opera_table where TaskKey = '";
				strSQL += nTaskId;
				strSQL += "'";
				rs = stmt.executeQuery(strSQL);
				int nTaskStatus = 1;
				while (rs.next()) {
					nTaskStatus = rs.getInt(6);
					break;
				}
				// 如果任务还没有结束
				if (1 == nTaskStatus) {
					if (rs != null) {
						rs.close();
						rs = null;
					}
					// 先将任务状态重置为没有拿下状态
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

					// 获取评论表名称
					strSQL = "select * from task_help_opera_table where TaskKey = '";
					strSQL += nTaskId;
					strSQL += "'";
					rs = stmt.executeQuery(strSQL);
					String strTabName = "";
					while (rs.next()) {
						strTabName = rs.getString(9);
						break;
					}
					// 删除评论表中的数据
					strSQL = "delete from ";
					strSQL += strTabName;
					stmt.executeUpdate(strSQL);
					// 删除task_help_icon_eachother表中的相应图片数据
					strSQL = "delete from task_help_icon_eachother where TaskKey = '";
					strSQL += nTaskId;
					strSQL += "'";
					stmt.executeUpdate(strSQL);

					// 将更新标志设为更新状态,通知所有用户
					strSQL = "update customer_info_table";
					strSQL += " set UpdateSignal = '1'";
					stmt.executeUpdate(strSQL);
					nRet = 1;
				}

			} else if (strType.equals("2")) {// 如果是分享任务
				// 先判断该任务是否已经结束
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
					// 先将任务状态重置为没有拿下状态
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
					// 获取评论表名称
					strSQL = "select * from task_share_opera_table where TaskKey = '";
					strSQL += nTaskId;
					strSQL += "'";
					rs = stmt.executeQuery(strSQL);
					String strTabName = "";
					while (rs.next()) {
						strTabName = rs.getString(9);
					}
					// 删除评论表中的数据
					strSQL = "delete from ";
					strSQL += strTabName;
					stmt.executeUpdate(strSQL);
					// 删除task_share_icon_eachother表中的相应图片数据
					strSQL = "delete from task_share_icon_eachother where TaskKey = '";
					strSQL += nTaskId;
					strSQL += "'";
					stmt.executeUpdate(strSQL);

					// 将更新标志设为更新状态,通知所有用户
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
		int nRetType = 0;// 为0表示失败,1表示新账号,2表示账号重名
		Connection connect = null;
		Statement stmt = null;
		ResultSet rs = null;
		boolean bIsFind = false;
		try {
			connect = GetConnection.getSimpleConnection();
			stmt = connect.createStatement();
			// 先判断要注册的账号是否存在
			String strSQL = "select * from customer_info_table where Account = '";
			strSQL += strName;
			strSQL += "'";
			rs = stmt.executeQuery(strSQL);
			while (rs.next()) {
				// 如果找到了
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
		int nRetType = 1;// 为0表示成功,1表示失败,2表示账号重名
		Connection connect = null;
		Statement stmt = null;
		ResultSet rs = null;
		boolean bIsFind = false;
		try {
			connect = GetConnection.getSimpleConnection();
			stmt = connect.createStatement();
			// 先判断要注册的账号是否存在
			String strSQL = "select * from customer_info_table where Account = '";
			strSQL += strPersonName;
			strSQL += "'";
			rs = stmt.executeQuery(strSQL);
			while (rs.next()) {
				// 如果找到了
				bIsFind = true;
				nRetType = 2;
				break;
			}
			// 如果没有找到,那么就注册用户
			if (!bIsFind) {

				String strTaskAnnounceTime = "";
				// 将新的任务插入到数据库中
				SimpleDateFormat formatter = new SimpleDateFormat(
						"yyyy年MM月dd日HH:mm:ss");
				Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
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
		int nRetType = 0;// 为0表示失败
		Connection connect = null;
		Statement stmt = null;
		ResultSet rs = null;
		int nTaskCount = 0;
		try {
			connect = GetConnection.getSimpleConnection();
			stmt = connect.createStatement();
			String strSQL = "";
			// 先判断要注册的账号是否存在
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
				// 如果该任务为求助类型
				if (strTaskType.equals("1")) {
					// 先获取任务数量
					strSQL = "select * from task_help_opera_table";
					rs = stmt.executeQuery(strSQL);
					while (rs.next()) {
						rs.last();
						nTaskCount = rs.getRow();
						break;
					}
					nTaskCount += 1;
					String strTaskCount = nTaskCount + "";
					// 将新的任务插入到数据库中
					SimpleDateFormat formatter = new SimpleDateFormat(
							"yyyy年MM月dd日HH:mm:ss");
					Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
					strTaskAnnounceTime = formatter.format(curDate);
					int nTaskType = Integer.parseInt(strTaskType);
					String strCommentTableName = strTaskCount + "_h1";
					// 创建评论表
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
					// 将相应的数据插入到任务操作表中
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
					strSQL += "','0','0','闵行区','上海')";
					stmt.execute(strSQL);
					if (rs != null) {
						rs.close();
						rs = null;
					}
					// 插入缩小的图片
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

					// 插入相应记录
					strSQL = "insert into customer_comment_table values('";
					strSQL += nTaskCount;
					strSQL += "','";
					strSQL += strTaskAccountTrueName;
					strSQL += "','1','1')";
					stmt.execute(strSQL);
					// 将更新标志设为更新状态,通知所有用户
					strSQL = "update customer_info_table";
					strSQL += " set UpdateSignal = '1'";
					stmt.executeUpdate(strSQL);

					nRetType = nTaskCount;
				} else if (strTaskType.equals("2")) {// 如果该任务为分享
					// 先获取任务数量
					strSQL = "select * from task_share_opera_table";
					rs = stmt.executeQuery(strSQL);
					while (rs.next()) {
						rs.last();
						nTaskCount = rs.getRow();
						break;
					}
					nTaskCount += 1;
					String strTaskCount = nTaskCount + "";
					// 将新的任务插入到数据库中
					SimpleDateFormat formatter = new SimpleDateFormat(
							"yyyy年MM月dd日HH:mm:ss");
					Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
					strTaskAnnounceTime = formatter.format(curDate);
					int nTaskType = Integer.parseInt(strTaskType);
					String strCommentTableName = strTaskCount + "_s1";
					// 创建评论表
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
					// 将相应的数据插入到任务操作表中
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
					strSQL += "','0','0','闵行区','上海')";
					stmt.execute(strSQL);
					if (rs != null) {
						rs.close();
						rs = null;
					}
					// 插入缩小的图片
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

					// 插入相应记录
					strSQL = "insert into customer_comment_table values('";
					strSQL += nTaskCount;
					strSQL += "','";
					strSQL += strTaskAccountTrueName;
					strSQL += "','1','2')";
					stmt.execute(strSQL);
					// 将更新标志设为更新状态,通知所有用户
					strSQL = "update customer_info_table";
					strSQL += " set UpdateSignal = '1'";
					stmt.executeUpdate(strSQL);
					nRetType = nTaskCount;

					// 添加分享记录
					boolean bIsFind = false;
					formatter = new SimpleDateFormat("yyyy年MM月dd日");
					curDate = new Date(System.currentTimeMillis());// 获取当前时间
					String strCurrentTime1 = formatter.format(curDate);
					int nTimes = 0;
					// 先判断表中是否有当天的记录
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
					// 如果找到了
					if (bIsFind) {
						// 将分享次数加一
						nTimes += 1;
						// 将推送标志设为已推送
						strSQL = "update customer_share_expert_table";
						strSQL += " set ShareTimes = '";
						strSQL += nTimes;
						strSQL += "' where ShareDate = '";
						strSQL += strCurrentTime1;
						strSQL += "' and Account = '";
						strSQL += strTaskAccountTrueName;
						strSQL += "'";
						stmt.executeUpdate(strSQL);
					} else {// 如果没有找到,那么插入记录数据
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
			// 获取系统当前时间
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy年MM月dd日HH:mm:ss");
			Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
			String strCurrentTime = formatter.format(curDate);

			while (rs.next()) {
				// 任务发布时间
				String strAnnounceTime = rs.getString(18);
				Date d1 = formatter.parse(strCurrentTime);
				Date d2 = formatter.parse(strAnnounceTime);
				long diff = d1.getTime() - d2.getTime();// 这样得到的差值是微秒级别

				// 任务或分享发布人的名称
				String strAnnounceName = rs.getString(2);
				// 任务或分享发布人的头像
				String strAnnounceImage = "";
				// 获取用户头像
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
				// 获取评论条数
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
			// 获取系统当前时间
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy年MM月dd日HH:mm:ss");
			Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
			String strCurrentTime = formatter.format(curDate);

			while (rs.next()) {
				// 任务发布时间
				String strAnnounceTime = rs.getString(18);
				Date d1 = formatter.parse(strCurrentTime);
				Date d2 = formatter.parse(strAnnounceTime);
				long diff = d1.getTime() - d2.getTime();// 这样得到的差值是微秒级别

				// 任务或分享发布人的头像
				String strAnnounceImage = "";
				// 获取任务或分享发布人名称
				String strAnnounceName = rs.getString(2);
				// 获取用户头像
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
				// 获取评论条数
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
				// 判断是否到期,如果没有到期
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
			// 获取系统当前时间
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy年MM月dd日HH:mm:ss");
			Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
			String strCurrentTime = formatter.format(curDate);

			while (rs.next()) {
				// 任务发布时间
				String strAnnounceTime = rs.getString(18);
				Date d1 = formatter.parse(strCurrentTime);
				Date d2 = formatter.parse(strAnnounceTime);
				long diff = d1.getTime() - d2.getTime();// 这样得到的差值是微秒级别

				// 任务或分享发布人的头像
				String strAnnounceImage = "";
				// 获取任务或分享发布人名称
				String strAnnounceName = rs.getString(2);
				// 获取用户头像
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
				// 获取评论条数
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
				// 判断是否到期,如果没有到期
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
			// 先判断要注册的账号是否存在
			String strSQL = "select * from customer_info_table where Account = '";
			strSQL += strPersonName;
			strSQL += "'";
			rs = stmt.executeQuery(strSQL);
			while (rs.next()) {
				// 如果找到了
				bIsFind = true;
				break;
			}
			// 如果找到了用户就进行图片修改
			if (bIsFind) {
				// 更新用户图标
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

	// 执行存储过程
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

			// 获取系统当前时间
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy年MM月dd日HH:mm:ss");
			Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
			String strCurrentTime = formatter.format(curDate);

			while (rs.next()) {
				if (resultMapList == null) {
					resultMapList = new ArrayList<DistanceDetail>();
				}
				// 任务发布时间
				String strAnnounceTime = rs.getString(18);
				Date d1 = formatter.parse(strCurrentTime);
				Date d2 = formatter.parse(strAnnounceTime);
				long diff = d1.getTime() - d2.getTime();// 这样得到的差值是微秒级别

				// 任务或分享发布人的头像
				String strAnnounceImage = "";
				// 获取任务或分享发布人名称
				String strAnnounceName = rs.getString(2);
				// 获取用户头像
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
				// 获取评论条数
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
			// 调用查询附近数据存储过程
			resultMapList = execProcQuery(
					"{CALL pro_test_UpdateHelpNearData(?,?,?,?)}",// 要调用的存储过程
					new Object[] { longitude, latitude, dist, strDistrictName }, // 参数列表
					new String[] { "TaskKey", "distance" });// 要获取的值 key
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
			// 调用查询附近数据存储过程
			resultMapList = execProcQuery(
					"{CALL pro_test_UpdateShareNearData(?,?,?,?)}",// 要调用的存储过程
					new Object[] { longitude, latitude, dist, strDistrictName }, // 参数列表
					new String[] { "TaskKey", "distance" });// 要获取的值 key
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
			// 调用查询附近数据存储过程
			resultMapList = execProcQuery(
					"{CALL pro_test_LoadHelpNearData(?,?,?,?,?)}",// 要调用的存储过程
					new Object[] { longitude, latitude, dist, strDistrictName,
							strTime }, // 参数列表
					new String[] { "TaskKey", "distance" });// 要获取的值 key
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
			// 调用查询附近数据存储过程
			resultMapList = execProcQuery(
					"{CALL pro_test_LoadShareNearData(?,?,?,?,?)}",// 要调用的存储过程
					new Object[] { longitude, latitude, dist, strDistrictName,
							strTime }, // 参数列表
					new String[] { "TaskKey", "distance" });// 要获取的值 key
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
			// 将更新标志设为更新状态
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
				nRet = 1; // 看看是否有更新内容
				strSQL = "select * from customer_comment_table where CustomerName = '";
				strSQL += strCurrentAccountName;
				strSQL += "' and TaskCommentType = '2'";
				rs = stmt.executeQuery(strSQL);
				while (rs.next()) {
					strStatusInfo = "你有新消息";
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
			// 先判断表中是否有相应的记录
			String strSQL = "select * from customer_comment_table where TaskKey = '";
			strSQL += strTaskId;
			strSQL += "' and CustomerName = '";
			strSQL += strAnnounceName;
			strSQL += "' and TaskType = '";
			strSQL += strType;
			strSQL += "'";
			rs = stmt.executeQuery(strSQL);
			while (rs.next()) {
				// 获取评论状态
				nCurType = rs.getInt(3);
				bFind = true;
			}
			// 如果找到了,那么就直接更新状态
			if (bFind) {
				// 如果状态不相同就更改状态
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
					// 如果值为2,则要通知相应的用户
					if (2 == nValue) {
						// 将更新标志设为更新状态
						strSQL = "update customer_info_table";
						strSQL += " set UpdateSignal = '1' where Account = '";
						strSQL += strAnnounceName;
						strSQL += "'";
						stmt.executeUpdate(strSQL);
					}
					nRet = 1;
				}
			} else {// 如果没有找到,那么就插入数据
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

				// 如果值为2,则要通知相应的用户
				if (2 == nValue) {
					// 将更新标志设为更新状态
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
			// 评论表名称
			String strCommentTableName = "";
			boolean bFind = false;
			int nTaskId = Integer.parseInt(strTaskId);
			if (strType.equals("1")) {
				// 先根据strTaskId获取到评论表
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
				// 首先到表中查找是不是有评论人关于这个任务的记录
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
					// 如果没有找到,那么就将这条记录插入到表中
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
				// 查找表中是否有接收评论的人记录,如果没有就插入相应记录
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
					// 如果没有找到,那么就将这条记录插入到表中
					if (!bFind) {
						strSQL = "insert into customer_comment_table values('";
						strSQL += nTaskId;
						strSQL += "','";
						strSQL += strReceiveCommentPersonName;
						strSQL += "','2','1')";
						stmt.execute(strSQL);
					} else {// 如果找到了,就更新状态
						strSQL = "update customer_comment_table";
						strSQL += " set TaskCommentType = '2' where TaskKey = '";
						strSQL += strTaskId;
						strSQL += "' and CustomerName = '";
						strSQL += strReceiveCommentPersonName;
						strSQL += "' and TaskType = '1'";
						stmt.executeUpdate(strSQL);
					}
					// 将更新标志设为更新状态
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
				// 获取当前时间
				SimpleDateFormat formatter = new SimpleDateFormat(
						"yyyy年MM月dd日HH:mm:ss");
				Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
				String strTaskAnnounceTime = formatter.format(curDate);

				// 将数据插入到表中
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
				// 先根据strTaskId获取到评论表
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
				// 首先到表中查找是不是有评论人关于这个任务的记录
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
					// 如果没有找到,那么就将这条记录插入到表中
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
				// 查找表中是否有接收评论的人记录,如果没有就插入相应记录
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
					// 如果没有找到,那么就将这条记录插入到表中
					if (!bFind) {
						strSQL = "insert into customer_comment_table values('";
						strSQL += nTaskId;
						strSQL += "','";
						strSQL += strReceiveCommentPersonName;
						strSQL += "','2','2')";
						stmt.execute(strSQL);
					} else {// 如果找到了,就更新状态
						strSQL = "update customer_comment_table";
						strSQL += " set TaskCommentType = '2' where TaskKey = '";
						strSQL += strTaskId;
						strSQL += "' and CustomerName = '";
						strSQL += strReceiveCommentPersonName;
						strSQL += "' and TaskType = '2'";
						stmt.executeUpdate(strSQL);
					}
					// 将更新标志设为更新状态
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
				// 获取当前时间
				SimpleDateFormat formatter = new SimpleDateFormat(
						"yyyy年MM月dd日HH:mm:ss");
				Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
				String strTaskAnnounceTime = formatter.format(curDate);

				// 将数据插入到表中
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
			// 评论表名称
			String strCommentTableName = "";
			// 任务发布人名称
			// String strTaskAnnouncePersonName = "";
			boolean bFind = false;
			int nTaskId = Integer.parseInt(strTaskId);
			if (strType.equals("1")) {
				// 先根据strTaskId获取到评论表
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
				// 首先到表中查找是不是有评论人关于这个任务的记录
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
					// 如果没有找到,那么就将这条记录插入到表中
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
				// 查找表中是否有接收评论的人记录,如果没有就插入相应记录
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
					// 如果没有找到,那么就将这条记录插入到表中
					if (!bFind) {
						strSQL = "insert into customer_comment_table values('";
						strSQL += nTaskId;
						strSQL += "','";
						strSQL += strReceiveCommentPersonName;
						strSQL += "','2','1')";
						stmt.execute(strSQL);
					} else {// 如果找到了,就更新状态
						strSQL = "update customer_comment_table";
						strSQL += " set TaskCommentType = '2' where TaskKey = '";
						strSQL += strTaskId;
						strSQL += "' and CustomerName = '";
						strSQL += strReceiveCommentPersonName;
						strSQL += "' and TaskType = '1'";
						stmt.executeUpdate(strSQL);
					}
					// 将更新标志设为更新状态
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
				// 获取当前时间
				SimpleDateFormat formatter = new SimpleDateFormat(
						"yyyy年MM月dd日HH:mm:ss");
				Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
				String strTaskAnnounceTime = formatter.format(curDate);

				// 将数据插入到表中
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
				// 先根据strTaskId获取到评论表
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
				// 首先到表中查找是不是有评论人关于这个任务的记录
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
					// 如果没有找到,那么就将这条记录插入到表中
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
				// 查找表中是否有接收评论的人记录,如果没有就插入相应记录
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
					// 如果没有找到,那么就将这条记录插入到表中
					if (!bFind) {
						strSQL = "insert into customer_comment_table values('";
						strSQL += nTaskId;
						strSQL += "','";
						strSQL += strReceiveCommentPersonName;
						strSQL += "','2','2')";
						stmt.execute(strSQL);
					} else {// 如果找到了,就更新状态
						strSQL = "update customer_comment_table";
						strSQL += " set TaskCommentType = '2' where TaskKey = '";
						strSQL += strTaskId;
						strSQL += "' and CustomerName = '";
						strSQL += strReceiveCommentPersonName;
						strSQL += "' and TaskType = '2'";
						stmt.executeUpdate(strSQL);
					}
					// 将更新标志设为更新状态
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
				// 获取当前时间
				SimpleDateFormat formatter = new SimpleDateFormat(
						"yyyy年MM月dd日HH:mm:ss");
				Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
				String strTaskAnnounceTime = formatter.format(curDate);

				// 将数据插入到表中
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
			// 评论表名称
			String strCommentTableName = "";
			// 任务发布人名称
			// String strTaskAnnouncePersonName = "";
			int nTaskId = Integer.parseInt(strTaskId);
			boolean bFind = false;
			if (strType.equals("1")) {
				// 先根据strTaskId获取到评论表
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
				// 首先到表中查找是不是有评论人关于这个任务的记录
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
					// 如果没有找到,那么就将这条记录插入到表中
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
				// 查找表中是否有接收评论的人记录,如果没有就插入相应记录
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
					// 如果没有找到,那么就将这条记录插入到表中
					if (!bFind) {
						strSQL = "insert into customer_comment_table values('";
						strSQL += nTaskId;
						strSQL += "','";
						strSQL += strReceiveCommentPersonName;
						strSQL += "','2','1')";
						stmt.execute(strSQL);
					} else {// 如果找到了,就更新状态
						strSQL = "update customer_comment_table";
						strSQL += " set TaskCommentType = '2' where TaskKey = '";
						strSQL += strTaskId;
						strSQL += "' and CustomerName = '";
						strSQL += strReceiveCommentPersonName;
						strSQL += "' and TaskType = '1'";
						stmt.executeUpdate(strSQL);
					}
					// 将更新标志设为更新状态
					strSQL = "update customer_info_table";
					strSQL += " set UpdateSignal = '1' where Account = '";
					strSQL += strReceiveCommentPersonName;
					strSQL += "'";
					stmt.executeUpdate(strSQL);
				}
				// 获取当前时间
				SimpleDateFormat formatter = new SimpleDateFormat(
						"yyyy年MM月dd日HH:mm:ss");
				Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
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
				// 将数据插入到表中
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
				// 先根据strTaskId获取到评论表
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
				// 先根据strTaskId获取到评论表
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
				// 首先到表中查找是不是有评论人关于这个任务的记录
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
					// 如果没有找到,那么就将这条记录插入到表中
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
				// 查找表中是否有接收评论的人记录,如果没有就插入相应记录
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
					// 如果没有找到,那么就将这条记录插入到表中
					if (!bFind) {
						strSQL = "insert into customer_comment_table values('";
						strSQL += nTaskId;
						strSQL += "','";
						strSQL += strReceiveCommentPersonName;
						strSQL += "','2','2')";
						stmt.execute(strSQL);
					} else {// 如果找到了,就更新状态
						strSQL = "update customer_comment_table";
						strSQL += " set TaskCommentType = '2' where TaskKey = '";
						strSQL += strTaskId;
						strSQL += "' and CustomerName = '";
						strSQL += strReceiveCommentPersonName;
						strSQL += "' and TaskType = '2'";
						stmt.executeUpdate(strSQL);
					}
					// 将更新标志设为更新状态
					strSQL = "update customer_info_table";
					strSQL += " set UpdateSignal = '1' where Account = '";
					strSQL += strReceiveCommentPersonName;
					strSQL += "'";
					stmt.executeUpdate(strSQL);
				}
				// 获取当前时间
				SimpleDateFormat formatter = new SimpleDateFormat(
						"yyyy年MM月dd日HH:mm:ss");
				Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
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
				// 将数据插入到表中
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
			// 先判断之前是不是点过赞
			String strSQL = "select * from customer_praise_table where TaskKey = '";
			strSQL += strTaskId;
			strSQL += "' and PraisePersonName = '";
			strSQL += strPersonName;
			strSQL += "' and TaskType = '";
			strSQL += strType;
			strSQL += "'";
			rs = stmt.executeQuery(strSQL);
			// 如果存在
			while (rs.next()) {
				nRet = 2;
				break;
			}
			if (rs != null) {
				rs.close();
				rs = null;
			}
			// 如果没有找到
			if (nRet != 2) {
				int nSumCreditValue = 0;
				// 首先将赞值加1
				// 先取出赞值
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
				// 将更新标志设为更新状态
				strSQL = "update customer_info_table";
				strSQL += " set CharmValue = '";
				strSQL += nSumCreditValue;
				strSQL += "' where Account = '";
				strSQL += strReceivePersonName;
				strSQL += "'";
				stmt.executeUpdate(strSQL);
				// 更新任务和分享中的赞值
				if (strType.equals("1")) {
					int nCharmValue = 0;
					// 先获取该任务的赞值
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
					// 先获取该任务的赞值
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
				// 保存点赞记录
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
			// 先获取浏览次数
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
				// 如果找到了
				if (nBrowseTime > 0) {
					// 更新浏览次数
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
				// 如果找到了
				if (nBrowseTime > 0) {
					// 更新浏览次数
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
			// 任务类型
			int nTaskType = 0;
			// 任务id号
			int nTaskId = 0;
			// 消息状态
			int nMessageStatus = 0;
			// 获取新消息的条数
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

			// 根据表customer_comment_table获取与指定用户相关的数据
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
					// 消息状态
					int nStatus = nMessageStatus;
					String strName = "";
					// 获取系统当前时间
					SimpleDateFormat formatter = new SimpleDateFormat(
							"yyyy年MM月dd日HH:mm:ss");
					Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
					String strCurrentTime = formatter.format(curDate);

					strSQL = "select * from task_help_opera_table where TaskKey = '";
					strSQL += nTaskId;
					strSQL += "'";
					rs1 = stmt1.executeQuery(strSQL);
					while (rs1.next()) {
						// 任务id号
						int nid = rs1.getInt(1);
						// 如果两者的id不相同
						if (nTaskId != nid) {
							nStatus = 1;
						}
						strName = rs1.getString(2);

						// 任务或分享发布人的头像
						String strAnnounceImage = "";
						// 获取用户头像
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
						// 获取评论条数
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

						// 任务发布时间
						String strAnnounceTime = rs1.getString(18);
						Date d1 = formatter.parse(strCurrentTime);
						Date d2 = formatter.parse(strAnnounceTime);
						long diff = d1.getTime() - d2.getTime();// 这样得到的差值是微秒级别

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
					// 消息状态
					int nStatus = nMessageStatus;
					String strName = "";
					// 获取系统当前时间
					SimpleDateFormat formatter = new SimpleDateFormat(
							"yyyy年MM月dd日HH:mm:ss");
					Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
					String strCurrentTime = formatter.format(curDate);

					strSQL = "select * from task_share_opera_table where TaskKey = '";
					strSQL += nTaskId;
					strSQL += "'";
					rs1 = stmt1.executeQuery(strSQL);
					while (rs1.next()) {
						// 任务id号
						int nid = rs1.getInt(1);
						// 如果两者的id不相同,说明这个分享是由当前用户发布的
						if (nTaskId != nid) {
							nStatus = 1;
						}
						strName = rs1.getString(2);

						// 任务或分享发布人的头像
						String strAnnounceImage = "";
						// 获取用户头像
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
						// 获取评论条数
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
						// 任务发布时间
						String strAnnounceTime = rs1.getString(18);
						Date d1 = formatter.parse(strCurrentTime);
						Date d2 = formatter.parse(strAnnounceTime);
						long diff = d1.getTime() - d2.getTime();// 这样得到的差值是微秒级别

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
		int nRetType = 0;// 为0表示失败
		Connection connect = null;
		Statement stmt = null;
		ResultSet rs = null;
		int nTaskCount = 0;
		try {
			connect = GetConnection.getSimpleConnection();
			stmt = connect.createStatement();
			String strSQL = "";
			// 先判断要注册的账号是否存在
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
				// 如果该任务为求助类型
				if (strTaskType.equals("1")) {
					// 先获取任务数量
					strSQL = "select * from task_help_opera_table";
					rs = stmt.executeQuery(strSQL);
					while (rs.next()) {
						rs.last();
						nTaskCount = rs.getRow();
						break;
					}
					nTaskCount += 1;
					String strTaskCount = nTaskCount + "";
					// 将新的任务插入到数据库中
					SimpleDateFormat formatter = new SimpleDateFormat(
							"yyyy年MM月dd日HH:mm:ss");
					Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
					strTaskAnnounceTime = formatter.format(curDate);
					int nTaskType = Integer.parseInt(strTaskType);
					String strCommentTableName = strTaskCount + "_h1";
					// 创建评论表
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
					// 将相应的数据插入到任务操作表中
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
					// 插入缩小的图片
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

					// 插入相应记录
					strSQL = "insert into customer_comment_table values('";
					strSQL += nTaskCount;
					strSQL += "','";
					strSQL += strTaskAccountTrueName;
					strSQL += "','1','1')";
					stmt.execute(strSQL);
					// 将更新标志设为更新状态,通知所有用户
					strSQL = "update customer_info_table";
					strSQL += " set UpdateSignal = '1'";
					stmt.executeUpdate(strSQL);

					nRetType = nTaskCount;
				} else if (strTaskType.equals("2")) {// 如果该任务为分享
					// 先获取任务数量
					strSQL = "select * from task_share_opera_table";
					rs = stmt.executeQuery(strSQL);
					while (rs.next()) {
						rs.last();
						nTaskCount = rs.getRow();
						break;
					}
					nTaskCount += 1;
					String strTaskCount = nTaskCount + "";
					// 将新的任务插入到数据库中
					SimpleDateFormat formatter = new SimpleDateFormat(
							"yyyy年MM月dd日HH:mm:ss");
					Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
					strTaskAnnounceTime = formatter.format(curDate);
					int nTaskType = Integer.parseInt(strTaskType);
					String strCommentTableName = strTaskCount + "_s1";
					// 创建评论表
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
					// 将相应的数据插入到任务操作表中
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
					// 插入缩小的图片
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

					// 插入相应记录
					strSQL = "insert into customer_comment_table values('";
					strSQL += nTaskCount;
					strSQL += "','";
					strSQL += strTaskAccountTrueName;
					strSQL += "','1','2')";
					stmt.execute(strSQL);
					// 将更新标志设为更新状态,通知所有用户
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
			// 如果为求助
			if (strType.equals("1")) {
				// 修改求助中的描述信息
				String strSQL = "update task_help_opera_table set TaskDeail = '";
				strSQL += strText;
				strSQL += "' where TaskKey = '";
				strSQL += nId;
				strSQL += "'";
				stmt.executeUpdate(strSQL);
				nRet = 1;
			} else if (strType.equals("2")) {// 如果为分享
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
			// 如果为求助
			if (strType.equals("1")) {
				// 修改求助中的描述信息
				String strSQL = "update task_help_opera_table set TaskTitle = '";
				strSQL += strText;
				strSQL += "' where TaskKey = '";
				strSQL += nId;
				strSQL += "'";
				stmt.executeUpdate(strSQL);
				nRet = 1;
			} else if (strType.equals("2")) {// 如果为分享
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
			// 发布人
			String strAccountName = "";
			// 评论人的名称
			String strCommentPersonName;
			// 被评论人的名称
			String strCommentReceivePersonName;
			// 评论时间
			String strCommentTime;
			// 评论内容
			String strCommentContent;
			// 压缩图片
			String strSmallImage;
			// 评论号
			int nCommentIndex;
			// 评论人的名称
			String strCommentPersonTrueName;
			// 被评论人的名称
			String strCommentReceivePersonTrueName;
			// 评论内容类型(是否是悄悄话)
			int nCommentType;

			int nTaskId = Integer.parseInt(strTaskId);
			String strCommentTableName = "";
			if (strType.equals("1")) {
				// 根据任务id获取到评论表名称
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
				// 根据获取到的评论表名称获取到相应数据
				strSQL = "select * from ";
				strSQL += strCommentTableName;
				strSQL += " where TaskTalkTime >= '";
				strSQL += strTime;
				strSQL += "' ORDER BY TaskTalkTime asc";
				rs = stmt.executeQuery(strSQL);
				while (rs.next()) {
					strCommentPersonTrueName = rs.getString(10);
					strCommentReceivePersonTrueName = rs.getString(11);
					// 评论类型(是否是悄悄话)
					nCommentType = rs.getInt(12);
					// 如果当前用户不是这条评论的评论者和接收者以及当前用户不是发布人,同时这条评论是悄悄话,那么就不存储相应的内容
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
					// 评论人的头像
					String strAnnounceImage = "";
					// 评论人名称
					String strAnnounceName = rs.getString(10);
					// 获取用户头像
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
				// 根据任务id获取到评论表名称
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
				// 根据获取到的评论表名称获取到相应数据
				strSQL = "select * from ";
				strSQL += strCommentTableName;
				strSQL += " where TaskTalkTime >= '";
				strSQL += strTime;
				strSQL += "' ORDER BY TaskTalkTime asc";

				rs = stmt.executeQuery(strSQL);
				while (rs.next()) {
					strCommentPersonTrueName = rs.getString(10);
					strCommentReceivePersonTrueName = rs.getString(11);
					// 评论类型(是否是悄悄话)
					nCommentType = rs.getInt(12);
					// 如果当前用户不是这条评论的评论者和接收者以及当前用户不是发布人,同时这条评论是悄悄话,那么就不存储相应的内容
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

					// 任务或分享发布人的头像
					String strAnnounceImage = "";
					// 获取任务或分享发布人名称
					String strAnnounceName = rs.getString(10);
					// 获取用户头像
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
			// 评论表名称
			String strCommentTableName = "";
			// 任务发布人名称
			// String strTaskAnnouncePersonName = "";
			int nTaskId = Integer.parseInt(strTaskId);
			boolean bFind = false;
			if (strType.equals("1")) {
				// 先根据strTaskId获取到评论表
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
				// 首先到表中查找是不是有评论人关于这个任务的记录
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
					// 如果没有找到,那么就将这条记录插入到表中
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
				// 查找表中是否有接收评论的人记录,如果没有就插入相应记录
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
					// 如果没有找到,那么就将这条记录插入到表中
					if (!bFind) {
						strSQL = "insert into customer_comment_table values('";
						strSQL += nTaskId;
						strSQL += "','";
						strSQL += strReceiveCommentPersonName;
						strSQL += "','2','1')";
						stmt.execute(strSQL);
					} else {// 如果找到了,就更新状态
						strSQL = "update customer_comment_table";
						strSQL += " set TaskCommentType = '2' where TaskKey = '";
						strSQL += strTaskId;
						strSQL += "' and CustomerName = '";
						strSQL += strReceiveCommentPersonName;
						strSQL += "' and TaskType = '1'";
						stmt.executeUpdate(strSQL);
					}
					// 将更新标志设为更新状态
					strSQL = "update customer_info_table";
					strSQL += " set UpdateSignal = '1' where Account = '";
					strSQL += strReceiveCommentPersonName;
					strSQL += "'";
					stmt.executeUpdate(strSQL);
				}
				// 获取当前时间
				SimpleDateFormat formatter = new SimpleDateFormat(
						"yyyy年MM月dd日HH:mm:ss");
				Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
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
				// 将数据插入到表中
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
				// 先根据strTaskId获取到评论表
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
				// 先根据strTaskId获取到评论表
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
				// 首先到表中查找是不是有评论人关于这个任务的记录
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
					// 如果没有找到,那么就将这条记录插入到表中
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
				// 查找表中是否有接收评论的人记录,如果没有就插入相应记录
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
					// 如果没有找到,那么就将这条记录插入到表中
					if (!bFind) {
						strSQL = "insert into customer_comment_table values('";
						strSQL += nTaskId;
						strSQL += "','";
						strSQL += strReceiveCommentPersonName;
						strSQL += "','2','2')";
						stmt.execute(strSQL);
					} else {// 如果找到了,就更新状态
						strSQL = "update customer_comment_table";
						strSQL += " set TaskCommentType = '2' where TaskKey = '";
						strSQL += strTaskId;
						strSQL += "' and CustomerName = '";
						strSQL += strReceiveCommentPersonName;
						strSQL += "' and TaskType = '2'";
						stmt.executeUpdate(strSQL);
					}
					// 将更新标志设为更新状态
					strSQL = "update customer_info_table";
					strSQL += " set UpdateSignal = '1' where Account = '";
					strSQL += strReceiveCommentPersonName;
					strSQL += "'";
					stmt.executeUpdate(strSQL);
				}
				// 获取当前时间
				SimpleDateFormat formatter = new SimpleDateFormat(
						"yyyy年MM月dd日HH:mm:ss");
				Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
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
				// 将数据插入到表中
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
			// 评论表名称
			String strCommentTableName = "";
			boolean bFind = false;
			int nTaskId = Integer.parseInt(strTaskId);
			if (strType.equals("1")) {
				// 先根据strTaskId获取到评论表
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
				// 首先到表中查找是不是有评论人关于这个任务的记录
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
					// 如果没有找到,那么就将这条记录插入到表中
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
				// 查找表中是否有接收评论的人记录,如果没有就插入相应记录
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
					// 如果没有找到,那么就将这条记录插入到表中
					if (!bFind) {
						strSQL = "insert into customer_comment_table values('";
						strSQL += nTaskId;
						strSQL += "','";
						strSQL += strReceiveCommentPersonName;
						strSQL += "','2','1')";
						stmt.execute(strSQL);
					} else {// 如果找到了,就更新状态
						strSQL = "update customer_comment_table";
						strSQL += " set TaskCommentType = '2' where TaskKey = '";
						strSQL += strTaskId;
						strSQL += "' and CustomerName = '";
						strSQL += strReceiveCommentPersonName;
						strSQL += "' and TaskType = '1'";
						stmt.executeUpdate(strSQL);
					}
					// 将更新标志设为更新状态
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
				// 获取当前时间
				SimpleDateFormat formatter = new SimpleDateFormat(
						"yyyy年MM月dd日HH:mm:ss");
				Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
				String strTaskAnnounceTime = formatter.format(curDate);

				// 将数据插入到表中
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
				// 先根据strTaskId获取到评论表
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
				// 首先到表中查找是不是有评论人关于这个任务的记录
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
					// 如果没有找到,那么就将这条记录插入到表中
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
				// 查找表中是否有接收评论的人记录,如果没有就插入相应记录
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
					// 如果没有找到,那么就将这条记录插入到表中
					if (!bFind) {
						strSQL = "insert into customer_comment_table values('";
						strSQL += nTaskId;
						strSQL += "','";
						strSQL += strReceiveCommentPersonName;
						strSQL += "','2','2')";
						stmt.execute(strSQL);
					} else {// 如果找到了,就更新状态
						strSQL = "update customer_comment_table";
						strSQL += " set TaskCommentType = '2' where TaskKey = '";
						strSQL += strTaskId;
						strSQL += "' and CustomerName = '";
						strSQL += strReceiveCommentPersonName;
						strSQL += "' and TaskType = '2'";
						stmt.executeUpdate(strSQL);
					}
					// 将更新标志设为更新状态
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
				// 获取当前时间
				SimpleDateFormat formatter = new SimpleDateFormat(
						"yyyy年MM月dd日HH:mm:ss");
				Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
				String strTaskAnnounceTime = formatter.format(curDate);

				// 将数据插入到表中
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
			if (strTaskType.equals("1")) {// 求助
				strSQL = "select * from task_help_smallicon where TaskKey = '";
				strSQL += nTaskId;
				strSQL += "'";
				rs = stmt.executeQuery(strSQL);
				while (rs.next()) {
					strImage = rs.getString(2);
					break;
				}
			} else if (strTaskType.equals("2")) {// 分享
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
			if (strTaskType.equals("1")) {// 求助
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
			} else if (strTaskType.equals("2")) {// 分享
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
			// 任务类型
			int nTaskType = 0;
			// 任务id号
			int nTaskId = 0;
			// 消息状态
			int nMessageStatus = 0;
			// 获取新消息的条数
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
					"yyyy年MM月dd日");
			Date sharecurDate = new Date(System.currentTimeMillis());// 获取当前时间
			String strCurrentTime1 = shareformatter1.format(sharecurDate);
			// 查找分享达人信息
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
				// 达人的头像
				String strAnnounceImage = "";
				// 获取用户头像
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
					// 将推送标志设为已推送
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
			// 根据表customer_comment_table获取与指定用户相关的数据
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
					// 消息状态
					int nStatus = nMessageStatus;
					String strName = "";
					// 获取系统当前时间
					SimpleDateFormat formatter = new SimpleDateFormat(
							"yyyy年MM月dd日HH:mm:ss");
					Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
					String strCurrentTime = formatter.format(curDate);

					strSQL = "select * from task_help_opera_table where TaskKey = '";
					strSQL += nTaskId;
					strSQL += "'";
					rs1 = stmt1.executeQuery(strSQL);
					while (rs1.next()) {
						// 任务id号
						int nid = rs1.getInt(1);
						// 如果两者的id不相同
						if (nTaskId != nid) {
							nStatus = 1;
						}
						strName = rs1.getString(2);

						// 任务或分享发布人的头像
						String strAnnounceImage = "";
						// 获取用户头像
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
						// 获取评论条数
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

						// 任务发布时间
						String strAnnounceTime = rs1.getString(18);
						Date d1 = formatter.parse(strCurrentTime);
						Date d2 = formatter.parse(strAnnounceTime);
						long diff = d1.getTime() - d2.getTime();// 这样得到的差值是微秒级别

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
					// 消息状态
					int nStatus = nMessageStatus;
					String strName = "";
					// 获取系统当前时间
					SimpleDateFormat formatter = new SimpleDateFormat(
							"yyyy年MM月dd日HH:mm:ss");
					Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
					String strCurrentTime = formatter.format(curDate);

					strSQL = "select * from task_share_opera_table where TaskKey = '";
					strSQL += nTaskId;
					strSQL += "'";
					rs1 = stmt1.executeQuery(strSQL);
					while (rs1.next()) {
						// 任务id号
						int nid = rs1.getInt(1);
						// 如果两者的id不相同,说明这个分享是由当前用户发布的
						if (nTaskId != nid) {
							nStatus = 1;
						}
						strName = rs1.getString(2);

						// 任务或分享发布人的头像
						String strAnnounceImage = "";
						// 获取用户头像
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
						// 获取评论条数
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
						// 任务发布时间
						String strAnnounceTime = rs1.getString(18);
						Date d1 = formatter.parse(strCurrentTime);
						Date d2 = formatter.parse(strAnnounceTime);
						long diff = d1.getTime() - d2.getTime();// 这样得到的差值是微秒级别

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
			// 将更新标志设为更新状态
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
			// 获取系统当前时间
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy年MM月dd日HH:mm:ss");
			Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
			String strCurrentTime = formatter.format(curDate);

			SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy年MM月dd日");
			curDate = new Date(System.currentTimeMillis());// 获取当前时间
			String strCurrentTime1 = formatter1.format(curDate);
			String strEndTime = strCurrentTime1 + strExpertPersonTimeEnd;

			Date d1 = formatter.parse(strCurrentTime);
			Date d2 = formatter.parse(strEndTime);
			long diff = d1.getTime() - d2.getTime();// 这样得到的差值是微秒级别
			// 判断当前时间是否到了推送达人时间,只要相差时间不超过1分钟,就认为到了推送时间
			if (diff >= 0 && diff / 1000 <= nLimitTimeSeconds) {

				// 查找分享达人信息
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
				nRet = 1; // 看看是否有更新内容
				strSQL = "select * from customer_comment_table where CustomerName = '";
				strSQL += strCurrentAccountName;
				strSQL += "' and TaskCommentType = '2'";
				rs = stmt.executeQuery(strSQL);
				while (rs.next()) {
					strStatusInfo = "你有新消息";
					break;
				}
				if (1 == nStatusType) {
					strStatusInfo = "你有新消息";
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
			// 任务类型
			int nTaskType = 0;
			// 任务id号
			int nTaskId = 0;
			// 消息状态
			int nMessageStatus = 0;
			// 获取新消息的条数
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

			// 获取id容器
			HashMap<String, Integer> mapid = GetID(strID);

			SimpleDateFormat shareformatter1 = new SimpleDateFormat(
					"yyyy年MM月dd日");
			Date sharecurDate = new Date(System.currentTimeMillis());// 获取当前时间
			String strCurrentTime1 = shareformatter1.format(sharecurDate);
			// 查找分享达人信息
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
				// 达人的头像
				String strAnnounceImage = "";
				// 获取用户头像
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
					// 将推送标志设为已推送
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
			// 根据表customer_comment_table获取与指定用户相关的数据
			strSQL = "select * from customer_comment_table where CustomerName = '";
			strSQL += strAnnounceName;
			strSQL += "'";
			rs = stmt.executeQuery(strSQL);
			rs.afterLast();
			while (rs.previous()) {
				nTaskType = rs.getInt(4);
				nTaskId = rs.getInt(1);
				nMessageStatus = rs.getInt(3);
				// 判断id所指定的数据是否需要返回到客户端
				if (mapid.containsKey("" + nTaskId) && nMessageStatus != 2) {
					// 如果不需要那么就不执行下面的语句
					continue;
				}
				if (1 == nTaskType) {
					// 消息状态
					int nStatus = nMessageStatus;
					String strName = "";
					// 获取系统当前时间
					SimpleDateFormat formatter = new SimpleDateFormat(
							"yyyy年MM月dd日HH:mm:ss");
					Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
					String strCurrentTime = formatter.format(curDate);

					strSQL = "select * from task_help_opera_table where TaskKey = '";
					strSQL += nTaskId;
					strSQL += "'";
					rs1 = stmt1.executeQuery(strSQL);
					while (rs1.next()) {
						// 任务id号
						int nid = rs1.getInt(1);
						// 如果两者的id不相同
						if (nTaskId != nid) {
							nStatus = 1;
						}
						strName = rs1.getString(2);

						// 任务或分享发布人的头像
						String strAnnounceImage = "";
						// 获取用户头像
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
						// 获取评论条数
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

						// 任务发布时间
						String strAnnounceTime = rs1.getString(18);
						Date d1 = formatter.parse(strCurrentTime);
						Date d2 = formatter.parse(strAnnounceTime);
						long diff = d1.getTime() - d2.getTime();// 这样得到的差值是微秒级别

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
					// 消息状态
					int nStatus = nMessageStatus;
					String strName = "";
					// 获取系统当前时间
					SimpleDateFormat formatter = new SimpleDateFormat(
							"yyyy年MM月dd日HH:mm:ss");
					Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
					String strCurrentTime = formatter.format(curDate);

					strSQL = "select * from task_share_opera_table where TaskKey = '";
					strSQL += nTaskId;
					strSQL += "'";
					rs1 = stmt1.executeQuery(strSQL);
					while (rs1.next()) {
						// 任务id号
						int nid = rs1.getInt(1);
						// 如果两者的id不相同,说明这个分享是由当前用户发布的
						if (nTaskId != nid) {
							nStatus = 1;
						}
						strName = rs1.getString(2);

						// 任务或分享发布人的头像
						String strAnnounceImage = "";
						// 获取用户头像
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
						// 获取评论条数
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
						// 任务发布时间
						String strAnnounceTime = rs1.getString(18);
						Date d1 = formatter.parse(strCurrentTime);
						Date d2 = formatter.parse(strAnnounceTime);
						long diff = d1.getTime() - d2.getTime();// 这样得到的差值是微秒级别

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
			// 获取系统当前时间
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy年MM月dd日HH:mm:ss");
			Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
			String strCurrentTime = formatter.format(curDate);

			while (rs.next()) {
				// 任务发布时间
				String strAnnounceTime = rs.getString(18);
				Date d1 = formatter.parse(strCurrentTime);
				Date d2 = formatter.parse(strAnnounceTime);
				long diff = d1.getTime() - d2.getTime();// 这样得到的差值是微秒级别

				// 任务或分享发布人的头像
				String strAnnounceImage = "";
				// 获取任务或分享发布人名称
				String strAnnounceName = rs.getString(2);
				// 获取用户头像
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
				// 获取评论条数
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
				// 判断是否到期,如果没有到期
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
			// 获取系统当前时间
			SimpleDateFormat formatter = new SimpleDateFormat(
					"yyyy年MM月dd日HH:mm:ss");
			Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
			String strCurrentTime = formatter.format(curDate);

			while (rs.next()) {
				// 任务发布时间
				String strAnnounceTime = rs.getString(18);
				Date d1 = formatter.parse(strCurrentTime);
				Date d2 = formatter.parse(strAnnounceTime);
				long diff = d1.getTime() - d2.getTime();// 这样得到的差值是微秒级别

				// 任务或分享发布人的头像
				String strAnnounceImage = "";
				// 获取任务或分享发布人名称
				String strAnnounceName = rs.getString(2);
				// 获取用户头像
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
				// 获取评论条数
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
				// 判断是否到期,如果没有到期
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
