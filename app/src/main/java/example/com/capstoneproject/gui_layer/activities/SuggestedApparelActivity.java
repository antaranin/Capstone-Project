package example.com.capstoneproject.gui_layer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import example.com.capstoneproject.R;

public class SuggestedApparelActivity extends AppCompatActivity
{
    @BindView(R.id.current_weather_iv)
    ImageView weatherIv;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_apparel);

        ButterKnife.bind(this);

        Toolbar toolbar = ButterKnife.findById(this, R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.suggested_apparel_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_item_list:
                goToItemList();
                return true;
            case R.id.action_settings:
                return true;
            default:
                return false;
        }
    }

    private void goToItemList()
    {
        Intent i = new Intent(this, ClothingActivity.class);
        startActivity(i);
    }
}
