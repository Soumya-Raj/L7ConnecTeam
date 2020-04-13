package com.l7.connecteam.frame;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import com.l7.connecteam.controller.BatchController;
import com.l7.connecteam.controller.BatchwiseReportController;
import com.l7.connecteam.controller.IndividualReportController;
import com.l7.connecteam.controller.ModuleController;
import com.l7.connecteam.controller.UserController;
import com.l7.connecteam.dto.BatchDto;
import com.l7.connecteam.dto.BatchwiseReportDto;
import com.l7.connecteam.dto.FeatureDto;
import com.l7.connecteam.dto.IndividualReportDto;
import com.l7.connecteam.dto.ModuleDto;
import com.l7.connecteam.dto.RoleDto;
import com.l7.connecteam.dto.UserDto;
import com.l7.connecteam.excel.reader.BulkUploadProcessor;
import com.l7.connecteam.exception.UIException;
import com.l7.connecteam.utility.MoveDirectory;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

/**
 * @author soumya.raj 
 * This class acts as controller for ConnecTeam dashboard UI
 *
 */
public class MainGUIController implements Initializable {

	Logger logger = Logger.getLogger(MainGUIController.class.getName());
	UserDto userLogin = LoginController.userLogin;

	List<RoleDto> roleList = new ArrayList<RoleDto>();
	List<FeatureDto> featureList = new ArrayList<FeatureDto>();
	List<String> featureName = new ArrayList<String>();
	List<String> filePath = new ArrayList<String>();

	ComboBox<String> module = new ComboBox<String>();

	ListView<String> list = new ListView<String>();

	@FXML
	private VBox leftVBox;

	@FXML
	private Label userLabel;

	@FXML
	private Label empID;

	@FXML
	private ComboBox<String> role;

	@FXML
	private BorderPane borderPane;

	@FXML
	private AnchorPane anchorPaneUI;

	@FXML
	private VBox vBoxMain;

	@FXML
	private Label errorLabel;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		userLabel.setText("User Name : " + userLogin.getUsername());
		empID.setText("Emp ID : " + userLogin.getEmployeeId());
		for (RoleDto roleObj : userLogin.getRoleList()) {
			role.getItems().add(roleObj.getRoleName());
		}
	}

	/**
	 * Event handler for sign out action
	 * 
	 * @param event
	 */
	public void signOutEvent(ActionEvent event) {
		try {
			((Node) event.getSource()).getScene().getWindow().hide();
			Stage primaryStage = new Stage();
			Parent root = FXMLLoader.load(getClass().getResource("/view/Login.fxml"));
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			primaryStage.show();
			primaryStage.setResizable(false);
		} catch (IOException e) {
			logger.info(e.getMessage());
		}

	}

	/**
	 * 
	 * @param <T>
	 * @param Event handler function for combo box - role
	 */
	public <T> void comboAction(ActionEvent event) {
		leftVBox.getChildren().clear();
		ModuleController moduleConObj = new ModuleController();
		ModuleDto moduleObj = new ModuleDto();

		for (RoleDto roleObj : userLogin.getRoleList()) {
			if (role.getValue().equals(roleObj.getRoleName())) {
				int moduleID = 0;
				List<String> featureList = new ArrayList<String>();
				for (int i = 0; i < roleObj.getFeatureList().size(); i++) {
					if (moduleID == 0 || roleObj.getFeatureList().get(i).getModuleId() == moduleID) {
						featureList.add(roleObj.getFeatureList().get(i).getFeatureName());
						moduleID = roleObj.getFeatureList().get(i).getModuleId();

						if (i == (roleObj.getFeatureList().size()) - 1) {
							module = new ComboBox<String>();
							module.setId("featureComboBox");
							module.setPrefWidth(200);
							moduleObj.setModuleID(moduleID);
							try {
								moduleObj = moduleConObj.getModuleByID(moduleObj);
							} catch (UIException e) {
								logger.info(e.getMessage());
								;
								errorLabel.setText(e.getLocalizedMessage());
							} catch (SQLException e) {
								logger.info(e.getMessage());
								;
								errorLabel.setText(e.getLocalizedMessage());
							}

							module.setPromptText(moduleObj.getModuleName());
							leftVBox.getChildren().add(module);
							ObservableList<String> list = FXCollections.observableArrayList(featureList);
							module.setItems(list);
							featureList = new ArrayList<String>();
						}
					} else {
						module = new ComboBox<String>();
						module.setPrefWidth(200);
						moduleObj.setModuleID(moduleID);
						try {
							moduleObj = moduleConObj.getModuleByID(moduleObj);
						} catch (UIException e) {
							logger.info(e.getMessage());
							;
							errorLabel.setText(e.getLocalizedMessage());
						} catch (SQLException e) {
							logger.info(e.getMessage());
							;
							errorLabel.setText(e.getLocalizedMessage());
						}

						module.setPromptText(moduleObj.getModuleName());
						leftVBox.getChildren().add(module);
						ObservableList<String> list = FXCollections.observableArrayList(featureList);
						module.setItems(list);
						moduleID = roleObj.getFeatureList().get(i).getModuleId();
						i = i - 1;
						featureList = new ArrayList<String>();

					}
					module.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
						@Override
						public void changed(ObservableValue<? extends String> observable, String oldValue,
								String newValue) {
							switch (newValue) {
							case "Upload batch assessment details":
								borderPane.setCenter(anchorPaneUI);
								Button upload = new Button("upload");
								anchorPaneUI.getChildren().clear();
								anchorPaneUI.getChildren().add(upload);
								upload.setLayoutX(150);
								upload.setLayoutY(220);
								upload.setId("upload");

								anchorPaneUI.getChildren().add(list);
								list.setPrefHeight(380);
								list.setPrefWidth(300);
								list.setLayoutX(250);
								list.setLayoutY(70);

								Button export = new Button("export");
								anchorPaneUI.getChildren().add(export);
								export.setLayoutX(370);
								export.setLayoutY(470);

								upload.setOnAction(new EventHandler<ActionEvent>() {
									@Override
									public void handle(ActionEvent event) {
										uploadAction(event);
									}
								});

								export.setOnAction(new EventHandler<ActionEvent>() {
									@Override
									public void handle(ActionEvent event) {
										exportAction(event);
									}
								});
								break;

							case "View individual report":
								ComboBox<String> allUsers = new ComboBox<String>();
								if (role.getValue().equals("Trainee")) {
									getIndividualReport(userLogin);
								} else if (role.getValue().equals("Trainer") || role.getValue().equals("Admin")) {
									UserController userObj = new UserController();
									List<UserDto> userList = null;
									try {
										userList = userObj.getAllUsers();
									} catch (UIException e) {
										logger.info(e.getMessage());
										;
										errorLabel.setText(e.getLocalizedMessage());
									} catch (SQLException e) {
										logger.info(e.getMessage());
										;
										errorLabel.setText(e.getLocalizedMessage());
									}

									List<String> userNames = new ArrayList<String>();
									for (UserDto userDto : userList) {
										userNames.add(userDto.getUsername());
									}
									ObservableList<String> list = FXCollections.observableArrayList(userNames);
									allUsers.setItems(list);
									allUsers.setPromptText("Select trainee");
									leftVBox.getChildren().add(allUsers);
									allUsers.getSelectionModel().selectedItemProperty()
											.addListener(new ChangeListener<String>() {
												@Override
												public void changed(ObservableValue<? extends String> observable,
														String oldValue, String newValue) {
													UserDto userDataObj = new UserDto();
													userDataObj.setUsername(newValue);
													getIndividualReport(userDataObj);
												}
											});
								}
								break;

							case "View Batchwise report":
								borderPane.setCenter(anchorPaneUI);
								anchorPaneUI.getChildren().clear();
								ComboBox<String> batchNames = new ComboBox<String>();
								BatchController batchConObj = new BatchController();
								List<String> batches = null;
								try {
									batches = batchConObj.getAllBatchNames();
								} catch (UIException e) {
									logger.info(e.getMessage());
									;
									errorLabel.setText(e.getLocalizedMessage());
								} catch (SQLException e) {
									logger.info(e.getMessage());
									;
									errorLabel.setText(e.getLocalizedMessage());
								}

								ObservableList<String> listBatch = FXCollections.observableArrayList(batches);
								batchNames.setPromptText("Select Batch");
								batchNames.setItems(listBatch);
								anchorPaneUI.getChildren().clear();
								anchorPaneUI.getChildren().add(batchNames);
								batchNames.getSelectionModel().selectedItemProperty()
										.addListener(new ChangeListener<String>() {
											@Override
											public void changed(ObservableValue<? extends String> observable,
													String oldValue, String newValue) {
												BatchwiseReportController batchObj = new BatchwiseReportController();
												BatchDto batchDataObj = new BatchDto();
												batchDataObj.setBatchName(newValue);
												List<BatchwiseReportDto> batchReport = null;
												try {
													batchReport = batchObj.getBatchReport(batchDataObj);
												} catch (UIException e) {
													logger.info(e.getMessage());
													errorLabel.setText(e.getLocalizedMessage());
												} catch (SQLException e) {
													logger.info(e.getMessage());
													errorLabel.setText(e.getLocalizedMessage());
												}
												getBatchReport(batchReport, newValue);
											}
										});
								break;

							}
						}
					});

				}
			}

		}

	}

	/**
	 * @param event 
	 * Event handler function for upload button
	 */
	public void uploadAction(ActionEvent event) {
		list.getItems().clear();
		FileChooser fc = new FileChooser();
		fc.getExtensionFilters().addAll(new ExtensionFilter("Excel Files", "*.xlsx"));
		List<File> selectedFiles = fc.showOpenMultipleDialog(null);
		if (selectedFiles != null) {
			for (File selectedfile : selectedFiles) {
				int num = selectedfile.toString().lastIndexOf("\\");
				list.getItems().add(selectedfile.getName());
				new File("E:\\programs\\L7ConnecTeam\\Assessment Details\\").mkdir();
				String destination = "E:\\programs\\L7ConnecTeam\\Assessment Details\\"
						+ selectedfile.toString().substring(num + 1, selectedfile.toString().length());
				File dest = new File(destination);

				try {
					Files.copy(selectedfile.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
				} catch (IOException e) {
					list.getItems().clear();
					list.getItems().add("Error in uploading files. Please try again");
				}

			}
		} else {
			list.getItems().clear();
			list.getItems().add("Selected file is not valid");
		}

	}

	/**
	 * Event handler function for export button
	 * @param event
	 */
	public void exportAction(ActionEvent event) {
		BulkUploadProcessor bulkUploadObj = new BulkUploadProcessor();
		try {
			bulkUploadObj.uploadMasterExcel();
			list.getItems().clear();
			list.getItems().add("Upload complete. Please check ExcelUploadInfo.txt in E:/Assessment upload survey folder for more info");
		} catch (UIException e) {
			logger.info(e.getMessage());
			list.getItems().clear();
			list.getItems().add(e.getLocalizedMessage());
		} catch (SQLException e) {
			logger.info(e.getMessage());
			errorLabel.setText(e.getLocalizedMessage());
		}
			MoveDirectory moveDirObj = new MoveDirectory();
			moveDirObj.archiveUpload();
	}

	/**
	 * @param userDataObj 
	 * Method to generate individual report as table view
	 */
	@SuppressWarnings("unchecked")
	public void getIndividualReport(UserDto userDataObj) {
		TableView<IndividualReportDto> table = new TableView<IndividualReportDto>();

		TableColumn<IndividualReportDto, String> trainingGrp = new TableColumn<IndividualReportDto, String>("Training Group");
		trainingGrp.setCellValueFactory(new PropertyValueFactory<IndividualReportDto, String>("trainingGrpName"));
		trainingGrp.setMinWidth(100);

		TableColumn<IndividualReportDto, String> assessment = new TableColumn<IndividualReportDto, String>("Assessment");
		assessment.setCellValueFactory(new PropertyValueFactory<IndividualReportDto, String>("assessment"));
		assessment.setMinWidth(100);

		TableColumn<IndividualReportDto, Integer> minMarks = new TableColumn<IndividualReportDto, Integer>("Minimum Marks");
		minMarks.setCellValueFactory(new PropertyValueFactory<IndividualReportDto, Integer>("assessMinMark"));
		minMarks.setMinWidth(100);

		TableColumn<IndividualReportDto, Integer> maxMarks = new TableColumn<IndividualReportDto, Integer>("Maximum Marks");
		maxMarks.setCellValueFactory(new PropertyValueFactory<IndividualReportDto, Integer>("assessMaxMark"));
		maxMarks.setMinWidth(100);

		TableColumn<IndividualReportDto, Integer> marks = new TableColumn<IndividualReportDto, Integer>("Obtained Marks");
		marks.setCellValueFactory(new PropertyValueFactory<IndividualReportDto, Integer>("assessMark"));
		marks.setMinWidth(100);

		TableColumn<IndividualReportDto, Integer> percentage = new TableColumn<IndividualReportDto, Integer>(
				"Percentage Mark");
		percentage.setCellValueFactory(new PropertyValueFactory<IndividualReportDto, Integer>("assessPercentage"));
		percentage.setMinWidth(100);

		TableColumn<IndividualReportDto, String> status = new TableColumn<IndividualReportDto, String>("Status");
		status.setCellValueFactory(new PropertyValueFactory<IndividualReportDto, String>("status"));
		status.setMinWidth(100);

		table.getColumns().addAll(trainingGrp, assessment, minMarks, maxMarks, marks, percentage, status);
		table.setPlaceholder(new Label("No rows to display"));
		IndividualReportController reportObj = new IndividualReportController();
		List<IndividualReportDto> reportList = null;
		try {
			reportList = reportObj.getIndividualReport(userDataObj);
		} catch (UIException e) {
			logger.info(e.getMessage());
			;
			errorLabel.setText(e.getLocalizedMessage());
		} catch (SQLException e) {
			logger.info(e.getMessage());
			;
			errorLabel.setText(e.getLocalizedMessage());
		}

		ObservableList<IndividualReportDto> data = FXCollections.observableList(reportList);
		table.setItems(data);
		trainingGrp.setSortType(TableColumn.SortType.DESCENDING);
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		table.setEditable(false);
		ScrollPane scrollPane = new ScrollPane();
		scrollPane.setVbarPolicy(ScrollBarPolicy.ALWAYS);
		scrollPane.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
		scrollPane.setContent(table);
		borderPane.setCenter(scrollPane);
		scrollPane.setPadding(new Insets(80, 40, 0, 50));

	}

	/**
	 * Method to generate batch wise report as line graph
	 * 
	 * @param batchReport
	 * @param batchName
	 */
	public void getBatchReport(List<BatchwiseReportDto> batchReport, String batchName) {
		CategoryAxis xAxis = new CategoryAxis();
		NumberAxis yAxis = new NumberAxis(10, 100, 10);
		xAxis.setLabel("Trainee");
		yAxis.setLabel("Average Percentage Marks");
		LineChart<String, Number> lineChart = new LineChart<String, Number>(xAxis, yAxis);
		lineChart.setTitle("Trainee v/s Avg. Assessment Percentage");
		XYChart.Series<String, Number> series = new XYChart.Series<>();
		series.setName(batchName);

		for (BatchwiseReportDto report : batchReport) {
			series.getData().add(new XYChart.Data<String, Number>(report.getUserName(), report.getavgPercentMarks()));
		}

		lineChart.getData().add(series);
		anchorPaneUI.getChildren().add(lineChart);
		lineChart.setLayoutX(130);
		lineChart.setLayoutY(70);
	}

}
