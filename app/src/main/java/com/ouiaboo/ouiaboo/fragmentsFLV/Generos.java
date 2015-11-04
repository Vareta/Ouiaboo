package com.ouiaboo.ouiaboo.fragmentsFLV;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ouiaboo.ouiaboo.R;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Generos.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class Generos extends android.support.v4.app.Fragment {

    private OnFragmentInteractionListener mListener;

    public Generos() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View convertView = inflater.inflate(R.layout.fragment_generos, container, false);
        getActivity().setTitle(R.string.generos_drawer_layout);
        ImageView img = (ImageView)convertView.findViewById(R.id.imageView);
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES) + "/Ouiaboo/Thumbnails/" + "hola.jpg";
        Uri uri = Uri.parse(path);
        File file = new File(path);
        Log.d("PATH", path);
        Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
       // img.setImageBitmap(myBitmap);
        Picasso.with(getActivity()).load(file).resize(200, 250).into(img);
        return convertView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
