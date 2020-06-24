import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Group;

import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Button;

import javax.smartcardio.CardException;

/**
*	ГПИ для приложения "Тройка"
*/
public class TroikaAppMain extends Application
{
	/*Класс-контейнер состояния приложения.*/
	TroikaAppModel taModel = new TroikaAppModel();
	
	/*Выпадающий список выбора жанра музыки.*/
	ChoiceBox readersChoiceBox;
	/*Кнопка подключения доступных терминалов.*/
	Button buttonReadersChoiceBox;
	
	public static void main(String[] args)
	{
		Application.launch(args);
	}
	
	public void start(Stage stage) throws CardException
	{
		/*Подключить терминалы.*/
		taModel.ShowTerminals();
		
		/*Кнопка подключения доступных терминалов.*/
		buttonReadersChoiceBox = new Button();
		buttonReadersChoiceBox.setLayoutX(210);
		buttonReadersChoiceBox.setLayoutY(5);
		buttonReadersChoiceBox.setPrefWidth(25);
		
		/*Список доступных терминалов.*/
		readersChoiceBox  = new ChoiceBox();
		readersChoiceBox.setLayoutX(5);
		readersChoiceBox.setLayoutY(5);
		readersChoiceBox.setPrefWidth(200);
		readersChoiceBox.setItems(taModel.readers);
		
		/*Инициализация модели переключения терминалов таким свойством, при котором можно выбрать только один из них.*/
		taModel.terminalSelectionModel = readersChoiceBox.getSelectionModel();
		/*По умолчанию выбрать первый терминал из списка.*/
		taModel.terminalSelectionModel.selectFirst();
		/*Переключение между доступными терминалами.*/
		taModel.ReaderChoiceListener();
		
		/*Сцена.*/
		Group root	= new Group(readersChoiceBox, buttonReadersChoiceBox);
		Scene scene	= new Scene(root, 600, 800);
		stage.setScene(scene);
		stage.setTitle("Тройка");
		stage.show();
	}
}