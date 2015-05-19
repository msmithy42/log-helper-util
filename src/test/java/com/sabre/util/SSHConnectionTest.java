package com.sabre.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class SSHConnectionTest {

	
	@Test
	public void test() throws JSchException, IOException {
		Session session = createJSchSession("sg218329", "bnghty56", "pnrhlc220", 22);
		ChannelExec channelExec = null;
		
		for (int i = 0; i < 5; i++) {
			channelExec = (ChannelExec)session.openChannel("exec");
			channelExec.setCommand("sed -n " + i + "p" + " /sabre/nmv/pnr-services-s2-sandbox/logs/nmv.log");
			channelExec.connect();
			
			InputStream in = channelExec.getInputStream();
			StringWriter writer = new StringWriter();
			IOUtils.copy(in, writer, "UTF-8");
			String logString = writer.toString();
			
			System.out.println(logString);
		}
		
//		channelExec = (ChannelExec)session.openChannel("exec");
//		channelExec.setCommand("fgrep -nh -m 1 14:40:45,468 /sabre/cdresb/pnr-services-s2-dev/logs/nmv.log");
//		channelExec.connect();
//		
//		in = channelExec.getInputStream();
//		writer.getBuffer().setLength(0);
//		IOUtils.copy(in, writer, "UTF-8");
//		String logString2 = writer.toString();
//		
//		colonIndex = logString2.indexOf(":");
//		String endLine = logString2.substring(0, colonIndex);
//		System.out.println(endLine);
		
		channelExec.disconnect();
        session.disconnect();
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

}
