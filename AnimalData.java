package extra;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.render.PatternFactory;
import gov.nasa.worldwind.render.Polyline;
import gov.nasa.worldwind.render.SurfaceIcon;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import view.WidgetHouser;

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
 * Represents the attributes of each animal, including the trail and coordinates 
 * Makes the Polyline, and SurfaceIcon for the layers, also keeps track of the color change
 * Houses the IconLabel, a derivation of JLButton disguised as a label.
 * 
 **/
public class AnimalData {
	
	private ArrayList<Coordinate> coordList;	// list of the individual's movement
	private ArrayList<Position> trailList;		//  list of the coordinate, this grows as the animal moves
	
	private Color color;						// color of the animal
	private IconLabel label;					// label for the WidgetHouser
	private String stringID;					// ID number based on the excel table
		
	private int pos = 0;						// current position in the coordinate list 
	private int index;							// this unique value is assigned to each animal
												// used to access its respective SurfaceIcon or Polyline 
	
	/**
	 * Creates a new AnimalData object with a few attributes initialized
	 * @param i - unique index of this animal
	 */
	public AnimalData(int i){
		
		stringID = "";
		index = i;
		
		color = new Color(WidgetHouser.r.nextInt(256), WidgetHouser.r.nextInt(256), WidgetHouser.r.nextInt(256));
		
		coordList = new ArrayList<Coordinate>();
		trailList = new ArrayList<Position>();
	}
	
	/** adds coordinate positions (called from Controller.aquireData() **/
	public void addNextCoord(Position p1, GregorianCalendar tme){
		
		coordList.add(new Coordinate(p1, tme));
		
	}
	
	/**
	 * 
	 * @return a newly made Polyline that is stored in the layer 
	 */
	public Polyline makePolyline(){
		
		Polyline trial = new Polyline();
		trial.setFollowTerrain(true);
		trial.setColor(color);
		
		trailList.add(coordList.get(pos).getCoord());
		
		return trial;
		
	}
	
	/**
	 * 
	 * @return a newly made SurfaceIcon that is stored in the layer 
	 */
	public SurfaceIcon makeIcon(){
		
		SurfaceIcon icon = new SurfaceIcon(PatternFactory.createPattern(PatternFactory.PATTERN_CIRCLE, .5f),
				coordList.get(pos).getCoord());
		
		icon.setColor(color);
		icon.setMaintainSize(true);
		icon.setVisible(false);
		
		return icon;
		
	}
	
	/**
	 *  Creates a IconLabel for this individual
	 *  animal, setting attributes
	 *  
	 * @return returns the newly created IconLabel to pass to the JButton list in AMV_Main
	 */
	public IconLabel createLabel(){
		
		label = new IconLabel(stringID,index);
		label.setColorofIcon(color);
		
		return label;
	}

	/**
	 * 
	 * @return the unique index for retreival of layers (Polyline and SurfaceIcon)
	 */
	public int getAnimalIndex(){
		
		return index;
		
	}
	
	/**
	 * Sets the String id for this animal
	 * 
	 * @param id String to change to 
	 */
	public void setID(String id) {
		
		stringID = id;
		
	}
	
	/**
	 * 
	 * @return the String value (identified) of this animal
	 */
	public String getStringID(){
		
		return stringID;
		
	}
	
	/**
	 * 
	 * @return returns the Color of this animal
	 */
	public Color getColor() {
	
		return color;
		
	}
	
	/**
	 * Used to tell if at beginning of list movement or not
	 * 
	 * @return returns the current position this animal is at in his list
	 */
	public int getPosition(){
		
		return pos;
		
	}
	
	/**
	 * 
	 * Used to make changes to the trial internally, either by adding a new position, or
	 * truncating from the beginning (think of a queue, FIFO)
	 * 
	 * @param choice - if 'a' adds to trail, else truncates by the first value
	 * @return a new ArrayList<Position> with updated movement
	 */
	public ArrayList<Position> changeTrail(char choice){
		
		if(choice == 'a'){
			trailList.add(coordList.get(pos).getCoord());	
		} else {
			trailList.remove(0);
		}
		
		return trailList;
		
	}
	
	/**
	 * 
	 * This method is called each time the animal is able to move.
	 * Retrieves the Coordinate, time information of the current position.
	 * 
	 * @return the current time to compare the reference counter to
	 */
	public GregorianCalendar getTime(){
		
		return coordList.get(pos).getTimeStamp();

	}
	
	/**
	 * 
	 * @return returns the amount of coordinates this animal has
	 */
	public int getSize(){
		
		return coordList.size();
	
	}
		
	
	/**
	 * 
	 * @return returns a Position object, representing 
	 * the next position of the animals path
	 */
	public Position nextPos(){
	
		return coordList.get(pos).getCoord();
		
	}
	
		
	/**
	 * 
	 * @return can not continue if the position reference 
	 * is equal to the length of the coordinate list - true
	 * 
	 * otherwise - false
	 */
	public boolean canNotContinue() {
		
		return pos == coordList.size();
		
	}
	
	/**
	 * updates the position reference of this animal by 1
	 */
	public void increment(){
		
		pos++;
		
	}
		
	/**
	 * hides the IconLabel when this animal has halted animation
	 */
	public void removeLabel(){
		
		label.setVisible(false);
	}
	
	/**
	 * 
	 * @return true if this animal has been selected for first person perspective.
	 * 
	 */
	public boolean isLabelPOV(){
		
		return label.isOpaque();
	}

	/**
	 * sorts the coordinate list, after the list has been made. In the case of accidental tampering
	 * with data set, this takes care of this.
	 */
	@SuppressWarnings("unchecked")
	public void sortCoodinates(){
		
		Collections.sort(coordList);
		
	}
	
	/**
	 *  condenses the size of the ArrayList to match how many coordinates actually exist 
	 */
	public void resizeCoordinates() {
		
		coordList.trimToSize();
		
	}

	/**
	 * 
	 * @return returns the next Position of the track, without any changes. 
	 */
	public Position peekNewPos() {
		
		return coordList.get(pos-1).getCoord();
	}

}
