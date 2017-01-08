package fi.reuna.poko;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BarcodeListActivity extends AppCompatActivity {

    private RecyclerView barcodesView;
    private BarcodesAdapter barcodesAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private FloatingActionButton newBarcodeButton;
    boolean reloadOnStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        barcodesView = (RecyclerView) findViewById(R.id.barcodes_view);
        assert barcodesView != null;
        barcodesView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        barcodesView.setLayoutManager(layoutManager);

        barcodesAdapter = new BarcodesAdapter();
        barcodesView.setAdapter(barcodesAdapter);

        newBarcodeButton = (FloatingActionButton) findViewById(R.id.new_barcode_button);
        newBarcodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNewBarcodeDialog(null);
            }
        });

        Intent intent = getIntent();

        if (savedInstanceState == null && intent != null) {
            checkIntent(intent);
        }

        ItemTouchHelper.Callback callback = new ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            private ItemTouchDrawer drawer;

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                Barcode barcode = ((BarcodeViewHolder) viewHolder).barcode;

                if (direction == ItemTouchHelper.RIGHT) {
                    PokoApplication.startTrackPackageActivity(BarcodeListActivity.this, barcode);

                    // Swipe removes the item from the adapter -> reload the list to reattach the item.
                    reloadOnStart = true;

                } else {
                    Log.d("remove %s", barcode.getCode());
                    BarcodeManager.instance.removeBarcode(barcode);
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

                if (drawer == null) {
                    drawer = new ItemTouchDrawer(BarcodeListActivity.this,
                            R.drawable.ic_delete_white_24dp, R.drawable.ic_open_in_new_white_24dp,
                            R.color.dismiss, R.color.tracking, null);
                }

                drawer.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

            @Override
            public float getSwipeEscapeVelocity(float defaultValue) {
                // Make the swipes harder to avoid accidental ones.
                return defaultValue * 6;
            }

            @Override
            public float getSwipeThreshold(RecyclerView.ViewHolder viewHolder) {
                return 0.75f;
            }
        };

        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(barcodesView);

        PokoApplication.getInstance().getBus().register(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        checkIntent(intent);
    }

    private void checkIntent(Intent intent) {
        String input = intent.getStringExtra(Intent.EXTRA_TEXT);

        if (input != null) {
            String code = extractCode(input);
            showNewBarcodeDialog(code == null ? input : code);
        }
    }

    void showNewBarcodeDialog(String code) {
        NewBarcodeDialog.newInstance(code).show(getFragmentManager(), "newBarcodeDialog");
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (reloadOnStart) {
            reloadOnStart = false;
            barcodesAdapter.notifyDataSetChanged();
        }
    }

    @Subscribe
    public void barcodesChanged(BarcodeManager.BarcodeEvent event) {

        if (event.type == BarcodeManager.BarcodeEventType.Added) {
            barcodesAdapter.notifyItemInserted(event.position);
        } else {
            barcodesAdapter.notifyItemRemoved(event.position);

            final Barcode barcode = event.barcode;
            Snackbar undoer = Snackbar.make(barcodesView, R.string.removed_text, Snackbar.LENGTH_LONG);
            undoer.setActionTextColor(getColor(R.color.snackAction));
            undoer.setAction(R.string.removed_undo, new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    BarcodeManager.instance.addBarcode(barcode);
                }
            });

            undoer.show();
        }
    }

    private static class BarcodeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView nameView;
        private TextView codeView;
        private TextView dateView;
        private Barcode barcode;

        public BarcodeViewHolder(View barcodeView) {
            super(barcodeView);
            nameView = (TextView) barcodeView.findViewById(R.id.nameView);
            codeView = (TextView) barcodeView.findViewById(R.id.codeView);
            dateView = (TextView) barcodeView.findViewById(R.id.dateView);
            barcodeView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Context ctx = view.getContext();
            Intent intent = new Intent(ctx, DetailsActivity.class);
            intent.putExtra(DetailsActivity.EXTRA_BARCODE, barcode);
            ctx.startActivity(intent);
        }

        public void update(Barcode barcode) {
            this.barcode = barcode;
            nameView.setText(barcode.getName());
            codeView.setText(barcode.getCode());
            dateView.setText(DateFormat.getDateFormat(dateView.getContext()).format(barcode.getCreated()));
        }
    }

    private static class BarcodesAdapter extends RecyclerView.Adapter<BarcodeViewHolder> {

        private List<Barcode> barcodes;

        public BarcodesAdapter() {
            this.barcodes = BarcodeManager.instance.getBarcodes();
        }

        @Override
        public BarcodeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.barcode_view, parent, false);
            return new BarcodeViewHolder(v);
        }

        @Override
        public void onBindViewHolder(BarcodeViewHolder holder, int position) {
            Barcode barcode = barcodes.get(position);
            holder.update(barcode);
        }

        @Override
        public int getItemCount() {
            return barcodes.size();
        }
    }

    private static String extractCode(String text) {
        // Space character and characters -+$/% are part of the valid character set, but since this method is meant
        // for reading from a generic text string such as a SMS message that contains proper sentences leave those
        // special characters out (especially space and dot).
        Matcher matcher = Pattern.compile("[A-Z0-9]+").matcher(text);
        String longestMatch = null;

        // Figure out the longest valid string as it is most likely the code of interest.
        while (matcher.find()) {
            String match = matcher.group(0);

            if (longestMatch == null || match.length() > longestMatch.length()) {
                longestMatch = match;
            }
        }

        return longestMatch;
    }
}
