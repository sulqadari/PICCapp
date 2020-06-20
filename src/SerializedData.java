import java.io.Serializable;
import java.util.ArrayList;

public class SerializedData implements Serializable{
	
	private ArrayList<String> serializedData;
	private int nElems;
	
	public SerializedData()
	{	
		serializedData	= new ArrayList<String>();
		nElems			= 0;
	}
	
	/**
	*	�������� ��������� ���� � ������ ��������.
	*	@param inputLine	��������� ������������� ���������� �����.
	*/
	public void Insert(String inputLine)
	{	
		serializedData.add(inputLine);
	}
	
	/**
	*	����� � ������� ��������� ����.
	*	@param record ������ ����� � �������.
	*/
	public String Find(int record)
	{	
		return serializedData.get(record - 1);
	}
	
	/**
	*	����� �� ������.
	*	@param	searchLine	��������� ������������� �����.
	*/
	public String Find(String searchLine)
	{	
		int j;
		for(j = 0; j < nElems; j++)
		{
			if (serializedData.get(j).equals(searchLine))
			{
				break;
			}
		}
		
		if (j == nElems)
		{
			return "������ �� �������";
		}
		else
		{
			return serializedData.get(j);
		}
	}
	
	/**
	*	������� ����.
	*	@param index	������ �����.
	*/
	public String Get(int index)
	{	
		return serializedData.get(index);
	}
	
	/**����� ������� � �������.*/
	public int Size()
	{	
		return serializedData.size();
	}
}