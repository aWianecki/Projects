package userInterface;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JFrame;

public class DisplayConnection 
{
	public final Color COLOR_GREY = new Color(8421504);
	public final Color COLOR_RED = new Color(16711680);
	public final Color COLOR_GREEN = new Color(65280);
	private JFrame frame = new JFrame("Connection Status");
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private void createAndShowGUI() 
    {
        //Create and set up the window.
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(500, 500));
        frame.getContentPane().setBackground(COLOR_RED);
        frame.getContentPane().setSize(500, 500);

        //Add the ubiquitous "Hello World" label.
        //JLabel label = new JLabel("Connected");
        //frame.getContentPane().add(label);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
    
    public void setColor(Color color)
    {
    	frame.getContentPane().setBackground(color);
    }

    public void drawUI() 
    {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}