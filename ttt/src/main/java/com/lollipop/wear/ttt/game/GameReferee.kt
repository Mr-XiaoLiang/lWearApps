package com.lollipop.wear.ttt.game

object GameReferee {

    fun checkWinner(board: GameBoard): Result {
        var hasNext = false
        var loop = 0
        while (true) {
            // 循环检查每一项，如果提前计算出了胜负，那么直接返回
            // 否则我们检查是否有空位，有的话，我们在检查结束之后，告诉外部可以继续。否则告诉他们已经平局
            when (getCheckResult(board, loop)) {
                is Result.WinnerO -> {
                    return Result.WinnerO
                }

                is Result.WinnerX -> {
                    return Result.WinnerX
                }

                is Result.Continue -> {
                    hasNext = true
                }

                is Result.Draw -> {
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

    sealed class PieceResult {

        abstract val piece: GamePiece

        data object ContinueAll : PieceResult() {
            override val piece: GamePiece = GamePiece.Empty
        }

        data object ContinueO : PieceResult() {
            override val piece = GamePiece.O
        }

        data object ContinueX : PieceResult() {
            override val piece = GamePiece.X
        }

        data object WinnerO : PieceResult() {
            override val piece: GamePiece = GamePiece.O
        }

        data object WinnerX : PieceResult() {
            override val piece: GamePiece = GamePiece.X
        }

        data object Draw : PieceResult() {
            override val piece: GamePiece = GamePiece.Empty
        }
    }

    sealed class Result {
        data object WinnerO : Result()
        data object WinnerX : Result()
        data object Draw : Result()
        data object Continue : Result()
    }

}