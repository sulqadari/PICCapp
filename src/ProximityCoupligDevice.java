import javax.smartcardio.TerminalFactory;
import javax.smartcardio.CardException;
import javax.smartcardio.ResponseAPDU;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.Card;
import java.util.List;

public class ProximityCoupligDevice
{
	/*��������������� ������ ��������� ����������.*/
	private List<CardTerminal>	terminalsList;
	/*��������� ������������� ���������. ����� ���� �������������� ����� � �������.*/
	private CardTerminal		terminal;
	/*������������ �����.*/
	private Card				card;
	/*����� ����� � ������.*/
	private CardChannel			channel;
	/*��������� SW �����.*/
	private ResponseAPDU		answer;
	
	/**
	*	����������� � ������ �� ��������� ����������.
	*	@param	terminalNumber	- ���������� ����� ���������.
	*/
	public void ConnectTerminal(int terminalNumber) throws CardException
	{
		/*�������� ��������� TerminalFactory.*/
		TerminalFactory terminalFactory	= TerminalFactory.getDefault();
		/*�������� ������ ��������� ����������.*/
		terminalsList					= terminalFactory.terminals().list();
		/*����������� ��������� � terminalNumber.*/
		terminal						= terminalsList.get(terminalNumber);
	}
	
	/**
	*	��������� ���������� ������. �� ��������� ����� ����� ��� ������ ����� ������������� ���������.
	*	@returns terminal	��������� ������������� ���������.
	*/
	public CardTerminal GetTerminal()
	{
		return terminal;
	}
	
	/**
	*	������ ��������� �������. ����� ������������ ��� ������ �� ����� ������ ����������, � ����� ������� ���� �� ���.
	*	@returns List<CardTerminal>	������ ��������� �������.
	*/
	public List<CardTerminal> GetAvailableTerminalsList()
	{
		return terminalsList;
	}
	
	/**���������� �����.*/
	public void ConnectCard() throws CardException
	{	
		terminal.waitForCardPresent(0);
		card	= terminal.connect("*");
		channel	= card.getBasicChannel();
	}
	
	/*
	*	��������� ��� �������� ������� �����.
	*	@returns channel - ��������� �������������� ������ �����.
	*/
	public CardChannel GetChannel()
	{
		return channel;
	}
	
	/**��������� ���������� � ������.*/
	public void DisconnectCard() throws CardException
	{
		card.disconnect(false);
		terminal.waitForCardAbsent(0);
	}
	
	/**
	*	�������� ����� ��������������.
	*	��������� C-APDU ���������� ��� ��������� ACR1281U
	*	case �3:
	*	CLA=	0xFF;
	*	INS=	0x82; - ��������� �������.
	*	P1=		0x00;
	*	P2=		0x20; - ���� ����� ��������� �� ��������� ������ ������.
	*	P3=		0x06; - ����� ����� ��������������.
	
	*	@param	key:	����� ���� ����� ������� � �������. ����� ������� ������������ ��� ��������������.
	*	@returns true	���� ���� ������� ��������.
	*/
	public boolean LoadKey(String key) throws CardException
	{
		byte[] loadKeyCommand = Utilities.ToByteArray("FF82002006" + key);

		/*�������� �������, ��������� ������.*/
		answer = channel.transmit(new CommandAPDU(loadKeyCommand));
		/*���� ������� �� ���������.*/
		if (answer.getSW() != 0x9000)
		{
			throw new CardException("�� ������� ��������� ����.");
		}
		return true;
	}
	
	/**
	*	�������������� ������������ ����� ��� ����������� �������.
	*	C-DATA, case 3:
	*	CLA		0xFF;
	*	INS		0x86;		- ��������� �������
	*	P1		0x00;
	*	P2		0x00;
	*	P3		0x05;		- ���������� ���������� ����
	*	CDATA:	n			- ����� �����, �� ��������� � �������� �������������� ��������������.
	*	CDATA:	0x60/0x61	- ����� �/�.
	*	CDATA:	0x20		- ���� �� ��������� ������.
	*
	*	@param	blockNumber	���� ������, �� ��������� � �������� ���� ����������������� ����.
	*	@param	keyType			��� ����� (�/�)
	*/
	public void AuthenticateKey(int blockNumber, String keyType) throws CardException
	{
		byte[] authenticateAPDU = Utilities.ToByteArray("FF860000050100006020");
		/*����� �������, �� ��������� � �������� �������������� �������������� �����.*/
		authenticateAPDU[7] =(byte)blockNumber;
		
		/*����������� ���� �����. ���������� � �������� ��������.*/
		keyType = keyType.toUpperCase();
		switch(keyType)
		{
			case "A": authenticateAPDU[8] =(byte)0x60; break;
			case "B": authenticateAPDU[8] =(byte)0x61; break;
		}
		/*�������� ������� � ��������� ������.*/
		answer = GetChannel().transmit(new CommandAPDU(authenticateAPDU));
		/*���� ����� �������������.*/
		if(answer.getSW() != 0x9000)
		{
			throw new CardException("���� �� �������.");
		}
	}
}