package my.beaglebone.pins;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static my.beaglebone.test.Main.executeCommand;

public class PWM {
    private static final HashMap<String, String> pinCodesToDirNames = new HashMap<>();

    static {
        pinCodesToDirNames.put("p9.14", "pwm-4:0");
        pinCodesToDirNames.put("p9.16", "pwm-3:0");//doesn't work
        pinCodesToDirNames.put("p9.21", "pwm-1:1");//max current around 1.33
        pinCodesToDirNames.put("p9.22", "pwm-1:0");//max current around 1.33
        pinCodesToDirNames.put("p9.42", "pwm-0:0");
        pinCodesToDirNames.put("p8.13", "pwm-7:1");
        pinCodesToDirNames.put("p8.19", "pwm-7:0");
    }

    private final static String BASEDIR="/sys/class/pwm/%s";
    private final static String ENABLE="/sys/class/pwm/%s/enable";
    private final static String PERIOD="/sys/class/pwm/%s/period";
    private final static String DUTYCYCLE="/sys/class/pwm/%s/duty_cycle";

    private final String dirName;


    public PWM(String pinCode, long period, long dutyCycle) throws FileNotFoundException {
        this.dirName = pinCodesToDirNames.get(pinCode);
        if(this.dirName == null) {
            System.err.printf("%s pin does not have pwm mode%n", pinCode);
            return;
        }

        executeCommand(String.format("config-pin %s pwm", pinCode));

        if(setPeriod(period))
            setDutyCycle(dutyCycle);
        else if(setDutyCycle(dutyCycle))
            setPeriod(period);
    }

    public boolean setPeriod(long period) throws FileNotFoundException {
        if(period < getDutyCycle()) {
            System.err.println("Period can't be smaller than duty cycle");
            return false;
        }

        File periodFile = new File(String.format(PERIOD, dirName));
        PrintStream periodFileStream = new PrintStream(periodFile);
        periodFileStream.print(period);
        periodFileStream.close();

        return period == getPeriod();
    }

    public boolean setDutyCycle(long dutyCycle) throws FileNotFoundException {
        if(dutyCycle > getPeriod()) {
            System.err.println("Duty cycle can't be greater than period");
            return false;
        }

        File dutyCycleFile = new File(String.format(DUTYCYCLE, dirName));
        PrintStream dutyCycleFileStream = new PrintStream(dutyCycleFile);
        dutyCycleFileStream.print(dutyCycle);
        dutyCycleFileStream.close();

        return dutyCycle == getDutyCycle();
    }

    public void setEnable(int enable) throws FileNotFoundException {
        File enableFile = new File(String.format(ENABLE, dirName));
        PrintStream enableFileStream = new PrintStream(enableFile);
        enableFileStream.print(enable);
        enableFileStream.close();
    }

    public long getPeriod(){
        String periodS = "0";
        try {
            FileReader periodFileReader = new FileReader(String.format(PERIOD, dirName));
            BufferedReader periodFileStream = new BufferedReader(periodFileReader);
            periodS = periodFileStream.readLine();
            periodFileStream.close();
            periodFileReader.close();
        } catch (Exception e) {//todo separate exceptions
            System.err.println("Error accessing period file");
        }
        return Long.parseLong(periodS);
    }

    public long getDutyCycle() {
        String dutyCycleS = "0";
        try {
            FileReader dutyCycleFileReader = new FileReader(String.format(DUTYCYCLE, dirName));
            BufferedReader dutyCycleFileStream = new BufferedReader(dutyCycleFileReader);
            dutyCycleS = dutyCycleFileStream.readLine();
            dutyCycleFileStream.close();
            dutyCycleFileReader.close();
        }
        catch (Exception e){//todo separate exceptions
            System.err.println("Error accessing duty_cycle file");
        }
        return Long.parseLong(dutyCycleS);
    }
}
