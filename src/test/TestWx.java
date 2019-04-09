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
	 * 把对象转换为一个json字符串
	 */
	@Test
	public void testButton(){
		// 菜单对象
		Button btn =new Button();
		//添加第一个一级菜单
		btn.getButton().add(new ClickButton("一级点击", "1"));
		//添加第二个一级菜单
		btn.getButton().add(new ViewButton("一级跳转", "http://www.baidu.com"));
		//创建第三个一级菜单
		SubButton sb=new SubButton("有子菜单");
		//为第三个一级菜单增加子菜单
		sb.getSubButton().add(new PhotoOrAlbumBuntton("传图", "31"));
		sb.getSubButton().add(new ClickButton("点击", "32"));
		sb.getSubButton().add(new ViewButton("网易新闻", "news.163.com"));
		//将第三个一级菜单加入
		btn.getButton().add(sb);
		//转为json
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
