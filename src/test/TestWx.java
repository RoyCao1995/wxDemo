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
	 * ���test���ڲ����Ƿ���ͨ�����췽������
	 */
	@Test
	public void testMsg(){
		Map<String, String> map=new HashMap<String, String>();
		map.put("ToUserName", "to");
		map.put("FromUserName", "from");
		map.put("MsgType", "type");
		TextMessage tm=new TextMessage(map, "����");
		
		XStream stream =new XStream();
		//Ĭ���ǲ���ע��ģ���Ҫ����һ��
		//������Ҫ����@XStreamAlias("xml")ע�����
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
