package com.kyle.lefebvre.cardholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cooltechworks.creditcarddesign.CreditCardView;

import java.util.ArrayList;

/**
 * Created by kyle on 9/6/2017.
 */

public class cardAdapter extends RecyclerView.Adapter<cardAdapter.CardViewHolder> {

    public int facing = 0; // 1 = back
    private ArrayList<Card> mCards;
    private Context mContext;



    public cardAdapter(Context mContext,ArrayList<Card> mCards) {
        this.mCards = mCards;
        this.mContext = mContext;
    }

    public class CardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public CreditCardView mCardView;


        public CardViewHolder(View view) {
            super(view);

            mCardView = (CreditCardView) view.findViewById(R.id.card);
            mCardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(facing == 0){
                mCardView.showBack();
                facing = 1;
            }else{
                mCardView.showFront();
                facing = 0;
            }

        }
    }




    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // inflating recycler item view
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card, parent, false);
        return new CardViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CardViewHolder holder, int position) {
        holder.mCardView.setCardNumber(mCards.get(position).getNumber());
        holder.mCardView.setCardHolderName(mCards.get(position).getName());
        holder.mCardView.setCVV(mCards.get(position).getCvv());
        holder.mCardView.setCardExpiry(mCards.get(position).getExpiry());

    }

    @Override
    public int getItemCount() {
        return mCards.size();
    }


}
