package com.example.projectchat;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

class Utility {

    static String removeTextEng = "Remove",
            removeTextNor = "Slett",
            urlEnglish = "http://www.nyinorge.no/en/Familiegjenforening/New-in-Norway/Housing/Renting-a-houseapartment/Your-rights-as-a-tenant/",
            urlNorsk = "http://www.nyinorge.no/no/Familiegjenforening/Ny-i-Norge/Bolig/A-leie-bolig/Rettigheter-som-leietaker/";

    static String languageSwitch(String eng, String nor) {
        String t;
        switch (UserDetails.language) {
            case "English":
                t = eng;
                break;
            case "Norsk":
                t = nor;
                break;
            default:
                t = "";
                break;
        }
        return t;
    }

    static void savePreference_1(Context context, String p, String into_1, String val_1) {
        SharedPreferences pref = context.getSharedPreferences(p, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString(into_1, val_1);
        editor.apply();
    }

    static void savePreference_2(Context context, String p, String into_1, String val_1, String into_2, String val_2) {
        SharedPreferences pref = context.getSharedPreferences(p, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString(into_1, val_1);
        editor.putString(into_2, val_2);
        editor.apply();
    }

    static void savePreference_3(Context context, String p, String into_1, String val_1,
                                 String into_2, String val_2, String into_3, String val_3) {
        SharedPreferences pref = context.getSharedPreferences(p, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString(into_1, val_1);
        editor.putString(into_2, val_2);
        editor.putString(into_3, val_3);
        editor.apply();
    }
}