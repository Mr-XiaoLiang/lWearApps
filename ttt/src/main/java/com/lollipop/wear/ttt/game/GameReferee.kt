package com.lollipop.wear.ttt.game

object GameReferee {

    fun checkWinner(board: GameBoard): Result {
        var hasNext = false
        var loop = 0
        while (true) {
            // 循环检查每一项，如果提前计算出了胜负，那么直接返回
            // 否则我们检查是否有空位，有的话，我们在检查结束之后，告诉外部可以继续。否则告诉他们已经平局
            when (getCheckResult(board, loop)) {
                Result.WinnerO -> {
                    return Result.WinnerO
                }

                Result.WinnerX -> {
                    return Result.WinnerX
                }

                Result.Continue -> {
                    hasNext = true
                }

                Result.Draw -> {
                    // 当前的线路填满了
                }
                // 为空那么结束循环，没有然后了
                else -> {
                    break
                }
            }
            loop++
        }

        return if (hasNext) {
            Result.Continue
        } else {
            Result.Draw
        }
    }

    private fun getCheckResult(
        board: GameBoard,
        loop: Int
    ): Result? {
        when (loop) {
            0 -> {
                return checkRow(board, 0)
            }

            1 -> {
                return checkRow(board, 1)
            }

            2 -> {
                return checkRow(board, 2)
            }

            3 -> {
                return checkColumn(board, 0)
            }

            4 -> {
                return checkColumn(board, 1)
            }

            5 -> {
                return checkColumn(board, 2)
            }

            6 -> {
                return checkDiagonalA(board)
            }

            7 -> {
                return checkDiagonalB(board)
            }
        }
        return null
    }


    private fun checkRow(board: GameBoard, y: Int): Result {
        return checkLine(board.get(0, y), board.get(1, y), board.get(2, y))
    }

    private fun checkColumn(board: GameBoard, x: Int): Result {
        return checkLine(board.get(x, 0), board.get(x, 1), board.get(x, 2))
    }

    private fun checkDiagonalA(board: GameBoard): Result {
        return checkLine(board.get(0, 0), board.get(1, 1), board.get(2, 2))
    }

    private fun checkDiagonalB(board: GameBoard): Result {
        return checkLine(board.get(2, 0), board.get(1, 1), board.get(0, 2))
    }

    /**
     * 井字棋，那么只要检查这三个棋子是否一样就行了
     *
     * 能赢的棋子只有2种，XXX与OOO
     * 那么我们只要判定这两个条件，达成即可获胜，否则即没有
     */
    private fun checkLine(piece1: GamePiece, piece2: GamePiece, piece3: GamePiece): Result {
        when (checkPiece(piece1, piece2)) {
            PieceResult.ContinueAll -> {
                // 如果这行2个空，那么说明这行必定空
                return Result.Continue
            }

            PieceResult.ContinueO -> {
                // 如果这行有一个O，那么看看剩下的是不是X, 如果是相反的，那么也废了，否则就还有戏
                return if (piece3 == GamePiece.X) {
                    Result.Draw
                } else {
                    Result.Continue
                }
            }

            PieceResult.ContinueX -> {
                // 如果这行有一个X，那么看看剩下的是不是O, 如果是相反的，那么也废了，否则就还有戏
                return if (piece3 == GamePiece.O) {
                    Result.Draw
                } else {
                    Result.Continue
                }
            }

            PieceResult.Draw -> {
                // 这两个都平了，那这条线就废了
                return Result.Draw
            }

            PieceResult.WinnerO -> {
                // 这里已经有2个O了，如果现在给个X，那么就废了，如果现在给个O，那么就赢了
                return when (piece3) {
                    GamePiece.Empty -> {
                        Result.Continue
                    }

                    GamePiece.X -> {
                        return Result.Draw
                    }

                    GamePiece.O -> {
                        return Result.WinnerO
                    }
                }
            }

            PieceResult.WinnerX -> {
                // 这里已经有2个X了，如果现在给个O，那么就废了，如果现在给个X，那么就赢了
                return when (piece3) {
                    GamePiece.Empty -> {
                        Result.Continue
                    }

                    GamePiece.O -> {
                        return Result.Draw
                    }

                    GamePiece.X -> {
                        return Result.WinnerX
                    }
                }
            }
        }
    }

    /**
     * 我们把判断规则再次细化到相邻的两个棋子的关系
     * 假设我们将棋子设定为A、B
     * 1. A为空时，那么必定无法连接，因此我们需要判断B的内容，确定是空一个还是全空，以此来帮助下一个棋子
     * 2. B为空时，那么也必定无法连接，但是此时A已经确定不为空了，因此他不可能出现全空结果
     * 3. 第三阶段时，A和B都不可能为空了，因此我们判断是否一致，如果一致，那么它可能连成一线，否则无法连线
     */
    private fun checkPiece(pieceA: GamePiece, pieceB: GamePiece): PieceResult {
        // 如果A为空，那么我们判断B
        if (pieceA == GamePiece.Empty) {
            return when (pieceB) {
                GamePiece.Empty -> {
                    PieceResult.ContinueAll
                }

                GamePiece.X -> {
                    PieceResult.ContinueX
                }

                GamePiece.O -> {
                    PieceResult.ContinueO
                }
            }
        }
        // 如果B为空，那么我们就直接认为A非O即X
        if (pieceB == GamePiece.Empty) {
            return if (pieceA == GamePiece.O) {
                PieceResult.ContinueO
            } else {
                PieceResult.ContinueX
            }
        }

        // 如果两个都不为空，接着判断是否一致
        if (pieceA == pieceB) {
            return if (pieceA == GamePiece.O) {
                PieceResult.WinnerO
            } else {
                PieceResult.WinnerX
            }
        }
        // 如果不一致，那么就是平局
        return PieceResult.Draw
    }

    /**
     * 棋子判定结果细分
     */
    private enum class PieceResult {
        /**
         * 全空
         */
        ContinueAll,

        /**
         * 一个空与一个O
         */
        ContinueO,

        /**
         * 一个空与一个X
         */
        ContinueX,

        /**
         * 两个O
         */
        WinnerO,

        /**
         * 两个X
         */
        WinnerX,

        /**
         * 一个O与一个X
         */
        Draw;
    }

    /**
     * 游戏结果的细分
     */
    enum class Result {
        /**
         * O赢了
         */
        WinnerO,

        /**
         * X赢了
         */
        WinnerX,

        /**
         * 平局
         */
        Draw,

        /**
         * 还未结束
         */
        Continue;
    }

}