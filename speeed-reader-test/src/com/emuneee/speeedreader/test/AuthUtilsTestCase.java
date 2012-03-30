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
package com.emuneee.speeedreader.test;

import com.emuneee.speeedreader.AuthUtils;
import com.emuneee.speeedreader.activity.LoginActivity;

import android.accounts.Account;
import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

/**
 * @author Evan
 *
 */
public class AuthUtilsTestCase extends
		ActivityInstrumentationTestCase2<LoginActivity> {
	private final String TAG = getClass().getSimpleName();
	private Activity mActivity;
	
	public AuthUtilsTestCase() {
		super("com.emuneee.speeedreader", LoginActivity.class);
	}
	
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mActivity = this.getActivity();
    }
    
    /**
     * Test that we are able to pull the Google accounts from the device
     */
    public void testGetAccountList() {
    	Account[] accounts;
    	
    	accounts = AuthUtils.getGoogleAccounts(mActivity);
    	assertEquals(true, accounts.length > 0);
    }
    
    /**
     * Test that we are able to pull a specific Google accounts from the device
     */
    public void testGetSpecificAccount() {
    	Account[] accounts;
    	
    	accounts = AuthUtils.getGoogleAccounts(mActivity);
    	assertEquals(true, accounts[0].name.contentEquals("evan.halley@gmail.com"));
    }
    
    /**
     * Test we are able to get a auth token for Google Reader permissions
     */
    public void testGetAuthToken() {
    	Account[] accounts;
    	String token;
    	
    	accounts = AuthUtils.getGoogleAccounts(mActivity);
    	token = AuthUtils.getAuthToken(mActivity, accounts[0].name, "reader");
    	Log.v(TAG, "Token: " + token);
    	assertNotNull(token);
    }
    
    /**
     * Test we are able to get a refreshed auth token for Google Reader permissions
     */
    public void testRefreshAuthToken() {
    	Account[] accounts;
    	String oldToken;
    	String newToken;
    	
    	accounts = AuthUtils.getGoogleAccounts(mActivity);
    	oldToken = AuthUtils.getAuthToken(mActivity, accounts[0].name, "reader");
    	newToken = AuthUtils.refreshAuthToken(mActivity, oldToken, accounts[0].name, "reader");
    	assertNotNull(newToken);
    	assertEquals(false, newToken.contentEquals(oldToken));
    }
}
