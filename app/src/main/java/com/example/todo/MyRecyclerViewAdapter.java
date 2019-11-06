package com.example.todo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<CardViewItemDTO> cardViewItemDTOs;
    private Context context;

    public MyRecyclerViewAdapter(ArrayList<CardViewItemDTO> contents, Context context) {
        this.cardViewItemDTOs = contents;
        this.context = context;
    }

    @NonNull
    @Override
    // XML 세팅
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_item, parent,false);
        return new RowCell(view);
    }

    @Override
    // 각 아이템 세팅
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        ((RowCell)holder).task.setText(cardViewItemDTOs.get(position).task);
        ((RowCell)holder).dayCount.setText(dDayConverter(cardViewItemDTOs.get(position).dateEnd));
        ((RowCell)holder).importance.setRating(cardViewItemDTOs.get(position).importance);
        ((RowCell)holder).importance.setIsIndicator(true);
        ((RowCell)holder).details.setText(cardViewItemDTOs.get(position).details);

        if (cardViewItemDTOs.get(position).done) { // task completed
            ((RowCell)holder).done.setImageResource(android.R.drawable.checkbox_on_background);
        } else { // task not completed
            ((RowCell)holder).done.setImageResource(android.R.drawable.checkbox_off_background);
        }

        ((RowCell)holder).done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardViewItemDTOs.get(position).done = !cardViewItemDTOs.get(position).done;
                if (cardViewItemDTOs.get(position).done) {
                    ((ImageView)view).setImageResource(android.R.drawable.checkbox_on_background);
                } else {
                    ((ImageView)view).setImageResource(android.R.drawable.checkbox_off_background);
                }
                notifyDataSetChanged();
                saveData();
            }
        });

        ((RowCell)holder).calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String dateAdd = cardViewItemDTOs.get(position).dateAdded;
                String dateEnd = cardViewItemDTOs.get(position).dateEnd;
                // Inflate the layout of the popup window
                LayoutInflater inflater = (LayoutInflater)
                        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.popup_window, null);
                TextView textViewAdd = (TextView) popupView.findViewById(R.id.text_add);
                TextView textViewEnd = (TextView) popupView.findViewById(R.id.text_end);

                textViewAdd.setText(dateAdd);
                textViewEnd.setText(dateEnd);


                // create the popup window
                int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                boolean focusable = true; // lets taps outside the popup also dismiss it
                final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

                // show the popup window
                // which view you pass in doesn't matter, it is only used for the window tolken
                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

                // dismiss the popup window when touched
                popupView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        popupWindow.dismiss();
                        return true;
                    }
                });
            }
        });

        ((RowCell)holder).edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, AddActivity.class);
                i.putExtra("title", cardViewItemDTOs.get(position).task);
                i.putExtra("importance", cardViewItemDTOs.get(position).importance);
                i.putExtra("details", cardViewItemDTOs.get(position).details);
                i.putExtra("startDate", cardViewItemDTOs.get(position).dateAdded);
                if (cardViewItemDTOs.get(position).dateEnd.length() > 0) {
                    i.putExtra("endDate", cardViewItemDTOs.get(position).dateEnd.substring(0,10));
                    i.putExtra("endTime", cardViewItemDTOs.get(position).dateEnd.substring(11));
                } else {
                    i.putExtra("endDate", "");
                    i.putExtra("endTime", "");
                }
                i.putExtra("done", cardViewItemDTOs.get(position).done);
                context.startActivity(i);
                cardViewItemDTOs.remove(cardViewItemDTOs.get(position));
                notifyDataSetChanged();
                saveData();
            }
        });

        ((RowCell)holder).delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardViewItemDTOs.remove(cardViewItemDTOs.get(position));
                notifyDataSetChanged();
                saveData();
            }
        });
    }

    private String dDayConverter(String dateEnd) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date1 = new java.util.Date();
        Date date2 = null;
        try {
            date2 = df.parse(dateEnd);
        } catch (ParseException e) {
            e.printStackTrace();
            return "No D-Day";
        }
        long diff = date2.getTime() - date1.getTime();
        String result = "D";
        if (diff >= 0) result += "-";
        else result += "+";

        diff /= (1000 * 60); // diff in Minutes
        diff = Math.abs(diff);
        int num = 0;
        if (diff > 24 * 60) {
            num = (int)Math.floor(diff / (24 * 60));
            result += num + (num > 1 ? "Days" : "Day");
        } else {
            if (diff == 0) {
                result += "0";
            } else if (diff <= 60) {
                num = (int)Math.floor(diff);
                result += num + (num > 1 ? "Mins" : "Min");
            } else {
                num = (int)Math.floor(diff / 60);
                result += num + (num > 1 ? "Hrs" : "Hr");
            }
        }
        return result;
    }

    private void saveData() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(cardViewItemDTOs);
        editor.putString("task list", json);
        editor.apply();
    }

    public ArrayList<CardViewItemDTO> getCardViewItemDTOs() {
        return cardViewItemDTOs;
    }

    public void setCardViewItemDTOs(ArrayList<CardViewItemDTO> cardViewItemDTOs) {
        this.cardViewItemDTOs = cardViewItemDTOs;
    }

    @Override
    // Counter
    public int getItemCount() {
        return cardViewItemDTOs.size();
    }

    private class RowCell extends RecyclerView.ViewHolder {
        public TextView task;
        public TextView dayCount;
        public RatingBar importance;
        public TextView details;

        public ImageView done;
        public ImageView edit;
        public ImageView calendar;
        public ImageView delete;

        public RowCell(View view) {
            super(view);
            task = (TextView)view.findViewById(R.id.item_task);
            dayCount = (TextView)view.findViewById(R.id.day_counter);
            importance = (RatingBar) view.findViewById(R.id.item_importance);
            details = (TextView)view.findViewById(R.id.item_details);

            done = (ImageView)view.findViewById(R.id.checkbox);
            edit = (ImageView)view.findViewById(R.id.edit_button);
            calendar = (ImageView)view.findViewById(R.id.calendar_button);
            delete = (ImageView)view.findViewById(R.id.delete_button);
        }
    }
}

