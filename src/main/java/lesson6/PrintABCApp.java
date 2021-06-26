package lesson6;

public class PrintABCApp {
    private final Object monitor = new Object();
    private volatile char currentLetter = 'A';

    public static void main(String[] args) {
        PrintABCApp printABC = new PrintABCApp();
        Thread threadA = new Thread(() -> {
            printABC.printA();
        });
        Thread threadB = new Thread(printABC::printB);
        Thread threadC = new Thread(printABC::printC);

        threadA.start();
        threadB.start();
        threadC.start();

    }

    public void printA() {
        synchronized (monitor) {
            try {

                for (int i = 0; i < 5; i++) {
                    while (currentLetter != 'A') {
                        monitor.wait();
                    }
                    System.out.print("A");
                    currentLetter = 'B';
                    monitor.notifyAll();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void printB() {
        synchronized (monitor) {
            try {

                for (int i = 0; i < 5; i++) {
                    while (currentLetter != 'B') {
                        monitor.wait();
                    }
                    System.out.print("B");
                    currentLetter = 'C';
                    monitor.notifyAll();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void printC() {
        synchronized (monitor) {
            try {

                for (int i = 0; i < 5; i++) {
                    while (currentLetter != 'C') {
                        monitor.wait();
                    }
                    System.out.print("C");
                    currentLetter = 'A';
                    monitor.notifyAll();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
