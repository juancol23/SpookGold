package com.valdemar.spook.holder;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.valdemar.spook.R;

public class CategoryViewHolderPro extends RecyclerView.ViewHolder{

    public View mViewStructure_h;
    public TextView mItem_recycler_structure_title_h;
    public TextView mItem_recycler_structure_category_h;
    public TextView mItem_recycler_structure_send_by_h;
    public ImageView mPost_image_h;
    public ImageView mReaction_icon;

    public ImageView reactionImage; // txt_nro_reactions
    public ImageView meGustaImage; // txt_nro_reactions
    public ImageView meEncantaImage; // txt_nro_reactions
    public  ImageView meAsombraImage; // txt_nro_reactions
    public  TextView nroReactions;



    ImageButton mfavoriteBtn;

    public CategoryViewHolderPro(View itemView) {
        super(itemView);
        mViewStructure_h = itemView ;
        mReaction_icon = itemView.findViewById(R.id.reaction_icon);
        mItem_recycler_structure_title_h = itemView.findViewById(R.id.title_album);


        reactionImage = itemView.findViewById(R.id.reaction_icon);
        nroReactions = itemView.findViewById(R.id.txt_nro_reactions);
        meGustaImage = itemView.findViewById(R.id.me_gusta_icon);
        meEncantaImage = itemView.findViewById(R.id.me_encanta_icon);
        meAsombraImage = itemView.findViewById(R.id.me_asombra_icon);

    }

    public void setTitle(String title){
        //mItem_recycler_structure_title.setTypeface(Pacifico);
        mItem_recycler_structure_title_h = mViewStructure_h.findViewById(R.id.title_album);
        mItem_recycler_structure_title_h.setText(title);
    }
    public void setSendBy(String title){
        //mItem_recycler_structure_title.setTypeface(Pacifico);
        mItem_recycler_structure_send_by_h = mViewStructure_h.findViewById(R.id.send_by_album);
        mItem_recycler_structure_send_by_h.setText(title);
    }

    public void setImage(Context context, String image){
        mPost_image_h = mViewStructure_h.findViewById(R.id.thumbnail);


        Glide.with(context)
                .load(image)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mPost_image_h);

    }
    public void getImageReaccion(View holder){


    }



}
