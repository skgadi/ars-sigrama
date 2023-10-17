package mx.com.sigrama.ars.device;

import java.util.Date;

public class PhasorData {
    Date dateTime;
    double[] voltage;
    double[] current;
    double frequency;
    double[] phase;
    double[] power;
    double[] powerFactor;
    double[] impedance;
    double[] admittance;
    double[] apparentPower;
    double[] reactivePower;
    double[] activePower;

    public PhasorData (Date dateTime, double[] voltage, double[] current, double frequency, double[] phase, double[] power, double[] powerFactor, double[] impedance, double[] admittance, double[] apparentPower, double[] reactivePower, double[] activePower){
        this.dateTime = dateTime;
        this.voltage = voltage;
        this.current = current;
        this.frequency = frequency;
        this.phase = phase;
        this.power = power;
        this.powerFactor = powerFactor;
        this.impedance = impedance;
        this.admittance = admittance;
        this.apparentPower = apparentPower;
        this.reactivePower = reactivePower;
        this.activePower = activePower;
    }


}
