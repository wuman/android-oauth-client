
package com.wuman.oauth.samples;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.text.TextUtils;

import com.google.api.client.auth.oauth2.TokenErrorResponse;
import com.google.api.client.auth.oauth2.TokenResponseException;
import com.wuman.android.auth.oauth2.explicit.LenientTokenResponseException;
import com.wuman.oauth.samples.AsyncResourceLoader.Result;

import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AsyncResourceLoader<T> extends AsyncTaskLoader<Result<T>> {

    static final Logger LOGGER = Logger.getLogger(SamplesConstants.TAG);

    Result<T> mResult;

    public AsyncResourceLoader(Context context) {
        super(context);
    }

    @Override
    public Result<T> loadInBackground() {
        Result<T> result = new Result<T>();
        try {
            result.data = loadResourceInBackground();
            updateErrorStateIfApplicable(result);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "loadInBackground thrown error", e);
            StackTraceElement t = e.getStackTrace()[0];
            result.success = false;
            if (e instanceof LenientTokenResponseException) {
                LenientTokenResponseException tre = (LenientTokenResponseException) e;
                TokenErrorResponse errorResponse = tre.getDetails();
                result.errorMessage = errorResponse.getError();
                if (!TextUtils.isEmpty(errorResponse.getErrorDescription())) {
                    result.errorMessage += (": " + errorResponse.getErrorDescription());
                }
            } else if (e instanceof TokenResponseException) {
                TokenResponseException tre = (TokenResponseException) e;
                TokenErrorResponse errorResponse = tre.getDetails();
                result.errorMessage = errorResponse.getError();
                if (!TextUtils.isEmpty(errorResponse.getErrorDescription())) {
                    result.errorMessage += (": " + errorResponse.getErrorDescription());
                }
            } else {
                result.errorMessage = e.getMessage();
            }
            result.errorMessage += ("\n"
                    + e.getClass().getName() + " at " + t.getClassName()
                    + "(" + t.getFileName() + ":" + t.getLineNumber() + ")");
            result.exception = e;
        }
        return result;
    }

    public abstract T loadResourceInBackground() throws Exception;

    /**
     * Subclass should update the correct values for {@link Result#success} and
     * {@link Result#errorMessage} based on {@link Result#data}.
     * 
     * @param result
     */
    public abstract void updateErrorStateIfApplicable(Result<T> result);

    @Override
    public void deliverResult(Result<T> result) {
        if (isReset()) {
            if (result != null) {
                onReleaseResources(result);
            }
            return;
        }
        Result<T> oldFeed = mResult;
        mResult = result;

        if (isStarted()) {
            super.deliverResult(result);
        }

        if (oldFeed != null) {
            onReleaseResources(oldFeed);
        }
    }

    @Override
    protected void onStartLoading() {
        if (mResult != null) {
            deliverResult(mResult);
        }

        if (takeContentChanged() || mResult == null) {
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    public void onCanceled(Result<T> data) {
        super.onCanceled(data);
        onReleaseResources(data);
    }

    @Override
    protected void onReset() {
        super.onReset();

        onStopLoading();

        if (mResult != null) {
            onReleaseResources(mResult);
            mResult = null;
        }
    }

    protected void onReleaseResources(Result<T> result) {
        if (result != null) {
            if (result.data != null) {
                result.data = null;
            }
            if (result.exception != null) {
                result.exception = null;
            }
        }
    }

    public static final class Result<T> {
        public T data;
        public Exception exception;
        public boolean success = true;
        public String errorMessage;
    }

}
