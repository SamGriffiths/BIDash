package sample;

/**
 * Created by Jamie on 06/02/2016.
 */
public class SalesData {

    private int Year;
    private byte QTR;
    private String Region;
    private String Vehicle;
    private int Quantity;

    public void setYear(int year){
        this.Year = year;
    }

    public int getYear(){
        return this.Year;
    }

    public void setQTR(byte quarter){
        this.QTR = quarter;
    }

    public byte getQTR(){
        return this.QTR;
    }

    public void setRegion(String region){
        this.Region = region;
    }

    public String getRegion(){
        return this.Region;
    }

    public void setVehicle(String vehicle){
        this.Vehicle = vehicle;
    }

    public String getVehicle(){
        return this.Vehicle;
    }

    public void setQuantity(int sales){
        this.Quantity = sales;
    }

    public int getQuantity(){
        return this.Quantity;
    }
}
