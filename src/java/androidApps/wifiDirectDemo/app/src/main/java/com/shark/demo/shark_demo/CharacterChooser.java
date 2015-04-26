package com.shark.demo.shark_demo;

import android.content.Intent;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

/**
 * This is the first class to be created. It doesn't do much except waiting
 * for the user to choose his avatar.
 */
public class CharacterChooser extends ActionBarActivity implements View.OnClickListener {

    private Button _bobBtn;
    private Button _aliceBtn;
    private Button _claraBtn;
    public static final String NAME = "com.shark.demo.shark_demo.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character_chooser);

        _aliceBtn = (Button) findViewById(R.id.alicebtn);
        _bobBtn = (Button) findViewById(R.id.bobbtn);
        _claraBtn = (Button) findViewById(R.id.clarabtn);

        _aliceBtn.setOnClickListener(this);
        _bobBtn.setOnClickListener(this);
        _claraBtn.setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_character_chooser, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        String name = "";

        if (v.getId() == R.id.alicebtn) name = "Alice";
        else if (v.getId() == R.id.bobbtn) name = "Bob";
        else if (v.getId() == R.id.clarabtn) name = "Clara";


        Intent intent = new Intent(CharacterChooser.this, MainActivity.class);
        intent.putExtra(NAME, name);
        startActivity(intent);
    }
}
