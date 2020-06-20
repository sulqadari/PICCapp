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
	*	Вставить очередной ключ в массив хранения.
	*	@param inputLine	строковое представление очередного ключа.
	*/
	public void Insert(String inputLine)
	{	
		serializedData.add(inputLine);
	}
	
	/**
	*	Найти и вернуть очередной ключ.
	*	@param record индекс ключа в массиве.
	*/
	public String Find(int record)
	{	
		return serializedData.get(record - 1);
	}
	
	/**
	*	Найти по строке.
	*	@param	searchLine	строковое представление ключа.
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
			return "Запись не найдена";
		}
		else
		{
			return serializedData.get(j);
		}
	}
	
	/**
	*	Вернуть ключ.
	*	@param index	индекс ключа.
	*/
	public String Get(int index)
	{	
		return serializedData.get(index);
	}
	
	/**Длина массива с ключами.*/
	public int Size()
	{	
		return serializedData.size();
	}
}