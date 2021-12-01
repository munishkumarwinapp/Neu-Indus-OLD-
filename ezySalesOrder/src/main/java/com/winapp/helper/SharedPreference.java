package com.winapp.helper;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.winapp.SFA.R;

import java.util.Locale;

/**
 * Created by user on 05-Jun-17.
 */

public class SharedPreference implements Constants {
private SharedPreferences preference;

    private Context context;
    public SharedPreference(Context context){
        this.context = context;
        preference = this.context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
    }
    public void putLanguage(String language){
        Editor editor =  preference.edit();
        editor.putString(PREF_LANGUAGE,language);
        editor.commit();
    }
    public String getLanguage(){
       return preference.getString(PREF_LANGUAGE,null);
    }
    public Locale getLanguageLocale(){
        String[] supportedLanguages = context.getResources().getStringArray(R.array.supported_languages);
        if(preference.getString(PREF_LANGUAGE,"en").equals(supportedLanguages[1])){
            return Locale.SIMPLIFIED_CHINESE;
        }
        return Locale.ENGLISH;
    }
}
