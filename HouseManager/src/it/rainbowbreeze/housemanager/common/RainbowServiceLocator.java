/**
 * Copyright (C) 2010 Alfredo Morresi
 * 
 * This file is part of RainbowLibs project.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.rainbowbreeze.housemanager.common;

import java.util.ArrayList;
import java.util.List;

import static it.rainbowbreeze.housemanager.common.RainbowContractHelper.*;

public class RainbowServiceLocator {
	//---------- Private fields
	private static List<Object>  mServices;



	//---------- Constructors
	static {
		mServices = new ArrayList<Object>();
	}

	//---------- Public properties



	
	//---------- Public methods
	/**
	 * Get a service from Locator
	 * 
	 * @param serviceToRetrieve
	 */
	public static <T extends Object> T get(Class<T> serviceToRetrieve) {
		checkNotNull(serviceToRetrieve, "Service to retrieve");
		T retrievedService = null;

		for (Object service:mServices) {
			if (serviceToRetrieve.isInstance(service)) {
				retrievedService = serviceToRetrieve.cast(service);
				break;
			}
		}
		return retrievedService;
	}
	
	/**
	 * Put a service inside the Locator
	 * 
	 * @param serviceToAdd
	 */
	public static void put(Object serviceToAdd) {
		checkNotNull(serviceToAdd, "Service to add");
		
		//search if service is already present
		remove(serviceToAdd.getClass());
		//add the service
		mServices.add(serviceToAdd);
	}
	
	
	/**
	 * Remove a service from Locator
	 * 
	 * @param serviceToRemove
	 */
	public static <T extends Object> void remove(Class<T> serviceToRemove) {
		checkNotNull(serviceToRemove, "Service to remove");
	
		//TODO
		//still not covered:
		//-father object in in the list
		//-children object is added in the list
		//-father must be removed and children is added to the list
		for(int i=0; i<mServices.size(); i++) {
			Object service = mServices.get(i);
            if (serviceToRemove.isInstance(service)) {
				mServices.remove(i);
				break;
			}
		}
	}
	
	
	/**
	 * Clean all services inside Locator
	 */
	public static void clear() {
		mServices.clear();
	}




	//---------- Private methods

}
