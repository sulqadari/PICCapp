import javax.smartcardio.CardException;
import javax.smartcardio.Card;

import java.io.IOException;
import java.io.FileNotFoundException;
import javax.smartcardio.ResponseAPDU;
import javax.smartcardio.CommandAPDU;
import java.util.ArrayList;



public class CardProcessing
{
	/*��������� SW �����.*/
	private ResponseAPDU answer;
	
	/**
	*	�������� ������ ��������� �����.
	*	C-DATA, case 2:
	*	CLA		0xFF;
	*	INS		0xB0;
	*	P1		0x00;
	*	P2		blockNumber;
	*	P3		0x10		- ��������� 16 ���� ������.
	*	
	*	@param	key			��������� ������ SerializedData � �������.
	*	@param	outputFile	������� ���� ���������� ��������� ������.
	*	@param	reader		������� ������������ �����.
	*	@param	keyType		��� ����� ������ ������.
	*
	*	@throws CardException	�� ������� ��������� ������.
	*/
	public void ReadData(SerializedData keys, String outputFile, ProximityCoupligDevice reader, String keyType) throws CardException, IOException
	{	
		/*������ ���������� �������� ��������� ������.*/
		ArrayList<String> retrievedData	= new ArrayList<String>();
		/*16 ���� ���������� ���������� �����.*/
		String data						= "";
		/*C-DATA. �������� 16 ����.*/
		byte[] readDataAPDU				= Utilities.ToByteArray("FFB0000010");
		
		int sector = 0;
		for(int nextKey = 0; nextKey < 16; nextKey++)
		{
			/*�������� �����.*/
			reader.LoadKey(keys.Get(nextKey));
			/*�������������� ����� � ��������� �������.*/
			reader.AuthenticateKey(sector, keyType);
			/*��������� ������ �������.*/
			for(int j = sector; j < (sector + 4); j++)
			{
				readDataAPDU[3] = (byte)j;
				try
				{
					answer = reader.GetChannel().transmit(new CommandAPDU(readDataAPDU));
					if(answer.getSW() != 0x9000)
					{
						throw new CardException("�� ������� ��������� ����.");
					}
					else data = Utilities.Hexify(answer.getData());
					
					/*������� �� �����.*/
					System.out.format("%d	%S%n", j, data);
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
		Utilities.SaveToFile(outputFile, retrievedData);
	}
	
	/**
	*	����������� ������ ������� ��� ������� �������. ����� �������� � .txt-�����.<br>
	*	@param inputFile	
	*	@param identifiedKey
	*	@param reader
	*	@param keyType
	*
	*	@throws CardException	�� ������� ��������� ������.
	*	@throws StringIndexOutOfBoundsException	������ ����� �� ������������� ����������. ������������� ������� Utilities(???). �����������!
	*	@throws CardException	�� ������� ��������� ������.
	*/
	public void DefineKeys(String inputFile, String identifiedKey, ProximityCoupligDevice reader, String keyType) throws CardException, FileNotFoundException, IOException
	{
		/*������ ��������� ������.*/
		ArrayList<String> availableKeysArrayList	= Utilities.LoadFile(inputFile);
		/*����� �� ������� ArrayList.size() ��� ��������� ��������.*/
		int length									= availableKeysArrayList.size();
		/*����� �������.*/
		int sector									= 0;
		/*i-��� ���� �� ������.*/
		for(int i = 0; i < length; i++)
		{
			sector = 0;
			/*16 ��������, 64 �����.*/
			for(int block = 0; block < 64; block += 4)
			{
				try
				{
					/*�������� �����. ��������� ���������� ������ �������� �� ���������� �������.*/
					reader.LoadKey(availableKeysArrayList.get(i));
					/*��������������. ��������� ���������� ������ �������� �� ���������� �������.*/
					reader.AuthenticateKey(block, keyType);
					/*���� �������������� ������ �������, �� �������� ����� ������� � ����������� ����.*/
					Utilities.SaveToFile(identifiedKey, Integer.toString((block - sector)), availableKeysArrayList.get(i));
					
				}
				catch(CardException | StringIndexOutOfBoundsException | IllegalArgumentException e)
				{
					/*������������ ������ ��������������, � ��� ����� �������� ������ �����.*/
				}
				sector += 3;
			}
		}
	}
	
	/*�������� ���������� ����������������� ����� �����.*/
	public String GetUid(ProximityCoupligDevice reader)  throws CardException
	{
		byte[] getUidCommand = Utilities.ToByteArray("FFCA000000");
		String uid = "";
		
		answer = reader.GetChannel().transmit(new CommandAPDU(getUidCommand));
		if(answer.getSW() != 0x9000)
		{
			throw new CardException("�� ������� �������� UID.");
		}
		else
		{
			uid = Utilities.Hexify(answer.getData());
		}
		return uid;
	}
	
	
}