package com.mrjaffesclass.othello;

import java.util.ArrayList;

/**
 * Your extended player class
 */
public class PitneyNguyenNguyen extends Player {

    /**
     * Constructor
     * @param color Player color: one of Constants. BLACK or Constants. WHITE
     */
    public PitneyNguyenNguyen(int color) {
        super(color);
    }

    /**
     * Determines the player's next move
     * @param board The current state of the game board
     * @return The player's next move
     */
    @Override
    public Position getNextMove(Board board) {
        ArrayList<Position> legalMoves = getLegalMoves(board);

        if (legalMoves.isEmpty()) {
            return null;
        }

        int maxEval = Integer.MIN_VALUE;
        Position bestMove = null;

        for (Position move : legalMoves) {
            int eval = minimax(board, move, 3, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
            if (eval > maxEval) {
                maxEval = eval;
                bestMove = move;
            }
        }

        return bestMove;
    }


    private int minimax(Board board, Position position, int depth, int alpha, int beta, boolean maximizingPlayer) {
        if (depth == 0 || board.countSquares(Constants.EMPTY) == 0) {
            return assessMove(board, position);
        }

        if (maximizingPlayer) {
            int maxEval = -1000;
            ArrayList<Position> list = this.getLegalMoves(board);
            for (Position pos:list) {
                int eval = minimax(board, pos, depth - 1, alpha, beta, false);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) {
                    break;
                }
            }
            return maxEval;
        } else {
            int minEval = 1000;
            ArrayList<Position> list = this.getLegalMoves(board);
            for (Position pos:list) {
                int eval = minimax(board, pos, depth - 1, alpha, beta, true);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) {
                    break;
                }
            }
            return minEval;
        }

    }
    private int assessMove(Board board, Position move) {
        int flippedDiscs = countFlippedDiscs(board, move);
        int boardControl = calculateBoardControl(board, move);

        return (2 * flippedDiscs) + boardControl;
    }

    private int calculateBoardControl(Board board, Position move) {
        int control = 0;

        for (String direction : Directions.getDirections()) {
            Position directionVector = Directions.getVector(direction);
            Position newPosition = move.translate(directionVector);
            if (!newPosition.isOffBoard()) {
                if (board.getSquare(newPosition).getStatus() == Constants.EMPTY) {
                    control++;
                }
            }
        }

        return control;
    }

    private int countFlippedDiscs(Board board, Position move) {
        int color = this.getColor();
        int flippedDiscs = 0;

        // Iterate through directions
        for (String direction : Directions.getDirections()) {
            Position directionVector = Directions.getVector(direction);
            Position newPosition = move.translate(directionVector);

            // Check if the new position is on the board
            if (!newPosition.isOffBoard()) {
                // Check if there is a line of opponent discs to flip
                if (step(board, newPosition, directionVector, 0)) {
                    flippedDiscs++;
                }
            }
        }

        return flippedDiscs;
    }



    public ArrayList<Position> getLegalMoves(Board board) {
        int color = this.getColor();
        ArrayList<Position> list = new ArrayList<>();
        for (int row = 0; row < Constants.SIZE; row++) {
            for (int col = 0; col < Constants.SIZE; col++) {
                if (board.getSquare(this, row, col).getStatus() == Constants.EMPTY) {
                    Position testPosition = new Position(row, col);
                    if (this.isLegalMove(board, testPosition)) {
                        list.add(testPosition);
                    }
                }
            }
        }
        return list;
    }

    private boolean isLegalMove(Board board, Position positionToCheck) {
        for (String direction : Directions.getDirections()) {
            Position directionVector = Directions.getVector(direction);
            if (step(board, positionToCheck, directionVector, 0)) {
                return true;
            }
        }
        return false;
    }

    private boolean step(Board board, Position position, Position direction, int count) {
        Position newPosition = position.translate(direction);
        int color = this.getColor();
        if (newPosition.isOffBoard()) {
            return false;
        } else if (board.getSquare(newPosition).getStatus() == -color) {
            return this.step(board, newPosition, direction, count + 1);
        } else if (board.getSquare(newPosition).getStatus() == color) {
            return count > 0;
        } else {
            return false;
        }
    }
}
