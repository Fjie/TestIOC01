package me.fanjie.testioc01;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import me.fanjie.testioc01.ioc.ContentView;
import me.fanjie.testioc01.ioc.OnClick;
import me.fanjie.testioc01.ioc.ViewInject;
import me.fanjie.testioc01.ioc.ViewInjectUtils;

@ContentView(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {

    @ViewInject(R.id.fab)
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        findViewById()

        ViewInjectUtils.inject(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


    }
    @OnClick({R.id.fab})
    public void click(View view){
        int id = view.getId();
        if(id == R.id.fab){
            Toast.makeText(MainActivity.this,"成功",Toast.LENGTH_SHORT).show();
        }
    }


}
