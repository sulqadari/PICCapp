import javafx.beans.Observable;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.scene.control.SingleSelectionModel;

import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;

public class TroikaAppModel
{
	/*Ёкземпл€р класса по работе с ридером.*/
	ProximityCoupligDevice		reader = new ProximityCoupligDevice();
	/*—писок доступных терминалов.*/
	private ObservableList<CardTerminal>		readers;
	/*ѕереключение между доступными терминалами. »нициализируетс€ моделью выбора в главном классе.*/
	private SingleSelectionModel	terminalSelectionModel;
	
	/*¬ывести в ChoiceBox список доступных терминалов.*/
	public void ShowTerminals() throws CardException, NullPointerException
	{
		readers = FXCollections.observableArrayList(reader.GetTerminalsList());
	}
}