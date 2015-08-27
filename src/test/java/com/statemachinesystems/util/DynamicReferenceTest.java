package com.statemachinesystems.util;

import org.junit.Test;

import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DynamicReferenceTest {

    @Test
    public void returnsInitialValue() {
        DynamicReference<Integer> n = new DynamicReference<>(1);

        assertThat(n.get(), is(1));
    }

    @Test
    public void thunkReceivesOverriddenValueInSingleThread() throws InterruptedException {
        DynamicReference<Integer> n = new DynamicReference<>(1);

        CountDownLatch latch = new CountDownLatch(1);
        n.withValue(2, () -> {
            assertThat(n.get(), is(2));
            latch.countDown();
        });

        assertThat(latch.await(1, TimeUnit.SECONDS), is(true));
        assertThat(n.get(), is(1));
    }

    @Test
    public void supplierReceivesOverriddenValue() {
        DynamicReference<Integer> n = new DynamicReference<>(1);

        int result = n.withValue(2, () -> 2 + n.get());
        assertThat(result, is(4));
    }

    @Test
    public void nestedThunksReceiveOverriddenValuesInSingleThread() throws InterruptedException {
        DynamicReference<Integer> n  = new DynamicReference<>(1);

        CountDownLatch latch = new CountDownLatch(2);
        n.withValue(2, () -> {
            assertThat(n.get(), is(2));

            n.withValue(3, () -> {
                assertThat(n.get(), is(3));
                latch.countDown();
            });

            assertThat(n.get(), is(2));
            latch.countDown();
        });

        assertThat(latch.await(1, TimeUnit.SECONDS), is(true));
        assertThat(n.get(), is(1));
    }

    @Test
    public void multipleThreadsReceiveSameInitialValue() throws InterruptedException {
        DynamicReference<Integer> n  = new DynamicReference<>(1);

        CountDownLatch latch = new CountDownLatch(6);

        Runnable task = () -> {
            assertThat(n.get(), is(1));

            n.withValue(2, () -> {
                assertThat(n.get(), is(2));

                n.withValue(3, () -> {
                    assertThat(n.get(), is(3));
                    latch.countDown();
                });

                assertThat(n.get(), is(2));
                latch.countDown();
            });

            assertThat(n.get(), is(1));
            latch.countDown();
        };

        new Thread(task).start();
        new Thread(task).start();

        assertThat(latch.await(1, TimeUnit.SECONDS), is(true));
    }

    @Test
    public void childThreadsReceiveOverriddenValueFromParentThread() throws InterruptedException {
        DynamicReference<Integer> n  = new DynamicReference<>(1);

        CountDownLatch latch = new CountDownLatch(2);

        Runnable assertNEquals2 = () -> {
            assertThat(n.get(), is(2));
            latch.countDown();
        };
        Runnable assertNEquals3 = () -> {
            assertThat(n.get(), is(3));
            latch.countDown();
        };

        Thread thread = new Thread(() -> n.withValue(2, () -> {
            n.withValue(3, () -> new Thread(assertNEquals3).start());

            new Thread(assertNEquals2).start();
        }));

        thread.start();

        assertThat(latch.await(1, TimeUnit.SECONDS), is(true));
    }

    @Test
    public void toStringMethodDelegatesToValue() {
        Object anObject = new Date();
        assertThat(new DynamicReference<>(anObject).toString(), is(anObject.toString()));
    }
}
