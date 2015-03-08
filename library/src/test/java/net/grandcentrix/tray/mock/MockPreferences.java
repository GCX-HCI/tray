package net.grandcentrix.tray.mock;

import net.grandcentrix.tray.TrayModulePreferences;

import android.content.Context;

/**
 * Created by pascalwelsch on 2/26/15.
 */
public class MockPreferences extends TrayModulePreferences {

    public MockPreferences(final Context context, final String module) {
        super(context, module, 1);
    }

    @Override
    protected void onCreate(final int newVersion) {

    }

    @Override
    protected void onUpgrade(final int oldVersion, final int newVersion) {

    }
}
