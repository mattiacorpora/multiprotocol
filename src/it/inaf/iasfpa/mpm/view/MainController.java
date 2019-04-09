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
import it.inaf.iasfpa.mpm.utils.DataManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import net.sf.yad2xx.Device;
import net.sf.yad2xx.FTDIException;
import net.sf.yad2xx.FTDIInterface;



public class MainController implements Initializable {

	private UM232HCommon serv = new UM232HCommon();

	@FXML
	private Button okButton, resetButton, clearButton, connectButton, disconnectButton, sendButton, receiveButton, sendReceiveButton, saveButton;

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
	private TextArea response;
	
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
	

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		resetButtonEvent();
		
				
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
	
	public void scanBus() {
		ObservableList<Integer> slavesaddress = FXCollections.observableArrayList(parameters.getListSlaveAddress());
		slaveAddress.setItems(slavesaddress);
	}

}
