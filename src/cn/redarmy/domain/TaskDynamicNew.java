package cn.redarmy.domain;

import java.io.Serializable;

public class TaskDynamicNew implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TaskDynamicNew() {
	}

	public TaskDynamicNew(String strTaskKey, String strTaskAccountName,
			String strTaskImplementAccountName, int nTaskSelectType,
			int nTaskFinishType, int nTaskVerifiType,
			int nTaskAnnounceCommentType, int nTaskImplementCommentType,
			int nDynamicNewsNum, String TaskAccountCommentContent,
			String TaskAccountImage, String TaskImplementCommentContent,
			String TaskImplementImage) {
		this.strTaskKey = strTaskKey;
		this.strTaskAccountName = strTaskAccountName;
		this.strTaskImplementAccountName = strTaskImplementAccountName;
		this.nTaskSelectType = nTaskSelectType;
		this.nTaskFinishType = nTaskFinishType;
		this.nTaskVerifiType = nTaskVerifiType;
		this.nTaskAnnounceCommentType = nTaskAnnounceCommentType;
		this.nTaskImplementCommentType = nTaskImplementCommentType;
		this.nDynamicNewsNum = nDynamicNewsNum;
		this.strTaskAccountCommentContent = TaskAccountCommentContent;
		this.strTaskAccountImage = TaskAccountImage;
		this.strTaskImplementCommentContent = TaskImplementCommentContent;
		this.strTaskImplementImage = TaskImplementImage;
	}

	public String getstrTaskKey() {
		return strTaskKey;
	}

	public String getstrTaskAccountName() {
		return strTaskAccountName;
	}

	public String getstrTaskImplementAccountName() {
		return strTaskImplementAccountName;
	}

	public int getnTaskSelectType() {
		return nTaskSelectType;
	}
	
	public void SetnTaskSelectType(int nType) {
		nTaskSelectType = nType;
	}

	public int getnTaskFinishType() {
		return nTaskFinishType;
	}
	
	public void SetnTaskFinishType(int nType) {
		nTaskFinishType = nType;
	}

	public int getnTaskVerifiType() {
		return nTaskVerifiType;
	}
	
	public void SetnTaskVerifiType(int nType) {
		nTaskVerifiType = nType;
	}

	public int getnTaskAnnounceCommentType() {
		return nTaskAnnounceCommentType;
	}
	
	public void SetnTaskAnnounceCommentType(int nType) {
		nTaskAnnounceCommentType = nType;
	}

	public int getnTaskImplementCommentType() {
		return nTaskImplementCommentType;
	}
	
	public void SetnTaskImplementCommentType(int nType) {
		nTaskImplementCommentType = nType;
	}

	public int getnDynamicNewsNum() {
		return nDynamicNewsNum;
	}

	public String getstrTaskAccountCommentContent() {
		return strTaskAccountCommentContent;
	}

	public String getstrTaskAccountImage() {
		return strTaskAccountImage;
	}

	public String getstrTaskImplementCommentContent() {
		return strTaskImplementCommentContent;
	}

	public String getstrTaskImplementImage() {
		return strTaskImplementImage;
	}

	String strTaskKey;// 任务id号
	String strTaskAccountName;// 任务发布人
	String strTaskImplementAccountName;// 任务执行人
	int nTaskSelectType;// 任务处理类型,1表示没有人接收,2表示有人接收,3表示任务已经接收同时任务发布者已经查看了这个接收
	int nTaskFinishType;// 任务完成状态,1表示任务没有完成,2表示任务完成,3表示任务完成同时发布者已查看了这个状态
	int nTaskVerifiType;// 任务验收状态,1表示没有验收,2表示验收了,3表示验收了同时任务执行者已经查看该状态
	int nTaskAnnounceCommentType;// 发布者最新评论,1表示没有最新评论,2表示有最新评论,3表示有最新评论同时任务执行者已查看
	int nTaskImplementCommentType;// 执行者最新评论，1表示没有最新评论,2表示有最新评论,3表示有最新评论同时任务发布者已查看
	int nDynamicNewsNum;// 动态消息条数
	String strTaskAccountCommentContent;// 任务发布者给执行者的评论
	String strTaskAccountImage;// 任务发布者给执行者的图片
	String strTaskImplementCommentContent;// 任务执行者给发布者的评论
	String strTaskImplementImage;// 任务执行者给发布者的图片
}
