package cn.redarmy.domain;

import java.io.Serializable;

public class CustomerInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public CustomerInfo() {
	}
	
	public CustomerInfo(String sname1,
			            String sname2,
			            int iname1,
			            int iname2,
			            String sname3,
			            int nCreditValue,
			            int nCharmValue,
			            String strCustomerName,
			            String strNickName){
		sex = sname1;
		detail = sname2;
		age = iname1;
		score = iname2;
		strIcon = sname3;
		this.nCreditValue = nCreditValue;
		this.nCharmValue = nCharmValue;
		this.strCustomerName = strCustomerName;
		this.strNickName = strNickName;
	}
	
	public String getsex(){
		return sex;
	}
	
	public String getdetail(){
		return detail;
	}
	
	public int getage(){
		return age;
	}
	
	public int getscore(){
		return score;
	}
	
	public String getstrIcon(){
		return strIcon;
	}
	
	public int getnCreditValue(){
		return nCreditValue;
	}
	
	public int getnCharmValue(){
		return nCharmValue;
	}
	
	public String getstrCustomerName(){
		return strCustomerName;
	}
	
	public String getstrNickName(){
		return strNickName;
	}
	
	//�Ա�
	private String sex;
	//��ϸ��Ϣ
	private String detail; 
	//����
	private int age;
	//����
	private int score;
	//�û�ͼ��
	private String strIcon;
	//�û���Ʒֵ
	private int nCreditValue;
	//�û���ֵ
	private int nCharmValue;
	//�û�����
	private String strCustomerName;
	//�û��ǳ�
	private String strNickName;
}