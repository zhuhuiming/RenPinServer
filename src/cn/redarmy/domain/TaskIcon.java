package cn.redarmy.domain;

import java.io.Serializable;

public class TaskIcon implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TaskIcon() {
	}

	public TaskIcon(String strIcon1, String strIcon2, String strIcon3,
			String strIcon4, String strIcon5, String strIcon6) {
		this.strIcon1 = strIcon1;
		this.strIcon2 = strIcon2;
		this.strIcon3 = strIcon3;
		this.strIcon4 = strIcon4;
		this.strIcon5 = strIcon5;
		this.strIcon6 = strIcon6;
	}

	public String getstrIcon1() {
		return strIcon1;
	}

	public String getstrIcon2() {
		return strIcon2;
	}

	public String getstrIcon3() {
		return strIcon3;
	}

	public String getstrIcon4() {
		return strIcon4;
	}

	public String getstrIcon5() {
		return strIcon5;
	}

	public String getstrIcon6() {
		return strIcon6;
	}

	String strIcon1;// ��һ��ͼƬ
	String strIcon2;// �ڶ���ͼƬ
	String strIcon3;// ������ͼƬ
	String strIcon4;// ������ͼƬ
	String strIcon5;// ������ͼƬ
	String strIcon6;// ������ͼƬ
}
