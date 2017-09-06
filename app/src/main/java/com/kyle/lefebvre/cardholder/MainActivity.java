package com.kyle.lefebvre.cardholder;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.cooltechworks.creditcarddesign.CardEditActivity;
import com.cooltechworks.creditcarddesign.CreditCardUtils;
import com.geniusforapp.fancydialog.FancyAlertDialog;
import com.robertlevonyan.views.customfloatingactionbutton.CustomFloatingActionButton;
import com.robertlevonyan.views.customfloatingactionbutton.OnFabClickListener;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity {

    private CustomFloatingActionButton button;
    private RecyclerView mRecyclerView;
    private Context mContext;
    private ArrayList<Card> mCards;
    private cardAdapter mCardAdapter;
    private DbHelper databaseHelper;
    private String cardNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        initViews();
        initObjects();




    }

    private void initObjects() {
        mCards = new ArrayList<>();
        mCardAdapter = new cardAdapter(this,mCards);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                mRecyclerView, new ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                //Values are passing to activity & to fragment as well

            }

            @Override
            public void onLongClick(View view, final int position) {

                FancyAlertDialog.Builder alert = new FancyAlertDialog.Builder(MainActivity.this)
                        .setTextTitle("Delete/Edit Card")
                        .setTitleColor(R.color.colorPrimary)
                        .setBody("\n")
                        .setNegativeColor(R.color.OrangeRed)
                        .setNegativeButtonText("Delete")
                        .setOnNegativeClicked(new FancyAlertDialog.OnNegativeClicked() {
                            @Override
                            public void OnClick(View view, Dialog dialog) {
                                dialog.dismiss();
                                String num = mCards.get(position).getNumber();
                                databaseHelper.deleteRowFromTable(num);
                                getDataFromSQLite();

                            }
                        })
                        .setPositiveButtonText("Edit")
                        .setPositiveColor(R.color.colorPrimaryDark)
                        .setOnPositiveClicked(new FancyAlertDialog.OnPositiveClicked() {
                            @Override
                            public void OnClick(View view, Dialog dialog) {


                                Intent i = new Intent(MainActivity.this,CardEditActivity.class);
                                i.putExtra(CreditCardUtils.EXTRA_CARD_HOLDER_NAME,mCards.get(position).getName());
                                i.putExtra(CreditCardUtils.EXTRA_CARD_CVV,mCards.get(position).getCvv());
                                i.putExtra(CreditCardUtils.EXTRA_CARD_EXPIRY,mCards.get(position).getExpiry());
                                cardNumber = mCards.get(position).getNumber();
                                i.putExtra(CreditCardUtils.EXTRA_CARD_NUMBER,cardNumber);
                                i.putExtra(CreditCardUtils.EXTRA_CARD_SHOW_CARD_SIDE, CreditCardUtils.CARD_SIDE_BACK);
                                i.putExtra(CreditCardUtils.EXTRA_VALIDATE_EXPIRY_DATE, true);
                                i.putExtra(CreditCardUtils.EXTRA_ENTRY_START_PAGE, CreditCardUtils.CARD_NUMBER_PAGE);
                                startActivityForResult(i,2);
                                dialog.dismiss();

                            }
                        })
                       /* .setAutoHide(true)*/
                        .build();
                alert.show();

            }
        }));


        mRecyclerView.setAdapter(mCardAdapter);
        databaseHelper = new DbHelper(this);

        getDataFromSQLite();

    }

    private void initViews(){
        button = findViewById(R.id.button);
        mRecyclerView = findViewById(R.id.recyclerview);

        button.setOnFabClickListener(new OnFabClickListener() {
            @Override
            public void onFabClick(View v) {
                Intent i = new Intent(MainActivity.this, CardEditActivity.class);
                startActivityForResult(i,1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == 1) {
                Card c = new Card();
                String name = data.getStringExtra(CreditCardUtils.EXTRA_CARD_HOLDER_NAME);
                String cardNumber = data.getStringExtra(CreditCardUtils.EXTRA_CARD_NUMBER);
                String expiry = data.getStringExtra(CreditCardUtils.EXTRA_CARD_EXPIRY);
                String cvv = data.getStringExtra(CreditCardUtils.EXTRA_CARD_CVV);
                c.setExpiry(expiry);
                c.setName(name);
                c.setNumber(cardNumber);
                c.setCvv(cvv);

                if (databaseHelper.checkAlreadyExist(cardNumber)) {
                    Toasty.error(getApplicationContext(), "Card already Exists", Toast.LENGTH_LONG).show();
                } else {
                    databaseHelper.addCard(c);
                }


                getDataFromSQLite();


            }

            if(requestCode == 2){
                String name = data.getStringExtra(CreditCardUtils.EXTRA_CARD_HOLDER_NAME);
                String num = data.getStringExtra(CreditCardUtils.EXTRA_CARD_NUMBER);
                String expiry = data.getStringExtra(CreditCardUtils.EXTRA_CARD_EXPIRY);
                String cvv = data.getStringExtra(CreditCardUtils.EXTRA_CARD_CVV);


                databaseHelper.updateCard(cardNumber,num,cvv,name,expiry);
                getDataFromSQLite();

            }

        }
    }

    private void getDataFromSQLite() {
        // AsyncTask is used that SQLite operation not blocks the UI Thread.
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                mCards.clear();
                mCards.addAll(databaseHelper.getAllcard());

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                mCardAdapter.notifyDataSetChanged();
            }
        }.execute();
    }

    public static interface ClickListener{
        public void onClick(View view,int position);
        public void onLongClick(View view,int position);
    }

    class RecyclerTouchListener implements RecyclerView.OnItemTouchListener{

        private ClickListener clicklistener;
        private GestureDetector gestureDetector;

        public RecyclerTouchListener(Context context, final RecyclerView recycleView, final ClickListener clicklistener){

            this.clicklistener=clicklistener;
            gestureDetector=new GestureDetector(context,new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child=recycleView.findChildViewUnder(e.getX(),e.getY());
                    if(child!=null && clicklistener!=null){
                        clicklistener.onLongClick(child,recycleView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child=rv.findChildViewUnder(e.getX(),e.getY());
            if(child!=null && clicklistener!=null && gestureDetector.onTouchEvent(e)){
                clicklistener.onClick(child,rv.getChildAdapterPosition(child));
            }

            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }
}
