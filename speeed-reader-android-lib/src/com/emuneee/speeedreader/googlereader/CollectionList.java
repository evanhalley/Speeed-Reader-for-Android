/*
 * Copyright 2012 emuneee apps
 * http://emuneee.com/apps
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
package com.emuneee.speeedreader.googlereader;

import java.util.Hashtable;
import java.util.Map;

/**
 * Wrapper for a list of collections
 * @author Evan
 *
 */
public class CollectionList {
	private final Map<String, Collection> mCollections;
	
	public CollectionList() {
		mCollections = new Hashtable<String, Collection>();
	}
	
	/**
	 * Adds a collection to the collection list
	 * @param collectionId collection id
	 * @param collection collection to add
	 */
	public void addCollection(String collectionId, Collection collection) {
		mCollections.put(collectionId, collection);
	}
	
	/**
	 * Returns a collection
	 * @param collectionId id of the collection to return
	 * @return collection
	 */
	public Collection getCollection(String collectionId) {
		return mCollections.get(collectionId);
	}
	
	/**
	 * Returns the current size of the collection list
	 * @return size of the collection list
	 */
	public int getCollectionListSize() {
		return mCollections.size();
	}
	
	/**
	 * Returns the Map of collectionss
	 * @return
	 */
	public Map<String, Collection> getCollectionList() {
		return mCollections;
	}
}
