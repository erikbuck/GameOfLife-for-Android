package com.wsu.erikbuck.gameoflife;

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;


/**
 * Created by erik on 5/23/16.
 */
public class GameOfLifeModel {
    private Hashtable<CellCoordinate, LifeCell> mCells;

    private boolean containsKey(CellCoordinate pos) {
        return mCells.containsKey(pos);
    }

    private LifeCell getCellAt(CellCoordinate pos) {
        return mCells.get(pos);
    }

    private void spawnCellAt(final CellCoordinate pos) {
        LifeCell newCell = new LifeCell(pos);
        assert newCell != null;
        mCells.put(pos, newCell);
    }

    public class CellCoordinate {
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
        public int hashCode() { return this.toString().hashCode(); }

        @Override
        public boolean equals(Object other) { return this.toString().equals(other.toString()); }
    }

    private enum Status { Alive, Spawning, Dead }

    private class LifeCell {
        private CellCoordinate position;
        private Status status;

        public LifeCell(final CellCoordinate position) {
            assert null != position;
            this.position = new CellCoordinate(position.x, position.y);
            this.status = Status.Spawning;
        }

        @Override
        public String toString() {
            return position.toString() + status;
        }

        @Override
        public int hashCode() { return position.hashCode(); }

        @Override
        public boolean equals(Object other) { return this.toString().equals(other.toString()); }

        private boolean isSpawning() {
            return this.status == Status.Spawning;
        }

        private CellCoordinate [] getNeighbors(CellCoordinate pos) {
            CellCoordinate neighborPositions[] = {
                    new CellCoordinate(pos, -1, -1),
                    new CellCoordinate(pos, 0, -1),
                    new CellCoordinate(pos, 1, -1),
                    new CellCoordinate(pos, -1, 0),
                    new CellCoordinate(pos, 1, 0),
                    new CellCoordinate(pos, -1, 1),
                    new CellCoordinate(pos, 0, 1),
                    new CellCoordinate(pos, 1, 1),
            };
            return neighborPositions;
        }

        private int getCountOfNotSpawningNeighbors(final CellCoordinate pos, GameOfLifeModel theModel) {
            CellCoordinate neighborPositions[] = getNeighbors(pos);

            int countOfNotSpawnedNeighbors = 0;
            for (CellCoordinate nextPos : neighborPositions) {
                assert theModel.containsKey(nextPos);

                if (theModel.containsKey(nextPos) && !theModel.getCellAt(nextPos).isSpawning()) {
                    countOfNotSpawnedNeighbors += 1;
                 }
            }

            return countOfNotSpawnedNeighbors;
        }

        private void update(GameOfLifeModel theModel) {
            assert status == Status.Alive;
            assert theModel.containsKey(position);
            Assert.assertTrue(theModel.containsKey(position));

            CellCoordinate neighborPositions[] = getNeighbors(position);
            int countOfNotSpawnedNeighbors = getCountOfNotSpawningNeighbors(position, theModel);

            if (2 > countOfNotSpawnedNeighbors) {
                // Each cell with one or no neighbors dies, as if by solitude.
                this.status = Status.Dead;
            } else if (3 < countOfNotSpawnedNeighbors) {
                // Each cell with four or more neighbors dies, as if by overpopulation.
                this.status = Status.Dead;
            }

            // Each unpopulated cell with three neighbors becomes populated.
            for (CellCoordinate pos : neighborPositions) {
                if (!theModel.containsKey(pos)) {
                    // pos is unpopulated
                    if (3 == getCountOfNotSpawningNeighbors(pos, theModel)) {
                        // pos has exactly 3 neighbors who are not spawning
                        theModel.spawnCellAt(pos);
                    }
                }
            }
        }
    }

    GameOfLifeModel(final int initialPositions[][]) {
        this.mCells = new Hashtable<CellCoordinate, LifeCell>();
        for (int pos[] : initialPositions) {
            this.spawnCellAt(new CellCoordinate(pos));
        }
    }

    public void update() {
        Hashtable<CellCoordinate, LifeCell> newHashtable = new Hashtable<CellCoordinate, LifeCell>();

        // Convert spawning cells into alive cells and remove dead cells from game
        for (LifeCell cell : mCells.values()) {
            if (cell.status == Status.Dead) {
                assert !newHashtable.containsKey(cell.position);
            } else {
                cell.status = Status.Alive;
                newHashtable.put(cell.position, cell);
            }
        }

        mCells = newHashtable;

        // Update all the cells
        for (LifeCell cell : new ArrayList<LifeCell>(mCells.values())) {
            assert cell.status == Status.Alive;
            Assert.assertTrue(mCells.containsKey(cell.position));
            cell.update(this);
        }
    }

    public Collection<CellCoordinate> getPositions() {
        return this.mCells.keySet();
    }
}
