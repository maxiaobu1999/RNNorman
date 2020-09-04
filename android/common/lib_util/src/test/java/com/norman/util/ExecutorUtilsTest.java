package com.norman.util;


import org.junit.Test;

public class ExecutorUtilsTest {
    @Test
    public void getIoExecutor() {
//        Assert.assertNotNull(ExecutorUtils.getIoExecutor());
    }

//    @Test
//    public void testConcurrentGetIoExecutor() throws Exception {
//        final CountDownLatch cdl = new CountDownLatch(3);
//        final Executor[] executor1 = new Executor[1];
//        final Executor[] executor2 = new Executor[1];
//        final Executor[] executor3 = new Executor[1];
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                executor1[0] = ExecutorUtils.getIoExecutor();
//                cdl.countDown();
//            }
//        }).start();
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                executor2[0] = ExecutorUtils.getIoExecutor();
//                cdl.countDown();
//            }
//        }).start();
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                executor3[0] = ExecutorUtils.getIoExecutor();
//                cdl.countDown();
//            }
//        }).start();
//
//        cdl.await();
//        Assert.assertEquals(executor1[0], executor2[0]);
//        Assert.assertEquals(executor2[0], executor3[0]);
//    }
//
//    @Test
//    public void getComputationExecutor() {
//        Assert.assertNotNull(ExecutorUtils.getComputationExecutor());
//    }
//
//    @Test
//    public void testConcurrentGetComputationExecutor() throws Exception {
//        final CountDownLatch cdl = new CountDownLatch(3);
//        final Executor[] executor1 = new Executor[1];
//        final Executor[] executor2 = new Executor[1];
//        final Executor[] executor3 = new Executor[1];
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                executor1[0] = ExecutorUtils.getComputationExecutor();
//                cdl.countDown();
//            }
//        }).start();
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                executor2[0] = ExecutorUtils.getComputationExecutor();
//                cdl.countDown();
//            }
//        }).start();
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                executor3[0] = ExecutorUtils.getComputationExecutor();
//                cdl.countDown();
//            }
//        }).start();
//
//        cdl.await();
//        Assert.assertEquals(executor1[0], executor2[0]);
//        Assert.assertEquals(executor2[0], executor3[0]);
//    }
//
//    @Test
//    public void getSerialExecutor() {
//        Assert.assertNotNull(ExecutorUtils.getSerialExecutor());
//    }
//
//    @Test
//    public void testConcurrentGetSerialExecutor() throws Exception {
//        final CountDownLatch cdl = new CountDownLatch(3);
//        final Executor[] executor1 = new Executor[1];
//        final Executor[] executor2 = new Executor[1];
//        final Executor[] executor3 = new Executor[1];
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                executor1[0] = ExecutorUtils.getSerialExecutor();
//                cdl.countDown();
//            }
//        }).start();
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                executor2[0] = ExecutorUtils.getSerialExecutor();
//                cdl.countDown();
//            }
//        }).start();
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                executor3[0] = ExecutorUtils.getSerialExecutor();
//                cdl.countDown();
//            }
//        }).start();
//
//        cdl.await();
//        Assert.assertEquals(executor1[0], executor2[0]);
//        Assert.assertEquals(executor2[0], executor3[0]);
//    }
//
//    @Test
//    public void testIoExecutor() throws Exception {
//        final int number = 100;
//        final CountDownLatch cdl = new CountDownLatch(number);
//        final HashSet<Thread> set = new HashSet<>();
//
//        for (int i = 0; i < number; i++) {
//            System.out.println("ExecutorUtilsTest--" + Thread.currentThread().getName());
//            ExecutorUtils.getIoExecutor().execute(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    synchronized (set) {
//                        set.add(Thread.currentThread());
//                    }
//                    System.out.println("ExecutorUtilsTest+++" + Thread.currentThread().getId());
//                    cdl.countDown();
//                }
//            });
//        }
//
//        cdl.await();
//        synchronized (set) {
//            Assert.assertEquals(number, set.size());
//        }
//
//    }
//
//    @Test
//    public void testComputationExecutor() throws Exception {
//
//        int count = Runtime.getRuntime().availableProcessors();
//        final CountDownLatch cdl = new CountDownLatch(count * 2 + 1);
//        for (int i = 0; i < count * 2 + 1; i++) {
//            ExecutorUtils.getComputationExecutor().execute(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    cdl.countDown();
//                }
//            });
//        }
//
//        Assert.assertFalse(cdl.await(1000 * 2, TimeUnit.MILLISECONDS));
//    }
//
//    @Test
//    public void testSerialExecutor() throws Exception {
//        final CountDownLatch cdl = new CountDownLatch(100);
//
//        final int[] result = new int[100];
//        final int[] count = new int[]{0};
//
//        for (int i = 0; i < 100; i++) {
//            final int index = i;
//            ExecutorUtils.getSerialExecutor().execute(new Runnable() {
//                @Override
//                public void run() {
//                    result[index] = (count[0])++;
//                    cdl.countDown();
//                }
//            });
//        }
//
//        cdl.await();
//
//        for (int i = 0; i < 100; i++) {
//            Assert.assertEquals(i, result[i]);
//        }
//
//    }
//
//    @Test
//    public void postOnIO() throws Exception {
//        final CountDownLatch cdl = new CountDownLatch(1);
//        Thread current = Thread.currentThread();
//        final Thread[] runningThread = new Thread[1];
//
//        ExecutorUtils.postOnIO(new Runnable() {
//            @Override
//            public void run() {
//                runningThread[0] = Thread.currentThread();
//                cdl.countDown();
//            }
//        }, "");
//
//        cdl.await(1, TimeUnit.SECONDS);
//        Assert.assertNotSame(current, runningThread);
//
//    }
//
//    @Test
//    public void postOnComputation() throws Exception {
//
//        final CountDownLatch cdl = new CountDownLatch(1);
//        Thread current = Thread.currentThread();
//        final Thread[] runningThread = new Thread[1];
//
//        ExecutorUtils.postOnComputation(new Runnable() {
//            @Override
//            public void run() {
//                runningThread[0] = Thread.currentThread();
//                cdl.countDown();
//            }
//        }, "");
//
//        cdl.await(1, TimeUnit.SECONDS);
//        Assert.assertNotSame(current, runningThread);
//    }
//
//    @Test
//    public void postOnSerial() throws Exception {
//
//        final CountDownLatch cdl = new CountDownLatch(1);
//        Thread current = Thread.currentThread();
//        final Thread[] runningThread = new Thread[1];
//
//        ExecutorUtils.postOnSerial(new Runnable() {
//            @Override
//            public void run() {
//                runningThread[0] = Thread.currentThread();
//                cdl.countDown();
//            }
//        }, "");
//
//        cdl.await(1, TimeUnit.SECONDS);
//        Assert.assertNotSame(current, runningThread);
//    }
//
//    @Test
//    public void delayPostOnIo() throws Exception {
//        final CountDownLatch cdl = new CountDownLatch(1);
//        Thread current = Thread.currentThread();
//        final Thread[] runningThread = new Thread[1];
//
//        ExecutorUtils.delayPostOnIO(new Runnable() {
//            @Override
//            public void run() {
//                runningThread[0] = Thread.currentThread();
//                cdl.countDown();
//            }
//        }, "", 1000, TimeUnit.MILLISECONDS);
//
//        Assert.assertFalse(cdl.await(900, TimeUnit.MILLISECONDS));
//        Assert.assertNotSame(current, runningThread);
//    }
//
//    @Test
//    public void delayPostOnComputation() throws Exception {
//        final CountDownLatch cdl = new CountDownLatch(1);
//        Thread current = Thread.currentThread();
//        final Thread[] runningThread = new Thread[1];
//
//        ExecutorUtils.delayPostOnComputation(new Runnable() {
//            @Override
//            public void run() {
//                runningThread[0] = Thread.currentThread();
//                cdl.countDown();
//            }
//        }, "", 1000, TimeUnit.MILLISECONDS);
//
//        Assert.assertFalse(cdl.await(900, TimeUnit.MILLISECONDS));
//        Assert.assertNotSame(current, runningThread);
//    }
//
//    @Test
//    public void delayPostOnSerial() throws Exception {
//        final CountDownLatch cdl = new CountDownLatch(1);
//        Thread current = Thread.currentThread();
//        final Thread[] runningThread = new Thread[1];
//
//        ExecutorUtils.delayPostOnSerial(new Runnable() {
//            @Override
//            public void run() {
//                runningThread[0] = Thread.currentThread();
//                cdl.countDown();
//            }
//        }, "", 1000, TimeUnit.MILLISECONDS);
//
//        Assert.assertFalse(cdl.await(900, TimeUnit.MILLISECONDS));
//        Assert.assertNotSame(current, runningThread);
//    }
//
//    @SuppressWarnings("ConstantConditions")
//    @Test
//    public void testNull() {
//        Assert.assertNotNull(ExecutorUtils.getStandardThreadName(null));
//        ExecutorUtils.getIoExecutor().execute(null);
//        ExecutorUtils.getIoExecutor().execute(null, null);
//        ExecutorUtils.getComputationExecutor().execute(null);
//        ExecutorUtils.getComputationExecutor().execute(null, null);
//        ExecutorUtils.getSerialExecutor().execute(null);
//        ExecutorUtils.getSerialExecutor().execute(null, null);
//        ExecutorUtils.postOnComputation(null, null);
//        ExecutorUtils.postOnIO(null, null);
//        ExecutorUtils.postOnSerial(null, null);
//        ExecutorUtils.delayPostOnComputation(null, null, 0, TimeUnit.MILLISECONDS);
//        ExecutorUtils.delayPostOnIO(null, null, 0, TimeUnit.MILLISECONDS);
//        ExecutorUtils.delayPostOnSerial(null, null, 0, TimeUnit.MILLISECONDS);
//    }
//
//    @Test
//    public void testGetStandardThreadName() {
//        Assert.assertTrue(ExecutorUtils.getStandardThreadName(new String(new byte[1024])).length() <= 256);
//    }
//
//
//    @Test
//    public void testCustomPublishProcessor() {
//    }

}