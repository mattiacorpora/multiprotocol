package it.inaf.iasfpa.mpm.view;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
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
	private Button okButton, resetButton, clearButton, connectButton, disconnectButton,
					sendButton, receiveButton, sendReceiveButton, saveButton, clearButton2, saveButton2,
					openButton, runMonitoring, runControl, stopMonitoring, stopControl, createButton;

	@FXML
	private VBox dinamicVBox;

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
	
	@FXML
	private TextArea response, scriptArea, hystory;
	
	@FXML
	private ImageView schematic;

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
	private ScriptManager sm = new ScriptManager();
	private Stage dialogStage;
	private DataErrorControl ec = new DataErrorControl();
	private boolean reqC, ackM, ackC, stopev;
	private Thread m, c;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		resetButtonEvent();
		ackC = true;
		stopev = false;
		reqC = false;
	}
	
	protected void loadScriptText(Script script) {
		this.script = script;
		scriptArea.setText(sm.scriptToJsonString(this.script));
		dialogStage.close();
		runMonitoring.setDisable(false);
		runControl.setDisable(false);
		stopMonitoring.setDisable(false);
		stopControl.setDisable(false);
		
	}
	
	@FXML
	private void clearHystory2() {
		hystory.clear();
	}
	
	@FXML
	private void saveHystory2() {
		File sFile;
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Salva");
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Text Files", "*.txt"));
		sFile = fileChooser.showSaveDialog(null);

		FileWriter fw = null;
		try {
			fw = new FileWriter(sFile, true);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		BufferedWriter bw = new BufferedWriter(fw);
		String temp = hystory.getText();
		try {
			bw.write(temp);
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
			response.setDisable(false);
			sendButton.setDisable(false);
			receiveButton.setDisable(false);
			sendReceiveButton.setDisable(false);
			labelStatus.setText("Status: Connected");
			if (mode.compareTo("SPI") == 0) {
				sonContrSPI.clockfrequency.setDisable(true);
				sonContrSPI.spimode.setDisable(true);
				sonContrSPI.csStatusCombo.setDisable(true);
				nbyte.setDisable(false);
			}else if(mode.compareTo("UART") == 0) {
				sonContrUart.baudrateCombo.setDisable(true);
				sonContrUart.databitCombo.setDisable(true);
				sonContrUart.parityCombo.setDisable(true);
				sonContrUart.stopbitCombo.setDisable(true);
			}else if(mode.compareTo("I2C") == 0) {
				sonContrI2C.clockFrequencyCombo.setDisable(true);
				radioButtonAck.setDisable(false);
				radioButtonNack.setDisable(false);
				slaveAddress.setDisable(false);
				radioButtonAck.setSelected(true);
				parameters.setAckkreceive(true);
				nbyte.setDisable(false);
				scanBus();
			}else if(mode.compareTo("FIFO245") == 0) {
				sonContrFifo.asyncRadioButton.setDisable(true);
				sonContrFifo.syncRadioButton.setDisable(true);
				sendReceiveButton.setDisable(true);
			}
		}

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
		response.setDisable(true);
		sendButton.setDisable(true);
		receiveButton.setDisable(true);
		sendReceiveButton.setDisable(true);
		labelStatus.setText("Status: Disconnected");
		nbyte.setDisable(true);
		if (mode.compareTo("SPI") == 0) {
			sonContrSPI.clockfrequency.setDisable(false);
			sonContrSPI.spimode.setDisable(false);
			sonContrSPI.csStatusCombo.setDisable(false);
		}else if(mode.compareTo("UART") == 0) {
			sonContrUart.baudrateCombo.setDisable(false);
			sonContrUart.databitCombo.setDisable(false);
			sonContrUart.parityCombo.setDisable(false);
			sonContrUart.stopbitCombo.setDisable(false
					);
		}else if(mode.compareTo("I2C") == 0) {
			sonContrI2C.clockFrequencyCombo.setDisable(false);
			slaveAddress.setDisable(true);
		}else if(mode.compareTo("FIFO245") == 0) {
			sonContrFifo.asyncRadioButton.setDisable(false);
			sonContrFifo.syncRadioButton.setDisable(false);
			
		}

	}

	@FXML
	private void sendButtonEvent() {
		
		String sendData = request.getText();
		response.appendText("S: " + sendData + "\r" + "\n");
		if (hexFormat.isSelected()) {
			p.send(DataManager.hexStringToByte(sendData));
		} else if (charFormat.isSelected()) {
			if (mode.compareTo("UART") == 0) {
				p.send((sendData + "\r" + "\n").getBytes());
			} else {
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
				response.appendText("R: " + DataManager.ByteToHexString(buffer) + "\r" + "\n");
				
			} else if (charFormat.isSelected()) {
				if(mode.compareTo("UART") == 0) {
					response.appendText("R: " +DataManager.ByteToCharString(buffer));
				} else {
					response.appendText("R: " +DataManager.ByteToCharString(buffer) + "\r" + "\n");
				}
				
			}
		} else
			response.appendText("R: " + "Timeout" + "\r" + "\n");

	}

	@FXML
	private void sendReceiveButtonEvent() {
		
		String temp = request.getText();
		request.setText("");
		response.appendText("S: " + temp + "\r" + "\n");
		byte[] buffer = null;
		if (charFormat.isSelected()) {
			if (mode.compareTo("UART") == 0) {
				
				temp = temp + '\r' + '\n';
			}
			
			buffer = p.sendReceive(temp.getBytes());
			if (buffer != null) {
				if (mode.compareTo("UART") == 0) {
					response.appendText("R: " + DataManager.ByteToCharString(buffer));
				} else {
					response.appendText("R: " + DataManager.ByteToCharString(buffer) + '\r' + '\n' );
				}
					
				
			} else
				response.appendText("R: " + "Timeout" + '\r' + '\n');

		} else if (hexFormat.isSelected()) {
			
			buffer = p.sendReceive(DataManager.hexStringToByte(temp));
			if (buffer != null) {
				response.appendText("R: " + DataManager.ByteToHexString(buffer) + '\r' + '\n');
			} else
				response.appendText("R: " + "Timeout" + '\r' + '\n');

		}
	}
	
	@FXML
	private void clearButtonevent() {
		response.clear();
	}
	
	@FXML
	private void resetButtonEvent() {
		idDevice.setDisable(false);
		operationMode.setDisable(false);
		okButton.setDisable(false);
		dinamicVBox.getChildren().clear();
		ObservableList<String> operatiosModeOptions = FXCollections.observableArrayList("UART", "SPI", "I2C", "FIFO245");
		operationMode.setItems(operatiosModeOptions);
		ObservableList<Integer> idDeviceAvaible = FXCollections.observableArrayList(serv.getAvaiblePorts());
		idDevice.setItems(idDeviceAvaible);
		connectButton.setDisable(true);
		disconnectButton.setDisable(true);
		hexFormat.setDisable(true);
		charFormat.setDisable(true);
		request.setDisable(true);
		response.setDisable(true);
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
	
	
	@FXML
	public void saveButtonEvent() {
		File sFile;
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Salva");
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Text Files", "*.txt"));
		sFile = fileChooser.showSaveDialog(null);

		FileWriter fw = null;
		try {
			fw = new FileWriter(sFile, true);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		BufferedWriter bw = new BufferedWriter(fw);
		String temp = response.getText();
		try {
			bw.write(temp);
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
			dialogStage.setTitle("Gui2");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);
			if(mode.compareTo("I2C") == 0) {
				gui2Cont.setSlaveAddress(FXCollections.observableArrayList(parameters.getListSlaveAddress()));
			}
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
		scriptArea.setText(sm.scriptToJsonString(script));
		
		
	}
	
	public void scanBus() {
		ObservableList<Integer> slavesaddress = FXCollections.observableArrayList(parameters.getListSlaveAddress());
		slaveAddress.setItems(slavesaddress);
	}
	
	
	
	public void runSingleCommand(SingleCommand sl) {
		
		
		if(mode.compareTo("I2C") == 0) {
			parameters.setSlaveAddress(Integer.parseInt(sl.getAddress()));
		}
		
		switch (sl.getCmd()) {
		
		case "S":{
			
			byte[] tdata, tdata1, tdata2;
			
			if(sl.getMsgtype().compareTo("HEX") == 0) {
				tdata = DataManager.hexStringToByte(sl.getMsg()); 
			} else {
				tdata = (sl.getMsg()).getBytes();
			}
			
			if (sl.getCrc().compareTo("XOR")== 0) {
				tdata1 = new byte[tdata.length+1];
				System.arraycopy(tdata, 0, tdata1, 0, tdata.length);
				tdata1[tdata1.length-1] = ec.checkSum(tdata);
			} else if (sl.getCrc().compareTo("CRC16")== 0) {
				tdata1 = new byte[tdata.length+2];
				System.arraycopy(tdata, 0, tdata1, 0, tdata.length);
				System.arraycopy(ec.crc16Calc(tdata), 0, tdata1, tdata.length, 2);
			} else {
				tdata1 = tdata;
			}
			
			if (mode.compareTo("UART") == 0 && sl.getMsgtype().compareTo("CHAR") == 0) {
				tdata2 = new byte[tdata1.length+2];
				System.arraycopy(tdata1, 0, tdata2, 0, tdata1.length);
				tdata2[tdata2.length-2] = (byte)'\r';
				tdata2[tdata2.length-1] = (byte)'\n';
			}else {
				tdata2 = tdata1;
			}
			for(int i = 0; i < Integer.parseInt(sl.getRt()); i++) {
				p.send(tdata2);
				if (mode.compareTo("UART") != 0) {
				hystory.appendText("S: " + DataManager.ByteToCharString(tdata2) + "\r" + "\n");
				}
				if (mode.compareTo("UART") == 0) {
					if(sl.getMsgtype().compareTo("HEX") == 0) {
					hystory.appendText("S: " + DataManager.ByteToCharString(tdata2) + "\r" + "\n");
					}else {
						hystory.appendText("S: " + DataManager.ByteToCharString(tdata2));
					}
				}
			}
			break;
			
			
			}
		
		case "R":{
			if(mode.compareTo("I2C") == 0) {
				if(sl.getrOpt().compareTo("with ACK") == 0) {
					parameters.setAckkreceive(true);
				} else if(sl.getrOpt().compareTo("with ACK") == 0) {
					parameters.setAckkreceive(false);
				}
			}
			
			for(int i = 0; i < Integer.parseInt(sl.getRt()); i++) {
				byte[] buffer = null;
				buffer = p.receive();
				if (buffer != null) {
					if (sl.getMsgtype().compareTo("HEX") == 0) {
						hystory.appendText("R: " + DataManager.ByteToHexString(buffer) + "\r" + "\n");
						
					} else if (sl.getMsgtype().compareTo("CHAR") == 0) {
						if(mode.compareTo("UART") == 0) {
							hystory.appendText("R: " +DataManager.ByteToCharString(buffer));
						} else {
							hystory.appendText("R: " +DataManager.ByteToCharString(buffer) + "\r" + "\n");
						}
						
					}
				} else
					hystory.appendText("R: " + "Timeout" + "\r" + "\n");
			}
			
		break;
		}
		
		case "S/R":{
			
			byte[] tdata, tdata1, tdata2;
			
			if(sl.getMsgtype().compareTo("HEX") == 0) {
				tdata = DataManager.hexStringToByte(sl.getMsg()); 
			} else {
				tdata = (sl.getMsg()).getBytes();
			}
			
			if (sl.getCrc().compareTo("XOR")== 0) {
				tdata1 = new byte[tdata.length+1];
				System.arraycopy(tdata, 0, tdata1, 0, tdata.length);
				tdata1[tdata1.length-1] = ec.checkSum(tdata);
			} else if (sl.getCrc().compareTo("CRC16")== 0) {
				tdata1 = new byte[tdata.length+2];
				System.arraycopy(tdata, 0, tdata1, 0, tdata.length);
				System.arraycopy(ec.crc16Calc(tdata), 0, tdata1, tdata.length, 2);
			} else {
				tdata1 = tdata;
			}
			
			if (mode.compareTo("UART") == 0 && sl.getMsgtype().compareTo("CHAR") == 0) {
				tdata2 = new byte[tdata1.length+2];
				System.arraycopy(tdata1, 0, tdata2, 0, tdata1.length);
				tdata2[tdata2.length-2] = (byte)'\r';
				tdata2[tdata2.length-1] = (byte)'\n';
			}else {
				tdata2 = tdata1;
			}
			
			byte[] buffer = null;
			
			
			for(int i = 0; i < Integer.parseInt(sl.getRt()); i++) {
				buffer =p.sendReceive(tdata2);
				
				if (mode.compareTo("UART") != 0) {
				response.appendText("S: " + DataManager.ByteToCharString(tdata2) + "\r" + "\n");
				
				}
				if (mode.compareTo("UART") == 0) {
					if(sl.getMsgtype().compareTo("HEX") == 0) {
					hystory.appendText("S: " + DataManager.ByteToCharString(tdata2) + "\r" + "\n");
					}else {
						hystory.appendText("S: " + DataManager.ByteToCharString(tdata2));
					}
				}
				
				if (buffer != null) {
					if (sl.getMsgtype().compareTo("HEX") == 0) {
						hystory.appendText("R: " + DataManager.ByteToHexString(buffer) + "\r" + "\n");
						
					} else if (sl.getMsgtype().compareTo("CHAR") == 0) {
						if(mode.compareTo("UART") == 0) {
							hystory.appendText("R: " +DataManager.ByteToCharString(buffer));
						} else {
							hystory.appendText("R: " +DataManager.ByteToCharString(buffer) + "\r" + "\n");
						}
						
					}
				} else
					hystory.appendText("R: " + "Timeout" + "\r" + "\n");
			}
			break;
		}
	}
	}
	
	@FXML
	private void runMonitoringEvent() {
		
			
		if(script.getMonitoring().length != 0) {
			m = new Thread(new MonitoringThread());
			m.start();
		}
		
		
	}
	
	@FXML
	private void stopMonitoringEvent() {
		m.interrupt();
	}
	
	
	@FXML
	private void runControlEvent() {
		
		if(script.getControl().length != 0) {
			c = new Thread(new ControlThread());
			c.start();
			 
		}
	}
	
	@FXML
	private void stopControlEvent() {
		c.interrupt();
	}

	
	// inizio thread monitoring
	public class MonitoringThread implements Runnable{
		
		
		@Override
		public void run() {
			while(!stopev) {
				for(int i = 0; i< script.getMonitoring().length; i++) {
					if(!reqC) {
						try {
							Thread.sleep(Integer.parseInt(script.getMonitoring()[i].getWait()));
						} catch (NumberFormatException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						runSingleCommand(script.getMonitoring()[i]);
						
					}else {
						ackM = true;
						while(!ackC) {
							try {
								Thread.sleep(1);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}
				
				
			}
			
		}
		
	}
	//fine thread monitoring
	
	
	//inizio thread control
	public class ControlThread implements Runnable{

		@Override
		public void run() {
			while(!stopev) {
				reqC = true;
				while(!ackM) {
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				ackC = false;
				for(int i = 0; i < script.getControl().length; i++) {
					try {
						Thread.sleep(Integer.parseInt(script.getControl()[i].getWait()));
					} catch (NumberFormatException | InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					runSingleCommand(script.getControl()[i]);
				}
				ackC = true;
			}
			
		}
		
	}
	//fine thread control
	
	
	
//fine classe MainController	
}
