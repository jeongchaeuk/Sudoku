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

    fun makeSample() {
        setValue(2, 1)
        setValue(5, 6)
        setValue(7, 4)
        setValue(9, 5)
        setValue(14, 9)
        setValue(15, 3)
        setValue(19, 6)
        setValue(20, 2)
        setValue(25, 1)
        setValue(26, 9)
        setValue(31, 7)
        setValue(32, 8)
        setValue(36, 8)
        setValue(39, 9)
        setValue(41, 1)
        setValue(44, 5)
        setValue(48, 6)
        setValue(49, 3)
        setValue(54, 6)
        setValue(55, 4)
        setValue(60, 2)
        setValue(61, 5)
        setValue(65, 9)
        setValue(66, 5)
        setValue(71, 6)
        setValue(73, 5)
        setValue(75, 8)
        setValue(78, 9)
    }

    override fun toString(): String {
        var result = ""
        var count = 0
        var groupCol = 0
        var groupRow = 0
        for (block in board) {
            result += block.toString()
            if (++groupCol == 3) {
                result += " | "
                groupCol = 0
            }
            if (++groupRow == 27) {
                result += List(9 * 11 + 2 * 3, { "-" }).joinToString("")
                groupRow = 0
            }

            if (++count == SIZE) {
                result += "\n"
                count = 0
            }
        }

        return result
    }

    fun getBlock(row: Int, col: Int): Block {
        return board.get(index = row * SIZE + col)
    }

    /**
     * Get same row blocks
     */
    private fun getSameRowOf(target: Block): List<Block> {
        val row = target.index / SIZE
        val start = row * SIZE
        return board.subList(start, start + SIZE)
    }

    /**
     * Get same column blocks
     */
    private fun getSameColumnOf(block: Block): List<Block> {
/*
        val col = (block.index % SIZE)
        val list = mutableListOf<Block>()
        for (i in 0 until SIZE)
            list.add(board[col + i * SIZE])
        return list
*/
        return List<Block>(SIZE) { board[block.index % SIZE + it * SIZE] }
    }

    /**
     * Get 3 x 3 blocks
     */
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

    /**
     * Set value of index-th block.
     * Remove possible value from same row, same column and same group blocks of index-th block.
     */
    fun setValue(
        index: Int,
        value: Int,
        checkGroup: Boolean = true,
        checkRow: Boolean = true,
        checkCol: Boolean = true
    ) {
        val target = board[index]
        target.setValue(value)

        if (checkGroup)
            updateBlocks(target, getSameGroup(target))

        if (checkRow)
            updateBlocks(target, getSameRowOf(target))

        if (checkCol)
            updateBlocks(target, getSameColumnOf(target))
    }

    private fun updateBlocks(target: Block, blocks: List<Block>) {
        for (block in blocks) {
            if (block != target &&
                block.removePossibleValue(target.getValue()) &&
                block.getPossibleValuesCount() == 1
            )
                setValue(block.index, block.getValue())
        }
    }

    private fun checkColumn(_block: Block) {
        val blocks = getSameColumnOf(_block)

        for (i in 0 until SIZE) {
            if (blocks[i].getPossibleValuesCount() == 1)
                continue

            val target = blocks[i]
            val sameBlocks = blocks.filter { target.hasSamePossibleValues(it) }
            if (sameBlocks.size < 2 || sameBlocks.size != target.getPossibleValuesCount())
                continue

            for (diffBlock in blocks.subtract(sameBlocks)) {
                if (diffBlock.getPossibleValuesCount() == 1)
                    continue
                diffBlock.removePossibleValues(target)
                if (diffBlock.getPossibleValuesCount() == 1) {
                    setValue(diffBlock.index, diffBlock.getValue())
                }
            }
        }
    }

    fun checkColumns() {
        for (i in 0 until SIZE) {
            checkColumn(board[i])
        }
    }

    private fun checkRow(_block: Block) {
        val blocks = getSameRowOf(_block)

        for (i in 0 until SIZE) {
            val target = blocks[i]
            val sameBlocks = blocks.filter { target.hasSamePossibleValues(it) }
            if (sameBlocks.size < 2 || sameBlocks.size != target.getPossibleValuesCount())
                continue

            for (diffBlock in blocks.subtract(sameBlocks)) {
                diffBlock.removePossibleValues(target)
                if (diffBlock.getPossibleValuesCount() == 1) {
                    setValue(diffBlock.index, diffBlock.getValue())
                }
            }
        }
    }

    fun checkRows() {
        for (i in 0 until SIZE) {
            checkRow(board[i])
        }
    }

    /**
     * Remove redundant possible values from boards
     */
    fun checkGroups(): Boolean {
        var tryAgain = false
        val counts = MutableList<Int>(SIZE) { 0 }

        for (groupIndex in 0 until SIZE) {
            val group = getSameGroup(board[(groupIndex * 3) + (groupIndex / 3) * SIZE * 2])

            for (block in group) {
                for (value in 1..SIZE) {
                    if (block.containsPossibleValue(value))
                        ++counts[value - 1]
                }
            }

            for (value in 1..SIZE) {
                if (counts[value - 1] != 1)
                    continue
                for (block in group) {
                    if (1 < block.getPossibleValuesCount() && block.containsPossibleValue(value)) {
                        setValue(block.index, value, false)
                        tryAgain = true
                    }
                }
            }
            counts.fill(0)
        }

        // Call checkBoard() again until no block was changed
        return tryAgain
    }

    fun getAllPossibleValuesCount(): Int {
        var count = 0
        for (block in board) {
            count += block.getPossibleValuesCount()
        }

        return count
    }
}

/**
 * A block.
 */
class Block(val index: Int) {

    private val possibleValues = (1..9).toMutableList()

    fun setValue(value: Int) {
        if (getValue() == value)
            return

        possibleValues.clear()
        possibleValues.add(value)
    }

    fun getValue(): Int {
        return when (possibleValues.size) {
            1 -> possibleValues[0]
            else -> 0 // error!
        }
    }

    fun removePossibleValue(value: Int) = possibleValues.remove(value)

    fun removePossibleValues(other: Block) = possibleValues.removeAll(other.possibleValues)

    fun containsPossibleValue(value: Int) = possibleValues.contains(value)

    fun getPossibleValuesCount() = possibleValues.count()

    fun hasSamePossibleValues(other: Block) =
        possibleValues.joinToString("") == other.possibleValues.joinToString("")

    override fun toString(): String {
        return "${possibleValues.joinToString("", "[", "]").padEnd(11)}"
    }

    override fun hashCode() = index
}

