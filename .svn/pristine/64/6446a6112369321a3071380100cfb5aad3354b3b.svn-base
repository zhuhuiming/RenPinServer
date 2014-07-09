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
    
	//状态更新标志0表示没有更新,1表示有更新
	int nUpdateSignal;
	//状态更新描述文字
	String strUpdateDescribe;
}
