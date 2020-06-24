import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Group;

import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Button;

import javax.smartcardio.CardException;

/**
*	��� ��� ���������� "������"
*/
public class TroikaAppMain extends Application
{
	/*�����-��������� ��������� ����������.*/
	TroikaAppModel taModel = new TroikaAppModel();
	
	/*���������� ������ ������ ����� ������.*/
	ChoiceBox readersChoiceBox;
	/*������ ����������� ��������� ����������.*/
	Button buttonReadersChoiceBox;
	
	public static void main(String[] args)
	{
		Application.launch(args);
	}
	
	public void start(Stage stage) throws CardException
	{
		/*���������� ���������.*/
		taModel.ShowTerminals();
		
		/*������ ����������� ��������� ����������.*/
		buttonReadersChoiceBox = new Button();
		buttonReadersChoiceBox.setLayoutX(210);
		buttonReadersChoiceBox.setLayoutY(5);
		buttonReadersChoiceBox.setPrefWidth(25);
		
		/*������ ��������� ����������.*/
		readersChoiceBox  = new ChoiceBox();
		readersChoiceBox.setLayoutX(5);
		readersChoiceBox.setLayoutY(5);
		readersChoiceBox.setPrefWidth(200);
		readersChoiceBox.setItems(taModel.readers);
		
		/*������������� ������ ������������ ���������� ����� ���������, ��� ������� ����� ������� ������ ���� �� ���.*/
		taModel.terminalSelectionModel = readersChoiceBox.getSelectionModel();
		/*�� ��������� ������� ������ �������� �� ������.*/
		taModel.terminalSelectionModel.selectFirst();
		/*������������ ����� ���������� �����������.*/
		taModel.ReaderChoiceListener();
		
		/*�����.*/
		Group root	= new Group(readersChoiceBox, buttonReadersChoiceBox);
		Scene scene	= new Scene(root, 600, 800);
		stage.setScene(scene);
		stage.setTitle("������");
		stage.show();
	}
}