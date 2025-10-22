package com.example.scientificcalculator

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.lang.Math.cos
import java.lang.Math.log
import java.lang.Math.log10
import java.lang.Math.pow
import java.lang.Math.sin
import java.lang.Math.tan
import kotlin.math.sqrt

class MainActivity : AppCompatActivity() {

    private lateinit var tvDisplay: TextView
    private var inputString: String = ""
    private var currentResult: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvDisplay = findViewById(R.id.tvDisplay)

        setupButtonListeners()
    }

    private fun setupButtonListeners() {
        val buttons = getAllButtons()
        buttons.forEach { button ->
            button.setOnClickListener {
                val buttonText = (it as Button).text.toString()
                processButtonInput(buttonText)
            }
        }
    }

    private fun getAllButtons(): List<Button> {
        val buttonList = mutableListOf<Button>()
        val gridLayout = findViewById<android.widget.GridLayout>(R.id.gridLayout)
        for (i in 0 until gridLayout.childCount) {
            val child = gridLayout.getChildAt(i)
            if (child is Button) {
                buttonList.add(child)
            }
        }
        return buttonList
    }

    private fun processButtonInput(buttonText: String) {
        when (buttonText) {
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "." -> {
                inputString += buttonText
            }
            "+", "-", "x", "÷" -> {
                inputString += " $buttonText "
            }
            "AC" -> {
                inputString = ""
                currentResult = 0.0
            }
            "Back" -> {
                if (inputString.isNotEmpty()) {
                    inputString = inputString.dropLast(1)
                }
            }
            "=" -> {
                try {
                    val result = evaluateExpression(inputString)
                    currentResult = result
                    inputString = result.toString()
                } catch (e: Exception) {
                    inputString = "Error"
                }
            }
            "sin", "cos", "tan", "sin⁻¹", "cos⁻¹", "tan⁻¹", "log", "ln" -> {
                inputString += "$buttonText("
            }
            "√x" -> {
                inputString += "sqrt("
            }
            "³√x" -> {
                inputString += "cbrt("
            }
            "π" -> {
                inputString += "π"
            }
            "e" -> {
                inputString += "e"
            }
            "x²" -> {
                inputString += "^2"
            }
            "x³" -> {
                inputString += "^3"
            }
            "xʸ" -> {
                inputString += "^"
            }
            "eˣ" -> {
                inputString += "e^"
            }
            "10ˣ" -> {
                inputString += "10^"
            }
            "%" -> {
                inputString += "/100"
            }
            "1/x" -> {
                inputString = "1/($inputString)"
            }
            "n!" -> {
                inputString += "!"
            }
            "Ans" -> {
                inputString += currentResult.toString()
            }
            "(" -> {
                inputString += "("
            }
            ")" -> {
                inputString += ")"
            }
        }
        tvDisplay.text = if (inputString.isEmpty()) "0" else inputString
    }

    private fun evaluateExpression(expression: String): Double {
        val expr = expression.replace("x", "*").replace("÷", "/")
        return eval(expr)
    }

    private fun eval(str: String): Double {
        return object : Any() {
            var pos = -1
            var ch: Char = ' '

            fun nextChar() {
                ch = if (++pos < str.length) str[pos] else (-1).toChar()
            }

            fun eat(charToEat: Char): Boolean {
                while (ch == ' ') nextChar()
                if (ch == charToEat) {
                    nextChar()
                    return true
                }
                return false
            }

            fun parse(): Double {
                nextChar()
                val x = parseExpression()
                if (pos < str.length) throw RuntimeException("Unexpected: $ch")
                return x
            }

            fun parseExpression(): Double {
                var x = parseTerm()
                while (true) {
                    if (eat('+')) x += parseTerm()
                    else if (eat('-')) x -= parseTerm()
                    else return x
                }
            }

            fun parseTerm(): Double {
                var x = parseFactor()
                while (true) {
                    if (eat('*')) x *= parseFactor()
                    else if (eat('/')) x /= parseFactor()
                    else return x
                }
            }

            fun parseFactor(): Double {
                if (eat('+')) return parseFactor()
                if (eat('-')) return -parseFactor()

                var x: Double
                val startPos = this.pos

                when {
                    eat('(') -> {
                        x = parseExpression()
                        eat(')')
                    }
                    ch in '0'..'9' || ch == '.' -> {
                        while (ch in '0'..'9' || ch == '.') nextChar()
                        x = str.substring(startPos, this.pos).toDouble()
                    }
                    ch.isLetter() || ch in "π⁻¹" -> {
                        while (ch.isLetter() || ch in "π⁻¹") nextChar()
                        val func = str.substring(startPos, this.pos)
                        if (eat('(')) {
                            x = parseExpression()
                            eat(')')
                            x = when (func) {
                                "sin" -> sin(Math.toRadians(x))
                                "cos" -> cos(Math.toRadians(x))
                                "tan" -> tan(Math.toRadians(x))
                                "sin⁻¹" -> Math.toDegrees(Math.asin(x))
                                "cos⁻¹" -> Math.toDegrees(Math.acos(x))
                                "tan⁻¹" -> Math.toDegrees(Math.atan(x))
                                "sqrt" -> sqrt(x)
                                "cbrt" -> Math.cbrt(x)
                                "log" -> log10(x)
                                "ln" -> log(x)
                                else -> throw RuntimeException("Unknown function: $func")
                            }
                        } else {
                            x = when (func) {
                                "e" -> Math.E
                                "π" -> Math.PI
                                else -> throw RuntimeException("Unknown constant: $func")
                            }
                        }
                    }
                    else -> throw RuntimeException("Unexpected character: $ch")
                }

                if (eat('^')) x = pow(x, parseFactor())

                while (eat('!')) {
                    x = factorial(x)
                }

                return x
            }

            fun factorial(n: Double): Double {
                if (n < 0 || n != n.toInt().toDouble()) throw RuntimeException("Factorial is only for non-negative integers")
                val num = n.toInt()
                var result = 1.0
                for (i in 1..num) {
                    result *= i.toDouble()
                }
                return result
            }
        }.parse()
    }
}
