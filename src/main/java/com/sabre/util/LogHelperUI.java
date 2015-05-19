package com.sabre.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.IOException;

import javax.swing.*;

public class LogHelperUI {
	
	private String[] saveData;
		
	private String user;
	private String pass;
	private String host;
	private String path;
	private String outputName;
	private String startTime;
	private String endTime;
		
	public LogHelperUI() {
		JFrame mainFrame = new JFrame();
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setTitle("Log Helper Tool");
		mainFrame.setSize(360, 400);
		mainFrame.setLocationRelativeTo(null);
		mainFrame.setResizable(false);
		
		try {
			saveData = LogHelperSaveData.load();
		} catch (IOException e) {
			System.out.println("Load error: " + e);
		}
		
		JPanel controlPanel = createMainPanel();
		controlPanel.setLayout(null);
		mainFrame.add(controlPanel);
		
		mainFrame.setSize(360, 400);
		mainFrame.setVisible(true);
	}
	
	private JPanel createMainPanel() {
		JLabel userLabel = new JLabel("Username: ");
		JTextField userField = new JTextField();
		userLabel.setBounds(10, 20, 100, 25);
		userField.setBounds(120, 20, 200, 25);
		
		JLabel passLabel = new JLabel("Password: ");
		JPasswordField passField = new JPasswordField();
		passLabel.setBounds(10, 60, 100, 25);
		passField.setBounds(120, 60, 200, 25);
		
		JLabel hostLabel = new JLabel("Host name: ");
		JTextField hostField = new JTextField();
		hostLabel.setBounds(10, 100, 100, 25);
		hostField.setBounds(120, 100, 200, 25);
		
		JLabel pathLabel = new JLabel("Path to file: ");
		JTextField pathField = new JTextField();
		pathLabel.setBounds(10, 140, 100, 25);
		pathField.setBounds(120, 140, 200, 25);
		
		JLabel outputLabel = new JLabel("Output filename: ");
		JTextField outputField = new JTextField();
		outputLabel.setBounds(10, 180, 100, 25);
		outputField.setBounds(120, 180, 200, 25);
		
		JLabel startTimeLabel = new JLabel("Start time: ");
		HintTextField startTimeField = new HintTextField("ex. HH:mm:ss,SSS");
		startTimeLabel.setBounds(10, 220, 100, 25);
		startTimeField.setBounds(120, 220, 200, 25);
		
		JLabel endTimeLabel = new JLabel("End time: ");
		HintTextField endTimeField = new HintTextField("ex. HH:mm:ss,SSS");
		endTimeLabel.setBounds(10, 260, 100, 25);
		endTimeField.setBounds(120, 260, 200, 25);

		try {
			loadFieldsWithSaveData(userField, hostField, pathField, outputField, startTimeField, endTimeField);
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Data loading exception: " + e);
			System.out.println("Verify save data has correct number of elements.");
		}
		
		JPanel controlPanel = new JPanel();
		controlPanel.setSize(360, 400);
		controlPanel.add(userLabel);
		controlPanel.add(userField);
		controlPanel.add(passLabel);
		controlPanel.add(passField);
		controlPanel.add(hostLabel);
		controlPanel.add(hostField);
		controlPanel.add(pathLabel);
		controlPanel.add(pathField);
		controlPanel.add(outputLabel);
		controlPanel.add(outputField);
		controlPanel.add(startTimeLabel);
		controlPanel.add(startTimeField);
		controlPanel.add(endTimeLabel);
		controlPanel.add(endTimeField);
		
		JButton saveButton = createSaveButton(userField, passField, hostField, pathField, outputField, startTimeField, endTimeField);
		JButton getButton = createGetButton(userField, passField, hostField, pathField, outputField, startTimeField, endTimeField);
		controlPanel.add(saveButton);
		controlPanel.add(getButton);
		
		return controlPanel;
	}
	
	private JButton createSaveButton(final JTextField userField, final JPasswordField passField, final JTextField hostField, final JTextField pathField, final JTextField outputField, final JTextField startTimeField, final JTextField endTimeField) {
		JButton saveButton = new JButton("SAVE");
		saveButton.setBounds(70, 300, 80, 40);
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				user = userField.getText();
				host = hostField.getText();
				path = pathField.getText();
				outputName = outputField.getText();
				startTime = startTimeField.getText();
				endTime = endTimeField.getText();
				
				String[] data = new String[]{user, host, path, outputName, startTime, endTime};
				try {
					LogHelperSaveData.save(data);
				} catch (IOException e1) {
					System.out.println(e1.getMessage());
				}
			}
		});
		
		return saveButton;
	}
	
	private JButton createGetButton(final JTextField userField, final JPasswordField passField, final JTextField hostField, final JTextField pathField, final JTextField outputField, final HintTextField startTimeField, final HintTextField endTimeField) {
		JButton getButton = new JButton("GET");
		getButton.setBounds(210, 300, 80, 40);
		getButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				user = userField.getText();
				pass = new String(passField.getPassword());
				host = hostField.getText();
				path = pathField.getText();
				outputName = outputField.getText();
				startTime = startTimeField.getText();
				endTime = endTimeField.getText();
				LogHelperSSHConnection.connect(user, pass, host, path, outputName, startTime, endTime);
			}
		});
		
		return getButton;
	}
	
	private void loadFieldsWithSaveData(JTextField userField, JTextField hostField, JTextField pathField, JTextField outputField, HintTextField startTimeField, HintTextField endTimeField) {
		if (saveData != null && !saveData[0].isEmpty()) {
			for (int i = 0; i < saveData.length; i++) {
				System.out.println(saveData[i]);
			}
			userField.setText(saveData[0]);
			hostField.setText(saveData[1]);
			pathField.setText(saveData[2]);
			outputField.setText(saveData[3]);
			startTimeField.setText(saveData[4]);
			startTimeField.setShowingHint(false);
			startTimeField.setFont(startTimeField.getFont().deriveFont(Font.PLAIN));
			startTimeField.setForeground(Color.DARK_GRAY);
			endTimeField.setText(saveData[5]);
			endTimeField.setShowingHint(false);
			endTimeField.setFont(endTimeField.getFont().deriveFont(Font.PLAIN));
			endTimeField.setForeground(Color.DARK_GRAY);
		}
	}

}

class HintTextField extends JTextField implements FocusListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -638544907345438269L;
	private final String hint;
	private boolean showingHint;

	public HintTextField(final String hint) {
		super(hint);
		super.setFont(super.getFont().deriveFont(Font.ITALIC));
		super.setForeground(Color.GRAY);
		this.hint = hint;
		this.showingHint = true;
		super.addFocusListener(this);
	}

	@Override
	public void focusGained(FocusEvent e) {
		if (this.getText().isEmpty()) {
			super.setText("");
			super.setForeground(Color.DARK_GRAY);
			super.setFont(super.getFont().deriveFont(Font.PLAIN));
			showingHint = false;
		}
	}

	@Override
	public void focusLost(FocusEvent e) {
		if (this.getText().isEmpty()) {
			super.setText(hint);
			super.setFont(super.getFont().deriveFont(Font.ITALIC));
			super.setForeground(Color.GRAY);
			showingHint = true;
		}
	}
	
	public void setShowingHint(boolean showingHint) {
		this.showingHint = showingHint;
	}

	@Override
	public String getText() {
		return showingHint ? "" : super.getText();
	}
}
