package com.norman.util;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.annotations.CheckReturnValue;
import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;
import io.reactivex.exceptions.MissingBackpressureException;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import io.reactivex.internal.util.BackpressureHelper;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.processors.FlowableProcessor;

/** PublishProcessor不能继承，炒了一遍 */
public class CustomPublishProcessor<T> extends FlowableProcessor<T> {
    /** The terminated indicator for the subscribers array. */
    @SuppressWarnings("rawtypes")
    static final CustomPublishProcessor.PublishSubscription[] TERMINATED = new CustomPublishProcessor.PublishSubscription[0];
    /** An empty subscribers array to avoid allocating it all the time. */
    @SuppressWarnings("rawtypes")
    static final CustomPublishProcessor.PublishSubscription[] EMPTY = new CustomPublishProcessor.PublishSubscription[0];

    /** The array of currently subscribed subscribers. */
    final AtomicReference<PublishSubscription<T>[]> subscribers;

    /** The error, write before terminating and read after checking subscribers. */
    Throwable error;

    /**
     * Constructs a CustomPublishProcessor.
     *
     * @param <T> the value type
     * @return the new CustomPublishProcessor
     */
    @CheckReturnValue
    @NonNull
    public static <T> CustomPublishProcessor<T> create() {
        return new CustomPublishProcessor<T>();
    }

    /**
     * Constructs a CustomPublishProcessor.
     *
     * @since 2.0
     */
    @SuppressWarnings("unchecked")
    CustomPublishProcessor() {
        subscribers = new AtomicReference<CustomPublishProcessor.PublishSubscription<T>[]>(EMPTY);
    }

    @Override
    protected void subscribeActual(Subscriber<? super T> t) {
        CustomPublishProcessor.PublishSubscription<T> ps = new CustomPublishProcessor.PublishSubscription<T>(t, this);
        t.onSubscribe(ps);
        if (add(ps)) {
            // if cancellation happened while a successful add, the remove() didn't work
            // so we need to do it again
            if (ps.isCancelled()) {
                remove(ps);
            }
        } else {
            Throwable ex = error;
            if (ex != null) {
                t.onError(ex);
            } else {
                t.onComplete();
            }
        }
    }

    /**
     * Tries to add the given subscriber to the subscribers array atomically
     * or returns false if this processor has terminated.
     *
     * @param ps the subscriber to add
     * @return true if successful, false if this processor has terminated
     */
    boolean add(CustomPublishProcessor.PublishSubscription<T> ps) {
        for (; ; ) {
            CustomPublishProcessor.PublishSubscription<T>[] a = subscribers.get();
            if (a == TERMINATED) {
                return false;
            }

            int n = a.length;
            @SuppressWarnings("unchecked")
            CustomPublishProcessor.PublishSubscription<T>[] b = new CustomPublishProcessor.PublishSubscription[n + 1];
            System.arraycopy(a, 0, b, 0, n);
            b[n] = ps;

            if (subscribers.compareAndSet(a, b)) {
                return true;
            }
        }
    }

    /**
     * Atomically removes the given subscriber if it is subscribed to this processor.
     *
     * @param ps the subscription wrapping a subscriber to remove
     */
    @SuppressWarnings("unchecked")
    void remove(CustomPublishProcessor.PublishSubscription<T> ps) {
        for (; ; ) {
            CustomPublishProcessor.PublishSubscription<T>[] a = subscribers.get();
            if (a == TERMINATED || a == EMPTY) {
                return;
            }

            int n = a.length;
            int j = -1;
            for (int i = 0; i < n; i++) {
                if (a[i] == ps) {
                    j = i;
                    break;
                }
            }

            if (j < 0) {
                return;
            }

            CustomPublishProcessor.PublishSubscription<T>[] b;

            if (n == 1) {
                b = EMPTY;
            } else {
                b = new CustomPublishProcessor.PublishSubscription[n - 1];
                System.arraycopy(a, 0, b, 0, j);
                System.arraycopy(a, j + 1, b, j, n - j - 1);
            }
            if (subscribers.compareAndSet(a, b)) {
                return;
            }
        }
    }

    @Override
    public void onSubscribe(Subscription s) {
        if (subscribers.get() == TERMINATED) {
            s.cancel();
            return;
        }
        // CustomPublishProcessor doesn't bother with request coordination.
        s.request(Long.MAX_VALUE);
    }

    @Override
    public void onNext(T t) {
        ObjectHelper.requireNonNull(t, "onNext called with null. Null values are generally not allowed in 2.x operators and sources.");
        for (CustomPublishProcessor.PublishSubscription<T> s : subscribers.get()) {
            s.onNext(t);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onError(Throwable t) {
        ObjectHelper.requireNonNull(t, "onError called with null. Null values are generally not allowed in 2.x operators and sources.");
        if (subscribers.get() == TERMINATED) {
            RxJavaPlugins.onError(t);
            return;
        }
        error = t;

        for (CustomPublishProcessor.PublishSubscription<T> s : subscribers.getAndSet(TERMINATED)) {
            s.onError(t);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onComplete() {
        if (subscribers.get() == TERMINATED) {
            return;
        }
        for (CustomPublishProcessor.PublishSubscription<T> s : subscribers.getAndSet(TERMINATED)) {
            s.onComplete();
        }
    }

    /**
     * Tries to emit the item to all currently subscribed Subscribers if all of them
     * has requested some value, returns false otherwise.
     * <p>
     * This method should be called in a sequential manner just like the onXXX methods
     * of the CustomPublishProcessor.
     * <p>
     * Calling with null will terminate the CustomPublishProcessor and a NullPointerException
     * is signalled to the Subscribers.
     * <p>History: 2.0.8 - experimental
     *
     * @param t the item to emit, not null
     * @return true if the item was emitted to all Subscribers
     * @since 2.2
     */
    public boolean offer(T t) {
        if (t == null) {
            onError(new NullPointerException("onNext called with null. Null values are generally not allowed in 2.x operators and sources."));
            return true;
        }
        CustomPublishProcessor.PublishSubscription<T>[] array = subscribers.get();

        for (CustomPublishProcessor.PublishSubscription<T> s : array) {
            if (s.isFull()) {
                return false;
            }
        }

        for (CustomPublishProcessor.PublishSubscription<T> s : array) {
            s.onNext(t);
        }
        return true;
    }

    @Override
    public boolean hasSubscribers() {
        return subscribers.get().length != 0;
    }

    @Override
    @Nullable
    public Throwable getThrowable() {
        if (subscribers.get() == TERMINATED) {
            return error;
        }
        return null;
    }

    @Override
    public boolean hasThrowable() {
        return subscribers.get() == TERMINATED && error != null;
    }

    @Override
    public boolean hasComplete() {
        return subscribers.get() == TERMINATED && error == null;
    }

    /**
     * Wraps the actual subscriber, tracks its requests and makes cancellation
     * to remove itself from the current subscribers array.
     *
     * @param <T> the value type
     */
    static final class PublishSubscription<T> extends AtomicLong implements Subscription {

        private static final long serialVersionUID = 3562861878281475070L;
        /** The actual subscriber. */
        final Subscriber<? super T> downstream;
        /** The parent processor servicing this subscriber. */
        final CustomPublishProcessor<T> parent;

        /**
         * Constructs a PublishSubscriber, wraps the actual subscriber and the state.
         *
         * @param actual the actual subscriber
         * @param parent the parent CustomPublishProcessor
         */
        PublishSubscription(Subscriber<? super T> actual, CustomPublishProcessor<T> parent) {
            this.downstream = actual;
            this.parent = parent;
        }

        public void onNext(T t) {
            long r = get();
            if (r == Long.MIN_VALUE) {
                return;
            }
            if (r != 0L) {
                downstream.onNext(t);
                BackpressureHelper.producedCancel(this, 1);
            } else {
                cancel();
                downstream.onError(new MissingBackpressureException("Could not emit value due to lack of requests"));
            }
        }

        public void onError(Throwable t) {
            if (get() != Long.MIN_VALUE) {
                downstream.onError(t);
            } else {
                RxJavaPlugins.onError(t);
            }
        }

        public void onComplete() {
            if (get() != Long.MIN_VALUE) {
                downstream.onComplete();
            }
        }

        @Override
        public void request(long n) {
            if (SubscriptionHelper.validate(n)) {
                BackpressureHelper.addCancel(this, n);
            }
        }

        @Override
        public void cancel() {
            if (getAndSet(Long.MIN_VALUE) != Long.MIN_VALUE) {
                parent.remove(this);
            }
        }

        public boolean isCancelled() {
            return get() == Long.MIN_VALUE;
        }

        boolean isFull() {
            return get() == 0L;
        }
    }
}
