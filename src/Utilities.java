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
	*	�������������� ������ � �������� ������.
	*	@param	stringRepresentation	- ��������� ������������� ������ APDU
	*	@return 						- �������� ������.
	*/
	public static byte[] ToByteArray(String stringRepresentation)
	{
		stringRepresentation	= stringRepresentation.toUpperCase();
		/*����� ������� ������. ����� �������� ������ ���� ������.*/
		int		len				= stringRepresentation.length();
		/*�������� ������ � ��� ���� ������, �.�. ��� ��������� ������� (���������) ��������� � ����� ������ (8 ��� ������).*/
		byte[]	byteArray		= new byte[len/2];
		/*����� ��������� �������. ���������� ���������� � ����� Arrays.copyOfRange().*/
		int		byteArrayIndex	= 0;
		/*������� ���������� ������� ������� ������.*/
		int		i				= 0;
		/*������� �������� (Most Significant Nibble).*/
		char	msn				= '0';
		/*������� �������� (Less Significant Nibble).*/
		char	lsn				= '0';
		/*����, ��������� �� ������� ((msn << 4) || (lsn))*/
		byte	compiledByte	= (byte)0x00;
		
		try
		{
			while(i < len)
			{
				/*������ ��� �������� i ����� ������� ����������. (���� 7F, ����� msb=70)*/
				msn = stringRepresentation.charAt(i);
				i++;
				
				/*������ ��� �������� i+1 ����� ������� ����������. (���� 7F, ����� lsb=0F)*/
				lsn = stringRepresentation.charAt(i);
				i++;
				
				/*�������� ��������: ((7 = 00000111 << 4) = 01110000 | F = 00001111) = 01111111*/
				compiledByte				= (byte)((Character.digit(msn, 16) << 4) | (Character.digit(lsn, 16)));
				/*���������� ���� ��� ��������� ��������.*/
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
	*	��������� ���� ������ � ����. �������� � �������� ������: ����� 3 ��� ��������, �� ���������� ���� 16 ���.
	*	@param outputFile	��� �������� �����.
	*	@param sector		����� �������.
	*	@param data			������ � ������.
	
	*/
	public static void SaveToFile(String outputFile, String sector, String data) throws IOException
	{
		FileWriter fileWriter	= new FileWriter(outputFile, true);
		fileWriter.write(sector + "	" + data + "\n");
		fileWriter.close();
	}
	
	/**
	*	��������� ������ �����, ���������� � ArrayList. ��������, ��� 64 ���� ��������� � ��������� ����.
	*	����� ���� �����������, ������ ��������������� �������� � ����, ����� �� �����������.
	
	*	@param outputFile	��� �������� �����.
	*	@param data			������ � ������.
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
	*	��������� ���� � ArrayList.
	*	@param inputFile	����� � �������.
	*	@returns ArrayList	������ ��������� ������.
	*/
	public static ArrayList<String> LoadFile(String inputFile) throws FileNotFoundException
	{
		/*��������� ����� � ArrayList.*/
		ArrayList<String> data	= new ArrayList<String>();
		/*���������� ������������ ����� � �������.*/
		Scanner scanner			= new Scanner(new File(inputFile));
		while(scanner.hasNextLine())
		{
			data.add(scanner.nextLine());
		}
		scanner.close();
		return data;
	}
}