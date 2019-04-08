package entity;

import java.util.Map;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("xml")
public class ImageMessage extends BaseMessage {
	private String mediaId;
	
	public String getMediaId() {
		return mediaId;
	}
	public void setMediaId(String mediaId) {
		this.mediaId = mediaId;
	}
	public ImageMessage(Map<String, String> requestMap,String mediaId) {
		//这里必须调用父类
		super(requestMap);
		//设置图片消息的MsgType为image
		this.setMsgType("image");
		this.mediaId=mediaId;
	}
}
