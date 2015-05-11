package view;

import gov.nasa.worldwind.BasicModel;
import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.layers.CompassLayer;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.LayerList;
import gov.nasa.worldwind.layers.ViewControlsLayer;
import gov.nasa.worldwind.layers.ViewControlsSelectListener;
import gov.nasa.worldwind.render.Renderable;
import gov.nasa.worldwind.util.StatusBar;
import gov.nasa.worldwindx.examples.LayerPanel;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import model.AMV_Listeners;
/**
 * 
 * @author Devtulya Kavathekar
 * version: 1.3.2
 * 
 * Copyright 2012 Fagan Lab

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

/**
 * Represents the Graphical User Interface components of the Animal Movement Visualizer
 */
public class WidgetHouser {

	public static JMenuItem jmiOpen;					// "Open" widget
	public static JMenuItem jmiReset; 					// "Reset" widget
	public static JMenuItem jmiContact;					// "Contact" widget
	public static JMenuItem jmAbout;					// "About" widget
	public static JMenuBar jmb;							// Menu bar where above are stored
	public static JPanel labelPan; 						// stores Animal labels
	public static JSlider speedSlider; 					// slider for the speed
	public static JTextField currSpeed; 				// displays speed value
	public static JTextField removeValue; 				// segment value
	public static JButton animate; 						// Main widget user clicks
	public static JLabel showTime; 						// displays the changing time clock
	public static JComboBox linesBox;					// run selection (lines,nolines,full lines)				
	public static JLabel spdLab;						// displays timer
	public static JTabbedPane tabPane; 					// contains the WidgetHouser widgets
	
	public static boolean play = false; 				// true when Start Animation button is selected
	public static int noLines = 1; 						// true when the lines option is selected
	public static GregorianCalendar calCounter; 		// iterative counter used as reference
	public static int realSpeed;						// computes by value of 1000 milliseconds
	public static boolean nullFlag = false;				// internal mechanism (start animation, pause,play)
	public static File thefile = null; 					// opens a file
	
	public static AMV_Listeners runner_Listener;		// listener object 
	
	public static WorldWindowGLCanvas worldWindCanvas;  // main WWJ object
	public static LayerPanel layerPan; 					// other options layer check boxes
	public static LayerList llist; 						// list of layers where renderables are displayed
	public static boolean trackAnimal;					// used for POV
	
	public static StatusBar statusPan;					// status bar .SOUTH
	
	public static JFrame frame = new JFrame();			// main frame of this software
	
	public static ArrayList<Renderable> iconLayerList;	// iterable list of renderables (SurfaceIcon)
	public static ArrayList<Renderable> polyLayerList;	// iterable list of renderables (Polyline)
	
	public static Random r = new Random();				// to create colors

	public WidgetHouser() {


		/** ----World Wind object creation---- **/
		runner_Listener = new AMV_Listeners();
		
		worldWindCanvas = new WorldWindowGLCanvas();
		worldWindCanvas.setModel(new BasicModel());
		worldWindCanvas.addSelectListener(runner_Listener);
		
		//main rendering listener for animation movement
		worldWindCanvas.addRenderingListener(runner_Listener);
		llist = worldWindCanvas.getModel().getLayers();
		
		//status bar
		statusPan = new StatusBar();
		statusPan.setEventSource(worldWindCanvas);
		
		//on screen controls
		ViewControlsLayer viewControlsLayer = new ViewControlsLayer();
		insertBeforeCompass(worldWindCanvas, viewControlsLayer);
        worldWindCanvas.addSelectListener(new ViewControlsSelectListener(worldWindCanvas, viewControlsLayer));
			
        
		/** ----GUI Widget creation---- **/
		tabPane = new JTabbedPane();
		
		JPanel pan = new JPanel(new FlowLayout(FlowLayout.CENTER));

		animate = new JButton("Start Animation");
		animate.setEnabled(false);

		showTime = new JLabel("Time Not Set");

		animate.addActionListener(runner_Listener);

		pan.add(animate);
		pan.add(showTime);

		pan.setPreferredSize(new Dimension(201, 200));
		pan.setBackground(AMV_Main.FRAMECOLOR);
		pan.setBorder(BorderFactory.createEtchedBorder());

		// --file open bar--
		jmb = new JMenuBar();
		JMenu jmFile = new JMenu("File");
		JMenu jmHelp = new JMenu("Help!");

		jmAbout = new JMenuItem("About");
		jmAbout.addActionListener(runner_Listener);
		jmAbout.setEnabled(true);

		jmiOpen = new JMenuItem("Open");
		jmiOpen.addActionListener(runner_Listener);

		jmiReset = new JMenuItem("Reset");
		jmiReset.addActionListener(runner_Listener);
		jmiReset.setEnabled(true);

		jmiContact = new JMenuItem("Contact");
		jmiContact.addActionListener(runner_Listener);
		jmiContact.setEnabled(true);

		jmFile.add(jmiOpen);
		jmFile.add(jmiReset);

		jmHelp.add(jmiContact);
		jmHelp.add(jmAbout);

		jmb.add(Box.createHorizontalGlue());
		jmb.add(jmFile);
		jmb.add(jmHelp);

		speedSlider = new JSlider(0, 10000);

		speedSlider.setBackground(AMV_Main.FRAMECOLOR);
		speedSlider.addChangeListener(runner_Listener);

		currSpeed = new JTextField(Integer.toString(speedSlider.getValue()), 7);
		currSpeed.setBackground(AMV_Main.FRAMECOLOR);
		realSpeed = Integer.valueOf(currSpeed.getText());
		currSpeed.addKeyListener(runner_Listener);

		pan.add(speedSlider);
		spdLab = new JLabel("Speed: ");
		pan.add(spdLab);
		pan.add(currSpeed);
		pan.setBackground(AMV_Main.FRAMECOLOR);

		removeValue = new JTextField("5", 3);
		

		JPanel linesSelectionPan = new JPanel();
		String[] lineOpts = { "# trail", "No trail", "Full trail" };

		linesBox = new JComboBox(lineOpts);
		linesBox.addActionListener(runner_Listener);

		linesSelectionPan.add(linesBox);
		linesSelectionPan.add(removeValue);
		linesSelectionPan.setBackground(AMV_Main.FRAMECOLOR);

		pan.add(linesSelectionPan);

		pan.add(new JLabel());

		labelPan = new JPanel();
		labelPan.setLayout(new BoxLayout(labelPan, BoxLayout.Y_AXIS));

		labelPan.setBackground(AMV_Main.FRAMECOLOR);
		labelPan.setPreferredSize(new Dimension(200, 1000));

		JScrollPane scrollPane = new JScrollPane(labelPan);

		scrollPane.setVerticalScrollBar(new JScrollBar());
		scrollPane.setPreferredSize(new Dimension(200, 400));

		pan.add(new JLabel("           Index of Animals:           "));
		pan.add(scrollPane);

		tabPane = new JTabbedPane();
		tabPane.addTab("Tools", pan);
		
		layerPan = new LayerPanel(worldWindCanvas);
		tabPane.addTab("Layers", layerPan);
		
	}
	
	//algorithm copied from open source WW classes
	private static void insertBeforeCompass(WorldWindow wwd, Layer layer)
    {
        // Insert the layer into the layer list just before the compass.
        int compassPosition = 0;
        LayerList layers = wwd.getModel().getLayers();
        for (Layer l : layers)
        {
            if (l instanceof CompassLayer)
                compassPosition = layers.indexOf(l);
        }
        layers.add(compassPosition, layer);
    }

}
