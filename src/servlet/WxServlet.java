package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Time;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import service.WxService;

public class WxServlet extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		/**
		 * signature 微信加密签名，signature结合了开发者填写的token参数和请求中的timestamp参数、nonce参数。
		 * timestamp 时间戳 nonce 随机数 echostr 随机字符串
		 *
		 */
		String signature = request.getParameter("signature");
		String timestamp = request.getParameter("timestamp");
		String nonce = request.getParameter("nonce");
		String echostr = request.getParameter("echostr");
		
		// 验证签名
		if (WxService.check(signature, timestamp, nonce)) {
			PrintWriter out = response.getWriter();
			// 通过检验signature对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败
			out.print(echostr);
			out.close();
			out = null;
			System.out.println(echostr);
			System.out.println("接入成功");
		} else {
			System.out.println("接入失败");
		}

	}

	/**
	 * 接收消息和事件推送
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf8");
		response.setCharacterEncoding("utf8");
		//处理消息和事件推送
		Map<String, String> requestMap=WxService.parseRequest(request.getInputStream());
//System.out.println("dopost-requestMap->parseReq:"+requestMap);
//**parse方法没问题
		//准备回复的数据包
		String respXml=WxService.getResponse(requestMap);
//System.out.println("dopost-respXml"+respXml);
		PrintWriter out = response.getWriter();
		out.print(respXml);
		out.flush();
		out.close();
	}

}
