import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Button;

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
	ChoiceBox<CardTerminal> readersChoiceBox;
	/*������ ����������� ��������� ����������.*/
	Button btnPCDChoice;
	
	public static void main(String[] args)
	{
		Application.launch(args);
	}
	
	public void start(Stage stage) throws CardException
	{
		/*������ ��������� ����������.*/
		readersChoiceBox  = new ChoiceBox<CardTerminal>();
		readersChoiceBox.setLayoutX(5);
		readersChoiceBox.setLayoutY(5);
		readersChoiceBox.setPrefWidth(200);
		
		/*������ ����������� ��������� ����������.*/
		btnPCDChoice = new Button();
		btnPCDChoice.setLayoutX(210);
		btnPCDChoice.setLayoutY(5);
		btnPCDChoice.setPrefWidth(25);
		btnPCDChoice.setOnAction((e) ->
		{
			/*���������� ���������.*/
			try
			{
				taModel.ShowTerminals();
			}
			catch(CardException exc)
			{
				System.out.println("������: ��� ��������� ����������.");
			}
			/*���������������� ���������� ������.*/
			try
			{
				readersChoiceBox.setItems(taModel.readers);
			}
			catch(Throwable exc)
			{
				System.out.println("������: ����� readersChoiceBox.setItems(taModel.readers).");
			}
			/*������������� ������ ������������ ���������� ����� ���������, ��� ������� ����� ������� ������ ���� �� ���.*/
			taModel.terminalSelectionModel = readersChoiceBox.getSelectionModel();
			/*�� ��������� ������� ������ �������� �� ������.*/
			taModel.terminalSelectionModel.selectFirst();
		});

		/*�����.*/
		Group root	= new Group(readersChoiceBox, btnPCDChoice);
		Scene scene	= new Scene(root, 600, 800);
		stage.setScene(scene);
		stage.setTitle("������");
		stage.show();
	}
}