import javafx.beans.Observable;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.scene.control.SingleSelectionModel;

import javax.smartcardio.CardException;

public class TroikaAppModel
{
	/*��������� ������ �� ������ � �������.*/
	ProximityCoupligDevice		reader = new ProximityCoupligDevice();
	/*������ ��������� ����������.*/
	public ObservableList		readers;
	/*������������ ����� ���������� �����������. ���������������� ������� ������ � ������� ������.*/
	public SingleSelectionModel	terminalSelectionModel;
	
	/*������� � ChoiceBox ������ ��������� ����������.*/
	public void ShowTerminals() throws CardException
	{
		readers = FXCollections.observableArrayList(reader.GetTerminals());
	}
	
	/*���������� ��������� ��������.*/
	public void ReaderChoiceListener()
	{
		terminalSelectionModel.selectedIndexProperty().addListener((Observable observable) -> 
        {
			try
			{
				reader.ConnectTerminal(terminalSelectionModel.selectedIndexProperty().getValue());
			}
			catch(CardException e)
			{
				
			}
		});
	}
}