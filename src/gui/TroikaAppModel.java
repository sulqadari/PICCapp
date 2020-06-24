import javafx.beans.Observable;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.scene.control.SingleSelectionModel;

import javax.smartcardio.CardException;

public class TroikaAppModel
{
	/*Экземпляр класса по работе с ридером.*/
	ProximityCoupligDevice		reader = new ProximityCoupligDevice();
	/*Список доступных терминалов.*/
	public ObservableList		readers;
	/*Переключение между доступными терминалами. Инициализируется моделью выбора в главном классе.*/
	public SingleSelectionModel	terminalSelectionModel;
	
	/*Вывести в ChoiceBox список доступных терминалов.*/
	public void ShowTerminals() throws CardException
	{
		readers = FXCollections.observableArrayList(reader.GetTerminals());
	}
	
	/*Подключить выбранный терминал.*/
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