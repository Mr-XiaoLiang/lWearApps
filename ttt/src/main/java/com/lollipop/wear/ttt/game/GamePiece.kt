package com.lollipop.wear.ttt.game

enum class GamePiece(val code: Int) {
    Empty(0),
    X(1),
    O(2);

    companion object {
        fun getByCode(code: Int): GamePiece {
            for (piece in entries) {
                if (piece.code == code) {
                    return piece
                }
            }
            return GamePiece.Empty
        }
    }

}
