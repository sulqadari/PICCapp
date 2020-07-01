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
		/*Экземпляр ридера.*/
		ProximityCoupligDevice reader	= new ProximityCoupligDevice();
		/*Экземпляр чтения карты.*/
		CardProcessing cardProcessing	= new CardProcessing();
		/*Инструмент ввода данных с клавиатуры.*/
		Console consoleInput			= System.console();
		/*Получение SW карты.*/
		ResponseAPDU answer				= null;
		/*Сериализованные данные.*/
		SerializedData serializedData	= null;
		/*Дата/время создания файла как часть его имени.*/
		SimpleDateFormat formatDateTime = null;
		/*Выбор опций.*/
		String choice					= "";
		/*Строковое представление даты/времени создания файла.*/
		String dateString				= "";
		/*Полное имя файла: UIDformatDateTime.txt*/
		String fileName					= "";
		/*Список доступных ключей.*/
		String identifiedKeys			= "";
		/*Задать тип подбираемого ключа.*/
		String keyType					= "";
		/*Первичный вывод доступных опций.*/
		ShowMenu();
		while(!choice.equals("q"))
		{
			/*Выбор действия.*/
			choice = consoleInput.readLine("Выбрать опцию: ");
			switch(choice)
			{
				/*Показать меню.*/
				case "1":
				{
					ShowMenu();
				}break;
				
				/*Подключить терминал.*/
				case "2":
				{
					reader.ConnectTerminal(0);
					System.out.format("Подключен терминал %s%n", reader.GetTerminal().getName());
				}break;
				
				/*Подобрать ключи.*/
				case "3":
				{
					System.out.println("Подобрать ключи к карте:\n");
					/*Указать относительный путь									res/TroykaKeys.txt*/
					String keysList		= consoleInput.readLine("Файл со списком ключей: ");
					/*Указать относительный путь													res/Troyka_{$}_Keys.txt*/
					identifiedKeys		= consoleInput.readLine("Относительный путь файла с ключами: ");
					/*Тип распознаваемого ключа.*/
					keyType 			= consoleInput.readLine("Тип Ключа [А или В]: ");
					/*Начать.*/
					System.out.println("Приложите карту...");
					/*Установить канал связи с картой.*/
					reader.ConnectCard();
					System.out.println("Идет перебор...");
					/*Метод подбора.*/
					cardProcessing.DefineKeys(keysList, identifiedKeys, reader, keyType);
					/*Разъединить канал связи с картой.*/
					System.out.println("Извлеките карту...");
					reader.DisconnectCard();
				}break;
				
				/*Считать данные с карты.*/
				case "4":
				{
					System.out.println("Считать с карты.\nПриложите карту...");
					/*Установить канал связи с картой.*/
					reader.ConnectCard();
					/*Тип ключа.*/
					keyType		= consoleInput.readLine("Тип Ключа [А или В]: ");
					/*UID + время + дата как часть имени файла.*/
					fileName	= fileName.
									concat(cardProcessing.GetUid(reader)).
									concat(new SimpleDateFormat("_yyyy.MM.dd_HH-mm").format(new Date()).toString()).
									concat(".txt");
					System.out.println("Идет чтение...");
					/*Загрузить сериализованные ключи.*/
					serializedData					= new DataProcessing().LoadKeys(keyType);
					/*Массив временного хранения считанных данных.*/
					ArrayList<String> retrievedData	= new ArrayList<String>();
					/*16 байт очередного считанного блока.*/
					String data						= "";
					/*Номер очередного сектора.*/
					int sector						= 0;
					/*Динамическая инициализацпия во вложенном цикле (значение переменной зависит от сектора).*/
					int blockNumber;
					for(int nextKey = 0; nextKey < 16; nextKey++)
					{
						/*Загрузка ключа.*/
						reader.LoadKey(serializedData.Get(nextKey));
						/*Аутентификация ключа в очередном секторе.*/
						try
						{
							reader.AuthenticateKey(sector, keyType);
						}
						catch(CardException exc)
						{
							System.out.println(exc.getMessage() + " сектор: " + nextKey);
						}
						/*Поблочное чтение сектора.*/
						for(blockNumber = sector; blockNumber < (sector + 4); blockNumber++)
						{
							try
							{
								answer = cardProcessing.ReadData(blockNumber, reader);
								if(answer.getSW() != 0x9000)
								{
									throw new CardException("Не удалось прочитать блок.");
								}
								else
								{
									data = Utilities.Hexify(answer.getData());
								}
								/*Вывести на экран.*/
								System.out.format("%d	%S%n", blockNumber, data);
								/*Сохранить полученные данные во временный массив.*/
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
					Utilities.SaveToFile(fileName, retrievedData);
					/*Очистить поле "имя файла", иначе при следующей записи произойдет конкатенация новых данных имени к старым.*/
					fileName			= "";
					/*Разъединить канал связи с картой.*/
					System.out.println("Уберите карту.");
					reader.DisconnectCard();
				}break;
				
				/*Обновить данные на карте.*/
				case "5":
				{
					System.out.println("Обновить даныне карты.\nПриложите карту...");
					/*Установить канал связи с картой.*/
					reader.ConnectCard();
					/*Тип ключа.*/
					keyType					= consoleInput.readLine("Тип Ключа [А или В]: ");
					/*Имя файла-источника*/
					fileName				= consoleInput.readLine("Имя файла с данными: ");
					System.out.println("Идет Запись...\n");
					/*Загрузить сериализованные ключи.*/
					serializedData			= new DataProcessing().LoadKeys(keyType);
					/*Массив временного хранения данных для записи.*/
					ArrayList<String> data	= Utilities.LoadFile(fileName);
					
					int sector				= 0;
					/*Динамическая инициализацпия во вложенном цикле (значение переменной зависит от сектора).*/
					int blockNumber;
					for(int nextKey = 0; nextKey < 16; nextKey++)
					{
						/*Загрузка ключа.*/
						reader.LoadKey(serializedData.Get(nextKey));
						/*Аутентификация ключа в очередном секторе.*/
						reader.AuthenticateKey(sector, keyType);
						/*Поблочная запись в сектор. Условие завершения очередного цикла обусловлено тем, что 4й блок (трейлер) не обновляем.*/
						for(blockNumber = sector; blockNumber < (sector + 3); blockNumber++)
						{
							try
							{
								answer = cardProcessing.WriteData(blockNumber, data.get(blockNumber), reader);
								if(answer.getSW() != 0x9000)
								{
									throw new CardException("Не удалось обновить блок.");
								}
								
								/*Вывести на экран.*/
								System.out.format("%d	%S	Успешно обновлен%n", blockNumber, data.get(blockNumber));
							}
							catch(CardException e)
							{
								System.out.println("ОШИБКА: " +e.getMessage());
							}
						}
						/*0; 4; 8; 12; 16; 20; 24; 28; 32; 36; 40; 44; 48; 52; 56; 60 - номера первых блоков каждого сектора.*/
						sector += 4;
					}
					/*Очистить поле "имя файла", иначе при следующей записи произойдет конкатенация новых данных имени к старым.*/
					fileName			= "";
					/*Разъединить канал связи с картой.*/
					System.out.println("Уберите карту.");
					reader.DisconnectCard();
					
				}break;
				
				/*Сохранить ключи в программе.*/
				case "6":
				{
					/*Начать.*/
					System.out.println("Вшить ключи в приложение.\nПриложите карту...");
					/*Установить канал связи с картой.*/
					reader.ConnectCard();
					/*																				res/Troyka_{$}_Keys.txt*/
					identifiedKeys		= consoleInput.readLine("Относительный путь файла с ключами: ");
					/*																				res/Troyka_{$}_Keys.dat*/
					fileName			= consoleInput.readLine("Сохранить в файл: ");
					/*Метод сериализации ключей.*/
					new DataProcessing().SaveKeys(identifiedKeys, fileName);
					fileName			= "";
					/*Разъединить канал связи с картой.*/
					System.out.println("Извлеките карту...");
					reader.DisconnectCard();
				}break;
				
				/*Дата прохода.*/
				case "7":
				{
					System.out.println("Дата прохода:");
					long tameStamp = Long.valueOf(consoleInput.readLine("Значение типа long: "));
					Date theEnd = new Date(tameStamp);
					DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.LONG, SimpleDateFormat.LONG);
					dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
					String text = dateFormat.format(theEnd);
					System.out.println(text);
				}break;
				
				/*Завершить работу.*/
				case "q":
				{
					System.out.println("Завершение работы.\n");
				}break;
				
				default:
				{
					System.out.println("Пункт отсутствует.\n");
					ShowMenu();
				}
			}
		}
	}
	
	public static void ShowMenu()
	{
		System.out.format("%S%n%S%n%S%n%S%n%S%n%S%n%S%n%s%n",
		"[1] - Меню",						"[2] - Подключить терминал",
		"[3] - Подобрать ключи к карте.",	"[4] - Считать данные с карты.",
		"[5] - Обновить данные на карте.",	"[6] - Вшить ключи в приложение.",
		"[7] - Временной штамп.",			"[q] - Выход."
		);
	}
}