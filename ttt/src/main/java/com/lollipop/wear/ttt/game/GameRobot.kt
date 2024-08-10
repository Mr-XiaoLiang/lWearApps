package com.lollipop.wear.ttt.game

import kotlin.random.Random

/**
 * 游戏的机器人
 * 它其实并不具备智能
 * 只是简单的根据当前的棋盘状态进行判断最合理的落子
 * 这里我使用评分模式，对每一条线的落子进行评分，最终评分最高的落子就是机器人落子的位置
 * 它与裁判的区别在于：
 * 1. 它需要指定自己的棋子类型，以此区分对家与自己
 * 2. 它需要对落子的位置做优先级判断，也就是评分是有不同权重的
 */
object GameRobot {

    /**
     * 基础评分
     * 表示它能带来收益
     * 即当前线没有对方棋子
     */
    private const val SCORE_BASIC = 1

    /**
     * 当一条线中，如果没有对方棋子的情况下，每存在一枚自己的棋子，那么积分将会增加1次
     */
    private const val SCORE_FAMILY = 6

    /**
     * 当一条线中，存在两个对方的棋子时，那么意味着危机，此时我们将会认为它的优先级会非常高
     */
    private const val SCORE_CRISIS = 10

    /**
     * 获取落子位置
     */
    fun getResult(board: GameBoard, self: GamePiece): Result {
        var maxSource = 0
        var lastX = -1
        var lastY = -1
        for (x in 0 until GameBoard.BOARD_SIZE) {
            for (y in 0 until GameBoard.BOARD_SIZE) {
                val score = getScore(board, self, x, y)
                if (score > maxSource) {
                    maxSource = score
                    lastX = x
                    lastY = y
                } else if (score == maxSource) {
                    // 当评分相等的时候，为了多一些变化，我们让它有50%的概率使用另一种方式
                    if (Random.nextFloat() > 0.5f) {
                        lastX = x
                        lastY = y
                    }
                }
            }
        }
        if (maxSource > 0 && lastX >= 0 && lastY >= 0) {
            return Result.Success(lastX, lastY)
        }
        return Result.Error
    }

    /**
     * 获取一个点的评分
     * 它会尝试累加横向，纵向，斜向的评分
     * 当没有收获的时候，会返回0
     */
    private fun getScore(board: GameBoard, self: GamePiece, x: Int, y: Int): Int {
        val current = board.get(x, y)
        // 都填上了，就别判断了
        if (current != GamePiece.Empty) {
            return 0
        }
        var score = 0
        // 同一行， Y一样，X变化, 我们通过当前的X来获取左右两边的棋子
        score += checkPiece(self, board.get(getFirst(x), y), board.get(getLast(x), y))
        // 同一列， X一样，Y变化, 我们通过当前的Y来获取上下两边的棋子
        score += checkPiece(self, board.get(x, getFirst(y)), board.get(x, getLast(y)))
        // 斜线，它有要求，与斜线有关的只有5个点
        when (x) {
            0 -> {
                when (y) {
                    0 -> {
                        // 左上角，那么剩下的两个就是固定的位置了
                        score += checkPiece(self, board.get(1, 1), board.get(2, 2))
                    }

                    2 -> {
                        // 左下角，剩下的两个就是中间和右上角
                        score += checkPiece(self, board.get(1, 1), board.get(2, 0))
                    }
                }
            }

            1 -> {
                // x为1的时候，只有y也为1，是中间那一个，才有斜线可以连接
                if (y == 1) {
                    // 左上角与右下角的点连线
                    score += checkPiece(self, board.get(0, 0), board.get(2, 2))
                    // 左下角与右上角的点连线
                    score += checkPiece(self, board.get(0, 2), board.get(2, 0))
                }
            }

            2 -> {
                when (y) {
                    0 -> {
                        // 右上角，那么剩下的两个就是固定的位置了
                        score += checkPiece(self, board.get(1, 1), board.get(0, 2))
                    }

                    2 -> {
                        // 右下角，剩下的两个就是中间和左上角
                        score += checkPiece(self, board.get(1, 1), board.get(0, 0))
                    }
                }
            }
        }
        return score
    }

    /**
     * 获取前一个值，也许是X也许是Y
     * 因为我们一行只有3个，那么当给定一个X之后，剩下的两个就很好确认了。
     * 如果当前的不是0，那么就取0，如果是0，就取1
     */
    private fun getFirst(a: Int): Int {
        if (a == 0) {
            return 1
        }
        return 0
    }

    /**
     * 获取后一个值，也许是X或者是Y
     * 因为我们一行只有3个，那么当给定一个X之后，剩下的两个就很好确认了。
     * 如果当前的不是2，那么就取2，如果是2，就取1
     */
    private fun getLast(a: Int): Int {
        if (a == 2) {
            return 1
        }
        return 2
    }

    /**
     * 检查棋子，因为我们落子的时候，必然是有空位的情况下，那么当前线，就只需要判断另外两个的状态就好了
     * 我们的评分，也主要是针对另外两个条件
     * 如果两个一样，并且都不是空的，还是对家的棋子，那么说明当前线出现了危机，直接返回
     */
    private fun checkPiece(self: GamePiece, pieceA: GamePiece, pieceB: GamePiece): Int {
        if (pieceA == pieceB && pieceA != GamePiece.Empty && pieceA != self) {
            // 如果两个一样，并且都不是空的，还是对家的棋子，那么说明当前线出现了危机，直接返回
            return SCORE_CRISIS
        }
        var score = 0
        when (pieceA) {
            GamePiece.Empty -> {
                // 是个空，那么加个分，有机会
                score += SCORE_BASIC
            }

            self -> {
                // 是自己的，那么加个分，有队友
                score += SCORE_FAMILY
            }

            else -> {
                // 都不是，那说明是对家，这条线没意义了
                return 0
            }
        }
        when (pieceB) {
            GamePiece.Empty -> {
                // 是个空，那么加个分，有机会
                score += SCORE_BASIC
            }

            self -> {
                // 是自己的，那么加个分，有队友
                score += SCORE_FAMILY
            }

            else -> {
                // 都不是，那说明是对家，这条线没意义了
                return 0
            }
        }
        return score
    }

    sealed class Result {
        data object Error : Result()
        data class Success(val x: Int, val y: Int) : Result()
    }

}