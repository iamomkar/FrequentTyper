package com.creativeminds.omkar.frequenttyper;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    ArrayAdapter<String > arrayAdapter;
    ArrayList<String> fwordList = new ArrayList<>();
    SQLDatabaseHelper sqlDatabaseHelper;
    boolean isIntentLaunch=false,isreadonly=false;
    String search_key,new_fword_str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sqlDatabaseHelper = new SQLDatabaseHelper(MainActivity.this);

        if(getIntent().getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT) != null) {
            isIntentLaunch=true;
            CharSequence text = getIntent().getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT);
            search_key=text.toString();

            isreadonly = getIntent().getBooleanExtra(Intent.EXTRA_PROCESS_TEXT_READONLY, false);
            if(isreadonly){
                customToast("This Field is Not Editable Add as New Frequent Word");
                new_fword_str=text.toString();
                addNewFwordDialog();
            }else {
                new_fword_str=text.toString();
                customToast("Recived Text :- "+text.toString().toLowerCase());
                if(sqlDatabaseHelper.checkifSearchKeyAlreadyPresent(text.toString().toLowerCase())){
                    Intent intent = new Intent();
                    intent.putExtra(Intent.EXTRA_PROCESS_TEXT,sqlDatabaseHelper.getFwordBySearchKey(text.toString().toLowerCase()).getFword());
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        }

        listView = (ListView)findViewById(R.id.list_view);

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, fwordList);
        listView.setAdapter(arrayAdapter);

        sqlDatabaseHelper.getAllFwordsasString(fwordList);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String data = (String) adapterView.getItemAtPosition(i);
                if(isIntentLaunch){
                    Intent intent = new Intent();
                    intent.putExtra(Intent.EXTRA_PROCESS_TEXT,data);
                    setResult(RESULT_OK, intent);
                    finish();
                }else {
                    showDetailDialog(data);
                }
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                Fword fword = sqlDatabaseHelper.getFword((String)adapterView.getItemAtPosition(i));
                sqlDatabaseHelper.deleteFword(fword.getDate());
                customSnackbar("Deleted Successfully");
                notifyAdapter();
                return true;
            }
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewFwordDialog();
            }
        });
    }

  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Idea and Developed by");
            builder.setMessage("\n Omkar Shinde @ Creative Minds\n");
            builder.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void customToast(String msg){
        Toast.makeText(MainActivity.this,msg,Toast.LENGTH_SHORT).show();
    }

    public void customSnackbar(String msg){
        Snackbar.make((View)findViewById(R.id.rel_layout),msg,Snackbar.LENGTH_LONG).show();
    }

    public void addNewFwordDialog(){

        LinearLayout linearLayout = new LinearLayout(MainActivity.this);

        LinearLayout.LayoutParams layoutParams= new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        final EditText editText = new EditText(MainActivity.this);
        editText.setHint("Enter Frequent Word");editText.setLayoutParams(layoutParams);
        if(isIntentLaunch){editText.setText(new_fword_str);}
        else if(!isreadonly && isIntentLaunch){editText.setText(new_fword_str);}

        final EditText editText2 = new EditText(MainActivity.this);
        editText2.setHint("Enter Search Key(Lower Case)");editText2.setLayoutParams(layoutParams);
        editText2.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE);

        linearLayout.addView(editText);linearLayout.addView(editText2);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Add New Entry");
        builder.setView(linearLayout);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String word = editText.getText().toString();
                String key = editText2.getText().toString().toLowerCase();
                String date = getCurrentDateTime();

                Fword fword = new Fword(word,key,date);

                if(sqlDatabaseHelper.checkifSearchKeyAlreadyPresent(key)){
                    customToast("Frequent Word with Same Search Key Already Present");
                }else if(sqlDatabaseHelper.checkifFwordAlreadyPresent(key)) {
                    customToast("Frequent Word with Same Data Already Present");
                }else {
                        sqlDatabaseHelper.addNewFword(fword);
                        notifyAdapter();
                }
            }
        });
        builder.show();

    }

    public String getCurrentDateTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(new Date());
    }

    public void showDetailDialog(String key){
        final Fword fword = sqlDatabaseHelper.getFword(key);
        AlertDialog.Builder builder =  new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(fword.getFword());
        builder.setMessage("Search Key:- "+fword.getSearch_key());
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                sqlDatabaseHelper.deleteFword(fword.getDate());
                notifyAdapter();
            }
        });
        builder.setNegativeButton("Edit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                editFwordDialog(fword);
            }
        });
        builder.show();
    }

    public void editFwordDialog(final Fword fword){
        LinearLayout linearLayout = new LinearLayout(MainActivity.this);

        LinearLayout.LayoutParams layoutParams= new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        final EditText editText = new EditText(MainActivity.this);
        editText.setHint("Enter Frequent Word");editText.setText(fword.getFword());editText.setLayoutParams(layoutParams);

        final EditText editText2 = new EditText(MainActivity.this);
        editText2.setHint("Enter Search Key(Lower Case)"); editText2.setText(fword.getSearch_key());editText2.setLayoutParams(layoutParams);
        editText2.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE);

        linearLayout.addView(editText);linearLayout.addView(editText2);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Edit");
        builder.setView(linearLayout);
        builder.setPositiveButton("DONE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Fword editedFword = new Fword(editText.getText().toString(),editText2.getText().toString().toLowerCase(),fword.getDate());
                sqlDatabaseHelper.updateFword(editedFword);
                customSnackbar("Updated Successfully");
                notifyAdapter();
            }
        });
        builder.setNegativeButton("Cancel",null);
        builder.show();

    }

    public void notifyAdapter(){
        sqlDatabaseHelper.getAllFwordsasString(fwordList);
        arrayAdapter.notifyDataSetChanged();
    }
}
