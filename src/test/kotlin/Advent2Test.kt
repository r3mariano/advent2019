import io.kotlintest.matchers.shouldBe
import org.junit.Test

internal class Advent2Test {
    @Test
    fun `opcode 1 reads two numbers and stores them in a memory location`() {
        A2X()
            .run("1,1,2,3,99")
            .view(3) shouldBe 3
    }

    @Test
    fun `opcode 2 multiplies two numbers and stores them in a memory location`() {
        A2X()
            .run("2,0,0,5,99,0")
            .view(5) shouldBe 4
    }

    @Test
    fun `opcode 99 halts the program`() {
        // This shouldn't crash:
        A2X()
            .run("99")
    }

    @Test
    fun `works with multiple statements`() {
        A2X()
            .run("1,1,2,3,2,0,0,5,99")
            .view(5) shouldBe 1
    }

    @Test
    fun `the intcode program is written into the data block`() {
        A2X()
            .run("1,1,2,5,2,2,6,4,99")
            .view(4) shouldBe 30
    }

    @Test
    fun `code after opcode 99 is ignored`() {
        // If 99 didn't halt the program, the next op (add 1 + 1 and store in position 1)
        // would execute.
        A2X()
            .run("99,0,1,1,1,1,1,1")
            .view(1) shouldBe 0
    }

    @Test
    fun `executes sample problems`() {
        /*
        1,0,0,0,99 becomes 2,0,0,0,99 (1 + 1 = 2).
        2,3,0,3,99 becomes 2,3,0,6,99 (3 * 2 = 6).
        2,4,4,5,99,0 becomes 2,4,4,5,99,9801 (99 * 99 = 9801).
        1,1,1,4,99,5,6,0,99 becomes 30,1,1,4,2,5,6,0,99.
         */
//        A2().run("1,0,0,0,99").viewAll(upTo = 4) shouldBe "2,0,0,0,99"
//        A2().run("2,3,0,3,99").viewAll(upTo = 4) shouldBe "2,3,0,6,99"
//        A2().run("2,4,4,5,99,0").viewAll(upTo = 5) shouldBe "2,4,4,5,99,9801"
//        A2().run("1,1,1,4,99,5,6,0,99").viewAll(upTo = 8) shouldBe "30,1,1,4,2,5,6,0,99"

        A2X().run("1,0,0,0,99").view() shouldBe "2,0,0,0,99"
        A2X().run("2,3,0,3,99").view() shouldBe "2,3,0,6,99"
        A2X().run("2,4,4,5,99,0").view() shouldBe "2,4,4,5,99,9801"
        A2X().run("1,1,1,4,99,5,6,0,99").view() shouldBe "30,1,1,4,2,5,6,0,99"
    }

    @Test
    fun `part 1`() {
        println(
            A2X().run("1,0,0,3,1,1,2,3,1,3,4,3,1,5,0,3,2,9,1,19,1,19,5,23,1,9,23,27,2,27,6,31,1,5,31,35,2,9,35,39,2,6,39,43,2,43,13,47,2,13,47,51,1,10,51,55,1,9,55,59,1,6,59,63,2,63,9,67,1,67,6,71,1,71,13,75,1,6,75,79,1,9,79,83,2,9,83,87,1,87,6,91,1,91,13,95,2,6,95,99,1,10,99,103,2,103,9,107,1,6,107,111,1,10,111,115,2,6,115,119,1,5,119,123,1,123,13,127,1,127,5,131,1,6,131,135,2,135,13,139,1,139,2,143,1,143,10,0,99,2,0,14,0").view()
        )
        println(
            A2X().run("1,12,2,3,1,1,2,3,1,3,4,3,1,5,0,3,2,9,1,19,1,19,5,23,1,9,23,27,2,27,6,31,1,5,31,35,2,9,35,39,2,6,39,43,2,43,13,47,2,13,47,51,1,10,51,55,1,9,55,59,1,6,59,63,2,63,9,67,1,67,6,71,1,71,13,75,1,6,75,79,1,9,79,83,2,9,83,87,1,87,6,91,1,91,13,95,2,6,95,99,1,10,99,103,2,103,9,107,1,6,107,111,1,10,111,115,2,6,115,119,1,5,119,123,1,123,13,127,1,127,5,131,1,6,131,135,2,135,13,139,1,139,2,143,1,143,10,0,99,2,0,14,0").view()
        )
    }

    @Test
    fun `part 2`() {
        println(A2Solver().solve(19690720))
    }
}