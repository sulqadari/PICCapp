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
}