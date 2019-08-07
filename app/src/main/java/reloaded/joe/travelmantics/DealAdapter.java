package reloaded.joe.travelmantics;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
public class DealAdapter extends RecyclerView.Adapter<DealAdapter.DealViewHolder> {
    ArrayList<TravelDeal> deals;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildListener;
    private ImageView imageDeal;
    private EditText ettitle;
   public DealAdapter(){
       mFirebaseDatabase=FirebaseUtil.mFirebaseDatabase;
      // FirebaseUtil.openFbReference("traveldeal");
       mDatabaseReference=FirebaseUtil.mDatabaseReference;
       deals=FirebaseUtil.mDeals;
       mChildListener=new ChildEventListener() {
           @Override
           public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
               TravelDeal td = dataSnapshot.getValue(TravelDeal.class);
               Log.d("Deal:",td.getTitle(ettitle.getText().toString()));
               td.setId(dataSnapshot.getKey());
               deals.add(td);
               notifyItemInserted(deals.size()-1);
           }

           @Override
           public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

           }

           @Override
           public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

           }

           @Override
           public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       };
      mDatabaseReference.addChildEventListener(mChildListener);
   }
    @NonNull
    @Override
    public DealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context=parent.getContext();
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.rv_row,parent,false);
        return new DealViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DealViewHolder holder, int position) {
        TravelDeal deal=deals.get(position);
        holder.bind(deal);
    }

    @Override
    public int getItemCount() {
        return deals.size() ;
    }

    public class DealViewHolder extends RecyclerView.ViewHolder
    implements View.OnClickListener{
      EditText etTitle;
      EditText etDescription;
      EditText etPrice;

        public DealViewHolder(@NonNull View itemView) {
            super(itemView);
            ettitle=itemView.findViewById(R.id.ettitle);
            etDescription=itemView.findViewById(R.id.etdescription);
            etPrice=itemView.findViewById(R.id.etprice);
            imageDeal=itemView.findViewById(R.id.imageDeal);
            itemView.setOnClickListener(this);
        }
        public void bind(TravelDeal deal){
            ettitle.setText(deal.getTitle(ettitle.getText().toString()));
            etDescription.setText(deal.getDescription(etDescription.getText().toString()));
            etPrice.setText(deal.getPrice(etPrice.getText().toString()));
            showImage(deal.getImageUrl());
        }

        @Override
        public void onClick(View v) {
            int position=getAdapterPosition();
            Log.d("click",String.valueOf(position));
            TravelDeal selectedDeal=deals.get(position);
            Intent intent=new Intent(itemView.getContext(),Deal.class);
            intent.putExtra("Deal",selectedDeal);
            itemView.getContext().startActivity(intent);


        }
        private void showImage(String url){
            if (url !=null&&url.isEmpty()){
                Picasso.get().load(url).resize(160,160).centerCrop().into(imageDeal);
            }
        }
    }
}
