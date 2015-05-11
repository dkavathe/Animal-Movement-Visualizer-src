package model;
import extra.AnimalData;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.render.Polyline;
import gov.nasa.worldwind.render.Renderable;
import gov.nasa.worldwind.render.SurfaceIcon;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import view.WidgetHouser;

import jxl.*;
import jxl.read.biff.BiffException;
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
 * Main work horse class, uses an internal ConcurrentHashMap to keep 
 * track of the coordinates, and time stamps of each individual animal 
 * Uses AnimalData objects, along with WidgetHouser variables for animation display.
 * 
 */
public class Controller {
	
	private static ConcurrentHashMap<String,AnimalData> info;   // --main data structure that 
																// contains AMV information--
	
	private static int valueToRemove; 							// number of line segments 
																// (if unselected, always highest amount) 
 
	private static int latBound; 								// variables used to find the 
	private static int longBound;								// center bound
	
	
	/**
	 * Reads from the specified excel document and stores the data into
	 * the main data structure, calculates lat and long center position for initial view
	 * 
	 * @param fname - .xls (.xlsx) file that has coordinate information
	 * @return - the starting Time of animal movements.
	 * @throws BiffException - JExcel requirement
	 * @throws IOException - incorrect excel file (this should never throw, checked prior)
	 */
	public static GregorianCalendar aquireInfo(String fname) throws BiffException, IOException {
		
		valueToRemove = 0; 
		
		// instantiates new SurfaceIcon, Polyline layers
		WidgetHouser.iconLayerList = new ArrayList<Renderable>();
	    WidgetHouser.polyLayerList = new ArrayList<Renderable>();
	    
	    
	    // JExcel utilities creation
		Workbook workbook = Workbook.getWorkbook(new File(fname));
		Sheet sheet = workbook.getSheet(0);
		String comparebefore = "";
		String currID = "";
		double latCoord = 0;
		double longCoord = 0;
		GregorianCalendar earliestTime = new GregorianCalendar();
		
		
		info = new ConcurrentHashMap<String, AnimalData>();
		
		
		// Upon new individual, instantiate AnimalData, add 'null' to both layers
		// and increase unique index count by 1.
		int count = 0;
		WidgetHouser.iconLayerList.add(null);
		WidgetHouser.polyLayerList.add(null);
		AnimalData animal = new AnimalData(count);
		count++;
		
		// length of data set 
		int iDlength = sheet.getColumn(0).length;
		
		comparebefore = sheet.getCell(0,1).getContents();
		
		// Algorithm which allocates/encapsulates information 
		// from Excel file to data structures, skips first row
		// assuming it has textual information.
		for(int i = 1; i < iDlength; i++) {
			
			currID = sheet.getCell(0, i).getContents();
			
			NumberCell la = (NumberCell) sheet.getCell(5,i);
			NumberCell lo = (NumberCell) sheet.getCell(6,i);
			
			latCoord = la.getValue();
			longCoord = lo.getValue();
			
			latBound += latCoord;
			longBound += longCoord;
			

			// if still in current Animal information, simply 'tacks' on to current AnimalData
			if(currID.equals(comparebefore)){
				
				GregorianCalendar timeStamp =  getTimeStamp(sheet.getCell(1,i),sheet.getCell(2,i),sheet.getCell(3,i),sheet.getCell(4,i));
				
				//calculates start time
				if(earliestTime.after(timeStamp)){
					earliestTime = timeStamp;
				}
				
				animal.addNextCoord(Position.fromDegrees(latCoord,longCoord),timeStamp);
				
				
			// now a new Animal has appeared (new id), repeats process.
			}else{
				
				info.put(comparebefore, animal);
				
				WidgetHouser.iconLayerList.add(null);
				WidgetHouser.polyLayerList.add(null);
				animal = new AnimalData(count);
				count++;
				
				GregorianCalendar timeStamp =  getTimeStamp(sheet.getCell(1,i),sheet.getCell(2,i),sheet.getCell(3,i),sheet.getCell(4,i));
				
				if(earliestTime.after(timeStamp)){
					
					earliestTime = timeStamp;
				}
				
				animal.addNextCoord(Position.fromDegrees(latCoord,longCoord), timeStamp);
				comparebefore = currID;
							
			}
			
		}
		
		info.put(comparebefore, animal);
	
		//stores ID in Animal object
		//resizes Arraylist encapsulation for efficiency
		for (String key: info.keySet()) {
			
			AnimalData curr = info.get(key);
			
			curr.setID(key);
			curr.resizeCoordinates();
			curr.sortCoodinates();
			
			WidgetHouser.iconLayerList.set(curr.getAnimalIndex(),curr.makeIcon());
			WidgetHouser.polyLayerList.set(curr.getAnimalIndex(),curr.makePolyline());
		}
	

		latBound = latBound / iDlength;
		longBound = longBound / iDlength;
		
		workbook.close();
		
		WidgetHouser.iconLayerList.trimToSize();
		WidgetHouser.polyLayerList.trimToSize();
		
		return earliestTime;
		
	}
	
	/** 
	 * 
	 * traverses through the map iterating, and updating 
	 * if any of the animals who's time reference is before the timer
	 * with polylines displayed
	 */
	public static void executeLines(boolean truncate) throws InterruptedException{
		
		AnimalData animal;
		
		// O(n) inferred run complexity
		for (Entry<String, AnimalData> entry: info.entrySet()){
			
			animal = entry.getValue();
					
			// end of animation
			if(animal.canNotContinue()){
			
				if(truncate){
				
					// O(1)
					WidgetHouser.iconLayerList.set(animal.getAnimalIndex(),null);
					WidgetHouser.polyLayerList.set(animal.getAnimalIndex(),null);
					animal.removeLabel();
				}
				
				info.remove(entry.getKey());
				animal = null;
		
			} // still movement remaining
			else if(animal.getTime().compareTo(WidgetHouser.calCounter) <= 0){
					
				SurfaceIcon currIcon = (SurfaceIcon) WidgetHouser.iconLayerList.get(animal.getAnimalIndex());
				
				// first time on screen
				if(animal.getPosition() == 0){

					currIcon.setVisible(true);
					WidgetHouser.labelPan.add(animal.createLabel());
				
				}else{
					
					// updates new position of SurfaceIcon
					currIcon.setLocation(animal.nextPos());
					
					Polyline currLine = (Polyline) WidgetHouser.polyLayerList.get(animal.getAnimalIndex());
					
					// truncate to keep 'valueToRemove' segements
					if(truncate && animal.getPosition() > valueToRemove){
						animal.changeTrail('t');
					}

					// updates new position of Polyline
					currLine.setPositions(animal.changeTrail('a'));
					
				}
				
				//updates counter
				animal.increment();
				
			}
								
		}//end foreach
		
	}
	
	/**
	 * this method is run if the lines option is unselected.
	 * same idea as lines (algorithm)
	 * @throws InterruptedException
	 */
	public static void executeNoLines() throws InterruptedException{
		
		AnimalData animal;
		
		for (Entry<String, AnimalData> entry: info.entrySet()){
			
			animal = entry.getValue();
					
			if(animal.canNotContinue()){
			
				WidgetHouser.iconLayerList.set(animal.getAnimalIndex(),null);
				
				info.remove(entry.getKey());
				animal.removeLabel();
				animal = null;
		
			}
			else if(animal.getTime().compareTo(WidgetHouser.calCounter) <= 0){
					
				SurfaceIcon currIcon = (SurfaceIcon) WidgetHouser.iconLayerList.get(animal.getAnimalIndex());
				
				if(animal.getPosition() == 0){

					currIcon.setVisible(true);
					WidgetHouser.labelPan.add(animal.createLabel());
				
				}else{
					
					currIcon.setLocation(animal.nextPos());	
	
				}
				
				animal.increment();
				
			}
								
		}//end foreach
		
	}
	
	/**
	 * @return total number of animals
	 */
	public static int getTotalAnimals(){
		
		return info.size();
	}
	
	/**
	 * @param v - num segments the user wants 
	 */
	public static void setRemoveInterval(int v){
		
		valueToRemove = v;
		
	}

	/** 
	 * @return the farthest end "longitudinally"
	 */
	public static int getLongBound() {
		
		return longBound;
	}

	/**
	 * @return the farthest end "latitudionally"
	 */
	public static int getLatBound() {
		
		return latBound;
	}
	
	//changes, typecasts and reformats the time properties 
	private static GregorianCalendar getTimeStamp(Cell yr, Cell mnth, Cell day, Cell hrminsec){
		
		int year = Integer.parseInt(yr.getContents());
		int month = Integer.parseInt(mnth.getContents());
		int theday = Integer.parseInt(day.getContents());
		
		String[] toextract = hrminsec.getContents().split(":");
		
		int hour = Integer.parseInt(toextract[0].trim());
		int min = Integer.parseInt(toextract[1].trim());
		int sec = Integer.parseInt(toextract[2].trim());
		
		return new GregorianCalendar(year,month-1,theday,hour,min,sec);
	
	}
	
	/**
	 * Computes the longest length of the animal set, setting this as the
	 * default segment length
	 * @return value of largest animal trail
	 */
	public static int getMaxLinesPossible(){
		
		int max = Integer.MIN_VALUE;
		int tSize;
		
		//stores ID in Animal object
		for (String key: info.keySet()) {
			
			  tSize = info.get(key).getSize();
			  
			  if(max <= tSize){
				  max = tSize;
			  }
			  
		}
	
		return max;
		
	}
		
	
}
