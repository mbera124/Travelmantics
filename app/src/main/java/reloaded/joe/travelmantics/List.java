package reloaded.joe.travelmantics;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class List extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        findViewById(R.id.rvdeals).setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {

                startActivity(new Intent(List.this, Deal.class));

            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_menu, menu);
        MenuItem insertMenu=menu.findItem(R.id.insert_menu);
        if (FirebaseUtil.isAdmin){
            insertMenu.setVisible(true);
        }
        else{
            insertMenu.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.insert_menu:
                Intent intent = new Intent(this, Deal.class);
                startActivity(intent);
                return true;
            case R.id.logout_menu:
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d("Logout","Logout Success!");
                                FirebaseUtil.attatchListener();
                            }
                        });
                FirebaseUtil.detatchListener();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void  onPause(){
        super.onPause();
        FirebaseUtil.detatchListener();
    }
    @Override
    protected void  onResume(){
        super.onResume();
        FirebaseUtil.openFbReference("traveldeals",this);
        RecyclerView rvdeals = findViewById(R.id.rvdeals);
        final DealAdapter adapter = new DealAdapter();
        rvdeals.setAdapter(adapter);
        LinearLayoutManager dealsLayoutManager =
                new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rvdeals.setLayoutManager(dealsLayoutManager);
        FirebaseUtil.attatchListener();
    }
    public void showMenu(){
        invalidateOptionsMenu();
    }
}


