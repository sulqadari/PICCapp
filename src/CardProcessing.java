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
	*	P2		blockNumber		����� ������������ �����.
	*	P3		0x10			��������� 16 ���� ������.
	*	
	*	@param	blockNumber		����� ������������ �����.
	*	@param	reader			������� ������������ �����.
	*
	*	@throws CardException	�� ������� ��������� ������.
	*/
	public ResponseAPDU ReadData(int blockNumber, ProximityCoupligDevice reader) throws CardException
	{	
		/*C-DATA. �������� 16 ����.*/
		byte[] readDataAPDU		= Utilities.ToByteArray("FFB0000010");
		/*����� ������������ �����.*/
		readDataAPDU[3]			= (byte)blockNumber;
		/*����� �����. �������� SW � (���� ���� �������) 16 ���� ������ �����.*/
		answer					= reader.GetChannel().transmit(new CommandAPDU(readDataAPDU));
		/*������� ���������� ������.*/
		return answer;
	}
	
	/**
	*	�������� ������ ��������� �����.
	*	C-DATA, case 2:
	*	CLA		0xFF;
	*	INS		0xD6;
	*	P1		0x00;
	*	P2		blockNumber;	����� ������������ �����.
	*	P3		0x10			���������� ���� � ���������� (16).
	*	data	10				��������������� �����.
	*	
	*	@param	blockNumber		����� ������������ �����.
	*	@param	data			����� ������.
	*	@param	reader			������� ������������ �����.
	*
	*	@throws CardException	������ �������� ������/��������� SW.
	*/
	public ResponseAPDU WriteData(int blockNumber, String data, ProximityCoupligDevice reader) throws CardException
	{	
		/*C-DATA. �������� 16 ���� ����� ������.*/
		byte[] updateDataAPDU	= Utilities.ToByteArray("FFD6000010" + data);
		/*����� ������������ �����.*/
		updateDataAPDU[3]		= (byte)blockNumber;
		/*����� �����. �������� SW.*/
		answer					= reader.GetChannel().transmit(new CommandAPDU(updateDataAPDU));
		/*������� ���������� ������.*/
		return answer;
	}
	
	/*�������� ���������� ����������������� ����� �����.*/
	public String GetUid(ProximityCoupligDevice reader)  throws CardException
	{
		byte[] getUidCommand	= Utilities.ToByteArray("FFCA000000");
		String uid				= "";
		answer					= reader.GetChannel().transmit(new CommandAPDU(getUidCommand));
		
		if(answer.getSW() != 0x9000)
		{
			uid = "�� ������� �������� UID.";
		}
		else
		{
			uid = Utilities.Hexify(answer.getData());
		}
		return uid;
	}

	/**
	*	����������� ������ ������� ��� ������� �������. ����� �������� � .txt-�����.<br>
	*	@param inputFile	
	*	@param identifiedKey
	*	@param reader
	*	@param keyType
	*
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
}