package controller;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.ClientListener;
import main.ClientMain;
import main.MyInform;

public class RootController implements Initializable {
	private Stage primaryStage;
	Socket socket;
	InputStreamReader inputStreamReader;
	OutputStreamWriter outputStreamWriter;
	
	private static final char LOGIN_TYPE = 'l';
	
	@FXML private TextField idTextField;
	@FXML private TextField passTextField;
	@FXML private Button loginBtn;
	@FXML private Button registBtn;
	@FXML private Button exitBtn;
	
	public void setPrimaryStage(Stage primaryStage){
		this.primaryStage = primaryStage;
	}
	public void setSocket(Socket socket) throws IOException{
		this.socket = socket;
		this.outputStreamWriter=new OutputStreamWriter(socket.getOutputStream());
		this.inputStreamReader = new InputStreamReader(socket.getInputStream());
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		loginBtn.setOnAction(event->login());
		registBtn.setOnAction(event->{
			try {
				goRegist();
			} catch (IOException e) {
				stopClient();
				e.printStackTrace();
			}
		});
		exitBtn.setOnAction(event->exit());
	}

	private void exit() {
		System.exit(0);
	}

	private void goRegist() throws IOException{
		System.out.println(getClass().getResource("../fxml/regist.fxml"));
		FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxml/regist.fxml"));
		Parent regist = loader.load();
		Scene scene = new Scene(regist);
		RegistController registController = loader.getController();
		registController.setStage(primaryStage);
		registController.setSocket(socket);
		primaryStage.setScene(scene);
		
	}

	public void login() {
		try {
			outputStreamWriter.write(LOGIN_TYPE);
			outputStreamWriter.flush();
			if(isServerReady()){
				String msg = idTextField.getText() + "/" + passTextField.getText();
				outputStreamWriter.write(msg);
				outputStreamWriter.flush();
				
				char[] readBytes = new char[10];
				inputStreamReader.read(readBytes);
				String data = new String(readBytes).trim();
				Stage dialog = new Stage(StageStyle.UTILITY);
				dialog.initModality(Modality.WINDOW_MODAL);
				dialog.initOwner(primaryStage);
				
				
				if(data.equals("success")){
					System.out.println("로그인 성공");
					FXMLLoader loader =
						new FXMLLoader(getClass().getResource("../fxml/friendsList.fxml"));
					Parent friendList = loader.load();
					Scene scene = new Scene(friendList);
					FriendsListController friendsListController =
							loader.getController();
					friendsListController.setStage(primaryStage);
					friendsListController.setSocket(socket);
					primaryStage.setScene(scene);
					ClientMain.myInform = new MyInform();
					ClientMain.myInform.setId(idTextField.getText());
					new ClientListener(primaryStage, socket, friendsListController);
					
				}else if(data.equals("passFail")){
					Parent parent = 
							FXMLLoader.load(getClass().getResource("../fxml/clear.fxml"));
					Button okBtn = (Button) parent.lookup("#okBtn");
					Label label = (Label)parent.lookup("#msg");
					label.setText("패스워드가 일치하지 않습니다.");
					okBtn.setOnAction(event->dialog.close());
					Scene scene = new Scene(parent);
					dialog.setScene(scene);
					dialog.setResizable(false);
					dialog.show();
				}else if(data.equals("noClient")){
					Parent parent = 
							FXMLLoader.load(getClass().getResource("../fxml/clear.fxml"));
					Button okBtn = (Button) parent.lookup("#okBtn");
					Label label = (Label)parent.lookup("#msg");
					label.setText("회원이 존재하지 않습니다.");
					okBtn.setOnAction(event->dialog.close());
					Scene scene = new Scene(parent);
					dialog.setScene(scene);
					dialog.setResizable(false);
					dialog.show();
				}
			}else{
				throw new IOException();
			}
		} catch (IOException e) {
			stopClient();
			e.printStackTrace();
		}
		
	}
	private boolean isServerReady() throws IOException {
		char[] data = new char[10];
		inputStreamReader.read(data);
		String msg = new String(data);
		System.out.println(msg);
		if(msg.trim().equals("ready"))
			return true;
		else
			return false;
	}
	
	public void stopClient() {
		try{
			inputStreamReader.close();
			outputStreamWriter.close();
			socket.close();
		}catch (Exception e) {
			
			e.printStackTrace();
		}
	}

}
