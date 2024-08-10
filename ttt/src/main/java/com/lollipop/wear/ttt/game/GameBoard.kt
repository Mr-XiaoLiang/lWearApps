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
class GameBoard : GameBoardProvider {

    companion object {
        const val BOARD_SIZE = 3

        private fun newMap(): Array<Array<GamePiece>> {
            return Array(BOARD_SIZE) { Array(BOARD_SIZE) { GamePiece.Empty } }
        }

        private fun parseMap(data: Int, map: Array<Array<GamePiece>>): Array<Array<GamePiece>> {
            var code = data
            for (y in map.indices) {
                val line = map[y]
                for (x in line.indices) {
                    line[x] = GamePiece.getByCode(code % 10)
                    code /= 10
                }
            }
            return map
        }

        private fun formatMap(map: Array<Array<GamePiece>>): Int {
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

    }

    internal val map = newMap()

    fun get(x: Int, y: Int): GamePiece {
        if (x < 0 || x >= BOARD_SIZE || y < 0 || y >= BOARD_SIZE) {
            return GamePiece.Empty
        }
        return map[y][x]
    }

    fun put(x: Int, y: Int, piece: GamePiece) {
        if (x < 0 || x >= BOARD_SIZE || y < 0 || y >= BOARD_SIZE) {
            return
        }
        map[y][x] = piece
    }

    fun format(): Int {
        return formatMap(map)
    }

    fun load(data: Int) {
        parseMap(data, map)
    }

    override fun snapshot(): GameBoardSnapshot {
        val newMap = newMap()
        for (y in map.indices) {
            val line = map[y]
            for (x in line.indices) {
                newMap[y][x] = map[y][x]
            }
        }
        return GameBoardSnapshot(newMap)
    }
}

interface GameBoardProvider {

    fun snapshot(): GameBoardSnapshot

}

class GameBoardSnapshot(
    private val map: Array<Array<GamePiece>>
) {

    companion object {
        val EMPTY = GameBoardSnapshot(emptyArray())
    }

    fun get(x: Int, y: Int): GamePiece {
        if (x !in map.indices || y !in map[x].indices) {
            return GamePiece.Empty
        }
        return map[x][y]
    }
}
