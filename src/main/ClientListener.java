package main;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import controller.FriendsListController;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

public class ClientListener {
	Stage primaryStage;
	Socket socket;
	InputStreamReader inputStreamReader;
	OutputStreamWriter outputStreamWriter;
	
	FriendsListController friendsListController;
	
	private final char FRIEND_ADD_TYPE = 'f';
	private final char REQUEST_FRIEND_ADD_TYPE = 'r';
	private final char REQUEST_RECEIVED_FRIEND_ADD_TYPE = 'a';

	public ClientListener(Stage primaryStage, Socket socket,
			FriendsListController friendsListController) {
		this.primaryStage = primaryStage;
		this.socket = socket;
		this.friendsListController = friendsListController;
		try {
			this.inputStreamReader = new InputStreamReader(socket.getInputStream());
			this.outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());
			receive();
		} catch (Exception e) {
			try {
				stopClient();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
	}

	private void receive() {
		Runnable runnable =()->{
			try {
			while(true){
				
				char[] requestType = new char[1];
				inputStreamReader.read(requestType);
				
				switch (requestType[0]) {
				case FRIEND_ADD_TYPE:
					friendsListController.findId();
					receiveResponse();
					break;
				case REQUEST_FRIEND_ADD_TYPE:
					friendsListController.sendIds();
					break;
				case REQUEST_RECEIVED_FRIEND_ADD_TYPE:
					System.out.println("서버로부터 수신 받음");
					friendsListController.addFriendRequest();
					
				}
			}
			} catch (Exception e) {
				try {
					stopClient();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				e.printStackTrace();
			}
		};
		Thread thread = new Thread(runnable);
		thread.start();
	}

	private void receiveResponse() throws IOException {
		char[] readBytes = new char[100];
		inputStreamReader.read(readBytes);
		String msg = new String(readBytes).trim();
		if(msg.equals("find")){
			friendsListController.setId();
		}else if(msg.equals("noClient")){
			friendsListController.popUp("../fxml/clear.fxml", "회원이 존재하지 않습니다.");
		}
		
	}

	public void stopClient() throws IOException{
		inputStreamReader.close();
		outputStreamWriter.close();
		socket.close();
	}

}
