package cn.redarmy.domain;

import java.io.Serializable;

public class OtherTaskInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OtherTaskInfo() {
	}

	public OtherTaskInfo(String strRegion, String strIcon, String strPersonName,
			String strTitle, String strId, double dLongitude, double dLatidude,
			String strTaskAnnouceTime, String strTimeLimit,
			String strTaskDetail, String strRunTime,
			String strTaskImplementName, int nTimeStatus, 
			int nImpleStatus,int nTaskType) {
		mstrTaskRegion = strRegion;
		mTaskAskPersonIcon = strIcon;
		mPersonName = strPersonName;
		mTaskTitle = strTitle;
		mstrId = strId;
		mdLongitude = dLongitude;
		mdLatidude = dLatidude;
		mTaskAnnounceTime = strTaskAnnouceTime;
		mTimeLimit = strTimeLimit;
		mTaskDetail = strTaskDetail;
		mRunSeconds = strRunTime;
		mTaskImplementName = strTaskImplementName;
		mnValiableStatus = nTimeStatus;
		mnImplementStatus = nImpleStatus;
		mnTaskType = nTaskType;
	}

	public String getmstrTaskRegion() {
		return mstrTaskRegion;
	}

	public String getmTaskAskPersonIcon() {
		return mTaskAskPersonIcon;
	}

	public String getmTaskTitle() {
		return mTaskTitle;
	}

	public String getmPersonName() {
		return mPersonName;
	}

	public String getmstrId() {
		return mstrId;
	}

	public double getmdLongitude() {
		return mdLongitude;
	}

	public double getmdLatidude() {
		return mdLatidude;
	}

	public String getmTaskAnnounceTime() {
		return mTaskAnnounceTime;
	}

	public String getmTimeLimit() {
		return mTimeLimit;
	}

	public String getmTaskDetail() {
		return mTaskDetail;
	}

	public String getmRunSeconds() {
		return mRunSeconds;
	}

	public String getmTaskImplementName() {
		return mTaskImplementName;
	}

	public int getmnValiableStatus() {
		return mnValiableStatus;
	}

	public int getmnImplementStatus() {
		return mnImplementStatus;
	}
	
	public int getmnTaskType() {
		return mnTaskType;
	}
	// 任务发布区域
	private String mstrTaskRegion;
	// 发布任务的人头像
	private String mTaskAskPersonIcon;
	// 发布任务人名称
	private String mPersonName;
	// 发布任务的标题
	private String mTaskTitle;
	// 任务id号
	private String mstrId;
	// 任务发布区域的经度
	private double mdLongitude;
	// 任务发布区域的纬度
	private double mdLatidude;
	// 任务发布时间
	private String mTaskAnnounceTime;
	// 时间限制
	private String mTimeLimit;
	// 任务详细描述
	private String mTaskDetail;
	// 任务从发布到现在经过的秒数
	private String mRunSeconds;
	// 任务接收人名称
	private String mTaskImplementName;
	// 任务的有效期状态,1表示处于有效期,2表示任务过期
	private int mnValiableStatus;
	// 任务的执行状态,1表示等待任务接收,2表示任务执行中,3表示任务已完成等待验证,4表示任务验证成功,5表示任务验证失败
	private int mnImplementStatus;
	//任务类型,1表示求助,2表示分享
	private int mnTaskType;
}
