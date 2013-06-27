
package com.wuman.android.auth;

import android.os.Handler;
import android.os.OperationCanceledException;

import com.wuman.android.auth.OAuthManager.OAuthCallback;
import com.wuman.android.auth.OAuthManager.OAuthFuture;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.util.RobolectricBackgroundExecutorService;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(RobolectricTestRunner.class)
public class OAuthManagerTest extends TestCase {

    private MockOAuthManager mock;
    private CountDownLatch latch;

    @Before
    public void setUp() throws Exception {
        mock = new MockOAuthManager(new RobolectricBackgroundExecutorService());
        latch = new CountDownLatch(1);
    }

    @After
    public void tearDown() throws Exception {
        mock = null;
        latch = null;
    }

    @Test
    public void testNormalTaskExecution() throws OperationCanceledException, IOException {
        OAuthFuture<String> future = mock.runTest(null, null, null);
        String result = future.getResult();
        assertEquals("ok", result);
    }

    @Test(expected = RuntimeException.class)
    public void testTaskExecutionThatThrowsException() throws OperationCanceledException,
            IOException {
        OAuthFuture<String> future = mock.runTest(new Runnable() {
            @Override
            public void run() {
                throw new RuntimeException();
            }
        }, null, null);
        future.getResult();
    }

    @Test
    public void testCallback() throws InterruptedException, OperationCanceledException, IOException {
        MockOAuthCallback mockCallback = new MockOAuthCallback();
        mock.runTest(null, mockCallback, null);
        latch.await(10, TimeUnit.SECONDS);
        assertEquals("ok", mockCallback.getResult());
    }

    private static class MockOAuthCallback implements OAuthCallback<String> {
        private OAuthFuture<String> future = null;

        public String getResult() throws OperationCanceledException, IOException {
            return future.getResult();
        }

        @Override
        public void run(OAuthFuture<String> future) {
            this.future = future;
        }

    }

    private class MockOAuthManager extends OAuthManager {

        public MockOAuthManager(
                RobolectricBackgroundExecutorService executor) {
            super(null, null, executor);
        }

        public OAuthFuture<String> runTest(final Runnable mockRunnable,
                OAuthCallback<String> callback,
                Handler handler) {
            final Future2Task<String> task = new Future2Task<String>(handler, callback) {

                @Override
                public void doWork() throws Exception {
                    try {
                        if (mockRunnable != null) {
                            mockRunnable.run();
                        }
                        set("ok");
                    } finally {
                        latch.countDown();
                    }
                }

            };
            submitTaskToExecutor(task);
            return task;
        }

    }

}
