package cn.redarmy.action;

public class Message {
	
	private int vsersionCode;
	private String apkUrl;
	public int getVsersionCode() {
		return vsersionCode;
	}
	public void setVsersionCode(int vsersionCode) {
		this.vsersionCode = vsersionCode;
	}
	public String getApkUrl() {
		return apkUrl;
	}
	public void setApkUrl(String apkUrl) {
		this.apkUrl = apkUrl;
	}
	@Override
	public String toString() {
		return "Message [vsersionCode=" + vsersionCode + ", apkUrl=" + apkUrl
				+ "]";
	}
	

}