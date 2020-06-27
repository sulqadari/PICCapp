import javafx.beans.Observable;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.scene.control.SingleSelectionModel;

import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;

public class TroikaAppModel
{
	/*��������� ������ �� ������ � �������.*/
	ProximityCoupligDevice		reader = new ProximityCoupligDevice();
	/*������ ��������� ����������.*/
	private ObservableList<CardTerminal>		readers;
	/*������������ ����� ���������� �����������. ���������������� ������� ������ � ������� ������.*/
	private SingleSelectionModel	terminalSelectionModel;
	
	/*������� � ChoiceBox ������ ��������� ����������.*/
	public void ShowTerminals() throws CardException, NullPointerException
	{
		readers = FXCollections.observableArrayList(reader.GetTerminalsList());
	}
}