package ks.teaching.res;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Main menu class, takes user name and then offers a level choice.
 * @author kwss
 *
 */
public class MainMenu extends JPanel 
	implements ActionListener, MouseMotionListener, MouseListener {
	/**
	 * Generated ID for serialising this object
	 */
	private static final long serialVersionUID = 82097461286975542L;
	
	// Next page button
	private JButton next = new JButton("Next");
	
	// Name field
	private JTextField name = new JTextField("Enter your name");
	
	// Track if we're on the name page or level select page
	private int page = 0;
	
	// Level images
	private BufferedImage l1, l2, l3;
	
	// The frame that holds this menu
	private QuizFrame qf;
	
	// Initialise pointer location to origin
	private Point currentPos = new Point(0, 0);
	
	// Declare arraylist to hold click hitzones
	private ArrayList<Rectangle2D> hitZones;
	
	// Menu image
	private BufferedImage picture;
	
	// Constructor
	public MainMenu(QuizFrame qf) {
		// Call parent super constructor
		super();
		
		// Store reference to the parent frame
		this.qf = qf;
		// Set background color
		setBackground(Color.gray);
		
		// Set size
		this.setPreferredSize(new Dimension(800,670));
		
		// assign this as an action listener to the next button
		// (This classes actionPerformed method will be called for next button events)
		next.addActionListener(this);
		
		// Add the name field to this component
		this.add(name);
		
		// Add the next button to this component
		this.add(next);
		
		// Mouse events should be routed to this components mouse handling methods
		this.addMouseMotionListener(this);
		this.addMouseListener(this);
		
		// Clear the name field on click
		name.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                name.setText("");
            }
        });
		
		// Try to load in the images
		try {
			l1 = ImageIO.read(getClass().getResource("/traffic lights - 1.png"));
			l2 = ImageIO.read(getClass().getResource("/traffic lights - 2.png"));
			l3 = ImageIO.read(getClass().getResource("/traffic lights - 3.png"));
			this.picture = ImageIO.read(getClass().getResource("/picture.png"));
		} catch (IOException e) {
			// Print stack trace on exception
			e.printStackTrace();
		}
		
		// Generate hit zones for level icons
		hitZones = new ArrayList<Rectangle2D>();
		Double r1 = new Rectangle2D.Double(49, 99, 52, 125);
		hitZones.add(r1);
		Double r2 = new Rectangle2D.Double(174, 99, 52, 125);
		hitZones.add(r2);
		Double r3 = new Rectangle2D.Double(299, 99, 52, 125);
		hitZones.add(r3);
		
		// Set the size and font of the name and next objects
		name.setSize(new Dimension(150,50));
		next.setSize(new Dimension(150,50));
		name.setFont(new Font("Comic Sans MS", 1, 15));
		next.setFont(new Font("Comic Sans MS", 1, 15));

		// Add the next and name components to this content pane
		add(name);
		add(next);
		
	}
	
	
	// Instructions for drawing this component
	public void paintComponent(Graphics g) {
		// Call super.paintComponent to ensure the frame is cleared each repaint
		super.paintComponent(g);
		// Switch on the current page
		switch(page) {
			case 0:
				// Just draw the background
				g.drawImage(picture, 0, 0, null);
				break;
			case 1:
				// Remove all the components
				removeAll();
				
				// Draw the background and level icons
				g.drawImage(picture, 0, 0, null);
				Graphics2D g2 = (Graphics2D) g;
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					g2.scale(2.0, 2.0);
					g2.setFont(new Font("Comic Sans MS", 1, 14));
					g2.drawImage(l1, 50, 100, null);
					g2.drawImage(l2, 175, 100, null);
					g2.drawImage(l3, 300, 100, null);
					g2.setColor(Color.white);
					g2.drawString("Level 1", 50, 220);
					g2.drawString("Level 2", 175, 220);
					g2.drawString("Level 3", 300, 220);
					
					// If the hitzones are hovered over then highlight them
					for (Rectangle2D r : hitZones) {
						if (r.contains(currentPos)) {
							g2.draw(r);
						}
					}
				
			}
	}

	/**
	 * Handle action events
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// If the user clicks next then do some validation and set name and 
		// increment page if validation passes
		if(e.getSource() == next) {
			if(name.getText().equals("Enter your name") || name.getText().equals("")) {
				JOptionPane.showMessageDialog(null, "You must enter your name to proceed", "Warning!", JOptionPane.WARNING_MESSAGE);
				return;
			}
			page=1;
			GlobalData.name = name.getText();
			repaint();
		}
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// When the mouse moves, log the position so that hovering over icons works
		currentPos = new Point(arg0.getPoint().x/2, arg0.getPoint().y/2);
		repaint();
		
		
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// Get the location of the click
		if (page == 0) {
			return;
		}
		Point p = arg0.getPoint();
		Point scaledPoint = new Point(p.x/2, p.y/2);
		// Loop through level icons to see if one was clicked
		for (Rectangle2D r : hitZones) {
			if (r.contains(scaledPoint)) {
				GlobalData.currentLevel = hitZones.indexOf(r)+1;
				GlobalData.currentQuestion = 1;
				qf.startQuiz();
			}
		}
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {}

	@Override
	public void mouseExited(MouseEvent arg0) {}

	@Override
	public void mousePressed(MouseEvent arg0) {}

	@Override
	public void mouseReleased(MouseEvent arg0) {}
}
