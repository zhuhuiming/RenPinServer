package cn.redarmy.service.GoodService;

import java.util.List;

import cn.redarmy.domain.CommentInfo;
import cn.redarmy.domain.CreditAndCharmForTask;
import cn.redarmy.domain.CustomerInfo;
import cn.redarmy.domain.DistanceDetail;
import cn.redarmy.domain.TaskIcon;
import cn.redarmy.domain.TaskInfo;
import cn.redarmy.domain.TaskInfoDetail;
import cn.redarmy.domain.UpdateData;

public interface GoodService {

	// 意见反馈,0表示失败,1表示成功
	public abstract int SendAdviceText(String strAdvice);

	// 获取任务发布信息数据,strType为数据类型,1表示求助,2表示分享
	public abstract List<TaskInfo> GetTaskInfo(String strType);

	// 获取任务发布信息数据(带有人品值与赞值限制),nType为数据类型,1表示求助,2表示分享
	public abstract List<TaskInfoDetail> GetTaskInfoForLimit(String strType);

	// 发布任务信息
	public abstract int AddTaskInfo(
			String strTaskType,// 任务类型,nType为数据类型,1表示求助,2表示分享
			String strRegionName,// 任务执行区域
			String strTaskTitle,// 任务标题
			String strTaskDeatial,// 任务描述
			String strTaskAnnounceTime,// 任务发布时间
			String strTaskImpleTime,// 任务完成时间
			String strTaskAccountName,// 任务发布人名称(昵称)
			String strImplementAccountName,// 任务执行人名称(昵称)
			String strTaskAccountTrueName, // 任务发布人名称
			String strImplementAccountTrueName,// 任务执行人名称
			String strLongitude,// 任务执行区域的经度
			String strLatidude,// 任务执行区域的纬度
			String strTaskAccountIcon,// 任务发布者的图标
			String strLimitTime,
			String strIcon1,// 第一张压缩图片(下面类似)
			String strIcon2, String strIcon3, String strIcon4, String strIcon5,
			String strIcon6);

	// 发布任务信息,添加相应的限制
	public abstract int AddTaskInfoForLimit(
			String strTaskType,// 任务类型,nType为数据类型,1表示求助,2表示分享
			String strRegionName,// 任务执行区域
			String strTaskTitle,// 任务标题
			String strTaskDeatial,// 任务描述
			String strTaskAnnounceTime,// 任务发布时间
			String strTaskImpleTime,// 任务完成时间
			String strTaskAccountName,// 任务发布人名称(昵称)
			String strImplementAccountName,// 任务执行人名称(昵称)
			String strTaskAccountTrueName, // 任务发布人名称
			String strImplementAccountTrueName,// 任务执行人名称
			String strLongitude,// 任务执行区域的经度
			String strLatidude,// 任务执行区域的纬度
			String strTaskAccountIcon,// 任务发布者的图标
			String strLimitTime,
			String strIcon1,// 第一张压缩图片(下面类似)
			String strIcon2, String strIcon3, String strIcon4, String strIcon5,
			String strIcon6, int nCreditValue, int nCharmValue);

	// 发布任务信息,添加相应的限制(添加了区域与城市名称)
	public abstract int AddTaskInfoForLimitNew(
			String strTaskType,// 任务类型,nType为数据类型,1表示求助,2表示分享
			String strRegionName,// 任务执行区域
			String strTaskTitle,// 任务标题
			String strTaskDeatial,// 任务描述
			String strTaskAnnounceTime,// 任务发布时间
			String strTaskImpleTime,// 任务完成时间
			String strTaskAccountName,// 任务发布人名称(昵称)
			String strImplementAccountName,// 任务执行人名称(昵称)
			String strTaskAccountTrueName, // 任务发布人名称
			String strImplementAccountTrueName,// 任务执行人名称
			String strLongitude,// 任务执行区域的经度
			String strLatidude,// 任务执行区域的纬度
			String strTaskAccountIcon,// 任务发布者的图标
			String strLimitTime,
			String strIcon1,// 第一张压缩图片(下面类似)
			String strIcon2, String strIcon3, String strIcon4, String strIcon5,
			String strIcon6, int nCreditValue, int nCharmValue,
			String strDistrictName, String strCityName);

	// 注册用户
	public abstract int RegisterCustomer(String strBase64CustomerIcon,// 用户头像
			String strName, // 用户名称
			String strPassword, // 用户密码
			String strCreditValue,// 人品值
			String strCharmValue,// 赞值
			String strNickName,// 昵称
			String strSex);// 性别

	// 注册用户,返回值0表示成功,2表示账号已存在,其他表示失败
	public abstract int RegisterCustomerDetail(String strBase64CustomerIcon,// 用户头像
			String strName, // 用户名称
			String strPassword, // 用户密码
			String strCreditValue,// 人品值
			String strCharmValue,// 赞值
			String strNickName,// 昵称
			String strSex,// 性别
			String strLargeImage);// 大图片

	// 判断用户名的有效性,如果这是一个新账号,那么就返回1,如果不是返回2,函数调用失败返回0
	public abstract int JudgeCustomerValidity(String strName, // 用户名称
			String strPassword);// 用户密码

	// 登录
	public abstract CustomerInfo LogIn(String strPersonName, String strPassword);

	// 用户拿下某个任务,0表示失败,如果返回1表示任务获取成功,2表示任务已有人拿下,strType为数据类型,1表示求助,2表示分享
	public abstract int SelectTask(String strTaskId, String strPersonName,
			String strType, String strPersonNickName);// 任务id号

	/**************************** 动态信息获取接口(start) ********************************/
	// 获取动态条数
	public abstract List<TaskInfo> GetMsgInfoNum(String strAnnounceName);// 任务发布者名称

	// 获取动态条数(针对多对多评论)
	public abstract List<TaskInfo> GetMsgInfoNumNew(String strAnnounceName);// 任务发布者名称

	// 获取动态条数(针对多对多评论),增加了任务的浏览、赞值、评论条数等数据
	public abstract List<TaskInfoDetail> GetMsgInfoNumNewDetail(
			String strAnnounceName);// 任务发布者名称

	// 获取动态条数(针对多对多评论),增加了任务的浏览、赞值、评论条数等数据
	public abstract List<TaskInfoDetail> GetMsgInfoNumNewDetail1(
			String strAnnounceName);// 任务发布者名称

	// 获取动态条数(针对多对多评论,添加了系统信息,添加了一个ID字符串,这样就不用将数据全部读过来),增加了任务的浏览、赞值、评论条数等数据
	public abstract List<TaskInfoDetail> GetMsgInfoNumNewDetail2(
			String strAnnounceName, String strID);// 任务发布者名称

	/**************************** 动态信息获取接口(end) ********************************/

	// 更新接收状态,这个针对发布者,0表示失败,1表示成功
	// 这里的nValue,1表示没有人接收,2表示有人接收,3表示任务已经接收同时任务发布者已经查看了这个接收
	public abstract int UpdateTaskSelectType(String strAnnounceName,
			int nValue, String strTaskId, String strType);// 任务发布者

	// 更新完成状态,这个针对发布者
	// 这里的nValue,1表示任务没有完成,2表示任务完成,3表示任务完成同时发布者已查看了这个状态,nTaskType为任务类型,1表示求助,2表示分享
	public abstract int UpdateTaskFinishType(int nTaskType,
			String strAnnounceName, int nValue, String strTaskId,
			String strBase64Image, String strCommentContent);// 任务发布者

	// 更新执行者评论状态,这个针对发布者,0表示失败,1表示成功
	// 这里的nValue,1表示没有最新评论,2表示有最新评论,3表示有最新评论同时任务发布者已查看
	public abstract int UpdateTaskImplementCommentType(String strAnnounceName,
			int nValue, String strTaskId, String strType);// 任务发布者

	// 更新执行者评论状态,这个针对发布者,0表示失败,1表示成功(针对多对多评论)
	// 这里的nValue,1表示没有最新评论,2表示有最新评论,3表示有最新评论同时已查看
	public abstract int UpdateCommentType(String strAnnounceName, int nValue,
			String strTaskId, String strType);

	// 更新任务验证状态,这个针对执行者,0表示失败,1表示成功
	// 这里的nValue,1表示没有验收,2表示验收了,3表示验收了同时任务执行者已经查看该状态,nTaskType为任务类型,1表示求助,2表示分享
	public abstract int UpdateTaskVerifiType(int nTaskType,
			String strAnnounceName, int nValue, String strTaskId,
			String strBase64Image, String strCommentContent);// 任务执行者

	// 更新任务发布者评论状态,这个针对执行者,0表示失败,1表示成功
	// 这里的nValue,1表示没有最新评论,2表示有最新评论,3表示有最新评论同时任务执行者已查看
	public abstract int UpdateTaskAnnounceCommentType(String strAnnounceName,
			int nValue, String strTaskId, String strType);// 任务执行者

	// 获取指定任务的所有评论
	public abstract List<CommentInfo> GetCommentsForTask(String strTaskId,
			String strType);

	// 获取指定任务的所有评论(添加了悄悄话,如果是这条评论是悄悄话,那么无关用户就看不到),strCustomerName为当前用户名称
	public abstract List<CommentInfo> GetCommentsForTaskNew(String strTaskId,
			String strType, String strCustomerName);

	// 获取指定任务的所有评论(添加了悄悄话,如果是这条评论是悄悄话,那么无关用户就看不到),strCustomerName为当前用户名称
	// strTime为客户端获得最新评论时间(评论按时间排序的)
	public abstract List<CommentInfo> GetCommentsForTaskNew1(String strTaskId,
			String strType, String strCustomerName, String strTime);

	// 根据获取指定评论中缩略图的放大图片
	public abstract String GetCommentsLargeImage(String strTaskId,
			String strType, int nCommentIndex);

	// 发送评论数据,0表示失败,1表示成功
	public abstract int SendCommentContent(String strTaskId,
			String strCommentPersonName,// 评论人名称
			String strReceiveCommentPersonName,// 接收到评论的人名称
			String strCommentContent,// 评论内容
			String strCommentPersonImage, // 评论者的图标
			String strType, String strCommentPersonNickName,// 评论人名称(昵称)
			String strReceiveCommentPersonNickName// 接收到评论的人名称(昵称)
	);

	// 返回值0表示失败,其他值表示图片索引号
	public abstract int SendCommentImage(String strTaskId,// 任务id号
			String strCommentPersonName,// 评论人名称
			String strReceiveCommentPersonName,// 接收到评论的人名称
			String strCommentPersonImage, String strSmallImage,// 压缩图片
			String strLargeImage,// 放大图片
			String strType,// 任务类型
			String strCommentPersonNickName,// 评论人名称(昵称)
			String strReceiveCommentPersonNickName// 接收到评论的人名称(昵称)
	);

	// 发送评论数据,0表示失败,1表示成功(针对多对多评论)
	public abstract int SendCommentContentNew(String strTaskId,
			String strCommentPersonName,// 评论人名称
			String strReceiveCommentPersonName,// 接收到评论的人名称
			String strCommentContent,// 评论内容
			String strType, String strCommentPersonNickName,// 评论人名称(昵称)
			String strReceiveCommentPersonNickName// 接收到评论的人名称(昵称)
	);

	// 返回值0表示失败,其他值表示图片索引号(针对多对多评论)
	public abstract int SendCommentImageNew(String strTaskId,// 任务id号
			String strCommentPersonName,// 评论人名称
			String strReceiveCommentPersonName,// 接收到评论的人名称
			String strSmallImage,// 压缩图片
			String strLargeImage,// 放大图片
			String strType,// 任务类型
			String strCommentPersonNickName,// 评论人名称(昵称)
			String strReceiveCommentPersonNickName// 接收到评论的人名称(昵称)
	);

	// 发送评论数据,0表示失败,1表示成功(添加了悄悄话功能)
	public abstract int SendCommentContentNewSecret(String strTaskId,
			String strCommentPersonName,// 评论人名称
			String strReceiveCommentPersonName,// 接收到评论的人名称
			String strCommentContent,// 评论内容
			String strType, String strCommentPersonNickName,// 评论人名称(昵称)
			String strReceiveCommentPersonNickName,// 接收到评论的人名称(昵称)
			int nSecretType// 这条评论是不是悄悄话
	);

	// 返回值0表示失败,其他值表示图片索引号(针对多对多评论)
	public abstract String SendCommentImageNewTime(String strTaskId,// 任务id号
			String strCommentPersonName,// 评论人名称
			String strReceiveCommentPersonName,// 接收到评论的人名称
			String strSmallImage,// 压缩图片
			String strLargeImage,// 放大图片
			String strType,// 任务类型
			String strCommentPersonNickName,// 评论人名称(昵称)
			String strReceiveCommentPersonNickName// 接收到评论的人名称(昵称)
	);

	// 发送评论数据,0表示失败,1表示成功(添加了悄悄话功能)
	public abstract String SendCommentContentNewSecretTime(String strTaskId,
			String strCommentPersonName,// 评论人名称
			String strReceiveCommentPersonName,// 接收到评论的人名称
			String strCommentContent,// 评论内容
			String strType, String strCommentPersonNickName,// 评论人名称(昵称)
			String strReceiveCommentPersonNickName,// 接收到评论的人名称(昵称)
			int nSecretType// 这条评论是不是悄悄话
	);

	// 获取更新状态标志,返回UpdateData类型的值nUpdateSignal，-1表示操作失败,0表示不需要更新,1表示需要更新
	public abstract UpdateData GetUpdateSignal(String strCurrentAccountName);// 有数据更新的用户名称(下面函数参数表示的意思相同)
	// 获取更新状态标志,返回UpdateData类型的值nUpdateSignal，-1表示操作失败,0表示不需要更新,1表示需要更新

	// (针对多对多评论)
	public abstract UpdateData GetUpdateSignalNew(String strCurrentAccountName);// 有数据更新的用户名称(下面函数参数表示的意思相同)
	// 设置更新标志,0表示失败,1表示成功

	// (针对多对多评论,包括系统消息)
	public abstract UpdateData GetUpdateSignalNew1(String strCurrentAccountName);// 有数据更新的用户名称(下面函数参数表示的意思相同)
	// 设置更新标志,0表示失败,1表示成功

	public abstract int SetUpdateSignal(String strCurrentAccountName,
			int nSignal);

	// 设置人品值,0表示失败,1表示成功
	public abstract int SetCreditValue(String strPersonName, int nCreditValue);

	// 设置人品值,0表示失败,1表示成功
	public abstract int SetCharmValue(String strPersonName, int nCharmValue);

	// 设置人品值,-1表示失败
	public abstract int GetCreditValue(String strPersonName);

	// 设置人品值,-1表示失败
	public abstract int GetCharmValue(String strPersonName);

	// 增加人品值,nIncrValue表示人品增加值,0表示失败,1表示成功
	public abstract int AddCreditValue(String strPersonName, int nIncrValue);

	// 增加赞值,nIncrValue表示赞值,0表示失败,1表示成功
	public abstract int AddCharmValue(String strPersonName, int nIncrValue);

	// 根据任务id获取该任务的当前剩余时间,-1表示失败(返回值的单位为秒)
	public abstract long GetTaskRemainTime(String strTaskId, String strType);

	// 加载没有到期的求助或分享任务数据,nLimit为一次加载的数据最大条数,nType为类型,1表示求助,
	// 2表示分享,nMaxTaskId表示最多能加载到的任务号
	public abstract List<TaskInfo> LoadTaskData(int nLimit, int nType,
			int nMaxTaskId);

	// 这个函数与上面函数差不多,只是返回值不一样
	public abstract List<TaskInfoDetail> LoadTaskDataForLimit(int nLimit,
			int nType, int nMaxTaskId);

	// 这个函数与上面函数差不多,只是返回值不一样(按时间排序)
	public abstract List<TaskInfoDetail> LoadTaskDataForLimitNew(int nLimit,
			int nType, String strMaxTime);

	// 刷新没有到期的求助或分享任务数据,nLimit为一次加载的数据最大条数,nType为类型,1表示求助,
	// 2表示分享,nMaxTaskId表示目前刷新到的最大任务号
	public abstract List<TaskInfo> UpdateTaskData(int nLimit, int nType,
			int nMaxTaskId);

	// 这个函数与上面函数差不多,只是返回值不一样(按id排序)
	public abstract List<TaskInfoDetail> UpdateTaskDataForLimit(int nLimit,
			int nType, int nMaxTaskId);

	// 更新数据(按发布时间排序)
	public abstract List<TaskInfoDetail> UpdateTaskDataForLimitNew(int nLimit,
			int nType, String strMaxTime);

	// 上传发布时的大图片值,0表示失败,1表示成功
	public abstract int UploadLargeImage(
			String strTaskId,// 任务id号
			String strTaskType,// 任务类型,strTaskType为数据类型,1表示求助,2表示分享
			String strIcon1, String strIcon2, String strIcon3, String strIcon4,
			String strIcon5, String strIcon6);

	// 上传任务执行者发送给发布者的图片或者分享发布者发送给接收者的图片,0表示失败,1表示成功
	public abstract int UploadTaskFinishTypeForLargeImage(String strTaskId,
			String strTaskType,// strTaskType为数据类型,1表示求助,2表示分享
			String strBase64Image);

	// 上传任务发布者发送给任务执行者的图片或者分享接收者发送给分享者的图片,0表示失败,1表示成功
	public abstract int UploadTaskVerifiTypeForLargeImage(String strTaskId,
			String strTaskType,// strTaskType为数据类型,1表示求助,2表示分享
			String strBase64Image);

	// 获取求助或分享相关缩小图片
	public abstract TaskIcon GetTaskSmallIcon(String strTaskId,// 任务id号
			String strTaskType);// 任务类型,1表示求助,2表示分享

	// 获取求助或分享第一张缩小图片
	public abstract String GetFirstSmallIcon(String strTaskId,// 任务id号
			String strTaskType);// 任务类型,1表示求助,2表示分享

	// 获取求助或分享除下第一张以外的缩小图片
	public abstract TaskIcon GetOtherSmallIcon(String strTaskId,// 任务id号
			String strTaskType);// 任务类型,1表示求助,2表示分享

	// 获取求助或分享相关放大图片

	public abstract String GetTaskLargeIcon(String strTaskId,// 任务id
			String strTaskType,// 任务类型
			int nIconId);// 任务放大图片id号,索引号从1开始
	// 获取任务执行者发送给发布者的大图片或者分享者发送给接收者的大图片

	public abstract String GetTaskFinishLargeImage(String strTaskId,// 任务id
			String strTaskType);// 同上
	// 获取任务发布者发送给执行者的大图片获取接收者发送给分享者的大图片

	public abstract String GetTaskVerifiLargeImage(String strTaskId,// 任务id
			String strTaskType);// 同上

	// 将任务或分享设置为没有被拿下或抢下状态
	public abstract int ResetTaskStatue(String strTaskId, String strType);// 任务id号
	// 记录该任务或分享获得的人品值与赞值,0表示失败,1表示成功

	public abstract int RecordCreditAndCharmForTask(String strTaskId,
			String strType, int nCredit,// 人品值
			int nCharm);// 赞值

	// 返回某个任务或分享中获得的人品值和赞值
	public abstract CreditAndCharmForTask GetCreditAndCharmForTask(
			String strTaskId, String strType);

	// 修改用户头像,0表示失败,1表示成功,2表示不存在该用户
	public abstract int ChangePersonImage(String strPersonName,
			String strSmallImage, String strLargeImage);

	public List<DistanceDetail> execProcQuery(final String procName,
			final Object[] params, final String[] resultParams);

	// 获取距离小于dist的前30条数据(求助)
	List<DistanceDetail> getHelpNearData(Double longitude, Double latitude,
			Double dist, String strDistrictName);

	// 获取距离小于dist的前30条数据(分享)
	List<DistanceDetail> getShareNearData(Double longitude, Double latitude,
			Double dist, String strDistrictName);

	// 获取距离大于dist的前30条数据(求助)
	List<DistanceDetail> LoadHelpNearData(Double longitude, Double latitude,
			Double dist, String strDistrictName, String strTime);

	// 获取距离大于dist的前30条数据(分享)
	List<DistanceDetail> LoadShareNearData(Double longitude, Double latitude,
			Double dist, String strDistrictName, String strTime);

	// 对某个任务或分享点赞,0表示操作失败,1表示成功,2表示已经点过赞
	public abstract int PraiseToTaskOrShare(String strTaskId,
			String strPersonName, // 给赞人的名称
			String strReceivePersonName,// 接收赞的人名
			String strType);

	// 添加浏览次数,0表示失败,其他值表示浏览次数
	public abstract int AddBrowseTimes(String strTaskId, String strType);

	// 修改文本信息,0表示操作失败,1表示成功
	public abstract int UpdateItemDetailText(String strTaskId, String strType,
			String strText);

	// 修改标题信息,0表示操作失败,1表示成功
	public abstract int UpdateItemTitleText(String strTaskId, String strType,
			String strText);

	// 删除评论记录,这样"我的"中就收不到相应的数据了,0表示操作失败,1表示成功
	public abstract int DeleteMineDynamicInfo(String strTaskId, String strType,
			String strPersonName);
}
