package com.example.todo;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Spinner mySpinner; // Dropdown menu (Sorting items)
    private RecyclerView view; // RecyclerView
    private ArrayAdapter<String> myAdapter; // Spinner
    private ImageView addBtn; // Adding Item
    private ImageView refreshBtn;
    private ArrayList<CardViewItemDTO> items; // ArrayList of all Items
    private MyRecyclerViewAdapter adapter; // Adapter
    private SharedPreferences sharedPreferences; // For Saving Data

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);

        view = (RecyclerView)findViewById(R.id.list_section);
        view.setLayoutManager(new LinearLayoutManager(this));
        view.setOnClickListener(this);

        // Spinner
        myAdapter = new ArrayAdapter<>(MainActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.names));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner = (Spinner) findViewById(R.id.mainSpinner);
        mySpinner.setAdapter(myAdapter);

        adapter = new MyRecyclerViewAdapter(items, MainActivity.this);
        view.setAdapter(adapter);

        // List of items
        Bundle extras = getIntent().getExtras();

        loadData();
        if (extras != null) {
            String newTitle = extras.getString("title");
            float newImportance = extras.getFloat("importance");
            String newDetails = extras.getString("details");
            String newStartDate = extras.getString("startDate");
            String newEndDate = extras.getString("endDate");
            boolean newDone = extras.getBoolean("done");
            items.add(new CardViewItemDTO(newTitle, newImportance, newDetails, newStartDate, newEndDate, newDone));
            adapter.setCardViewItemDTOs(items);
            saveData();
        }

        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch(i) {
                    case 1: // End Date Latest
                        Collections.sort(items, new Comparator<CardViewItemDTO>() {
                            @Override
                            public int compare(CardViewItemDTO cardViewItemDTO, CardViewItemDTO t1) {
                                boolean firstDateEmpty = false;
                                Date date1 = null;
                                Date date2 = null;
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                try {
                                    date1 = sdf.parse(cardViewItemDTO.dateEnd);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                    firstDateEmpty = true;
                                }
                                try {
                                    date2 = sdf.parse(t1.dateEnd);
                                    if (firstDateEmpty) return -1;
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                    if (firstDateEmpty) return cardViewItemDTO.task.compareTo(t1.task);
                                    else return 1;
                                }
                                return date2.compareTo(date1);
                            }
                        });
                        break;
                    case 2: // Date Added Earliest
                        Collections.sort(items, new Comparator<CardViewItemDTO>() {
                            @Override
                            public int compare(CardViewItemDTO cardViewItemDTO, CardViewItemDTO t1) {
                                Date date1 = null;
                                Date date2 = null;
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                try {
                                    date1 = sdf.parse(cardViewItemDTO.dateAdded);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    date2 = sdf.parse(t1.dateAdded);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                return date1.compareTo(date2) == 0 ? cardViewItemDTO.task.compareTo(t1.task) : date1.compareTo(date2);
                            }
                        });
                        break;
                    case 3: // reverse
                        Collections.sort(items, new Comparator<CardViewItemDTO>() {
                            @Override
                            public int compare(CardViewItemDTO cardViewItemDTO, CardViewItemDTO t1) {
                                Date date1 = null;
                                Date date2 = null;
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                try {
                                    date1 = sdf.parse(cardViewItemDTO.dateAdded);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    date2 = sdf.parse(t1.dateAdded);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                return date2.compareTo(date1) == 0 ? cardViewItemDTO.task.compareTo(t1.task) : date2.compareTo(date1);
                            }
                        });
                        break;
                    case 4: // Importance Highest
                        Collections.sort(items, new Comparator<CardViewItemDTO>() {
                            @Override
                            public int compare(CardViewItemDTO cardViewItemDTO, CardViewItemDTO t1) {
                                float im1 = cardViewItemDTO.importance;
                                float im2 = t1.importance;
                                return im1 == im2 ? cardViewItemDTO.task.compareTo(t1.task) : (int)(im2 - im1);
                            }
                        });
                        break;
                    case 5: // reverse
                        Collections.sort(items, new Comparator<CardViewItemDTO>() {
                            @Override
                            public int compare(CardViewItemDTO cardViewItemDTO, CardViewItemDTO t1) {
                                float im1 = cardViewItemDTO.importance;
                                float im2 = t1.importance;
                                return im1 == im2 ? cardViewItemDTO.task.compareTo(t1.task) : (int)(im1 - im2);
                            }
                        });
                        break;
                    default: // case 0 (End Date Earliest)
                        Collections.sort(items, new Comparator<CardViewItemDTO>() {
                            @Override
                            public int compare(CardViewItemDTO cardViewItemDTO, CardViewItemDTO t1) {
                                boolean firstDateEmpty = false;
                                Date date1 = null;
                                Date date2 = null;
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                try {
                                    date1 = sdf.parse(cardViewItemDTO.dateEnd);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                    firstDateEmpty = true;
                                }
                                try {
                                    date2 = sdf.parse(t1.dateEnd);
                                    if (firstDateEmpty) return 1;
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                    if (firstDateEmpty) return cardViewItemDTO.task.compareTo(t1.task);
                                    else return -1;
                                }
                                return date1.compareTo(date2);
                            }
                        });
                }
                adapter.setCardViewItemDTOs(items);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });

        // Add Button

        addBtn = findViewById(R.id.add_button);
        addBtn.setOnClickListener(this);
        refreshBtn = findViewById(R.id.refresh_button);
        refreshBtn.setOnClickListener(this);
    }

    private void saveData() {
        items = adapter.getCardViewItemDTOs();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(items);
        editor.putString("task list", json);
        editor.apply();
    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("task list", null);
        Type type = new TypeToken<ArrayList<CardViewItemDTO>>() {}.getType();
        items = gson.fromJson(json, type);
        if (items == null) {
            items = new ArrayList<>();
        }
        adapter.setCardViewItemDTOs(items);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_button: // Generate text fields for new task
                startActivity(new Intent(MainActivity.this, AddActivity.class));
                break;
            case R.id.refresh_button: // Update Day Counters
                adapter.notifyDataSetChanged();
                break;
        }
    }
}


