import java.util.Scanner;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileNotFoundException;

public class DataProcessing
{	
	private SerializedData serializedData;
	
	DataProcessing()
	{
		serializedData = new SerializedData();
	}
	
	/**
	*	Сохранить ключи внутри приложения, чтобы избавиться от постоянного обращения к текстовому файлу.
	*	@param	inputFile	Файл с ключами.
	*	@param	OutputFile	Имя сериализизованного файла.
	*/
	public void SaveKeys(String inputFile, String outputFile) throws FileNotFoundException, IOException
	{
		Scanner scan =new Scanner(new File(inputFile));
		while(scan.hasNextLine()){
	
			serializedData.Insert(scan.nextLine());
		}
		scan.close();
		
		ObjectOutputStream objOutput = new ObjectOutputStream(new FileOutputStream(outputFile));
		objOutput.writeObject(serializedData);
		objOutput.close();
	}
	
	/**
	*	Загрузить сериализованный объект с ключами.
	*	@param keyType	Выбор типа ключа.
	*	TODO убрать хардкод: программа должна работать даже если имена файлов поменяются.
	*/
	public SerializedData LoadKeys(String keyType) throws FileNotFoundException, IOException, ClassNotFoundException
	{
		keyType = keyType.toUpperCase();
		ObjectInputStream objInput;
		if (keyType.equals("A"))
		{
			objInput =new ObjectInputStream(new FileInputStream("res/Troyka_A_Keys.dat"));
		}
		else
		{
			objInput =new ObjectInputStream(new FileInputStream("res/Troyka_B_Keys.dat"));
		}
		
		serializedData =(SerializedData) objInput.readObject();
		objInput.close();
		return serializedData;
	}
}