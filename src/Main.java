import java.io.Console;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.smartcardio.CardException;

public class Main
{
	public static void main(String[] args) throws CardException, FileNotFoundException, IOException, ClassNotFoundException
	{
		/*��������� ������.*/
		ProximityCoupligDevice reader	= new ProximityCoupligDevice();
		/*��������� ������ �����.*/
		CardProcessing cardProcessing	= new CardProcessing();
		/*���������� ����� ������ � ����������.*/
		Console consoleInput			= System.console();
		/*��������������� ������.*/
		SerializedData serializedData	= null;
		/*����/����� �������� ����� ��� ����� ��� �����.*/
		SimpleDateFormat formatDateTime = null;
		/*����� �����.*/
		String choice					= "";
		/*��������� ������������� ����/������� �������� �����.*/
		String dateString				= "";
		/*������ ��� �����: UIDformatDateTime.txt*/
		String fileName					= "";
		/*������ ��������� ������.*/
		String identifiedKeys			= "";
		/*������ ��� ������������ �����.*/
		String keyType					= "";
		/*��������� ����� ��������� �����.*/
		ShowMenu();
		while(!choice.equals("q"))
		{
			/*����� ��������.*/
			choice = consoleInput.readLine("������� �����: ");
			switch(choice)
			{
				/*�������� ����.*/
				case "1":
				{
					ShowMenu();
				}break;
				/*���������� ��������.*/
				case "2":
				{
					reader.ConnectTerminal(0);
					System.out.format("��������� �������� %s%n", reader.GetTerminal().getName());
				}break;
				/*��������� �����.*/
				case "3":
				{
					System.out.println("��������� ����� � �����:\n");
					/*������� ������������� ����									res/TroykaKeys.txt*/
					String keysList		= consoleInput.readLine("���� �� ������� ������: ");
					/*������� ������������� ����													res/Troyka_{$}_Keys.txt*/
					identifiedKeys		= consoleInput.readLine("������������� ���� ����� � �������: ");
					/*��� ��������������� �����.*/
					keyType 			= consoleInput.readLine("��� ����� [� ��� �]: ");
					/*������.*/
					System.out.println("��������� �����...");
					/*���������� ����� ����� � ������.*/
					reader.ConnectCard();
					System.out.println("���� �������...");
					/*����� �������.*/
					cardProcessing.DefineKeys(keysList, identifiedKeys, reader, keyType);
					/*����������� ����� ����� � ������.*/
					System.out.println("��������� �����...");
					reader.DisconnectCard();
				}break;
				/*������� ������ � �����.*/
				case "4":
				{
					System.out.println("������� � �����.\n");
										
					/*���������� ����� ����� � ������.*/
					System.out.println("��������� �����...");
					reader.ConnectCard();
					/*��� �����.*/
					keyType				= consoleInput.readLine("��� ����� [� ��� �]: ");
					/*UID + ����� + ���� ��� ����� ����� �����.*/
					fileName			= fileName.concat(cardProcessing.GetUid(reader)).
												   concat(new SimpleDateFormat("_HH-mm_dd.MM.yyyy").format(new Date()).toString()).
												   concat(".txt");
					System.out.println("���� ������...");
					/*��������� ��������������� �����.*/
					serializedData		= new DataProcessing().LoadKeys(keyType);
					/*������� �� ����� � �������� � txt-����.*/
					cardProcessing.ReadData(serializedData, fileName, reader, keyType);
					fileName			= "";
					/*����������� ����� ����� � ������.*/
					System.out.println("������� �����.");
					reader.DisconnectCard();
				}break;
				/*�������� ������ �� �����.*/
				case "5":
				{
					System.out.println("�������� ����� ������...\n");
				}break;
				/*��������� ����� � ���������.*/
				case "6":
				{
					System.out.println("����� ����� � ����������.\n");
					/*������.*/
					System.out.println("��������� �����...");
					/*���������� ����� ����� � ������.*/
					reader.ConnectCard();
					/*																				res/Troyka_{$}_Keys.txt*/
					identifiedKeys		= consoleInput.readLine("������������� ���� ����� � �������: ");
					/*																				res/Troyka_{$}_Keys.dat*/
					fileName			= consoleInput.readLine("��������� � ����: ");
					/*����� ������������ ������.*/
					new DataProcessing().SaveKeys(identifiedKeys, fileName);
					fileName			= "";
					/*����������� ����� ����� � ������.*/
					System.out.println("��������� �����...");
					reader.DisconnectCard();
				}break;
				/*��������� ������.*/
				case "q":
				{
					System.out.println("���������� ������.\n");
				}break;
				
				default:
				{
					System.out.println("����� �����������.\n");
					ShowMenu();
				}
			}
		}
	}
	
	public static void ShowMenu()
	{
		System.out.format("%S%n%S%n%S%n%S%n%S%n%S%n%s%n",
		"[1] - ����",						"[2] - ���������� ��������",
		"[3] - ��������� ����� � �����.",	"[4] - ������� ������ � �����.",
		"[5] - �������� ������ �� �����.","[6] - ����� ����� � ����������.",
		"[q] - �����."
		);
	}
}