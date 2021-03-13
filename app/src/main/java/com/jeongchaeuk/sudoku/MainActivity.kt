package com.jeongchaeuk.sudoku

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.jeongchaeuk.sudoku.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val game = Sudoku()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val startId = R.id.no_00
        var id = startId
        while (id <= R.id.no_88) {
            findViewById<TextView>(id).text = (game.board[id - startId].index / SIZE).toString()
            id++
        }
    }
}

const val SIZE = 9
const val GROUP_SIZE = 3

class Sudoku(var board: List<Block> = List(SIZE * SIZE) { Block(it) }) {

    fun getBlock(row: Int, col: Int): Block {
        return board.get(index = row * SIZE + col)
    }

    private fun getSameRow(target: Block): List<Block> {
        val row = target.index / SIZE
        val start = row * SIZE
        return board.subList(start, start + SIZE)
    }

    private fun getSameCol(target: Block): List<Block> {
        val col = (target.index % SIZE)
        val list = mutableListOf<Block>()

        for (i in 0 until SIZE) {
            list.add(board[col + i * SIZE])
        }

        return list
    }

    private fun getSameGroup(target: Block): List<Block> {
/*
        val row = target.index / SIZE
        val col = target.index % SIZE

        val groupRow = row / GROUP_SIZE
        val groupCol = col / GROUP_SIZE

        val startRow = groupRow * GROUP_SIZE
        val startCol = groupCol * GROUP_SIZE

        val startIndex = startRow * SIZE + startCol
*/
        val startIndex =
            (target.index / SIZE / GROUP_SIZE * GROUP_SIZE * SIZE) + (target.index % SIZE / GROUP_SIZE * GROUP_SIZE)
        val list = mutableListOf<Block>()

        for (row in 0 until GROUP_SIZE) {
            for (col in 0 until GROUP_SIZE) {
                list.add(board[startIndex + row * SIZE + col])
            }
        }

        return list
    }

    fun setValue(index: Int, value: Int) {
        val target = board[index]
        target.setValue(value)

        var blocks = getSameGroup(target)
        for (block in blocks)
            block.removePossibleValue(value)

        blocks = getSameRow(target)
        for (block in blocks)
            block.removePossibleValue(value)

        blocks = getSameCol(target)
        for (block in blocks)
            block.removePossibleValue(value)
    }
}

class Block(val index: Int) {

    private val possibleValues = (1..9).toMutableList()

    fun setValue(value: Int) {
        possibleValues.clear()
        possibleValues.add(value)
    }

    fun getValue(): Int {
        return when (possibleValues.size) {
            1 -> possibleValues[0]
            else -> 0
        }
    }

    fun removePossibleValue(value: Int) {
        possibleValues.remove(value)
    }

    override fun toString(): String {
        return "[$index]$possibleValues"
    }

}

