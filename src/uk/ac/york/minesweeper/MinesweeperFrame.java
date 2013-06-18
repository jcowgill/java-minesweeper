package uk.ac.york.minesweeper;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.Timer;

public class MinesweeperFrame extends JFrame implements ActionListener
{
	// Variables
	private static final String INCREMENT = "incr";
	private static final String RESET = "reset";
	private static final String START = "start";
	private static final String STOP = "stop";
	
	// Interface
	private JPanel mainPanel =  new JPanel(new BorderLayout(10, 10));
	private static final String[] difficulties = {"Easy", "Medium", "Hard"};
	private JComboBox difficultyBox = new JComboBox(difficulties);
	private MinefieldPanel minePanel;
	
	// Timer
	private Timer scoreTimer = new Timer(1000, this);
	private JLabel topTimer;
	private int time = 0;
	
	// Buton Images
	private JButton topResetBtn;
	private Image defaultFace = Toolkit.getDefaultToolkit().getImage("default.png");
	private Image winFace = Toolkit.getDefaultToolkit().getImage("won.png");
	private Image looseFace = Toolkit.getDefaultToolkit().getImage("lost.png");
	
	public MinesweeperFrame()
	{
		// Basic Interface Settings
		this.setLayout(new BorderLayout(0,0));
		this.getContentPane().setBackground(Color.white);
		this.setSize(new Dimension(400, 500));
		this.setMinimumSize(new Dimension(400, 500));
		this.setTitle("Minesweeper");
		
		// Interface Structure
		
		JPanel topPanel = new JPanel(new GridLayout(1, 3, 10, 10));
		topPanel.setBackground(Color.white);
		
		JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
		centerPanel.setBackground(Color.white);
		
		JPanel centerMidPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
		
		minePanel = new MinefieldPanel(new Minefield(16, 16, 40));
		minePanel.addStateChangeListener(new MinefieldStateChangeListener(){

			@Override
			public void stateChanged(MinefieldStateChangeEvent event) {
				// TODO Auto-generated method stub
				if(minePanel.getMinefield().getGameState() == GameState.RUNNING)
				{
					topResetBtn.setIcon(new ImageIcon(defaultFace));
					scoreTimer.start();
				}
				else 
				{
					if(minePanel.getMinefield().getGameState() == GameState.WON)
					{
						topResetBtn.setIcon(new ImageIcon(winFace));
					}
					else if (minePanel.getMinefield().getGameState() == GameState.LOST)
					{
						topResetBtn.setIcon(new ImageIcon(looseFace));
					}
					scoreTimer.stop();
				}
				topResetBtn.repaint();
			}
			
			
		});
		centerMidPanel.add(minePanel);
		
		// Difficulty Chooser
		difficultyBox.setSelectedIndex(1);
		
		// Reset Button
		topResetBtn = new JButton(":)");
		topResetBtn.setPreferredSize(new Dimension(50, 50));
		topResetBtn.setActionCommand(RESET);
		topResetBtn.addActionListener(this);
		centerPanel.add(topResetBtn);
		
		topResetBtn.setIcon(new ImageIcon(defaultFace));
		
		// Labels
		topTimer = new JLabel(String.valueOf(time) + " Seconds");
		scoreTimer.setActionCommand(INCREMENT);
		
		// Adding Items to Grid
		topPanel.add(difficultyBox);
		topPanel.add(centerPanel);
		topPanel.add(topTimer);
		mainPanel.add(topPanel, BorderLayout.NORTH);
		mainPanel.add(centerMidPanel, BorderLayout.CENTER);
		
		this.getContentPane().add(mainPanel, BorderLayout.NORTH);
		
		pack();
	}
	
	public void actionPerformed(ActionEvent event) 
	{
		if(event.getActionCommand().equals(INCREMENT))
		{
			time = time + 1;
		}
		else if(event.getActionCommand().equals(RESET))
		{
			time = 0;
			
			JPanel centerMidPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
			MinefieldPanel tempPanel;
			
			if (difficultyBox.getSelectedIndex() == 0)
			{
				minePanel.setMinefield((new Minefield(9, 9, 10)));
			}
			else if (difficultyBox.getSelectedIndex() == 2)
			{
				minePanel.setMinefield((new Minefield(30, 16, 99)));
			}
			else if (difficultyBox.getSelectedIndex() == 1)
			{
				minePanel.setMinefield((new Minefield(16, 16, 40)));
			}
			pack();
			
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
