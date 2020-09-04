/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.norman.util;

import android.annotation.SuppressLint;
import android.os.Build;

/**
 * Class containing some static utility methods.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
@SuppressLint("ObsoleteSdkInt")
public final class APIUtils {

	/**
	 * android4.4 KitKat
	 */
	public static final int KITKAT = 19;
	/**
	 * android5.0 Lollipop
	 */
	public static final int LOLLIPOP = 21;

	/**
	 * android5.1 LOLLIPOP MR1
	 */
	public static final int LOLLIPOP_MR1 = 22;
	
	/**
	 * android6.0 MarshMallow
	 */
	public static final int MARSHMALLOW = 23;

    /**
     * android7.0 Nougat
     */
    public static final int Nougat = 24;

    /**
     * android7.1 NougatPlus
     */
    public static final int NougatPlus = 25;

    /**
     * Private constructor to prohibit nonsense instance creation.
     */
    private APIUtils() {
    }

    /**
     * If platform is Froyo (level 8) or above.
     * @return If platform SDK is above Froyo
     */
    public static boolean hasFroyo() {
        // Can use static final constants like FROYO, declared in later versions
        // of the OS since they are inlined at compile time. This is guaranteed behavior.
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    }

    /**
     * If platform is Gingerbread (level 9) or above.
     * @return If platform SDK is above Gingerbread
     */
    public static boolean hasGingerbread() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
    }
    
    /**
     * If platform is Gingerbread (level 10).
     * @return If platform SDK is GINGERBREAD_MR1
     */
    public static boolean isGingerbreadmr1() {
        return Build.VERSION.SDK_INT == Build.VERSION_CODES.GINGERBREAD_MR1;
    }
    
    /**
     * If platform is Gingerbread (level 9).
     * @return If platform SDK is Gingerbread
     */
    public static boolean isGingerbread() {
        return Build.VERSION.SDK_INT == Build.VERSION_CODES.GINGERBREAD;
    }

    /**
     * sdk>11(android 3.0)
     * @return If platform SDK is above Honeycomb
     */
    public static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    /**
     * If platform is Honeycomb MR1 (level 12) or above.
     * @return If platform SDK is above Honeycomb MR1
     */
    public static boolean hasHoneycombMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
    }
    
    /**
     * If platform is Ice Cream Sandwich (level 14) or above.
     * @return If platform SDK is above Ice Cream Sandwich
     */
    public static boolean hasICS() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }
    
    /**
     * If platform is Ice Cream Sandwich MR1 (level 15) or above.
     * @return If platform SDK is above Ice Cream Sandwich MR1
     */
    public static boolean hasICSMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1;
    }

    /**
     * If platform is JellyBean (level 16) or above.
     * @return If platform SDK is above JellyBean
     */
    public static boolean hasJellyBean() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    /**
     * If platform is JellyBean MR1 (level 17) or above.
     * @return If platform SDK is above JellyBean MR1
     */
    public static boolean hasJellyBeanMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
    }

    /**
     * If platform is JellyBean MR1 (level 18) or above.
     * @return If platform SDK is above JellyBean MR2
     */
    public static boolean hasJellyBeanMR2() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2;
    }

    /**
     * If platform is KitKat (level 19) or above.
     * @return If platform SDK is above KitKat
     */
    public static boolean hasKitKat() {
        return Build.VERSION.SDK_INT >= KITKAT;
    }
    
    /**
     * If platform is KitKat (level 19).
     * @return If platform SDK is KitKat
     */
    public static boolean isKitKat() {
        return Build.VERSION.SDK_INT == KITKAT;
    }
    
    /**
     * If platform is Lollipop (level 21) or above.
     * @return If platform SDK is above Lollipop
     */
    public static boolean hasLollipop() {
        return Build.VERSION.SDK_INT >= LOLLIPOP;
    }

    /**
     * If platform is Lollipop MR1 (level 22) or above.
     * @return If platform SDK is above Lollipop MR1
     */
    public static boolean hasLollipopMR1() {
        return Build.VERSION.SDK_INT >= LOLLIPOP_MR1;
    }
    
    /**
     * If platform is Lollipop (level 21).
     * @return If platform SDK is above Lollipop
     */
    public static final boolean isLollipop() {
        return Build.VERSION.SDK_INT == LOLLIPOP;
    }
    
    public static boolean hasMarshMallow() {
        return Build.VERSION.SDK_INT >= MARSHMALLOW;
    }

    /**
     * If platform is Nougat (level 24).
     * @return If platform SDK is above Nougat
     */
    public static boolean hasNougat() {
        return Build.VERSION.SDK_INT >= Nougat;
    }

    /**
     * If platform is hasNougatMR1 (level 25).
     * @return If platform SDK is above NougatPlus
     */
    public static boolean hasNougatMR1() {
        return Build.VERSION.SDK_INT >= NougatPlus;
    }

    /**
     * If platform is has O (level 26).
     * @return If platform SDK is above O
     */
    public static boolean hasOreo() {
        return Build.VERSION.SDK_INT >= 26;
    }
}
