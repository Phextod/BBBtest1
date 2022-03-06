package my.beaglebone.pins;

import java.io.*;
import java.util.HashMap;

import static my.beaglebone.Main.executeCommand;

//PWM pins can only output, for reading use ANALOG
public class PWM {
    private static final HashMap<String, String> pinCodesToDirNames = new HashMap<>();

    static {
        final String fileName = "classes/PWM.txt";
        String line;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":", 2);
                if (parts.length >= 2 && parts[0].charAt(0) != '#') {
                    String key = parts[0];
                    String value = parts[1];
                    pinCodesToDirNames.put(key, value);
                }
            }
        } catch (FileNotFoundException e){
            System.err.printf("Can't init PWM class(%s file not found)%n", fileName);
        } catch (IOException e){
            System.err.printf("Can't init PWM class(can't read %s file)%n", fileName);
        }
    }

    private final static String ENABLE="/sys/class/pwm/%s/enable";
    private final static String PERIOD="/sys/class/pwm/%s/period";
    private final static String DUTY_CYCLE ="/sys/class/pwm/%s/duty_cycle";

    private final String dirName;


    public PWM(String pinCode, long period, long dutyCycle) throws FileNotFoundException {
        dirName = pinCodesToDirNames.get(pinCode);
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

        File dutyCycleFile = new File(String.format(DUTY_CYCLE, dirName));
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
            BufferedReader periodBufferedReader = new BufferedReader(periodFileReader);
            periodS = periodBufferedReader.readLine();
            periodBufferedReader.close();
            periodFileReader.close();
        } catch (Exception e) {//should separate exceptions
            System.err.println("Error while getting period");
        }
        return Long.parseLong(periodS);
    }

    public long getDutyCycle() {
        String dutyCycleS = "0";
        try {
            FileReader dutyCycleFileReader = new FileReader(String.format(DUTY_CYCLE, dirName));
            BufferedReader dutyCycleBufferedReader = new BufferedReader(dutyCycleFileReader);
            dutyCycleS = dutyCycleBufferedReader.readLine();
            dutyCycleBufferedReader.close();
            dutyCycleFileReader.close();
        }
        catch (Exception e){//should separate exceptions
            System.err.println("Error while getting duty cycle");
        }
        return Long.parseLong(dutyCycleS);
    }

    public boolean isEnabled() {
        String enabledS = "0";
        try {
            FileReader enableFileReader = new FileReader(String.format(ENABLE, dirName));
            BufferedReader enableBufferedReader = new BufferedReader(enableFileReader);
            enabledS = enableBufferedReader.readLine();
            enableBufferedReader.close();
            enableFileReader.close();
        } catch (IOException e) {//should separate exceptions
            System.err.println("Error while getting enabled state");
        }
        return enabledS.equals("1");
    }
}
