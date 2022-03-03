package my.beaglebone.test;

import java.io.*;
import java.lang.*;

public class LinuxGPIO {
    public enum Direction {IN, OUT, HIGH, LOW}
    private static final String EXPORT = "/sys/class/gpio/export";
    private static final String UNEXPORT = "/sys/class/gpio/unexport";
    private static final String DIRECTIONFMT = "/sys/class/gpio/gpio%d/direction";
    private static final String VALUEFMT = "/sys/class/gpio/gpio%d/value";
    private final int pinNumber_;
    private Direction direction_;

    public LinuxGPIO(int pinNumber, Direction direction) throws Exception {
        pinNumber_ = pinNumber;
        direction_ = direction;

        executeCommand(String.format("echo %d > %s%n", pinNumber_, EXPORT));

        File dirFile = new File(String.format(DIRECTIONFMT,pinNumber_));
        PrintStream dirFp = new PrintStream(dirFile);
        switch (direction_) {
            case IN:
                dirFp.print("in");
                break;
            case OUT:
                dirFp.print("out");
                break;
            case HIGH:
                dirFp.print("high");
                break;
            case LOW:
                dirFp.print("low");
                break;
        }
        dirFp.close();
    }

    public int Read()  throws Exception {
        FileInputStream in = new FileInputStream(String.format(VALUEFMT,pinNumber_));
        BufferedReader d = new BufferedReader(new InputStreamReader(in));
        return( Integer.parseInt(d.readLine()) );
    }

    public void Write(int v)  throws Exception {
        PrintStream out = new PrintStream(String.format(VALUEFMT,pinNumber_));
        out.printf("%d\n",v);
        out.close();
    }

    public Direction GetDirection()  throws Exception {
        FileInputStream in = new FileInputStream(String.format(DIRECTIONFMT,pinNumber_));
        BufferedReader d = new BufferedReader(new InputStreamReader(in));
        String dir = d.readLine();
        switch (dir) {
            case "in":
                direction_ = Direction.IN;
            case "out":
                direction_ = Direction.OUT;
            case "high":
                direction_ = Direction.HIGH;
            case "low":
                direction_ = Direction.LOW;
        }
        direction_ = Direction.IN;
        return direction_;
    }

    public static boolean executeCommand(String command) {

        // Try running the command.
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


