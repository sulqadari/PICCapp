import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Button;

import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
/**
*	ГПИ для приложения "Тройка"
*/
public class TroikaAppMain extends Application
{
	/*Класс-контейнер состояния приложения.*/
	TroikaAppModel taModel = new TroikaAppModel();
	
	/*Выпадающий список выбора жанра музыки.*/
	ChoiceBox<CardTerminal> readersChoiceBox;
	/*Кнопка подключения доступных терминалов.*/
	Button btnPCDChoice;
	
	public static void main(String[] args)
	{
		Application.launch(args);
	}
	
	public void start(Stage stage) throws CardException
	{
		/*Список доступных терминалов.*/
		readersChoiceBox  = new ChoiceBox<CardTerminal>();
		readersChoiceBox.setLayoutX(5);
		readersChoiceBox.setLayoutY(5);
		readersChoiceBox.setPrefWidth(200);
		
		/*Кнопка подключения доступных терминалов.*/
		btnPCDChoice = new Button();
		btnPCDChoice.setLayoutX(210);
		btnPCDChoice.setLayoutY(5);
		btnPCDChoice.setPrefWidth(25);
		btnPCDChoice.setOnAction((e) ->
		{
			/*Подключить терминалы.*/
			try
			{
				taModel.ShowTerminals();
			}
			catch(CardException exc)
			{
				System.out.println("ОШИБКА: нет доступных терминалов.");
			}
			/*Инициализировать выпадающий список.*/
			try
			{
				readersChoiceBox.setItems(taModel.readers);
			}
			catch(Throwable exc)
			{
				System.out.println("ОШИБКА: метод readersChoiceBox.setItems(taModel.readers).");
			}
			/*Инициализация модели переключения терминалов таким свойством, при котором можно выбрать только один из них.*/
			taModel.terminalSelectionModel = readersChoiceBox.getSelectionModel();
			/*По умолчанию выбрать первый терминал из списка.*/
			taModel.terminalSelectionModel.selectFirst();
		});

		/*Сцена.*/
		Group root	= new Group(readersChoiceBox, btnPCDChoice);
		Scene scene	= new Scene(root, 600, 800);
		stage.setScene(scene);
		stage.setTitle("Тройка");
		stage.show();
	}
}