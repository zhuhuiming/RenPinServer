package cn.redarmy.domain;

import java.io.Serializable;

public class CommentInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public CommentInfo() {
	}
	
	public CommentInfo(String strCommentPersonImage,
			           String strCommentPersonName,
			           String strCommentReceivePersonName,
			           String strCommentTime,
			           String strCommentContent,
			           String strSmallImage,
			           int nCommentIndex,
			           String strCommentPersonTrueName,
			           String strCommentReceivePersonTrueName){
		this.strCommentPersonImage = strCommentPersonImage;
		this.strCommentPersonName = strCommentPersonName;
		this.strCommentReceivePersonName = strCommentReceivePersonName;
		this.strCommentTime = strCommentTime;
		this.strCommentContent = strCommentContent;
		this.strSmallImage = strSmallImage;
		this.nCommentIndex = nCommentIndex;
		this.strCommentPersonTrueName = strCommentPersonTrueName;
		this.strCommentReceivePersonTrueName = strCommentReceivePersonTrueName;
	}
	
	public String getstrCommentPersonImage(){
		return strCommentPersonImage;
	}
	
	public String getstrCommentPersonName(){
		return strCommentPersonName;
	}
	
	public String getstrCommentReceivePersonName(){
		return strCommentReceivePersonName;
	}
	
	public String getstrCommentTime(){
		return strCommentTime;
	}
	
	public String getstrCommentContent(){
		return strCommentContent;
	}
	
	public String getstrSmallImage(){
		return strSmallImage;
	}
	
	public int getnCommentIndex(){
		return nCommentIndex;
	}
	
	public String getstrCommentPersonTrueName(){
		return strCommentPersonTrueName;
	}
	
	public String getstrCommentReceivePersonTrueName(){
		return strCommentReceivePersonTrueName;
	}
	//评论人的图片
	String strCommentPersonImage;
	//评论人的名称(昵称)
	String strCommentPersonName;
	//被评论人的名称(昵称)
	String strCommentReceivePersonName;
	//评论时间
	String strCommentTime;
	//评论内容
	String strCommentContent;
	//缩略图片
	String strSmallImage;
	//评论的索引号
	int nCommentIndex;
	//评论人名称
	String strCommentPersonTrueName;
	//被评论人名称
	String strCommentReceivePersonTrueName;
}
