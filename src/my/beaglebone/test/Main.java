package my.beaglebone.test;

import my.beaglebone.pins.PWM;

public class Main {



    public static void main(String[] args) throws Exception {
        System.out.printf("Hello World %d%n", 1);

        /*HelloWorldPublisher publisher = new HelloWorldPublisher();
        publisher.publisherMain(0, 10);*/

        /*GPIO p = new GPIO(23, GPIO.Direction.OUT);
        for (int i = 0; i < 5; i++) {
            p.Write(1);
            Thread.sleep(1000);
            p.Write(0);
            Thread.sleep(1000);
        }*/
        PWM p = new PWM("p9.21", 200000, 200000);
        for (int i = 0; i < 2; i++) {
            p.setEnable(1);
            Thread.sleep(3000);
            p.setEnable(0);
            Thread.sleep(3000);
        }
    }

    public static boolean executeCommand(String command) {
        try {
            Process proc = Runtime.getRuntime().exec(command);
            proc.waitFor();
        }
        catch(Exception e) {
            return false;
        }
        return true;
    }
}
