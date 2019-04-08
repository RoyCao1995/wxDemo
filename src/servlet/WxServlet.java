package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Time;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import service.WxService;

public class WxServlet extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		/**
		 * signature ΢�ż���ǩ����signature����˿�������д��token�����������е�timestamp������nonce������
		 * timestamp ʱ��� nonce ����� echostr ����ַ���
		 * 
		 */
		String signature = request.getParameter("signature");
		String timestamp = request.getParameter("timestamp");
		String nonce = request.getParameter("nonce");
		String echostr = request.getParameter("echostr");
		// ��֤ǩ��
		if (WxService.check(signature, timestamp, nonce)) {
			PrintWriter out = response.getWriter();
			// ͨ������signature���������У�飬��У��ɹ���ԭ������echostr����ʾ����ɹ����������ʧ��
			out.print(echostr);
			out.close();
			out = null;
			System.out.println(echostr);
			System.out.println("����ɹ�");
		} else {
			System.out.println("����ʧ��");
		}

	}

	/**
	 * ������Ϣ���¼�����
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf8");
		response.setCharacterEncoding("utf8");
		//������Ϣ���¼�����
		Map<String, String> requestMap=WxService.parseRequest(request.getInputStream());
System.out.println("dopost-requestMap->parseReq:"+requestMap);
//**parse����û����
		//׼���ظ������ݰ�
		String respXml=WxService.getResponse(requestMap);
System.out.println("dopost-respXml"+respXml);
		PrintWriter out = response.getWriter();
		out.print(respXml);
		out.flush();
		out.close();
	}

}
