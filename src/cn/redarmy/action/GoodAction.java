package cn.redarmy.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import cn.redarmy.domain.CommentInfo;
import cn.redarmy.domain.CreditAndCharmForTask;
import cn.redarmy.domain.CustomerInfo;
import cn.redarmy.domain.DistanceDetail;
import cn.redarmy.domain.TaskDynamicNew;
import cn.redarmy.domain.TaskIcon;
import cn.redarmy.domain.TaskInfo;
import cn.redarmy.domain.TaskInfoDetail;
import cn.redarmy.domain.UpdateData;
import cn.redarmy.service.GoodService.GoodService;
import cn.redarmy.service.impl.GoodServiceImpl;

import com.opensymphony.xwork2.ActionSupport;

public class GoodAction extends ActionSupport implements ServletRequestAware,
		ServletResponseAware {
	HttpServletRequest request;
	HttpServletResponse response;
	/**  
	 *   
     */
	private static final long serialVersionUID = 6340167538296898588L;

	// 定义业务操作bean
	private GoodService goodService = new GoodServiceImpl();

	// 定义返回的对象(我的)
	private List<TaskInfo> taskinfos;
	// 定义返回的对象(我的)
	private List<TaskInfoDetail> taskinfodetails;
	// 带有人品值与赞值限制的任务或分享数据
	private List<TaskInfoDetail> taskdetailinfos;
	// 返回动态更新
	private List<TaskDynamicNew> taskdynamicnews;
	// 注册返回值
	private int bregeditret;
	// 注册返回值
	private int bregeditretforlargeimage;
	// 添加任务信息返回值
	private int baddtaskret;
	// 登录后返回用户信息
	private CustomerInfo customerinfo;
	// 用户选择任务操作的返回值
	private int nselecttaskret;
	// 用户动态消息条数
	private int nmsgnum;
	// 评论内容
	private List<CommentInfo> commentinfos;
	// 评论内容(悄悄话)
	private List<CommentInfo> newcommentinfos;
	// 发送评论内容操作的返回值
	private int nsendcommentret;
	// 发送评论图片返回的值
	private int nsendcommentimageret;
	// 获取评论大图片
	private String strcommentlargeimage;
	// 获取更新标志的值
	private UpdateData nupdatevalue;
	// 设置更新标志返回的值
	private int nsetupdatevalueret;
	// 设置人品值返回值
	private int nsetcreditvalueret;
	// 设置赞值返回值
	private int nsetcharmvalueret;
	// 获取人品值返回值
	private int ngetcreditvalueret;
	// 获取赞值返回值
	private int ngetcharmvalueret;
	// 获取人品值返回值
	private int naddcreditvalueret;
	// 获取赞值返回值
	private int naddcharmvalueret;
	// 任务执行剩余时间(秒)
	private long ltaskremaintime;
	// 加载数据
	private List<TaskInfo> loadtaskinfos;
	// 加载数据(带有人品值和赞值限制)
	private List<TaskInfoDetail> loaddetailtaskinfos;
	// 加载数据(带有人品值和赞值限制)
	private List<TaskInfoDetail> loaddetailtaskinfosnew;
	// 更新数据
	private List<TaskInfo> updatetaskinfos;
	// 更新数据
	private List<TaskInfoDetail> updatedetailtaskinfos;
	// 更新数据(按时间排序)
	private List<TaskInfoDetail> updatedetailtaskinfosnew;

	// 以下针对发布者更新返回值
	private int announceret1;
	private int announceret2;
	private int announceret3;

	// 上传发布任务或分享大图片返回值
	int nuploadlargeimageret;
	// 上传执行完成或分享完成图片返回值
	int nuploadfinishimageret;
	// 上传验证完成图片返回值
	int nuploadverifiimageret;
	// 返回发布中缩小图片
	TaskIcon mtaskicon;
	// 返回发布中指定的放大图片
	String strtaskicon;
	// 返回求助任务执行者发送给发布者的图片或者分享者发送给接收者的图片
	String strtaskfinishlargeimage;
	// 返回求助任务发布者发送给执行者的图片或者接收者发送给分享者的图片
	String strtaskverifilargeimage;
	// 重置任务的返回值
	int nretsettaskret;
	// 执行记录赞值与人品值变化操作的返回值
	int nrecordvalueret;
	// 返回人品值和赞值
	CreditAndCharmForTask creditandcharm;
	// 发送意见返回值
	int nsendadviceret;

	// 以下针对执行者更新返回值
	private int implementret1;
	private int implementret2;
	// 判断用户是否有效的返回结果
	private int nJudgeValidityRet;
	// 版本号
	private int nversioncode;
	// 版本名称
	private String strversionname;
	// 添加任务信息返回值(带有人品值与赞值限制)
	private int baddtaskforlimitret;
	// 添加任务信息返回值(带有人品值与赞值限制以及城市名称和区名称)
	private int baddtaskforlimitnewret;
	// 修改头像返回值
	private int nchangepersonimageret;
	// 获取周围指定范围内的数据,求助(小于指定距离)
	private List<DistanceDetail> distancehelpdata;
	// 获取周围指定范围内的数据,分享(小于指定距离)
	private List<DistanceDetail> distancesharedata;
	// 获取周围指定范围内的数据,求助(大于指定距离)
	private List<DistanceDetail> distancehelpdata1;
	// 获取周围指定范围内的数据,分享(大于指定距离)
	private List<DistanceDetail> distancesharedata1;
	// 更新评论状态返回值
	private int commentstatusret;
	// 发送评论内容操作的返回值
	private int nsendcommentnewret;
	// 发送评论内容操作的返回值(悄悄话)
	private int nsendcommentnewsecretret;
	// 发送评论内容,时间返回值
	private String strsendcommentnewsecretret;
	// 发送评论图片操作的返回值
	private int nsendcommentimagenewret;
	// 发送评论图片操作返回的时间值
	private String strsendcommentimagenewret;
	// 给赞操作的返回值
	private int nPraiseRet;
	// 添加浏览次数操作返回值
	private int nBrowseRet;
	// 更新项目详细信息返回值
	private int nupdateitemdetailtextRet;
	// 更新项目标题信息返回值
	private int nupdateitemtitletextRet;
	// 返回求助或分享中第一张发布图片
	private String strfirstimage;
	// 返回求助或分享中除了第一张图片的其他图片
	private TaskIcon mothersmallicon;
	// 删除"我的"数据返回值
	private int deletedynamicret;

	public List<TaskInfo> gettaskinfos() {
		return taskinfos;
	}

	public void settaskinfos(List<TaskInfo> taskinfos) {
		this.taskinfos = taskinfos;
	}

	public List<TaskInfoDetail> gettaskdetailinfos() {
		return taskdetailinfos;
	}

	public void settaskdetailinfos(List<TaskInfoDetail> taskinfos) {
		this.taskdetailinfos = taskinfos;
	}

	public int getbregeditret() {
		return bregeditret;
	}

	public void setbregeditret(int nret) {
		this.bregeditret = nret;
	}

	public int getbregeditretforlargeimage() {
		return bregeditretforlargeimage;
	}

	public void setbregeditretforlargeimage(int nret) {
		this.bregeditretforlargeimage = nret;
	}

	public CustomerInfo getcustomerinfo() {
		return customerinfo;
	}

	public void setcustomerinfo(CustomerInfo customer) {
		this.customerinfo = customer;
	}

	public int getbaddtaskret() {
		return baddtaskret;
	}

	public void setbaddtaskret(int nret) {
		this.baddtaskret = nret;
	}

	public int getnselecttaskret() {
		return nselecttaskret;
	}

	public void setnselecttaskret(int nret) {
		this.nselecttaskret = nret;
	}

	public int getnmsgnum() {
		return nmsgnum;
	}

	public void setnmsgnum(int nret) {
		this.nmsgnum = nret;
	}

	public int getannounceret1() {
		return announceret1;
	}

	public void setannounceret1(int nret) {
		this.announceret1 = nret;
	}

	public int getannounceret2() {
		return announceret2;
	}

	public void setannounceret2(int nret) {
		this.announceret2 = nret;
	}

	public int getannounceret3() {
		return announceret3;
	}

	public void setannounceret3(int nret) {
		this.announceret3 = nret;
	}

	public int getimplementret1() {
		return implementret1;
	}

	public void setimplementret1(int nret) {
		this.implementret1 = nret;
	}

	public int getimplementret2() {
		return implementret2;
	}

	public void setimplementret2(int nret) {
		this.implementret2 = nret;
	}

	public List<TaskDynamicNew> gettaskdynamicnews() {
		return taskdynamicnews;
	}

	public void settaskdynamicnews(List<TaskDynamicNew> nret) {
		this.taskdynamicnews = nret;
	}

	public List<CommentInfo> getcommentinfos() {
		return commentinfos;
	}

	public void setcommentinfos(List<CommentInfo> comments) {
		this.commentinfos = comments;
	}

	public List<CommentInfo> getnewcommentinfos() {
		return newcommentinfos;
	}

	public void setnewcommentinfos(List<CommentInfo> comments) {
		this.newcommentinfos = comments;
	}

	public int getnsendcommentret() {
		return nsendcommentret;
	}

	public void setnsendcommentret(int nCommentRet) {
		this.nsendcommentret = nCommentRet;
	}

	public UpdateData getnupdatevalue() {
		return nupdatevalue;
	}

	public void setnupdatevalue(UpdateData nValue) {
		this.nupdatevalue = nValue;
	}

	public int getnsetupdatevalueret() {
		return nsetupdatevalueret;
	}

	public void setnsetupdatevalueret(int nValue) {
		this.nsetupdatevalueret = nValue;
	}

	public int getnsetcreditvalueret() {
		return nsetcreditvalueret;
	}

	public void setnsetcreditvalueret(int nValue) {
		this.nsetcreditvalueret = nValue;
	}

	public int getnsetcharmvalueret() {
		return nsetcharmvalueret;
	}

	public void setnsetcharmvalueret(int nValue) {
		this.nsetcharmvalueret = nValue;
	}

	public int getngetcharmvalueret() {
		return ngetcharmvalueret;
	}

	public void setngetcharmvalueret(int nValue) {
		this.ngetcharmvalueret = nValue;
	}

	public int getngetcreditvalueret() {
		return ngetcreditvalueret;
	}

	public void setngetcreditvalueret(int nValue) {
		this.ngetcreditvalueret = nValue;
	}

	public int getnaddcreditvalueret() {
		return naddcreditvalueret;
	}

	public void setnaddcreditvalueret(int nValue) {
		this.naddcreditvalueret = nValue;
	}

	public int getnaddcharmvalueret() {
		return naddcharmvalueret;
	}

	public void setnaddcharmvalueret(int nValue) {
		this.naddcharmvalueret = nValue;
	}

	public long getltaskremaintime() {
		return ltaskremaintime;
	}

	public void setltaskremaintime(long lValue) {
		this.ltaskremaintime = lValue;
	}

	public List<TaskInfo> getloadtaskinfos() {
		return loadtaskinfos;
	}

	public void setloadtaskinfos(List<TaskInfo> tasks) {
		this.loadtaskinfos = tasks;
	}

	public List<TaskInfoDetail> getloaddetailtaskinfos() {
		return loaddetailtaskinfos;
	}

	public void setloaddetailtaskinfos(List<TaskInfoDetail> tasks) {
		this.loaddetailtaskinfos = tasks;
	}
	
	public List<TaskInfoDetail> getloaddetailtaskinfosnew() {
		return loaddetailtaskinfosnew;
	}

	public void setloaddetailtaskinfosnew(List<TaskInfoDetail> tasks) {
		this.loaddetailtaskinfosnew = tasks;
	}

	public List<TaskInfo> getupdatetaskinfos() {
		return updatetaskinfos;
	}

	public void setupdatetaskinfos(List<TaskInfo> tasks) {
		this.updatetaskinfos = tasks;
	}

	public List<TaskInfoDetail> getupdatedetailtaskinfos() {
		return updatedetailtaskinfos;
	}

	public void setupdatedetailtaskinfosnew(List<TaskInfoDetail> tasks) {
		this.updatedetailtaskinfosnew = tasks;
	}

	public List<TaskInfoDetail> getupdatedetailtaskinfosnew() {
		return updatedetailtaskinfosnew;
	}

	public void setupdatedetailtaskinfos(List<TaskInfoDetail> tasks) {
		this.updatedetailtaskinfos = tasks;
	}

	public int getnuploadlargeimageret() {
		return nuploadlargeimageret;
	}

	public void setnuploadlargeimageret(int nValue) {
		this.nuploadlargeimageret = nValue;
	}

	public int getnuploadfinishimageret() {
		return nuploadfinishimageret;
	}

	public void setnuploadfinishimageret(int nValue) {
		this.nuploadfinishimageret = nValue;
	}

	public int getnuploadverifiimageret() {
		return nuploadverifiimageret;
	}

	public void setnuploadverifiimageret(int nValue) {
		this.nuploadverifiimageret = nValue;
	}

	public TaskIcon getmtaskicon() {
		return mtaskicon;
	}

	public void setmtaskicon(TaskIcon taskicon) {
		this.mtaskicon = taskicon;
	}

	public String getstrtaskicon() {
		return strtaskicon;
	}

	public void setstrtaskicon(String strtaskicon) {
		this.strtaskicon = strtaskicon;
	}

	public String getstrtaskfinishlargeimage() {
		return strtaskfinishlargeimage;
	}

	public void setstrtaskfinishlargeimage(String strtaskicon) {
		this.strtaskfinishlargeimage = strtaskicon;
	}

	public String getstrtaskverifilargeimage() {
		return strtaskverifilargeimage;
	}

	public void setstrtaskverifilargeimage(String strtaskicon) {
		this.strtaskverifilargeimage = strtaskicon;
	}

	public int getnsendcommentimageret() {
		return nsendcommentimageret;
	}

	public void setnsendcommentimageret(int nValue) {
		this.nsendcommentimageret = nValue;
	}

	public String getstrcommentlargeimage() {
		return strcommentlargeimage;
	}

	public void setstrcommentlargeimage(String strimage) {
		this.strcommentlargeimage = strimage;
	}

	public int getnretsettaskret() {
		return nretsettaskret;
	}

	public void setnretsettaskret(int nValue) {
		this.nretsettaskret = nValue;
	}

	public int getnrecordvalueret() {
		return nrecordvalueret;
	}

	public void setnrecordvalueret(int nValue) {
		this.nrecordvalueret = nValue;
	}

	public CreditAndCharmForTask getcreditandcharm() {
		return creditandcharm;
	}

	public void setcreditandcharm(CreditAndCharmForTask TaskValue) {
		this.creditandcharm = TaskValue;
	}

	public int getnsendadviceret() {
		return nsendadviceret;
	}

	public void setnsendadviceret(int nValue) {
		this.nsendadviceret = nValue;
	}

	public int getnJudgeValidityRet() {
		return nJudgeValidityRet;
	}

	public void setnJudgeValidityRet(int nValue) {
		this.nJudgeValidityRet = nValue;
	}

	public void setnversioncode(int nversioncode) {
		this.nversioncode = nversioncode;
	}

	public int getnversioncode() {
		return this.nversioncode;
	}

	public void setstrversionname(String versionname) {
		this.strversionname = versionname;
	}

	public String getstrversionname() {
		return this.strversionname;
	}

	public void setbaddtaskforlimitret(int nValue) {
		this.baddtaskforlimitret = nValue;
	}

	public int getbaddtaskforlimitret() {
		return this.baddtaskforlimitret;
	}

	public void setbaddtaskforlimitnewret(int nValue) {
		this.baddtaskforlimitnewret = nValue;
	}

	public int getbaddtaskforlimitnewret() {
		return this.baddtaskforlimitnewret;
	}

	public void setnchangepersonimageret(int nValue) {
		this.nchangepersonimageret = nValue;
	}

	public int getnchangepersonimageret() {
		return this.nchangepersonimageret;
	}

	public void setdistancehelpdata(List<DistanceDetail> data) {
		this.distancehelpdata = data;
	}

	public List<DistanceDetail> getdistancehelpdata() {
		return this.distancehelpdata;
	}

	public void setdistancesharedata(List<DistanceDetail> data) {
		this.distancesharedata = data;
	}

	public List<DistanceDetail> getdistancesharedata() {
		return this.distancesharedata;
	}

	public void setdistancehelpdata1(List<DistanceDetail> data) {
		this.distancehelpdata1 = data;
	}

	public List<DistanceDetail> getdistancehelpdata1() {
		return this.distancehelpdata1;
	}

	public void setdistancesharedata1(List<DistanceDetail> data) {
		this.distancesharedata1 = data;
	}

	public List<DistanceDetail> getdistancesharedata1() {
		return this.distancesharedata1;
	}

	public int getcommentstatusret() {
		return commentstatusret;
	}

	public void setcommentstatusret(int nret) {
		this.commentstatusret = nret;
	}

	public int getnsendcommentnewret() {
		return nsendcommentnewret;
	}

	public void setnsendcommentnewret(int nCommentRet) {
		this.nsendcommentnewret = nCommentRet;
	}

	public int getnsendcommentnewsecretret() {
		return nsendcommentnewsecretret;
	}

	public void setnsendcommentnewsecretret(int nCommentRet) {
		this.nsendcommentnewsecretret = nCommentRet;
	}

	public String getstrsendcommentnewsecretret() {
		return strsendcommentnewsecretret;
	}

	public void setstrsendcommentnewsecretret(String strCommentRet) {
		this.strsendcommentnewsecretret = strCommentRet;
	}

	public int getnsendcommentimagenewret() {
		return nsendcommentimagenewret;
	}

	public void setnsendcommentimagenewret(int nCommentRet) {
		this.nsendcommentimagenewret = nCommentRet;
	}

	public String getstrsendcommentimagenewret() {
		return strsendcommentimagenewret;
	}

	public void setstrsendcommentimagenewret(String strCommentRet) {
		this.strsendcommentimagenewret = strCommentRet;
	}

	public int getnPraiseRet() {
		return nPraiseRet;
	}

	public void setnPraiseRet(int nCommentRet) {
		this.nPraiseRet = nCommentRet;
	}

	public int getnBrowseRet() {
		return nBrowseRet;
	}

	public void setnBrowseRet(int nCommentRet) {
		this.nBrowseRet = nCommentRet;
	}

	public List<TaskInfoDetail> gettaskinfodetails() {
		return taskinfodetails;
	}

	public void settaskinfodetails(List<TaskInfoDetail> taskinfos) {
		this.taskinfodetails = taskinfos;
	}

	public int getnupdateitemdetailtextRet() {
		return nupdateitemdetailtextRet;
	}

	public void setnupdateitemdetailtextRet(int nRet) {
		this.nupdateitemdetailtextRet = nRet;
	}

	public int getnupdateitemtitletextRet() {
		return nupdateitemtitletextRet;
	}

	public void setnupdateitemtitletextRet(int nRet) {
		this.nupdateitemtitletextRet = nRet;
	}

	public String getstrfirstimage() {
		return strfirstimage;
	}

	public void setstrfirstimage(String strImage) {
		this.strfirstimage = strImage;
	}

	public TaskIcon getmothersmallicon() {
		return mothersmallicon;
	}

	public void setmothersmallicon(TaskIcon taskicon) {
		this.mothersmallicon = taskicon;
	}

	public int getdeletedynamicret() {
		return deletedynamicret;
	}

	public void setdeletedynamicret(int nRet) {
		this.deletedynamicret = nRet;
	}

	public String GetTaskInfo() {
		String strName = "";
		// String strName = "上海";
		try {
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("UTF-8");
			strName = this.request.getParameter("TaskType");
		} catch (Exception e) {
			e.printStackTrace();
		}
		taskinfos = goodService.GetTaskInfo(strName);
		return SUCCESS;
	}

	public String GetTaskInfoForLimit() {
		String strName = "";
		try {
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("UTF-8");
			strName = this.request.getParameter("TaskType");
		} catch (Exception e) {
			e.printStackTrace();
		}
		taskdetailinfos = goodService.GetTaskInfoForLimit(strName);
		return SUCCESS;
	}

	public String RegisterCustomer() {
		String strname1 = "";
		String strname2 = "";
		String strname3 = "";
		String strname4 = "";
		String strname5 = "";
		String strname6 = "";
		String strname7 = "";
		try {
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("UTF-8");
			strname1 = this.request.getParameter("PersonName");
			strname2 = this.request.getParameter("Password");
			strname3 = this.request.getParameter("PersonIcon");
			strname4 = this.request.getParameter("CreditValue");
			strname5 = this.request.getParameter("CharmValue");
			strname6 = this.request.getParameter("strNickName");
			strname7 = this.request.getParameter("strSex");
		} catch (Exception e) {
			e.printStackTrace();
		}
		bregeditret = goodService.RegisterCustomer(strname3, strname1,
				strname2, strname4, strname5, strname6, strname7);
		return SUCCESS;
	}

	public String LogIn() {
		String strname1 = "";
		String strname2 = "";
		try {
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("UTF-8");
			strname1 = this.request.getParameter("PersonName");
			strname2 = this.request.getParameter("Password");
		} catch (Exception e) {
			e.printStackTrace();
		}
		customerinfo = goodService.LogIn(strname1, strname2);
		return SUCCESS;
	}

	public String AddTaskInfo() {
		String strname1 = "";
		String strname2 = "";
		String strname3 = "";
		String strname4 = "";
		String strname5 = "";
		String strname6 = "";
		String strname7 = "";
		String strname8 = "";
		String strname9 = "";
		String strname10 = "";
		// String strname11 = "";
		String strname12 = "";
		String strname13 = "";
		String strname14 = "";
		String strname15 = "";
		String strname16 = "";
		String strname17 = "";
		String strname18 = "";
		String strname19 = "";
		String strname20 = "";
		try {
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("UTF-8");
			strname1 = this.request.getParameter("nTaskType");
			strname2 = this.request.getParameter("strRegionName");
			strname3 = this.request.getParameter("strTaskTitle");
			strname4 = this.request.getParameter("strTaskDeatial");
			strname5 = this.request.getParameter("strTaskAnnounceTime");
			strname6 = this.request.getParameter("strTaskImpleTime");
			strname7 = this.request.getParameter("strTaskAccountName");
			strname8 = this.request.getParameter("strImplementAccountName");
			strname9 = this.request.getParameter("dLongitude");
			strname10 = this.request.getParameter("dLatidude");
			// strname11 = this.request.getParameter("strTaskAccountIcon");
			strname12 = this.request.getParameter("strTimeLimit");
			strname13 = this.request.getParameter("strIcon1");
			strname14 = this.request.getParameter("strIcon2");
			strname15 = this.request.getParameter("strIcon3");
			strname16 = this.request.getParameter("strIcon4");
			strname17 = this.request.getParameter("strIcon5");
			strname18 = this.request.getParameter("strIcon6");
			strname19 = this.request.getParameter("strTaskAccountTrueName");
			strname20 = this.request
					.getParameter("strImplementAccountTrueName");
		} catch (Exception e) {
			e.printStackTrace();
		}
		baddtaskret = goodService.AddTaskInfo(strname1, strname2, strname3,
				strname4, strname5, strname6, strname7, strname8, strname19,
				strname20, strname9, strname10, ""/* strname11 */, strname12,
				strname13, strname14, strname15, strname16, strname17,
				strname18);
		return SUCCESS;
	}

	// 选择任务
	public String SelectTask() {
		String strname1 = "";
		String strname2 = "";
		String strname3 = "";
		String strname4 = "";
		try {
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("UTF-8");
			strname1 = this.request.getParameter("TaskId");
			strname2 = this.request.getParameter("PersonName");
			strname3 = this.request.getParameter("strType");
			strname4 = this.request.getParameter("strPersonNickName");
		} catch (Exception e) {
			e.printStackTrace();
		}
		nselecttaskret = goodService.SelectTask(strname1, strname2, strname3,
				strname4);
		return SUCCESS;
	}

	public String GetMsgNum() {
		String strname1 = "";
		try {
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("UTF-8");
			strname1 = this.request.getParameter("AnnounceName");
		} catch (Exception e) {
			e.printStackTrace();
		}
		taskinfos = goodService.GetMsgInfoNum(strname1);
		return SUCCESS;
	}

	public String UpdateTaskSelectType() {
		String strname1 = "";
		String strname2 = "";
		String strname3 = "";
		String strname4 = "";
		try {
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("UTF-8");
			strname1 = this.request.getParameter("AnnounceName");
			strname2 = this.request.getParameter("nValue");
			strname3 = this.request.getParameter("TaskId");
			strname4 = this.request.getParameter("strType");
		} catch (Exception e) {
			e.printStackTrace();
		}
		announceret1 = goodService.UpdateTaskSelectType(strname1,
				Integer.parseInt(strname2), strname3, strname4);
		return SUCCESS;
	}

	public String UpdateTaskFinishType() {
		String strname1 = "";
		String strname2 = "";
		String strname3 = "";
		String strname4 = "";
		String strname5 = "";
		String strname6 = "";
		try {
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("UTF-8");
			strname1 = this.request.getParameter("AnnounceName");
			strname2 = this.request.getParameter("nValue");
			strname3 = this.request.getParameter("TaskId");
			strname4 = this.request.getParameter("Base64Image");
			strname5 = this.request.getParameter("CommentContent");
			strname6 = this.request.getParameter("nTaskType");
		} catch (Exception e) {
			e.printStackTrace();
		}
		announceret2 = goodService.UpdateTaskFinishType(
				Integer.parseInt(strname6), strname1,
				Integer.parseInt(strname2), strname3, strname4, strname5);
		return SUCCESS;
	}

	public String UpdateTaskImplementCommentType() {
		String strname1 = "";
		String strname2 = "";
		String strname3 = "";
		String strname4 = "";
		try {
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("UTF-8");
			strname1 = this.request.getParameter("AnnounceName");
			strname2 = this.request.getParameter("nValue");
			strname3 = this.request.getParameter("TaskId");
			strname4 = this.request.getParameter("strType");
		} catch (Exception e) {
			e.printStackTrace();
		}
		announceret3 = goodService.UpdateTaskImplementCommentType(strname1,
				Integer.parseInt(strname2), strname3, strname4);
		return SUCCESS;
	}

	public String UpdateTaskVerifiType() {
		String strname1 = "";
		String strname2 = "";
		String strname3 = "";
		String strname4 = "";
		String strname5 = "";
		String strname6 = "";
		try {
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("UTF-8");
			strname1 = this.request.getParameter("AnnounceName");
			strname2 = this.request.getParameter("nValue");
			strname3 = this.request.getParameter("TaskId");
			strname4 = this.request.getParameter("Base64Image");
			strname5 = this.request.getParameter("CommentContent");
			strname6 = this.request.getParameter("nTaskType");
		} catch (Exception e) {
			e.printStackTrace();
		}
		implementret1 = goodService.UpdateTaskVerifiType(
				Integer.parseInt(strname6), strname1,
				Integer.parseInt(strname2), strname3, strname4, strname5);
		return SUCCESS;
	}

	public String UpdateTaskAnnounceCommentType() {
		String strname1 = "";
		String strname2 = "";
		String strname3 = "";
		String strname4 = "";
		try {
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("UTF-8");
			strname1 = this.request.getParameter("AnnounceName");
			strname2 = this.request.getParameter("nValue");
			strname3 = this.request.getParameter("TaskId");
			strname4 = this.request.getParameter("strType");
		} catch (Exception e) {
			e.printStackTrace();
		}
		implementret2 = goodService.UpdateTaskAnnounceCommentType(strname1,
				Integer.parseInt(strname2), strname3, strname4);
		return SUCCESS;
	}

	public String GetComments() {
		String strname1 = "";
		String strname2 = "";
		try {
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("UTF-8");
			strname1 = this.request.getParameter("TaskId");
			strname2 = this.request.getParameter("strType");
		} catch (Exception e) {
			e.printStackTrace();
		}
		commentinfos = goodService.GetCommentsForTask(strname1, strname2);
		return SUCCESS;
	}

	public String GetNewComments() {
		String strname1 = "";
		String strname2 = "";
		String strname3 = "";
		try {
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("UTF-8");
			strname1 = this.request.getParameter("TaskId");
			strname2 = this.request.getParameter("strType");
			strname3 = this.request.getParameter("strCustomerName");
		} catch (Exception e) {
			e.printStackTrace();
		}
		newcommentinfos = goodService.GetCommentsForTaskNew(strname1, strname2,
				strname3);
		return SUCCESS;
	}

	public String GetNewComments1() {
		String strname1 = "";
		String strname2 = "";
		String strname3 = "";
		String strname4 = "";
		try {
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("UTF-8");
			strname1 = this.request.getParameter("TaskId");
			strname2 = this.request.getParameter("strType");
			strname3 = this.request.getParameter("strCustomerName");
			strname4 = this.request.getParameter("strTime");
		} catch (Exception e) {
			e.printStackTrace();
		}
		newcommentinfos = goodService.GetCommentsForTaskNew1(strname1,
				strname2, strname3, strname4);
		return SUCCESS;
	}

	public String SendCommentContent() {
		String strname1 = "";
		String strname2 = "";
		String strname3 = "";
		String strname4 = "";
		String strname5 = "";
		String strname6 = "";
		String strname7 = "";
		String strname8 = "";
		try {
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("UTF-8");
			strname1 = this.request.getParameter("strTaskId");
			strname2 = this.request.getParameter("strCommentPersonName");
			strname3 = this.request.getParameter("strReceiveCommentPersonName");
			strname4 = this.request.getParameter("strCommentContent");
			strname5 = this.request.getParameter("strCommentImage");
			strname6 = this.request.getParameter("strType");
			strname7 = this.request.getParameter("strCommentPersonNickName");
			strname8 = this.request
					.getParameter("strReceiveCommentPersonNickName");
		} catch (Exception e) {
			e.printStackTrace();
		}
		nsendcommentret = goodService.SendCommentContent(strname1, strname2,
				strname3, strname4, strname5, strname6, strname7, strname8);
		return SUCCESS;
	}

	public String SendCommentImage() {
		String strname1 = "";
		String strname2 = "";
		String strname3 = "";
		String strname4 = "";
		String strname5 = "";
		String strname6 = "";
		String strname7 = "";
		String strname8 = "";
		String strname9 = "";
		try {
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("UTF-8");
			strname1 = this.request.getParameter("strTaskId");
			strname2 = this.request.getParameter("strCommentPersonName");
			strname3 = this.request.getParameter("strReceiveCommentPersonName");
			strname4 = this.request.getParameter("strCommentImage");
			strname5 = this.request.getParameter("strSmallImage");
			strname6 = this.request.getParameter("strLargeImage");
			strname7 = this.request.getParameter("strType");
			strname8 = this.request.getParameter("strCommentPersonNickName");
			strname9 = this.request
					.getParameter("strReceiveCommentPersonNickName");
		} catch (Exception e) {
			e.printStackTrace();
		}
		nsendcommentimageret = goodService.SendCommentImage(strname1, strname2,
				strname3, strname4, strname5, strname6, strname7, strname8,
				strname9);
		return SUCCESS;
	}

	public String GetCommentsLargeImage() {
		String strname1 = "";
		String strname2 = "";
		String strname3 = "";
		try {
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("UTF-8");
			strname1 = this.request.getParameter("TaskId");
			strname2 = this.request.getParameter("strType");
			strname3 = this.request.getParameter("nCommentIndex");
		} catch (Exception e) {
			e.printStackTrace();
		}
		int nIndex = Integer.parseInt(strname3);
		strcommentlargeimage = goodService.GetCommentsLargeImage(strname1,
				strname2, nIndex);
		return SUCCESS;
	}

	public String GetUpdataSignl() {
		String strname1 = "";
		try {
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("UTF-8");
			strname1 = this.request.getParameter("strCurrentAccountName");
		} catch (Exception e) {
			e.printStackTrace();
		}
		nupdatevalue = goodService.GetUpdateSignal(strname1);
		return SUCCESS;
	}

	public String SetUpdataSignl() {
		String strname1 = "";
		String strname2 = "";
		try {
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("UTF-8");
			strname1 = this.request.getParameter("strCurrentAccountName");
			strname2 = this.request.getParameter("strUpdateValue");
		} catch (Exception e) {
			e.printStackTrace();
		}
		nsetupdatevalueret = goodService.SetUpdateSignal(strname1,
				Integer.parseInt(strname2));
		return SUCCESS;
	}

	public String SetCreditValue() {
		String strname1 = "";
		String strname2 = "";
		try {
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("UTF-8");
			strname1 = this.request.getParameter("strPersonName");
			strname2 = this.request.getParameter("nCreditValue");
		} catch (Exception e) {
			e.printStackTrace();
		}
		nsetcreditvalueret = goodService.SetCreditValue(strname1,
				Integer.parseInt(strname2));
		return SUCCESS;
	}

	public String SetCharmValue() {
		String strname1 = "";
		String strname2 = "";
		try {
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("UTF-8");
			strname1 = this.request.getParameter("strPersonName");
			strname2 = this.request.getParameter("nCharmValue");
		} catch (Exception e) {
			e.printStackTrace();
		}
		nsetcharmvalueret = goodService.SetCharmValue(strname1,
				Integer.parseInt(strname2));
		return SUCCESS;
	}

	public String GetCreditValue() {
		String strname1 = "";
		try {
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("UTF-8");
			strname1 = this.request.getParameter("strPersonName");
		} catch (Exception e) {
			e.printStackTrace();
		}
		ngetcreditvalueret = goodService.GetCreditValue(strname1);
		return SUCCESS;
	}

	public String GetCharmValue() {
		String strname1 = "";
		try {
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("UTF-8");
			strname1 = this.request.getParameter("strPersonName");
		} catch (Exception e) {
			e.printStackTrace();
		}
		ngetcharmvalueret = goodService.GetCharmValue(strname1);
		return SUCCESS;
	}

	public String AddCreditValue() {
		String strname1 = "";
		String strname2 = "";
		try {
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("UTF-8");
			strname1 = this.request.getParameter("strPersonName");
			strname2 = this.request.getParameter("nIncrValue");
		} catch (Exception e) {
			e.printStackTrace();
		}
		naddcreditvalueret = goodService.AddCreditValue(strname1,
				Integer.parseInt(strname2));
		return SUCCESS;
	}

	public String AddCharmValue() {
		String strname1 = "";
		String strname2 = "";
		try {
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("UTF-8");
			strname1 = this.request.getParameter("strPersonName");
			strname2 = this.request.getParameter("nIncrValue");
		} catch (Exception e) {
			e.printStackTrace();
		}
		naddcharmvalueret = goodService.AddCharmValue(strname1,
				Integer.parseInt(strname2));
		return SUCCESS;
	}

	public String GetTaskRemainTime() {
		String strname1 = "";
		String strname2 = "";
		try {
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("UTF-8");
			strname1 = this.request.getParameter("strTaskId");
			strname2 = this.request.getParameter("strType");
		} catch (Exception e) {
			e.printStackTrace();
		}
		ltaskremaintime = goodService.GetTaskRemainTime(strname1, strname2);
		return SUCCESS;
	}

	public String LoadTaskData() {
		String strname1 = "";
		String strname2 = "";
		String strname3 = "";
		try {
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("UTF-8");
			strname1 = this.request.getParameter("nLimit");
			strname2 = this.request.getParameter("nType");
			strname3 = this.request.getParameter("nMaxTaskId");
		} catch (Exception e) {
			e.printStackTrace();
		}
		int nLimit = Integer.parseInt(strname1);
		int nType = Integer.parseInt(strname2);
		int nMaxTaskId = Integer.parseInt(strname3);
		loadtaskinfos = goodService.LoadTaskData(nLimit, nType, nMaxTaskId);
		return SUCCESS;
	}

	public String LoadTaskDataForLimit() {
		String strname1 = "";
		String strname2 = "";
		String strname3 = "";
		try {
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("UTF-8");
			strname1 = this.request.getParameter("nLimit");
			strname2 = this.request.getParameter("nType");
			strname3 = this.request.getParameter("nMaxTaskId");
		} catch (Exception e) {
			e.printStackTrace();
		}
		int nLimit = Integer.parseInt(strname1);
		int nType = Integer.parseInt(strname2);
		int nMaxTaskId = Integer.parseInt(strname3);
		loaddetailtaskinfos = goodService.LoadTaskDataForLimit(nLimit, nType,
				nMaxTaskId);
		return SUCCESS;
	}
	
	public String LoadTaskDataForLimitNew() {
		String strname1 = "";
		String strname2 = "";
		String strname3 = "";
		try {
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("UTF-8");
			strname1 = this.request.getParameter("nLimit");
			strname2 = this.request.getParameter("nType");
			strname3 = this.request.getParameter("strMaxTime");
		} catch (Exception e) {
			e.printStackTrace();
		}
		int nLimit = Integer.parseInt(strname1);
		int nType = Integer.parseInt(strname2);
		loaddetailtaskinfosnew = goodService.LoadTaskDataForLimitNew(nLimit, nType,
				strname3);
		return SUCCESS;
	}

	public String UpdateTaskData() {
		String strname1 = "";
		String strname2 = "";
		String strname3 = "";
		try {
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("UTF-8");
			strname1 = this.request.getParameter("nLimit");
			strname2 = this.request.getParameter("nType");
			strname3 = this.request.getParameter("nMaxTaskId");
		} catch (Exception e) {
			e.printStackTrace();
		}
		int nLimit = Integer.parseInt(strname1);
		int nType = Integer.parseInt(strname2);
		int nMaxTaskId = Integer.parseInt(strname3);
		updatetaskinfos = goodService.UpdateTaskData(nLimit, nType, nMaxTaskId);
		return SUCCESS;
	}

	public String UpdateTaskDataForLimit() {
		String strname1 = "";
		String strname2 = "";
		String strname3 = "";
		try {
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("UTF-8");
			strname1 = this.request.getParameter("nLimit");
			strname2 = this.request.getParameter("nType");
			strname3 = this.request.getParameter("nMaxTaskId");
		} catch (Exception e) {
			e.printStackTrace();
		}
		int nLimit = Integer.parseInt(strname1);
		int nType = Integer.parseInt(strname2);
		int nMaxTaskId = Integer.parseInt(strname3);
		updatedetailtaskinfos = goodService.UpdateTaskDataForLimit(nLimit,
				nType, nMaxTaskId);
		return SUCCESS;
	}

	public String UpdateTaskDataForLimitNew() {
		String strname1 = "";
		String strname2 = "";
		String strname3 = "";
		try {
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("UTF-8");
			strname1 = this.request.getParameter("nLimit");
			strname2 = this.request.getParameter("nType");
			strname3 = this.request.getParameter("strMaxTime");
		} catch (Exception e) {
			e.printStackTrace();
		}
		int nLimit = Integer.parseInt(strname1);
		int nType = Integer.parseInt(strname2);
		updatedetailtaskinfosnew = goodService.UpdateTaskDataForLimitNew(
				nLimit, nType, strname3);
		return SUCCESS;
	}

	public String UploadLargeImage() {
		String strname1 = "";
		String strname2 = "";
		String strname3 = "";
		String strname4 = "";
		String strname5 = "";
		String strname6 = "";
		String strname7 = "";
		String strname8 = "";
		try {
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("UTF-8");
			strname1 = this.request.getParameter("strTaskId");
			strname2 = this.request.getParameter("strTaskType");
			strname3 = this.request.getParameter("strIcon1");
			strname4 = this.request.getParameter("strIcon2");
			strname5 = this.request.getParameter("strIcon3");
			strname6 = this.request.getParameter("strIcon4");
			strname7 = this.request.getParameter("strIcon5");
			strname8 = this.request.getParameter("strIcon6");
		} catch (Exception e) {
			e.printStackTrace();
		}
		nuploadlargeimageret = goodService.UploadLargeImage(strname1, strname2,
				strname3, strname4, strname5, strname6, strname7, strname8);
		return SUCCESS;
	}

	public String UploadTaskFinishTypeForLargeImage() {
		String strname1 = "";
		String strname2 = "";
		String strname3 = "";
		try {
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("UTF-8");
			strname1 = this.request.getParameter("strTaskId");
			strname2 = this.request.getParameter("strTaskType");
			strname3 = this.request.getParameter("strBase64Image");
		} catch (Exception e) {
			e.printStackTrace();
		}
		nuploadfinishimageret = goodService.UploadTaskFinishTypeForLargeImage(
				strname1, strname2, strname3);
		return SUCCESS;
	}

	public String UploadTaskVerifiTypeForLargeImage() {
		String strname1 = "";
		String strname2 = "";
		String strname3 = "";
		try {
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("UTF-8");
			strname1 = this.request.getParameter("strTaskId");
			strname2 = this.request.getParameter("strTaskType");
			strname3 = this.request.getParameter("strBase64Image");
		} catch (Exception e) {
			e.printStackTrace();
		}
		nuploadfinishimageret = goodService.UploadTaskVerifiTypeForLargeImage(
				strname1, strname2, strname3);
		return SUCCESS;
	}

	public String GetTaskSmallIcon() {
		String strname1 = "";
		String strname2 = "";
		try {
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("UTF-8");
			strname1 = this.request.getParameter("strTaskId");
			strname2 = this.request.getParameter("strTaskType");
		} catch (Exception e) {
			e.printStackTrace();
		}
		mtaskicon = goodService.GetTaskSmallIcon(strname1, strname2);
		return SUCCESS;
	}

	public String GetFirstSmallIcon() {
		String strname1 = "";
		String strname2 = "";
		try {
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("UTF-8");
			strname1 = this.request.getParameter("strTaskId");
			strname2 = this.request.getParameter("strTaskType");
		} catch (Exception e) {
			e.printStackTrace();
		}
		strfirstimage = goodService.GetFirstSmallIcon(strname1, strname2);
		return SUCCESS;
	}

	public String GetOtherSmallIcon() {
		String strname1 = "";
		String strname2 = "";
		try {
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("UTF-8");
			strname1 = this.request.getParameter("strTaskId");
			strname2 = this.request.getParameter("strTaskType");
		} catch (Exception e) {
			e.printStackTrace();
		}
		mothersmallicon = goodService.GetTaskSmallIcon(strname1, strname2);
		return SUCCESS;
	}

	public String GetTaskLargeIcon() {
		String strname1 = "";
		String strname2 = "";
		String strname3 = "";
		try {
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("UTF-8");
			strname1 = this.request.getParameter("strTaskId");
			strname2 = this.request.getParameter("strTaskType");
			strname3 = this.request.getParameter("nIconId");
		} catch (Exception e) {
			e.printStackTrace();
		}
		int nIconId = Integer.parseInt(strname3);
		strtaskicon = goodService.GetTaskLargeIcon(strname1, strname2, nIconId);
		return SUCCESS;
	}

	public String GetTaskFinishLargeImage() {
		String strname1 = "";
		String strname2 = "";
		try {
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("UTF-8");
			strname1 = this.request.getParameter("strTaskId");
			strname2 = this.request.getParameter("strTaskType");
		} catch (Exception e) {
			e.printStackTrace();
		}
		strtaskfinishlargeimage = goodService.GetTaskFinishLargeImage(strname1,
				strname2);
		return SUCCESS;
	}

	public String GetTaskVerifiLargeImage() {
		String strname1 = "";
		String strname2 = "";
		try {
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("UTF-8");
			strname1 = this.request.getParameter("strTaskId");
			strname2 = this.request.getParameter("strTaskType");
		} catch (Exception e) {
			e.printStackTrace();
		}
		strtaskverifilargeimage = goodService.GetTaskVerifiLargeImage(strname1,
				strname2);
		return SUCCESS;
	}

	public String ResetTaskStatue() {
		String strname1 = "";
		String strname2 = "";
		try {
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("UTF-8");
			strname1 = this.request.getParameter("strTaskId");
			strname2 = this.request.getParameter("strTaskType");
		} catch (Exception e) {
			e.printStackTrace();
		}
		nretsettaskret = goodService.ResetTaskStatue(strname1, strname2);
		return SUCCESS;
	}

	public String RecordCreditAndCharmForTask() {
		String strname1 = "";
		String strname2 = "";
		String strname3 = "";
		String strname4 = "";
		try {
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("UTF-8");
			strname1 = this.request.getParameter("strTaskId");
			strname2 = this.request.getParameter("strTaskType");
			strname3 = this.request.getParameter("nCredit");
			strname4 = this.request.getParameter("nCharm");
		} catch (Exception e) {
			e.printStackTrace();
		}
		int nCredit = Integer.parseInt(strname3);
		int nCharm = Integer.parseInt(strname4);
		nrecordvalueret = goodService.RecordCreditAndCharmForTask(strname1,
				strname2, nCredit, nCharm);
		return SUCCESS;
	}

	public String GetCreditAndCharmForTask() {
		String strname1 = "";
		String strname2 = "";
		try {
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("UTF-8");
			strname1 = this.request.getParameter("strTaskId");
			strname2 = this.request.getParameter("strTaskType");
		} catch (Exception e) {
			e.printStackTrace();
		}
		creditandcharm = goodService.GetCreditAndCharmForTask(strname1,
				strname2);
		return SUCCESS;
	}

	public String SendAdviceText() {
		String strname1 = "";
		try {
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("UTF-8");
			strname1 = this.request.getParameter("strAdvice");
		} catch (Exception e) {
			e.printStackTrace();
		}
		nsendadviceret = goodService.SendAdviceText(strname1);
		return SUCCESS;
	}

	public String JudgeCustomerValidity() {
		String strname1 = "";
		String strname2 = "";
		try {
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("UTF-8");
			strname1 = this.request.getParameter("PersonName");
			strname2 = this.request.getParameter("Password");
		} catch (Exception e) {
			e.printStackTrace();
		}
		nJudgeValidityRet = goodService.JudgeCustomerValidity(strname1,
				strname2);
		return SUCCESS;
	}

	public String GetVersionCode() {
		try {
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		nversioncode = 2;
		return SUCCESS;
	}

	public String GetVersionName() {
		try {
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		strversionname = "1.1";
		return SUCCESS;
	}

	public String RegisterCustomerDetail() {
		String strname1 = "";
		String strname2 = "";
		String strname3 = "";
		String strname4 = "";
		String strname5 = "";
		String strname6 = "";
		String strname7 = "";
		String strname8 = "";
		try {
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("UTF-8");
			strname1 = this.request.getParameter("PersonName");
			strname2 = this.request.getParameter("Password");
			strname3 = this.request.getParameter("PersonIcon");
			strname4 = this.request.getParameter("CreditValue");
			strname5 = this.request.getParameter("CharmValue");
			strname6 = this.request.getParameter("strNickName");
			strname7 = this.request.getParameter("strSex");
			strname8 = this.request.getParameter("strLargeImage");
		} catch (Exception e) {
			e.printStackTrace();
		}
		bregeditretforlargeimage = goodService.RegisterCustomerDetail(strname3,
				strname1, strname2, strname4, strname5, strname6, strname7,
				strname8);
		return SUCCESS;
	}

	public String AddTaskInfoForLimit() {
		String strname1 = "";
		String strname2 = "";
		String strname3 = "";
		String strname4 = "";
		String strname5 = "";
		String strname6 = "";
		String strname7 = "";
		String strname8 = "";
		String strname9 = "";
		String strname10 = "";
		// String strname11 = "";
		String strname12 = "";
		String strname13 = "";
		String strname14 = "";
		String strname15 = "";
		String strname16 = "";
		String strname17 = "";
		String strname18 = "";
		String strname19 = "";
		String strname20 = "";
		String strname21 = "";
		String strname22 = "";
		try {
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("UTF-8");
			strname1 = this.request.getParameter("nTaskType");
			strname2 = this.request.getParameter("strRegionName");
			strname3 = this.request.getParameter("strTaskTitle");
			strname4 = this.request.getParameter("strTaskDeatial");
			strname5 = this.request.getParameter("strTaskAnnounceTime");
			strname6 = this.request.getParameter("strTaskImpleTime");
			strname7 = this.request.getParameter("strTaskAccountName");
			strname8 = this.request.getParameter("strImplementAccountName");
			strname9 = this.request.getParameter("dLongitude");
			strname10 = this.request.getParameter("dLatidude");
			// strname11 = this.request.getParameter("strTaskAccountIcon");
			strname12 = this.request.getParameter("strTimeLimit");
			strname13 = this.request.getParameter("strIcon1");
			strname14 = this.request.getParameter("strIcon2");
			strname15 = this.request.getParameter("strIcon3");
			strname16 = this.request.getParameter("strIcon4");
			strname17 = this.request.getParameter("strIcon5");
			strname18 = this.request.getParameter("strIcon6");
			strname19 = this.request.getParameter("strTaskAccountTrueName");
			strname20 = this.request
					.getParameter("strImplementAccountTrueName");
			strname21 = this.request.getParameter("nCreditValue");
			strname22 = this.request.getParameter("nCharmValue");
		} catch (Exception e) {
			e.printStackTrace();
		}
		int nCreditValue = Integer.parseInt(strname21);
		int nCharmValue = Integer.parseInt(strname22);
		baddtaskforlimitret = goodService.AddTaskInfoForLimit(strname1,
				strname2, strname3, strname4, strname5, strname6, strname7,
				strname8, strname19, strname20, strname9, strname10,
				""/* strname11 */, strname12, strname13, strname14, strname15,
				strname16, strname17, strname18, nCreditValue, nCharmValue);
		return SUCCESS;
	}

	public String AddTaskInfoForLimitNew() {
		String strname1 = "";
		String strname2 = "";
		String strname3 = "";
		String strname4 = "";
		String strname5 = "";
		String strname6 = "";
		String strname7 = "";
		String strname8 = "";
		String strname9 = "";
		String strname10 = "";
		// String strname11 = "";
		String strname12 = "";
		String strname13 = "";
		String strname14 = "";
		String strname15 = "";
		String strname16 = "";
		String strname17 = "";
		String strname18 = "";
		String strname19 = "";
		String strname20 = "";
		String strname21 = "";
		String strname22 = "";
		String strname23 = "";
		String strname24 = "";
		try {
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("UTF-8");
			strname1 = this.request.getParameter("nTaskType");
			strname2 = this.request.getParameter("strRegionName");
			strname3 = this.request.getParameter("strTaskTitle");
			strname4 = this.request.getParameter("strTaskDeatial");
			strname5 = this.request.getParameter("strTaskAnnounceTime");
			strname6 = this.request.getParameter("strTaskImpleTime");
			strname7 = this.request.getParameter("strTaskAccountName");
			strname8 = this.request.getParameter("strImplementAccountName");
			strname9 = this.request.getParameter("dLongitude");
			strname10 = this.request.getParameter("dLatidude");
			// strname11 = this.request.getParameter("strTaskAccountIcon");
			strname12 = this.request.getParameter("strTimeLimit");
			strname13 = this.request.getParameter("strIcon1");
			strname14 = this.request.getParameter("strIcon2");
			strname15 = this.request.getParameter("strIcon3");
			strname16 = this.request.getParameter("strIcon4");
			strname17 = this.request.getParameter("strIcon5");
			strname18 = this.request.getParameter("strIcon6");
			strname19 = this.request.getParameter("strTaskAccountTrueName");
			strname20 = this.request
					.getParameter("strImplementAccountTrueName");
			strname21 = this.request.getParameter("nCreditValue");
			strname22 = this.request.getParameter("nCharmValue");
			strname23 = this.request.getParameter("strDistrictName");
			strname24 = this.request.getParameter("strCityName");
		} catch (Exception e) {
			e.printStackTrace();
		}
		int nCreditValue = Integer.parseInt(strname21);
		int nCharmValue = Integer.parseInt(strname22);
		baddtaskforlimitnewret = goodService.AddTaskInfoForLimitNew(strname1,
				strname2, strname3, strname4, strname5, strname6, strname7,
				strname8, strname19, strname20, strname9, strname10, "",
				strname12, strname13, strname14, strname15, strname16,
				strname17, strname18, nCreditValue, nCharmValue, strname23,
				strname24);
		return SUCCESS;
	}

	public String ChangePersonImage() {
		String strname1 = "";
		String strname2 = "";
		String strname3 = "";
		try {
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("UTF-8");
			strname1 = this.request.getParameter("strPersonName");
			strname2 = this.request.getParameter("strSmallImage");
			strname3 = this.request.getParameter("strLargeImage");
		} catch (Exception e) {
			e.printStackTrace();
		}
		nchangepersonimageret = goodService.ChangePersonImage(strname1,
				strname2, strname3);
		return SUCCESS;
	}

	public String GetHelpNearData() {
		String strname1 = "";
		String strname2 = "";
		String strname3 = "";
		String strname4 = "";
		try {
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("UTF-8");
			strname1 = this.request.getParameter("longitude");
			strname2 = this.request.getParameter("latitude");
			strname3 = this.request.getParameter("dist");
			strname4 = this.request.getParameter("name");
		} catch (Exception e) {
			e.printStackTrace();
		}
		Double lon = Double.parseDouble(strname1);
		Double lat = Double.parseDouble(strname2);
		Double distance = Double.parseDouble(strname3);
		distancehelpdata = goodService.getHelpNearData(lon, lat, distance,
				strname4);
		return SUCCESS;
	}

	public String GetShareNearData() {
		String strname1 = "";
		String strname2 = "";
		String strname3 = "";
		String strname4 = "";
		try {
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("UTF-8");
			strname1 = this.request.getParameter("longitude");
			strname2 = this.request.getParameter("latitude");
			strname3 = this.request.getParameter("dist");
			strname4 = this.request.getParameter("name");
		} catch (Exception e) {
			e.printStackTrace();
		}
		Double lon = Double.parseDouble(strname1);
		Double lat = Double.parseDouble(strname2);
		Double distance = Double.parseDouble(strname3);
		distancesharedata = goodService.getShareNearData(lon, lat, distance,
				strname4);
		return SUCCESS;
	}

	public String LoadHelpNearData() {
		String strname1 = "";
		String strname2 = "";
		String strname3 = "";
		String strname4 = "";
		String strname5 = "";
		try {
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("UTF-8");
			strname1 = this.request.getParameter("longitude");
			strname2 = this.request.getParameter("latitude");
			strname3 = this.request.getParameter("dist");
			strname4 = this.request.getParameter("name");
			strname5 = this.request.getParameter("time");
		} catch (Exception e) {
			e.printStackTrace();
		}
		Double lon = Double.parseDouble(strname1);
		Double lat = Double.parseDouble(strname2);
		Double distance = Double.parseDouble(strname3);
		distancehelpdata1 = goodService.LoadHelpNearData(lon, lat, distance,
				strname4, strname5);
		return SUCCESS;
	}

	public String LoadShareNearData() {
		String strname1 = "";
		String strname2 = "";
		String strname3 = "";
		String strname4 = "";
		String strname5 = "";
		try {
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("UTF-8");
			strname1 = this.request.getParameter("longitude");
			strname2 = this.request.getParameter("latitude");
			strname3 = this.request.getParameter("dist");
			strname4 = this.request.getParameter("name");
			strname5 = this.request.getParameter("time");
		} catch (Exception e) {
			e.printStackTrace();
		}
		Double lon = Double.parseDouble(strname1);
		Double lat = Double.parseDouble(strname2);
		Double distance = Double.parseDouble(strname3);
		distancesharedata1 = goodService.LoadShareNearData(lon, lat, distance,
				strname4, strname5);
		return SUCCESS;
	}

	public String GetMsgNumNew() {
		String strname1 = "";
		try {
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("UTF-8");
			strname1 = this.request.getParameter("AnnounceName");
		} catch (Exception e) {
			e.printStackTrace();
		}
		taskinfos = goodService.GetMsgInfoNumNew(strname1);
		return SUCCESS;
	}

	public String UpdateCommentType() {
		String strname1 = "";
		String strname2 = "";
		String strname3 = "";
		String strname4 = "";
		try {
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("UTF-8");
			strname1 = this.request.getParameter("AnnounceName");
			strname2 = this.request.getParameter("nValue");
			strname3 = this.request.getParameter("TaskId");
			strname4 = this.request.getParameter("strType");
		} catch (Exception e) {
			e.printStackTrace();
		}
		commentstatusret = goodService.UpdateCommentType(strname1,
				Integer.parseInt(strname2), strname3, strname4);
		return SUCCESS;
	}

	public String SendCommentContentNew() {
		String strname1 = "";
		String strname2 = "";
		String strname3 = "";
		String strname4 = "";
		String strname5 = "";
		String strname6 = "";
		String strname7 = "";
		try {
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("UTF-8");
			strname1 = this.request.getParameter("strTaskId");
			strname2 = this.request.getParameter("strCommentPersonName");
			strname3 = this.request.getParameter("strReceiveCommentPersonName");
			strname4 = this.request.getParameter("strCommentContent");
			strname5 = this.request.getParameter("strType");
			strname6 = this.request.getParameter("strCommentPersonNickName");
			strname7 = this.request
					.getParameter("strReceiveCommentPersonNickName");
		} catch (Exception e) {
			e.printStackTrace();
		}
		nsendcommentnewret = goodService.SendCommentContentNew(strname1,
				strname2, strname3, strname4, strname5, strname6, strname7);
		return SUCCESS;
	}

	public String SendCommentContentNewSecret() {
		String strname1 = "";
		String strname2 = "";
		String strname3 = "";
		String strname4 = "";
		String strname5 = "";
		String strname6 = "";
		String strname7 = "";
		String strname8 = "";
		try {
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("UTF-8");
			strname1 = this.request.getParameter("strTaskId");
			strname2 = this.request.getParameter("strCommentPersonName");
			strname3 = this.request.getParameter("strReceiveCommentPersonName");
			strname4 = this.request.getParameter("strCommentContent");
			strname5 = this.request.getParameter("strType");
			strname6 = this.request.getParameter("strCommentPersonNickName");
			strname7 = this.request
					.getParameter("strReceiveCommentPersonNickName");
			strname8 = this.request.getParameter("nSecretType");
		} catch (Exception e) {
			e.printStackTrace();
		}
		int nSecretType = Integer.parseInt(strname8);
		nsendcommentnewsecretret = goodService.SendCommentContentNewSecret(
				strname1, strname2, strname3, strname4, strname5, strname6,
				strname7, nSecretType);
		return SUCCESS;
	}

	public String SendCommentContentNewSecretTime() {
		String strname1 = "";
		String strname2 = "";
		String strname3 = "";
		String strname4 = "";
		String strname5 = "";
		String strname6 = "";
		String strname7 = "";
		String strname8 = "";
		try {
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("UTF-8");
			strname1 = this.request.getParameter("strTaskId");
			strname2 = this.request.getParameter("strCommentPersonName");
			strname3 = this.request.getParameter("strReceiveCommentPersonName");
			strname4 = this.request.getParameter("strCommentContent");
			strname5 = this.request.getParameter("strType");
			strname6 = this.request.getParameter("strCommentPersonNickName");
			strname7 = this.request
					.getParameter("strReceiveCommentPersonNickName");
			strname8 = this.request.getParameter("nSecretType");
		} catch (Exception e) {
			e.printStackTrace();
		}
		int nSecretType = Integer.parseInt(strname8);
		strsendcommentnewsecretret = goodService
				.SendCommentContentNewSecretTime(strname1, strname2, strname3,
						strname4, strname5, strname6, strname7, nSecretType);
		return SUCCESS;
	}

	public String SendCommentImageNew() {
		String strname1 = "";
		String strname2 = "";
		String strname3 = "";
		String strname4 = "";
		String strname5 = "";
		String strname6 = "";
		String strname7 = "";
		String strname8 = "";
		try {
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("UTF-8");
			strname1 = this.request.getParameter("strTaskId");
			strname2 = this.request.getParameter("strCommentPersonName");
			strname3 = this.request.getParameter("strReceiveCommentPersonName");
			strname4 = this.request.getParameter("strSmallImage");
			strname5 = this.request.getParameter("strLargeImage");
			strname6 = this.request.getParameter("strType");
			strname7 = this.request.getParameter("strCommentPersonNickName");
			strname8 = this.request
					.getParameter("strReceiveCommentPersonNickName");
		} catch (Exception e) {
			e.printStackTrace();
		}
		nsendcommentimagenewret = goodService.SendCommentImageNew(strname1,
				strname2, strname3, strname4, strname5, strname6, strname7,
				strname8);
		return SUCCESS;
	}

	public String SendCommentImageNewTime() {
		String strname1 = "";
		String strname2 = "";
		String strname3 = "";
		String strname4 = "";
		String strname5 = "";
		String strname6 = "";
		String strname7 = "";
		String strname8 = "";
		try {
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("UTF-8");
			strname1 = this.request.getParameter("strTaskId");
			strname2 = this.request.getParameter("strCommentPersonName");
			strname3 = this.request.getParameter("strReceiveCommentPersonName");
			strname4 = this.request.getParameter("strSmallImage");
			strname5 = this.request.getParameter("strLargeImage");
			strname6 = this.request.getParameter("strType");
			strname7 = this.request.getParameter("strCommentPersonNickName");
			strname8 = this.request
					.getParameter("strReceiveCommentPersonNickName");
		} catch (Exception e) {
			e.printStackTrace();
		}
		strsendcommentimagenewret = goodService.SendCommentImageNewTime(
				strname1, strname2, strname3, strname4, strname5, strname6,
				strname7, strname8);
		return SUCCESS;
	}

	public String GetUpdataSignlNew() {
		String strname1 = "";
		try {
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("UTF-8");
			strname1 = this.request.getParameter("strCurrentAccountName");
		} catch (Exception e) {
			e.printStackTrace();
		}
		nupdatevalue = goodService.GetUpdateSignalNew(strname1);
		return SUCCESS;
	}

	public String GetUpdataSignlNew1() {
		String strname1 = "";
		try {
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("UTF-8");
			strname1 = this.request.getParameter("strCurrentAccountName");
		} catch (Exception e) {
			e.printStackTrace();
		}
		nupdatevalue = goodService.GetUpdateSignalNew1(strname1);
		return SUCCESS;
	}

	public String PraiseToTaskOrShare() {
		String strname1 = "";
		String strname2 = "";
		String strname3 = "";
		String strname4 = "";
		try {
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("UTF-8");
			strname1 = this.request.getParameter("strTaskId");
			strname2 = this.request.getParameter("strPersonName");
			strname3 = this.request.getParameter("strReceivePersonName");
			strname4 = this.request.getParameter("strType");
		} catch (Exception e) {
			e.printStackTrace();
		}
		nPraiseRet = goodService.PraiseToTaskOrShare(strname1, strname2,
				strname3, strname4);
		return SUCCESS;
	}

	public String AddBrowseTimes() {
		String strname1 = "";
		String strname2 = "";
		try {
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("UTF-8");
			strname1 = this.request.getParameter("strTaskId");
			strname2 = this.request.getParameter("strType");
		} catch (Exception e) {
			e.printStackTrace();
		}
		nBrowseRet = goodService.AddBrowseTimes(strname1, strname2);
		return SUCCESS;
	}

	public String GetMsgNumNewDetail() {
		String strname1 = "";
		try {
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("UTF-8");
			strname1 = this.request.getParameter("AnnounceName");
		} catch (Exception e) {
			e.printStackTrace();
		}
		taskinfodetails = goodService.GetMsgInfoNumNewDetail(strname1);
		return SUCCESS;
	}

	public String GetMsgNumNewDetail1() {
		String strname1 = "";
		try {
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("UTF-8");
			strname1 = this.request.getParameter("AnnounceName");
		} catch (Exception e) {
			e.printStackTrace();
		}
		taskinfodetails = goodService.GetMsgInfoNumNewDetail1(strname1);
		return SUCCESS;
	}

	public String GetMsgNumNewDetail2() {
		String strname1 = "";
		String strname2 = "";
		try {
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("UTF-8");
			strname1 = this.request.getParameter("AnnounceName");
			strname2 = this.request.getParameter("strID");
		} catch (Exception e) {
			e.printStackTrace();
		}
		taskinfodetails = goodService.GetMsgInfoNumNewDetail2(strname1,
				strname2);
		return SUCCESS;
	}

	public String UpdateItemDetailText() {
		String strname1 = "";
		String strname2 = "";
		String strname3 = "";
		try {
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("UTF-8");
			strname1 = this.request.getParameter("strTaskId");
			strname2 = this.request.getParameter("strType");
			strname3 = this.request.getParameter("strText");
		} catch (Exception e) {
			e.printStackTrace();
		}
		nupdateitemdetailtextRet = goodService.UpdateItemDetailText(strname1,
				strname2, strname3);
		return SUCCESS;
	}

	public String UpdateItemTitleText() {
		String strname1 = "";
		String strname2 = "";
		String strname3 = "";
		try {
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("UTF-8");
			strname1 = this.request.getParameter("strTaskId");
			strname2 = this.request.getParameter("strType");
			strname3 = this.request.getParameter("strText");
		} catch (Exception e) {
			e.printStackTrace();
		}
		nupdateitemtitletextRet = goodService.UpdateItemTitleText(strname1,
				strname2, strname3);
		return SUCCESS;
	}

	public String DeleteMineDynamicInfo() {
		String strname1 = "";
		String strname2 = "";
		String strname3 = "";
		try {
			this.response.setContentType("text/html;charset=utf-8");
			this.response.setCharacterEncoding("UTF-8");
			strname1 = this.request.getParameter("strTaskId");
			strname2 = this.request.getParameter("strType");
			strname3 = this.request.getParameter("strPersonName");
		} catch (Exception e) {
			e.printStackTrace();
		}
		deletedynamicret = goodService.DeleteMineDynamicInfo(strname1,
				strname2, strname3);
		return SUCCESS;
	}

	@Override
	public void setServletResponse(HttpServletResponse arg0) {
		response = arg0;
	}

	@Override
	public void setServletRequest(HttpServletRequest arg0) {
		request = arg0;
	}
}
