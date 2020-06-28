import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.geometry.VPos;
import javafx.scene.paint.Color;

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
	ChoiceBox<CardTerminal> readersList;
	/*Кнопка отображения доступных терминалов.*/
	Button listReadersButton;
	/*Кнопка подлючения терминала.*/
	Button connectTerminalButton;
	/*Отображение считанных данных.*/
	ScrollPane dataSPane;
	/*Данные.*/
	Text textRef;
	
	public static void main(String[] args)
	{
		Application.launch(args);
	}
	
	public void start(Stage stage) throws CardException
	{
		/*Список доступных терминалов.*/
		readersList  = new ChoiceBox<CardTerminal>();
		readersList.setLayoutX(5);
		readersList.setLayoutY(5);
		readersList.setPrefWidth(200);
		
		/*Кнопка подключения доступных терминалов.*/
		listReadersButton = new Button("s");
		listReadersButton.setLayoutX(210);
		listReadersButton.setLayoutY(5);
		listReadersButton.setPrefWidth(25);
		listReadersButton.setOnAction((e) ->
		{
			/*Подключить терминалы.*/
			try
			{
				taModel.ShowTerminals();
			}
			catch(CardException exc)
			{
				System.out.println(exc.getCause());
			}
			
			/*Инициализировать выпадающий список.*/
			if (taModel.readers != null)
			{
				readersList.setItems(taModel.readers);
				/*Инициализация модели переключения терминалов таким свойством, при котором можно выбрать только один из них.*/
				taModel.terminalSelectionModel = readersList.getSelectionModel();
				/*По умолчанию выбрать первый терминал из списка.*/
				taModel.terminalSelectionModel.selectFirst();
			}
		});
		/*Подключить терминал.*/
		connectTerminalButton = new Button("c");
		connectTerminalButton.setLayoutX(240);
		connectTerminalButton.setLayoutY(5);
		connectTerminalButton.setPrefWidth(25);
		connectTerminalButton.setOnAction((e) ->
		{
			taModel.ConnectTerminal();
		});
		
		textRef = new Text("047B490A396780084400120111003319"
							+	"00000000000000000000000000000000"
							+	"00000000000000000000000000000000"
							+	"0000000000007C378800000000000000"
							+	"00000000000000000000000000000000"
							+	"00000000000000000000000000000000"
							+	"00000000000000000000000000000000"
							+	"0000000000007C378800000000000000"
							+	"77327728230000003062657000000000"
							+	"047B490A396780084400120111003319"
							+	"00000000000000000000000000000000"
							+	"00000000000000000000000000000000"
							+	"0000000000007C378800000000000000"
							+	"00000000000000000000000000000000"
							+	"00000000000000000000000000000000"
							+	"00000000000000000000000000000000"
							+	"0000000000007C378800000000000000"
							+	"77327728230000003062657000000000"
							+	"00000000000000000000000000000000"
							+	"00000000000000000000000000000000"
							+	"0000000000007C378800000000000000"
							+	"00000000000000000000000000000000"
							+	"00000000000000000000000000000000"
							+	"00000000000000000000000000000000"
							+	"0000000000007C378800000000000000"
							+	"77327728230000003062657000000000"
							+	"047B490A396780084400120111003319"
							+	"00000000000000000000000000000000"
							+	"00000000000000000000000000000000"
							+	"0000000000007C378800000000000000"
							+	"00000000000000000000000000000000"
							+	"00000000000000000000000000000000"
							+	"00000000000000000000000000000000"
							+	"0000000000007C378800000000000000"
							+	"77327728230000003062657000000000"
							+	"00000000000000000000000000000000"
							+	"00000000000000000000000000000000"
							+	"0000000000007C378800000000000000"
							+	"00000000000000000000000000000000"
							+	"00000000000000000000000000000000"
							+	"00000000000000000000000000000000"
							+	"0000000000007C378800000000000000"
							+	"77327728230000003062657000000000"
							+	"047B490A396780084400120111003319"
							+	"00000000000000000000000000000000"
							+	"00000000000000000000000000000000"
							+	"0000000000007C378800000000000000"
							+	"00000000000000000000000000000000"
							+	"00000000000000000000000000000000"
							+	"00000000000000000000000000000000"
							+	"0000000000007C378800000000000000"
							+	"77327728230000003062657000000000"
		);
		textRef.setTextOrigin(VPos.TOP);
		/*Равномерное горизонтальное выравнивание текста по обеим сторонам окна видимости.*/
		textRef.setTextAlignment(TextAlignment.JUSTIFY);
		/*Ограничение длины строки - автоматический перенос. Значение подогнано под ширину поля clip узла Group textGroup.*/
		textRef.setWrappingWidth(215);
		/*Поле fill встречается у многих узлов и ему можно передать цвет, паттерн и еще что-то.*/
		//textRef.setFill(Color.rgb(255, 128, 65));
		/*Отображение считанных данных.*/
		dataSPane = new ScrollPane();
		dataSPane.setLayoutX(5);
		dataSPane.setLayoutY(35);
		dataSPane.setPrefWidth(590);
		dataSPane.setPrefHeight(760);
		dataSPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
		dataSPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
		dataSPane.setContent(textRef);
		
		
		/*Сцена.*/
		Group root	= new Group(readersList, listReadersButton, connectTerminalButton, dataSPane);
		Scene scene	= new Scene(root, 600, 800);
		stage.setScene(scene);
		stage.setTitle("Тройка");
		stage.show();
	}
}