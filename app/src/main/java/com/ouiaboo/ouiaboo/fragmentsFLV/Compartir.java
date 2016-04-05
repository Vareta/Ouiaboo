package com.ouiaboo.ouiaboo.fragmentsFLV;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.widget.ShareDialog;
import com.ouiaboo.ouiaboo.AnalyticsApplication;
import com.ouiaboo.ouiaboo.R;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Compartir.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class Compartir extends Fragment {
    private ImageView facebookBtn;
    private ImageView twitterBtn;
    private ImageView otrosBtn;
    private  CallbackManager callbackManager;
    private ShareDialog shareDialog;

    private OnFragmentInteractionListener mListener;

    public Compartir() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View convertView = inflater.inflate(R.layout.fragment_compartir, container, false);
        getActivity().setTitle(R.string.compartir_drawer_layout);
        facebookBtn = (ImageView)convertView.findViewById(R.id.facebookButton);
        twitterBtn = (ImageView)convertView.findViewById(R.id.twitterButton);
        otrosBtn = (ImageView) convertView.findViewById(R.id.otroskButton);

        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Log.d("SUCCESS", "RESULTO");
                AnalyticsApplication.getInstance().trackEvent("Compartir", "Exito", "Facebook");
            }

            @Override
            public void onCancel() {
                Log.d("CANCEL", "CANCEL");
                AnalyticsApplication.getInstance().trackEvent("Compartir", "Cancelado", "Facebook");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("ERROR", "ERROR");
                AnalyticsApplication.getInstance().trackEvent("Compartir", "Error", "Facebook");
            }
        });
        facebookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareLinkContent content = new ShareLinkContent.Builder()
                        .setImageUrl(Uri.parse("http://i.imgur.com/cMD1lVy.png"))
                        .setContentTitle("Ouiaboo")
                        .setContentDescription(getResources().getString(R.string.contentTitleFacebook_Compartir))
                        .setContentUrl(Uri.parse("https://ouiaboo.wordpress.com/"))
                        .build();
                shareDialog.show(content);
            }
        });

        twitterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Create intent using ACTION_VIEW and a normal Twitter url:
                String tweetUrl = String.format("https://twitter.com/intent/tweet?text=%s&url=%s",
                        "Viendo anime desde mi Android vía Ouiaboo", "http://ouiaboo.wordpress.com/");
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(tweetUrl));

                // Narrow down to official Twitter app, if available:
                List<ResolveInfo> matches = getActivity().getPackageManager().queryIntentActivities(intent, 0);
                for (ResolveInfo info : matches) {
                    if (info.activityInfo.packageName.toLowerCase().startsWith("com.twitter")) {
                        intent.setPackage(info.activityInfo.packageName);
                    }
                }

                startActivity(intent);
            }
        });

        otrosBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Viendo anime desde mi A ndroid vía Ouiaboo http://ouiaboo.wordpress.com/");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });

        return convertView;
    }

    //Activity result facebook
    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        AnalyticsApplication.getInstance().trackScreenView("Compartir");
    }
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
