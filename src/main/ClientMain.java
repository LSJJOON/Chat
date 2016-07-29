package main;
import java.net.Socket;

import controller.RootController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClientMain extends Application {
	Socket socket;
	public static MyInform myInform;

	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void init() throws Exception {
		socket = new Socket("localhost", 5001);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		System.out.println(getClass().getResource("../fxml/root.fxml"));
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../fxml/root.fxml"));
		Parent root = fxmlLoader.load();
		RootController rootController = fxmlLoader.getController();
		rootController.setPrimaryStage(primaryStage);
		rootController.setSocket(socket);
		Scene scene = new Scene(root);
		primaryStage.setTitle("·Î±×ÀÎ");
		primaryStage.setResizable(false);
		primaryStage.setScene(scene);
		primaryStage.show();

	}



	@Override
	public void stop() throws Exception {
		socket.close();
		super.stop();
	}
	
	


}
