package com.example.emma.chatapp;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.sql.Date;
import java.sql.Ref;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Emma on 7/2/17.
 */

public class GroceryListActivity extends AppCompatActivity {

    private List<GroceryItem> mTotalItems;

    private boolean isEmpty(EditText etText) {
        
        // a function to see if an edit text input is empty
        if (etText.getText().toString().trim().length() > 0)
            return false;

        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
     
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_item_new:


                // create a pop up where we add a new item

                final AlertDialog alertDialog = new AlertDialog.Builder(this).create();

                View dialog_layout = getLayoutInflater().inflate(R.layout.dialog_layout, null);
              
                // get the textviews associated with the alert dialog pop up for the new grocery item
                final EditText text1 = (EditText) dialog_layout.findViewById(R.id.text1);
                TextView textview1 = (TextView) dialog_layout.findViewById(R.id.textView);
                final EditText text2 = (EditText) dialog_layout.findViewById(R.id.text2);
                TextView textView2 = (TextView) dialog_layout.findViewById(R.id.textView2);


                alertDialog.setView(dialog_layout);
                alertDialog.setTitle("New Grocery Item");
                alertDialog.setButton(Dialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (isEmpty(text1) | isEmpty(text2)) {


                            Toast.makeText(GroceryListActivity.this, "Please enter an item name and/or price.", Toast.LENGTH_SHORT).show();

                        } else {

                            String userItemName = text1.getText().toString();
                            double userItemPrice = Double.parseDouble(text2.getText().toString());
                            GroceryItem mItem = new GroceryItem();
                            mItem.setTitle(userItemName);
                            mItem.setPrice(userItemPrice);

                            // insert the new item into the database
                            FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
                            DatabaseReference mReference = mDatabase.getReference("items");
                            String id = mReference.push().getKey();
                            
                                                                                                                                            

                            // set value and add
                            mReference.child(id).setValue(mItem);
                            alertDialog.dismiss();

                  

                        }
                    }
                });


                 // show the alert dialog
                alertDialog.show();


                // add item
                break;

            case R.id.my_logout_item:
                
                // nothing


                break;
            default:
                return super.onOptionsItemSelected(item);

        }
        return true;
    }




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grocery_list);


        //set firebaseadapter

        ListView list = (ListView) findViewById(R.id.my_list_view);

        // set it to be empty when the grocery list is empty

        list.setEmptyView(findViewById(R.id.emptyElement));

        // populate the list with the items

        // another way to get a reference to the database
        final DatabaseReference mReference = FirebaseDatabase.getInstance().
                getReferenceFromUrl("https://emmas-projects.firebaseio.com/items");

        final FirebaseListAdapter mAdapter = new FirebaseListAdapter<GroceryItem>(this, GroceryItem.class,
                R.layout.custom_layout, mReference) {
            @Override
            protected void populateView(final View v, final GroceryItem model, final int position) {
                // Get references to the views of message.xml
                TextView userItem = (TextView) v.findViewById(R.id.custom_item_name_view);
                TextView userPrice = (TextView) v.findViewById(R.id.custom_item_price_view);


                // Set their text
                userItem.setText(model.getTitle());
                userPrice.setText("$" + String.format("%.2f", model.getPrice()));


            }

        };


        // set adapter
        list.setAdapter(mAdapter);


        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> adapterView, View view, final int position, long l) {


                // create a pop up where we update a new item

                final AlertDialog alertDialog = new AlertDialog.Builder(GroceryListActivity.this).create();


                View dialog_layout = getLayoutInflater().inflate(R.layout.list_dialog_layout, null);

                final EditText updateItemName = (EditText)dialog_layout.findViewById(R.id.update_item_name);
                final EditText updateItemPrice = (EditText)dialog_layout.findViewById(R.id.update_item_price);


                Button mDeleteButton = (Button) dialog_layout.findViewById(R.id.delete_item);
                mDeleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // delete selected item


                        DatabaseReference itemRef = mAdapter.getRef(position);
                        itemRef.removeValue();

                    }
                });


                Button mUpdateButton = (Button) dialog_layout.findViewById(R.id.update_item);
                mUpdateButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        DatabaseReference itemRef = mAdapter.getRef(position);
                        GroceryItem gItem = new GroceryItem();

                        gItem.setTitle(updateItemName.getText().toString());
                        gItem.setPrice(Double.parseDouble(updateItemPrice.getText().toString()));
                        itemRef.setValue(gItem);

                        // create new item in same position with new price, etc.

                    }
                });


                
                alertDialog.setButton(Dialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {



                             alertDialog.dismiss();



                        }
                    } );






                alertDialog.setView(dialog_layout);
                alertDialog.setTitle("Delete/Update Item");




          
                alertDialog.show();


            }
        });




    }

}







