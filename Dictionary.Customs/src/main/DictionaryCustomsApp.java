package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListDataListener;

public class DictionaryCustomsApp extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final int WINWIDTH = 500;
	private static final int WINHEIGHT = 500;
	
	// establish conenction to mysql database
	Connection connection;
	Statement statement = null;
	PreparedStatement preparedStatement = null;
	ResultSet resultSet = null;
	
	// front page components
	JFrame frame;
	JPanel titlePanel;
	FlowLayout titleFlowLayout;
	JLabel titleLbl;
	JPanel coverPanelMain;
	JButton coverAddButton;
	JButton coverGoToButton;
	JPanel controlsPanel;
	JButton controlsFwdButton;
	JButton controlsBackButton;
	JPanel mainPanel;
	Border titleBorder;
	Border controlsBorder;
	Border coverDictListBorder1;
	JPanel coverPanelSub2;
	JPanel coverPanelSub1;
	JTextArea displayDefTextArea;
	boolean gotoPressed = false;
	static JList<String> coverDictList;
	
	// the words and definitions 
	private static String[] wordsList;
	private static String[] defsList;
	
	JLabel displayWordLbl;
    JLabel displayDefLbl;
	
	//use an array to hold the panels or panes for each page
	// when selecting a word from the right, a button pops up saying "Go to ..." which will take you to the panel/pane for the word and show the page number at the bottom (where the
	// controls panel is at "2/45")
	
	private void createAndShowGUI() {
	
		// Add the words and defs from the database into this class
		wordsList = DictionaryDatabase.getWords();
        defsList = DictionaryDatabase.getDefs();
		
        //Create and set up the window.
        frame = new JFrame("Wordsy");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(WINWIDTH, WINHEIGHT);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setBounds((int)(screenSize.getWidth()/1.5), (int)(screenSize.getHeight()/3), WINWIDTH, WINHEIGHT);
        frame.setResizable(false);
        
        // create a title panel
        titlePanel = new JPanel();
        titlePanel.setPreferredSize(new Dimension(WINWIDTH, 50));      
        titleFlowLayout = new FlowLayout();
        titleFlowLayout.setAlignment(FlowLayout.LEFT);
        titlePanel.setLayout(titleFlowLayout);
        titleBorder = BorderFactory.createMatteBorder(0,0,2,0,Color.GRAY);
        titlePanel.setBorder(titleBorder);
        
        // add the cover panel
        coverPanelMain = new JPanel();
        coverPanelMain.setPreferredSize(new Dimension(WINWIDTH, 400));
        coverPanelMain.setBorder(new EmptyBorder(10,10,10,10));
        coverPanelMain.setLayout(new BorderLayout());
        
        // add the controls panel
        controlsPanel = new JPanel();
        controlsPanel.setPreferredSize(new Dimension(WINWIDTH, 50));
        controlsPanel.setLayout(new BorderLayout());
        controlsBorder = BorderFactory.createMatteBorder(2,0,0,0,Color.GRAY);
        controlsPanel.setBorder(controlsBorder);
        
        // added components to the title panel
        titleLbl = new JLabel("Wordsy");
        titleLbl.setFont(new Font("Times New Roman", Font.BOLD, 15));
        titleLbl.setFocusable(true);
        titleLbl.setBorder(new EmptyBorder(10,224,0,0));
        titlePanel.add(titleLbl);
        
        // setting up layout for left side of the cover panel
        
        coverPanelSub1 = new JPanel();
        coverPanelSub1.setLayout(new FlowLayout());
        coverPanelSub1.setLayout(new BoxLayout(coverPanelSub1,BoxLayout.Y_AXIS));
        displayDefTextArea = new JTextArea();
        displayDefTextArea.setText(DictionaryDatabase.getDescription());
        
        displayDefTextArea.setPreferredSize(new Dimension(300,250));
        displayDefTextArea.setWrapStyleWord(true);
        displayDefTextArea.setLineWrap(true);
        displayDefTextArea.setOpaque(false);
        displayDefTextArea.setFocusable(false);
        displayDefTextArea.setBackground(UIManager.getColor("Label.background"));
        displayDefTextArea.setFont(UIManager.getFont("Label.font"));
        displayDefTextArea.setBorder(new CompoundBorder(new EmptyBorder(30,0,20,20), UIManager.getBorder("Label.border")));
        displayDefTextArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        displayDefTextArea.setFont(new Font("Times New Roman", Font.PLAIN, 14));
        
        displayWordLbl = new JLabel();
        displayWordLbl.setText(DictionaryDatabase.getTitle());
        displayWordLbl.setBorder(new EmptyBorder(0,0,20,0));
        displayWordLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        displayWordLbl.setFont(new Font("Times New Roman", Font.BOLD, 42));
        
        
        coverPanelSub1.setBorder(new EmptyBorder(55,20,20,20));
        coverPanelSub1.add(displayWordLbl);
        coverPanelSub1.add(displayDefTextArea);
        
        // setting up layout for right side of the cover panel
        GridBagConstraints gbc = new GridBagConstraints();
        coverPanelSub2 = new JPanel(new GridBagLayout());
        
        coverAddButton = new JButton();
        coverAddButton.setText("Add to dictionary...");
        coverAddButton.addActionListener(addDictOnPress());
        
    
        
        coverDictList = new JList<String>();
        coverDictList.setModel(new AbstractListModel<String>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public String getElementAt(int i) {
				// TODO Auto-generated method stub
				return wordsList[i];
			}

			@Override
			public int getSize() {
				// TODO Auto-generated method stub
				return wordsList.length;
			}
        	
        });
        coverDictList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        coverDictList.setLayoutOrientation(JList.VERTICAL);
        // the scroll pane below holds a max of 14 elements
        // to adjust how many elements are displayed, every 2 elements, we should add 44 to the height
        coverDictList.setPreferredSize(new Dimension(120,250));
        coverDictList.setVisibleRowCount(-1);
        coverDictListBorder1 = BorderFactory.createMatteBorder(1,1,1,1,Color.GRAY);
        coverDictList.setFixedCellWidth(150);
        coverDictList.setBorder(coverDictListBorder1);
        
        coverGoToButton = new JButton();
        coverGoToButton.setText("Go to ...");
        coverGoToButton.addActionListener(gotoOnPress());
        
        JScrollPane scrollPaneList = new JScrollPane(coverDictList);
        scrollPaneList.setPreferredSize(new Dimension(138, 260));
        
        gbc.insets = new Insets(10,0,10,0);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        coverPanelSub2.add(coverAddButton, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        coverPanelSub2.add(scrollPaneList, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        coverPanelSub2.add(coverGoToButton, gbc);
        
        coverPanelMain.add(coverPanelSub1);
        coverPanelMain.add(coverPanelSub2, BorderLayout.EAST);
        
        
        // add the components to controls panel
        controlsBackButton = new JButton();
        controlsFwdButton = new JButton();
        controlsBackButton.setText("<<");
        controlsFwdButton.setText(">>");
        controlsFwdButton.addActionListener(fwdOnPress());
        controlsBackButton.addActionListener(backOnPress());
        controlsPanel.add(controlsBackButton, BorderLayout.WEST);
        controlsPanel.add(controlsFwdButton, BorderLayout.EAST);

        
        // all panels go into this main panel
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));
        mainPanel.setPreferredSize(new Dimension(WINWIDTH, WINHEIGHT));
        mainPanel.add(titlePanel);
        mainPanel.add(coverPanelMain);
        mainPanel.add(controlsPanel);
 
        //Add the titlePanel.
        frame.setContentPane(mainPanel);
 
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
	
	public static void updateList() {
		wordsList = DictionaryDatabase.getWords();
		defsList = DictionaryDatabase.getDefs();
		DefaultListModel<String> model = new DefaultListModel<>();
		int i=0;
		while (wordsList[i] != null) {
			model.addElement(wordsList[i]);
			i++;
		}
		coverDictList.setModel(model);
	}
	
	private ActionListener addDictOnPress() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				AddDefWindow.run(DictionaryCustomsApp.this);
			}
			
		};
	}
	
	private ActionListener gotoOnPress() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				// set action for the goto button
				int selectedIndex = coverDictList.getSelectedIndex();
				String wordlabel = wordsList[selectedIndex];
				String deftextarea = defsList[selectedIndex];
				displayWordLbl.setText(wordlabel);
				displayDefTextArea.setText(deftextarea);
				gotoPressed = true;
				
				// the following code sets the index of the selected list... (index starts at 0)
//				coverDictList.setSelectedIndex(3);
			}
			
		};
	}
	
	private ActionListener backOnPress() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				// the selected index is the current index before the button is pressed
				int selectedIndex = coverDictList.getSelectedIndex();
				// none of the tings are selected
				if (selectedIndex == -1 && !gotoPressed) {
					return;
				}
				if (selectedIndex == 0) {
					return;
				}
				String wordlabel = wordsList[selectedIndex-1];
				String deftextarea = defsList[selectedIndex-1];
				displayWordLbl.setText(wordlabel);
				displayDefTextArea.setText(deftextarea);
				coverDictList.setSelectedIndex(selectedIndex-1);
			}
			
		};
	}
	
	private ActionListener fwdOnPress() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				// the selected index is the current index before the button is pressed
				int selectedIndex = coverDictList.getSelectedIndex();
				// none of the tings are selected
				if (selectedIndex == -1 && !gotoPressed) {
					String wordlabel = wordsList[0];
					String deftextarea = defsList[0];
					displayWordLbl.setText(wordlabel);
					displayDefTextArea.setText(deftextarea);
					coverDictList.setSelectedIndex(0);
					return;
				}
				if (selectedIndex == wordsList.length-1) {
					return;
				}
				if (wordsList[selectedIndex + 1] == null && defsList[selectedIndex + 1] == null) {
					return;
				}
				String wordlabel = wordsList[selectedIndex + 1];
				String deftextarea = defsList[selectedIndex + 1];
				displayWordLbl.setText(wordlabel);
				displayDefTextArea.setText(deftextarea);
				coverDictList.setSelectedIndex(selectedIndex + 1);
			}
			
		};
	}
	
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new DictionaryCustomsApp().createAndShowGUI();
            }
        });
	}
}
