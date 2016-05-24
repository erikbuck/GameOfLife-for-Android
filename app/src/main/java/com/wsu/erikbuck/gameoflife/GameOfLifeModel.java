package com.wsu.erikbuck.gameoflife;

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;

class CellCoordinate {
    public int x;
    public int y;

    public CellCoordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public CellCoordinate(int xy[]) {
        assert 2 == xy.length;
        this.x = xy[0];
        this.y = xy[1];
    }

    public CellCoordinate(final CellCoordinate original, int dx, int dy) {
        assert null != original;
        this.x = original.x + dx;
        this.y = original.y + dy;
    }

    @Override
    public String toString() {
        return "{" + x + ", " + y + "} ";
    }

    @Override
    public int hashCode() {
        int result = this.toString().hashCode();
        //Log.d("hashCode", "" + result);
        return result;
    }
    @Override
    public boolean equals(Object other) {
        boolean result = this.toString().equals(other.toString());
        //Log.d("equals", "" + this + other);
        return result;
    }
}


class LifeCell {
    public enum Status {
        Alive, Spawning, Dead
    }

    public CellCoordinate position;
    public Status status;

    LifeCell(final CellCoordinate position) {
        assert null != position;
        this.position = new CellCoordinate(position.x, position.y);
        this.status = Status.Spawning;
    }

    @Override
    public String toString() {
        return position.toString() + status;
    }

    @Override
    public int hashCode() {
        int result = position.hashCode();
        //Log.d("hashCode", "" + result);
        return result;
    }
    @Override
    public boolean equals(Object other) {
        boolean result = (this.toString().equals(other.toString()));
        //Log.d("equals", "" + this + other);
        return result;
    }

    boolean isSpawning() {
        return  this.status == Status.Spawning;
    }

    private int getCountOfNotSpawningNeighbors(final CellCoordinate pos, GameOfLifeModel theModel) {
        CellCoordinate neighborPositions[] = {
                new CellCoordinate(pos, -1, -1),
                new CellCoordinate(pos,  0, -1),
                new CellCoordinate(pos,  1, -1),
                new CellCoordinate(pos, -1,  0),
                new CellCoordinate(pos,  1,  0),
                new CellCoordinate(pos, -1,  1),
                new CellCoordinate(pos,  0,  1),
                new CellCoordinate(pos,  1,  1),
        };

        int countOfNotSpawnedNeighbors = 0;
        for(CellCoordinate nextPos : neighborPositions) {
            //Log.d("getCountOfNotSpawningNeighbors", position + " " + nextPos);
            //Log.d("containsKey", "" + nextPos + " " + theModel.containsKey(nextPos));
            assert theModel.containsKey(nextPos);

            if(theModel.containsKey(nextPos)) {
                //Log.d("contained", position + " " + nextPos);

                if(!theModel.getCellAt(nextPos).isSpawning()) {
                    countOfNotSpawnedNeighbors += 1;
                }
            }
            else
            {
                //Log.d("NOT PRESENT", position + " " + nextPos);
            }
            //Log.d("getCountOfNotSpawningNeighbors", nextPos + " countOfNotSpawnedNeighbors " + countOfNotSpawnedNeighbors);
        }

        return countOfNotSpawnedNeighbors;
    }

    void update(GameOfLifeModel theModel) {
        assert status == Status.Alive;
        assert theModel.containsKey(position);
        Assert.assertTrue( theModel.containsKey(position) );

        CellCoordinate neighborPositions[] = {
                new CellCoordinate(position, -1, -1),
                new CellCoordinate(position,  0, -1),
                new CellCoordinate(position,  1, -1),
                new CellCoordinate(position, -1,  0),
                new CellCoordinate(position,  1,  0),
                new CellCoordinate(position, -1,  1),
                new CellCoordinate(position,  0,  1),
                new CellCoordinate(position,  1,  1),
        };

        int countOfNotSpawnedNeighbors = getCountOfNotSpawningNeighbors(position, theModel);

        if(2 > countOfNotSpawnedNeighbors) {
            // Each cell with one or no neighbors dies, as if by solitude.
            this.status = Status.Dead;
            //Log.d("2 >", "Dead at " + position);
        }
        else if(3 < countOfNotSpawnedNeighbors)  {
            // Each cell with four or more neighbors dies, as if by overpopulation.
            this.status = Status.Dead;
            //Log.d("3 <", "Dead at " + position);
        }
        else {
            // Each cell with two or three neighbors survives.
            //Log.d("", "Alive at " + position);
        }

        // Each unpopulated cell with three neighbors becomes populated.
            for(CellCoordinate pos : neighborPositions) {
                if(!theModel.containsKey(pos)) {
                    // pos is unpopulated
                    if(3 == getCountOfNotSpawningNeighbors(pos, theModel)) {
                        // pos has exactly 3 neighbors who are not spawning
                        theModel.spawnCellAt(pos);
                        //Log.d("3 ==", "Spawn at " + pos);
                    }
                }
            }
    }
}

/**
 * Created by erik on 5/23/16.
 */
public class GameOfLifeModel {
     private Hashtable<CellCoordinate, LifeCell> mCells;


    public boolean containsKey(CellCoordinate pos) {
        return mCells.containsKey(pos);
    }

    public LifeCell getCellAt(CellCoordinate pos) {
        return mCells.get(pos);
    }

    public void spawnCellAt(final CellCoordinate pos) {
        LifeCell newCell = new LifeCell(pos);
        assert newCell != null;
        mCells.put(pos, newCell);
    }

    GameOfLifeModel() {
        this.mCells = new Hashtable<CellCoordinate, LifeCell>();
    }

    GameOfLifeModel(final CellCoordinate initialPositions[]) {
        this.mCells = new Hashtable<CellCoordinate, LifeCell>();
        for (CellCoordinate initialPosition : initialPositions) {
            this.spawnCellAt(initialPosition);
        }
    }

    GameOfLifeModel(final int initialPositions[][]) {
        this.mCells = new Hashtable<CellCoordinate, LifeCell>();
        for (int pos[] : initialPositions) {
            this.spawnCellAt(new CellCoordinate(pos));
        }
    }

    public void update() {
        //Log.d("", "========= STARTING UPDATE =========");
        Hashtable<CellCoordinate, LifeCell>newHashtable = new Hashtable<CellCoordinate, LifeCell>();

        // Convert spawning cells into alive cells and remove dead cells from game
        for(LifeCell cell : mCells.values()) {
            //Log.d("Before Update", "cell: " + cell);

            if(cell.status == LifeCell.Status.Spawning) {
                cell.status = LifeCell.Status.Alive;
                newHashtable.put(cell.position, cell);
            } else if(cell.status == LifeCell.Status.Dead) {
                //Log.d("*******************", "cell: " + cell);
                assert !newHashtable.containsKey(cell.position);
            } else {
                newHashtable.put(cell.position, cell);
            }

        }

        mCells = newHashtable;
        for(LifeCell cell : new ArrayList<LifeCell>(mCells.values())) {
            assert cell.status == LifeCell.Status.Alive;
            Assert.assertTrue( mCells.containsKey(cell.position) );
            cell.update(this);
        }
    }

    public Collection<LifeCell> getCells() {
        return this.mCells.values();
    }
    public Collection<CellCoordinate> getPositions() {
        return this.mCells.keySet();
    }
}
