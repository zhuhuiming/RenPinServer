package cn.redarmy.domain;

import java.io.Serializable;

public class UpdateData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UpdateData() {
	}

	public UpdateData(int nUpdateSignal,String strUpdateDescribe) {
		this.nUpdateSignal = nUpdateSignal;
		this.strUpdateDescribe = strUpdateDescribe;
	}

	public int getnUpdateSignal() {
		return nUpdateSignal;
	}
	
	public String getstrUpdateDescribe() {
		return strUpdateDescribe;
	}
	
	public void setnUpdateSignal(int nValue) {
		nUpdateSignal = nValue;
	}
	
	public void setstrUpdateDescribe(String strDes) {
		strUpdateDescribe = strDes;
	}
    
	//״̬���±�־0��ʾû�и���,1��ʾ�и���
	int nUpdateSignal;
	//״̬������������
	String strUpdateDescribe;
}
