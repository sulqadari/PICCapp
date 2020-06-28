import javafx.beans.Observable;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.scene.control.SingleSelectionModel;

import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;

public class TroikaAppModel
{
	/*Экземпляр класса по работе с ридером.*/
	public ProximityCoupligDevice 		reader = new ProximityCoupligDevice();
	/*Список доступных терминалов.*/
	public ObservableList<CardTerminal>	readers;
	/*Переключение между доступными терминалами. Инициализируется в основном классе.*/
	public SingleSelectionModel			terminalSelectionModel;
	
	/*Вывести в ChoiceBox список доступных терминалов.*/
	public void ShowTerminals() throws CardException
	{
		readers = FXCollections.observableArrayList(reader.GetTerminalsList());
	}
	
	/*Подключить терминал.*/
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