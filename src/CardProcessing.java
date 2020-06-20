import javax.smartcardio.CardException;
import javax.smartcardio.Card;

import java.io.IOException;
import java.io.FileNotFoundException;
import javax.smartcardio.ResponseAPDU;
import javax.smartcardio.CommandAPDU;
import java.util.ArrayList;



public class CardProcessing
{
	/*Получение SW карты.*/
	private ResponseAPDU answer;
	
	/**
	*	Прочесть данные заданного блока.
	*	C-DATA, case 2:
	*	CLA		0xFF;
	*	INS		0xB0;
	*	P1		0x00;
	*	P2		blockNumber		Номер считываемого блока.
	*	P3		0x10			ожидается 16 байт ответа.
	*	
	*	@param	blockNumber		Номер считываемого блока.
	*	@param	reader			Текущий подключенный ридер.
	*
	*	@throws CardException	Не удалось прочитать данные.
	*/
	public ResponseAPDU ReadData(int blockNumber, ProximityCoupligDevice reader) throws CardException
	{	
		/*C-DATA. Получить 16 байт.*/
		byte[] readDataAPDU		= Utilities.ToByteArray("FFB0000010");
		/*Номер считываемого блока.*/
		readDataAPDU[3]			= (byte)blockNumber;
		/*Ответ карты. Содержит SW и (если ключ подошел) 16 байт данных блока.*/
		answer					= reader.GetChannel().transmit(new CommandAPDU(readDataAPDU));
		/*Вернуть полученные данные.*/
		return answer;
	}
	
	/**
	*	Обновить данные заданного блока.
	*	C-DATA, case 2:
	*	CLA		0xFF;
	*	INS		0xD6;
	*	P1		0x00;
	*	P2		blockNumber;	Номер обновляемого блока.
	*	P3		0x10			Количество байт к обновлению (16).
	*	data	10				Непосредственно байты.
	*	
	*	@param	blockNumber		Номер обновляемого блока.
	*	@param	data			Новые данные.
	*	@param	reader			Текущий подключенный ридер.
	*
	*	@throws CardException	Ошибка передачи данных/получения SW.
	*/
	public ResponseAPDU WriteData(int blockNumber, String data, ProximityCoupligDevice reader) throws CardException
	{	
		/*C-DATA. Передать 16 байт новых данных.*/
		byte[] updateDataAPDU	= Utilities.ToByteArray("FFD6000010" + data);
		/*Номер считываемого блока.*/
		updateDataAPDU[3]		= (byte)blockNumber;
		/*Ответ карты. Содержит SW.*/
		answer					= reader.GetChannel().transmit(new CommandAPDU(updateDataAPDU));
		/*Вернуть полученные данные.*/
		return answer;
	}
	
	/*Получить уникальный идентификационный номер карты.*/
	public String GetUid(ProximityCoupligDevice reader)  throws CardException
	{
		byte[] getUidCommand	= Utilities.ToByteArray("FFCA000000");
		String uid				= "";
		answer					= reader.GetChannel().transmit(new CommandAPDU(getUidCommand));
		
		if(answer.getSW() != 0x9000)
		{
			uid = "Не удалось получить UID.";
		}
		else
		{
			uid = Utilities.Hexify(answer.getData());
		}
		return uid;
	}

	/**
	*	Определение ключей доступа для каждого сектора. Ключи подаются в .txt-файле.<br>
	*	@param inputFile	
	*	@param identifiedKey
	*	@param reader
	*	@param keyType
	*
	*	@throws StringIndexOutOfBoundsException	Размер ключа не соответствует ожидаемому. Выбрасывается классом Utilities(???). Разобраться!
	*	@throws CardException	Не удалось прочитать данные.
	*/
	public void DefineKeys(String inputFile, String identifiedKey, ProximityCoupligDevice reader, String keyType) throws CardException, FileNotFoundException, IOException
	{
		/*Список доступных ключей.*/
		ArrayList<String> availableKeysArrayList	= Utilities.LoadFile(inputFile);
		/*Чтобы не дергать ArrayList.size() при очередной итерации.*/
		int length									= availableKeysArrayList.size();
		/*Номер сектора.*/
		int sector									= 0;
		/*i-ный ключ из списка.*/
		for(int i = 0; i < length; i++)
		{
			sector = 0;
			/*16 секторов, 64 блока.*/
			for(int block = 0; block < 64; block += 4)
			{
				try
				{
					/*Загрузка ключа. Генерация исключения начнет итерацию со следующего сектора.*/
					reader.LoadKey(availableKeysArrayList.get(i));
					/*Аутентификация. Генерация исключения начнет итерацию со следующего сектора.*/
					reader.AuthenticateKey(block, keyType);
					/*Если аутентификация прошла успешно, то записать номер сектора и подобранный ключ.*/
					Utilities.SaveToFile(identifiedKey, Integer.toString((block - sector)), availableKeysArrayList.get(i));
					
				}
				catch(CardException | StringIndexOutOfBoundsException | IllegalArgumentException e)
				{
					/*Игнорировать провал аутентификации, в том числе неверный формат ключа.*/
				}
				sector += 3;
			}
		}
	}
}