package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Region;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Controller implements Initializable {

    @FXML
    private ProgressIndicator myProgressIndicator;

    @FXML
    private TableView dataTable;

    @FXML
    private Region veil;

    @FXML
    private LineChart lineChart;

    @FXML
    private BarChart barChart;

    @FXML
    private CheckBox autoUpdateCheck;

    @FXML
    private Label lastUpdatedLabel;

    private final SalesService salesService = new SalesService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
                salesService.stateProperty().addListener(new ChangeListener<Worker.State>() {
            @Override public void changed(ObservableValue<? extends Worker.State> observableValue, Worker.State oldState, Worker.State newState) {
                System.out.println(newState.toString());
                switch (newState) {
                    case SCHEDULED:
                        break;
                    case RUNNING:
                        veil.setVisible(true);
                        veil.setStyle("-fx-background-color: rgba(0, 0, 0, 0.25)");
                        veil.setOpacity(0.8);
                        myProgressIndicator.setVisible(true);
                        myProgressIndicator.progressProperty().bind(salesService.progressProperty());
                        break;
                    case READY:
                    case SUCCEEDED:
                    case CANCELLED:
                    case FAILED:
                        myProgressIndicator.progressProperty().unbind();
                        myProgressIndicator.setVisible(false);
                        veil.setVisible(false);
                        setLastUpdated();
                        break;
                }
            }
        });

        salesService.setOnSucceeded(event -> {
            ObservableList<SalesData> data = salesService.getValue();
            bindTable(data);
            buildGraph(data);
        });


        TableColumn<SalesData,Integer> yearCol = new TableColumn<>();
        yearCol.setText("Year");
        yearCol.setCellValueFactory(new PropertyValueFactory<SalesData, Integer>("Year"));
        TableColumn<SalesData,Integer> qtrCol = new TableColumn<>();
        qtrCol.setText("Quarter");
        qtrCol.setCellValueFactory(new PropertyValueFactory<SalesData, Integer>("QTR"));
        TableColumn modelCol = new TableColumn();
        modelCol.setText("Model");
        modelCol.setCellValueFactory(new PropertyValueFactory("Vehicle"));
        TableColumn regionCol = new TableColumn();
        regionCol.setText("Region");
        regionCol.setCellValueFactory(new PropertyValueFactory("Region"));
        TableColumn<SalesData,Integer> salesCol = new TableColumn<>();
        salesCol.setText("Sales");
        salesCol.setCellValueFactory(new PropertyValueFactory<SalesData, Integer>("Quantity"));

        dataTable.getColumns().addAll(yearCol,qtrCol,modelCol,regionCol,salesCol);
        //dataTable.itemsProperty().bind(salesService.valueProperty());

        myProgressIndicator.progressProperty().bind(salesService.progressProperty());

        if(!salesService.isRunning()){
            salesService.reset();
            salesService.start();
        }
    }


    public static <T> Predicate<T> distinctByKey(Function<? super T,Object> keyExtractor) {
        Map<Object,Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    private void buildGraph(ObservableList<SalesData> data) {
        barChart.getData().clear();
        List<SalesData> years = data.stream().filter(distinctByKey(SalesData::getYear)).collect(Collectors.toList());

        for(SalesData sd : years){
            XYChart.Series series = new XYChart.Series();
            series.setName(Integer.toString(sd.getYear()));
            data.stream().filter(o -> o.getYear() == sd.getYear()).forEach(o -> series.getData().add(new XYChart.Data<>(o.getVehicle(), o.getQuantity())));
        }

    }


    private void bindTable(ObservableList<SalesData> data) {
        dataTable.getItems().clear();
        dataTable.getItems().addAll(data);
    }

    private void setLastUpdated() {
        lastUpdatedLabel.setText(String.format("Last Updated: %s", new SimpleDateFormat("dd/MM/yyyy - hh:mm:ss").format(new Date())));
    }

    public void onAutoUpdateCheckChanged(ActionEvent actionEvent) {
        if(autoUpdateCheck.isSelected()){
            if(!salesService.isRunning()){
                salesService.reset();
                salesService.start();
            }
        }else{
            salesService.cancel();
        }
    }
}
