package cn.redarmy.domain;

import java.io.Serializable;

public class CreditAndCharmForTask implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CreditAndCharmForTask() {
	}
	
	public CreditAndCharmForTask(int nCreditValue,int nCharmValue){
		this.nCreditValue = nCreditValue;
		this.nCharmValue = nCharmValue;
	}
	
	public int getnCreditValue(){
		return nCreditValue;
	}
	
	public int getnCharmValue(){
		return nCharmValue;
	}
	
	//��Ʒֵ
	int nCreditValue;
	//��ֵ
	int nCharmValue;
}
