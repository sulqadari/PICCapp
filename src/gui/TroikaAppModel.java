import javafx.beans.Observable;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.scene.control.SingleSelectionModel;

import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;

public class TroikaAppModel
{
	/*��������� ������ �� ������ � �������.*/
	public ProximityCoupligDevice 		reader = new ProximityCoupligDevice();
	/*������ ��������� ����������.*/
	public ObservableList<CardTerminal>	readers;
	/*������������ ����� ���������� �����������. ���������������� � �������� ������.*/
	public SingleSelectionModel			terminalSelectionModel;
	
	/*������� � ChoiceBox ������ ��������� ����������.*/
	public void ShowTerminals() throws CardException
	{
		readers = FXCollections.observableArrayList(reader.GetTerminalsList());
	}
	
	/*���������� ��������.*/
	public void ConnectTerminal()
	{
		
		terminalSelectionModel.selectedIndexProperty().addListener((Observable observable) ->
		{
			int selectedIndex = terminalSelectionModel.selectedIndexProperty().get();
			try
			{
				reader.ConnectTerminal(selectedIndex);
			}
			catch(CardException exc)
			{
				System.out.println(exc.getMessage());
			}
			
		});
	}
}