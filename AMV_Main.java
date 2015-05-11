package view;
import java.awt.BorderLayout;
import java.awt.Color;
import jxl.read.biff.BiffException;
import extra.IconLabel;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.AnnotationLayer;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.SurfaceIcon;
import gov.nasa.worldwind.view.orbit.OrbitView;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;
import model.Controller;
import java.io.IOException;
import java.util.GregorianCalendar;

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
 *  
 * 1. Handles all AMV's operations
 * 2. Deals with view changes
 * 3. main run method is housed here
 * 
 */
public class AMV_Main{
	
	// static color constants
	public final static Color FRAMECOLOR = new Color(135,213,100);
	public final static Color REDISH = new Color(255,0,100);
	public final static Color CLEAR = new Color(0,0,0);
	public final static Color YELLOWHIGHLIGHT = new Color(60,100,100); 
	
	public static SurfaceIcon pov_Animal;
	public static Position prePos;
	public static double preZoom;
	
	/** shows the default file dialog box taking in a .xls file.
	 *  Recursively prompts until valid input **/
	public static boolean openDialogBox(){
		
		JFileChooser jfc = new JFileChooser("./");
		
		jfc.setDialogTitle("Open an Excel file");
		jfc.addChoosableFileFilter(new FileNameExtensionFilter("Micosoft Excel 2007+", "xlsx"));
		jfc.addChoosableFileFilter(new FileNameExtensionFilter("Micosoft Excel 2003", "xls"));
		
		int result = jfc.showOpenDialog(null);
		
		if(result == JFileChooser.CANCEL_OPTION){
			return false;
		}
		
		if(result == JFileChooser.APPROVE_OPTION){
			
			//opens the file			
			WidgetHouser.thefile = jfc.getSelectedFile();
			
			if(!WidgetHouser.thefile.exists()){
				jfc = null;
				openDialogBox();

			}
			
			String filename = WidgetHouser.thefile.getName();
			
			//retrieves extension of the file inputed
			String extension;
			int dotPos = filename.lastIndexOf(".");
			extension = filename.substring(dotPos);
			
			//checks for malicious input
			//recursively re-asks for appropriate file format 
			if(!extension.equals(".xls") && !extension.equals(".xlsx")){
				
				System.out.println(extension);
				dotPos = -1;
				filename = "";
				jfc = null;
				openDialogBox();
				
			}
			
			//changes the name of the frame
			WidgetHouser.frame.setTitle("Animal Movement Visualizer - " + WidgetHouser.thefile.getName());
			
		
		}
		
		return true;
		
	}
		
	/** called upon termination of program and/or manual reset **/
	public static void reset(){
		
		WidgetHouser.animate.setText("Start Animation");
	
		WidgetHouser.showTime.setText("Time not set");
		WidgetHouser.animate.setEnabled(false);
		WidgetHouser.animate.requestFocus();
		WidgetHouser.removeValue.setEnabled(true);
		WidgetHouser.linesBox.setEnabled(true);
		
		// clears layers
		WidgetHouser.llist.remove(WidgetHouser.llist.size()-3);
		WidgetHouser.llist.remove(WidgetHouser.llist.size()-2);
		WidgetHouser.llist.remove(WidgetHouser.llist.size()-1);
			
		WidgetHouser.labelPan.removeAll();
		WidgetHouser.labelPan.repaint();
		
		WidgetHouser.trackAnimal = false;
		
		IconLabel.hit = false;
		
	}
	
	/** passes in file to be parsed and stored in data structures **/ 
	public static void aquireData(){
				
		WidgetHouser.calCounter = new GregorianCalendar();
		WidgetHouser.nullFlag = false;
		
		try{	
			
			WidgetHouser.calCounter = Controller.aquireInfo(WidgetHouser.thefile.getAbsolutePath());
			WidgetHouser.removeValue.setText(String.valueOf(Controller.getMaxLinesPossible()));
		} catch (BiffException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e){
			
			WidgetHouser.nullFlag = true;
			
		}
		
	}
	
	/** Creates the main frame of AMV **/
	private static void instantiate() {
		
		//create a WorldWind main object
	
		WidgetHouser.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		WidgetHouser.frame.setLayout(new BorderLayout());
		 
		WidgetHouser.frame.add(WidgetHouser.tabPane, BorderLayout.EAST);
		WidgetHouser.frame.add(WidgetHouser.statusPan, BorderLayout.SOUTH);
		WidgetHouser.frame.add(WidgetHouser.worldWindCanvas, BorderLayout.CENTER);
			
		WidgetHouser.frame.setJMenuBar(WidgetHouser.jmb);
		WidgetHouser.frame.setSize(1000,650);
	
		WidgetHouser.frame.setIconImage(new ImageIcon("umdimage.gif").getImage());
		WidgetHouser.frame.setVisible(true);
		
	
	}
	

	/** Resets the view upon unselection from POV **/
	public static void setView(Position pos, double zoom) {
		
		OrbitView view = (OrbitView) WidgetHouser.worldWindCanvas.getView();
		view.setCenterPosition(pos);
		view.setZoom(zoom);
	
	}
		
	
	/**loads layers to WWJ object **/
	public static void load() {
		
		WidgetHouser.showTime.setText(WidgetHouser.calCounter.getTime().toString());
		WidgetHouser.animate.setText("Start Animation");
 		
 		if(!WidgetHouser.nullFlag)
 			WidgetHouser.animate.setEnabled(true);
		
 		RenderableLayer surfLayer = new RenderableLayer();
 		surfLayer.setRenderables(WidgetHouser.iconLayerList);
 		
 		RenderableLayer polyLayer = new RenderableLayer();
 		polyLayer.setRenderables(WidgetHouser.polyLayerList);
 		
		// SurfaceIcon (size-3)
 		WidgetHouser.llist.add(surfLayer);
		
		// Polyline (size-2)
 		WidgetHouser.llist.add(polyLayer);
		
		// annotations (size-1)
 		WidgetHouser.llist.add(new AnnotationLayer());
			
	}
	
	/** work horse of the rendering using the Controller 
	 *  object's method which houses 2 algorithms **/ 
	public static void run(){
		
		try {
			
			switch(WidgetHouser.noLines){
			
				// just icons
				case 0:Controller.executeNoLines();break;
				
				// # lines
				case 1:Controller.executeLines(true);break;
				
				// full lines
				case 2:Controller.executeLines(false);
			}
			
		} catch (InterruptedException e) {	
			e.printStackTrace();
		}

		
		WidgetHouser.showTime.setText("    "+WidgetHouser.calCounter.getTime().toString()+"    ");
		WidgetHouser.worldWindCanvas.redraw();		

		// increments each second of the time counter each second at a time
		WidgetHouser.realSpeed = Integer.parseInt(WidgetHouser.currSpeed.getText())*1000;
		WidgetHouser.calCounter.add(GregorianCalendar.MILLISECOND, WidgetHouser.realSpeed);
		
		if(WidgetHouser.trackAnimal){
			
			setView(pov_Animal.getReferencePosition(), 10000);
		}
				
	}
	
	/** toggles which animal is POV **/
	public static void toogleTrack(int index) {
		
		if(WidgetHouser.trackAnimal == false){
			
			OrbitView view = (OrbitView) WidgetHouser.worldWindCanvas.getView();
			prePos = view.getCenterPosition();
			preZoom = view.getZoom();
			pov_Animal = (SurfaceIcon) WidgetHouser.iconLayerList.get(index);
			
			WidgetHouser.trackAnimal = true;
			
		} else{
			
			setView(prePos,preZoom);
			WidgetHouser.trackAnimal = false;
			
		}
		
	}

	/** loads the software using external methods **/
	public static void main(String[] args) {
	
		try {UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());}
		catch (ClassNotFoundException e) {}
		catch (InstantiationException e) {}
		catch (IllegalAccessException e) {}
		catch (UnsupportedLookAndFeelException e) {}
				
		java.awt.EventQueue.invokeLater(new Runnable() {
			
            public void run()
            {
            	//order is important otherwise will cause fatal errors
        		if(!AMV_Main.openDialogBox()){
        					
        			System.out.println("AMV Terminated");
        			return;
        			
        		}
        		
            	new WidgetHouser();
        		AMV_Main.aquireData();
        		AMV_Main.instantiate();
        		AMV_Main.setView(Position.fromDegrees(Controller.getLatBound(), Controller.getLongBound()),10000000);
        		AMV_Main.load();
        		
            }
            
        });
	 
	}
	
}
