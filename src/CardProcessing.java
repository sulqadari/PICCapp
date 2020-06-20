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
	*	P2		blockNumber;
	*	P3		0x10		- ожидается 16 байт ответа.
	*	
	*	@param	key			Экземпляр класса SerializedData с ключами.
	*	@param	outputFile	Целевой файл сохранения считанных данных.
	*	@param	reader		Текущий подключенный ридер.
	*	@param	keyType		Тип ключа чтения данных.
	*
	*	@throws CardException	Не удалось прочитать данные.
	*/
	public void ReadData(SerializedData keys, String outputFile, ProximityCoupligDevice reader, String keyType) throws CardException, IOException
	{	
		/*Массив временного хранения считанных данных.*/
		ArrayList<String> retrievedData	= new ArrayList<String>();
		/*16 байт очередного считанного блока.*/
		String data						= "";
		/*C-DATA. Получить 16 байт.*/
		byte[] readDataAPDU				= Utilities.ToByteArray("FFB0000010");
		
		int sector = 0;
		for(int nextKey = 0; nextKey < 16; nextKey++)
		{
			/*Загрузка ключа.*/
			reader.LoadKey(keys.Get(nextKey));
			/*Аутентификация ключа в очередном секторе.*/
			reader.AuthenticateKey(sector, keyType);
			/*Поблочное чтение сектора.*/
			for(int j = sector; j < (sector + 4); j++)
			{
				readDataAPDU[3] = (byte)j;
				try
				{
					answer = reader.GetChannel().transmit(new CommandAPDU(readDataAPDU));
					if(answer.getSW() != 0x9000)
					{
						throw new CardException("Не удалось прочитать блок.");
					}
					else data = Utilities.Hexify(answer.getData());
					
					/*Вывести на экран.*/
					System.out.format("%d	%S%n", j, data);
					retrievedData.add(data);
				}
				catch(CardException e)
				{
					System.out.println("ОШИБКА: " +e.getMessage());
				}
			}
			/*0; 4; 8; 12; 16; 20; 24; 28; 32; 36; 40; 44; 48; 52; 56; 60 - номера первых блоков каждого сектора.*/
			sector += 4;
		}
		/*Записать в файл.*/
		Utilities.SaveToFile(outputFile, retrievedData);
	}
	
	/**
	*	Определение ключей доступа для каждого сектора. Ключи подаются в .txt-файле.<br>
	*	@param inputFile	
	*	@param identifiedKey
	*	@param reader
	*	@param keyType
	*
	*	@throws CardException	Не удалось прочитать данные.
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
	
	/*Получить уникальный идентификационный номер карты.*/
	public String GetUid(ProximityCoupligDevice reader)  throws CardException
	{
		byte[] getUidCommand = Utilities.ToByteArray("FFCA000000");
		String uid = "";
		
		answer = reader.GetChannel().transmit(new CommandAPDU(getUidCommand));
		if(answer.getSW() != 0x9000)
		{
			throw new CardException("Не удалось получить UID.");
		}
		else
		{
			uid = Utilities.Hexify(answer.getData());
		}
		return uid;
	}
	
	
}