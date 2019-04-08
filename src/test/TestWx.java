package test;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.thoughtworks.xstream.XStream;

import entity.BaseMessage;
import entity.ImageMessage;
import entity.MusicMessage;
import entity.NewsMessage;
import entity.TextMessage;
import entity.VideoMessage;
import entity.VoiceMessage;

public class TestWx {
	/**
	 * 这个test用于测试是否能通过构造方法构造
	 */
	@Test
	public void testMsg(){
		Map<String, String> map=new HashMap<String, String>();
		map.put("ToUserName", "to");
		map.put("FromUserName", "from");
		map.put("MsgType", "type");
		TextMessage tm=new TextMessage(map, "还好");
		
		XStream stream =new XStream();
		//默认是不看注解的，需要加这一句
		//设置需要处理@XStreamAlias("xml")注解的类
		stream.processAnnotations(TextMessage.class);
		stream.processAnnotations(ImageMessage.class);
		stream.processAnnotations(MusicMessage.class);
		stream.processAnnotations(NewsMessage.class);
		stream.processAnnotations(VideoMessage.class);
		stream.processAnnotations(VoiceMessage.class);
		String xml=stream.toXML(tm);
		System.out.println(xml);
	}
}
