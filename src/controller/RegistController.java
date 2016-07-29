package controller;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class RegistController implements Initializable {
	Stage primaryStage;
	Socket socket;
	OutputStreamWriter outputStreamWriter;
	InputStreamReader inputStreamReader;
	

	private static final char REGIST_TYPE = 'r';
	
	@FXML private Button backBtn;
	@FXML private Button submitBtn;
	@FXML private TextField idTextField;
	@FXML private TextField nameTextField;
	@FXML private TextField passTextField;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		backBtn.setOnAction(event->goBack());
		submitBtn.setOnAction(event->submitToServer());
	}

	private void submitToServer() {
		try{
			outputStreamWriter.write(REGIST_TYPE);
			outputStreamWriter.flush();
			if(isServerReady()){
				String msg = idTextField.getText()+"/"+passTextField.getText()
				+"/"+nameTextField.getText();
				outputStreamWriter.write(msg);
				outputStreamWriter.flush();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		Stage dialog = new Stage(StageStyle.UTILITY);
		dialog.initModality(Modality.WINDOW_MODAL);
		dialog.initOwner(primaryStage);
		if(isSubmitClear()){
			try {
				Parent parent = 
						FXMLLoader.load(getClass().getResource("../fxml/clear.fxml"));
				Button okBtn = (Button) parent.lookup("#okBtn");
				okBtn.setOnAction(event->dialog.close());
				Scene scene = new Scene(parent);
				dialog.setScene(scene);
				dialog.setResizable(false);
				dialog.show();
			} catch (IOException e) {
				e.printStackTrace();
			}
			goBack();
		}else{
			try {
				Parent parent = 
						FXMLLoader.load(getClass().getResource("../fxml/clear.fxml"));
				Button okBtn = (Button) parent.lookup("#okBtn");
				Label label = (Label)parent.lookup("#msg");
				label.setText("이미 존재하는 회원입니다.");
				idTextField.setText("");
				passTextField.setText("");
				okBtn.setOnAction(event->dialog.close());
				Scene scene = new Scene(parent);
				dialog.setScene(scene);
				dialog.setResizable(false);
				dialog.show();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
	}

	private boolean isSubmitClear() {
		char[] dataChar = new char[10];
		try {
			inputStreamReader.read(dataChar);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String data = new String(dataChar).trim();
		if(data.equals("already")){
			System.out.println("이미있습니다");
			return false;
		}else if(data.equals("clear")){
			System.out.println("성공적으로 클라이언트 생성");
			return true;
		}
		return false;
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

	private void goBack() {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxml/root.fxml"));
		Parent regist = null;
		try {
			regist = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Scene scene = new Scene(regist);
		RootController rootController = loader.getController();
		rootController.setPrimaryStage(primaryStage);
		try {
			rootController.setSocket(socket);
		} catch (IOException e) {
			e.printStackTrace();
		}
		primaryStage.setScene(scene);
	}

	public void setStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
		
	}

	public void setSocket(Socket socket) throws IOException {
		this.socket = socket;
		this.outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());
		this.inputStreamReader= new InputStreamReader(socket.getInputStream());
		
	}

}
