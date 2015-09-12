package com.example.reico_000.prescriptionreadernfc;

/**
 * Created by reico_000 on 26/2/2015.
 */
public interface Communicator {

    public void respondSaveMedication();
    public void respondReset();
    public void displayToastForId(long idInDB);
    public void deleteId(long idInDB);
    public void respondConsumeMed();

}
