package sample;

import javafx.collections.ObservableList;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.util.Duration;

/**
 * Created by Jamie on 06/02/2016.
 */
public class SalesService extends ScheduledService<ObservableList<SalesData>> {

    private static final Duration DELAY = new Duration(30000);


    public SalesService(){
        this.setPeriod(DELAY);
    }


    @Override
    protected Task<ObservableList<SalesData>> createTask() {
        return new SalesTask();
    }
}
