import java.lang.RuntimeException

class A2 {
    private var memory: Array<Int> = Array(MEMORY_SIZE, init = { 0 })

    fun run(code: String): A2 {
        // Flash the memory.
        code.split(",")
            .map(String::toInt)
            .forEachIndexed { index, i -> memory[index] = i }

        // Run the code!
        var pc = 0
        while (true) {
            if (pc >= MEMORY_SIZE || memory[pc] == 99) break
            when (memory[pc]) {
                1 -> add(memory.sliceArray(IntRange(pc, pc + 3)))
                2 -> mul(memory.sliceArray(IntRange(pc, pc + 3)))
                else -> throw RuntimeException("invalid opcode")
            }
            pc += 4
        }

        return this
    }

    private fun mul(op: Array<Int>) {
        store(op[3], load(op[1]) * load(op[2]))
    }

    private fun add(op: Array<Int>) {
        store(op[3], load(op[1]) + load(op[2]))
    }

    private fun store(position: Int, value: Int) {
        memory[position] = value
    }

    internal fun load(position: Int): Int {
        return memory[position]
    }

    internal fun viewAll(upTo: Int): String {
        return memory.sliceArray(IntRange(0, upTo)).joinToString(",")
    }

    companion object {
        const val MEMORY_SIZE = 4096
    }
}