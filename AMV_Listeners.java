package model;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import view.AMV_Main;
import view.WidgetHouser;

import gov.nasa.worldwind.event.RenderingEvent;
import gov.nasa.worldwind.event.RenderingListener;
import gov.nasa.worldwind.event.SelectEvent;
import gov.nasa.worldwind.event.SelectListener;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.AnnotationLayer;
import gov.nasa.worldwind.render.GlobeAnnotation;
import gov.nasa.worldwind.render.SurfaceIcon;

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
 * Swing Widget and World Wind rendering listeners, all stored here
 * 
 * 1. Rendering listener
 * 2. Balloon annotation listener
 * 3. Timer updater listener
 * 4. Speed option changing listener
 * 5. number of lines selection listener
 */
public class AMV_Listeners implements ChangeListener, ActionListener, 
									  SelectListener, RenderingListener, KeyListener{

	/** WidgetHouser changes listener implementation **/
	public void actionPerformed(ActionEvent ae){
			
		if(WidgetHouser.animate == ae.getSource()){
		
			String text = WidgetHouser.animate.getText();
			
			if(text.startsWith("S")){
				
				WidgetHouser.animate.setText("Pause");
				WidgetHouser.animate.requestFocus();
				
				//passes in value of max num of line trails
				Controller.setRemoveInterval(Integer.parseInt(WidgetHouser.removeValue.getText()));
				WidgetHouser.play = true;
				
				//enables/disables based upon operation of widget after start
				WidgetHouser.removeValue.setEnabled(false);
				
				WidgetHouser.jmiOpen.setEnabled(false);
				WidgetHouser.linesBox.setEnabled(false);
				
				WidgetHouser.worldWindCanvas.repaint();
				
				
			} else if(text.startsWith("P")){
				
				//freezes animation
				WidgetHouser.play = false;
				WidgetHouser.animate.setText("Resume");
				WidgetHouser.worldWindCanvas.repaint();
				WidgetHouser.jmiOpen.setEnabled(true);
				
			} else if(text.startsWith("Resu")){
				
				//continues animation
				WidgetHouser.play = true;
				
				//removes annotation bubbles if 
				//existing before resuming animation
				AnnotationLayer annLay = (AnnotationLayer) WidgetHouser.llist.get(WidgetHouser.llist.size()-1);
				
				if(!(annLay.getEntries().size() == 0)){	
					annLay.removeAllAnnotations();
				}
			
				WidgetHouser.animate.setText("Pause");
				WidgetHouser.worldWindCanvas.repaint();
			
				
			} else if(text.startsWith("Rese")){
				
				//reset code
				WidgetHouser.play = false;
				AMV_Main.reset();
				AMV_Main.aquireData();
				AMV_Main.load();
				AMV_Main.setView(Position.fromDegrees(Controller.getLatBound(), Controller.getLongBound()),10000000);
				WidgetHouser.jmiOpen.setEnabled(true);
			}
		
		}else if("Open".equals(ae.getActionCommand())){
			
			AMV_Main.reset();
			AMV_Main.openDialogBox();
			AMV_Main.aquireData();
			AMV_Main.load();
			AMV_Main.setView(Position.fromDegrees(Controller.getLatBound(), Controller.getLongBound()),10000000);
			
		}else if(WidgetHouser.jmiReset == ae.getSource()){
			
			//reset code
			WidgetHouser.play = false;
			AMV_Main.reset();
			AMV_Main.aquireData();
			AMV_Main.load();
			AMV_Main.setView(Position.fromDegrees(Controller.getLatBound(), Controller.getLongBound()),10000000);
			WidgetHouser.jmiOpen.setEnabled(true);
			
		}else if(WidgetHouser.linesBox == ae.getSource()){
			
			if(WidgetHouser.linesBox.getSelectedItem().equals("No trail")){
				
				WidgetHouser.removeValue.setEnabled(false);
				WidgetHouser.noLines = 0;
				WidgetHouser.polyLayerList.clear();
				
			} else if(WidgetHouser.linesBox.getSelectedItem().equals("# trail")){
				
				WidgetHouser.removeValue.setEnabled(true);
				WidgetHouser.noLines = 1;
				
			} else {
				
				WidgetHouser.removeValue.setEnabled(false);
				WidgetHouser.noLines = 2;
				
			}
	
		} else if("Contact".equals(ae.getActionCommand())){
			
			JOptionPane.showMessageDialog(WidgetHouser.frame, "Please send all inquiries" +
					" to devkavathekar23@gmail.com");
			
		} else if("About".equals(ae.getActionCommand())){
			
			String descp = "Fagan Lab, Department of Biology, University of Maryland.\n" +
					"Project conceptualized under the discretion of Dr. Thomas Mueller and " +
					"Dr. William Fagan. \nSoftware developed and coded by Devtulya Kavathekar, " +
					"undergraduate student, University of Maryland, College Park";
			JOptionPane.showMessageDialog(WidgetHouser.frame, descp);
	
		} 
		
	}
	
	public void keyReleased(KeyEvent arg0) {}
	
	/** Manual speed change **/
	public void keyPressed(KeyEvent arg0) {
		if(arg0.getKeyCode() == KeyEvent.VK_ENTER){
			
			WidgetHouser.speedSlider.setValue(Math.abs(Integer.parseInt(WidgetHouser.currSpeed.getText())));
			WidgetHouser.spdLab.setText("Speed");
			WidgetHouser.spdLab.setForeground(new Color(0,0,0));
			WidgetHouser.animate.setText("Resume");
			
		} else {
			
			WidgetHouser.spdLab.setText("Press Enter");
			WidgetHouser.spdLab.setForeground(new Color(255,0,100));
		}
		
	}

	/** Manual speed change **/
	public void keyTyped(KeyEvent arg0) {
		
		if(arg0.getKeyCode() != KeyEvent.VK_ENTER){
			WidgetHouser.play = false;
		}
	}
	
	/** speed bar widget **/
	public void stateChanged(ChangeEvent e){
	
		WidgetHouser.currSpeed.setText(String.valueOf(WidgetHouser.speedSlider.getValue()));
		WidgetHouser.animate.requestFocus();
	}
	
	
	
	//--------------------World Wind Listeners-----------------------------------
	
	
	
	/** World Wind pickable balloon **/
	public void selected(SelectEvent event) {
		
		//when user clicks on screen, annotation box appears above Icon
		//showing latitude and longitude position
		if(!WidgetHouser.play){
			
			//if it is a left click of the mouse
			if(event.getEventAction().equals(SelectEvent.LEFT_CLICK)){
		
				//look if the wwj has objects loaded
				if(event.hasObjects()){
					
					//if type SurfaceIcon
					if(event.getTopObject() instanceof SurfaceIcon){
					
						//type-casting..
						SurfaceIcon pickedIcon = (SurfaceIcon) event.getTopObject();
						AnnotationLayer annLay = (AnnotationLayer) WidgetHouser.llist.get(WidgetHouser.llist.size()-1);
						GlobeAnnotation curr = new GlobeAnnotation(pickedIcon.getLocation().toString(), (Position) pickedIcon.getLocation());
						
						//adds to layer
						annLay.addAnnotation(curr);
						
					}else if(event.getTopObject() instanceof GlobeAnnotation){
			
						//removes the annotation if the user selects the annotation after creation
						AnnotationLayer annLay = (AnnotationLayer) WidgetHouser.llist.get(WidgetHouser.llist.size()-1);								
						GlobeAnnotation an = (GlobeAnnotation) event.getTopObject();
						
						annLay.removeAnnotation(an);
					
					}//end else if
					
				}//end if
				
			}//end left click listener
			
		}//end of listener
		
	}

	
	/** Animal Movement rendering listener **/
	public void stageChanged(RenderingEvent event) {
		
		if(WidgetHouser.play){
			
			//the animation still has animals tracking
			if(Controller.getTotalAnimals() > 0){
				AMV_Main.run();
			}
			else{
				
				//end of animation, shows dialogue box
				WidgetHouser.play = false;
				
				Object[] options = {"Yes",
	                    "No"};
				int n = JOptionPane.showOptionDialog(WidgetHouser.frame,
				    "Restart?",
				    "Animal Movement Visualizer",
				    JOptionPane.YES_NO_CANCEL_OPTION,
				    JOptionPane.QUESTION_MESSAGE,
				    null,
				    options,
				    options[1]);
				
				if(n == JOptionPane.YES_OPTION){
					AMV_Main.reset();
					AMV_Main.aquireData();
					AMV_Main.load();
					AMV_Main.setView(Position.fromDegrees(Controller.getLatBound(), Controller.getLongBound()),10000000);
					WidgetHouser.jmiReset.setEnabled(true);
				} else if(n == JOptionPane.NO_OPTION){
					
					WidgetHouser.animate.setText("Reset");
				}
				
				
			}//end else
		
		}//end if
		
	}
	
	

}
