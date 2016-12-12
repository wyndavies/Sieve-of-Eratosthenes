package sieve;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.SwingWorker;

/*
 * The class that starts everything.
 * This is an implementation of the sieve of Eratosthenes. He worked out the first reliable way of finding
 * prime numbers. It also lends itself well to multi-threading.
 * We pick a maximum number and then use sifters to find all the prime numbers from 2 to max.
 * First we take the smallest number in the list - 2 - and use this as the first sifter.
 * We remove all numbers that can be evenly divided by 2 from the list. The smallest number left is a prime
 * In this case it is 3. We then use this as the next sifter and repeat. We can stop creating sifters once
 * we have reached the square root of max value. All numbers left and all sifters are prime numbers.
 * 
 * To replicate this as a multi-threaded program is pretty much the same logic. We create a sifter with a
 * key of 2 to filter out all multiples of itself. We also have a feeder, feeding in all the numbers from
 * 3 to max in order. It then sends a value of -1, which means 'finished'
 * If the sifter can't divide the number it has received evenly it tries to pass this number on to the next
 * sifter.
 * If there isn't a next sifter it has found a prime and it records this in the results window.
 * If the new value is less than the square root of max value it also creates another sifter using the
 * new prime as its key and will send all subsequent numbers to this sifter.
 * When the sifter receives a value of -1 it knows the list is finished and it shuts down.
 * 
 * This isn't a very efficient way of finding prime numbers, but it is a good way to show multi-threading
 * and inter-thread communications.
 */

public class Sieve {

	protected TopLevelWindow resultsWindow;
	protected TopLevelWindow inputWindow;
	protected TopLevelWindow workingWindow;
	protected Feeder feeder;
	protected boolean running = false;
	JButton startButton;
	
	public static void main(String[] args) {
		Sieve mainSieve = new Sieve();
		mainSieve.StartUp();
	}
	
	public void StartUp()
	{
		// Make some windows and add an input field for numbers only, plus a button
		int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
		int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
		
		
		resultsWindow = new TopLevelWindow();
		resultsWindow.createWindow(screenWidth - 350, 300, 350, 300);
		resultsWindow.addLabel("Results", 300, 100);

		
		inputWindow = new TopLevelWindow();
		inputWindow.createWindow(screenWidth - 300, 100, 200, 150);
		inputWindow.addLabel("Input a number", 150, 30);
		inputWindow.addFormattedTextField("Input", 300, 100, 100);
		startButton = inputWindow.addButton("Press me!", 150, 30);
		
		// The button needs to call the actionPerformed method of a class of type ActionListener
		// As all we want is to call a single method there is no point creating an entire new class
		// So we'll just make an anonymous class and bind it to the button's action listener
		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startButton.setText("Running...");
				StartBackgroundSieving();
			}
		});
		
		workingWindow = new TopLevelWindow();
		workingWindow.createWindow(200, 100, 1000, screenHeight - 200);	
		// Java doesn't support delegates or method references, so we'll have to pass in a reference
		// to the object. This gives too much interconnectivity, but can't currently think of a way
		// around it
		workingWindow.setSieveRef(this);
	}
	
	public void ClearOut()
	{
		// Clear out any existing outputs
		resultsWindow.clearLabels();
		workingWindow.removeLabels();
	}
	
	public void StartBackgroundSieving()
	{
		if (running) return;
		
		running = true;
		// We don't want this starting up whilst an existing job is running
		
		// We need to get this processing off the Event Dispatcher Thread.
		// Unfortunately starting a background thread normally doesn't do this,
		// (starting a thread from the EDT means the new thread is also counted as being the EDT)
		// so we shall make use of the SwingWorker class which will sort it out for us.
		// There are other ways of creating a background thread, but if there is a library
		// that does it for us it makes sense to use it.
		// No input and no return needed, so the SwingWorker types are Void and Void.
		SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
			public Void doInBackground() {
				ClearOut();
				StartSieving();
				return null;
			}
		};
		worker.execute();
	}
	
	public void StartSieving()
	{
		// Get the maximum value
		int value = inputWindow.getFormattedTextFieldValue();
		if (value < 3)
			return; // No point doing anything if the max value is less than 3.
		
		// Create the first sifter, pass it links to the results label and the working label
		// and the first number to sift and then start it running
		
		// Reuse an existing results field if it exists
		OutputTextField resultsText;
		resultsText = resultsWindow.getFirstField();
		if (resultsText == null)
			resultsText = resultsWindow.addTextField(300, 100);

		OutputTextField workingLabel = workingWindow.addTextField("Starting...", 300, 100);
		
		Sifter firstSifter = new Sifter();
		firstSifter.setSiftValues(2);
		firstSifter.setMaxValue(value);
		firstSifter.setSiftLabel(workingLabel);
		firstSifter.setResultsText(resultsText);
		firstSifter.setWorkingWindow(workingWindow);
		
		// Feed the numbers in via a feeder class running in a separate thread
		feeder = new Feeder(value);
		// The feeder creates a default output queue, so tell the sifter about it
		firstSifter.setReceiveQueue(feeder.getQueue());
		feeder.run();
		firstSifter.run();
	}
	
	public void resetFlag()
	{
		startButton.setText("Press me!");
		running = false;
	}
}
