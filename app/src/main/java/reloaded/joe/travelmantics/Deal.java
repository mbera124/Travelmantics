package reloaded.joe.travelmantics;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class Deal extends AppCompatActivity {
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private EditText ettitle, etprice, etdescription;
    private static final int PICTURE_RESULT=42;
    ImageView imageview;
    TravelDeal deal;
   // RecyclerView rvDeals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deal);
       mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        mDatabaseReference = FirebaseUtil.mDatabaseReference;
        ettitle = findViewById(R.id.ettitle);
        etprice = findViewById(R.id.etprice);
        etdescription = findViewById(R.id.etdescription);
        showImage(deal.getImageUrl());
        imageview=findViewById(R.id.image);
        Button btnimage=findViewById(R.id.btnimage);
        btnimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
                startActivityForResult(intent.createChooser(intent,"Insert Picture"),PICTURE_RESULT);
            }
        });
        Intent intent = getIntent();
        TravelDeal deal = (TravelDeal) intent.getSerializableExtra("Deal");
        if (deal == null) {
            deal = new TravelDeal();
        }
        this.deal = deal;
        ettitle.setText(deal.getTitle(ettitle.getText().toString()));
        etdescription.setText(deal.getDescription(etdescription.getText().toString()));
        etprice.setText(deal.getPrice(etprice.getText().toString()));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.savemenu:
                saveDeal();
                Toast.makeText(this, "Deal Saved", Toast.LENGTH_LONG).show();
                clean();
                backToList();
                return true;
            case R.id.deletemenu:
                deleteDeal();
                Toast.makeText(this, "Deal Deleted", Toast.LENGTH_LONG).show();
                backToList();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //save menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);
        if (FirebaseUtil.isAdmin){
            menu.findItem(R.id.deletemenu).setVisible(true);
            menu.findItem(R.id.savemenu).setVisible(true);
            enableEditTexts(true);
            findViewById(R.id.btnimage).setEnabled(true);
        }
        else{
            menu.findItem(R.id.deletemenu).setVisible(false);
            menu.findItem(R.id.savemenu).setVisible(false);
            enableEditTexts(false);
            findViewById(R.id.btnimage).setEnabled(false);
        }

        return true;
    }
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode==PICTURE_RESULT&&resultCode==RESULT_OK){
            Uri imageUri=data.getData();
            StorageReference ref=FirebaseUtil.mStorageRef.child(imageUri.getLastPathSegment());
            ref.putFile(imageUri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                 //check
                   String url=taskSnapshot.getMetadata().toString();
                   String pictureName=taskSnapshot.getStorage().getPath();
                    deal.setImageUrl(url);
                    deal.setImageName(pictureName);
                    Log.d("Url:",url);
                    Log.d("Name",pictureName);
                   showImage(url);
                }
            });
        }
    }

    private void saveDeal() {
        deal.getTitle(ettitle.getText().toString());
        deal.getDescription(etdescription.getText().toString());
        deal.getPrice(etprice.getText().toString());
        if (deal.getId() == null) {
            mDatabaseReference.push().setValue(deal);
        } else {
            mDatabaseReference.child(deal.getId()).setValue(deal);
        }

    }

    private void deleteDeal() {
        if (deal == null) {
            Toast.makeText(this, "save Deal before deleting", Toast.LENGTH_SHORT).show();
            return;
        }
        mDatabaseReference.child(deal.getId()).removeValue();
        Log.d("image name",deal.getImageName());
        if (deal.getImageName() !=null && deal.getImageName().isEmpty()){
            StorageReference picRef=FirebaseUtil.mStorage.getReference().child(deal.getImageName());
            picRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("Delete Image","Image Deleted Successfully");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("Delete Image",e.getMessage());

                }
            });
        }
    }
    private void backToList(){
        Intent intent=new Intent(this,List.class);
        startActivity(intent);
    }

    private void clean(){
        etprice.setText("");
        etdescription.setText("");
        ettitle.setText("");
        ettitle.requestFocus();
    }
    private void enableEditTexts(boolean isEnabled){
        ettitle.setEnabled(isEnabled);
        etprice.setEnabled(isEnabled);
        etdescription.setEnabled(isEnabled);
    }
    private void showImage(String url){
        if (url !=null && url.isEmpty()){
            int width = Resources.getSystem().getDisplayMetrics().widthPixels;
            Picasso.get().load(url).resize(width,width*2/3).centerCrop().into(imageview);
        }
    }

}
