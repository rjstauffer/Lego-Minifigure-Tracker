package XMLreader;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.*;
import java.util.Map.Entry;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class practiceReader2 
{
	static Map<String, Double> map = new HashMap<>();
	static Map<String, Map<String, Double>> datedList = new LinkedHashMap<>();
	static String id = null;
	static String price = null;
	static Double priceNumber = 0.00;
	static final DecimalFormat df = new DecimalFormat("0.00");
	final static String OUTPUT_FILE = "output.csv";
	String fileName;
	
	public practiceReader2(String name)
	{
		fileName = name;
	}

	/**
	 * parses string of price into a double so it can be manipulated
	 * @param string of price
	 * @return double of price
	 */
	static double parseNum(String string) 
	{
		try 
		{
			return Double.parseDouble(string);
		} 
		catch (NumberFormatException e) 
		{
			return 0;
		}
	}

	/**
	 * Calculates the sum of every value in the map
	 * @param instance of map
	 * @return sum of prices in from of double
	 */
	static double getSum(Map<String, Double> map) 
	{
		double sum = 0;
		for (double val : map.values())
			sum += val; // sum = sum + val

		return sum;
	}

	/**
	 * Converts to tree map, in order to sort keys in order
	 * @return tree map with same keys/values, just different order
	 */
	static TreeMap<String, Double> sortById()
	{
		TreeMap<String, Double> sorted = new TreeMap<>();
		sorted.putAll(map);
		
		return sorted;
	}
	
	/**
	 * identical method for use with a specific map
	 * @param map, specific
	 * @return tree map in order
	 */
	static TreeMap<String, Double> sortById(Map<String, Double> map)
	{
		TreeMap<String, Double> sorted = new TreeMap<>();
		sorted.putAll(map);
		
		return sorted;
	}

	/**
	 * calculates the percent gain/loss for each minifigure for the current and previous entry
	 * calculates and prints the current sum of entire collection
	 * creates output csv file containing entire collection
	 * @param datedList
	 */
	public static void analyse(Map<String, Map<String, Double>> datedList)
	{
		int length = datedList.size() - 1;
		Collection<Map<String, Double>> values = datedList.values();
		ArrayList<Map<String, Double>> prices = new ArrayList<>(values);
		Map<String, Double> first = prices.get(length);
		Map<String, Double> second = prices.get(length-1);
		System.out.println(first);
		System.out.println(second);
		
		Map<String, Double> gainers = new HashMap<>();
		
		for (String key : first.keySet())
		{
			if (second.containsKey(key))
			{
				double gain = (first.get(key) - second.get(key)) / second.get(key);
				gainers.put(key, gain*100);
			}
		}
		
		datedList.put("Gainers and Losers", gainers);
		System.out.println("Collection is worth $"+df.format(getSum(first)));
		writeFile(datedList, OUTPUT_FILE);
	}
	
	/**
	 * Implements apache commons api to write information to a new csv file
	 * @param datedList2, map with dates and prices of each minifigure
	 * @param filename
	 */
	static void writeFile(Map<String, Map<String, Double>> datedList2, String filename) 
	{
		try
		{
		   BufferedWriter writer = Files.newBufferedWriter(Paths.get(filename));//creates new writer object
		   CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT);
		   //uses imported methods to create a printer and also creates a header for the file
		   
		   for (Entry<String, Map<String, Double>> entry : datedList2.entrySet())//iterates over input hashmap
		   {
		    csvPrinter.printRecord(Arrays.asList(entry.getKey(), entry.getValue()));//adds each line of hashmap to file
		   }
		   csvPrinter.flush();
		   csvPrinter.close();
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	 }
	
	/**
	 * parses new input XML file from Bricklink.com wishlist
	 * splits minifigure ID and price and puts it into a hashmap
	 * @param filename, location of file (most likely downloads)
	 * @return tree map of IDs and prices, sorted
	 */
	public static TreeMap<String, Double> parseNewFile(String filename) 
	{
		map.clear();
		id = null; price = null;
		try 
		{
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			DefaultHandler handler = new DefaultHandler() 
			{
				boolean ID = false;
				boolean maxPrice = false;

				// parser starts parsing a specific element inside the document
				public void startElement(String uri, String localName, String qName, Attributes attributes)
						throws SAXException {

					if (qName.equalsIgnoreCase("ITEMID")) 
						ID = true;
					
					if (qName.equalsIgnoreCase("MAXPRiCE")) 
						maxPrice = true;
				}

				public void characters(char ch[], int start, int length) throws SAXException 
				{
					if (ID) 
					{
						id = new String(ch, start, length);
						ID = false;
					}
					
					if (maxPrice) 
					{
						price = new String(ch, start, length);
						priceNumber = parseNum(price);
						maxPrice = false;
					}
					
					map.put(id, priceNumber);
				}
			};
			
			saxParser.parse(filename, handler);
			map.remove(null);
			//sortById();
			//System.out.println(sortById());
			//return sortById();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return sortById();
	}

	/**
	 * Note that this program must be manually updated
	 * In order for an updated file to be added, a new instance of the reader is created referenceing the location of the new file
	 * Then, it is necessary to add the file to the list by using the put method and specifying the current date, 
	 * as well as call parseNewFile() 
	 */
	public static void main(String args[]) 
	{
		practiceReader2 second = new practiceReader2("C:\\Users\\stauf\\Downloads\\test.xml");
		practiceReader2 third = new practiceReader2("C:\\Users\\stauf\\Downloads\\test (1).xml");
		
		datedList.put("9/1/2022", parseNewFile(second.fileName));
		datedList.put("1/1/2023", parseNewFile(third.fileName));
		
		analyse(datedList);
		
	}
}