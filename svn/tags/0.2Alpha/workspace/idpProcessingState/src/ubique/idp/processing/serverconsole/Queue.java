package ubique.idp.processing.serverconsole;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Synchronized queue containing server console data.
 * Used by client to get data printed on server console window.
 * @author NatfullinA
 *
 */
public class Queue {
	
	private final LinkedList<String> queue;
	
	private final ArrayList<String> part;
	
	static Queue qconsole;
	
	public Queue()
	{
		queue = new LinkedList<String>();
		part = new ArrayList<String>();
	}
	
	public static Queue getQueue()
	{
		if (qconsole == null)
		{
			qconsole = new Queue(); 
		}
		return qconsole;
	}
	
	/**
	 * Put string to queue. 
	 * String is a line, printed on the server console window - it always ends by '\n' symbol.
	 * Synchronized: blocks queue during operation.
	 * @param s - string to put
	 */
	public void push(String s)
	{
		synchronized (queue) {
			queue.add(s);
			queue.notify();
		}
	}
	
	/**
	 * Removes and returns the first string from the queue.
	 * Synchronized: blocks queue during operation.
	 * @return
	 * 		string
	 */
	public String pop()
	{
		synchronized (queue) {
			return queue.removeFirst();
		}
	}

	/**
	 * Returns array of strings from queue.
	 * @param max - the maximum string amount to get.
	 * If amount is less than <b>max<b> then returns and removes existent strings.
	 * @return
	 * 		array of strings
	 */
	public ArrayList<String> poll(int max)
	{
		int i = 0;
		part.clear();
		while (!queue.isEmpty() && i < max)
		{
			part.add(queue.pop());
			i++;
		}
		return part;
	}
}
