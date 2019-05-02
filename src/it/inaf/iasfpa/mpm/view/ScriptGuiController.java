package it.inaf.iasfpa.mpm.view;

import java.net.URL;
import java.util.ResourceBundle;

import it.inaf.iasfpa.mpm.utils.Script;
import it.inaf.iasfpa.mpm.utils.ScriptManager;
import it.inaf.iasfpa.mpm.utils.SingleLine;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class ScriptGuiController implements Initializable {
	
	@FXML
	private TextField message, numberRepeat, waitTime;
	
	@FXML
	private Button addLine, open, save, load, removeLine;
	
	@FXML
	private RadioButton sendRB, receiveRB, sendRecRB;
	
	@FXML
	private CheckBox repeatBox, waitBox, threadBox;
	
	@FXML
	private ComboBox<String> errorControl, mtype, format, slaveAddress, recOptCombo;
	
	@FXML
	private TableView<SingleLine> monitoring, control;
		 
	
	public boolean i2cMode = false;
	private ObservableList<SingleLine> listMonitoring = FXCollections.observableArrayList();
	private ObservableList<SingleLine> listcontrol = FXCollections.observableArrayList();
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		sendRB.setSelected(true);
		ObservableList<String> errorOpt = FXCollections.observableArrayList("XOR", "CRC16", "None");
		errorControl.setItems(errorOpt);
		ObservableList<String> formatOpt = FXCollections.observableArrayList("HEX", "CHAR");
		format.setItems(formatOpt);
		ObservableList<String> recOpt = FXCollections.observableArrayList("with ACK", "with NAK", "None");
		recOptCombo.setItems(recOpt);
		recOptCombo.setValue(recOpt.get(2));
		
		format.setValue(formatOpt.get(0));
		errorControl.setValue(errorOpt.get(2));
		numberRepeat.setText("1");
		waitTime.setText("0");
		// inizio costruzione tabelle
		TableColumn<SingleLine, String> cmd = new TableColumn<SingleLine, String>("CMD");
		cmd.setCellValueFactory(new PropertyValueFactory<>("cmd"));
		TableColumn<SingleLine, String> wait = new TableColumn<SingleLine, String>("WAIT");
		wait.setCellValueFactory(new PropertyValueFactory<>("wait"));
		TableColumn<SingleLine, String> rt = new TableColumn<SingleLine, String>("RT");
		rt.setCellValueFactory(new PropertyValueFactory<>("rt"));
		TableColumn<SingleLine, String> type = new TableColumn<SingleLine, String>("MSG TYPE");
		type.setCellValueFactory(new PropertyValueFactory<>("msgtype"));
		TableColumn<SingleLine, String> msg = new TableColumn<SingleLine, String>("MSG");
		msg.setCellValueFactory(new PropertyValueFactory<>("msg"));
		TableColumn<SingleLine, String> address = new TableColumn<SingleLine, String>("ADDRESS");
		address.setCellValueFactory(new PropertyValueFactory<>("address"));
		TableColumn<SingleLine, String> rOpt = new TableColumn<SingleLine, String>("REC OPT");
		rOpt.setCellValueFactory(new PropertyValueFactory<>("rOpt"));
		TableColumn<SingleLine, String> checksum = new TableColumn<SingleLine, String>("CHECKSUM");
		checksum.setCellValueFactory(new PropertyValueFactory<>("crc"));
		
		TableColumn<SingleLine, String> cmd1 = new TableColumn<SingleLine, String>("CMD");
		cmd1.setCellValueFactory(new PropertyValueFactory<>("cmd"));
		TableColumn<SingleLine, String> wait1 = new TableColumn<SingleLine, String>("WAIT");
		wait1.setCellValueFactory(new PropertyValueFactory<>("wait"));
		TableColumn<SingleLine, String> rt1 = new TableColumn<SingleLine, String>("RT");
		rt1.setCellValueFactory(new PropertyValueFactory<>("rt"));
		TableColumn<SingleLine, String> type1 = new TableColumn<SingleLine, String>("MSG TYPE");
		type1.setCellValueFactory(new PropertyValueFactory<>("msgtype"));
		TableColumn<SingleLine, String> msg1 = new TableColumn<SingleLine, String>("MSG");
		msg1.setCellValueFactory(new PropertyValueFactory<>("msg"));
		TableColumn<SingleLine, String> address1 = new TableColumn<SingleLine, String>("ADDRESS");
		address1.setCellValueFactory(new PropertyValueFactory<>("address"));
		TableColumn<SingleLine, String> rOpt1 = new TableColumn<SingleLine, String>("REC OPT");
		rOpt1.setCellValueFactory(new PropertyValueFactory<>("rOpt"));
		TableColumn<SingleLine, String> checksum1 = new TableColumn<SingleLine, String>("CHECKSUM");
		checksum1.setCellValueFactory(new PropertyValueFactory<>("crc"));
		
		monitoring.getColumns().addAll(cmd, wait, rt, type, msg, address, rOpt, checksum);
		control.getColumns().addAll(cmd1, wait1, rt1, type1, msg1, address1, rOpt1, checksum1);
		monitoring.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		control.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		monitoring.setItems(listMonitoring);
		control.setItems(listcontrol);
		// fine costruzione tabelle
		System.out.println(recOptCombo.getSelectionModel().getSelectedIndex());
	}
	
	@FXML
	private void addButtonEvent() {
		String tempcmd = null;
		String rept = null;
		String wt = null;
		String sa = null;
		String ro = null;
		if(sendRB.isSelected()) {
			tempcmd = "S";
		}
		else if(receiveRB.isSelected()) {
			tempcmd = "R";
		} else if(sendRecRB.isSelected()) {
			tempcmd = "S/R";
		}
		
		if(repeatBox.isSelected()) {
			rept = numberRepeat.getText();
		} else {
			rept = "none";
		}
		
		if(waitBox.isSelected()) {
			wt = waitTime.getText();
		} else {
			wt = "none";
		}
		
		if(i2cMode) {
			sa = slaveAddress.getValue();
			ro = recOptCombo.getValue();
		} else{
			sa = "none";
			ro = "none";
		}
		
		
		
		if(threadBox.isSelected() == false) {
			listcontrol.add(new SingleLine(tempcmd, wt, rept, format.getValue(),
					message.getText(), ro, sa, errorControl.getValue()));
			
		}
		else if(threadBox.isSelected() == true) {
			listMonitoring.add(new SingleLine(tempcmd, wt, rept, format.getValue(),
					message.getText(), ro, sa, errorControl.getValue()));
					
		}
			
	}
	
	@FXML
	private void removeLineButtonEvent() {
		SingleLine rmlineM = monitoring.getSelectionModel().getSelectedItem();
		int mIndex = monitoring.getSelectionModel().getSelectedIndex();
		SingleLine rmlineC = control.getSelectionModel().getSelectedItem();
		int cIndex = control.getSelectionModel().getSelectedIndex();
		if (rmlineM != null) {
			listMonitoring.remove(mIndex);
			
		}
		
		if (rmlineC != null) {
			listcontrol.remove(cIndex);
			
		}
		
	}
	
	@FXML
	private void saveButtonEvent() {
		Script script = new Script();
		ScriptManager sm = new ScriptManager();
		SingleLine[] mon = new SingleLine[listMonitoring.size()];
		SingleLine[] con = new SingleLine[listcontrol.size()];
		for(int i = 0; i < mon.length; i++) {
			mon[i] = listMonitoring.get(i);
		}
		for(int i = 0; i < con.length; i++) {
			con[i] = listcontrol.get(i);
		}
		script.setControl(con);
		script.setMonitoring(mon);
		sm.createFileScript(script);
		
		
	}

}
