package com.gcm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

/**
 * Servlet implementation class GCMServer
 */
public class GCMServer extends HttpServlet {
	private static final String GOOGLE_SERVER_KEY = "AIzaSyAvOH-hapA8Qqgsfc07DsSZkyrj1yEPnL4";
	static final String MESSAGE_KEY = "iycNotification";	


	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);

	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {


		MulticastResult result = null;

		String share = request.getParameter("shareRegId");

		// GCM RedgId of Android device to send push notification
		String regId = "";
		if (share != null && !share.isEmpty()) {
			System.out.println("share is null");
			regId = request.getParameter("regId");
			File file=new File("c://GCMGoogleRegIds.txt");
			if(!file.exists())
			{
			file.createNewFile();
			}
			PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("c://GCMGoogleRegIds.txt", true)));
			writer.println(regId);
			writer.close();
			request.setAttribute("pushStatus", "GCM RegId Received.");
			request.getRequestDispatcher("index.jsp")
					.forward(request, response);
		} else {

			try {
				List<String> regIdList=new ArrayList<String>();
				BufferedReader br = new BufferedReader(new FileReader(
						"c://GCMGoogleRegIds.txt"));
				String line="";
				while((line=br.readLine())!=null)
				{
					regIdList.add(line);
				}
				regId = br.readLine();
				br.close();
				String userMessage = request.getParameter("message");
				Sender sender = new Sender("AIzaSyAvOH-hapA8Qqgsfc07DsSZkyrj1yEPnL4");
				Message message = new Message.Builder().timeToLive(30)
						.delayWhileIdle(true).addData(MESSAGE_KEY, userMessage).build();
				System.out.println("regId: " + regId);
				//result = sender.send(message, regId, 1);
				result = sender.send(message, regIdList, 3);
				request.setAttribute("pushStatus", result.toString());
			} catch (IOException ioe) {
				ioe.printStackTrace();
				request.setAttribute("pushStatus",
						"RegId required: " + ioe.toString());
			} catch (Exception e) {
				e.printStackTrace();
				request.setAttribute("pushStatus", e.toString());
			}
			request.getRequestDispatcher("index.jsp")
					.forward(request, response);
		}
	}
	
}
