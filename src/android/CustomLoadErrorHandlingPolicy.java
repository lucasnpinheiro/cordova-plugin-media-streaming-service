package com.paulkjoseph.mediastreaming;

import android.util.Log;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.upstream.DefaultLoadErrorHandlingPolicy;
import com.google.android.exoplayer2.upstream.HttpDataSource;

import java.io.FileNotFoundException;
import java.io.IOException;

public final class CustomLoadErrorHandlingPolicy extends DefaultLoadErrorHandlingPolicy {

    private final static String TAG = CustomLoadErrorHandlingPolicy.class.getName();

    public CustomLoadErrorHandlingPolicy() {
        super();
    }

    public CustomLoadErrorHandlingPolicy(int minimumLoadableRetryCount) {
        super(minimumLoadableRetryCount);
    }

    @Override
    public long getBlacklistDurationMsFor(int dataType, long loadDurationMs, IOException exception, int errorCount) {
        Log.e(TAG, "getBlacklistDurationMsFor[dataType]: " + dataType + ", [loadDurationMs]: " + loadDurationMs + ", [errorCount]: " + errorCount, exception);
        if (exception instanceof HttpDataSource.InvalidResponseCodeException
                && ((HttpDataSource.InvalidResponseCodeException) exception).responseCode == 500) {
            return DEFAULT_TRACK_BLACKLIST_MS;
        }
        return super.getBlacklistDurationMsFor(
                dataType, loadDurationMs, exception, errorCount);
    }

    @Override
    public long getRetryDelayMsFor(int dataType, long loadDurationMs, IOException exception, int errorCount) {
        Log.e(TAG, "getRetryDelayMsFor[dataType]: " + dataType + ", [loadDurationMs]: " + loadDurationMs + ", [errorCount]: " + errorCount, exception);
        return exception instanceof FileNotFoundException
                ? C.TIME_UNSET
                : super.getRetryDelayMsFor(
                dataType, loadDurationMs, exception, errorCount);
    }

    @Override
    public int getMinimumLoadableRetryCount(int dataType) {
        return super.getMinimumLoadableRetryCount(dataType);
    }
}
