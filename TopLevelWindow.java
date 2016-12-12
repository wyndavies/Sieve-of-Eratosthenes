package sieve;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.text.NumberFormat;
import javax.swing.*;
import javax.swing.text.NumberFormatter;

import java.util.*;

/*
 * A class to handle the frames, panels, etc and handle various methods to keep all the components
 * updated as needed
 * In hindsight I should have extended JFrame, rather than made this a wrapper to a JFrame, but this code
 * was created in a rather chaotic manner and was originally intended to be part of a completely different
 * program
*/
public class TopLevelWindow {
	
	protected JPanel mainPanel;
	protected ArrayList<OutputTextField> listOfFields;
	protected JFormattedTextField inputField;
	protected JFrame frame;
	protected Sieve sieve = null;
	
	public TopLevelWindow()
	{
		super();
		listOfFields = new ArrayList<OutputTextField>();
	}
	
	public void createWindow(int xpos, int ypos, int width, int height)
	{
		frame = new JFrame("Sieve of Erastophenes");
		// I often forget to handle window close operations and end up with active hidden windows
		// In this case I want the whole program to shut down if any window is closed. This does it.
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setPreferredSize(new Dimension(width, height));

		// Use the custom WrapLayout class as the layout manager
		WrapLayout wl = new WrapLayout();
		mainPanel = new JPanel(wl);

		// The main panel will have a vertical scroll bar
		JScrollPane sp = new JScrollPane();
		sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		sp.setViewportBorder(BorderFactory.createLineBorder(Color.black));
		sp.getViewport().add(mainPanel);
		
		frame.getContentPane().add(sp, BorderLayout.CENTER);

		// Test code
		/*for(int i = 0; i < 5; i++)
		{
			JLabel newLabel = new JLabel("Hello", SwingConstants.CENTER);
			newLabel.setPreferredSize(new Dimension(100,100));
			newLabel.setBorder(BorderFactory.createLineBorder(Color.black));
			mainPanel.add(newLabel);
		}*/
		
		// Display the window
		frame.setLocation(xpos, ypos);
		frame.pack();
		frame.setVisible(true);
	}
	
	public OutputTextField addTextField(String inStr, int width, int height)
	{
		// Add a text field with a starting string.
		// Call the method that adds an empty text field and then add the text
		OutputTextField newText = addTextField(width, height);
		newText.updateText(inStr);
		refresh();
		return newText;
	}
	
	public OutputTextField addTextField(int width, int height)
	{
		// Create an empty text field using our modified class and then add it to the main panel
		OutputTextField newText = new OutputTextField(this);
		newText.setBorder(BorderFactory.createLineBorder(Color.black));
		newText.setPreferredSize(new Dimension(width, height));
		mainPanel.add(newText.getPane());
		listOfFields.add(newText);
		refresh();
		return newText;
	}

	public JLabel addLabel(String inStr, int width, int height)
	{
		JLabel newLabel = new JLabel(inStr);
		newLabel.setBorder(BorderFactory.createLineBorder(Color.black));
		newLabel.setPreferredSize(new Dimension(width, height));
		mainPanel.add(newLabel);
		refresh();
		return newLabel;
	}
	
	public void addFormattedTextField(String name, int width, int height, int defaultValue)
	{
		// This is the input field, where you type in the number you want.
		// It only accepts numeric input
		NumberFormat format = NumberFormat.getInstance();
		NumberFormatter formatter = new NumberFormatter(format);
		formatter.setValueClass(Integer.class);
		formatter.setMinimum(3);
		formatter.setMaximum(Integer.MAX_VALUE);
		formatter.setAllowsInvalid(false);
		formatter.setCommitsOnValidEdit(true);
		
		inputField = new JFormattedTextField(formatter);
		inputField.setValue(defaultValue);
		inputField.setColumns(10);
		inputField.setBorder(BorderFactory.createLineBorder(Color.black));
		mainPanel.add(inputField);
		refresh();
	}
	
	public JButton addButton(String name, int width, int height)
	{
		JButton newButton = new JButton(name);
		newButton.setPreferredSize(new Dimension(width, height));
		mainPanel.add(newButton);
		refresh();
		return newButton;
	}
	
	public int getFormattedTextFieldValue()
	{
		// Get the number that was typed in
		Integer returnValue = (Integer)inputField.getValue();
		return returnValue.intValue();
	}
	
	public void refresh()
	{
		// If the refresh isn't called on the event dispatch thread then it won't update the GUI
		// Hence this horrible looking bit of code
		if (SwingUtilities.isEventDispatchThread())
		{
			callRefresh();
		} else {
			try {
				// This triggers a process to run on the EDT, which will update the screen
				// Might be better to call invokeLater(). I'll try it later.
				SwingUtilities.invokeAndWait(new Runnable() {public void run() {callRefresh();}});
			} catch (InvocationTargetException e) {
				// This exception can be triggered by entering a really large number and then
				// clicking inside the results window whilst it is still writing the first few numbers
				// Not sure why and it doesn't seem to cause any harm
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void callRefresh()
	{
		// After a lot of experimentation it turns out a single call does all the repainting needed
		mainPanel.revalidate();
	}
	
	public void clearLabels()
	{
		// Clear the content of all labels
		for(OutputTextField otf : listOfFields)
		{		
			otf.clearText();
		}
		refresh();
	}
	
	public void removeLabels()
	{
		mainPanel.removeAll();
		listOfFields.clear();
		refresh();
	}
	
	public ArrayList<OutputTextField> getFields()
	{
		return listOfFields;
	}
	
	public OutputTextField getFirstField()
	{
		if (listOfFields.size() > 0)
			return listOfFields.get(0);
		else
			return null;
	}
	
	public void setSieveRef(Sieve newSieve)
	{
		sieve = newSieve;
	}
	
	public void resetFlag()
	{
		if (sieve != null)
			sieve.resetFlag();
	}
}
