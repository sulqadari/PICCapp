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
	/*Непосредственно список доступных терминалов.*/
	private List<CardTerminal>	terminalsList;
	/*Экземпляр подключенного терминала. Через него обеспечивается связь с картами.*/
	private CardTerminal		terminal;
	/*Распознанная карта.*/
	private Card				card;
	/*Канал связи с картой.*/
	private CardChannel			channel;
	/*Получение SW карты.*/
	private ResponseAPDU		answer;
	
	/**
	*	Подключение к одному из доступных терминалов.
	*	@param	terminalNumber	- Порядковый номер терминала.
	*/
	public void ConnectTerminal(int terminalNumber) throws CardException
	{
		/*Получить экземпляр TerminalFactory.*/
		TerminalFactory terminalFactory	= TerminalFactory.getDefault();
		/*Получить список доступных терминалов.*/
		terminalsList					= terminalFactory.terminals().list();
		/*Подключение терминалу № terminalNumber.*/
		terminal						= terminalsList.get(terminalNumber);
	}
	
	/**
	*	Экземпляр выбранного ридера. На начальном этапе нужен для вывода имени подключенного терминала.
	*	@returns terminal	Экземпляр подключенного терминала.
	*/
	public CardTerminal GetTerminal()
	{
		return terminal;
	}
	
	/**
	*	Список доступных ридеров. Можно использовать для вывода на экран списка терминалов, а также выбрать один из них.
	*	@returns List<CardTerminal>	список доступных ридеров.
	*/
	public List<CardTerminal> GetAvailableTerminalsList()
	{
		return terminalsList;
	}
	
	/**Подключить карту.*/
	public void ConnectCard() throws CardException
	{	
		terminal.waitForCardPresent(0);
		card	= terminal.connect("*");
		channel	= card.getBasicChannel();
	}
	
	/*
	*	Необходим при передаче команды карте.
	*	@returns channel - экземпляр установленного канала связи.
	*/
	public CardChannel GetChannel()
	{
		return channel;
	}
	
	/**Разорвать соединение с картой.*/
	public void DisconnectCard() throws CardException
	{
		card.disconnect(false);
		terminal.waitForCardAbsent(0);
	}
	
	/**
	*	Загрузка ключа аутентификации.
	*	Заголовок C-APDU специфицен для терминала ACR1281U
	*	case №3:
	*	CLA=	0xFF;
	*	INS=	0x82; - кодировка команды.
	*	P1=		0x00;
	*	P2=		0x20; - ключ будет храниться во временной памяти ридера.
	*	P3=		0x06; - длина ключа аутентификации.
	
	*	@param	key:	шесть байт ключа доступа к сектору. Номер сектора определяется при аутентификации.
	*	@returns true	Если ключ успешно загружен.
	*/
	public boolean LoadKey(String key) throws CardException
	{
		byte[] loadKeyCommand = Utilities.ToByteArray("FF82002006" + key);

		/*Отправка команды, получение ответа.*/
		answer = channel.transmit(new CommandAPDU(loadKeyCommand));
		/*Если команда не выполнена.*/
		if (answer.getSW() != 0x9000)
		{
			throw new CardException("Не удалось загрузить ключ.");
		}
		return true;
	}
	
	/**
	*	Аутентификация загруженного ключа для конкретного сектора.
	*	C-DATA, case 3:
	*	CLA		0xFF;
	*	INS		0x86;		- кодировка команды
	*	P1		0x00;
	*	P2		0x00;
	*	P3		0x05;		- количество подаваемых байт
	*	CDATA:	n			- номер блока, по отношению к которому осуществляется аутентификация.
	*	CDATA:	0x60/0x61	- ключи А/В.
	*	CDATA:	0x20		- ключ из временной памяти.
	*
	*	@param	blockNumber	Блок данных, по отношению к которому надо аутентифицировать ключ.
	*	@param	keyType			Тип ключа (А/В)
	*/
	public void AuthenticateKey(int blockNumber, String keyType) throws CardException
	{
		byte[] authenticateAPDU = Utilities.ToByteArray("FF860000050100006020");
		/*Адрес сектора, по отношению к которому осуществляется аутентификация ключа.*/
		authenticateAPDU[7] =(byte)blockNumber;
		
		/*Определение типа ключа. Приведение к верхнему регистру.*/
		keyType = keyType.toUpperCase();
		switch(keyType)
		{
			case "A": authenticateAPDU[8] =(byte)0x60; break;
			case "B": authenticateAPDU[8] =(byte)0x61; break;
		}
		/*Отправка команды и получение ответа.*/
		answer = GetChannel().transmit(new CommandAPDU(authenticateAPDU));
		/*Если ответ отрицательный.*/
		if(answer.getSW() != 0x9000)
		{
			throw new CardException("Ключ не подошел.");
		}
	}
}