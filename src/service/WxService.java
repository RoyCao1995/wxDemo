package service;

import static org.hamcrest.CoreMatchers.nullValue;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletInputStream;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.baidu.aip.ocr.AipOcr;
import com.thoughtworks.xstream.XStream;

import entity.AccessToken;
import entity.Article;
import entity.BaseMessage;
import entity.ImageMessage;
import entity.MusicMessage;
import entity.NewsMessage;
import entity.TextMessage;
import entity.VideoMessage;
import entity.VoiceMessage;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import util.Util;

public class WxService {
	// 聚合数据-聊天机器人：与接口配置信息中的Token要一致,自己配置
	private static String token = "photoshop_xatu";
	private static String APPKEY = "1fec136dbd19f44743803f89bd55ca62";

	// 微信公众号 access_token：url
	private static final String GET_TOKEN_URL="https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
	private static final String APPID="wx5ee32410401e3e90";
	private static final String APPSECRET="38cbe17852a2e3b7531692dbec29f51a";//这些应该写在配置文件里面
	//百度AI
    public static final String APP_ID = "15975666";
    public static final String API_KEY = "PWhOtG6IFLMGPFa1GRLjf9pH";
    public static final String SECRET_KEY = "QOwot9jeYERWGXNdQblTVs6U2xiaUeGd";
    
	// 用于存储access_token
	private static AccessToken at;

	/**
	 * 获取一个access_token，不对外界提供返回值
	 */
	private static void getToken(){
		//生成微信服务器获取access_token的微信接口
		String url=GET_TOKEN_URL.replace("APPID", APPID).replace("APPSECRET", APPSECRET);
		String tokenStr = Util.get(url);//这里获取的access_token是一个json字符串，包含着有效期2小时
		//将json封装成一个对象
		JSONObject jsonObject = JSONObject.fromObject(tokenStr);
		String accessToken = jsonObject.getString("access_token");
		String expireIn = jsonObject.getString("expires_in");
		// 创建access_token对象，并存起来
		at=new AccessToken(accessToken, expireIn);
	}


	public static String getAccessToken() {
		//保证一定有一个token
		if (at==null||at.isExpire()) {
			getToken();
		}
		return at.getAccessToken();
	}

	/**
	 * 验证签名
	 *
	 * @param signature
	 * @param timestamp
	 * @param nonce
	 * @return
	 */
	public static boolean check(String signature, String timestamp, String nonce) {

		// 1）将token、timestamp、nonce三个参数进行字典序排序
		String[] arr = new String[] { token, timestamp, nonce };
		Arrays.sort(arr);
		// 2）将三个参数字符串拼接成一个字符串进行sha1加密I
		String mixStr = arr[0] + arr[1] + arr[2];
		String mySig = sha1(mixStr);
		// 3）开发者获得加密后的字符串可与signature对比，标识该请求来源于微信
		return mySig.equalsIgnoreCase(signature);
	}

	/**
	 * 进行sha1加密
	 *
	 * @param mixStr
	 * @return
	 */
	private static String sha1(String mixStr) {

		try {
			// 获取一个加密对象
			MessageDigest md = MessageDigest.getInstance("sha1");
			// 加密
			byte[] digest = md.digest(mixStr.getBytes());
			char[] chars = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
			StringBuilder sb = new StringBuilder();
			// 处理加密结果
			for (byte b : digest) {
				sb.append(chars[(b >> 4) & 15]);
				sb.append(chars[b & 15]);
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 将字节转换为十六进制字符串
	 *
	 * @param mByte
	 * @return
	 */
	private static String byteToHexStr(byte mByte) {
		char[] Digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		char[] tempArr = new char[2];
		tempArr[0] = Digit[(mByte >>> 4) & 0X0F];
		tempArr[1] = Digit[mByte & 0X0F];

		String s = new String(tempArr);
		return s;
	}

	/**
	 * 解析XML数据包
	 *
	 * @param is
	 * @return
	 */
	public static Map<String, String> parseRequest(ServletInputStream is) {
		Map<String, String> map = new HashMap<>();
		SAXReader reader = new SAXReader();
		try {
			// 读取输入流，获取文档对象
			Document document = reader.read(is);
			// 根据文档对象获取根节点：<xml>
			Element root = document.getRootElement();
			// 获取根节点的所有子节点
			List<Element> elements = root.elements();
			for (Element element : elements) {
				map.put(element.getName(), element.getStringValue());
			}
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * 用于处理所有的事件和消息的回复
	 *
	 * @param requestMap
	 * @return 返回的是回复给用户的XML数据包
	 */
	public static String getResponse(Map<String, String> requestMap) {
		BaseMessage msg = null;
		// 这个requestMap不一定是文本消息
System.out.println("get-response->requestMap:"+requestMap);
//这里的requestMap：ok
		String MsgType = requestMap.get("MsgType");

		switch (MsgType) {
		case "text":
System.out.println("getResponse->MsgType:"+MsgType);
			// 文本消息
			msg = dealTextMessage(requestMap);
			System.out.println("get-msg:" + msg);
			break;
		case "image":
			// 图片消息
			msg=dealImage(requestMap);
			break;
		case "voice":
			// 语音消息

			break;
		case "video":
			// 文本消息

			break;
		case "shortvideo":
			// 文本消息

			break;
		case "location":
			// 文本消息

			break;
		case "link":
			// 文本消息

			break;
		case "event":
			// 事件
			msg=dealEvent(requestMap);
			break;
		default:
			break;
		}
		// 把java消息对象处理为xml数据包
		if (msg != null) {
			// 防止没有进行处理，可能类型不对
			return beanToXml(msg);
		}
		return null;

	}
	
	
	/**
	 * 进行图片识别
	 * @param requestMap
	 * @return
	 */
	private static BaseMessage dealImage(Map<String, String> requestMap) {
		// 初始化一个AipOcr
        AipOcr client = new AipOcr(APP_ID, API_KEY, SECRET_KEY);

        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);
        // 调用接口
        String path = requestMap.get("PicUrl");
//        org.json.JSONObject res = client.basicGeneral(path, new HashMap<String, String>());
        
        //设置进行网络图片的识别
        org.json.JSONObject res = client.generalUrl(path, new HashMap<String, String>());
        
        String json = res.toString();
        //将识别出来的结果转为json object
        JSONObject jsonObject = JSONObject.fromObject(json);
        JSONArray jsonArray = jsonObject.getJSONArray("words_result");
        Iterator<JSONObject> it = jsonArray.iterator();
        StringBuilder sb=new StringBuilder();
        while (it.hasNext()) {
        	JSONObject next = it.next();
        	sb.append(next.getString("words"));
		}
		return new TextMessage(requestMap, sb.toString());
	}


	/**
	 * 专门用来处理事件推送的
	 * 注：自定义菜单事件只是事件之一
	 * @param requestMap
	 * @return
	 */
	private static BaseMessage dealEvent(Map<String, String> requestMap) {
		String event = requestMap.get("Event");
		switch (event) {
		case "CLICK":
			return dealClick(requestMap);
		case "VIEW":
			return dealView(requestMap);

		default:
			break;
		}
		return null;
	}

	/**
	 * 专门处理view类型的按钮
	 * @param requestMap
	 * @return
	 */
	private static BaseMessage dealView(Map<String, String> requestMap) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 处理click类型的按钮
	 * 因为click可能有好几种，所以需要取出EventKey来判断这个click来自于哪个按钮的点击
	 * @param requestMap
	 * @return
	 */
	private static BaseMessage dealClick(Map<String, String> requestMap) {
		String key = requestMap.get("EventKey");
		switch (key) {
		case "1":
			//处理点击了第一个一级菜单
			return new TextMessage(requestMap, "你点了一下第一个一级菜单");
		case "32":
			//处理点击了第三个一级菜单的第二个子菜单
			
			
		default:
			break;
		}
		return null;
	}


	/**
	 * 把java消息对象处理为xml数据包
	 *
	 * @param msg
	 * @return
	 */
	private static String beanToXml(BaseMessage msg) {
		XStream stream = new XStream();
		// 默认是不看注解的，需要加这一句
		// 设置需要处理@XStreamAlias("xml")注解的类
		stream.processAnnotations(TextMessage.class);
		stream.processAnnotations(ImageMessage.class);
		stream.processAnnotations(MusicMessage.class);
		stream.processAnnotations(NewsMessage.class);
		stream.processAnnotations(VideoMessage.class);
		stream.processAnnotations(VoiceMessage.class);
		String xml = stream.toXML(msg);
		return xml;
	}

	/**
	 * 处理文本消息
	 *
	 * @param requestMap
	 * @return
	 */
	private static BaseMessage dealTextMessage(Map<String, String> requestMap) {

System.out.println("dealTextMessage->requestMap:" + requestMap);
		// 用户发来的内容
		String msg = requestMap.get("Content");
System.out.println("dealTextMessage->msg:" + msg);
		if ("图文".equals(msg)) {
			List<Article> articles = new ArrayList<Article>();
			articles.add(new Article("这是图文消息的标题", "这是图文消息的详细介绍",
					"https://code.juhe.cn/Public/V2/images/freecode.png?v=1.1", "www.baidu.com"));
			NewsMessage nm = new NewsMessage(requestMap, articles);
System.out.println("11111");
			return nm;
		}

		// 调用方法返回聊天内容
		String resp = chat(msg);

		TextMessage tm = new TextMessage(requestMap, resp);
		return tm;
	}

	/**
	 * 调用图灵机器人聊天
	 *
	 * @param msg
	 *            用户发送的消息
	 * @return
	 */
	private static String chat(String msg) {
		// 1.问答
		String result = null;
		String url = "http://op.juhe.cn/robot/index";// 请求接口地址
		Map params = new HashMap();// 请求参数
		params.put("key", APPKEY);// 您申请到的本接口专用的APPKEY
		params.put("info", msg);// 要发送给机器人的内容，不要超过30个字符
		params.put("dtype", "");// 返回的数据的格式，json或xml，默认为json
		params.put("loc", "");// 地点，如北京中关村
		params.put("lon", "");// 经度，东经116.234632（小数点后保留6位），需要写为116234632
		params.put("lat", "");// 纬度，北纬40.234632（小数点后保留6位），需要写为40234632
		params.put("userid", "");// 1~32位，此userid针对您自己的每一个用户，用于上下文的关联

		try {
			result = Util.net(url, params, "GET");
			System.out.println(result);
			// 解析json
			JSONObject json0bject = JSONObject.fromObject(result);
			// 取出error_code
			int code = json0bject.getInt("error_code");
			if (code != 0) {
				return null;
			}
			// 取出返回的消息的内容
			String resp = json0bject.getJSONObject("result").getString("text");
			return resp;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 上传临时素材
	 * @param path：上传文件的路径
	 * @param type：上传文件的类型
	 * @return
	 */
	public static String upload(String path,String type) {
		File file=new File(path);
		//地址
		String url="https://api.weixin.qq.com/cgi-bin/media/upload?access_token=ACCESS_TOKEN&type=TYPE";
		url.replace("ACCESS_TOKEN", getAccessToken()).replace("TYPE", type);
		try {
			URL urlObj=new URL(url);
			//强转为安全链接：https
			HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
			//设置连接的信息
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);
			//设置请求头信息
			connection.setRequestProperty("Connection", "Keep-Alive");
			connection.setRequestProperty("Charset", "utf8");
			//数据边界
			String boundary="-----"+System.currentTimeMillis();
			connection.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
			//获取输出流
			OutputStream os = connection.getOutputStream();
			//创建文件的输入流
			InputStream is=new FileInputStream(file);
			//第一部分：头部信息
			//	准备头部信息
			StringBuilder sb=new StringBuilder();
			sb.append("--");
			sb.append(boundary);
			sb.append("\r\n");
			sb.append("Content-Disposition:form-data;name=media;filename="+file.getName());
			sb.append("Content-Type:application/octet-stream\r\n\r\n");
			os.write(sb.toString().getBytes());
			os.flush();
			os.close();
			//第二部分：文件内容
			byte[] b=new byte[1024];
			int len;
			while ((len=is.read(b))!=-1) {
				os.write(b, 0, len);
			}
			//第三部分：尾部信息
			String foot="\r\n--"+boundary+"--\r\n";
			os.write(foot.getBytes());
			os.flush();
			os.close();
			//读取数据
			InputStream is2=connection.getInputStream();
			StringBuilder resp=new StringBuilder();
			while ((len=is2.read(b))!=-1) {
				resp.append(new String(b, 0, len));
			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return type;
	}
}
