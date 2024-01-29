package com.example.myvoice;

import android.Manifest;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.ContactsContract;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import androidx.appcompat.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * main page here everything happen: the voice recognizer and his actions,create history and accepting permissions in order to use the app
 */
public class MainPageActivity extends AppCompatActivity {

    private Button btnSayWhatYouWantToDo;
    private List<ContactModel> contacts;
    private TextView tvCall, tvSms, tvWaze, tvGoogle, tvYoutube, tvWhatYouSaid,tvWhatYouCanSay;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("");
    private Intent intent;
    private String phone = "", name, place;
    private User u;
    private CountDownTimer cdt, onStartctd;
    private ArrayList<String> data;
    private ArrayList<History> myHistory = new ArrayList<>();
    private Dialog whatISaidDialog;



    /**
     * connect between the code and layout
     * open the recognizer intennt and the function that will choose what to do with it
     * @param savedInstanceState not in use
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        intent = getIntent();
        if (intent.hasExtra("User")) {
            u = (User) intent.getSerializableExtra("User");
            if (u.HasArray())
                myHistory=u.getMyHistory();
        }
        tvWhatYouCanSay = findViewById(R.id.tvWhatYouCanSay);
        tvWhatYouCanSay.setText("מה אתה יכול לעשות:");
        btnSayWhatYouWantToDo = findViewById(R.id.btnSayWhatToDo);
        btnSayWhatYouWantToDo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (LoginActivity.checkWifi(getApplicationContext()))
                    if (!CheckPermission("record_audio")){
                        Toast.makeText(MainPageActivity.this, "יש צורך בהרשה על מנת לעשות זאת אנא אשר בהגדרות שימוש במיקרופון", Toast.LENGTH_SHORT).show();
                        requestPermissions(new String[] {  Manifest.permission.RECORD_AUDIO }, 411);
                        return;
                    }
                    else
                        getSpeech();
                else
                    Toast.makeText(getApplicationContext(),"יש צורך בחיבור לרשת",Toast.LENGTH_SHORT).show();
            }
        });

        tvCall = findViewById(R.id.tvCall);
        tvCall.setText("1.תתקשר ל+השם של מי שתרצה להתקשר אליו");
        tvGoogle = findViewById(R.id.tvGoogle);
        tvGoogle.setText("3.תחפש+מה שתרצה לחפש בגוגל");
        tvSms = findViewById(R.id.tvSms);
        tvSms.setText("2.תשלח הודעה ל+השם של מי שתרצה לשלוח לו הודעה");
        tvWaze = findViewById(R.id.tvGoWaze);
        tvWaze.setText("4.סע ל+המקום שאליו תרצה להגע");
        tvYoutube = findViewById(R.id.tvPlaySong);
        tvYoutube.setText("");

        onStartctd = new CountDownTimer(2000, 1000) {
            @Override
            public void onTick(long l) {
            }
            @Override
            public void onFinish() {
                if (LoginActivity.checkWifi(MainPageActivity.this))
                    if (!CheckPermission("record_audio")){
                        Toast.makeText(MainPageActivity.this, "יש צורך בהרשה על מנת לעשות זאת אנא אשר בהגדרות שימוש במיקרופון", Toast.LENGTH_SHORT).show();
                        requestPermissions(new String[] {  Manifest.permission.RECORD_AUDIO }, 887);
                        return;
                    }
                    else
                        getSpeech();
            }
        }.start();
    }
    /**
     * start Recognizer speech and you need to say what you want
     * and than starting Activity for result
     */
    private void getSpeech() {
//        bcrtoOpenTheActivity();
        SpeechRecognizer recognizer = SpeechRecognizer.createSpeechRecognizer(this);
        Intent whatToDoIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        whatToDoIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        whatToDoIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());  //"en-US" , "he-IL"
        whatToDoIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Need to speak");

        whatISaidDialog = new Dialog(MainPageActivity.this);
        whatISaidDialog.setContentView(R.layout.dialog_what_i_said);
        tvWhatYouSaid = whatISaidDialog.findViewById(R.id.tvWhatISaid);
        tvWhatYouSaid.setText("מה ברצונך לעשות?");

        recognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {
            }
            @Override
            public void onBeginningOfSpeech() {
            }
            /**
             * volume in DB
             * @param v
             */
            @Override
            public void onRmsChanged(float v) {
            }
            @Override
            public void onBufferReceived(byte[] bytes) {
            }
            @Override
            public void onEndOfSpeech() {
            }
            /**
             * 9 - permission
             * 6 - no input
             * @param i
             */
            @Override
            public void onError(int i) {
            }
            /**
             * what happen when finish listening
             * @param bundle text result
             */
            @Override
            public void onResults(Bundle bundle) {
                data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                Log.d("myInfo", data.get(0));

                cdt = new CountDownTimer(3000, 1000) {
                    @Override
                    public void onTick(long l) {
                        tvWhatYouSaid.setText(data.get(0));
                    }
                    @Override
                    public void onFinish() {
                        whatISaidDialog.dismiss();

                        if (!"".equals(data.get(0))) {

                            String lastResult = data.get(0).toString().toLowerCase().trim();
                            int lastIndex = lastResult.length();
                            Toast.makeText(MainPageActivity.this, lastResult, Toast.LENGTH_SHORT).show();

                            if (lastResult.contains("תתקשר") || lastResult.contains("התקשר")) {
                                if (lastResult.length() <= 6) {
                                    Toast.makeText(getApplicationContext(),"לא נאמר למי להתקשר",Toast.LENGTH_SHORT).show();
                                    getSpeech();
                                    return;
                                }
                                if (CheckPermission("contacts"))
                                    contacts = getContacts(MainPageActivity.this);
                                else {
                                    Toast.makeText(MainPageActivity.this, "יש צורך בהרשה על מנת לעשות זאת אנא אפשר גישה לאנשי קשר בהגדרות", Toast.LENGTH_SHORT).show();
                                    requestPermissions(new String[] {  Manifest.permission.READ_CONTACTS }, 446);
                                    return;
                                }
                                if (CheckPermission("CALL_PHONE")){
                                    Toast.makeText(MainPageActivity.this, "יש צורך בהרשה על מנת לעשות זאת אנא אפשר שימוש בטלפון בהגדרות", Toast.LENGTH_SHORT).show();

                                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 418);
                                    return;
                                }
                                name = lastResult.substring(7, lastIndex);
                                for (int i = 0; i <= contacts.size() - 1; i++) {
                                    if (contacts.get(i).contactName.equals(name)) {
                                        phone = contacts.get(i).mobileNumber;
                                    }
                                }
                                if (!phone.equals("")) {
                                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                                    callIntent.setData(Uri.parse("tel:" + phone + ""));
                                    BuildHistory(name, "התקשרת ל", phone);
                                    startActivity(callIntent);
                                } else {
                                    Toast.makeText(MainPageActivity.this, "אין לך איש קשר כזה נסה שוב", Toast.LENGTH_SHORT).show();
                                    getSpeech();
                                }

                            }
                            else if (lastResult.contains("תשלח הודעה ל")) {
                                if (!CheckPermission("SEND_SMS")) {
                                    Toast.makeText(MainPageActivity.this, "יש צורך בהרשה על מנת לעשות זאת אנא אפשר שימוש בSMS בהגדרות", Toast.LENGTH_SHORT).show();
                                    requestPermissions(new String[]{Manifest.permission.SEND_SMS}, 580);
                                    return;
                                }
                                if (CheckPermission("contacts"))
                                    contacts = getContacts(MainPageActivity.this);
                                else {
                                    Toast.makeText(MainPageActivity.this, "יש צורך בהרשה על מנת לעשות זאת אנא אפשר גישנה לאנשי הקשר בהגדרות", Toast.LENGTH_SHORT).show();
                                    requestPermissions(new String[] {  Manifest.permission.READ_CONTACTS }, 585);
                                    return;
                                }
                                name = lastResult.substring(12, lastIndex);
                                if (lastResult.length() <= 11) {
                                    Toast.makeText(getApplicationContext(),"לא נאמר למי לשלוח הודעה",Toast.LENGTH_SHORT).show();
                                    getSpeech();
                                    return;
                                }
                                for (int i = 0; i <= contacts.size() - 1; i++) {
                                    if (contacts.get(i).contactName.equals(name)) {
                                        phone = contacts.get(i).mobileNumber;
                                    }
                                }
                                if (!phone.equals("")) {
                                    getmsgSpeech();

                                } else {
                                    Toast.makeText(MainPageActivity.this, "אין לך איש קשר כזה נסה שוב", Toast.LENGTH_SHORT).show();
                                    getSpeech();
                                }

                            }
                            else if (lastResult.contains("תחפש") || lastResult.contains("חפש")) {
                                int startIndex;
                                if (lastResult.contains("תחפש"))
                                    startIndex = 4;
                                else
                                    startIndex = 3;
                                if (lastResult.length() <= 4) {
                                    Toast.makeText(getApplicationContext(),"לא נאמר מה לחפש",Toast.LENGTH_SHORT).show();
                                    getSpeech();
                                    return;
                                }

                                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                                String term = lastResult.substring(startIndex, lastIndex).trim();
                                intent.putExtra(SearchManager.QUERY, term);
                                BuildHistory("", " חיפשת בגוגל", term);
                                startActivity(intent);

                            }
                            else if (lastResult.contains("סע ")) {
                                if (lastResult.length() < 4) {
                                    Toast.makeText(getApplicationContext(),"לא נאמר לאן ברצונך לסוע",Toast.LENGTH_SHORT).show();
                                    getSpeech();
                                    return;
                                }
                                place = lastResult.substring(4, lastIndex).trim() + "";
                                String wazelink = place.replace(" ", "%20");
                                try {
                                    String url = "https://waze.com/ul?q=" + wazelink;
                                    BuildHistory("", "נסעת עם וויז", place);
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                    startActivity(intent);
                                } catch (ActivityNotFoundException ex) {
                                    // If Waze is not installed, open it in Google Play:
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.waze"));
                                    Toast.makeText(MainPageActivity.this, "וויז לא מותקן עליך להוריד אותו על מנת להשתמש בפונקצייה הזאת", Toast.LENGTH_SHORT).show();
                                    startActivity(intent);
                                }
                            }
                            else {
                                getSpeech();
                            }
                        }
                        else {
                            Toast.makeText(MainPageActivity.this,"לא נאמר דבר אנא נסה שנית",Toast.LENGTH_SHORT).show();
                            whatISaidDialog.dismiss();
                        }
                    }
                }.start();
            }
            @Override
            public void onPartialResults(Bundle bundle) {
            }
            @Override
            public void onEvent(int i, Bundle bundle) {
            }
        });
        whatISaidDialog.show();

        recognizer.startListening(whatToDoIntent);
//        bcrtoOpenTheActivity();
    }
    /**
     * start Recognizer speech and you need to say what you want
     * and than starting Activity for result
     */
    private void getmsgSpeech() {
        SpeechRecognizer msgRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        Intent msgIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH); //ACTION_RECOGNIZE_SPEECH);
        msgIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        msgIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault()); //"en-US" , "he-IL"
        msgIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Need to speak");

        whatISaidDialog = new Dialog(MainPageActivity.this);
        whatISaidDialog.setContentView(R.layout.dialog_what_i_said);
        tvWhatYouSaid = whatISaidDialog.findViewById(R.id.tvWhatISaid);
        tvWhatYouSaid.setText("אמור את ההודעה");

        msgRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {
            }
            @Override
            public void onBeginningOfSpeech() {
            }
            @Override
            public void onRmsChanged(float v) {
            }
            @Override
            public void onBufferReceived(byte[] bytes) {
            }
            @Override
            public void onEndOfSpeech() {
            }
            @Override
            public void onError(int i) {
            }
            @Override
            public void onResults(Bundle bundle) {
                data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                cdt = new CountDownTimer(5000, 1000) {
                    @Override
                    public void onTick(long l) {
                        tvWhatYouSaid.setText(data.get(0));
                    }
                    @Override
                    public void onFinish() {
                        whatISaidDialog.dismiss();
                        if (!"".equals(data.get(0))) {
                            String txtMessage = data.get(0).toLowerCase().trim();
                            BuildHistory(name, "שלחת הודעה ל", txtMessage);
                            SmsManager smgr = SmsManager.getDefault();
                            smgr.sendTextMessage(phone, null, txtMessage, null, null);
                            Toast.makeText(MainPageActivity.this, "שלחת הודעה ל" + name, Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(MainPageActivity.this, "לא אמרת כלום אנא נסה שנית", Toast.LENGTH_SHORT).show();
                    }
                }.start();
            }
            @Override
            public void onPartialResults(Bundle bundle) {

            }
            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });
        whatISaidDialog.show();
        msgRecognizer.startListening(msgIntent);
    }
    /**
     * create menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mymenu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    /**
     * what will happen after choosing option in menu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.History) {

            Intent i = new Intent(MainPageActivity.this, HistoryActivity.class);
            i.putExtra("User", u);
            startActivity(i);


        }
        if(item.getItemId() == R.id.Disconenct) {


                final Dialog myDialog = new Dialog(MainPageActivity.this);

                myDialog.setContentView(R.layout.dialog_stay_logged_in);
                Button btnYes = myDialog.findViewById(R.id.btnYes);
                Button btnNo = myDialog.findViewById(R.id.btnNo);
                TextView tvStayLoggedIn = myDialog.findViewById(R.id.tvStayLogged);
                tvStayLoggedIn.setText("האם תרצה להתחבר אוטומטית פעם הבאה?");

                btnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SharedPreferences.Editor editor = getSharedPreferences("myTable", MODE_PRIVATE).edit();
                        editor.putBoolean("save", true);
                        editor.apply();
                        editor.commit();
                        myDialog.dismiss();
                        finishAffinity();
                        //closing app
                    }
                });

                btnNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SharedPreferences.Editor editor = getSharedPreferences("myTable", MODE_PRIVATE).edit();
                        editor.putBoolean("save", false);
                        editor.apply();
                        editor.commit();
                        myDialog.dismiss();
                        finish();
                        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(i);

                    }
                });
                myDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * create string of the date
     *
     * @return String with the date details
     */
    private String getDate() {
        DateFormat dfTime = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");
        String time = dfTime.format(Calendar.getInstance().getTime());
        return  " " + time;
    }

    /**
     * this function add history object to the user's history ArrayList and push it into the FB
     * @param name       the name of the contact if you use contact
     * @param Whatyoudid what you did in app rn
     * @param detail     what is the details of what you did
     */
    private void BuildHistory(String name, String Whatyoudid, String detail) {
        String time = getDate();
        History h = new History(time, Whatyoudid + name, detail);
        myHistory.add(h);
        u.setMyHistory(myHistory);
        myRef.child("AllUsers").child(u.getUsername()).setValue(u);
    }

    /**
     * check if has permission
     * @param s which permission
     * @return if the app has permission
     */
    private boolean CheckPermission(String s) {
        boolean isPermission =  false;
        if (s.equals("SEND_SMS")) {
            if (checkSelfPermission( Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                isPermission=true;
            }
        }
        if (s.equals("CALL_PHONE")) {
            if (checkSelfPermission(  Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                isPermission=true;
            }
        }
        if (s.equals("contacts")) {
            if (checkSelfPermission( Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                isPermission= true;
            }
        }
        if (s.equals("record_audio")){
            if (checkSelfPermission( Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)
                isPermission=true;
        }
        return isPermission;
    }

    /**
     * create ContactModel class
     */
    class ContactModel {
        public String contactName;
        public String mobileNumber;
        public String toString(){
            return  contactName+ " " +mobileNumber;
        }
    }

    /**
     * create an ArrayList of all the contacts on the user's phone the app is running on
     * @param ctx the activity it is run on
     * @return Listview of all yur contacts in your phone
     */
    public List<ContactModel> getContacts(Context ctx) {
        List<ContactModel> list = new ArrayList<>();
        ContentResolver contentResolver = ctx.getContentResolver();
        Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                if (cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor cursorInfo = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                    InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(ctx.getContentResolver(),
                            ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(id)));
                    while (cursorInfo.moveToNext()) {
                        ContactModel info = new ContactModel();
                        info.contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        info.mobileNumber = cursorInfo.getString(cursorInfo.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        list.add(info);
                    }
                    cursorInfo.close();
                }
            }
            cursor.close();
        }
        return list;
    }

    BroadcastReceiver receiver;
    /**
     * this BCR opens the app no matter if the app is in the foreground or in background
     * it will be activated by service when the phone is shaked
     */
    public void bcrtoOpenTheActivity()
    {
        receiver = new BroadcastReceiver() {
            @Override public void onReceive(Context context, Intent intent )
            {

                Log.d("ggpauseBCR: Niv,", "in bcr");
                Intent i = new Intent(context, LoginActivity.class);
                startActivity(i);
            }
        };

    }

    @Override
    protected void onPause() {
      //  this.registerReceiver( receiver, new IntentFilter("com.blah.blah.somemessage"));
//            Log.d("ggpause","pause");


        super.onPause();
    }

    @Override
    protected void onResume() {

//        if(receiver!=null)
//            unregisterReceiver(receiver);
        super.onResume();
    }

    @Override
    protected void onStop() {
//        MySensorService.openClass = true;

        super.onStop();
    }
}
