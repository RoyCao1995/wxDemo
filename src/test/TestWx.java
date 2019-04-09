package test;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.thoughtworks.xstream.XStream;

import entity.AbstractButtonWX;
import entity.BaseMessage;
import entity.Button;
import entity.ClickButton;
import entity.ImageMessage;
import entity.MusicMessage;
import entity.NewsMessage;
import entity.PhotoOrAlbumBuntton;
import entity.SubButton;
import entity.TextMessage;
import entity.VideoMessage;
import entity.ViewButton;
import entity.VoiceMessage;
import net.sf.json.JSONObject;
import service.WxService;

public class TestWx {
	
	/**
	 * �Ѷ���ת��Ϊһ��json�ַ���
	 */
	@Test
	public void testButton(){
		// �˵�����
		Button btn =new Button();
		//��ӵ�һ��һ���˵�
		btn.getButton().add(new ClickButton("һ�����", "1"));
		//��ӵڶ���һ���˵�
		btn.getButton().add(new ViewButton("һ����ת", "http://www.baidu.com"));
		//����������һ���˵�
		SubButton sb=new SubButton("���Ӳ˵�");
		//Ϊ������һ���˵������Ӳ˵�
		sb.getSubButton().add(new PhotoOrAlbumBuntton("��ͼ", "31"));
		sb.getSubButton().add(new ClickButton("���", "32"));
		sb.getSubButton().add(new ViewButton("��������", "news.163.com"));
		//��������һ���˵�����
		btn.getButton().add(sb);
		//תΪjson
		JSONObject jsonObject = JSONObject.fromObject(btn);
		System.out.println(jsonObject.toString());
		
	}
	
	
	@Test
	public void testAccessToken(){
		System.out.println(WxService.getAccessToken());
		System.out.println(WxService.getAccessToken());
		System.out.println(WxService.getAccessToken());
		System.out.println(WxService.getAccessToken());
		System.out.println(WxService.getAccessToken());
		
	}
	
	
	
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
