package in.prashanthrao.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ISelectionChangeListener {

    SampleRecyclerViewAdapter adapter;

    ArrayList<String> animalNames = new ArrayList<>();
    ArrayList<String> birdsNames = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ContentSwitcherView contentSwitcherView = findViewById(R.id.contentSwitcherView);
        contentSwitcherView.setOnSelectionChangeListeners(this);

        animalNames.add("Horse");
        animalNames.add("Cow");
        animalNames.add("Camel");
        animalNames.add("Sheep");
        animalNames.add("Goat");

        birdsNames.add("Duck");
        birdsNames.add("Eagle");
        birdsNames.add("Goose");
        birdsNames.add("Flamingo");
        birdsNames.add("Hawk");

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SampleRecyclerViewAdapter(this, animalNames);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onSelectionChange(View view, int previousSelection, int currentSelection) {
        if (currentSelection == ContentSwitcherView.LEFT) {
            adapter.setData(animalNames);
        } else if (currentSelection == ContentSwitcherView.RIGHT) {
            adapter.setData(birdsNames);
        }
    }
}
