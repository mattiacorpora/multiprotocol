package it.inaf.iasfpa.mpm.view;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import it.inaf.iasfpa.mpm.MainApp;
import it.inaf.iasfpa.mpm.um232hmanager.FIFOUm232H;
import it.inaf.iasfpa.mpm.um232hmanager.I2CUm232H;
import it.inaf.iasfpa.mpm.um232hmanager.ProtocolInterface;
import it.inaf.iasfpa.mpm.um232hmanager.SPIUm232H;
import it.inaf.iasfpa.mpm.um232hmanager.UARTUm232H;
import it.inaf.iasfpa.mpm.um232hmanager.UM232HCommon;
import it.inaf.iasfpa.mpm.um232hmanager.UM232HDeviceParameters;
import it.inaf.iasfpa.mpm.utils.DataErrorControl;
import it.inaf.iasfpa.mpm.utils.DataManager;
import it.inaf.iasfpa.mpm.utils.Script;
import it.inaf.iasfpa.mpm.utils.ScriptManager;
import it.inaf.iasfpa.mpm.utils.SingleCommand;
import it.inaf.iasfpa.mpm.utils.SingleCommandLog;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.sf.yad2xx.Device;
import net.sf.yad2xx.FTDIException;
import net.sf.yad2xx.FTDIInterface;

public class MainController implements Initializable {

	private UM232HCommon serv = new UM232HCommon();

	@FXML
	private Button okButton, resetButton, clearButton, connectButton, disconnectButton, sendButton, receiveButton,
			sendReceiveButton, saveButton, clearButton2, saveButton2, openButton, runMonitoring, runControl,
			stopMonitoring, stopControl, createButton;

	@FXML
	private VBox dinamicVBox;

	@FXML
	private Tab single, multi;
	@FXML
	private ComboBox<String> operationMode;

	@FXML
	private ComboBox<Integer> idDevice;

	@FXML
	private ComboBox<Integer> slaveAddress;

	@FXML
	private RadioButton hexFormat, charFormat, radioButtonAck, radioButtonNack;

	@FXML
	private Label labelStatus;

	@FXML
	private TextField request, nbyte;

	// @FXML
	// private TextArea response, hystory;

	@FXML
	private ImageView schematic;

	@FXML
	private TableView<SingleCommand> tableControl, tableMonitoring;

	@FXML
	private TableView<SingleCommandLog> historyMultiTable, historySingleTable;
	private ObservableList<SingleCommandLog> historyListSingle = FXCollections.observableArrayList();
	private ObservableList<SingleCommandLog> historyListMulti = FXCollections.observableArrayList();
	private Device dev;
	private ProtocolInterface p;
	private UM232HDeviceParameters parameters = new UM232HDeviceParameters();
	private String mode;
	private UartController sonContrUart;
	private I2cController sonContrI2C;
	private SpiController sonContrSPI;
	private FifoController sonContrFifo;
	private ScriptGuiController gui2Cont;
	private Stage primaryStage;
	protected Script script;
	private Stage dialogStage;
	private DataErrorControl ec = new DataErrorControl();
	private boolean reqC, ackM, ackC, stopM, stopC;
	private Thread m, c;
	private boolean ve = true;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss.SSS");

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		resetButtonEvent();
		
		
		TableColumn<SingleCommandLog, String> tm = new TableColumn<SingleCommandLog, String>("TIME");
		tm.setCellValueFactory(new PropertyValueFactory<>("istant"));
		TableColumn<SingleCommandLog, String> thrsrc = new TableColumn<SingleCommandLog, String>("THREAD");
		thrsrc.setCellValueFactory(new PropertyValueFactory<>("threadSrc"));
		TableColumn<SingleCommandLog, String> reqd = new TableColumn<SingleCommandLog, String>("REQUEST");
		reqd.setCellValueFactory(new PropertyValueFactory<>("request"));
		TableColumn<SingleCommandLog, String> resd = new TableColumn<SingleCommandLog, String>("RESPONSE");
		resd.setCellValueFactory(new PropertyValueFactory<>("response"));
		historySingleTable.getColumns().addAll(tm, reqd, resd);
		historySingleTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		historySingleTable.setItems(historyListSingle);

		TableColumn<SingleCommandLog, String> tm1 = new TableColumn<SingleCommandLog, String>("TIME");
		tm1.setCellValueFactory(new PropertyValueFactory<>("istant"));
		TableColumn<SingleCommandLog, String> thrsrc1 = new TableColumn<SingleCommandLog, String>("THREAD");
		thrsrc1.setCellValueFactory(new PropertyValueFactory<>("threadSrc"));
		TableColumn<SingleCommandLog, String> reqd1 = new TableColumn<SingleCommandLog, String>("REQUEST");
		reqd1.setCellValueFactory(new PropertyValueFactory<>("request"));
		TableColumn<SingleCommandLog, String> resd1 = new TableColumn<SingleCommandLog, String>("RESPONSE");
		resd1.setCellValueFactory(new PropertyValueFactory<>("response"));
		historyMultiTable.getColumns().addAll(tm1, thrsrc1, reqd1, resd1);
		historyMultiTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		historyMultiTable.setItems(historyListMulti);

	}

	protected void loadScriptText(Script script) {
		this.script = script;
		ObservableList<SingleCommand> listMonitoring = FXCollections.observableArrayList();
		ObservableList<SingleCommand> listcontrol = FXCollections.observableArrayList();
		listMonitoring.addAll(script.getMonitoring());
		listcontrol.addAll(script.getControl());

		madeTable(listMonitoring, listcontrol);
		dialogStage.close();
		runMonitoring.setDisable(false);
		runControl.setDisable(false);
		stopMonitoring.setDisable(false);
		stopControl.setDisable(false);

	}

	@FXML
	private void clearHystory2() {
		historyListMulti.clear();
		// hystory.clear();
	}

	@FXML
	private void saveHystory2() {
		File sFile;
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save");
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("CSV Files", "*.csv"));
		sFile = fileChooser.showSaveDialog(null);

		FileWriter fw = null;
		try {
			fw = new FileWriter(sFile, true);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		BufferedWriter bw = new BufferedWriter(fw);

		// String temp = hystory.getText();
		try {
			bw.write(tableHistoryMToCsv());
			bw.flush();
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@FXML
	private void okButtonEvent() {

		String path = null;
		dev = null;
		p = null;
		try {
			dev = FTDIInterface.getDevices()[idDevice.getValue()];
			serv.programEeprom(dev, operationMode.getValue());

		} catch (FTDIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		mode = operationMode.getValue();
		switch (mode) {
		case "UART":
			path = "view/uartdef.fxml";
			p = new UARTUm232H();
			break;
		case "SPI":
			path = "view/spidef.fxml";
			p = new SPIUm232H();
			break;
		case "I2C":
			path = "view/i2cdef.fxml";
			p = new I2CUm232H();
			break;
		case "FIFO245":
			path = "view/fifodef.fxml";
			p = new FIFOUm232H();
			break;
		// case "FT128":
		// path = "view/ft128.fxml";
		// break;
		// case "OPTO ISOLATE":
		// path = "view/optoisolate.fxml";
		// break;
		// case "CPU FIFO":
		// path = "view/cpufifo.fxml";
		// break;
		default:
			path = "view/uart.fxml";
			p = new UARTUm232H();
			break;
		}

		p.setUM232HDevice(dev);

		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(MainApp.class.getResource(path));
		try {
			dinamicVBox.getChildren().add((VBox) loader.load());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		switch (mode) {
		case "UART":
			sonContrUart = (UartController) loader.getController();
			sonContrUart.setParameters(parameters);
			p.configureUM232HDevice(parameters);
			break;
		case "SPI":
			sonContrSPI = (SpiController) loader.getController();
			sonContrSPI.setParameters(parameters);
			p.configureUM232HDevice(parameters);
			break;
		case "I2C":
			sonContrI2C = (I2cController) loader.getController();
			sonContrI2C.setParameters(parameters);
			p.configureUM232HDevice(parameters);
			break;
		case "FIFO245":
			sonContrFifo = (FifoController) loader.getController();
			parameters.setSyncMode(true);
			sonContrFifo.setParameters(parameters);
			p.configureUM232HDevice(parameters);
			sendReceiveButton.setDisable(true);
		}

		okButton.setDisable(true);
		operationMode.setDisable(true);
		idDevice.setDisable(true);
		connectButton.setDisable(false);
		hexFormat.setSelected(true);
		resetButton.setDisable(false);
		clearButton.setDisable(false);
		openButton.setDisable(false);
		createButton.setDisable(false);
	}

	@FXML
	private void exitButtonEvent() {
		System.exit(0);
	}

	@FXML
	private void connectButtonEvent() {

		if (p.connect()) {
			connectButton.setDisable(true);
			disconnectButton.setDisable(false);
			hexFormat.setDisable(false);
			charFormat.setDisable(false);
			request.setDisable(false);
			// response.setDisable(false);
			sendButton.setDisable(false);
			receiveButton.setDisable(false);
			sendReceiveButton.setDisable(false);
			labelStatus.setText("Status: Connected");
			if (mode.compareTo("SPI") == 0) {
				sonContrSPI.clockfrequency.setDisable(true);
				sonContrSPI.spimode.setDisable(true);
				sonContrSPI.csStatusCombo.setDisable(true);
				nbyte.setDisable(false);
				if (Integer.parseInt(sonContrSPI.mtuField.getText()) == 0) {
					parameters.setDeviceBufferSize(65536);
				} else {
					parameters.setDeviceBufferSize(Integer.parseInt(sonContrSPI.mtuField.getText()));
				}
				parameters.setDelaypack(Integer.parseInt(sonContrSPI.delayField.getText()));
				sonContrSPI.mtuField.setDisable(true);
				sonContrSPI.delayField.setDisable(true);
			} else if (mode.compareTo("UART") == 0) {
				sonContrUart.baudrateCombo.setDisable(true);
				sonContrUart.databitCombo.setDisable(true);
				sonContrUart.parityCombo.setDisable(true);
				sonContrUart.stopbitCombo.setDisable(true);
				if (Integer.parseInt(sonContrUart.mtuField.getText()) == 0) {
					parameters.setDeviceBufferSize(65536);
				} else {
					parameters.setDeviceBufferSize(Integer.parseInt(sonContrUart.mtuField.getText()));
				}
				parameters.setDelaypack(Integer.parseInt(sonContrUart.delayField.getText()));
				sonContrUart.mtuField.setDisable(true);
				sonContrUart.delayField.setDisable(true);
			} else if (mode.compareTo("I2C") == 0) {
				sonContrI2C.clockFrequencyCombo.setDisable(true);
				radioButtonAck.setDisable(false);
				radioButtonNack.setDisable(false);
				slaveAddress.setDisable(false);
				radioButtonAck.setSelected(true);
				parameters.setAckkreceive(true);
				nbyte.setDisable(false);
				if (Integer.parseInt(sonContrI2C.mtuField.getText()) == 0) {
					parameters.setDeviceBufferSize(65536);
				} else {
					parameters.setDeviceBufferSize(Integer.parseInt(sonContrI2C.mtuField.getText()));
				}
				parameters.setDelaypack(Integer.parseInt(sonContrI2C.delayField.getText()));
				sonContrI2C.mtuField.setDisable(true);
				sonContrI2C.delayField.setDisable(true);
				scanBus();
			} else if (mode.compareTo("FIFO245") == 0) {
				sonContrFifo.asyncRadioButton.setDisable(true);
				sonContrFifo.syncRadioButton.setDisable(true);
				sendReceiveButton.setDisable(true);
				if (Integer.parseInt(sonContrFifo.mtuField.getText()) == 0) {
					parameters.setDeviceBufferSize(65536);
				} else {
					parameters.setDeviceBufferSize(Integer.parseInt(sonContrFifo.mtuField.getText()));
				}
				parameters.setDelaypack(Integer.parseInt(sonContrFifo.delayField.getText()));
				sonContrFifo.mtuField.setDisable(true);
				sonContrFifo.delayField.setDisable(true);
			}
		}

		multi.setDisable(false);
		single.setDisable(false);
		createButton.setDisable(false);
		openButton.setDisable(false);

	}

	@FXML
	private void disconnectButtonEvent() {
		p.disconnect();
		radioButtonAck.setDisable(true);
		radioButtonNack.setDisable(true);
		connectButton.setDisable(false);
		disconnectButton.setDisable(true);
		hexFormat.setDisable(true);
		charFormat.setDisable(true);
		request.setDisable(true);
		// response.setDisable(true);
		sendButton.setDisable(true);
		receiveButton.setDisable(true);
		sendReceiveButton.setDisable(true);
		labelStatus.setText("Status: Disconnected");
		nbyte.setDisable(true);
		if (mode.compareTo("SPI") == 0) {
			sonContrSPI.clockfrequency.setDisable(false);
			sonContrSPI.spimode.setDisable(false);
			sonContrSPI.csStatusCombo.setDisable(false);
			sonContrSPI.mtuField.setDisable(false);
			sonContrSPI.delayField.setDisable(false);
		} else if (mode.compareTo("UART") == 0) {
			sonContrUart.baudrateCombo.setDisable(false);
			sonContrUart.databitCombo.setDisable(false);
			sonContrUart.parityCombo.setDisable(false);
			sonContrUart.stopbitCombo.setDisable(false);
			sonContrUart.mtuField.setDisable(false);
			sonContrUart.delayField.setDisable(false);
		} else if (mode.compareTo("I2C") == 0) {
			sonContrI2C.clockFrequencyCombo.setDisable(false);
			slaveAddress.setDisable(true);
			sonContrI2C.mtuField.setDisable(false);
			sonContrI2C.delayField.setDisable(false);
		} else if (mode.compareTo("FIFO245") == 0) {
			sonContrFifo.asyncRadioButton.setDisable(false);
			sonContrFifo.syncRadioButton.setDisable(false);
			sonContrFifo.mtuField.setDisable(false);
			sonContrFifo.delayField.setDisable(false);

		}
		multi.setDisable(true);
		single.setDisable(true);

	}

	@FXML
	private void sendButtonEvent() {

		String sendData = request.getText();

		if (hexFormat.isSelected()) {
			historyListSingle.add(new SingleCommandLog(sdf.format(new Date()), null, sendData, "-"));
			p.send(DataManager.hexStringToByte(sendData));
		} else if (charFormat.isSelected()) {
			if (mode.compareTo("UART") == 0) {
				historyListSingle.add(new SingleCommandLog(sdf.format(new Date()), null, sendData + "\r" + "\n", "-"));
				p.send((sendData + "\r" + "\n").getBytes());
			} else {
				historyListSingle.add(new SingleCommandLog(sdf.format(new Date()), null, sendData, "-"));
				p.send(sendData.getBytes());
			}

		}
		request.setText("");

	}

	@FXML
	private void receiveButtonEvent() {

		byte[] buffer = null;
		buffer = p.receive();
		if (buffer != null) {
			if (hexFormat.isSelected()) {
				historyListSingle.add(
						new SingleCommandLog(sdf.format(new Date()), null, "-", DataManager.ByteToHexString(buffer)));
			} else if (charFormat.isSelected()) {

				historyListSingle.add(
						new SingleCommandLog(sdf.format(new Date()), null, "-", DataManager.ByteToCharString(buffer)));
			}
		} else

			historyListSingle.add(new SingleCommandLog(sdf.format(new Date()), null, "-", "TIMEOUT"));

	}

	@FXML
	private void sendReceiveButtonEvent() {

		String temp = request.getText();
		request.setText("");
		// response.appendText("S: " + temp + "\r" + "\n");

		byte[] buffer = null;
		if (charFormat.isSelected()) {
			if (mode.compareTo("UART") == 0) {

				temp = temp + '\r' + '\n';
			}

			buffer = p.sendReceive(temp.getBytes());
			if (buffer != null) {
				historyListSingle.add(
						new SingleCommandLog(sdf.format(new Date()), null, temp, DataManager.ByteToCharString(buffer)));

			} else
				historyListSingle.add(new SingleCommandLog(sdf.format(new Date()), null, temp, "TIMEOUT"));

		} else if (hexFormat.isSelected()) {

			buffer = p.sendReceive(DataManager.hexStringToByte(temp));
			if (buffer != null) {
				historyListSingle.add(
						new SingleCommandLog(sdf.format(new Date()), null, temp, DataManager.ByteToHexString(buffer)));

			} else
				historyListSingle.add(new SingleCommandLog(sdf.format(new Date()), null, temp, "TIMEOUT"));
			// response.appendText("R: " + "Timeout" + '\r' + '\n');

		}
	}

	@FXML
	private void clearButtonevent() {
		historyListSingle.clear();
		// response.clear();
	}

	@FXML
	private void resetButtonEvent() {
		if (p != null) {
			disconnectButtonEvent();
		}
		ve = false;
		ObservableList<String> operatiosModeOptions = FXCollections.observableArrayList("UART", "SPI", "I2C",
				"FIFO245");
		operationMode.setItems(operatiosModeOptions);
		ObservableList<Integer> idDeviceAvaible = FXCollections.observableArrayList(serv.getAvaiblePorts());
		idDevice.setItems(idDeviceAvaible);
		idDevice.setDisable(false);
		operationMode.setDisable(false);
		okButton.setDisable(false);
		dinamicVBox.getChildren().clear();
		connectButton.setDisable(true);
		disconnectButton.setDisable(true);
		hexFormat.setDisable(true);
		charFormat.setDisable(true);
		request.setDisable(true);
		// response.setDisable(true);
		sendButton.setDisable(true);
		receiveButton.setDisable(true);
		sendReceiveButton.setDisable(true);
		radioButtonAck.setDisable(true);
		radioButtonNack.setDisable(true);
		labelStatus.setText("Status: Disconnected");
		parameters.setClockrate(100000);
		parameters.setNumbRecByte(1);
		clearButton.setDisable(true);
		nbyte.setDisable(true);
		slaveAddress.setDisable(true);
		resetButton.setDisable(true);
		nbyte.setText("1");
		nbyte.textProperty().addListener((obs, oldText, newText) -> {
			parameters.setNumbRecByte(Integer.parseInt(nbyte.getText()));

		});

		openButton.setDisable(true);
		createButton.setDisable(true);
		runMonitoring.setDisable(true);
		runControl.setDisable(true);
		stopMonitoring.setDisable(true);
		stopControl.setDisable(true);
		multi.setDisable(true);
		single.setDisable(true);
		createButton.setDisable(true);
		openButton.setDisable(true);

		ackC = true;
		stopM = false;
		reqC = false;
		ackM = false;
		stopC = false;
		ve = true;

	}

	@FXML
	private void multiTabEvent() {
		hexFormat.setDisable(true);
		charFormat.setDisable(true);
		radioButtonAck.setDisable(true);
		radioButtonNack.setDisable(true);
		nbyte.setDisable(true);
		slaveAddress.setDisable(true);

	}

	@FXML
	private void singleTabEvent() {
		hexFormat.setDisable(false);
		charFormat.setDisable(false);
		radioButtonAck.setDisable(false);
		radioButtonNack.setDisable(false);
		nbyte.setDisable(false);
		slaveAddress.setDisable(false);

	}

	@FXML
	public void receiveWithAckEvent(ActionEvent event) {
		parameters.setAckkreceive(true);
	}

	@FXML
	public void receiveWithNackEvent(ActionEvent event) {
		parameters.setAckkreceive(false);
	}

	@FXML
	public void slaveAddressComboEvent(ActionEvent event) {
		parameters.setSlaveAddress(slaveAddress.getValue());
	}

	@FXML
	public void modeSelectedEvent(ActionEvent event) {
		if (ve) {
			Image img;
			switch (operationMode.getValue()) {
			case "UART":
				img = new Image("file:./schematic_img/uart.PNG", true);
				break;
			case "SPI":
				img = new Image("file:./schematic_img/spi.PNG", true);
				break;
			case "I2C":
				img = new Image("file:./schematic_img/i2c.PNG", true);
				break;
			case "FIFO245":
				img = new Image("file:./schematic_img/fifo.PNG", true);
				break;
			default:
				img = new Image("file:./schematic_img/uart.PNG", true);

			}
			schematic.setImage(img);
			schematic.setVisible(true);
		}

	}

	@FXML
	public void saveButtonEvent() {
		File sFile;
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Salva");
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("CSV Files", "*.csv"));
		sFile = fileChooser.showSaveDialog(null);

		FileWriter fw = null;
		try {
			fw = new FileWriter(sFile, true);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		BufferedWriter bw = new BufferedWriter(fw);

		try {
			bw.write(tableHistorySToCsv());
			bw.flush();
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@FXML
	private void createButtonEvent() {
		try {

			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/script_gui.fxml"));
			AnchorPane page = (AnchorPane) loader.load();
			gui2Cont = loader.getController();
			gui2Cont.setMain(this);
			dialogStage = new Stage();
			dialogStage.setTitle("Script Creator");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);
			if (mode.compareTo("I2C") == 0) {
				gui2Cont.i2cMode = true;
			}
			if (mode.compareTo("I2C") == 0) {
				gui2Cont.setSlaveAddress(FXCollections.observableArrayList(parameters.getListSlaveAddress()));
			}
			gui2Cont.i2cModeSelected();
			dialogStage.showAndWait();

		} catch (IOException e) {
			e.printStackTrace();

		}
	}

	@FXML
	private void openButtonEvent() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open");
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Json File", "*.json"));
		ScriptManager sm = new ScriptManager();
		this.script = sm.scriptFromJson(fileChooser.showOpenDialog(null));

		ObservableList<SingleCommand> listMonitoring = FXCollections.observableArrayList();
		ObservableList<SingleCommand> listcontrol = FXCollections.observableArrayList();
		listMonitoring.addAll(script.getMonitoring());
		listcontrol.addAll(script.getControl());
		madeTable(listMonitoring, listcontrol);
		runMonitoring.setDisable(false);
		runControl.setDisable(false);
		stopMonitoring.setDisable(false);
		stopControl.setDisable(false);

	}

	@FXML
	private void runMonitoringEvent() {

		if (script.getMonitoring().length != 0) {
			m = new Thread(new MonitoringThread());
			runMonitoring.setDisable(true);
			stopM = false;
			m.start();
		}

	}

	@FXML
	private void stopMonitoringEvent() {
		stopM = true;

	}

	@FXML
	private void runControlEvent() {

		if (script.getControl().length != 0) {
			c = new Thread(new ControlThread());
			stopC = false;
			runControl.setDisable(true);
			c.start();

		}
	}

	@FXML
	private void stopControlEvent() {
		stopC = true;
	}

	public void scanBus() {
		ObservableList<Integer> slavesaddress = FXCollections.observableArrayList(parameters.getListSlaveAddress());
		slaveAddress.setItems(slavesaddress);
	}

	// inizio run single command

	public void runSingleCommand(SingleCommand sl) {

		if (mode.compareTo("I2C") == 0) {
			parameters.setSlaveAddress(Integer.parseInt(sl.getAddress()));
		}

		switch (sl.getCmd()) {

		case "S": {

			byte[] tdata, tdata1, tdata2;

			if (sl.getMsgtype().compareTo("HEX") == 0) {
				tdata = DataManager.hexStringToByte(sl.getMsg());
			} else {
				tdata = (sl.getMsg()).getBytes();
			}

			if (sl.getCrc().compareTo("XOR") == 0) {
				tdata1 = new byte[tdata.length + 1];
				System.arraycopy(tdata, 0, tdata1, 0, tdata.length);
				tdata1[tdata1.length - 1] = ec.checkSum(tdata);
			} else if (sl.getCrc().compareTo("CRC16") == 0) {
				tdata1 = new byte[tdata.length + 2];
				System.arraycopy(tdata, 0, tdata1, 0, tdata.length);
				System.arraycopy(ec.crc16Calc(tdata), 0, tdata1, tdata.length, 2);
			} else {
				tdata1 = tdata;
			}

			if (mode.compareTo("UART") == 0 && sl.getMsgtype().compareTo("CHAR") == 0) {
				tdata2 = new byte[tdata1.length + 2];
				System.arraycopy(tdata1, 0, tdata2, 0, tdata1.length);
				tdata2[tdata2.length - 2] = (byte) '\r';
				tdata2[tdata2.length - 1] = (byte) '\n';
			} else {
				tdata2 = tdata1;
			}
			for (int i = 0; i < Integer.parseInt(sl.getRt()); i++) {

				if (sl.getMsgtype().compareTo("HEX") == 0) {
					historyListMulti.add(new SingleCommandLog(sdf.format(new Date()), ((reqC && ackM ) ? "C" : "M"),
							DataManager.ByteToHexString(tdata2), "-"));

				} else {

					historyListMulti.add(new SingleCommandLog(sdf.format(new Date()), ((reqC && ackM ) ? "C" : "M"),
							DataManager.ByteToCharString(tdata2), "-"));
				}
				p.send(tdata2);

			}
			break;

		}

		case "R": {
			if (mode.compareTo("I2C") == 0) {
				if (sl.getRoptc().compareTo("withACK") == 0) {
					parameters.setAckkreceive(true);
				} else if (sl.getRoptc().compareTo("withNACK") == 0) {
					parameters.setAckkreceive(false);
				}
			}

			for (int i = 0; i < Integer.parseInt(sl.getRt()); i++) {
				byte[] buffer = null;
				buffer = p.receive();
				if (buffer != null) {
					if (sl.getMsgtype().compareTo("HEX") == 0) {
						historyListMulti.add(new SingleCommandLog(sdf.format(new Date()), ((reqC && ackM ) ? "C" : "M"), "-",
								DataManager.ByteToHexString(buffer)));

					} else if (sl.getMsgtype().compareTo("CHAR") == 0) {

						historyListMulti.add(new SingleCommandLog(sdf.format(new Date()), ((reqC && ackM ) ? "C" : "M"), "-",
								DataManager.ByteToCharString(buffer)));

					}
				} else
					historyListMulti
							.add(new SingleCommandLog(sdf.format(new Date()), ((reqC && ackM ) ? "C" : "M"), "-", "TIMEOUT"));
			}

			break;
		}

		case "S/R": {

			byte[] tdata, tdata1, tdata2;

			if (sl.getMsgtype().compareTo("HEX") == 0) {
				tdata = DataManager.hexStringToByte(sl.getMsg());
			} else {
				tdata = (sl.getMsg()).getBytes();
			}

			if (sl.getCrc().compareTo("XOR") == 0) {
				tdata1 = new byte[tdata.length + 1];
				System.arraycopy(tdata, 0, tdata1, 0, tdata.length);
				tdata1[tdata1.length - 1] = ec.checkSum(tdata);
			} else if (sl.getCrc().compareTo("CRC16") == 0) {
				tdata1 = new byte[tdata.length + 2];
				System.arraycopy(tdata, 0, tdata1, 0, tdata.length);
				System.arraycopy(ec.crc16Calc(tdata), 0, tdata1, tdata.length, 2);
			} else {
				tdata1 = tdata;
			}

			if (mode.compareTo("UART") == 0 && sl.getMsgtype().compareTo("CHAR") == 0) {
				tdata2 = new byte[tdata1.length + 2];
				System.arraycopy(tdata1, 0, tdata2, 0, tdata1.length);
				tdata2[tdata2.length - 2] = (byte) '\r';
				tdata2[tdata2.length - 1] = (byte) '\n';
			} else {
				tdata2 = tdata1;
			}

			byte[] buffer = null;

			for (int i = 0; i < Integer.parseInt(sl.getRt()); i++) {
				buffer = p.sendReceive(tdata2);
				if (buffer != null) {

					if (sl.getMsgtype().compareTo("HEX") == 0) {
						historyListMulti.add(new SingleCommandLog(sdf.format(new Date()), ((reqC && ackM ) ? "C" : "M"),
								DataManager.ByteToHexString(tdata2), DataManager.ByteToHexString(buffer)));

					} else {
						historyListMulti.add(new SingleCommandLog(sdf.format(new Date()), ((reqC && ackM ) ? "C" : "M"),
								DataManager.ByteToCharString(tdata2), DataManager.ByteToCharString(buffer)));
					}

				} else {
					if (sl.getMsgtype().compareTo("HEX") == 0) {
						historyListMulti.add(new SingleCommandLog(sdf.format(new Date()), ((reqC && ackM ) ? "C" : "M"),
								DataManager.ByteToHexString(tdata2), "TIMEOUT"));

					} else {
						historyListMulti.add(new SingleCommandLog(sdf.format(new Date()), ((reqC && ackM ) ? "C" : "M"),
								DataManager.ByteToCharString(tdata2), "TIMEOUT"));
					}
					
				}

			}
			break;
		}
		}
	}
	// fine run single command

	private void madeTable(ObservableList<SingleCommand> listMonitoring, ObservableList<SingleCommand> listcontrol) {
		// costruzione tabella monitoring

		if (!tableMonitoring.getColumns().isEmpty()) {
			tableMonitoring.getColumns().remove(0, tableMonitoring.getColumns().size());
		}
		TableColumn<SingleCommand, String> cmd = new TableColumn<SingleCommand, String>("CMD");
		cmd.setCellValueFactory(new PropertyValueFactory<>("cmd"));
		TableColumn<SingleCommand, String> wait = new TableColumn<SingleCommand, String>("WAIT");
		wait.setCellValueFactory(new PropertyValueFactory<>("wait"));
		TableColumn<SingleCommand, String> rt = new TableColumn<SingleCommand, String>("RT");
		rt.setCellValueFactory(new PropertyValueFactory<>("rt"));
		TableColumn<SingleCommand, String> type = new TableColumn<SingleCommand, String>("MSG TYPE");
		type.setCellValueFactory(new PropertyValueFactory<>("msgtype"));
		TableColumn<SingleCommand, String> msg = new TableColumn<SingleCommand, String>("MSG");
		msg.setCellValueFactory(new PropertyValueFactory<>("msg"));
		TableColumn<SingleCommand, String> address = new TableColumn<SingleCommand, String>("ADDRESS");
		address.setCellValueFactory(new PropertyValueFactory<>("address"));
		TableColumn<SingleCommand, String> recOptc = new TableColumn<SingleCommand, String>("REC OPT");
		recOptc.setCellValueFactory(new PropertyValueFactory<>("Optc"));
		TableColumn<SingleCommand, String> checksum = new TableColumn<SingleCommand, String>("CHECKSUM");
		checksum.setCellValueFactory(new PropertyValueFactory<>("crc"));

		tableMonitoring.getColumns().add(cmd);
		tableMonitoring.getColumns().add(wait);
		tableMonitoring.getColumns().add(rt);
		tableMonitoring.getColumns().add(type);
		tableMonitoring.getColumns().add(msg);
		tableMonitoring.getColumns().add(address);
		tableMonitoring.getColumns().add(recOptc);
		tableMonitoring.getColumns().add(checksum);
		tableMonitoring.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		tableMonitoring.setItems(listMonitoring);

		if (!tableControl.getColumns().isEmpty()) {
			tableControl.getColumns().remove(0, tableControl.getColumns().size());
		}
		TableColumn<SingleCommand, String> cmd1 = new TableColumn<SingleCommand, String>("CMD");
		cmd1.setCellValueFactory(new PropertyValueFactory<>("cmd"));
		TableColumn<SingleCommand, String> wait1 = new TableColumn<SingleCommand, String>("WAIT");
		wait1.setCellValueFactory(new PropertyValueFactory<>("wait"));
		TableColumn<SingleCommand, String> rt1 = new TableColumn<SingleCommand, String>("RT");
		rt1.setCellValueFactory(new PropertyValueFactory<>("rt"));
		TableColumn<SingleCommand, String> type1 = new TableColumn<SingleCommand, String>("MSG TYPE");
		type1.setCellValueFactory(new PropertyValueFactory<>("msgtype"));
		TableColumn<SingleCommand, String> msg1 = new TableColumn<SingleCommand, String>("MSG");
		msg1.setCellValueFactory(new PropertyValueFactory<>("msg"));
		TableColumn<SingleCommand, String> address1 = new TableColumn<SingleCommand, String>("ADDRESS");
		address1.setCellValueFactory(new PropertyValueFactory<>("address"));
		TableColumn<SingleCommand, String> recOptc1 = new TableColumn<SingleCommand, String>("REC OPT");
		recOptc1.setCellValueFactory(new PropertyValueFactory<>("Optc"));
		TableColumn<SingleCommand, String> checksum1 = new TableColumn<SingleCommand, String>("CHECKSUM");
		checksum1.setCellValueFactory(new PropertyValueFactory<>("crc"));
		tableControl.getColumns().add(cmd1);
		tableControl.getColumns().add(wait1);
		tableControl.getColumns().add(rt1);
		tableControl.getColumns().add(type1);
		tableControl.getColumns().add(msg1);
		tableControl.getColumns().add(address1);
		tableControl.getColumns().add(recOptc1);
		tableControl.getColumns().add(checksum1);
		tableControl.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		tableControl.setItems(listcontrol);
	}

	public String tableHistorySToCsv() {
		String temp = "TIME" + '\t' + "REQUEST" + '\t' + "RESPONSE" + '\r' + '\n';
		for (int i = 0; i < historyListSingle.size(); i++) {
			temp = temp + historyListSingle.get(i).getIstant() + '\t' + historyListSingle.get(i).getRequest() + '\t'
					+ historyListSingle.get(i).getResponse() + '\r' + '\n';
		}
		return temp;
	}

	public String tableHistoryMToCsv() {
		String temp = "TIME" + '\t' + "THREAD" + '\t' + "REQUEST" + '\t' + "RESPONSE" + '\r' + '\n';
		for (int i = 0; i < historyListSingle.size(); i++) {
			temp = temp + historyListSingle.get(i).getIstant() + '\t' + historyListSingle.get(i).getThreadSrc() + '\t'
					+ historyListSingle.get(i).getRequest() + '\t' + historyListSingle.get(i).getResponse() + '\r'
					+ '\n';
		}
		return temp;
	}

	// inizio thread monitoring
	public class MonitoringThread implements Runnable {

		@Override
		public void run() {
			while (!stopM) {
				for (int i = 0; i < script.getMonitoring().length; i++) {
					if (!reqC) {
						try {
							Thread.sleep(Integer.parseInt(script.getMonitoring()[i].getWait()));
						} catch (NumberFormatException | InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						runSingleCommand(script.getMonitoring()[i]);

					} else {
						i--;
						ackM = true;
						while (!ackC) {
							try {
								Thread.sleep(10);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}

			}
			runMonitoring.setDisable(false);

		}

	}
	// fine thread monitoring

	// inizio thread control
	public class ControlThread implements Runnable {

		@Override
		public void run() {

			reqC = true;
			while (!ackM) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			ackC = false;
			for (int i = 0; i < script.getControl().length; i++) {
				if (!stopC) {
					try {
						Thread.sleep(Integer.parseInt(script.getControl()[i].getWait()));
					} catch (NumberFormatException | InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					runSingleCommand(script.getControl()[i]);
				} else {
					break;
				}
			}

			ackC = true;
			reqC = false;
			runControl.setDisable(false);

		}

	}
	// fine thread control

	// fine classe MainController
}
