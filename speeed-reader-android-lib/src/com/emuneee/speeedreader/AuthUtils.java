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
package com.emuneee.speeedreader;

import java.io.IOException;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

/**
 * @author Evan
 *
 */
public class AuthUtils {
	public final static String GOOGLE_ACCOUNT_TYPE = "com.google";
	private final static String TAG = "AuthUtils";
	
	/**
     * Retrieves all of the Google accounts on the device
     * @param context
     * @return
     */
    public static Account[] getGoogleAccounts(Context context) {
            return AccountManager.get(context).getAccountsByType(GOOGLE_ACCOUNT_TYPE);
    }
	
	public static String getAuthToken(Activity activity, String name, String googleApi) {
		String authToken = null;
		final Account account;
		AccountManagerFuture<Bundle> accountFuture;
		
		account = new Account(name, GOOGLE_ACCOUNT_TYPE);
		accountFuture = AccountManager.get(activity)
				.getAuthToken(account, googleApi, null, activity, null, null);

		try {
			authToken = accountFuture.getResult().get(AccountManager.KEY_AUTHTOKEN).toString();
			// invalidate the retrieved token and get a fresh one
			AccountManager.get(activity).invalidateAuthToken(GOOGLE_ACCOUNT_TYPE, authToken);
			accountFuture = AccountManager.get(activity)
					.getAuthToken(account, googleApi, null, activity, null, null);
			authToken = accountFuture.getResult().get(AccountManager.KEY_AUTHTOKEN).toString();     
		} catch (OperationCanceledException e) {
			Log.e(TAG, e.toString());
		} catch (AuthenticatorException e) {
			Log.e(TAG, e.toString());
		} catch (IOException e) {
			Log.e(TAG, e.toString());
		} 
		return authToken;
	}
	
	public static String refreshAuthToken(Activity activity, String token, String name, String googleApi) {
		String authToken = null;
		final Account account;
		AccountManagerFuture<Bundle> accountFuture;
		
		account = new Account(name, GOOGLE_ACCOUNT_TYPE);
		try {
			// invalidate the retrieved token and get a fresh one
			AccountManager.get(activity).invalidateAuthToken(GOOGLE_ACCOUNT_TYPE, token);
			accountFuture = AccountManager.get(activity)
					.getAuthToken(account, googleApi, null, activity, null, null);
			authToken = accountFuture.getResult().get(AccountManager.KEY_AUTHTOKEN).toString();     
		} catch (OperationCanceledException e) {
			Log.e(TAG, e.toString());
		} catch (AuthenticatorException e) {
			Log.e(TAG, e.toString());
		} catch (IOException e) {
			Log.e(TAG, e.toString());
		} 
		return authToken;
	}
}
