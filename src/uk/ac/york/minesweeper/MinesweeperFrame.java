package uk.ac.york.minesweeper;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

public class MinesweeperFrame extends JFrame implements ActionListener
{
	// Variables
	private static final String INCREMENT = "incr";
	private static final String RESET = "reset";
	private static final String START = "start";
	private static final String STOP = "stop";

	private JLabel topTimer;
	private int time = 0;

	// Timer
	private Timer scoreTimer = new Timer(1000, this);

	public MinesweeperFrame()
	{
		// Basic Interface Settings
		this.setLayout(new BorderLayout(0,0));
		this.getContentPane().setBackground(Color.white);
		this.setSize(new Dimension(350,450));
		this.setMinimumSize(new Dimension(350,450));
		this.setTitle("Minesweeper");

		// Interface Structure
		JPanel mainPanel =  new JPanel(new BorderLayout(10, 10));
		mainPanel.setBackground(Color.white);

		JPanel topPanel = new JPanel(new GridLayout(1, 3, 10, 10));
		topPanel.setBackground(Color.white);

		JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
		centerPanel.setBackground(Color.white);

		JPanel centerMidPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

		MinefieldPanel minePanel = new MinefieldPanel(new Minefield(10, 10, 10));
		centerMidPanel.add(minePanel);

		// Reset Button
		JButton topResetBtn = new JButton(":)");
		topResetBtn.setPreferredSize(new Dimension(50, 50));
		topResetBtn.setActionCommand(RESET);
		topResetBtn.addActionListener(this);
		centerPanel.add(topResetBtn);

		// Labels
		topTimer = new JLabel(String.valueOf(time) + " Seconds");

		// Adding Items to Grid
		topPanel.add(new JLabel());
		topPanel.add(centerPanel);
		topPanel.add(topTimer);
		mainPanel.add(topPanel, BorderLayout.NORTH);
		mainPanel.add(centerMidPanel, BorderLayout.CENTER);

		this.getContentPane().add(mainPanel, BorderLayout.NORTH);
	}

	@Override
    public void actionPerformed(ActionEvent event)
	{
		if(event.getActionCommand().equals(INCREMENT))
		{
			time = time + 1;
		}
		else if(event.getActionCommand().equals(RESET))
		{
			time = 0;
		}
		else if(event.getActionCommand().equals(START))
		{
			scoreTimer.start();
		}
		else if(event.getActionCommand().equals(STOP))
		{
			scoreTimer.stop();
		}

		topTimer.setText((time) + " Seconds   ");
	}

	public static void main(String[] args)
	{
		MinesweeperFrame mainWindow = new MinesweeperFrame();
		// To close the application when clicking the close button of a window.
		mainWindow.setDefaultCloseOperation(EXIT_ON_CLOSE);
		mainWindow.setVisible(true);
	}
}
