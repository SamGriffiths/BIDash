package sample;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.lang.reflect.Type;
import java.util.LinkedList;

/**
 * Created by Jamie on 06/02/2016.
 */
public class SalesDataParser {

    public ObservableList<SalesData> parseJSONData(String json) {
        Type listType = new TypeToken<LinkedList<SalesData>>() {}.getType();
        LinkedList<SalesData> dataList = new Gson().fromJson(json, listType);

        return FXCollections.observableArrayList(dataList);
    }
}
