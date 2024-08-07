package com.lollipop.wear.ttt.game

/**
 * 棋盘
 * [
 *  [0, 1, 2],
 *  [3, 4, 5],
 *  [6, 7, 8]
 * ]
 *
 * format = 876543210
 */
class GameBoard {

    val map = Array(3) { Array(3) { GamePiece.Empty } }

    fun get(x: Int, y: Int): GamePiece {
        if (x !in map.indices || y !in map[x].indices) {
            return GamePiece.Empty
        }
        return map[x][y]
    }

    fun put(x: Int, y: Int, piece: GamePiece) {
        if (x !in map.indices || y !in map[x].indices) {
            return
        }
        map[x][y] = piece
    }

    fun format(): Int {
        var position = 1
        var result = 0
        map.forEach { line ->
            line.forEach { piece ->
                result += piece.code * position
                position *= 10
            }
        }
        return result
    }

    fun load(data: Int) {
        var code = data
        for (y in map.indices) {
            val line = map[y]
            for (x in line.indices) {
                line[x] = GamePiece.getByCode(code % 10)
                code /= 10
            }
        }
    }
}