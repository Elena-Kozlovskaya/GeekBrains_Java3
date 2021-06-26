package lesson5;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class MainClass {
    public static final int CARS_COUNT = 4;

    public static void main(String[] args) throws InterruptedException {

        System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Подготовка!!!");

        Race race = new Race(new Road(60), new Tunnel(), new Road(40));
        CountDownLatch latch = new CountDownLatch(CARS_COUNT);
        CountDownLatch latch1 = new CountDownLatch(CARS_COUNT);
        CyclicBarrier cyclicBarrier = new CyclicBarrier(CARS_COUNT);
        Lock lock = new ReentrantLock();

        Car[] cars = new Car[CARS_COUNT];
        for (int i = 0; i < cars.length; i++) {
            cars[i] = new Car(race, 20 + (int) (Math.random() * 10), latch, cyclicBarrier, latch1, lock);
        }

        for (int i = 0; i < cars.length; i++) {
            new Thread(cars[i]).start();
        }

        latch.await();
        System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Гонка началась!!!");
        latch1.await();
        System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Гонка закончилась!!!");
    }

}








