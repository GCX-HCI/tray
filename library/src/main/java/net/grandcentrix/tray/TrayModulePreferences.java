package net.grandcentrix.tray;

import android.content.Context;
import android.support.annotation.NonNull;

/**
 * Created by pascalwelsch on 6/5/15.
 */
@Deprecated
public class TrayModulePreferences extends TrayPreferences {

    public TrayModulePreferences(@NonNull final Context context,
            @NonNull final String module, final int version) {
        super(context, module, version);
    }
}
