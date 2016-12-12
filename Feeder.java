package sieve;

import java.lang.Runnable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
/*
 * This class just runs in the background and feeds the numbers into the queue
 */
public class Feeder implements Runnable {

	// The default behaviour of BlockingQueue is FILO. The subclass LinkedBlockingQueue changes this
	// to FIFO.
	// A blocking queue does what it sounds like - a queue of values and it will cause the reader to
	// block (wait) until a value arrives.
	protected BlockingQueue<Integer> outputQueue = new LinkedBlockingQueue<Integer>();
	protected int maxValue = 0;
	
	// I've added a non-default constructor, so I need to specify the default as well otherwise this
	// won't exist. Actually, I'm not sure I use the default constructor now I think of it. Oh well.
	public Feeder()
	{
	}
	
	public Feeder(int inValue)
	{
		maxValue = inValue;
	}
	
	public void setMaxValue(int inValue)
	{
		maxValue = inValue;
	}
	
	public BlockingQueue<Integer> getQueue()
	{
		return outputQueue;
	}
	
	// Eclipse auto-added this method and the @Override command. This overrides the base implementation
	// of the method so you can't access it even if you cast the object to its parent type.
	// As the parent type - Runnable - is abstract there is no base implementation.
	// Not really needed, but I guess it prevents any confusion if something tries to use this object
	// as Runnable instead of as Feeder.
	@Override
	public void run() {
		// This will feed Integers into the output queue up to the max specified
		// and then feed in -1 (the terminating code)
		if (maxValue < 3)
		{
			// If the max value hasn't been set then just return
			// Also just return if it is less than 3 as we have to start with a number greater than 2.
			return;
		} else {
			try {
				for(int i = 3; i <= maxValue; i++)
				{
					outputQueue.put(new Integer(i));
				}
				outputQueue.put(new Integer(-1));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
