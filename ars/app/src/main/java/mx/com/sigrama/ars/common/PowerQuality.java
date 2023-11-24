package mx.com.sigrama.ars.common;

/**
 * Created by SKGadi on 24/11/2023.
 * This class is used to store the power quality data
 */
public class PowerQuality {
    int NO_OF_PHASES;
    double voltageRMS[][];
    double currentRMS[][];
    double cosPhi[][];
    double activePower[][];
    double reactivePower[][];
    double apparentPower[][];

    double powerFactorPerPhase[];
    double activePowerPerPhase[];
    double reactivePowerPerPhase[];
    double apparentPowerPerPhase[];
    double totalActivePower;
    double totalReactivePower;
    double totalApparentPower;
    double totalPowerFactor;
    boolean isDataValid;

    public PowerQuality(int NO_OF_PHASES) {
        this.NO_OF_PHASES = NO_OF_PHASES;
        voltageRMS = new double[NO_OF_PHASES][];
        currentRMS = new double[NO_OF_PHASES][];
        cosPhi = new double[NO_OF_PHASES][];
        isDataValid = false;
    }

    public void setVoltageRMS(int phase, double[] voltageRMS) {
        this.voltageRMS[phase] = voltageRMS;
    }

    public void setCurrentRMS(int phase, double[] currentRMS) {
        this.currentRMS[phase] = currentRMS;
    }

    public void setPowerFactor(int phase, double[] powerFactor) {
        this.cosPhi[phase] = powerFactor;
    }

    public int getNO_OF_PHASES() {
        return NO_OF_PHASES;
    }

    /**
     * This method calculates the following power quality parameters
     * Takes no arguments and updates the following parameters
     * 1. activePower
     * 2. reactivePower
     * 3. apparentPower
     * 4. powerFactorPerPhase
     * 5. activePowerPerPhase
     * 6. reactivePowerPerPhase
     * 7. apparentPowerPerPhase
     * 8. totalActivePower
     * 9. totalReactivePower
     * 10. totalApparentPower
     * 11. totalPowerFactor
     */
    public void calculatePowerQualityParameters() {
        activePower = new double[NO_OF_PHASES][];
        reactivePower = new double[NO_OF_PHASES][];
        apparentPower = new double[NO_OF_PHASES][];
        powerFactorPerPhase = new double[NO_OF_PHASES];
        activePowerPerPhase = new double[NO_OF_PHASES];
        reactivePowerPerPhase = new double[NO_OF_PHASES];
        apparentPowerPerPhase = new double[NO_OF_PHASES];
        totalApparentPower = 0;
        totalActivePower = 0;
        totalReactivePower = 0;
        totalPowerFactor = 0;

        for (int i = 0; i < NO_OF_PHASES; i++) {
            apparentPower[i] = new double[voltageRMS[i].length];
            activePower[i] = new double[voltageRMS[i].length];
            reactivePower[i] = new double[voltageRMS[i].length];
            for (int j = 0; j < voltageRMS[i].length; j++) {
                apparentPower[i][j] = voltageRMS[i][j] * currentRMS[i][j];
                activePower[i][j] = apparentPower[i][j] * cosPhi[i][j];
                reactivePower[i][j] = apparentPower[i][j] * Math.sqrt(1 - cosPhi[i][j] * cosPhi[i][j]);
                apparentPowerPerPhase[i] += apparentPower[i][j];
                activePowerPerPhase[i] += activePower[i][j];
                reactivePowerPerPhase[i] += reactivePower[i][j];
            }
            powerFactorPerPhase[i] = activePowerPerPhase[i] / apparentPowerPerPhase[i];
            totalApparentPower += apparentPowerPerPhase[i];
            totalActivePower += activePowerPerPhase[i];
            totalReactivePower += reactivePowerPerPhase[i];
        }
        totalPowerFactor = totalActivePower / totalApparentPower;
        isDataValid = true;
    }

    public double getActivePower() {
        return totalActivePower;
    }

    public double getReactivePower() {
        return totalReactivePower;
    }

    public double getApparentPower() {
        return totalApparentPower;
    }

    public double getPowerFactor() {
        return totalPowerFactor;
    }

    /**
     * This method returns the active power of a particular phase
     * @param phase
     * @return
     * Returns the active power of the phase
     */
    public double getActivePower(int phase) {
        if (!isDataValid) return 0;
        return activePowerPerPhase[phase];
    }

    /**
     * This method returns the reactive power of a particular phase
     * @param phase
     * @return
     * Returns the reactive power of the phase
     */
    public double getReactivePower(int phase) {
        if (!isDataValid) return 0;
        return reactivePowerPerPhase[phase];
    }

    /**
     * This method returns the apparent power of a particular phase
     * @param phase
     * @return
     * Returns the apparent power of the phase
     */
    public double getApparentPower(int phase) {
        if (!isDataValid) return 0;
        return apparentPowerPerPhase[phase];
    }

    /**
     * This method returns the power factor of a particular phase
     * @param phase
     * @return
     * Returns the power factor of the phase
     */
    public double getPowerFactor(int phase) {
        if (!isDataValid) return 0;
        return powerFactorPerPhase[phase];
    }

    /**
     * This method returns the active power of a particular phase in a particular harmonic
     * @param phase
     * @param harmonic
     * @return
     * Returns the active power of the phase in the harmonic
     */
    public double getActivePower(int phase, int harmonic) {
        if (!isDataValid) return 0;
        return activePower[phase][harmonic];
    }

    /**
     * This method returns the reactive power of a particular phase in a particular harmonic
     * @param phase
     * @param harmonic
     * @return
     * Returns the reactive power of the phase in the harmonic
     */
    public double getReactivePower(int phase, int harmonic) {
        if (!isDataValid) return 0;
        return reactivePower[phase][harmonic];
    }

    /**
     * This method returns the apparent power of a particular phase in a particular harmonic
     * @param phase
     * @param harmonic
     * @return
     * Returns the apparent power of the phase in the harmonic
     */
    public double getApparentPower(int phase, int harmonic) {
        if (!isDataValid) return 0;
        return apparentPower[phase][harmonic];
    }

    /**
     * This method returns the power factor of a particular phase in a particular harmonic
     * @param phase
     * @param harmonic
     * @return
     * Returns the power factor of the phase in the harmonic
     */
    public double getPowerFactor(int phase, int harmonic) {
        if (!isDataValid) return 0;
        return cosPhi[phase][harmonic];
    }

    /**
     * This method returns total number of harmonics
     * @return
     * Returns total number of harmonics
     */
    public int getNO_OF_HARMONICS() {
        if (!isDataValid) return 0;
        return voltageRMS[0].length;
    }
    public boolean isDataValid() {
        return isDataValid;
    }
}
