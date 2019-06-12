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
import net.sf.yad2xx.mpsse.I2C;

public class I2cController implements Initializable {

	@FXML
	public ComboBox<Integer> clockFrequencyCombo;
	
	@FXML
	public TextField mtuField, delayField;
	
	
	
	
	private UM232HDeviceParameters param;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		ObservableList<Integer> clockrateoption = FXCollections.observableArrayList(I2C.ONE_HUNDRED_KHZ,
				I2C.FOUR_HUNDRED_KHZ);
		clockFrequencyCombo.setItems(clockrateoption);
		clockFrequencyCombo.setValue(I2C.ONE_HUNDRED_KHZ);
		mtuField.setText("0");
		delayField.setText("0");
		                                                                                                                                                                                                                                                                          
		
	}
	
	public void setParameters(UM232HDeviceParameters param) {
		this.param = param;
		this.param.setClockrate(I2C.ONE_HUNDRED_KHZ);
		this.param.setAckkreceive(true);
		this.param.setDeviceBufferSize(655536);
		this.param.setDelaypack(0);
		
	}
	
	
	
	@FXML
	public void clockFrequencyComboEvent(ActionEvent event) {
		param.setClockrate(clockFrequencyCombo.getValue());
	}
	
	
	
	

}
