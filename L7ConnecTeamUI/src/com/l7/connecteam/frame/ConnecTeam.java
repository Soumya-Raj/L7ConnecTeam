package com.l7.connecteam.frame;

import java.util.logging.Logger;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;

/**
 * @author soumya.raj 
 * Acts as the starting point to load initial scene
 */
public class ConnecTeam extends Application {
	Logger logger = Logger.getLogger(ConnecTeam.class.getName());

	@Override
	public void start(Stage primaryStage) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("/view/Login.fxml"));
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setTitle("L7Connecteam Login");
			primaryStage.setScene(scene);
			primaryStage.show();
			primaryStage.setResizable(false);
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(e.getMessage());
		}
	}

	public static void main(String args[]) {
		launch(args);
	}

	public void init() throws Exception {

	}
}
