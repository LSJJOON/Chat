package controller;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.ClientMain;

public class FriendsListController implements Initializable {
	Stage primaryStage;
	Socket socket;
	InputStreamReader intputStreamReader;
	OutputStreamWriter outputStreamWriter;
	
	@FXML private Button findIdBtn;
	@FXML private TextField findIdTextField;
	@FXML private Button requestFriendBtn;
	@FXML private Label findId;
	
	@FXML private ListView<String> acceptRequestList;
	ObservableList<String> items;
	@FXML private Button acceptBtn;
	@FXML private Button rejectBtn;
	
	
	private final char FRIEND_ADD_TYPE = 'f';
	private final char REQUEST_FRIEND_ADD_TYPE = 'r';
	private final char REQUEST_RECEIVED_FRIEND_ADD_TYPE = 'a';

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		findIdBtn.setOnAction(event->sendRequest(FRIEND_ADD_TYPE));
		requestFriendBtn.setOnAction(event->sendRequest(REQUEST_FRIEND_ADD_TYPE));
		
		items = FXCollections.observableArrayList();
	}

	public void findId() {
		String msg = findIdTextField.getText();
		sendMsg(msg);
	}

	
	private void sendRequest(char requestType) {
		try {
			outputStreamWriter.write(requestType);
			outputStreamWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private void sendMsg(String msg) {
		
		try {
			outputStreamWriter.write(msg);
			outputStreamWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public void setStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
		try {
			this.intputStreamReader = new InputStreamReader(socket.getInputStream());
			this.outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setId() {		//친구를 찾으면 결과를 보여줌
		Platform.runLater(()->{
			findId.setText(findIdTextField.getText());
			requestFriendBtn.setVisible(true);
		});
	}

	public void popUp(String resource, String msg) {
		Platform.runLater(()->{
			Stage dialog = new Stage(StageStyle.UTILITY);
			dialog.initModality(Modality.WINDOW_MODAL);
			dialog.initOwner(primaryStage);
			Parent parent = null;
			try {
				parent = FXMLLoader.load(getClass().getResource(resource));
			} catch (IOException e) {
				e.printStackTrace();
			}
			Button okBtn = (Button) parent.lookup("#okBtn");
			Label label = (Label)parent.lookup("#msg");
			label.setText(msg);
			okBtn.setOnAction(event->dialog.close());
			Scene scene = new Scene(parent);
			dialog.setScene(scene);
			dialog.setResizable(false);
			dialog.show();
		});
		
	}

	public void sendIds() {			//친구 검색 
		String myId = ClientMain.myInform.getId();
		String targetId = findId.getText();
		String msg = myId+"/"+targetId;
		sendMsg(msg);
	}

	public void addFriendRequest() {
		sendRequest(REQUEST_RECEIVED_FRIEND_ADD_TYPE); 			//서버측에 준비가 되었다고 전달
		char[] readBytes = new char[1000];
		try {
			intputStreamReader.read(readBytes);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String data = new String(readBytes).trim();
		System.out.println("서버로부터 받은 데이터 = "+data);
		StringTokenizer st = new StringTokenizer(data, "/");
		while(st.hasMoreTokens()){
			items.add(st.nextToken());
		}
		acceptRequestList.setItems(items);
		
	}

}
