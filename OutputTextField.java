package sieve;

import java.awt.Adjustable;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.concurrent.TimeUnit;

import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;
/*
 * I wanted the functionality of JTextArea, but with scrollbars and thread-locking
 * I wasn't sure if I should extend JTextArea or JScrollPane, so I ended up creating a wrapper class
 * to hold both
*/
public class OutputTextField{

	private JTextArea jtArea;
	private JScrollPane jsPane;
	private boolean locked = false;
	private int prevMaxAdjustment = 0;
	
	public OutputTextField(TopLevelWindow inWindow)
	{
		jtArea = new JTextArea();
		jsPane = new JScrollPane(jtArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		// After many many attempts to get the text area to automatically scroll to the bottom (it was scrolling
		// to the 2nd last item or it was getting set and stuck on the last line) I found this code on stackexchange,
		// which does the job
		JScrollBar verticalBar = jsPane.getVerticalScrollBar();
		AdjustmentListener downScroller = new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				Adjustable adjustable = e.getAdjustable();
				// If the max size of the scrollbar has changed, move to the max (last) item
				if (adjustable.getMaximum() != prevMaxAdjustment)
				{
					prevMaxAdjustment = adjustable.getMaximum();
					adjustable.setValue(prevMaxAdjustment);
					
				}
			}
		};
		verticalBar.addAdjustmentListener(downScroller);
	}
	
	public JScrollPane getPane()
	{
		return jsPane;
	}

	public void updateText(String inText)
	{
	// This can be access from multiple threads
		while (locked == true)
		{
			try {
				TimeUnit.MILLISECONDS.sleep(10);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return;
			}
		}

		locked = true;

		// Add a new line if there is already text in the textarea
		if (jtArea.getText() != null && !(jtArea.getText().equals(""))) {
			jtArea.append(System.lineSeparator());
		}
		jtArea.append(inText);
		
		locked = false;
	}

	// These methods show why I should have extended JPane. But it is working now, so don't fix what
	// ain't broken.
	public void setBorder(Border inBorder)
	{
		jsPane.setBorder(inBorder);
	}
	
	public void setPreferredSize(Dimension inDimension)
	{
		jsPane.setPreferredSize(inDimension);
	}
	
	public void clearText()
	{
		jtArea.setText("");
	}
	
	public void refresh()
	{
		jsPane.repaint();
	}
}
