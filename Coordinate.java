package extra;
import java.util.GregorianCalendar;
import gov.nasa.worldwind.geom.Position;

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
 * Wrapper class to hold the position AND time value of one movement
 * 
 */
@SuppressWarnings("rawtypes")
public class Coordinate implements Comparable {
	
	private Position latlonCoord;				//the Position object
	private GregorianCalendar timestamp; 		//associated Time

	public Coordinate(Position ic, GregorianCalendar tme){
		
		latlonCoord = ic;
		timestamp = tme;
		
	}

	/**
	 * 
	 * @return the position value
	 */
	public Position getCoord() {
		
		return latlonCoord;
		
	}

	/**
	 * 
	 * @return the time value
	 */
	public GregorianCalendar getTimeStamp() {
		
		return timestamp;
		
	}

	/**
	 * Overridden compareTo method, compares Coordinate class
	 * based upon time attribute
	 */
	public int compareTo(Object arg0) {
		
		if(arg0 == this){
			return 0;
		}
		
		Coordinate pass = (Coordinate) arg0;
		return timestamp.compareTo(pass.timestamp);
	}
	
}
