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
	*	��������� ����� ������ ����������, ����� ���������� �� ����������� ��������� � ���������� �����.
	*	@param	inputFile	���� � �������.
	*	@param	OutputFile	��� ������������������ �����.
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
	*	��������� ��������������� ������ � �������.
	*	@param keyType	����� ���� �����.
	*	TODO ������ �������: ��������� ������ �������� ���� ���� ����� ������ ����������.
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