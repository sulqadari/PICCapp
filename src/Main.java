import java.io.Console;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.smartcardio.CardException;
import javax.smartcardio.ResponseAPDU;

import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import java.text.DateFormat;

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
		/*��������� SW �����.*/
		ResponseAPDU answer				= null;
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
					System.out.println("������� � �����.\n��������� �����...");
					/*���������� ����� ����� � ������.*/
					reader.ConnectCard();
					/*��� �����.*/
					keyType		= consoleInput.readLine("��� ����� [� ��� �]: ");
					/*UID + ����� + ���� ��� ����� ����� �����.*/
					fileName	= fileName.
									concat(cardProcessing.GetUid(reader)).
									concat(new SimpleDateFormat("_yyyy.MM.dd_HH-mm").format(new Date()).toString()).
									concat(".txt");
					System.out.println("���� ������...");
					/*��������� ��������������� �����.*/
					serializedData					= new DataProcessing().LoadKeys(keyType);
					/*������ ���������� �������� ��������� ������.*/
					ArrayList<String> retrievedData	= new ArrayList<String>();
					/*16 ���� ���������� ���������� �����.*/
					String data						= "";
					/*����� ���������� �������.*/
					int sector						= 0;
					/*������������ �������������� �� ��������� ����� (�������� ���������� ������� �� �������).*/
					int blockNumber;
					for(int nextKey = 0; nextKey < 16; nextKey++)
					{
						/*�������� �����.*/
						reader.LoadKey(serializedData.Get(nextKey));
						/*�������������� ����� � ��������� �������.*/
						try
						{
							reader.AuthenticateKey(sector, keyType);
						}
						catch(CardException exc)
						{
							System.out.println(exc.getMessage() + " ������: " + nextKey);
						}
						/*��������� ������ �������.*/
						for(blockNumber = sector; blockNumber < (sector + 4); blockNumber++)
						{
							try
							{
								answer = cardProcessing.ReadData(blockNumber, reader);
								if(answer.getSW() != 0x9000)
								{
									throw new CardException("�� ������� ��������� ����.");
								}
								else
								{
									data = Utilities.Hexify(answer.getData());
								}
								/*������� �� �����.*/
								System.out.format("%d	%S%n", blockNumber, data);
								/*��������� ���������� ������ �� ��������� ������.*/
								retrievedData.add(data);
							}
							catch(CardException e)
							{
								System.out.println("������: " +e.getMessage());
							}
						}
						/*0; 4; 8; 12; 16; 20; 24; 28; 32; 36; 40; 44; 48; 52; 56; 60 - ������ ������ ������ ������� �������.*/
						sector += 4;
					}
					/*�������� � ����.*/
					Utilities.SaveToFile(fileName, retrievedData);
					/*�������� ���� "��� �����", ����� ��� ��������� ������ ���������� ������������ ����� ������ ����� � ������.*/
					fileName			= "";
					/*����������� ����� ����� � ������.*/
					System.out.println("������� �����.");
					reader.DisconnectCard();
				}break;
				
				/*�������� ������ �� �����.*/
				case "5":
				{
					System.out.println("�������� ������ �����.\n��������� �����...");
					/*���������� ����� ����� � ������.*/
					reader.ConnectCard();
					/*��� �����.*/
					keyType					= consoleInput.readLine("��� ����� [� ��� �]: ");
					/*��� �����-���������*/
					fileName				= consoleInput.readLine("��� ����� � �������: ");
					System.out.println("���� ������...\n");
					/*��������� ��������������� �����.*/
					serializedData			= new DataProcessing().LoadKeys(keyType);
					/*������ ���������� �������� ������ ��� ������.*/
					ArrayList<String> data	= Utilities.LoadFile(fileName);
					
					int sector				= 0;
					/*������������ �������������� �� ��������� ����� (�������� ���������� ������� �� �������).*/
					int blockNumber;
					for(int nextKey = 0; nextKey < 16; nextKey++)
					{
						/*�������� �����.*/
						reader.LoadKey(serializedData.Get(nextKey));
						/*�������������� ����� � ��������� �������.*/
						reader.AuthenticateKey(sector, keyType);
						/*��������� ������ � ������. ������� ���������� ���������� ����� ����������� ���, ��� 4� ���� (�������) �� ���������.*/
						for(blockNumber = sector; blockNumber < (sector + 3); blockNumber++)
						{
							try
							{
								answer = cardProcessing.WriteData(blockNumber, data.get(blockNumber), reader);
								if(answer.getSW() != 0x9000)
								{
									throw new CardException("�� ������� �������� ����.");
								}
								
								/*������� �� �����.*/
								System.out.format("%d	%S	������� ��������%n", blockNumber, data.get(blockNumber));
							}
							catch(CardException e)
							{
								System.out.println("������: " +e.getMessage());
							}
						}
						/*0; 4; 8; 12; 16; 20; 24; 28; 32; 36; 40; 44; 48; 52; 56; 60 - ������ ������ ������ ������� �������.*/
						sector += 4;
					}
					/*�������� ���� "��� �����", ����� ��� ��������� ������ ���������� ������������ ����� ������ ����� � ������.*/
					fileName			= "";
					/*����������� ����� ����� � ������.*/
					System.out.println("������� �����.");
					reader.DisconnectCard();
					
				}break;
				
				/*��������� ����� � ���������.*/
				case "6":
				{
					/*������.*/
					System.out.println("����� ����� � ����������.\n��������� �����...");
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
				
				/*���� �������.*/
				case "7":
				{
					System.out.println("���� �������:");
					long tameStamp = Long.valueOf(consoleInput.readLine("�������� ���� long: "));
					Date theEnd = new Date(tameStamp);
					DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.LONG, SimpleDateFormat.LONG);
					dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
					String text = dateFormat.format(theEnd);
					System.out.println(text);
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
		System.out.format("%S%n%S%n%S%n%S%n%S%n%S%n%S%n%s%n",
		"[1] - ����",						"[2] - ���������� ��������",
		"[3] - ��������� ����� � �����.",	"[4] - ������� ������ � �����.",
		"[5] - �������� ������ �� �����.",	"[6] - ����� ����� � ����������.",
		"[7] - ��������� �����.",			"[q] - �����."
		);
	}
}