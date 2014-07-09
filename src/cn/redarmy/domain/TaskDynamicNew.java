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

	String strTaskKey;// ����id��
	String strTaskAccountName;// ���񷢲���
	String strTaskImplementAccountName;// ����ִ����
	int nTaskSelectType;// ����������,1��ʾû���˽���,2��ʾ���˽���,3��ʾ�����Ѿ�����ͬʱ���񷢲����Ѿ��鿴���������
	int nTaskFinishType;// �������״̬,1��ʾ����û�����,2��ʾ�������,3��ʾ�������ͬʱ�������Ѳ鿴�����״̬
	int nTaskVerifiType;// ��������״̬,1��ʾû������,2��ʾ������,3��ʾ������ͬʱ����ִ�����Ѿ��鿴��״̬
	int nTaskAnnounceCommentType;// ��������������,1��ʾû����������,2��ʾ����������,3��ʾ����������ͬʱ����ִ�����Ѳ鿴
	int nTaskImplementCommentType;// ִ�����������ۣ�1��ʾû����������,2��ʾ����������,3��ʾ����������ͬʱ���񷢲����Ѳ鿴
	int nDynamicNewsNum;// ��̬��Ϣ����
	String strTaskAccountCommentContent;// ���񷢲��߸�ִ���ߵ�����
	String strTaskAccountImage;// ���񷢲��߸�ִ���ߵ�ͼƬ
	String strTaskImplementCommentContent;// ����ִ���߸������ߵ�����
	String strTaskImplementImage;// ����ִ���߸������ߵ�ͼƬ
}
