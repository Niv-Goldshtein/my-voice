package com.example.myvoice;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * history activity in this activity the user will see all of his history , he can sort it by category and delete history
 */
public class HistoryActivity extends AppCompatActivity {
    private String[] sortSpinner = {"כל ההיסטוריה","וויז","התקשרת ל","חיפוש בגוגל","שלחת סמס"};
    private ArrayList<History> sortHistory = new ArrayList<>();
    private ArrayList<History> myHistory = new ArrayList<>();
    private ListView lvHistory;
    private Button btnBack,btnSort;
    private Spinner historySpinner;
    private User u;
    private History deleteH;
    private ArrayList<History> array = new ArrayList<>();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("");
    /**
     * connect between the layout and the code
     * you will see all your history and you can sort it by category or delete history
     * activated when this activity opens
     * @param savedInstanceState not in use
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history2);
        historySpinner = findViewById(R.id.spinnerH);
        lvHistory = findViewById(R.id.lvHistory);
        btnBack = findViewById(R.id.btnBack);
        btnSort = findViewById(R.id.btnSort);
        refresh_spinner();
        Intent i = getIntent();
        if (i.hasExtra("User")) {
            u = (User) i.getSerializableExtra("User");
            if (u.HasArray())
                myHistory = u.getMyHistory();
        }
        refresh_lv();
        btnSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sort = historySpinner.getSelectedItem() + "";
                String newsort = SortString(sort);
                sortHistory.removeAll(sortHistory);
                if (!sort.equals("כל ההיסטוריה")) {
                    for (int i = 0; i < myHistory.size(); i++) {
                        if (myHistory.get(i).getWhatIdid().contains(newsort))
                            sortHistory.add(myHistory.get(i));
                    }
                    refresh_sortlv();
                } else
                    refresh_lv();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("User",u);
                finish();
            }
        });
        lvHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                final Dialog myDialog = new Dialog(HistoryActivity.this);
                myDialog.setContentView(R.layout.dialog_history);
                Button btnBack, btnDelete;
                final TextView tvTime, tvWhatIDid, tvDetail;
                ImageView ivWhatIDid;

                if ((historySpinner.getSelectedItem()+"").equals("כל ההיסטוריה"))
                    array = myHistory;
                else
                    array = sortHistory;

                tvTime = myDialog.findViewById(R.id.tvTime1);
                tvWhatIDid = myDialog.findViewById(R.id.tvWhatIDid1);
                tvDetail = myDialog.findViewById(R.id.tvDetail1);
                ivWhatIDid = myDialog.findViewById(R.id.ivWhatIDid1);

                if (array.get(position).getWhatIdid().contains("התקשרת ל")) {
                    ivWhatIDid.setImageResource(R.drawable.phone);
                } else if (array.get(position).getWhatIdid().contains("שלחת הודעה ל")) {
                    ivWhatIDid.setImageResource(R.drawable.sms);
                } else if (array.get(position).getWhatIdid().contains("נסעת עם וויז")) {
                    ivWhatIDid.setImageResource(R.drawable.waze);
                } else if (array.get(position).getWhatIdid().contains("חיפשת בגוגל"))
                    ivWhatIDid.setImageResource(R.drawable.google);

                tvTime.setText(array.get(position).getTime());
                tvWhatIDid.setText(array.get(position).getWhatIdid());
                tvDetail.setText(array.get(position).getDetails());
                btnBack = myDialog.findViewById(R.id.btnBack);
                btnDelete = myDialog.findViewById(R.id.btnDelete);

                btnBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        myDialog.dismiss();
                    }
                });
                deleteH=array.get(position);
                btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (LoginActivity.checkWifi(HistoryActivity.this)) {
                            if (myHistory.size()>1) {
                                myHistory.remove(deleteH);
                            }
                            else
                                myHistory.removeAll(myHistory);
                            u.setMyHistory(myHistory);
                            myRef.child("AllUsers").child(u.getUsername()).setValue(u);
                            if ((historySpinner.getSelectedItem()+"").equals("כל ההיסטוריה")) {
                                refresh_lv();
                            }
                            else
                            {
                                for (int i = 0; i<sortHistory.size();i++){
                                    if (sortHistory.get(i).getTime().equals(deleteH.getTime())&&sortHistory.get(i).getDetails().equals(deleteH.getDetails())
                                    &&sortHistory.get(i).getWhatIdid().equals(deleteH.getWhatIdid())){
                                        sortHistory.remove(i);
                                    }
                                }
                                refresh_sortlv();
                            }
                        }
                        else
                            Toast.makeText(HistoryActivity.this,"יש צורך בחיבור רשת על מנת למחוק מההיסטוריה",Toast.LENGTH_SHORT).show();
                        myDialog.dismiss();
                    }
                });
                myDialog.show();
            }
        });
    }
    /**
     * change the string to what you did in history according to your sort
     * @param s how it appears in he spinner
     * @return how it appears in your history in what you did category
     */
    private String SortString(String s){
        if (s.equals("וויז"))
            s = "נסעת עם וויז";
        if (s.equals("שלחת סמס"))
            s = "שלחת הודעה";
        if (s.equals("חיפוש בגוגל"))
            s = "חיפשת בגוגל";
        if (s.equals("התקשרת ל"))
            s = "התקשרת ל";
        return s;
    }
    /**
     * show the arraylist in the listview
     */
    private void refresh_lv(){
        MyAdapter adp = new MyAdapter(this,R.layout.row, myHistory);

        lvHistory.setAdapter(adp);
    }
    /**
     * show the sort arraylist in the listview
     */
    private void refresh_sortlv(){
        MyAdapter adp = new MyAdapter(this,R.layout.row, sortHistory);

        lvHistory.setAdapter(adp);
    }
    /**
     * show the category in the spinner
     */
    private void refresh_spinner(){
        ArrayAdapter adp = new ArrayAdapter(this,R.layout.row,sortSpinner);

        historySpinner.setAdapter(adp);
    }
}
