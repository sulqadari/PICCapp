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
*	��� ��� ���������� "������"
*/
public class TroikaAppMain extends Application
{
	/*�����-��������� ��������� ����������.*/
	TroikaAppModel taModel = new TroikaAppModel();
	/*���������� ������ ������ ����� ������.*/
	ChoiceBox<CardTerminal> readersList;
	/*������ ����������� ��������� ����������.*/
	Button listReadersButton;
	/*������ ���������� ���������.*/
	Button connectTerminalButton;
	/*����������� ��������� ������.*/
	ScrollPane dataSPane;
	/*������.*/
	Text textRef;
	
	public static void main(String[] args)
	{
		Application.launch(args);
	}
	
	public void start(Stage stage) throws CardException
	{
		/*������ ��������� ����������.*/
		readersList  = new ChoiceBox<CardTerminal>();
		readersList.setLayoutX(5);
		readersList.setLayoutY(5);
		readersList.setPrefWidth(200);
		
		/*������ ����������� ��������� ����������.*/
		listReadersButton = new Button("s");
		listReadersButton.setLayoutX(210);
		listReadersButton.setLayoutY(5);
		listReadersButton.setPrefWidth(25);
		listReadersButton.setOnAction((e) ->
		{
			/*���������� ���������.*/
			try
			{
				taModel.ShowTerminals();
			}
			catch(CardException exc)
			{
				System.out.println(exc.getCause());
			}
			
			/*���������������� ���������� ������.*/
			if (taModel.readers != null)
			{
				readersList.setItems(taModel.readers);
				/*������������� ������ ������������ ���������� ����� ���������, ��� ������� ����� ������� ������ ���� �� ���.*/
				taModel.terminalSelectionModel = readersList.getSelectionModel();
				/*�� ��������� ������� ������ �������� �� ������.*/
				taModel.terminalSelectionModel.selectFirst();
			}
		});
		/*���������� ��������.*/
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
		/*����������� �������������� ������������ ������ �� ����� �������� ���� ���������.*/
		textRef.setTextAlignment(TextAlignment.JUSTIFY);
		/*����������� ����� ������ - �������������� �������. �������� ��������� ��� ������ ���� clip ���� Group textGroup.*/
		textRef.setWrappingWidth(215);
		/*���� fill ����������� � ������ ����� � ��� ����� �������� ����, ������� � ��� ���-��.*/
		//textRef.setFill(Color.rgb(255, 128, 65));
		/*����������� ��������� ������.*/
		dataSPane = new ScrollPane();
		dataSPane.setLayoutX(5);
		dataSPane.setLayoutY(35);
		dataSPane.setPrefWidth(590);
		dataSPane.setPrefHeight(760);
		dataSPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
		dataSPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
		dataSPane.setContent(textRef);
		
		
		/*�����.*/
		Group root	= new Group(readersList, listReadersButton, connectTerminalButton, dataSPane);
		Scene scene	= new Scene(root, 600, 800);
		stage.setScene(scene);
		stage.setTitle("������");
		stage.show();
	}
}