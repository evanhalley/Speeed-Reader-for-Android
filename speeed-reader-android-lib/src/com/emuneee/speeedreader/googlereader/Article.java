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

import java.util.ArrayList;

/**
 * Represents a single article in Google Reader
 * 
 * @author Evan
 * 
 */
public class Article {
	private String mId;
	private ArrayList<String> mCategories;
	private String mTitle;
	private long mPublishedTimeStamp;
	private String mArticleLink;
	private String mContent;
	private String mAuthor;
	private int mLikes;
	private String mSiteUrl;
	private String mSiteTitle;
	private boolean mIsRead;
	private boolean mIsStarred;
	
	

	/**
	 * @return the isRead
	 */
	public boolean isIsRead() {
		return mIsRead;
	}

	/**
	 * @param isRead the isRead to set
	 */
	public void setIsRead(boolean isRead) {
		mIsRead = isRead;
	}

	/**
	 * @return the isStarred
	 */
	public boolean isIsStarred() {
		return mIsStarred;
	}

	/**
	 * @param isStarred the isStarred to set
	 */
	public void setIsStarred(boolean isStarred) {
		mIsStarred = isStarred;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return mId;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		mId = id;
	}
	
	public String getCategoriesAsString() {
		final StringBuilder categories;
		
		categories = new StringBuilder();
		for(String category : mCategories) {
			categories.append(category).append(',');
		}
		return categories.toString();
	}

	/**
	 * @return the categories
	 */
	public ArrayList<String> getCategories() {
		return mCategories;
	}

	/**
	 * @param categories
	 *            the categories to set
	 */
	public void setCategories(ArrayList<String> categories) {
		mCategories = categories;
	}
	
	/**
	 * the comma delimited string of categories to set
	 * @param categories
	 */
	public void setCategoriesFromString(String categories) {
		final String[] tempArr;
		
		mCategories = new ArrayList<String>();
		tempArr = categories.split(",");
		for(int i = 0; i < tempArr.length; i++) mCategories.add(tempArr[i]);
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return mTitle;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		mTitle = title;
	}

	/**
	 * @return the publishedTimeStamp
	 */
	public long getPublishedTimeStamp() {
		return mPublishedTimeStamp;
	}

	/**
	 * @param publishedTimeStamp
	 *            the publishedTimeStamp to set
	 */
	public void setPublishedTimeStamp(long publishedTimeStamp) {
		mPublishedTimeStamp = publishedTimeStamp;
	}

	/**
	 * @return the articleLink
	 */
	public String getArticleLink() {
		return mArticleLink;
	}

	/**
	 * @param articleLink
	 *            the articleLink to set
	 */
	public void setArticleLink(String articleLink) {
		mArticleLink = articleLink;
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return mContent;
	}

	/**
	 * @param content
	 *            the content to set
	 */
	public void setContent(String content) {
		mContent = content;
	}

	/**
	 * @return the author
	 */
	public String getAuthor() {
		return mAuthor;
	}

	/**
	 * @param author
	 *            the author to set
	 */
	public void setAuthor(String author) {
		mAuthor = author;
	}

	/**
	 * @return the likes
	 */
	public int getLikes() {
		return mLikes;
	}

	/**
	 * @param likes
	 *            the likes to set
	 */
	public void setLikes(int likes) {
		mLikes = likes;
	}

	/**
	 * @return the siteUrl
	 */
	public String getSiteUrl() {
		return mSiteUrl;
	}

	/**
	 * @param siteUrl
	 *            the siteUrl to set
	 */
	public void setSiteUrl(String siteUrl) {
		mSiteUrl = siteUrl;
	}

	/**
	 * @return the siteTitle
	 */
	public String getSiteTitle() {
		return mSiteTitle;
	}

	/**
	 * @param siteTitle the siteTitle to set
	 */
	public void setSiteTitle(String siteTitle) {
		mSiteTitle = siteTitle;
	}

}
