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
		/*Экземпляр ридера.*/
		ProximityCoupligDevice reader	= new ProximityCoupligDevice();
		/*Экземпляр чтения карты.*/
		CardProcessing cardProcessing	= new CardProcessing();
		/*Инструмент ввода данных с клавиатуры.*/
		Console consoleInput			= System.console();
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
					System.out.println("Считать с карты.\n");
										
					/*Установить канал связи с картой.*/
					System.out.println("Приложите карту...");
					reader.ConnectCard();
					/*Тип ключа.*/
					keyType				= consoleInput.readLine("Тип Ключа [А или В]: ");
					/*UID + время + дата как часть имени файла.*/
					fileName			= fileName.concat(cardProcessing.GetUid(reader)).
												   concat(new SimpleDateFormat("_HH-mm_dd.MM.yyyy").format(new Date()).toString()).
												   concat(".txt");
					System.out.println("Идет чтение...");
					/*Загрузить сериализованные ключи.*/
					serializedData		= new DataProcessing().LoadKeys(keyType);
					/*Вывести на экран и записать в txt-файл.*/
					cardProcessing.ReadData(serializedData, fileName, reader, keyType);
					fileName			= "";
					/*Разъединить канал связи с картой.*/
					System.out.println("Уберите карту.");
					reader.DisconnectCard();
				}break;
				/*Обновить данные на карте.*/
				case "5":
				{
					System.out.println("Сохраняю новые данные...\n");
				}break;
				/*Сохранить ключи в программе.*/
				case "6":
				{
					System.out.println("Вшить ключи в приложение.\n");
					/*Начать.*/
					System.out.println("Приложите карту...");
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
		System.out.format("%S%n%S%n%S%n%S%n%S%n%S%n%s%n",
		"[1] - Меню",						"[2] - Подключить терминал",
		"[3] - Подобрать ключи к карте.",	"[4] - Считать данные с карты.",
		"[5] - Обновить данные на карте.","[6] - Вшить ключи в приложение.",
		"[q] - Выход."
		);
	}
}