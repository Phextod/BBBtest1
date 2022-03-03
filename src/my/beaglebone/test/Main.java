package my.beaglebone.test;

import my.beaglebone.pins.GPIO;

public class Main {



    public static void main(String[] args) throws Exception {
        System.out.printf("Hello World %d%n", 1);

        /*HelloWorldPublisher publisher = new HelloWorldPublisher();
        publisher.publisherMain(0, 10);*/

        GPIO p = new GPIO(23, GPIO.Direction.OUT);
        for (int i = 0; i < 5; i++) {
            p.Write(1);
            Thread.sleep(1000);
            p.Write(0);
            Thread.sleep(1000);
        }


    }
}
