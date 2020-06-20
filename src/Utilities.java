import java.util.ArrayList;
import java.lang.StringIndexOutOfBoundsException;

import java.util.Scanner;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileNotFoundException;

import static java.util.Arrays.copyOfRange;

public class Utilities
{
	public static String Hexify(byte[] bytes)
	{
		ArrayList<String> bytesToString = new ArrayList<String>(bytes.length);
		for(byte b: bytes)
		{
			bytesToString.add(String.format("%02X", b));
		}
		return String.join("", bytesToString);
	}
	
	/**
	*	Преобразование строки в байтовый массив.
	*	@param	stringRepresentation	- строковое представление байтов APDU
	*	@return 						- байтовый массив.
	*/
	public static byte[] ToByteArray(String stringRepresentation)
	{
		stringRepresentation	= stringRepresentation.toUpperCase();
		/*Длина входной строки. Число символов должно быть четным.*/
		int		len				= stringRepresentation.length();
		/*Выходной массив в два раза меньше, т.к. два строковых символа (полубайты) уместятся в одной ячейке (8 бит каждая).*/
		byte[]	byteArray		= new byte[len/2];
		/*Длина выходного массива. Передается аргументом в метод Arrays.copyOfRange().*/
		int		byteArrayIndex	= 0;
		/*Счетчик очередного символа входной строки.*/
		int		i				= 0;
		/*Старший полубайт (Most Significant Nibble).*/
		char	msn				= '0';
		/*Младший полубайт (Less Significant Nibble).*/
		char	lsn				= '0';
		/*Байт, собранный по формуле ((msn << 4) || (lsn))*/
		byte	compiledByte	= (byte)0x00;
		
		try
		{
			while(i < len)
			{
				/*Символ под индексом i будет старшим полубайтом. (Если 7F, тогда msb=70)*/
				msn = stringRepresentation.charAt(i);
				i++;
				
				/*Символ под индексом i+1 будет младшим полубайтом. (Если 7F, тогда lsb=0F)*/
				lsn = stringRepresentation.charAt(i);
				i++;
				
				/*Сложение полубайт: ((7 = 00000111 << 4) = 01110000 | F = 00001111) = 01111111*/
				compiledByte				= (byte)((Character.digit(msn, 16) << 4) | (Character.digit(lsn, 16)));
				/*Разместить байт под очередным индексом.*/
				byteArray[byteArrayIndex]	= compiledByte;
				byteArrayIndex++;
			}
		}
		catch(StringIndexOutOfBoundsException e)
		{
			System.out.println(e.getMessage());
		}
		//return copyOfRange(byteArray, 0, byteArrayIndex);
		return byteArray;
	}
	
	/**
	*	Сохранить одну строку в файл. Применим в переборе ключей: более 3 тыс итераций, но вызывается лишь 16 раз.
	*	@param outputFile	Имя целевого файла.
	*	@param sector		Номер сектора.
	*	@param data			Данные к записи.
	
	*/
	public static void SaveToFile(String outputFile, String sector, String data) throws IOException
	{
		FileWriter fileWriter	= new FileWriter(outputFile, true);
		fileWriter.write(sector + "	" + data + "\n");
		fileWriter.close();
	}
	
	/**
	*	Сохранить данные карты, хранящиеся в ArrayList. Надежней, чем 64 раза открывать и закрывать файл.
	*	Здесь файл открывается, данные последовательно вносятся в него, затем он закрывается.
	
	*	@param outputFile	Имя целевого файла.
	*	@param data			Данные к записи.
	*
	*/
	public static void SaveToFile(String outputFile, ArrayList<String> data) throws IOException
	{
		FileWriter fileWriter	= new FileWriter(outputFile, true);
		for (String a: data)
		{
			fileWriter.write(a + "\n");
		}
		fileWriter.close();
	}
	
	/**
	*	Загрузить файл в ArrayList.
	*	@param inputFile	файлы с ключами.
	*	@returns ArrayList	Список считанных ключей.
	*/
	public static ArrayList<String> LoadFile(String inputFile) throws FileNotFoundException
	{
		/*Сохранить ключи в ArrayList.*/
		ArrayList<String> data	= new ArrayList<String>();
		/*Построчное сканирование файла с ключами.*/
		Scanner scanner			= new Scanner(new File(inputFile));
		while(scanner.hasNextLine())
		{
			data.add(scanner.nextLine());
		}
		scanner.close();
		return data;
	}
}