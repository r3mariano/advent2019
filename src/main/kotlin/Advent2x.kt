import java.lang.RuntimeException

// A2, but with time travel.
class A2X {
    data class State(
        val memory: List<Int>,
        val pc: Int,
        val halted: Boolean = false
    ) {
        internal fun viewAll(): String {
            return memory.joinToString(",")
        }
    }

    private lateinit var state: State

    fun run(code: String): A2X {
        state = State(
            memory = storeAll(code),
            pc = 0
        )

        while (!state.halted) {
            state = reduce(state)
//            println(state)
        }
        return this
    }

    private fun reduce(state: State): State = State(
        memory = act(state.memory, state.pc),
        pc = state.pc + 4,
        halted = shouldHalt(state.memory, state.pc)
    )

    private fun shouldHalt(memory: List<Int>, pc: Int): Boolean = memory[pc] == 99

    private fun act(memory: List<Int>, pc: Int): List<Int> = when (memory[pc]) {
        1 -> add(memory, memory.subList(pc, pc + 4))
        2 -> mul(memory, memory.subList(pc, pc + 4))
        99 -> memory
        else -> throw RuntimeException("invalid opcode")
    }

    private fun add(memory: List<Int>, op: List<Int>): List<Int> = store(
        memory = memory,
        position = op[3],
        value = load(memory, op[1]) + load(memory, op[2])
    )

    private fun mul(memory: List<Int>, op: List<Int>): List<Int> = store(
        memory = memory,
        position = op[3],
        value = load(memory, op[1]) * load(memory, op[2])
    )

    private fun load(memory: List<Int>, position: Int): Int = memory[position]

    private fun store(memory: List<Int>, position: Int, value: Int): List<Int> {
        val newMemory = memory.toMutableList()
        newMemory[position] = value
        return newMemory.toList()
    }

    private fun storeAll(code: String): List<Int> = code.split(",").map(String::toInt)

    internal fun view(position: Int) = load(state.memory, position)

    internal fun view(): String = state.viewAll()
}

class A2Solver {
    fun solve(desiredZero: Int): Int {
        val puzzle = "1,0,0,3,1,1,2,3,1,3,4,3,1,5,0,3,2,9,1,19,1,19,5,23,1,9,23,27,2,27,6,31,1,5,31,35,2,9,35,39,2,6,39,43,2,43,13,47,2,13,47,51,1,10,51,55,1,9,55,59,1,6,59,63,2,63,9,67,1,67,6,71,1,71,13,75,1,6,75,79,1,9,79,83,2,9,83,87,1,87,6,91,1,91,13,95,2,6,95,99,1,10,99,103,2,103,9,107,1,6,107,111,1,10,111,115,2,6,115,119,1,5,119,123,1,123,13,127,1,127,5,131,1,6,131,135,2,135,13,139,1,139,2,143,1,143,10,0,99,2,0,14,0"
        for (noun in 1..99) {
            for (verb in 1..99) {
                val modifiedPuzzle = puzzle.split(",")
                    .map(String::toInt)
                    .mapIndexed { index, i -> when(index) {
                        1 -> noun
                        2 -> verb
                        else -> i
                    } }.joinToString(",")
                if (A2X().run(modifiedPuzzle).view(0) == desiredZero) return 100 * noun + verb
            }
        }
        throw RuntimeException("no solution")
    }
}
