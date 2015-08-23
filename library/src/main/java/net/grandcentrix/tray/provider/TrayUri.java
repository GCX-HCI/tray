package net.grandcentrix.tray.provider;

import net.grandcentrix.tray.storage.TrayStorage.Type;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

/**
 * Created by pascalwelsch on 6/11/15.
 */
public class TrayUri {

    public final class Builder {

        private boolean mInternal;

        private String mKey;

        private String mModule;

        private Type mStorage = Type.UNDEFINED;

        public Builder(final Context context) {
            mContext = context.getApplicationContext();
        }

        public Uri build() {
            final Uri uri = mInternal ? mContentUriInternal : mContentUri;
            final Uri.Builder builder = uri.buildUpon();
            if (mModule != null) {
                builder.appendPath(mModule);
            }
            if (mKey != null) {
                builder.appendPath(mKey);
            }
            if (mStorage != Type.UNDEFINED) {
                builder.appendQueryParameter("backup",
                        Type.USER.equals(mStorage) ? "true" : "false");
            }
            return builder.build();
        }

        public Builder setInternal(final boolean internal) {
            mInternal = internal;
            return this;
        }

        public Builder setKey(final String key) {
            mKey = key;
            return this;
        }

        public Builder setModule(final String module) {
            mModule = module;
            return this;
        }

        public Builder setType(Type storage) {
            mStorage = storage;
            return this;
        }
    }

    private final Uri mContentUri;

    private final Uri mContentUriInternal;

    private Context mContext;

    public TrayUri(@NonNull final Context context) {
        mContext = context;
        mContentUri = TrayContract.generateContentUri(context);
        mContentUriInternal = TrayContract.generateInternalContentUri(context);
    }

    public Builder builder() {
        return new Builder(mContext);
    }

    public Uri get() {
        return mContentUri;
    }

    public Uri getInternal() {
        return mContentUriInternal;
    }
}
