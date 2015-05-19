package com.sabre.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.time.format.DateTimeFormatter;

import org.apache.commons.io.IOUtils;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class LogHelperSSHConnection {
	
	public static void connect(String user, String pass, String host, String path, String outName, String startTime, String endTime) {
		try {
			Session session = createJSchSession(user, pass, host, 22);
			
			String startLine = getStartLine(session, startTime, path);
			String endLine = getEndLine(session, endTime, path);
			String logString = getLogBetweenTimes(session, startLine, endLine, path);
			
			FileOutputStream out = new FileOutputStream(outName);
			out.write(logString.getBytes());
			out.close();
			
	        session.disconnect();
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	private static Session createJSchSession(String user, String pass, String host, int port) throws JSchException {
		JSch jsch = new JSch();
		Session session = jsch.getSession(user, host, port);
        session.setPassword(pass);
        session.setConfig("StrictHostKeyChecking", "no");
        System.out.println("Establishing Connection...");
        session.connect();
        System.out.println("Connection established.");
        
        return session;
	}
	
	private static String getStartLine(Session session, String startTime, String path) throws JSchException, IOException {
		int unformattedLength = startTime.length();
		startTime = formatTimeString(startTime);
		ChannelExec channelExec = (ChannelExec)session.openChannel("exec");
		channelExec.setCommand("grep -nh -m 1 -a '" + startTime + "' " + path);
		channelExec.connect();
		
		InputStream in = channelExec.getInputStream();
		StringWriter writer = new StringWriter();
		IOUtils.copy(in, writer, "UTF-8");
		String logString = writer.toString();
		
		while (logString.isEmpty()) {
			// In the case no match is found, make search less strict
			channelExec.disconnect();
			startTime = startTime.substring(0, unformattedLength);
			int len = startTime.length();
			
			if (len == 0) {
				// Exhausted search
				break;
			}
			
			startTime = formatTimeString(startTime.substring(0, len - 1));
			
			channelExec = (ChannelExec)session.openChannel("exec");
			channelExec.setCommand("grep -nh -m 1 -a '" + startTime + "' " + path);
			channelExec.connect();
			
			in = channelExec.getInputStream();
			writer.getBuffer().setLength(0);
			IOUtils.copy(in, writer, "UTF-8");
			logString = writer.toString();
		}
		
		int colonIndex = logString.indexOf(":");
		channelExec.disconnect();
		
		return logString.substring(0, colonIndex);
	}
	
	private static String getEndLine(Session session, String endTime, String path) throws JSchException, IOException {
		int unformattedLength = endTime.length();
		endTime = formatTimeString(endTime);
		ChannelExec channelExec = (ChannelExec)session.openChannel("exec");
		channelExec.setCommand("grep -nh -m 1 -a '" + endTime + "' " + path);
		channelExec.connect();
		
		InputStream in = channelExec.getInputStream();
		StringWriter writer = new StringWriter();
		IOUtils.copy(in, writer, "UTF-8");
		String logString = writer.toString();

		while (logString.isEmpty()) {
			// In the case no match is found, make search less strict
			channelExec.disconnect();
			endTime = endTime.substring(0, unformattedLength);
			
			
			int len = endTime.length();
			if (len == 0) {
				// Exhausted search
				break;
			}
			
			endTime = formatTimeString(endTime.substring(0, len - 1));
			
			channelExec = (ChannelExec)session.openChannel("exec");
			channelExec.setCommand("grep -nh -m 1 -a '" + endTime + "' " + path);
			channelExec.connect();
			
			in = channelExec.getInputStream();
			writer.getBuffer().setLength(0);
			IOUtils.copy(in, writer, "UTF-8");
			logString = writer.toString();
		}
		
		int colonIndex = logString.indexOf(":");
		channelExec.disconnect();
		
		return logString.substring(0, colonIndex);
	}
	
	private static String getLogBetweenTimes(Session session, String startLine, String endLine, String path) throws JSchException, IOException {
		ChannelExec channelExec = (ChannelExec)session.openChannel("exec");
		channelExec.setCommand("sed -n " + startLine + "," + endLine + "p " + path);
		channelExec.connect();
		
		InputStream in = channelExec.getInputStream();
		StringWriter writer = new StringWriter();
		IOUtils.copy(in, writer, "UTF-8");
		return writer.toString();
	}
	
	private static String formatTimeString(String time) {
		if (time.length() > 9) {
			return time;
		}
		
		String formattedTime = "";
		switch(time.length()) {
		case 9: 
			formattedTime = time + "[0-9][0-9][0-9]";
			break;
		case 8: 
			formattedTime = time + ",[0-9][0-9][0-9]";
			break;
		case 7: 
			formattedTime = time + "[0-9],[0-9][0-9][0-9]";
			break;
		case 6: 
			formattedTime = time + "[0-9][0-9],[0-9][0-9][0-9]";
			break;
		case 5: 
			formattedTime = time + ":[0-9][0-9],[0-9][0-9][0-9]";
			break;
		case 4: 
			formattedTime = time + "[0-9]:[0-9][0-9],[0-9][0-9][0-9]";
			break;
		case 3: 
			formattedTime = time + "[0-9][0-9]:[0-9][0-9],[0-9][0-9][0-9]";
			break;
		case 2: 
			formattedTime = time + ":[0-9][0-9]:[0-9][0-9],[0-9][0-9][0-9]";
			break;
		}
		
		return formattedTime;
	}

}
