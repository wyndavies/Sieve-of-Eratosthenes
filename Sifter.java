package sieve;

import java.lang.Runnable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/* This class sifts the numbers fed in.
 * It checks all numbers fed in to see if they can be divided evenly by itself. If they can, then
 * they are not prime numbers and dropped.
 * If they can't be evenly divided then they might be prime. So they are passed to the next sifter.
 * If there isn't a next sifter then they are prime. And then another choice is made.
 * If the number found is less than the squareroot of the maxvalue being checked create a new sifter
 * using this new prime number as its key, otherwise just record it as a prime and do nothing else.
 * 
 * The objects require that both siftValue and maxValue are set. siftValue is the prime that it is using
 * to sift the numbers fed in.
 * maxValue is used to determine if a new sifter should be created or not.
 * The objects also need references to 2 output fields - the output window specific to this sifter to
 * show its current processing tasks and to the final results window.
 * Additionally it needs the global class reference to the top level window to give feedback on its status.
 * 
 * If/when another sifter is created in the chain, the current sifter keeps a reference to the sendQueue
 * so it can send items to the new sifter.
 * nextSifter reference is only used during the creation phase of another sifter. This reference isn't used
 * again.
 */

public class Sifter implements Runnable {
	protected int siftValue;
	protected static int maxValue;
	protected Sifter nextSifter = null;
	protected OutputTextField siftLabel;
	protected OutputTextField resultsText;
	protected static TopLevelWindow workingWindow = null;
	
	protected BlockingQueue<Integer> receiveQueue = new LinkedBlockingQueue<Integer>();
	protected BlockingQueue<Integer> sendQueue = null;
	
	public Sifter()
	{
	}
	
	public Sifter(int inValue)
	{
		siftValue = inValue;
	}
	
	public void run()
	{
		// Poll the queue and process numbers that come through.
		// The signal to shutdown is when the number -1 is passed
		Integer sentValue;
		int sentInt;
		try {
			boolean loopFlag = true;
			while(loopFlag)
			{
				sentValue = receiveQueue.take();
				sentInt = sentValue.intValue();
				if (sentInt == -1)
				{
					loopFlag = false;
					if (sendQueue != null)
					{
						sendQueue.put(sentValue);
					}
				} else {
					processNumber(sentValue.intValue());
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		siftLabel.updateText("Finished");
		if (nextSifter == null)
		{
			// We're shutting down and we are the last sifter in the list
			// so reset the running flag to allow another job to run
			workingWindow.resetFlag();
		}
	}

	public BlockingQueue<Integer> getSendQueue()
	{
		return sendQueue;
	}
	
	public void setReceiveQueue(BlockingQueue<Integer> inQueue)
	{
		receiveQueue = inQueue;
	}
	
	public void setSiftValues(int inValue)
	{
		siftValue = inValue;
	}
	
	public void setMaxValue(int inValue)
	{
		double dMaxValue = Math.sqrt(inValue);
		maxValue = (int)dMaxValue;
	}
	
	public void setSiftLabel(OutputTextField inLabel)
	{
		siftLabel = inLabel;
	}
	
	public void setResultsText(OutputTextField inText)
	{
		resultsText = inText;
	}
	
	public void setWorkingWindow(TopLevelWindow inWorkingWindow)
	{
		workingWindow = inWorkingWindow;
	}

	public void processNumber(int inValue)
	{
		StringBuilder newText = new StringBuilder();
		newText.append(inValue);
		
		// Can we divide this number?
		if (inValue % siftValue == 0)
		{
			// Drop the number
			newText.append(" - Dropped");
		}
		else
		{
			if(nextSifter != null)
			{
				try {
					sendQueue.put(new Integer(inValue));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				newText.append(" - Passed");
			}
			else
			{
				resultsText.updateText(inValue + " found");
				if ( inValue <= maxValue) {
					nextSifter = new Sifter(inValue);
					nextSifter.setSiftValues(inValue);
					nextSifter.setSiftLabel(workingWindow.addTextField("Starting with filter " + inValue, 300, 100));
					nextSifter.setResultsText(resultsText);
					sendQueue = new LinkedBlockingQueue<Integer>();
					nextSifter.setReceiveQueue(sendQueue);
					new Thread(nextSifter).start();
					newText.append(" - New Sifter created");
				}
				else
				{
					newText.append(" - Prime");
				}
			}
		}
		
		siftLabel.updateText(newText.toString());
		workingWindow.refresh();
	}
}
