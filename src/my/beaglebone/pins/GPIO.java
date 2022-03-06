package my.beaglebone.pins;

import java.io.*;
import java.lang.*;
import java.util.HashMap;

import static my.beaglebone.Main.executeCommand;

public class GPIO {
    private static final HashMap<String, Integer> pinCodesToGPIONumbers = new HashMap<>();

    static{
        final String fileName = "classes/GPIO.txt";
        String line;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":", 2);
                if (parts.length >= 2 && parts[0].charAt(0) != '#') {
                    String key = parts[0];
                    Integer value = Integer.parseInt(parts[1]);
                    pinCodesToGPIONumbers.put(key, value);
                }
            }
        } catch (FileNotFoundException e){
            System.err.printf("Can't init GPIO class(%s file not found)%n", fileName);
        } catch (IOException e){
            System.err.printf("Can't init GPIO class(can't read %s file)%n", fileName);
        }
    }

    public enum Direction {IN, OUT, HIGH, LOW}
    private static final String EXPORT = "/sys/class/gpio/export";
    private static final String UNEXPORT = "/sys/class/gpio/unexport";
    private static final String DIRECTIONFMT = "/sys/class/gpio/gpio%d/direction";
    private static final String VALUEFMT = "/sys/class/gpio/gpio%d/value";
    private final Integer pinNumber_;
    private Direction direction_;

    public GPIO(String pinCode, Direction direction) throws Exception {
        pinNumber_ = pinCodesToGPIONumbers.get(pinCode);
        if(pinNumber_ == null) {
            System.err.printf("pin %s does not have pwm mode%n", pinCode);
            return;
        }
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
        if(pinNumber_ == null) return 0;
        FileInputStream in = new FileInputStream(String.format(VALUEFMT,pinNumber_));
        BufferedReader d = new BufferedReader(new InputStreamReader(in));
        return( Integer.parseInt(d.readLine()) );
    }

    public void Write(int v)  throws Exception {
        if(pinNumber_ == null) return;
        PrintStream out = new PrintStream(String.format(VALUEFMT,pinNumber_));
        out.printf("%d\n",v);
        out.close();
    }

    public Direction GetDirection()  throws Exception {
        if(pinNumber_ == null) return Direction.IN;
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
}


