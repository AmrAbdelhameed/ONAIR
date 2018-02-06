package com.example.amr.onair.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.example.amr.onair.Others.MyDividerItemDecoration;
import com.example.amr.onair.R;
import com.example.amr.onair.adapters.DeleteAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DeleteActivity extends AppCompatActivity {

    DeleteAdapter deleteAdapter;
    RecyclerView recycler_view;
    RecyclerView.LayoutManager mLayoutManager;
    List<String> stockListEmail;
    Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setTitle("Edit Members");

        recycler_view = findViewById(R.id.recycler_view);
        stockListEmail = new ArrayList<>();
        gson = new Gson();

        Intent sentIntent = getIntent();
        Bundle sentBundle = sentIntent.getExtras();
        assert sentBundle != null;
        String stockListString = sentBundle.getString("stockListString");

        Type type = new TypeToken<List<String>>() {
        }.getType();
        stockListEmail = gson.fromJson(stockListString, type);

        deleteAdapter = new DeleteAdapter(DeleteActivity.this, stockListEmail);
        mLayoutManager = new GridLayoutManager(DeleteActivity.this, 1);
        recycler_view.setLayoutManager(mLayoutManager);
        recycler_view.addItemDecoration(new MyDividerItemDecoration(DeleteActivity.this, DividerItemDecoration.VERTICAL, 0));
        recycler_view.setHasFixedSize(true);
        recycler_view.setItemAnimator(new DefaultItemAnimator());
        recycler_view.setAdapter(deleteAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.done, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_done) {
            String stockListString = gson.toJson(stockListEmail);
            Intent intent = new Intent();
            Bundle b = new Bundle();
            b.putString("stockListString", stockListString);
            intent.putExtras(b);
            setResult(RESULT_OK, intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
