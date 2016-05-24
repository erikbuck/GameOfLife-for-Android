package com.wsu.erikbuck.gameoflife;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.HashMap;

public class GameOfLifeActivity extends AppCompatActivity {

    static final int gliderCellPositions[][] =  {
            {1,0}, {2,1}, {2,2}, {1,2}, {0,2},
    };

    static final int smallExplosionsCellPositions[][] =  {
            {0,1}, {0,2}, {1,0}, {1,1}, {1,3}, {2,1}, {2,2},
    };
    static final int explosionsCellPositions[][] =  {
            {0,0}, {0,1}, {0,2}, {0,3}, {0,4}, {2,0}, {2,4}, {4,0}, {4,1}, {4,2}, {4,3}, {4,4},
    };

    static final int fishCellPositions[][] = {
            {0,1}, {0,3}, {1,0}, {2,0}, {3,0}, {3,3}, {4,0}, {4,1}, {4,2}
    };

    static final int tenInARowCellPositions[][] = {
            {-4,0}, {-3,0}, {-2,0}, {-1,0}, {0,0}, {1,0}, {2,0}, {3,0}, {4,0}, {5,0},
    };

    static final int pumpCellPositions[][] = {
            {0,3}, {0,4}, {0,5}, {1,0}, {1,1}, {1,5}, {2,0}, {2,1}, {2,2}, {2,3}, {2,4}, {4,0}, {4,1}, {4,2}, {4,3}, {4,4}, {5,0}, {5,1}, {5,5}, {6,3}, {6,4}, {6,5},
    };

    static final int shooterCellPositions[][] = {
            {0,2}, {0,3}, {1,2}, {1,3}, {8,3}, {8,4}, {9,2}, {9,4}, {10,2}, {10,3}, {16,4}, {16,5}, {16,6}, {17,4}, {18,5}, {22,1}, {22,2}, {23,0}, {23,2}, {24,0}, {24,1}, {24,12}, {24,13}, {25,12}, {25,14}, {26,12}, {34,0}, {34,1}, {35,0}, {35,1}, {35,7}, {35,8}, {35,9}, {36,7}, {37,8},
    };

    static final int clearCellPositions[][] = {
    };

    static final HashMap<Integer, int [][]>initialCellPositions() {
        final HashMap<Integer, int [][]> map = new HashMap<>();
        map.put(R.id.action_clear, clearCellPositions);
        map.put(R.id.action_glider, gliderCellPositions);
        map.put(R.id.action_small_explosion, smallExplosionsCellPositions);
        map.put(R.id.action_explosion, explosionsCellPositions);
        map.put(R.id.action_fish, fishCellPositions);
        map.put(R.id.action_ten_in_a_row, tenInARowCellPositions);
        map.put(R.id.action_pump, pumpCellPositions);
        map.put(R.id.action_shooter, shooterCellPositions);
        return map;
    };

    private void setModel(GameOfLifeModel model) {
        assert(null != model);
        GameOfLifeView gameView = (GameOfLifeView)findViewById(R.id.gameoflife);
        assert(null != gameView);
        gameView.setModel(model);
        gameView.center();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_of_life);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (null != fab) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    GameOfLifeView gameView = (GameOfLifeView)findViewById(R.id.gameoflife);
                    assert(null != gameView);
                    gameView.toggleIsRunning();
                    if(gameView.getIsRunning()) {
                        fab.setImageResource(android.R.drawable.ic_media_pause);
                    } else {
                        fab.setImageResource(android.R.drawable.ic_media_play);
                    }
                }
            });

         }
        setModel(new GameOfLifeModel(tenInARowCellPositions));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game_of_life, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(GameOfLifeActivity.initialCellPositions().containsKey(id))
        {
            setModel(new GameOfLifeModel(GameOfLifeActivity.initialCellPositions().get(id)));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
