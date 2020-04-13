package com.l7.connecteam.frame;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import com.l7.connecteam.controller.UserController;
import com.l7.connecteam.dto.UserDto;
import com.l7.connecteam.exception.UIException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * @author soumya.raj
 * Controller for login UI
 */
public class LoginController implements Initializable {
	Logger logger = Logger.getLogger(LoginController.class.getName());

	public static UserDto userLogin=new UserDto();
	
	@FXML
	private TextField username;

	@FXML
	private PasswordField password;

	@FXML
	private Label signIn;
	
	@FXML
	private Label welcomeLabel;
	
	@FXML
	private Button signInbutton;

	/**
	 * Event handler for sign in button
	 * @param event
	 */
	public void loginAction(ActionEvent event) {
		boolean exitCondition = false;
		String user = username.getText().toString();
		String passwordEntered = password.getText().toString();

		UserController userObj = new UserController();
		UserDto userDataObj = new UserDto();
		userDataObj.setUsername(user);
		userDataObj.setPassword(passwordEntered);
		try {
			userDataObj = userObj.userLogin(userDataObj);
			userLogin=userDataObj;
		} catch (UIException e) {
			exitCondition = true;
			signIn.setText(e.getLocalizedMessage());
		} catch (SQLException e) {
			logger.info(e.getMessage());
			signIn.setText("Connection temporarily unavailable");
		}
		if (!exitCondition) {
			signIn.setText("Sign in successful");
			try {
				((Node)event.getSource()).getScene().getWindow().hide();
				Stage primaryStage=new Stage();
				Parent root=FXMLLoader.load(getClass().getResource("/view/MainGUI.fxml"));
				primaryStage.setTitle("L7Connecteam");
				Scene scene = new Scene(root);
				scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
				primaryStage.setScene(scene);
				primaryStage.show();
				primaryStage.setResizable(false);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void initialize(URL url, ResourceBundle rb) {

	}
}
