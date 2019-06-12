package it.inaf.iasfpa.mpm.view;

import java.net.URL;
import java.util.ResourceBundle;

import it.inaf.iasfpa.mpm.um232hmanager.UM232HDeviceParameters;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import net.sf.yad2xx.FTDIConstants;

public class UartController implements Initializable {
	
	
	@FXML
	protected ComboBox<String> stopbitCombo, parityCombo;
	
	@FXML
	protected ComboBox<Integer> baudrateCombo, databitCombo;
	
	@FXML
	public TextField mtuField, delayField;
	
	private UM232HDeviceParameters param;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ObservableList<String> parityOptions = FXCollections.observableArrayList("None", "Even", "Odd", "Mark",
				"Space");
		ObservableList<String> stopBitOptions = FXCollections.observableArrayList("1", "2");
		ObservableList<Integer> dataBitOption = FXCollections.observableArrayList(7, 8);
		ObservableList<Integer> baudrateOption = FXCollections.observableArrayList(FTDIConstants.FT_BAUD_300,
				FTDIConstants.FT_BAUD_600, FTDIConstants.FT_BAUD_1200, FTDIConstants.FT_BAUD_2400,
				FTDIConstants.FT_BAUD_4800, FTDIConstants.FT_BAUD_9600, FTDIConstants.FT_BAUD_14400,
				FTDIConstants.FT_BAUD_19200, FTDIConstants.FT_BAUD_38400, FTDIConstants.FT_BAUD_57600,
				FTDIConstants.FT_BAUD_115200, FTDIConstants.FT_BAUD_230400, FTDIConstants.FT_BAUD_460800,
				FTDIConstants.FT_BAUD_921600);
		baudrateCombo.setItems(baudrateOption);
		parityCombo.setItems(parityOptions);
		stopbitCombo.setItems(stopBitOptions);
		databitCombo.setItems(dataBitOption);
		parityCombo.setValue("Even");
		databitCombo.setValue(8);
		stopbitCombo.setValue("1");
		baudrateCombo.setValue(FTDIConstants.FT_BAUD_9600);
		mtuField.setText("0");
		delayField.setText("0");
		
	}
	
	@FXML
	private void baudRateComboEvent(ActionEvent event) {
		param.setBaudrate(baudrateCombo.getValue());
	}
	
	@FXML
	private void databitComboEvent(ActionEvent event) {
		param.setDatabit(databitCombo.getValue());
	}
	
	@FXML
	private void parityComboEvent(ActionEvent event) {
		param.setParity(parityCombo.getValue());
	}
	
	@FXML
	private void stopbitComboEvent(ActionEvent event) {
		param.setStopbit(stopbitCombo.getValue());
	}
	
	public void setParameters(UM232HDeviceParameters param) {
		this.param = param;
		this.param.setBaudrate(FTDIConstants.FT_BAUD_9600);
		this.param.setStopbit("1");
		this.param.setParity("Even");
		this.param.setDatabit(FTDIConstants.FT_BITS_8);
		this.param.setDeviceBufferSize(65536);
		this.param.setDelaypack(0);
	}

}
