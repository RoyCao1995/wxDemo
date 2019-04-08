package service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletInputStream;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.thoughtworks.xstream.XStream;

import entity.Article;
import entity.BaseMessage;
import entity.ImageMessage;
import entity.MusicMessage;
import entity.NewsMessage;
import entity.TextMessage;
import entity.VideoMessage;
import entity.VoiceMessage;
import net.sf.json.JSONObject;
import util.Util;

public class WxService {
	// ��ӿ�������Ϣ�е�TokenҪһ��,�Լ�����
	private static String token = "photoshop_xatu";
	private static String APPKEY = "1fec136dbd19f44743803f89bd55ca62";

	/**
	 * ��֤ǩ��
	 * 
	 * @param signature
	 * @param timestamp
	 * @param nonce
	 * @return
	 */
	public static boolean check(String signature, String timestamp, String nonce) {

		// 1����token��timestamp��nonce�������������ֵ�������
		String[] arr = new String[] { token, timestamp, nonce };
		Arrays.sort(arr);
		// 2�������������ַ���ƴ�ӳ�һ���ַ�������sha1����I
		String mixStr = arr[0] + arr[1] + arr[2];
		String mySig = sha1(mixStr);
		// 3�������߻�ü��ܺ���ַ�������signature�Աȣ���ʶ��������Դ��΢��
		return mySig.equalsIgnoreCase(signature);
	}

	/**
	 * ����sha1����
	 * 
	 * @param mixStr
	 * @return
	 */
	private static String sha1(String mixStr) {

		try {
			// ��ȡһ�����ܶ���
			MessageDigest md = MessageDigest.getInstance("sha1");
			// ����
			byte[] digest = md.digest(mixStr.getBytes());
			char[] chars = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
			StringBuilder sb = new StringBuilder();
			// ������ܽ��
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
	 * ���ֽ�ת��Ϊʮ�������ַ���
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
	 * ����XML���ݰ�
	 * 
	 * @param is
	 * @return
	 */
	public static Map<String, String> parseRequest(ServletInputStream is) {
		Map<String, String> map = new HashMap<>();
		SAXReader reader = new SAXReader();
		try {
			// ��ȡ����������ȡ�ĵ�����
			Document document = reader.read(is);
			// �����ĵ������ȡ���ڵ㣺<xml>
			Element root = document.getRootElement();
			// ��ȡ���ڵ�������ӽڵ�
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
	 * ���ڴ������е��¼�����Ϣ�Ļظ�
	 * 
	 * @param requestMap
	 * @return ���ص��ǻظ����û���XML���ݰ�
	 */
	public static String getResponse(Map<String, String> requestMap) {
		BaseMessage msg = null;
		// ���requestMap��һ�����ı���Ϣ
System.out.println("get-response->requestMap:"+requestMap);	
//�����requestMap��ok
		String MsgType = requestMap.get("MsgType");
		
		switch (MsgType) {
		case "text":
System.out.println("getResponse->MsgType:"+MsgType);
			// �ı���Ϣ
			msg = dealTextMessage(requestMap);
			System.out.println("get-msg:" + msg);
			break;
		case "image":
			// �ı���Ϣ

			break;
		case "voice":
			// �ı���Ϣ

			break;
		case "video":
			// �ı���Ϣ

			break;
		case "shortvideo":
			// �ı���Ϣ

			break;
		case "location":
			// �ı���Ϣ

			break;
		case "link":
			// �ı���Ϣ

			break;

		default:
			break;
		}
		// ��java��Ϣ������Ϊxml���ݰ�
		if (msg != null) {
			// ��ֹû�н��д����������Ͳ���
			return beanToXml(msg);
		}
		return null;

	}

	/**
	 * ��java��Ϣ������Ϊxml���ݰ�
	 * 
	 * @param msg
	 * @return
	 */
	private static String beanToXml(BaseMessage msg) {
		XStream stream = new XStream();
		// Ĭ���ǲ���ע��ģ���Ҫ����һ��
		// ������Ҫ����@XStreamAlias("xml")ע�����
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
	 * �����ı���Ϣ
	 * 
	 * @param requestMap
	 * @return
	 */
	private static BaseMessage dealTextMessage(Map<String, String> requestMap) {
	
System.out.println("dealTextMessage->requestMap:" + requestMap);
		// �û�����������
		String msg = requestMap.get("Content");
System.out.println("dealTextMessage->msg:" + msg);
		if ("ͼ��".equals(msg)) {
			List<Article> articles = new ArrayList<Article>();
			articles.add(new Article("����ͼ����Ϣ�ı���", "����ͼ����Ϣ����ϸ����",
					"https://code.juhe.cn/Public/V2/images/freecode.png?v=1.1", "www.baidu.com"));
			NewsMessage nm = new NewsMessage(requestMap, articles);
System.out.println("11111");
			return nm;
		}

		// ���÷���������������
		String resp = chat(msg);

		TextMessage tm = new TextMessage(requestMap, resp);
		return tm;
	}

	/**
	 * ����ͼ�����������
	 * 
	 * @param msg
	 *            �û����͵���Ϣ
	 * @return
	 */
	private static String chat(String msg) {
		// 1.�ʴ�
		String result = null;
		String url = "http://op.juhe.cn/robot/index";// ����ӿڵ�ַ
		Map params = new HashMap();// �������
		params.put("key", APPKEY);// �����뵽�ı��ӿ�ר�õ�APPKEY
		params.put("info", msg);// Ҫ���͸������˵����ݣ���Ҫ����30���ַ�
		params.put("dtype", "");// ���ص����ݵĸ�ʽ��json��xml��Ĭ��Ϊjson
		params.put("loc", "");// �ص㣬�籱���йش�
		params.put("lon", "");// ���ȣ�����116.234632��С�������6λ������ҪдΪ116234632
		params.put("lat", "");// γ�ȣ���γ40.234632��С�������6λ������ҪдΪ40234632
		params.put("userid", "");// 1~32λ����userid������Լ���ÿһ���û������������ĵĹ���

		try {
			result = Util.net(url, params, "GET");
			System.out.println(result);
			// ����json
			JSONObject json0bject = JSONObject.fromObject(result);
			// ȡ��error_code
			int code = json0bject.getInt("error_code");
			if (code != 0) {
				return null;
			}
			// ȡ�����ص���Ϣ������
			String resp = json0bject.getJSONObject("result").getString("text");
			return resp;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
