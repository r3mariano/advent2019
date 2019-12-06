import A5.Op.*
import A5.InstructionMode.IMMEDIATE
import A5.InstructionMode.POSITION
import java.lang.RuntimeException

typealias Pc = Int
typealias Memory = List<Int>
typealias OpPayload = List<Int>
typealias Input = Int
typealias Output = Int

class A5 {

    // region data classes
    enum class Op {
        ADD,
        MUL,
        STORE,
        LOAD,
        JIT, // jump if true
        JIF, // jump if false
        LT, // less than
        EQ, // equal
        HALT
    }

    enum class InstructionMode {
        IMMEDIATE,
        POSITION
    }

    data class State(
        val memory: Memory,
        val pc: Pc,
        val halted: Boolean = false
    ) {
        internal fun viewAll(): String {
            return memory.joinToString(",")
        }
    }

    data class Command(
        val op: Op,
        val modes: List<InstructionMode>,
        val length: Int
    )
    // endregion

    private lateinit var state: State
    private var input: Input = 0
    var output: Output = 0
        private set(value) {
            field = value
        }

    fun withInput(input: Input): A5 {
        this.input = input
        return this
    }

    fun run(code: String): A5 {
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

    internal fun view(position: Pc) = load(state.memory, position)

    internal fun view(): String = state.viewAll()

    // region parsing from string
    private fun storeAll(code: String): Memory = code.split(",").map(String::toInt)

    private fun parseOp(i: String): Op = when (i) {
        "01" -> ADD
        "02" -> MUL
        "03" -> STORE
        "04" -> LOAD
        "05" -> JIT
        "06" -> JIF
        "07" -> LT
        "08" -> EQ
        "99" -> HALT
        else -> throw RuntimeException("Invalid opcode: $i")
    }

    private fun mode(c: Char): InstructionMode = when (c) {
        '0' -> POSITION
        '1' -> IMMEDIATE
        else -> throw RuntimeException("Invalid instruction mode: $c")
    }
    // endregion

    private fun reduce(state: State): State {
        // What are the commands and params?
        val command = parseCommand(state.memory, state.pc)
        // Optionally load from memory based on the command.
        val payload = normaliseOpPayload(command, state.memory, state.pc)
        println("${state.pc} : ${view(state.pc)} $command, $payload")
        return execute(
            command = command,
            payload = payload,
            previousState = state
        )
    }

    private fun parseCommand(memory: Memory, pc: Pc): Command {
        val command = load(memory, pc)
        val commandArray = command.toString().padStart(5, '0').toCharArray()
        val instruction = parseOp("${commandArray[3]}${commandArray[4]}")
        val length = length(instruction)
        val modes = IntRange(0, 2).reversed().map { mode(commandArray[it]) }.subList(0, length - 1)
        return Command(
            op = instruction,
            modes = modes,
            length = length
        )
    }

    private fun normaliseOpPayload(command: Command, memory: Memory, pc: Pc): OpPayload {
        if (command.length < 2) return emptyList()
        return when (command.op) {
            // JIT and JIF have two params and no "output" params.
            JIT, JIF -> IntRange(1, command.length - 1).map { normaliseOne(memory, command, pc, it) }.toMutableList()
            else -> {
                // normalise all but the last one
                val result = IntRange(1, command.length - 2).map { normaliseOne(memory, command, pc, it) }.toMutableList()
                result.add(load(memory, pc + command.length - 1))
                result.toList()
            }
        }
    }

    private fun normaliseOne(memory: Memory, command: Command, pc: Pc, index: Int) = when (command.modes[index - 1]) {
        IMMEDIATE -> load(memory, pc + index)
        POSITION -> load(memory, load(memory, pc + index))
    }

    private fun length(i: Op): Int = when (i) {
        ADD, MUL, LT, EQ -> 4
        JIT, JIF -> 3
        STORE, LOAD -> 2
        HALT -> 1
    }

    private fun execute(command: Command, payload: OpPayload, previousState: State): State = State(
        pc = when (command.op) {
            JIT -> if (payload[0] != 0) payload[1] else previousState.pc + length(command.op)
            JIF -> if (payload[0] == 0) payload[1] else previousState.pc + length(command.op)
            else -> previousState.pc + length(command.op)
        },
        halted = command.op == HALT,
        memory = when (command.op) {
            ADD -> add(previousState.memory, payload)
            MUL -> mul(previousState.memory, payload)
            LT -> lt(previousState.memory, payload)
            EQ -> eq(previousState.memory, payload)
            STORE -> store(previousState.memory, payload[0], input)
            LOAD -> {
                // A side effect! :o
                output = load(previousState.memory, payload[0]); previousState.memory
            }
            JIT, JIF, HALT -> previousState.memory
            else -> throw RuntimeException("No exec block for instruction")
        }
    )

    // region operations
    private fun add(memory: Memory, payload: OpPayload): Memory = store(
        memory = memory,
        position = payload[2],
        value = payload[0] + payload[1]
    )

    private fun mul(memory: Memory, payload: OpPayload): Memory = store(
        memory = memory,
        position = payload[2],
        value = payload[0] * payload[1]
    )

    private fun lt(memory: Memory, payload: OpPayload): Memory = store(
        memory = memory,
        position = payload[2],
        value = if (payload[0] < payload[1]) 1 else 0
    )

    private fun eq(memory: Memory, payload: OpPayload): Memory = store(
        memory = memory,
        position = payload[2],
        value = if (payload[0] == payload[1]) 1 else 0
    )

    private fun load(memory: Memory, position: Pc): Pc = memory[position]

    private fun store(memory: Memory, position: Pc, value: Pc): Memory {
        val newMemory = memory.toMutableList()
        newMemory[position] = value
        return newMemory.toList()
    }
    // endregion
}
