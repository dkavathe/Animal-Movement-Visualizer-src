package extra;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.render.PatternFactory;
import gov.nasa.worldwind.render.Polyline;
import gov.nasa.worldwind.render.SurfaceIcon;

import java.awt.Color;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import view.AMV_Main;
import view.WidgetHouser;
import model.Controller;

/**
 * 
 * @author Devtulya Kavathekar, University of Maryland, College Park
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
 * A JButton with certain properties set false 
 * to give the illusion of a JLabel for aesthetic purposes
 *
 */
public class IconLabel extends JButton implements MouseListener {

	private static final long serialVersionUID = 1L;
	public static boolean hit = false;				//prevents multiple POV's from triggering
	
	private int currIndex;							//reference to the associated animal unique index
	public String currStr;							//reference to the associated animal name
	
	public IconLabel(String str, int i) {

		super(str, new ImageIcon());
	
		currStr = str;
		currIndex = i;
		
		//changes properties for transparency in background of JMenu
		this.setFocusPainted(false);
        this.setMargin(new Insets(0, 0, 0, 0));
        this.setContentAreaFilled(false);
        this.setBorderPainted(false);
        this.setOpaque(false);
        this.addMouseListener(this);
        
	}
	
	/**
	 * Changes the color of this IconLabel
	 * @param newColor - color to change to
	 */
	public void setColorofIcon(Color newColor){
		
		((ImageIcon) this.getIcon()).setImage(PatternFactory.createPattern(PatternFactory.PATTERN_CIRCLE, .5f, newColor));
		
	}

	/**
	 * listens for a RIGHT click, shows JColorChooser
	 */
	public void mouseClicked(MouseEvent arg0) {
		
		// left click - open first person perspective
		if(arg0.getButton() == MouseEvent.BUTTON1){

			// been clicked
			if(this.isOpaque()){
			
				AMV_Main.setView(Position.fromDegrees(Controller.getLatBound(), Controller.getLongBound()),10000000);
				this.setOpaque(false);
				this.setBackground(AMV_Main.CLEAR);
				AMV_Main.toogleTrack(currIndex);
				hit = false;
				
			} else {
			
				// never been clicked
				if(hit == true){
					return;
				}
				
				this.setOpaque(true);
				this.setBackground(Color.yellow);
				AMV_Main.toogleTrack(currIndex);
				hit = true;
				
			}
			
			this.repaint();
		
		// right click - open color choosing
		}else if(arg0.getButton() == MouseEvent.BUTTON3){
		
			try{
			
				Color newColor = JColorChooser.showDialog(
	                    this,
	                    "Choose New Icon Color",
	                    Color.black);
				
				
				//updates WW objects
				SurfaceIcon currIcon = (SurfaceIcon) WidgetHouser.iconLayerList.get(currIndex);
				Polyline poly = (Polyline) WidgetHouser.polyLayerList.get(currIndex);
				currIcon.setColor(newColor);
				poly.setColor(newColor);

				this.setColorofIcon(newColor);
				
			}catch(IllegalArgumentException i){
				return;
			}
			
		}
		
		WidgetHouser.animate.requestFocus();
		
	}

	/**
	 * Upon mouse entering change the text of the icon
	 */
	@Override
	public void mouseEntered(MouseEvent arg0) {
	
		this.setForeground(AMV_Main.REDISH);		
		this.setText("R-Click = color ; L-Click = POV");
		this.repaint();
		
	}

	/**
	 * Upon exiting the icon, reset the JButton to be, clear color
	 */
	@Override
	public void mouseExited(MouseEvent arg0) {
		
		this.setText(currStr);
		this.setForeground(AMV_Main.CLEAR);
		this.repaint();
		
	}

	/**
	 * Method ignored.
	 */
	@Override
	public void mousePressed(MouseEvent arg0) {
		
	}

	/**
	 * Method ignored.
	 */
	@Override
	public void mouseReleased(MouseEvent arg0) {
		
	}
	
}
