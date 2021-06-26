package lesson5;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.Lock;

public class Car implements Runnable {

    private static int CARS_COUNT;
    private Race race;
    private int speed;
    private String name;
    private CountDownLatch latch;
    private CyclicBarrier cyclicBarrier;
    private CountDownLatch latch1;
    private Lock lock;

    public String getName() {
        return name;
    }

    public int getSpeed() {
        return speed;
    }

    public Car(Race race, int speed, CountDownLatch latch, CyclicBarrier cyclicBarrier, CountDownLatch latch1, Lock lock) {
        this.race = race;
        this.speed = speed;
        CARS_COUNT++;
        this.name = "Участник #" + CARS_COUNT;
        this.latch = latch;
        this.cyclicBarrier = cyclicBarrier;
        this.latch1 = latch1;
        this.lock = lock;
    }

    @Override
    public void run() {
        try {
            System.out.println(this.name + " готовится");
            Thread.sleep(500 + (int)(Math.random() * 800));
            latch.countDown();
            System.out.println(this.name + " готов");
            Thread.sleep(500 + (int)(Math.random() * 800));
            cyclicBarrier.await(); // сюда ожидание всех машин cyclicBarrier
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0; i < race.getStages().size(); i++) {
            race.getStages().get(i).go(this);
            lock.tryLock(); // определение победителя
        }
        if (lock.tryLock() == true){
            System.out.println("Победил " + this.name);
        }
        latch1.countDown();
    }
}
