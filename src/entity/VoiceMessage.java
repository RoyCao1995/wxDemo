package entity;

import java.util.Map;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("xml")
public class VoiceMessage extends BaseMessage {
	private String mediaId;
	public String getMediaId() {
		return mediaId;
	}
	public void setMediaId(String mediaId) {
		this.mediaId = mediaId;
	}
	public VoiceMessage(Map<String, String> requestMap,String mediaId) {
		//这里必须调用父类
		super(requestMap);
		//设置语音消息的MsgType为voice
		this.setMsgType("voice");
		this.mediaId=mediaId;
	}
}
