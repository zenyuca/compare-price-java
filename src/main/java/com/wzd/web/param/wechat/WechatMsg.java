package com.wzd.web.param.wechat;

import java.io.Serializable;
import java.util.List;

import com.alibaba.fastjson.JSON;

/**
 * 来自Wechat端的消息(所有字段)
 * 
 * @author WeiZiDong
 *
 */
@SuppressWarnings("serial")
public class WechatMsg implements Serializable {
	private String ToUserName; // 开发者微信号
	private String FromUserName; // 发送方帐号（一个OpenID）
	private Long CreateTime; // 消息创建时间 （整型）
	private Long MsgId; // 消息id，64位整型
	private String MsgType; // 消息类型
	private Integer AgentID; // 企业应用的id，整型。可在应用的设置页面查看
	/** ==========text **/
	private String Content; // 文本消息内容
	/** ==========image **/
	private String PicUrl; // 图片链接
	private String MediaId; // 图片消息媒体id，可以调用多媒体文件下载接口拉取数据。
	/** ==========voice **/
	private String Format; // 语音格式，如amr，speex等
	private String Recognition; // 语音识别结果，使用UTF8编码。
	/** ==========video/shortvideo **/
	private String ThumbMediaId; // 视频消息缩略图的媒体id，可以调用多媒体文件下载接口拉取数据。
	/** ==========location **/
	private Double Location_X; // 地理位置维度
	private Double Location_Y; // 地理位置经度
	private Integer Scale; // 地图缩放大小
	private String Label; // 地理位置信息
	/** ==========link **/
	private String Title; // 消息标题
	private String Description; // 消息描述
	private String Url; // 消息链接
	/** ==========事件类型 **/
	private String Event; // 事件类型
	/** ==========事件KEY值 **/
	private String EventKey; // 事件KEY值，qrscene_为前缀，后面为二维码的参数值
	private String MenuId; // 点击事件的菜单ID
	/** ==========SCAN/subscribe **/
	private String Ticket; // 二维码的ticket，可用来换取二维码图片
	private String ScanCodeInfo; // 二扫描信息
	private String ScanType; // 扫描类型，一般是qrcode
	private String ScanResult; // 扫描结果，即二维码对应的字符串信息
	/** ==========pic_sysphoto/pic_photo_or_album/pic_weixin **/
	private String SendPicsInfo; // 发送的图片信息
	private Integer Count; // 发送的图片数量
	private String PicList; // 图片列表
	private String PicMd5Sum; // 图片的MD5值，开发者若需要，可用于验证接收到图片
	/** ==========LOCATION/location_select **/
	private Double Latitude; // 地理位置纬度
	private Double Longitude; // 地理位置经度
	private Double Precision; // 地理位置精度
	private Double Poiname; // 朋友圈POI的名字，可能为空
	private Double SendLocationInfo; // 发送的位置信息
	/** ==========batch_job_result **/
	private String JobId; // 异步任务id，最大长度为64字符
	private String JobType; // 操作类型，字符串，目前分别有：
							// 1. sync_user(增量更新成员)
							// 2.replace_user(全量覆盖成员)
							// 3. invite_user(邀请成员关注)
							// 4.replace_party(全量覆盖部门)
	private String ErrCode; // 返回码
	private String ErrMsg; // 对返回码的文本描述内容
	/** ==========通讯录变更 **/
	private String ChangeType; // 变更类型
	private Integer Id; // id
	private Integer ParentId; // 用户id
	private String UserID; // 用户id
	private String Name; // 姓名
	private String Position; // 姓名
	private String Mobile; // 电话
	private String Email; // 邮件
	private String Avatar; // 头像
	private String Weixinid; // 头像
	private String Department; // 部门
	private List<Item> ExtAttr; // 扩展字段
	private String Gender; // 性别
	private Integer Status; // 性别
	private Integer Order; // 性别
	private Integer WxPlugin_Status;
	private Long ExpiredTime;

	public Long getExpiredTime() {
		return ExpiredTime;
	}

	public void setExpiredTime(Long expiredTime) {
		ExpiredTime = expiredTime;
	}

	public Integer getWxPlugin_Status() {
		return WxPlugin_Status;
	}

	public void setWxPlugin_Status(Integer wxPlugin_Status) {
		WxPlugin_Status = wxPlugin_Status;
	}

	public Integer getOrder() {
		return Order;
	}

	public void setOrder(Integer order) {
		Order = order;
	}

	public String getWeixinid() {
		return Weixinid;
	}

	public void setWeixinid(String weixinid) {
		Weixinid = weixinid;
	}

	public String getPosition() {
		return Position;
	}

	public void setPosition(String position) {
		Position = position;
	}

	public Integer getStatus() {
		return Status;
	}

	public void setStatus(Integer status) {
		Status = status;
	}

	public String getDepartment() {
		return Department;
	}

	public void setDepartment(String department) {
		Department = department;
	}

	public String getToUserName() {
		return ToUserName;
	}

	public void setToUserName(String toUserName) {
		ToUserName = toUserName;
	}

	public Integer getId() {
		return Id;
	}

	public void setId(Integer id) {
		Id = id;
	}

	public Integer getParentId() {
		return ParentId;
	}

	public void setParentId(Integer parentId) {
		ParentId = parentId;
	}

	public String getFromUserName() {
		return FromUserName;
	}

	public void setFromUserName(String fromUserName) {
		FromUserName = fromUserName;
	}

	public Long getCreateTime() {
		return CreateTime;
	}

	public void setCreateTime(Long createTime) {
		CreateTime = createTime;
	}

	public Long getMsgId() {
		return MsgId;
	}

	public void setMsgId(Long msgId) {
		MsgId = msgId;
	}

	public String getMsgType() {
		return MsgType;
	}

	public void setMsgType(String msgType) {
		MsgType = msgType;
	}

	public Integer getAgentID() {
		return AgentID;
	}

	public void setAgentID(Integer agentID) {
		AgentID = agentID;
	}

	public String getContent() {
		return Content;
	}

	public void setContent(String content) {
		Content = content;
	}

	public String getPicUrl() {
		return PicUrl;
	}

	public void setPicUrl(String picUrl) {
		PicUrl = picUrl;
	}

	public String getMediaId() {
		return MediaId;
	}

	public void setMediaId(String mediaId) {
		MediaId = mediaId;
	}

	public String getFormat() {
		return Format;
	}

	public void setFormat(String format) {
		Format = format;
	}

	public String getRecognition() {
		return Recognition;
	}

	public void setRecognition(String recognition) {
		Recognition = recognition;
	}

	public String getThumbMediaId() {
		return ThumbMediaId;
	}

	public void setThumbMediaId(String thumbMediaId) {
		ThumbMediaId = thumbMediaId;
	}

	public Double getLocation_X() {
		return Location_X;
	}

	public void setLocation_X(Double location_X) {
		Location_X = location_X;
	}

	public Double getLocation_Y() {
		return Location_Y;
	}

	public void setLocation_Y(Double location_Y) {
		Location_Y = location_Y;
	}

	public Integer getScale() {
		return Scale;
	}

	public void setScale(Integer scale) {
		Scale = scale;
	}

	public String getLabel() {
		return Label;
	}

	public void setLabel(String label) {
		Label = label;
	}

	public String getTitle() {
		return Title;
	}

	public void setTitle(String title) {
		Title = title;
	}

	public String getDescription() {
		return Description;
	}

	public void setDescription(String description) {
		Description = description;
	}

	public String getUrl() {
		return Url;
	}

	public void setUrl(String url) {
		Url = url;
	}

	public String getEvent() {
		return Event;
	}

	public void setEvent(String event) {
		Event = event;
	}

	public String getEventKey() {
		return EventKey;
	}

	public void setEventKey(String eventKey) {
		EventKey = eventKey;
	}

	public String getMenuId() {
		return MenuId;
	}

	public void setMenuId(String menuId) {
		MenuId = menuId;
	}

	public String getTicket() {
		return Ticket;
	}

	public void setTicket(String ticket) {
		Ticket = ticket;
	}

	public String getScanCodeInfo() {
		return ScanCodeInfo;
	}

	public void setScanCodeInfo(String scanCodeInfo) {
		ScanCodeInfo = scanCodeInfo;
	}

	public String getScanType() {
		return ScanType;
	}

	public void setScanType(String scanType) {
		ScanType = scanType;
	}

	public String getScanResult() {
		return ScanResult;
	}

	public void setScanResult(String scanResult) {
		ScanResult = scanResult;
	}

	public String getSendPicsInfo() {
		return SendPicsInfo;
	}

	public void setSendPicsInfo(String sendPicsInfo) {
		SendPicsInfo = sendPicsInfo;
	}

	public Integer getCount() {
		return Count;
	}

	public void setCount(Integer count) {
		Count = count;
	}

	public String getPicList() {
		return PicList;
	}

	public void setPicList(String picList) {
		PicList = picList;
	}

	public String getPicMd5Sum() {
		return PicMd5Sum;
	}

	public void setPicMd5Sum(String picMd5Sum) {
		PicMd5Sum = picMd5Sum;
	}

	public Double getLatitude() {
		return Latitude;
	}

	public void setLatitude(Double latitude) {
		Latitude = latitude;
	}

	public Double getLongitude() {
		return Longitude;
	}

	public void setLongitude(Double longitude) {
		Longitude = longitude;
	}

	public Double getPrecision() {
		return Precision;
	}

	public void setPrecision(Double precision) {
		Precision = precision;
	}

	public Double getPoiname() {
		return Poiname;
	}

	public void setPoiname(Double poiname) {
		Poiname = poiname;
	}

	public Double getSendLocationInfo() {
		return SendLocationInfo;
	}

	public void setSendLocationInfo(Double sendLocationInfo) {
		SendLocationInfo = sendLocationInfo;
	}

	public String getJobId() {
		return JobId;
	}

	public void setJobId(String jobId) {
		JobId = jobId;
	}

	public String getJobType() {
		return JobType;
	}

	public void setJobType(String jobType) {
		JobType = jobType;
	}

	public String getErrCode() {
		return ErrCode;
	}

	public void setErrCode(String errCode) {
		ErrCode = errCode;
	}

	public String getErrMsg() {
		return ErrMsg;
	}

	public void setErrMsg(String errMsg) {
		ErrMsg = errMsg;
	}

	public String getChangeType() {
		return ChangeType;
	}

	public void setChangeType(String changeType) {
		ChangeType = changeType;
	}

	public String getUserID() {
		return UserID;
	}

	public void setUserID(String userID) {
		UserID = userID;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getMobile() {
		return Mobile;
	}

	public void setMobile(String mobile) {
		Mobile = mobile;
	}

	public String getEmail() {
		return Email;
	}

	public void setEmail(String email) {
		Email = email;
	}

	public String getAvatar() {
		return Avatar;
	}

	public void setAvatar(String avatar) {
		Avatar = avatar;
	}

	public List<Item> getExtAttr() {
		return ExtAttr;
	}

	public void setExtAttr(List<Item> extAttr) {
		ExtAttr = extAttr;
	}

	public String getGender() {
		return Gender;
	}

	public void setGender(String gender) {
		Gender = gender;
	}

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}

}
