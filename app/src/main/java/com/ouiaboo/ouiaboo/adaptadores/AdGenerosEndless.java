package com.ouiaboo.ouiaboo.adaptadores;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ouiaboo.ouiaboo.R;
import com.ouiaboo.ouiaboo.clases.HomeScreenEpi;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 *
 * http://stackoverflow.com/questions/30681905/adding-items-to-endless-scroll-recyclerview-with-progressbar-at-bottom
 */

public class AdGenerosEndless extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<HomeScreenEpi> items;
    private Context context;
    public CustomRecyclerListener customRecyclerListener;
    private int visibleThreshold = 2;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;
    private final int VIEW_PROG = 1;
    private final int VIEW_ITEM = 0;

    public AdGenerosEndless(Context context, @NonNull List<HomeScreenEpi> items, final RecyclerView recyclerView) {
        this.context = context;
        this.items = items;
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager)recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                    if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        //End has been reaches. Do something
                        if (onLoadMoreListener != null) {
                            onLoadMoreListener.onLoadMore();
                        }
                        loading = true;
                    }
                }
            });

        }
    }

    public class GenerosEndlessHolder extends RecyclerView.ViewHolder implements OnClickListener {
        public TextView nombre;
        public TextView informacion;
        public ImageView preview;

        public GenerosEndlessHolder(View itemLayoutView) {
            super(itemLayoutView);
            nombre = (TextView)itemLayoutView.findViewById(R.id.nombre_flv);
            informacion = (TextView)itemLayoutView.findViewById(R.id.informacion_flv);
            preview = (ImageView)itemLayoutView.findViewById(R.id.preview_flv);
            itemLayoutView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (customRecyclerListener != null) {
                customRecyclerListener.customClickListener(v, getLayoutPosition());
            }
        }

    }

    public class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View itemLayoutView) {
            super(itemLayoutView);

            progressBar = (ProgressBar)itemLayoutView.findViewById(R.id.progressBar);
        }
    }

    public static interface CustomRecyclerListener {
        public void customClickListener(View v, int position);
    }

    public void setClickListener(CustomRecyclerListener customRecyclerListener){
        this.customRecyclerListener = customRecyclerListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.busqueda_items_flv, parent, false);
            vh = new GenerosEndlessHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.progress_bar, parent, false);
            vh = new ProgressViewHolder(v);
        }

        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof GenerosEndlessHolder) {
            ((GenerosEndlessHolder)holder).nombre.setText(Html.fromHtml(items.get(position).getNombre()));
            ((GenerosEndlessHolder)holder).informacion.setText(Html.fromHtml(items.get(position).getInformacion()));
            Picasso.with(context).load(items.get(position).getPreview()).resize(200, 250).into(((GenerosEndlessHolder)holder).preview);
        } else {
            ((ProgressViewHolder)holder).progressBar.setIndeterminate(true);
        }

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    public void setLoaded() {
        loading = false;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

}
