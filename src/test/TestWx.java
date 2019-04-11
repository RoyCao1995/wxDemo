package test;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.baidu.aip.ocr.AipOcr;
import com.thoughtworks.xstream.XStream;

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
	//设置APPID/AK/SK
    public static final String APP_ID = "15975666";
    public static final String API_KEY = "PWhOtG6IFLMGPFa1GRLjf9pH";
    public static final String SECRET_KEY = "QOwot9jeYERWGXNdQblTVs6U2xiaUeGd";
    
	@Test
	public void testPic() {
		
		// 初始化一个AipOcr
        AipOcr client = new AipOcr(APP_ID, API_KEY, SECRET_KEY);

        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);

        // 可选：设置代理服务器地址, http和socket二选一，或者均不设置
//        client.setHttpProxy("proxy_host", proxy_port);  // 设置http代理
//        client.setSocketProxy("proxy_host", proxy_port);  // 设置socket代理

        // 可选：设置log4j日志输出格式，若不设置，则使用默认配置
        // 也可以直接通过jvm启动参数设置此环境变量
//        System.setProperty("aip.log4j.conf", "path/to/your/log4j.properties");

        // 调用接口
        String path = "/wxDemo/src/test/2.png";
        org.json.JSONObject res = client.basicGeneral(path, new HashMap<String, String>());
        System.out.println(res.toString(2));
	}

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
		sb.getSub_button().add(new PhotoOrAlbumBuntton("传图", "31"));
		sb.getSub_button().add(new ClickButton("点击", "32"));
		sb.getSub_button().add(new ViewButton("网易新闻", "news.163.com"));
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
