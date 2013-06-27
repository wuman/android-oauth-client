
package com.wuman.android.auth;

import android.os.Handler;
import android.os.Looper;

import com.google.api.client.auth.oauth.OAuthAuthorizeTemporaryTokenUrl;
import com.google.api.client.auth.oauth.OAuthCredentialsResponse;
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.BrowserClientRequestUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.CredentialStore;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.client.util.Preconditions;
import com.wuman.android.auth.oauth.OAuthHmacCredential;
import com.wuman.android.auth.oauth2.explicit.LenientTokenResponseException;
import com.wuman.android.auth.oauth2.implicit.ImplicitResponseUrl;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * OAuth authorization flow for an installed Android app that persists end-user
 * credentials. This class supports OAuth 1.0a and both explicit and implicit
 * authorization flows for OAuth 2.0.
 * <p>
 * To follow the OAuth 1.0a authorization flow,
 * {@link OAuthManager#authorize10a(String, OAuthCallback, Handler)} should be
 * called.
 * </p>
 * <p>
 * To follow the OAuth 2.0 explicit authorization flow,
 * {@link OAuthManager#authorizeExplicitly(String, OAuthCallback, Handler)}
 * should be called.
 * </p>
 * <p>
 * To follow the OAuth 2.0 implicit authorization flow,
 * {@link OAuthManager#authorizeImplicitly(String, OAuthCallback, Handler)}
 * should be called.
 * </p>
 * <p>
 * Note that all of the above three methods can be called from any thread
 * without blocking the main thread.
 * </p>
 * 
 * @author David Wu
 */
public class OAuthManager {

    static final Logger LOGGER = Logger.getLogger(OAuthConstants.TAG);

    private final AuthorizationFlow mFlow;
    private final AuthorizationUIController mUIController;

    /** Handler that runs in the UI thread. */
    private final Handler mMainHandler = new Handler(Looper.getMainLooper());

    /** ExecutorService that runs tasks in background threads. */
    private final ExecutorService mExecutor;

    public OAuthManager(AuthorizationFlow flow, AuthorizationUIController uiController) {
        this(flow, uiController, Executors.newSingleThreadExecutor());
    }

    public OAuthManager(AuthorizationFlow flow, AuthorizationUIController uiController,
            ExecutorService executor) {
        super();
        this.mFlow = flow;
        this.mUIController = uiController;
        this.mExecutor = Preconditions.checkNotNull(executor);
    }

    public OAuthFuture<Boolean> deleteCredential(final String userId,
            final OAuthCallback<Boolean> callback, Handler handler) {
        Preconditions.checkNotNull(userId);

        final Future2Task<Boolean> task = new Future2Task<Boolean>(handler, callback) {

            @Override
            public void doWork() throws Exception {
                LOGGER.info("deleteCredential");
                CredentialStore store = mFlow.getCredentialStore();
                if (store == null) {
                    set(false);
                    return;
                }

                store.delete(userId, null);
                set(true);
            }

        };

        // run the task in a background thread
        submitTaskToExecutor(task);

        return task;
    }

    /**
     * Authorizes the Android application to access user's protected data using
     * the authorization flow in OAuth 1.0a.
     * 
     * @param userId user ID or {@code null} if not using a persisted credential
     *            store
     * @param callback Callback to invoke when the request completes,
     *            {@code null} for no callback
     * @param handler {@link Handler} identifying the callback thread,
     *            {@code null} for the main thread
     * @return An {@link OAuthFuture} which resolves to a {@link Credential}
     */
    public OAuthFuture<Credential> authorize10a(final String userId,
            final OAuthCallback<Credential> callback, Handler handler) {
        Preconditions.checkNotNull(userId);

        final Future2Task<Credential> task = new Future2Task<Credential>(handler, callback) {

            @Override
            public void doWork() throws Exception {
                try {
                    LOGGER.info("authorize10a");
                    OAuthHmacCredential credential = mFlow.load10aCredential(userId);
                    if (credential != null && credential.getAccessToken() != null
                            && (credential.getRefreshToken() != null
                                    || credential.getExpiresInSeconds() == null
                                    || credential.getExpiresInSeconds() > 60)) {
                        set(credential);
                        return;
                    }

                    String redirectUri = mUIController.getRedirectUri();

                    OAuthCredentialsResponse tempCredentials =
                            mFlow.new10aTemporaryTokenRequest(redirectUri);
                    OAuthAuthorizeTemporaryTokenUrl authorizationUrl =
                            mFlow.new10aAuthorizationUrl(tempCredentials.token);
                    mUIController.requestAuthorization(authorizationUrl);

                    String code = mUIController.waitForVerifierCode();
                    OAuthCredentialsResponse response =
                            mFlow.new10aTokenRequest(tempCredentials, code).execute();
                    credential = mFlow.createAndStoreCredential(response, userId);
                    set(credential);
                } finally {
                    mUIController.stop();
                }
            }

        };

        // run the task in a background thread
        submitTaskToExecutor(task);

        return task;
    }

    /**
     * Authorizes the Android application to access user's protected data using
     * the Explicit Authorization Code flow in OAuth 2.0.
     * 
     * @param userId user ID or {@code null} if not using a persisted credential
     *            store
     * @param callback Callback to invoke when the request completes,
     *            {@code null} for no callback
     * @param handler {@link Handler} identifying the callback thread,
     *            {@code null} for the main thread
     * @return An {@link OAuthFuture} which resolves to a {@link Credential}
     */
    public OAuthFuture<Credential> authorizeExplicitly(final String userId,
            final OAuthCallback<Credential> callback, Handler handler) {
        Preconditions.checkNotNull(userId);

        final Future2Task<Credential> task = new Future2Task<Credential>(handler, callback) {

            @Override
            public void doWork() throws Exception {
                try {
                    Credential credential = mFlow.loadCredential(userId);
                    LOGGER.info("authorizeExplicitly");
                    if (credential != null && credential.getAccessToken() != null
                            && (credential.getRefreshToken() != null ||
                                    credential.getExpiresInSeconds() == null ||
                            credential.getExpiresInSeconds() > 60)) {
                        set(credential);
                        return;
                    }

                    String redirectUri = mUIController.getRedirectUri();

                    AuthorizationCodeRequestUrl authorizationUrl = mFlow
                            .newExplicitAuthorizationUrl()
                            .setRedirectUri(redirectUri);
                    mUIController.requestAuthorization(authorizationUrl);

                    String code = mUIController.waitForExplicitCode();
                    TokenResponse response = mFlow.newTokenRequest(code)
                            .setRedirectUri(redirectUri).execute();
                    credential = mFlow.createAndStoreCredential(response, userId);
                    set(credential);
                } finally {
                    mUIController.stop();
                }
            }

        };

        // run the task in a background thread
        submitTaskToExecutor(task);

        return task;
    }

    /**
     * Authorizes the Android application to access user's protected data using
     * the Implicit Authorization flow in OAuth 2.0.
     * 
     * @param userId user ID or {@code null} if not using a persisted credential
     *            store
     * @param callback Callback to invoke when the request completes,
     *            {@code null} for no callback
     * @param handler {@link Handler} identifying the callback thread,
     *            {@code null} for the main thread
     * @return An {@link OAuthFuture} which resolves to a {@link Credential}
     */
    public OAuthFuture<Credential> authorizeImplicitly(final String userId,
            final OAuthCallback<Credential> callback, Handler handler) {
        Preconditions.checkNotNull(userId);

        final Future2Task<Credential> task = new Future2Task<Credential>(handler, callback) {

            @Override
            public void doWork() throws TokenResponseException, Exception {
                try {
                    LOGGER.info("authorizeImplicitly");
                    Credential credential = mFlow.loadCredential(userId);
                    if (credential != null && credential.getAccessToken() != null
                            && (credential.getRefreshToken() != null ||
                                    credential.getExpiresInSeconds() == null ||
                            credential.getExpiresInSeconds() > 60)) {
                        set(credential);
                        return;
                    }

                    String redirectUri = mUIController.getRedirectUri();

                    BrowserClientRequestUrl authorizationUrl = mFlow.newImplicitAuthorizationUrl()
                            .setRedirectUri(redirectUri);
                    mUIController.requestAuthorization(authorizationUrl);

                    ImplicitResponseUrl implicitResponse = mUIController
                            .waitForImplicitResponseUrl();
                    credential = mFlow.createAndStoreCredential(implicitResponse, userId);
                    set(credential);
                } finally {
                    mUIController.stop();
                }
            }

        };

        // run the task in a background thread
        submitTaskToExecutor(task);

        return task;
    }

    /**
     * An {@code OAuthCallback} represents the callback handler to be invoked
     * when an asynchronous {@link OAuthManager} call is completed.
     * 
     * @author David Wu
     * @param <T>
     */
    public static interface OAuthCallback<T> {
        /**
         * Callback method that is invoked when an asynchronous
         * {@link OAuthManager} call is completed.
         * 
         * @param future An {@link OAuthFuture} that represents the result of an
         *            asynchronous {@link OAuthManager} call.
         */
        void run(OAuthFuture<T> future);
    }

    /**
     * An {@code OAuthFuture} represents the result of an asynchronous
     * {@link OAuthManager} call. Methods are provided to check if the
     * computation is complete, to wait for its completion, and to retrieve the
     * result of the computation. The result can only be retrieved using method
     * {@link #getResult()} or {@link #getResult(long, TimeUnit)} when the
     * computation has completed, blocking if necessary until it is ready.
     * Cancellation is performed by the {@link #cancel(boolean)} method.
     * Additional methods are provided to determine if the task completed
     * normally or was cancelled. Once a computation has completed, the
     * computation cannot be cancelled.
     * 
     * @author David Wu
     * @param <V>
     */
    public static interface OAuthFuture<V> {

        /**
         * Attempts to cancel execution of this task. This attempt will fail if
         * the task has already completed, has already been cancelled, or could
         * not be cancelled for some other reason. If successful, and this task
         * has not started when {@link #cancel(boolean)} is called, this task
         * should never run. If the task has already started, then the
         * {@code mayInterruptIfRunning} parameter determines whether the thread
         * executing this task should be interrupted in an attempt to stop the
         * task.
         * <p>
         * After this method returns, subsequent calls to {@link #isDone()} will
         * always return {@code true}. Subsequent calls to
         * {@link #isCancelled()} will always return {@code true} if this method
         * returns {@code true}.
         * </p>
         * 
         * @param mayInterruptIfRunning {@code true} if the thread executing
         *            this task should be interrupted; otherwise, in-progress
         *            tasks are allowed to complete
         * @return {@code false} if the task could not be cancelled, typically
         *         because it has already completed normally; {@code true}
         *         otherwise
         */
        boolean cancel(boolean mayInterruptIfRunning);

        /**
         * Returns {@code true} if this task was cancelled before it completed
         * normally.
         * 
         * @return {@code true} if this task was cancelled before it completed
         */
        boolean isCancelled();

        /**
         * Returns {@code true} if this task completed.
         * <p>
         * Completion may be due to normal termination, an exception, or
         * cancellation -- in all of these cases, this method will return
         * {@code true}.
         * </p>
         * 
         * @return {@code true} if this task completed
         */
        boolean isDone();

        /**
         * Accessor for the future result the {@link OAuthFuture} represents.
         * This call will block until the result is available. In order to check
         * if the result is available without blocking, one may call
         * {@link #isDone()} and {@link #isCancelled()}. If the request that
         * generated this result fails or is canceled then an exception will be
         * thrown rather than the call returning normally.
         * 
         * @return the actual result
         * @throws CancellationException if the request was canceled for any
         *             reason
         * @throws IOException if an IOException occurred while communicating
         *             with the server.
         */
        V getResult() throws CancellationException, IOException;

        /**
         * Accessor for the future result the {@link OAuthFuture} represents.
         * This call will block until the result is available. In order to check
         * if the result is available without blocking, one may call
         * {@link #isDone()} and {@link #isCancelled()}. If the request that
         * generated this result fails or is canceled then an exception will be
         * thrown rather than the call returning normally. If a timeout is
         * specified then the request will automatically be canceled if it does
         * not complete in that amount of time.
         * 
         * @param timeout the maximum time to wait
         * @param unit the time unit of the timeout argument. This must not be
         *            null.
         * @return the actual result
         * @throws CancellationException if the request was canceled for any
         *             reason
         * @throws IOException if an IOException occurred while communicating
         *             with the server.
         */
        V getResult(long timeout, TimeUnit unit) throws CancellationException, IOException;
    }

    protected final void submitTaskToExecutor(final Future2Task<?> task) {
        // run the task in a background thread
        mExecutor.submit(new Runnable() {
            public void run() {
                task.start();
            }
        });
    }

    private abstract class BaseFutureTask<T> extends FutureTask<T> {
        final Handler mHandler;

        public BaseFutureTask(Handler handler) {
            super(new Callable<T>() {
                public T call() throws Exception {
                    throw new IllegalStateException("this should never be called");
                }
            });
            this.mHandler = handler;
        }

        public abstract void doWork() throws Exception;

        protected void startTask() {
            try {
                doWork();
            } catch (Exception e) {
                setException(e);
            }
        }

        protected void postRunnableToHandler(Runnable runnable) {
            Handler handler = (mHandler == null) ? mMainHandler : mHandler;
            handler.post(runnable);
        }
    }

    public abstract class Future2Task<T> extends BaseFutureTask<T> implements
            OAuthFuture<T> {

        final OAuthCallback<T> mCallback;

        public Future2Task(Handler handler, OAuthCallback<T> callback) {
            super(handler);
            mCallback = callback;
        }

        public Future2Task<T> start() {
            startTask();
            return this;
        }

        @Override
        protected void done() {
            if (mCallback != null) {
                postRunnableToHandler(new Runnable() {
                    public void run() {
                        mCallback.run(Future2Task.this);
                    }
                });
            }
        }

        @Override
        public T getResult() throws CancellationException, IOException {
            return internalGetResult(null, null);
        }

        @Override
        public T getResult(long timeout, TimeUnit unit) throws CancellationException,
                IOException {
            return internalGetResult(timeout, unit);
        }

        private T internalGetResult(Long timeout, TimeUnit unit) throws CancellationException,
                IOException {
            if (!isDone()) {
                ensureNotOnMainThread();
            }
            try {
                if (timeout == null) {
                    return get();
                } else {
                    return get(timeout, unit);
                }
            } catch (CancellationException e) {
                throw new CancellationException(e.getMessage());
            } catch (TimeoutException e) {
                // fall through and cancel
            } catch (InterruptedException e) {
                // fall through and cancel
            } catch (ExecutionException e) {
                final Throwable cause = e.getCause();
                if (cause instanceof LenientTokenResponseException) {
                    throw (LenientTokenResponseException) cause;
                } else if (cause instanceof TokenResponseException) {
                    throw (TokenResponseException) cause;
                } else if (cause instanceof IOException) {
                    throw (IOException) cause;
                } else if (cause instanceof RuntimeException) {
                    throw (RuntimeException) cause;
                } else if (cause instanceof Error) {
                    throw (Error) cause;
                } else {
                    throw new IllegalStateException(cause);
                }
            } finally {
                cancel(true /* interrupt if running */);
            }
            throw new CancellationException();
        }

        private void ensureNotOnMainThread() {
            final Looper looper = Looper.myLooper();
            if (looper != null && looper == Looper.getMainLooper()) {
                final IllegalStateException exception = new IllegalStateException(
                        "calling this from your main thread can lead to deadlock");
                LOGGER.log(Level.WARNING,
                        "calling this from your main thread can lead to deadlock and/or ANRs",
                        exception);
                throw exception;
            }
        }

    }

}
